package com.example.voiceassistent.message

import java.util.Date

data class Message(var text: String, var date: Date = Date(), var isSend: Boolean) {
    constructor(text: String, isSend: Boolean) : this(text, Date(), isSend)
}