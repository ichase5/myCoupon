package org.example.myCoupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/*
 * Kafka 相关的服务接口定义
 */
public interface IKafkaService {

    /*
     * 消费优惠券 Kafka 消息
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
