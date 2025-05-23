package com.example.cap

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.android.BaseMessage
import com.sendbird.android.SendBird
import com.sendbird.android.UserMessage
import java.util.*

class MessageAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val VIEW_TYPE_USER_MESSAGE_ME = 10
    private val VIEW_TYPE_USER_MESSAGE_OTHER = 11

    private var messages: MutableList<BaseMessage>
    private var context: Context

    init {
        messages = ArrayList()
        this.context = context
    }

    fun loadMessages(messages: MutableList<BaseMessage>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    fun addFirst(message: BaseMessage) {
        messages.add(0, message)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            VIEW_TYPE_USER_MESSAGE_ME  -> {
                MyUserHolder(layoutInflater.inflate(R.layout.my_message, parent, false))
            }
            VIEW_TYPE_USER_MESSAGE_OTHER ->  {
                OtherUserHolder(layoutInflater.inflate(R.layout.other_message, parent, false))
            }
            else -> MyUserHolder(layoutInflater.inflate(R.layout.my_message, parent, false)) //Generic return

        }
    }

    override fun getItemViewType(position: Int): Int {

        val message = messages.get(position)

        when (message) {
            is UserMessage -> {
                if (message.sender.userId.equals(SendBird.getCurrentUser().userId)) return VIEW_TYPE_USER_MESSAGE_ME
                else return VIEW_TYPE_USER_MESSAGE_OTHER
            }
            else ->  return -1
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                holder as MyUserHolder
                holder.bindView(context, messages.get(position) as UserMessage)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                holder as OtherUserHolder
                holder.bindView(context, messages.get(position) as UserMessage)
            }
        }

    }

}