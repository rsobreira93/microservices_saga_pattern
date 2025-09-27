package br.com.microservices.orchestrated.orchestratorservice.core.consumer;


import br.com.microservices.orchestrated.orchestratorservice.core.dtos.Event;
import br.com.microservices.orchestrated.orchestratorservice.core.services.OrchestrationService;
import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SagaOrchestratorConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(SagaOrchestratorConsumer.class);

    private final JsonUtil jsonUtil;
    private final OrchestrationService orchestrationService;

    public SagaOrchestratorConsumer(JsonUtil jsonUtil, OrchestrationService orchestrationService) {
        this.jsonUtil = jsonUtil;
        this.orchestrationService = orchestrationService;
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.start-saga}"
    )
    public void consumeStartSagaEvent(String payload) {
        LOG.info("Receiving event from event {} start-saga topic", payload);

        Event event = jsonUtil.toEvent(payload);

        orchestrationService.startSaga(event);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.orchestrator}"
    )
    public void consumeOrchestratorEvent(String payload) {
        LOG.info("Receiving event from event {} orchestrator topic", payload);

        Event event = jsonUtil.toEvent(payload);

        orchestrationService.continueSaga(event);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-success}"
    )
    public void consumeFinishSuccessEvent(String payload) {
        LOG.info("Receiving event from event {} finish-success topic", payload);

        Event event = jsonUtil.toEvent(payload);

       orchestrationService.finishSagaSuccess(event);

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-fail}"
    )
    public void consumeFinishFailEvent(String payload) {
        LOG.info("Receiving event from event {} finish-fail topic", payload);

        Event event = jsonUtil.toEvent(payload);

        orchestrationService.finishSagaFail(event);

    }
}
