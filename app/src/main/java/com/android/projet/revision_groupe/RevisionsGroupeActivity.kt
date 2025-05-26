package com.android.projet.revision_groupe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.projet.R

class RevisionsGroupeActivity : AppCompatActivity() {
    lateinit var btnCreationGroupe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revisions_groupe)

        //Récupération des id
        btnCreationGroupe = findViewById(R.id.button_creation_groupe)

        //Clique sur le bouton Créer un groupe
        btnCreationGroupe.setOnClickListener{
            Intent(this, CreationGroupeActivity::class.java).also {intentToCreationGroupeActivity ->
                startActivity(intentToCreationGroupeActivity)
            }
        }
    }
}