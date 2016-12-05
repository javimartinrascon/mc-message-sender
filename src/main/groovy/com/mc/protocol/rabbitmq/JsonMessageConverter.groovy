package com.mc.protocol.rabbitmq

import groovy.json.JsonBuilder
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.support.converter.AbstractMessageConverter
import org.springframework.amqp.support.converter.MessageConversionException
import com.mc.model.Message as SenderMessage

class JsonMessageConverter extends AbstractMessageConverter {
    @Override
    protected Message createMessage(Object object, org.springframework.amqp.core.MessageProperties messageProperties) {
        MessageBuilder.
                withBody(buildMessage((SenderMessage)object).bytes).
                setDeliveryMode(messageProperties.deliveryMode).
                build()
    }

    @Override
    Object fromMessage(Message message) throws MessageConversionException {
        return null
    }

    private String buildMessage(SenderMessage message) {
        new  JsonBuilder(
                {
                    message_text message.message
                    phone_number message.phoneNumber
                    message_type message.msgType
                }
        )
    }

}
