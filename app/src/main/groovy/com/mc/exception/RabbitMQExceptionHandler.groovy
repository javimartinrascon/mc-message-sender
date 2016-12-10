package com.mc.exception

import com.rabbitmq.client.Connection
import com.rabbitmq.client.impl.StrictExceptionHandler
import org.slf4j.LoggerFactory

class RabbitMQExceptionHandler extends StrictExceptionHandler {

    static def log = LoggerFactory.getLogger(RabbitMQExceptionHandler.class)

    @Override
    void handleConnectionRecoveryException(Connection conn, Throwable exception) {
        log.error("Caught an exception during connection recovery: $exception.message")
    }
}
