package ru.maxilect.rangegenerator.service.kafka.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.maxilect.rangegenerator.domain.Range;
import ru.maxilect.rangegenerator.service.kafka.BaseKafkaProducerService;
import ru.maxilect.rangegenerator.service.kafka.RangesKafkaProducerService;

@Service
@Slf4j
public class RangesKafkaProducerServiceImpl extends BaseKafkaProducerService implements RangesKafkaProducerService {

    @Value("${kafka.last-number}")
    private BigInteger lastNumber;

    public RangesKafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    public void sendNextRange(String topic, Range previous) {
        Range nextRange = Range.next(previous);
        BigInteger start = nextRange.getStart();
        BigInteger end;
        if (nextRange.getEnd().compareTo(lastNumber) >= 0) {
            end = lastNumber;
            nextRange.setEnd(end);
        } else {
            end = nextRange.getEnd();
        }
        send(topic, String.format("%d-%d", start, end), nextRange);
    }

    @Override
    public void sendFirstRange(String topic, BigInteger startNumber, BigInteger range) {
        BigInteger end = startNumber.add(range.subtract(BigInteger.ONE));

        send(topic, String.format("%d-%d", startNumber, end), new Range(startNumber, end, range));
    }

    @Override
    public void send(String topic,
                     String kafkaKey,
                     Range document) {
        commonSend(topic, kafkaKey, document);
    }
}
