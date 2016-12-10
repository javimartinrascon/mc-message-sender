package com.mc.service

import com.mc.model.Message
import com.mc.protocol.rabbitmq.MessageProperties
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate

class RabbitMQService {

    static def log = LoggerFactory.getLogger(RabbitMQService.class)

    RabbitTemplate rabbitTemplate
    MessageProperties messageProperties

    void publish(Message message) {
        try {
            rabbitTemplate.convertAndSend(messageProperties.exchange, messageProperties.queueName, message)

            log.info("Message successfully published in the queue")
        } catch (Exception e) {
            log.error("Error publishing message [cause: ${e.message}]")
        }
    }
}
