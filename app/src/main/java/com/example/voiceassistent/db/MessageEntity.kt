package com.example.voiceassistent.db

import android.icu.text.SimpleDateFormat
import com.example.voiceassistent.message.Message

class MessageEntity{
    var text: String = ""
    var date: String = ""
    var isSend: Boolean = false

    companion object {
        val dateFormat = SimpleDateFormat("HH:mm")
    }
    constructor(text: String, date: String, isSend: Boolean) {
        this.text = text
        this.date = date
        this.isSend = isSend
    }
    constructor()

    constructor(message: Message) : this() {

        date = dateFormat.format(message.date)
        text = message.text
        isSend = message.isSend
    }
}