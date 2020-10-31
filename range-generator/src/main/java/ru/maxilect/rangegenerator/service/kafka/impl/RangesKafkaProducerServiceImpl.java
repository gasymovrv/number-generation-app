package ru.maxilect.rangegenerator.service.kafka.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.maxilect.rangegenerator.domain.Range;
import ru.maxilect.rangegenerator.service.kafka.BaseKafkaProducerService;
import ru.maxilect.rangegenerator.service.kafka.RangesKafkaProducerService;

@Service
@Slf4j
public class RangesKafkaProducerServiceImpl extends BaseKafkaProducerService implements RangesKafkaProducerService {

    public RangesKafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    public void sendNextRange(String topic, Range previous) {
        Range nextRange = Range.next(previous);
        BigInteger start = nextRange.getStart();
        BigInteger end = nextRange.getEnd();

        send(topic, String.format("%d-%d", start, end), nextRange);
    }

    @Override
    public void sendFirstRange(String topic, BigInteger startNumber, BigInteger range) {
        BigInteger end = startNumber.add(range.subtract(BigInteger.ONE));

        send(topic, String.format("%d-%d", startNumber, end), new Range(startNumber, end));
    }

    @Override
    public void send(String topic,
                     String kafkaKey,
                     Range document) {
        commonSend(topic, kafkaKey, document);
    }
}
