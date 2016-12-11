import groovy.json.JsonSlurper
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification

abstract class BaseSpec extends Specification {
    public static final String BASE_URL = 'http://localhost:8080/'

    public static final String DEFAULT_QUEUE = "QpidTestQueue"

    def getMessage(RabbitTemplate rabbitMQClient) {
        try {
            Message message = rabbitMQClient.receive(DEFAULT_QUEUE, 2000)
            if (message?.body) {
                return new JsonSlurper().parseText(new String(message.body))
            }
        } catch (ex) {
        }
    }

}