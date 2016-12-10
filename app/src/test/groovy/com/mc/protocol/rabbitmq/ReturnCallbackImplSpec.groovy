package com.mc.protocol.rabbitmq

import org.slf4j.Logger
import org.springframework.amqp.core.Message
import spock.lang.Specification
import org.springframework.amqp.core.MessageProperties

class ReturnCallbackImplSpec extends Specification {

    ReturnCallbackImpl returnCallback = new ReturnCallbackImpl()

    def "Should log an error message when it is called" () {
        given:
            def logMock = Mock(Logger)

            MessageProperties messageProperties = new MessageProperties()
            Message message =  new Message('some_message'.bytes, messageProperties)

            returnCallback.log = logMock

        when:
            returnCallback.returnedMessage(message, 999, "reply", 'exchange', 'key')

        then:
            1 * logMock.error("RabbitMQ server failed to deliver message. ReplyCode[999], ReplyText[reply] and message: some_message")
    }

}
