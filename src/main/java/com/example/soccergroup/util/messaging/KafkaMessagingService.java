package com.example.soccergroup.util.messaging;

import com.example.soccergroup.repository.SoccerGroupRepository;
import com.example.soccergroup.util.messaging.dto.PointEarnedMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

@Service
@Slf4j
public class KafkaMessagingService implements  MessageService
{

    private final Flux<ReceiverRecord<Integer, String>> inboundPointEarnedMsg;

    private final SoccerGroupRepository soccerGroupRepository;

    private final ObjectMapper objectMapper;

    public KafkaMessagingService(
            @Qualifier("getPointEarnedReceiverOption") ReceiverOptions<Integer, String> pointEarnedReceiverOptions,
            SoccerGroupRepository soccerGroupRepository)
    {
        this.soccerGroupRepository = soccerGroupRepository;

        inboundPointEarnedMsg = KafkaReceiver.create(pointEarnedReceiverOptions).receive();

        objectMapper = new ObjectMapper();

        inboundPointEarnedMsg.doOnError(e ->{
                    log.error(e.toString());
                })
                .flatMap(r -> {
                            PointEarnedMessage pointEarnedMessage = null;

                            try {
                                pointEarnedMessage = objectMapper.readValue(r.value(), PointEarnedMessage.class);

                                log.debug(pointEarnedMessage.toString());

                                int pointEarned = pointEarnedMessage.getEarnedPoint();
                                Long groupId = pointEarnedMessage.getGroupId();

                                r.receiverOffset().acknowledge();

                                return soccerGroupRepository.incrementGroupPoint(pointEarned, groupId);

                            } catch (JsonProcessingException e)
                            {
                                r.receiverOffset().acknowledge();
                                log.error(e.toString());
                                return Mono.empty();
                            }
                        }
                )
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

    }


}
