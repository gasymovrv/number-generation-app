package ru.maxilect.rangegenerator.service.kafka;

import java.math.BigInteger;
import ru.maxilect.rangegenerator.domain.Range;

public interface RangesKafkaProducerService extends KafkaProducerService<Range> {

    void sendNextRange(String topic, Range previous);

    void sendFirstRange(String topic, BigInteger startNumber, BigInteger range);
}
