import com.google.common.io.Files
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.apache.qpid.server.Broker
import org.apache.qpid.server.BrokerOptions
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Shared
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON

class MessageSenderQueueSpec extends BaseSpec {

    static final MESSAGE_TEXT = 'some message'
    static final MESSAGE_TYPE = 'sms'
    static final CORRECT_SMS_NUMBER = '600000000'

    @Shared
    RESTClient restClient
    @Shared
    RabbitTemplate rabbitMqClient
    @Shared
    Broker broker = new Broker()
    @Shared
    RabbitAdmin rabbitAdmin
    @Shared
    BrokerOptions brokerOptions

    static final def DEFAULT_HANDLER = { HttpResponseDecorator resp, data ->
        resp.data = data
        resp
    }

    def setupSpec() {

        restClient = new RESTClient(BASE_URL)
        restClient.handler.failure = DEFAULT_HANDLER

        createQueueForTest()
    }

    def cleanup() {
        cleanQueue()
    }

    def cleanupSpec() {
        broker.shutdown()
    }

    @Unroll
    def "return error message when message has missing values"() {
        when:
            def jsonResponse = restClient.post(path: 'send',
                    body: [message    : msgText,
                           msgType    : msgType,
                           phoneNumber: phNumber],
                    requestContentType: JSON)

        then:
            jsonResponse.status == 400

        and:
            getMessage(rabbitMqClient) == null

        where:
            msgText | msgType | phNumber
            null    | 'type'  | 'number'
            'text'  | null    | 'number'
            'text'  | 'type'  | null
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

    def "registering the message in the queue"() {

        when:
            def jsonResponse = restClient.post(path: 'send',
                    body: [message    : MESSAGE_TEXT,
                           msgType    : MESSAGE_TYPE,
                           phoneNumber: CORRECT_SMS_NUMBER],
                    requestContentType: JSON)

        then:
            jsonResponse.status == 204
        and:
            getMessage(rabbitMqClient) == [message_text: MESSAGE_TEXT, message_type: MESSAGE_TYPE, phone_number: CORRECT_SMS_NUMBER]
    }

    def "auto reconnect to rabbitmq"() {

        given: "broker is down"
            broker.shutdown()

        when:
            def jsonResponse = restClient.post(path: 'send',
                    body: [message    : MESSAGE_TEXT,
                           msgType    : MESSAGE_TYPE,
                           phoneNumber: CORRECT_SMS_NUMBER],
                    requestContentType: JSON)
        then:
            jsonResponse.status == 204
            getMessage(rabbitMqClient) == null

        when: "broker is up again"
            broker.startup(brokerOptions)
            rabbitAdmin.declareQueue(new org.springframework.amqp.core.Queue(DEFAULT_QUEUE, true))


            sleep(2000)
        and:
            jsonResponse = restClient.post(path: 'send',
                    body: [message    : MESSAGE_TEXT,
                           msgType    : MESSAGE_TYPE,
                           phoneNumber: CORRECT_SMS_NUMBER],
                    requestContentType: JSON)

        then:
            jsonResponse.status == 204
            getMessage(rabbitMqClient) == [message_text: MESSAGE_TEXT, message_type: MESSAGE_TYPE, phone_number: CORRECT_SMS_NUMBER]

    }

    private createQueueForTest() {
        int port = 5673
        System.properties.setProperty('qpid.passwordPath', this.class.getResource('/passwd.properties').path)
        System.properties.setProperty('qpid.port', port as String)
        brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.work_dir", Files.createTempDir().getAbsolutePath())
        brokerOptions.setInitialConfigurationLocation(this.class.getResource("/qpid-config.json").toString())

        broker.startup(brokerOptions)

        CachingConnectionFactory cf = new CachingConnectionFactory('localhost', port);
        cf.setVirtualHost('default')
        cf.setUsername('guest')
        cf.setPassword('guest')
        rabbitAdmin = new RabbitAdmin(cf);
        rabbitAdmin.declareQueue(new org.springframework.amqp.core.Queue(DEFAULT_QUEUE, true));
        rabbitMqClient = new RabbitTemplate(cf)
    }

    private cleanQueue() {
        rabbitAdmin.purgeQueue(DEFAULT_QUEUE, true)
    }

}
