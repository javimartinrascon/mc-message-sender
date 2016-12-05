package com.mc.protocol.rabbitmq

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate

class ReturnCallbackImpl implements RabbitTemplate.ReturnCallback {

    static def log  = LoggerFactory.getLogger(ReturnCallbackImpl.class)

    @Override
    void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {

        log.error("RabbitMQ server failed to deliver message. ReplyCode[$replyCode], ReplyText[${replyText}] and message: ${new String(message.body)}")
    }
}
