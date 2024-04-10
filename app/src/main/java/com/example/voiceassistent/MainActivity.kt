package com.example.voiceassistent

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.message.Message
import com.example.voiceassistent.message.MessageListAdapter
import java.util.*
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {
    private val sendButton: Button by lazy { findViewById(R.id.sendButton) }
    private lateinit var questionText: EditText
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var chatMessageList: RecyclerView

    protected var messageListAdapter: MessageListAdapter = MessageListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this)

        chatMessageList = findViewById(R.id.chatMessageList)
        chatMessageList.layoutManager = LinearLayoutManager(this)
        chatMessageList.adapter = messageListAdapter

        sendButton.setOnClickListener {
            onSend()
        }


        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { it ->
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
            }

        })
        questionText = findViewById(R.id.questionField)

    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    fun onSend(){
        val prompt = questionText.text.toString()

        AI().getAnswer(prompt, Consumer { answer ->
            // Обработка полученного ответа
            messageListAdapter.messageList.add(Message(prompt, isSend = true))
            messageListAdapter.messageList.add(Message(answer!!, isSend = false))
            messageListAdapter.notifyDataSetChanged()
            questionText.setText("") // Очищаем текстовое поле после отправки вопроса
            dismissKeyboard()
            textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null)
            chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
        })


    }

    private fun dismissKeyboard() {
        val view: View? = this.currentFocus // элемент, который имеет текущий фокус ввода
        if (view != null) {
            // определить менеджер, отвечающий  за ввод
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // менеджер скрывает экранную клавиатуру
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}