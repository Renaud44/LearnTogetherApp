package com.android.projet.chats

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConversationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConversationsAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var currentUid: String
    private val usersList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversations)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            finish() // ferme l'activité si non connecté
            return
        }
        currentUid = currentUser.uid

        recyclerView = findViewById(R.id.recyclerConversations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ConversationsAdapter(usersList) { selectedUser ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("binomeUid", selectedUser.uuid)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        fetchConversations()
    }

    private fun fetchConversations() {
        db.collection("users").document(currentUid).get()
            .addOnSuccessListener { document ->
                val list = document.get("binomesAcceptes") as? List<String> ?: return@addOnSuccessListener
                for (uid in list) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            val user = doc.toObject(User::class.java)
                            if (user != null) {
                                user.uuid = uid

                                val chatId = if (currentUid < uid) "$currentUid-$uid" else "$uid-$currentUid"

                                // 🔥 Récupère le dernier message pour cette conversation
                                db.collection("chats").document(chatId)
                                    .collection("messages")
                                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener { messages ->
                                        if (!messages.isEmpty) {
                                            val lastMsg = messages.documents[0].getString("content")
                                            user.lastMessage = lastMsg
                                        }
                                        usersList.add(user)
                                        adapter.notifyDataSetChanged()
                                    }
                            }
                        }
                }
            }
    }

}