import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON

class MessagesSenderSpec extends Specification {

    static final String BASE_URL = 'http://localhost:8080/'
    static final MESSAGE_TEXT = 'some message'
    static final MESSAGE_TYPE = 'sms'
    static final CORRECT_SMS_NUMBER = '600000000'

    @Shared
    RESTClient restClient

    def setupSpec() {
        restClient = new RESTClient(BASE_URL)
    }

    def "hitting controller and give us a 204 code"() {

        when:
            def jsonResponse = restClient.post(path: 'send',
                    body: [message    : MESSAGE_TEXT,
                           msgType    : MESSAGE_TYPE,
                           phoneNumber: CORRECT_SMS_NUMBER],
                    requestContentType: JSON)

        then:
            jsonResponse.status == 204
    }
}