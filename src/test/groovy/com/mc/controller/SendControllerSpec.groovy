package com.mc.controller

import com.mc.model.Message
import com.mc.service.MessagesExternalService
import com.mc.service.RabbitMQService
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NO_CONTENT

class SendControllerSpec extends Specification {

    void "Error response with wrong message and log the error"() {
        given:
            Message emptyMessage = new Message()
            Logger logMock = Mock()
            SendController controller = new SendController()
            controller.log = logMock

        when:
            ResponseEntity response = controller.publishMessage(emptyMessage)


        then:
            response.body == [error: "All fields are mandatory and should be correct", receivedData: expectedMessageMap]
            response.statusCode == BAD_REQUEST

        and:
            1 * logMock.warn("Message not sent as all mandatory fields not present or are wrong - ${expectedMessageMap}")

    }

    void "Successfull response with correct message, but queued message because of an issue in external service"() {
        given:
            Message message = Mock() {
                isValid() >> true
            }
            RabbitMQService rabbitServiceMock = Mock()
            Logger logMock = Mock()
            MessagesExternalService messagesExternalService = Mock() {
                sendMessage(message) >> HttpStatus.INTERNAL_SERVER_ERROR
            }
             SendController controller = new SendController(rabbitMQService: rabbitServiceMock,
                     messagesExternalService: messagesExternalService)
            controller.log = logMock

        when:
            ResponseEntity response = controller.publishMessage(message)

        then:
            1 * rabbitServiceMock.publish(message)

        and:
            1 * logMock.warn("The message is going to be queued due to some external service issue")

            response.statusCode == NO_CONTENT
    }

    void "Successfull response with correct message"() {
        given:
            Message message = Mock() {
                isValid() >> true
            }
            RabbitMQService rabbitServiceMock = Mock()
            Logger logMock = Mock()
            MessagesExternalService messagesExternalService = Mock() {
                sendMessage(message) >> HttpStatus.OK
            }
            SendController controller = new SendController(rabbitMQService: rabbitServiceMock,
                    messagesExternalService: messagesExternalService)
            controller.log = logMock

        when:
            ResponseEntity response = controller.publishMessage(message)

        then:
            0 * rabbitServiceMock.publish(message)

        and:
            0 * logMock.warn("The message is going to be queued due to some external service issue")

            response.statusCode == NO_CONTENT
    }

    private static getExpectedMessageMap() {
        [phoneNumber: null , message: null, msgType: null]
    }


}