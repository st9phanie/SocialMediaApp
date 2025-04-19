package com.example.cap

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.android.UserMessage
import java.text.SimpleDateFormat
import java.util.Locale

class OtherUserHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val messageText: TextView = view.findViewById(R.id.othermessage)
    private val timestamp: TextView = view.findViewById(R.id.timestamp)
    private val date: TextView = view.findViewById(R.id.date)
    private val pfp: TextView = view.findViewById(R.id.pfp)
    private val user: TextView = view.findViewById(R.id.username)


    fun bindView(context: Context, message: UserMessage) {

        messageText.setText(message.message)

        timestamp.text = DateUtil.formatTime(message.createdAt)

        date.visibility = View.VISIBLE
        date.text = DateUtil.formatDate(message.createdAt)

        user.text = message.sender.nickname

    }

    object DateUtil {
        fun formatTime(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }


        fun formatDate(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }
    }
}