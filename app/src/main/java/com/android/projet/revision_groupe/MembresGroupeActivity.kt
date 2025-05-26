package com.android.projet.revision_groupe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MembresGroupeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val membresList = mutableListOf<User>()
    private lateinit var adapter: MembresAdapter
    private val db = FirebaseFirestore.getInstance()
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var adminId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membres_groupe)

        val groupId = intent.getStringExtra("groupId") ?: return

        recyclerView = findViewById(R.id.recyclerMembres)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MembresAdapter(membresList)
        recyclerView.adapter = adapter

        db.collection("groupes").document(groupId).get()
            .addOnSuccessListener { doc ->
                val membres = doc.get("membres") as? List<String> ?: emptyList()
                adminId = doc.getString("adminId") ?: ""

                membresList.clear()

                for (uid in membres) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { userDoc ->
                            val user = userDoc.toObject(User::class.java)
                            user?.uuid = uid
                            user?.let {
                                membresList.add(it)
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur chargement membres", Toast.LENGTH_SHORT).show()
            }
    }
}