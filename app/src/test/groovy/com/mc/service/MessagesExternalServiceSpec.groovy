package com.mc.service

import com.mc.model.Message
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification

import javax.xml.ws.http.HTTPException

class MessagesExternalServiceSpec extends Specification {

    @Shared RESTClient restClient

    def "logs succesfully sent when success response"() {
        given:

            HttpResponseDecorator resp = Mock() {
                getStatus() >> 200
            }
            restClient = Mock()


            Logger logMock = Mock()
            MessagesExternalService service = new MessagesExternalService(restClient: restClient)
            service.log = logMock
            Message message = new Message(phoneNumber: '1234', message: 'some_message', msgType: 'sms')

        when:
            def response = service.sendMessage(message)

        then:
            response == HttpStatus.OK

        and:
            1 * restClient.get(_) >> resp
            1 * logMock.info("Successfully sent to external service")
    }

    def "logs the error sent when there is a wrong host"() {
        given:
            restClient = new RESTClient('http://host:8080')
            Logger logMock = Mock()
            MessagesExternalService service = new MessagesExternalService(restClient: restClient)
            service.log = logMock
            Message message = new Message(phoneNumber: '1234', message: 'some_message', msgType: 'sms')


        when:
            def response = service.sendMessage(message)

        then:
            response == null

        and:
            1 * logMock.error("Error sending message external service")
    }


//    def "Returns expected response in #msgType according to phoneNumber: #phoneNumber and message: #message"() {
//        given:
//            HTTPBuilder httpBuilder = new HTTPBuilder('http://localhost:9100/tinsa/')
//            MessagesExternalService service = new MessagesExternalService(http: httpBuilder)
//            Message message = new Message(phoneNumber: phoneNumber, message: messageText, msgType: msgType)
//
//
//        when:
//            def response = service.sendMessage(message)
//
//        then:
//            response == expectedResponse
//
//        where:
//            phoneNumber | messageText    | msgType | expectedResponse
//            '600000000' | 'some_message' | 'sms'   | HttpStatus.OK
//            '600000000' | ''             | 'sms'   | HttpStatus.INTERNAL_SERVER_ERROR
//            ''          | 'some_message' | 'sms'   | HttpStatus.INTERNAL_SERVER_ERROR
//            '100000000' | 'some_message' | 'sms'   | HttpStatus.INTERNAL_SERVER_ERROR
//            '600000000' | 'sóme_message' | 'sms'   | HttpStatus.INTERNAL_SERVER_ERROR
//    }
}
