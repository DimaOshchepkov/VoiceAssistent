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
import java.util.*
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : AppCompatActivity() {
    private lateinit var sendButton: Button
    private lateinit var chatWindow: TextView
    private lateinit var questionText: EditText
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this)

        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { it ->
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
            }

        })


        sendButton = findViewById(R.id.sendButton)
        chatWindow = findViewById(R.id.chatWindow)
        questionText = findViewById(R.id.questionField)

        sendButton.setOnClickListener {
            onSend()
        }
    }

    @SuppressLint("SetTextI18n")
    fun onSend(){
        val prompt = questionText.text.toString()
        val answer = AI().getAnswer(prompt)

        chatWindow.append(">>$prompt\n") // Добавляем вопрос в окно чата
        chatWindow.append("<<$answer\n") // Добавляем ответ в окно чата
        questionText.setText("") // Очищаем текстовое поле после отправки вопроса
        dismissKeyboard()
        textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH,null, null )
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