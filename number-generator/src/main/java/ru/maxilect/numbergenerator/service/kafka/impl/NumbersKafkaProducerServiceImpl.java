package ru.maxilect.numbergenerator.service.kafka.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.maxilect.numbergenerator.domain.NaturalNumber;
import ru.maxilect.numbergenerator.service.kafka.BaseKafkaProducerService;
import ru.maxilect.numbergenerator.service.kafka.KafkaProducerService;

@Service
@Slf4j
public class NumbersKafkaProducerServiceImpl
        extends BaseKafkaProducerService
        implements KafkaProducerService<BigInteger, NaturalNumber> {

    public NumbersKafkaProducerServiceImpl(KafkaTemplate<byte[], byte[]> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    public void send(String topic,
                     BigInteger kafkaKey,
                     NaturalNumber document) {
        commonSend(topic, kafkaKey.toByteArray(), document);
    }
}
