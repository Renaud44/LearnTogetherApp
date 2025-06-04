package com.android.projet.match_etude

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
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

class TrouverBinomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BinomeAdapter
    private val binomeList = mutableListOf<Binome>()
    private val db = FirebaseFirestore.getInstance()
    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trouver_binome)

        recyclerView = findViewById(R.id.recyclerBinomes)
        progressBar = findViewById(R.id.progressBar)
        emptyText = findViewById(R.id.emptyText)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = BinomeAdapter(
            binomeList,
            onAccept = { binome ->
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return@BinomeAdapter

                // 1. Ajouter l'UID dans les demandes reçues
                db.collection("users").document(binome.uuid)
                    .update("demandesRecues", FieldValue.arrayUnion(currentUid))

                // 2. Ajouter dans demandes envoyées
                db.collection("users").document(currentUid)
                    .update("demandeEnvoyeeA", FieldValue.arrayUnion(binome.uuid))

                // 3. Envoyer une notification Firestore de type "binome"
                val notification = Notification(
                    from = currentUid,
                    to = binome.uuid,
                    type = "binome",
                    read = false
                )

                db.collection("notifications").add(notification)
                    .addOnSuccessListener {
                        Toast.makeText(this, "${binome.pseudo} a été notifié", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erreur d'envoi de notification", Toast.LENGTH_SHORT).show()
                    }
            },
            onRefuse = { binome ->
                Toast.makeText(this, "${binome.pseudo} refusé(e)", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter = adapter

        val matieresDifficiles = intent.getStringExtra("matieres_difficiles") ?: ""
        if (matieresDifficiles.isNotEmpty()) {
            val difficulteList = matieresDifficiles.split(",").map { it.trim().lowercase() }
            chargerBinomes(difficulteList)
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Aucune matière difficile transmise", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chargerBinomes(matieresDifficiles: List<String>) {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyText.visibility = View.GONE

        db.collection("users").get()
            .addOnSuccessListener { documents ->
                binomeList.clear()
                for (doc in documents) {
                    if (doc.id == currentUserUid) continue
                    val binome = doc.toObject(Binome::class.java)
                    binome.uuid = doc.id

                    val matieresMaitrisees = binome.matieresMaitrisees
                        ?.split(",")
                        ?.map { it.trim().lowercase() } ?: emptyList()

                    if (matieresDifficiles.any { it in matieresMaitrisees }) {
                        binomeList.add(binome)
                    }
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.visibility = View.GONE
                    if (binomeList.isEmpty()) {
                        emptyText.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                }, 1000)
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Erreur chargement binômes", Toast.LENGTH_SHORT).show()
            }
    }
}