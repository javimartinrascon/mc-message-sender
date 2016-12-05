package com.mc.protocol.rabbitmq

import org.springframework.amqp.core.MessageDeliveryMode


@groovy.transform.builder.Builder
class MessageProperties {
    MessageDeliveryMode deliveryMode
    String queueName, exchange
}
