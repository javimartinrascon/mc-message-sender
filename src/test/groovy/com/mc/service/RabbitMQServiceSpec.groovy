package com.mc.service

import com.mc.model.Message
import com.mc.protocol.rabbitmq.MessageProperties
import org.slf4j.Logger
import org.springframework.amqp.core.MessageDeliveryMode
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification

class RabbitMQServiceSpec extends Specification {

    static final PHONE_NUMBER = '12345'
    static final MESSAGE_TEXT =  'some message'
    static final MESSAGE_TYPE = 'sms'

    def "log successful message when it is correctly published" () {
        given:
            Logger logMock = Mock()
            MessageProperties messageProperties = new MessageProperties(deliveryMode: MessageDeliveryMode.PERSISTENT, queueName: 'dummy', exchange: 'exchange')
            Message message = new Message(phoneNumber: PHONE_NUMBER, message: MESSAGE_TEXT, msgType: MESSAGE_TYPE)
            RabbitTemplate rabbitTemplateMock = Mock(RabbitTemplate)
            RabbitMQService service = new RabbitMQService(rabbitTemplate: rabbitTemplateMock, messageProperties: messageProperties)
            service.log = logMock

        when:
            service.publish(message)

        then:
            1 * logMock.info("Message successfully published in the queue")
    }

    void "log error message when there is a problem publishing the message"() {
        given:
            Logger logMock = Mock()
            MessageProperties messageProperties = new MessageProperties(deliveryMode: MessageDeliveryMode.PERSISTENT, queueName: 'dummy', exchange: 'exchange')
            Message message = new Message(phoneNumber: PHONE_NUMBER, message: MESSAGE_TEXT, msgType: MESSAGE_TYPE)
            RuntimeException exception = new RuntimeException('test exception')
            RabbitTemplate rabbitTemplateMock = Mock(RabbitTemplate) {
                convertAndSend(messageProperties.exchange,
                        messageProperties.queueName, message) >> { throw exception}
            }
            RabbitMQService service = new RabbitMQService(rabbitTemplate: rabbitTemplateMock, messageProperties: messageProperties)
            service.log = logMock

        when:
            service.publish(message)

        then:
            1 * logMock.error("Error publishing message [cause: test exception]")

    }
}
