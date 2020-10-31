package ru.maxilect.numberapi.service.kafka;

import java.util.concurrent.BlockingQueue;

public interface KafkaListenerService<T> {

    BlockingQueue<T> getBuffer();
}
