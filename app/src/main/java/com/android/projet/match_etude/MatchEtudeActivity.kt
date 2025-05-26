package com.android.projet.match_etude

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.projet.R
import com.google.android.material.textfield.TextInputLayout

class MatchEtudeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_etude)

        val btnTrouverBinome = findViewById<Button>(R.id.btn_trouver_binome)
        val inputMatieresDifficiles = findViewById<TextInputLayout>(R.id.matières_difficiles)

        btnTrouverBinome.setOnClickListener {
            val matieresDifficiles = inputMatieresDifficiles.editText?.text.toString().trim()

            if (matieresDifficiles.isNotEmpty()) {
                val intent = Intent(this, TrouverBinomeActivity::class.java)
                intent.putExtra("matieres_difficiles", matieresDifficiles)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Veuillez remplir les matières difficiles", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
