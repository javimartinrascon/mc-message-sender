package com.mc.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = 'rabbit')
class RabbitMQConfig {
    String host
    Integer port

    String username
    String password
    String queueName
    String virtualHost

    Integer heartbeat
    Integer networkRecoveryInterval
    Integer connectionTimeout
}
