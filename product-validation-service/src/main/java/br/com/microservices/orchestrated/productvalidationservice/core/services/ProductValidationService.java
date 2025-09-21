package br.com.microservices.orchestrated.productvalidationservice.core.services;

import br.com.microservices.orchestrated.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.productvalidationservice.core.dtos.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.dtos.History;
import br.com.microservices.orchestrated.productvalidationservice.core.dtos.OrderProducts;
import br.com.microservices.orchestrated.productvalidationservice.core.entities.Validation;
import br.com.microservices.orchestrated.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.core.repositories.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.repositories.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductValidationService.class);

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;

    public ProductValidationService(JsonUtil jsonUtil, KafkaProducer kafkaProducer, ProductRepository productRepository, ValidationRepository validationRepository) {
        this.jsonUtil = jsonUtil;
        this.kafkaProducer = kafkaProducer;
        this.productRepository = productRepository;
        this.validationRepository = validationRepository;
    }

    public void validateExistsProducts(Event event){

        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception e) {
            LOG.error("Error trying to validate products: ", e);
            handleFailCurrentNotExecuted(event, e.getMessage());
        }

        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    public void rollbackEvent(Event event) {
        changeValidationToFail(event);
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Rollback executed on product validation.");

        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void changeValidationToFail(Event event) {
        validationRepository.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .ifPresentOrElse(validation -> {
                    validation.setSuccess(false);
                    validationRepository.save(validation);
                },
                () -> createValidation(event, false));
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to validate products: ".concat(message));
    }

    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Products are validated successful.");
    }

    private void addHistory(Event event, String message) {
        History history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addToHistory(history);
    }

    private void createValidation(Event event, boolean success) {
        Validation validation = Validation.builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getPayload().getTransactionId())
                .success(success)
                .build();

        validationRepository.save(validation);
    }

    private void checkCurrentValidation(Event event) {
        validateProductsInformed(event);
        if(validationRepository.existsByOrderIdAndTransactionId(
                event.getOrderId(), event.getTransactionId()
        )){
            throw new ValidationException("There's another transactionId for this validation.");
        }

        event.getPayload().getProducts().forEach(product -> {
            validateProductInformed(product);
            validatedExistsProduct(product.getProduct().getCode());
        });
    }

    private static void validateProductsInformed(Event event) {
        if(isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())) {
            throw  new ValidationException("Product list is empty.");
        }

        if(isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())) {
            throw  new ValidationException("OrderId and TransactionId  must be informed!.");
        }
    }

    public void validateProductInformed(OrderProducts products) {
        if(isEmpty(products.getProduct()) || isEmpty(products.getProduct().getCode())) {
            throw  new ValidationException("Product must be informed.");
        }
    }

    public void validatedExistsProduct(String code) {
        if(!productRepository.existsByCode(code)) {
            throw new ValidationException("Product does not exists.");
        }
    }
}
