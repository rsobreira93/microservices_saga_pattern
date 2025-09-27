package br.com.microservices.orchestrated.paymentservice.core.services;

import br.com.microservices.orchestrated.paymentservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.paymentservice.core.dtos.Event;
import br.com.microservices.orchestrated.paymentservice.core.dtos.History;
import br.com.microservices.orchestrated.paymentservice.core.dtos.OrderProducts;
import br.com.microservices.orchestrated.paymentservice.core.entities.Payment;
import br.com.microservices.orchestrated.paymentservice.core.enums.EPaymentStatus;
import br.com.microservices.orchestrated.paymentservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.core.repositories.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.paymentservice.core.enums.ESagaStatus.*;

@Service
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
    private static final Double REDUCE_SUM_VALUE = 0.0;
    private static final Double MIN_AMOUNT_VALUE = 0.1;

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private final PaymentRepository paymentRepository;

    public PaymentService(JsonUtil jsonUtil, KafkaProducer kafkaProducer, PaymentRepository paymentRepository) {
        this.jsonUtil = jsonUtil;
        this.kafkaProducer = kafkaProducer;
        this.paymentRepository = paymentRepository;
    }

    public void realizePayment(Event event) {

        try {
            checkCurrentValidation(event);
            createPendingPayment(event);
            Payment payment = findByOrderAndTransactionId(event);
            validateAmount(payment.getTotalAmount());
            changePaymentToSuccess(payment);
            handleSuccess(event);
        } catch (Exception e) {
            LOG.error("Error trying to make payment: ", e);
            handleFailCurrentNotExecuted(event, e.getMessage());
        }

        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to realize payment: ".concat(message));
    }

    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Payment realized successfully.");
    }

    private void addHistory(Event event, String message) {
        History history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addHistory(history);
    }

    private void changePaymentToSuccess(Payment payment) {
        payment.setStatus(EPaymentStatus.SUCCESS);

        save(payment);
    }

    private void createPendingPayment(Event event) {
        Payment payment = Payment.builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .totalAmount(calculateAmount(event))
                .totalItems(calculateTotalItems(event))
                .build();

        save(payment);

        setEventAmountItems(event, payment);
    }

    private int calculateTotalItems(Event event) {
        return event.getPayload()
                .getProducts()
                .stream()
                .map(OrderProducts::getQuantity)
                .reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
    }

    private double calculateAmount(Event event) {
        return event.getPayload()
                .getProducts()
                .stream()
                .map(product -> product.getQuantity() * product.getProduct().getUnitValue())
                .reduce(REDUCE_SUM_VALUE, Double::sum);
    }

    private void save(Payment payment) {
        paymentRepository.save(payment);
    }

    private void checkCurrentValidation(Event event) {
        if (paymentRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private void validateAmount(double amount) {
        if(amount < MIN_AMOUNT_VALUE) {
            throw  new ValidationException("The minimum amount available is ".concat(MIN_AMOUNT_VALUE.toString()));
        }
    }

    private void setEventAmountItems(Event event, Payment payment) {
        event.getPayload().setTotalAmount(payment.getTotalAmount());
        event.getPayload().setTotalItems(payment.getTotalItems());
    }

    private Payment findByOrderAndTransactionId(Event event) {
        return paymentRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .orElseThrow(() -> new ValidationException("Payment not found by orderId and transactionId."));
    }

    public void realizeRefound(Event event) {
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);

        try {
            changePaymentStatsToRefound(event);
            addHistory(event, "Rollback executed for payment.");
        } catch (Exception e) {
            addHistory(event, "Rollback not executed for payment: ".concat(e.getMessage()));
        }

        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void changePaymentStatsToRefound(Event event) {
        Payment payment = findByOrderAndTransactionId(event);

        payment.setStatus(EPaymentStatus.REFOUND);

        setEventAmountItems(event, payment);

        save(payment);
    }

}
