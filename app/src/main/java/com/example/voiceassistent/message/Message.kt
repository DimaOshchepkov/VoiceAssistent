package com.example.voiceassistent.message


import android.icu.text.SimpleDateFormat
import com.example.voiceassistent.db.MessageEntity
import java.util.*


class Message{
    var text: String = ""
    var date: Date = Date()
    var isSend: Boolean = false

    companion object {
        val dateFormat = SimpleDateFormat("HH:mm")
    }

    constructor() {}

    constructor(text: String, isSend: Boolean) {
        this.text = text
        this.isSend = isSend
    }
    constructor(messageEntity: MessageEntity) : this() {
        date = dateFormat.parse(messageEntity.date)
        text = messageEntity.text
        isSend = messageEntity.isSend
    }
}