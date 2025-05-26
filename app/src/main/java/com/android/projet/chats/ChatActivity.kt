package com.android.projet.chats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var btnSend: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: ChatAdapter
    private lateinit var binomeUid: String
    private lateinit var currentUid: String
    private lateinit var chatId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recyclerMessages)
        inputMessage = findViewById(R.id.inputMessage)
        btnSend = findViewById(R.id.buttonSend)

        currentUid = auth.currentUser?.uid ?: return
        binomeUid = intent.getStringExtra("binomeUid") ?: return

        chatId = if (currentUid < binomeUid) "$currentUid-$binomeUid" else "$binomeUid-$currentUid"

        adapter = ChatAdapter(this, currentUid)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.map { it.toObject(Message::class.java) } ?: emptyList()
                adapter.submitList(messages)
                recyclerView.scrollToPosition(messages.size - 1)
            }

        btnSend.setOnClickListener {
            val text = inputMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val msg = Message(currentUid, text, System.currentTimeMillis())
                db.collection("chats").document(chatId).collection("messages").add(msg)
                inputMessage.setText("")
            }
        }
    }
}