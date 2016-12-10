package com.mc.controller

import com.mc.model.Message
import com.mc.service.MessagesExternalService
import com.mc.service.RabbitMQService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NO_CONTENT

@RestController
@RequestMapping(value = '/send', produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
class SendController {

    @Autowired RabbitMQService rabbitMQService
    @Autowired MessagesExternalService messagesExternalService

    static def log = LoggerFactory.getLogger(SendController.class)

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity publishMessage(@RequestBody Message message) {

        if (!message.isValid()) {
            Map messageMap = message as Map
            log.warn("Message not sent as all mandatory fields not present or are wrong - ${messageMap}")

            return new ResponseEntity([error: "All fields are mandatory and should be correct", receivedData: messageMap], BAD_REQUEST)
        }

        def response = messagesExternalService.sendMessage(message)
        if (response != HttpStatus.OK) {
            log.warn("The message is going to be queued due to some external service issue")
            rabbitMQService.publish(message)
        }

        // ORM app - insert with response result status

        new ResponseEntity(NO_CONTENT)
    }
}
