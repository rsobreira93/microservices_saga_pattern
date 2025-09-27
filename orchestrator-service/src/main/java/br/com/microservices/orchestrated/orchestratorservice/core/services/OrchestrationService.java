package br.com.microservices.orchestrated.orchestratorservice.core.services;

import br.com.microservices.orchestrated.orchestratorservice.core.dtos.Event;
import br.com.microservices.orchestrated.orchestratorservice.core.dtos.History;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.EnumTopics;
import br.com.microservices.orchestrated.orchestratorservice.core.producer.SagaOrchestratorProducer;
import br.com.microservices.orchestrated.orchestratorservice.core.saga.SagaExecutionController;
import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource.ORCHESTRATOR;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus.FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus.SUCCESS;

@Service
public class OrchestrationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrchestrationService.class);

    private final SagaOrchestratorProducer kafkaProducer;
    private final JsonUtil jsonUtil;
    private final SagaExecutionController sagaExecutionController;

    public OrchestrationService(SagaOrchestratorProducer kafkaProducer, JsonUtil jsonUtil, SagaExecutionController sagaExecutionController) {
        this.kafkaProducer = kafkaProducer;
        this.jsonUtil = jsonUtil;
        this.sagaExecutionController = sagaExecutionController;
    }

    public void startSaga(Event event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);

        EnumTopics topic = getTopic(event);
        LOG.info("SAGA STARTED!");

        addHistory(event, "Saga started!");
        sendToProducerWithTopic(event, topic);
    }

    public void finishSagaSuccess(Event event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);

        LOG.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}!", event.getId());
        addHistory(event, "Saga finished successfully!");

        notifyFinishedSaga(event);
    }

    public void finishSagaFail(Event event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(FAIL);

        LOG.info("SAGA FINISHED WITH ERRORS FOR EVENT {}!", event.getId());
        addHistory(event, "Saga finished with errors!");

        notifyFinishedSaga(event);
    }

    public void continueSaga(Event event) {
        EnumTopics topic = getTopic(event);

        LOG.info("SAGA CONTINUING FOR EVENT {}", event.getId());

        sendToProducerWithTopic(event, topic);
    }

    private EnumTopics getTopic(Event event) {
        return sagaExecutionController.getNextTopic(event);
    }

    private void addHistory(Event event, String message) {
        History history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addHistory(history);
    }

    private void sendToProducerWithTopic(Event event, EnumTopics topic) {
        kafkaProducer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    private void notifyFinishedSaga(Event event) {
        kafkaProducer.sendEvent(jsonUtil.toJson(event), EnumTopics.NOTIFY_ENDING.getTopic());
    }
}
