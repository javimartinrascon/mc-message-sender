package com.mc.exception

import com.rabbitmq.client.Connection
import org.slf4j.Logger
import spock.lang.Specification

class RabbitMQExceptionHandlerSpec extends Specification {

    void "Logs error when there is a problem trying to reconnect to rabbitMQ"() {
        given:
            Logger logMock = Mock()
            RabbitMQExceptionHandler exceptionHandler = new RabbitMQExceptionHandler()
            exceptionHandler.log = logMock
            Connection connection = Mock()
            Throwable exception = new Throwable('some exception')

        when:
            exceptionHandler.handleConnectionRecoveryException(connection, exception)

        then:
            1 * logMock.error("Caught an exception during connection recovery: some exception")
    }
}
