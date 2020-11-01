package ru.maxilect.numbergenerator.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseKafkaProducerService {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;

    private final ObjectMapper objectMapper;

    protected void commonSend(String topic,
                              byte[] kafkaKey,
                              Object document) {
        byte[] json = null;
        try {
            json = objectMapper.writeValueAsBytes(document);
        } catch (JsonProcessingException e) {
            log.error("#KafkaProducerService: error while writing message to json", e);
        }
        kafkaTemplate.send(new ProducerRecord<>(topic, kafkaKey, json))
                .addCallback(this::successCallback, this::failureCallback);
    }

    private void failureCallback(Throwable throwable) {
        log.error("#KafkaProducerService: cannot send message to kafka: ", throwable);
    }

    private void successCallback(SendResult<byte[], byte[]> sendResult) {
        log.debug("#KafkaProducerService: sent message [{}]", sendResult);
    }
}
