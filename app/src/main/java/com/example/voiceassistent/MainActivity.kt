package com.example.voiceassistent

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.db.DBHelper
import com.example.voiceassistent.db.MessageEntity
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
    private var prevKeyBoardHeight: Int = 0

    protected var messageListAdapter: MessageListAdapter = MessageListAdapter()

    private lateinit var dBHelper: DBHelper
    private lateinit var database: SQLiteDatabase

    private lateinit var sPref: SharedPreferences
    val APP_PREFERENCES = "mysettings"
    private var isLight = true
    private val THEME = "THEME"

    private fun setTheme(isLight: Boolean) {
        if (isLight) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }
        else {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this)

        sPref = getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE)
        isLight = sPref.getBoolean(THEME, true)
        setTheme(isLight)


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

        val contentView = findViewById<View>(android.R.id.content)
        contentView.viewTreeObserver.addOnGlobalLayoutListener {
            val keyboardHeight = contentView.rootView.height - contentView.height
            if (prevKeyBoardHeight - keyboardHeight < 0) { // Если высота клавиатуры
                // Выполните действие, когда клавиатура отображается
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
            } else {
                // Выполните действие, когда клавиатура скрывается
            }
            prevKeyBoardHeight = keyboardHeight
        }

        dBHelper = DBHelper(this)
        database = dBHelper.writableDatabase

        val cursor = database.query(dBHelper.TABLE_NAME, null, null, null,
            null, null, null)
        restoreChatFromDB(cursor)
        cursor.close()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.day_settings -> {
                isLight = true
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
            R.id.night_settings -> {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                isLight = false
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun saveChat() {
        val contentValuesList = messageListAdapter.messageList.map { message ->
            MessageEntity(message).run {
                ContentValues().apply {
                    put(dBHelper.FIELD_MESSAGE, text)
                    put(dBHelper.FIELD_SEND, isSend)
                    put(dBHelper.FIELD_DATE, date)
                }
            }
        }

        database.let { db ->
            db.beginTransaction()
            try {
                contentValuesList.forEach {
                    db.insert(dBHelper.TABLE_NAME, null, it)
                }
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                Log.e("Database", "Ошибка при вставке данных", e)
            } finally {
                db.endTransaction()
            }
        }
    }

    private fun saveTheme(isLight: Boolean) {
        val editor: SharedPreferences.Editor = sPref.edit()
        editor.putBoolean(THEME, isLight)
        editor.apply()
    }
    override fun onStop() {
        saveTheme(isLight)

        database.delete(dBHelper.TABLE_NAME, null, null)
        saveChat()

        super.onStop()
    }

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }

    private fun restoreChatFromDB(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            messageListAdapter.messageList.clear()
            val messageIndex = cursor.getColumnIndex(dBHelper.FIELD_MESSAGE)
            val dateIndex = cursor.getColumnIndex(dBHelper.FIELD_DATE)
            val sendIndex = cursor.getColumnIndex(dBHelper.FIELD_SEND)
            do {
                val entity = MessageEntity(
                    cursor.getString(messageIndex),
                    cursor.getString(dateIndex), cursor.getInt(sendIndex)==1
                )
                val message = Message(entity)
                messageListAdapter.messageList.add(message)
            } while (cursor.moveToNext())
        }

    }

}