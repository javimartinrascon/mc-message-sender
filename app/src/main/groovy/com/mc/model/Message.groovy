package com.mc.model

import com.mc.utils.MessageUtils

import java.util.regex.Pattern

class Message {

    static final validTypes = ['sms', 'fax']

    String phoneNumber
    String message
    String msgType

    boolean isValid() {
        if (!phoneNumber || !message || !msgType) return false
        MessageUtils.cleanMessageText(message)
        validType && validPhoneNumber && validText
    }

    Object asType(Class clazz) {
        if (clazz == Map) {
            [phoneNumber: phoneNumber, message: message, msgType: msgType]
        }
    }

    boolean isValidPhoneNumber() {
        Pattern pattern = createPattern(msgType)
        phoneNumber.trim()
        phoneNumber.matches(pattern)
    }

    boolean isValidType() {
        msgType.trim()
        msgType in validTypes
    }

    boolean isValidText() {
        message.trim()
    }

    private createPattern(String messageType) {
        if (messageType == 'sms')
            return Pattern.compile('6\\d{8}')
        if (messageType == 'fax')
            return Pattern.compile('91\\d{7}')
    }
}
