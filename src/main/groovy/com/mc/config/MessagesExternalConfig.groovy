package com.mc.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = 'external')
class MessagesExternalConfig {
    String host
    Integer port

    String basePath
}
