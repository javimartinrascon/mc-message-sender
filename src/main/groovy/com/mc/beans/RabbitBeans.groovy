package com.mc.beans

import com.rabbitmq.client.ConnectionFactory
import com.mc.config.RabbitMQConfig
import com.mc.exception.RabbitMQExceptionHandler
import com.mc.protocol.rabbitmq.MessageProperties
import com.mc.protocol.rabbitmq.ReturnCallbackImpl
import com.mc.service.RabbitMQService
import org.springframework.amqp.core.MessageDeliveryMode
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory as SpringConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import com.mc.protocol.rabbitmq.JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
class RabbitBeans extends WebMvcConfigurerAdapter {

    @Autowired RabbitMQConfig rabbitmqConfig

    @Bean
    RabbitMQService rabbitmqService() {
        new RabbitMQService(rabbitTemplate: rabbitTemplate, messageProperties: messageProperties)
    }

    private MessageProperties getMessageProperties() {
        MessageProperties.builder().
                exchange('').
                queueName(rabbitmqConfig.queueName).
                deliveryMode(MessageDeliveryMode.PERSISTENT).
                build()
    }

    private RabbitTemplate getRabbitTemplate() {
        def template = new RabbitTemplate(rabbitMQConnectionFactory)
        template.setMessageConverter(new JsonMessageConverter())
        template.setReturnCallback(new ReturnCallbackImpl())
        template.setMandatory(true)
        return template
    }

    private SpringConnectionFactory getRabbitMQConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(getConnectionFactory())
        factory.setPublisherReturns(true)
        return factory
    }

    private getConnectionFactory() {
        def factory = new ConnectionFactory()
        factory.setHost(rabbitmqConfig.host)
        factory.setPort(rabbitmqConfig.port)
        factory.setUsername(rabbitmqConfig.username)
        factory.setPassword(rabbitmqConfig.password)
        factory.setVirtualHost(rabbitmqConfig.virtualHost)
        factory.setAutomaticRecoveryEnabled(true)
        factory.setTopologyRecoveryEnabled(true)
        factory.setRequestedHeartbeat(rabbitmqConfig.heartbeat)
        factory.setConnectionTimeout(rabbitmqConfig.connectionTimeout)
        factory.setNetworkRecoveryInterval(rabbitmqConfig.networkRecoveryInterval)
        factory.setExceptionHandler(new RabbitMQExceptionHandler())

        factory
    }
}
