package com.android.projet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private var keepSplash = true
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash }

        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Retarde l'exécution pour simuler le temps de splash (facultatif)
        window.decorView.postDelayed({
            keepSplash = false

            val currentUser = auth.currentUser
            if (currentUser != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                startActivity(Intent(this, ConnexionActivity::class.java))
            }
            finish() // important pour ne pas revenir à cette activité
        }, 500)
    }
}
