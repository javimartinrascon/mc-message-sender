package com.mc.utils

import spock.lang.Specification
import spock.lang.Unroll

class MessageUtilsSpec extends Specification {

    @Unroll
    void "transforms #text removing accents and giving #expected"() {
        expect:
            MessageUtils.cleanMessageText(text) == expected

        where:
            text     | expected
            ' a '    | 'a'
            '  a  '  | 'a'
            'A'      | 'a'
            'áàä ÁÀÄ' | 'aaa aaa'
            'éèëÉÈË' | 'eeeeee'
            'íìïÍÌÏ' | 'iiiiii'
            'óòöÓÒÖ' | 'oooooo'
            'úùuÚÙÜ' | 'uuuuuu'
            'çÇ-'    | 'cc-'
            'ñÑ'     | 'ññ'
    }
}
