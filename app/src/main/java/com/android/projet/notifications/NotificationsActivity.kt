package com.android.projet.notifications

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.Binome
import com.android.projet.models.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private val notifications = mutableListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        recyclerView = findViewById(R.id.recyclerNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(
            notifications,
            onAcceptBinome = { binome ->
                val currentUid = auth.currentUser?.uid ?: return@NotificationAdapter

                db.collection("users").document(currentUid)
                    .update("binomesAcceptes", FieldValue.arrayUnion(binome.uuid))
                db.collection("users").document(binome.uuid)
                    .update("binomesAcceptes", FieldValue.arrayUnion(currentUid))

                supprimerNotificationsBinome(currentUid, binome.uuid)

                Toast.makeText(this, "Binôme accepté", Toast.LENGTH_SHORT).show()
            },
            onRefuseBinome = { binome ->
                val currentUid = auth.currentUser?.uid ?: return@NotificationAdapter
                supprimerNotificationsBinome(currentUid, binome.uuid)
                Toast.makeText(this, "Demande refusée", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter = adapter
        chargerNotifications()
    }

    private fun chargerNotifications() {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("notifications")
            .whereEqualTo("to", currentUid)
            .get()
            .addOnSuccessListener { docs ->
                notifications.clear()

                for (doc in docs) {
                    val notif = doc.toObject(Notification::class.java)
                    notif.id = doc.id

                    // Nettoyer les anciennes notifications déjà lues
                    if (notif.read == true) {
                        supprimerNotificationLue(notif.id)
                        continue
                    }

                    notifications.add(notif)
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun supprimerNotificationsBinome(currentUid: String, binomeUid: String) {
        db.collection("notifications")
            .whereEqualTo("from", binomeUid)
            .whereEqualTo("to", currentUid)
            .whereEqualTo("type", "binome")
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    db.collection("notifications").document(doc.id).delete()
                }
                chargerNotifications()
            }
    }

    private fun supprimerNotificationLue(notificationId: String) {
        db.collection("notifications").document(notificationId).delete()
    }
}