package com.mc.service

import com.mc.model.Message
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class MessagesExternalService {

    static def log = LoggerFactory.getLogger(MessagesExternalService.class)

    RESTClient restClient

    def sendMessage(Message message) {

        def status
        try {
            HttpResponseDecorator response = restClient.get(path: "/tinsa/${message.msgType}",
                    query: [phone: message.phoneNumber, message: message.message],
                    contentType: ContentType.JSON)
            status = HttpStatus.valueOf(response.status)
            log.info("Successfully sent to external service")
        } catch (Exception e){
            log.error("Error sending message external service")
        }
        status
    }

}
