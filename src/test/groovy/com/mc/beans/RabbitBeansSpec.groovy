package com.mc.beans

import com.mc.config.RabbitMQConfig
import com.mc.protocol.rabbitmq.ReturnCallbackImpl
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification

class RabbitBeansSpec extends Specification {

    RabbitMQConfig rabbitConfig = new RabbitMQConfig(host: 'host', port: 1234, username: 'user', virtualHost: 'vHost',
            heartbeat: 10, connectionTimeout: 5000, networkRecoveryInterval: 2000)
    RabbitBeans rabbitBeans = new RabbitBeans(rabbitmqConfig: rabbitConfig)

    def "rabbitmq template should be created with return callback"() {
        when:
            RabbitTemplate template = rabbitBeans.getRabbitTemplate()

        then:
            template.connectionFactory.host == 'host'
            template.connectionFactory.port == 1234
            template.connectionFactory.username == 'user'
            template.connectionFactory.virtualHost == 'vHost'

        and:
            template.returnCallback instanceof ReturnCallbackImpl
    }
}
