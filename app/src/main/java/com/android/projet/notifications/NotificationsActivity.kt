package com.android.projet.notifications

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.Binome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private val demandes = mutableListOf<Binome>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        recyclerView = findViewById(R.id.recyclerNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(
            demandes,
            onAccept = { binome ->
                val currentUid = auth.currentUser?.uid ?: return@NotificationAdapter

                db.collection("users").document(currentUid)
                    .update("binomesAcceptes", FieldValue.arrayUnion(binome.uuid))

                db.collection("users").document(binome.uuid)
                    .update("binomesAcceptes", FieldValue.arrayUnion(currentUid))

                db.collection("users").document(currentUid)
                    .update("demandesRecues", FieldValue.arrayRemove(binome.uuid))

                demandes.remove(binome)
                adapter.notifyDataSetChanged()

                Toast.makeText(this, "Demande acceptée", Toast.LENGTH_SHORT).show()
            },
            onRefuse = { binome ->
                val currentUid = auth.currentUser?.uid ?: return@NotificationAdapter

                db.collection("users").document(currentUid)
                    .update("demandesRecues", FieldValue.arrayRemove(binome.uuid))

                demandes.remove(binome)
                adapter.notifyDataSetChanged()

                Toast.makeText(this, "Demande refusée", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter = adapter
        fetchNotifications()
    }

    private fun fetchNotifications() {
        val currentUid = auth.currentUser?.uid ?: return
        db.collection("users").document(currentUid).get()
            .addOnSuccessListener {
                val list = it.get("demandesRecues") as? List<String> ?: listOf()
                demandes.clear()
                for (uid in list) {
                    db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                        val binome = doc.toObject(Binome::class.java)
                        binome?.uuid = uid
                        binome?.let {
                            demandes.add(it)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }
}
