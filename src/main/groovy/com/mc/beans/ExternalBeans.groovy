package com.mc.beans

import com.mc.config.MessagesExternalConfig
import com.mc.service.MessagesExternalService
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import groovyx.net.http.URIBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
class ExternalBeans extends WebMvcConfigurerAdapter {

    @Autowired MessagesExternalConfig messagesExternalConfig

    @Bean
    MessagesExternalService messagesExternalService() {
        new MessagesExternalService(restClient: restClient)
    }

    def getRestClient() {
        RESTClient client = new RESTClient(getURIBuilder())
        client.headers << ['Content-Type': ContentType.JSON]
        client
    }

    def getURIBuilder() {
        def uri = new URIBuilder("http://${messagesExternalConfig.host}")
        uri.port = messagesExternalConfig.port
        uri
    }
}
