package com.mc.beans

import com.mc.config.MessagesExternalConfig
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import spock.lang.Specification

class ExternalBeansSpec extends Specification {

    def "httpbuilder is created with the expected values"() {
        given:
            MessagesExternalConfig externalConfig = new MessagesExternalConfig(host: 'host', port: 1234)
            ExternalBeans externalBeans = new ExternalBeans(messagesExternalConfig: externalConfig)

        when:
            RESTClient restClient = externalBeans.restClient

        then:
            restClient.uri.toString() == 'http://host:1234'
    }
}
