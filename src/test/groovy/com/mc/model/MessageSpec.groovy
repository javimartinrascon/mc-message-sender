package com.mc.model

import org.springframework.core.env.Environment
import spock.lang.Specification
import spock.lang.Unroll

class MessageSpec extends Specification {

    @Unroll
    def "Validates type"() {
        given:
            Message message = new Message(phoneNumber: '1234', message: 'text', msgType: msgType,)
        when:
            boolean result = message.validType

        then:
            result == expected

        where:
            msgType | expected
            'sms'   | true
            'fax'   | true
            'email' | false
            ''      | false
    }

    @Unroll
    def "Validates phone number for sms"() {
        given:
            Message message = new Message(phoneNumber: phoneNumber, message: messageText, msgType: 'sms')

        when:
            boolean result = message.validPhoneNumber

        then:
            result == expected

        where:
            phoneNumber | messageText | expected
            '600000000' | 'message'   | true
            '60000000'  | 'message'   | false
            '6000 0000'  | 'message'   | false
            '910000000' | 'message'   | false
            '60d00000'  | 'message'   | false
            ''          | 'message'   | false
    }

    @Unroll
    def "Validates phone number for fax"() {
        given:
            Message message = new Message(phoneNumber: phoneNumber, message: messageText, msgType: 'fax')

        when:
            boolean result = message.validPhoneNumber

        then:
            result == expected

        where:
            phoneNumber | messageText | expected
            '910000000' | 'message'   | true
            '900000000' | 'message'   | false
            '91000 000' | 'message'   | false
            '10000000'  | 'message'   | false
            '60d00000'  | 'message'   | false
            'x00000000' | 'message'   | false
            ''          | 'message'   | false
    }

    @Unroll
    def "Validates Message"() {
        given:
            Message message = new Message(message: messageText)

        when:
            boolean result = message.isValidText()

        then:
            result == expected

        where:
            messageText | expected
            'valid msg' | true
            ''| false
            ' ' | false
    }

    @Unroll
    void "Validates request message"() {
        given:
            Message message = new Message(phoneNumber: phoneNumber, message: messageText, msgType: msgType)

        when:
            boolean result = message.isValid()

        then:
            result == expected

        where:
            phoneNumber | messageText    | msgType | expected
            '600000000' | 'some message' | 'sms'   | true
            ''          | 'some message' | 'sms'   | false
            '600000000' | ''             | 'sms'   | false
            '600000000' | 'some message' | ''      | false
            ''          | ''             | 'sms'   | false
            ''          | 'some message' | ''      | false
            '600000000' | ''             | ''      | false
            ''          | ''             | ''      | false

    }

    void "Returns the properties map when as Map is called"() {
        given:
            Message message = new Message(phoneNumber: '1234', message: 'some_message', msgType: 'sms')

        expect:
            expectedMessageMap == message as Map

    }

    private getExpectedMessageMap() {
        [phoneNumber: '1234', message: 'some_message', msgType: 'sms']
    }
}
