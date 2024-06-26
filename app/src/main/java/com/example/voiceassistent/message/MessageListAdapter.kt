package com.example.voiceassistent.message

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.R

class MessageListAdapter : RecyclerView.Adapter<MessageViewHolder>(){
    private val ASSISTANT_TYPE = 0
    private val USER_TYPE = 1
    var messageList : MutableList<Message> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view : View  = if (viewType == USER_TYPE) {
            LayoutInflater.from(parent.context).inflate(R.layout.user_message, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.assistent_message, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.count()
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    override fun getItemViewType(index: Int): Int {
        val message: Message = messageList[index]
        return if (message.isSend) {
            USER_TYPE
        } else {
            ASSISTANT_TYPE
        }
    }
}