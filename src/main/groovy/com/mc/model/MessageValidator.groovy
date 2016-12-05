package com.mc.model

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

import java.util.regex.Pattern

class MessageValidator {

    @Autowired
    Environment env

    static accents = "áàäéèëíìïóòöúùuÁÀÄÉÈËÍÌÏÓÒÖÚÙÜçÇ"
    static ascii = "aaaeeeiiiooouuuAAAEEEIIIOOOUUUcC"

    static boolean validateType(Message message, List allowedTypes) {
        if (!message.msgType) return false
        message.msgType in getMessageTypes()
    }

    private getMessageTypes() {
        env.getProperty('messageTypes').split(',')
    }

    boolean validateText(Message message) {
        if (message.message) {
            String clean = accents.inject { String text, character ->
                text.replace(character, ascii[accents.indexOf(character)])
            }
            clean.toLowerCase()
            return true
        }
        false
    }

    private cleanMessageText(String text) {
        String clean = accents.inject { String s, character ->
            s.replace(character, ascii[accents.indexOf(character)])
        }
        clean.toLowerCase()
    }

    static boolean validatePhoneNumber(Message message) {
        Pattern pattern = createPattern(message.msgType)
        message.phoneNumber.matches(pattern)
    }

    private static createPattern(String messageType) {
        if (messageType == 'sms')
            return Pattern.compile('6\\d{8}')
        if (messageType == 'fax')
            return Pattern.compile('91\\d{7}')
    }


}
