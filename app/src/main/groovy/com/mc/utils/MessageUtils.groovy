package com.mc.utils

class MessageUtils {

    static accents = "áàäéèëíìïóòöúùuÁÀÄÉÈËÍÌÏÓÒÖÚÙÜçÇ"
    static ascii = "aaaeeeiiiooouuuAAAEEEIIIOOOUUUcC"

    static cleanMessageText(String text) {
        String clean = text.trim()
        clean = accents.inject(clean) { String s, character ->
            s.replace(character, ascii[accents.indexOf(character)])
        }
        clean.toLowerCase()
    }
}
