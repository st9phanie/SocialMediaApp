package com.example.cap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.SendUserMessageHandler
import com.sendbird.android.GroupChannel.GroupChannelGetHandler
import com.sendbird.android.SendBird.ChannelHandler


class ChannelActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupChannel: GroupChannel
    private lateinit var backBtn: ImageButton
    private lateinit var send: Button
    private lateinit var textbox: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_chat)

        //setUpRecyclerView()
        setButtonListeners()
    }

    private fun setButtonListeners() {
        backBtn= findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, DirectMessages::class.java)
            startActivity(intent)
        }

        send = findViewById(R.id.button_gchat_send)
        send.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage()
    {
        textbox= findViewById(R.id.textbox)
        val params = UserMessageParams()
            .setMessage(textbox.text.toString())
        groupChannel.sendUserMessage(params,
            SendUserMessageHandler { userMessage, e ->
                if (e != null) {    // Error.
                    return@SendUserMessageHandler
                }
                adapter.addFirst(userMessage)
                textbox.text.clear()
            })
    }


    private fun setUpRecyclerView() {
        adapter = MessageAdapter(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.scrollToPosition(0)

    }


}