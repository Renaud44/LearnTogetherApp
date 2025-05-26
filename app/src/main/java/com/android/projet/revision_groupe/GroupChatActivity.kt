package com.android.projet.revision_groupe

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.MessageGroupe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GroupChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupMessageAdapter
    private val messages = mutableListOf<MessageGroupe>()

    private lateinit var inputMessage: EditText
    private lateinit var buttonSend: Button

    private lateinit var groupId: String
    private lateinit var userId: String
    private lateinit var userName: String

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        groupId = intent.getStringExtra("groupId") ?: return
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        recyclerView = findViewById(R.id.recyclerGroupMessages)
        inputMessage = findViewById(R.id.inputGroupMessage)
        buttonSend = findViewById(R.id.buttonSendGroupMessage)

        adapter = GroupMessageAdapter(messages, userId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Get current user's pseudo
        db.collection("users").document(userId).get().addOnSuccessListener {
            userName = it.getString("pseudo") ?: "Moi"
        }

        db.collection("groupes").document(groupId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, _ ->
                messages.clear()
                snapshots?.forEach { doc ->
                    messages.add(doc.toObject(MessageGroupe::class.java))
                }
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)
            }

        buttonSend.setOnClickListener {
            val text = inputMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val message = MessageGroupe(userId, userName, text)
                db.collection("groupes").document(groupId)
                    .collection("messages")
                    .add(message)
                inputMessage.setText("")
            }
        }
    }
}