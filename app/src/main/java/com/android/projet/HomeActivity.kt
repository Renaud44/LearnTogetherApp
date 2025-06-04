package com.android.projet

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.android.projet.chats.ConversationsActivity
import com.android.projet.defis.NouvelDefiInteractifActivity
import com.android.projet.match_etude.MatchEtudeActivity
import com.android.projet.notifications.NotificationsActivity
import com.android.projet.revision_groupe.RevisionsGroupeActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var btnMatchEtude: Button
    private lateinit var btnRevisionGroupe: Button
    private lateinit var btnQuiz: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser

        btnMatchEtude = findViewById(R.id.button_matchEtude)
        btnRevisionGroupe = findViewById(R.id.button_revisionGroupe)
        btnQuiz = findViewById(R.id.button_quiz)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val headerView = navigationView.getHeaderView(0)

        val profilImage = headerView.findViewById<ShapeableImageView>(R.id.profilImage)
        val pseudoTextView = headerView.findViewById<TextView>(R.id.pseudoTextView)

        val imageFile = File(filesDir, "profile_image.jpg")
        if (imageFile.exists()) {
            Glide.with(this)
                .load(imageFile)
                .placeholder(R.drawable.ic_profil)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profilImage)
        }

        currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val pseudo = document.getString("pseudo") ?: "Utilisateur"
                    pseudoTextView.text = pseudo
                }
        }

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {}
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_match_etude -> startActivity(Intent(this, MatchEtudeActivity::class.java))
                R.id.nav_revision_groupe -> startActivity(Intent(this, RevisionsGroupeActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, NotificationsActivity::class.java))
                R.id.nav_messages -> startActivity(Intent(this, ConversationsActivity::class.java))
                R.id.nav_quiz -> startActivity(Intent(this, NouvelDefiInteractifActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        btnMatchEtude.setOnClickListener {
            startActivity(Intent(this, MatchEtudeActivity::class.java))
        }

        btnRevisionGroupe.setOnClickListener {
            startActivity(Intent(this, RevisionsGroupeActivity::class.java))
        }

        btnQuiz.setOnClickListener {
            startActivity(Intent(this, NouvelDefiInteractifActivity::class.java))
        }

        // Lancer badge check
        checkForNotificationBadge()
    }

    private fun updateMenuBadge(menuItemId: Int, showBadge: Boolean) {
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val menu = navigationView.menu
        val item = menu.findItem(menuItemId)

        if (item.actionView == null) {
            item.setActionView(R.layout.menu_item_with_badge)
        }

        val badge = item.actionView?.findViewById<TextView>(R.id.menu_badge)
        badge?.visibility = if (showBadge) View.VISIBLE else View.GONE
    }

    private fun checkForNotificationBadge() {
        val uid = currentUser?.uid ?: return

        db.collection("notifications")
            .whereEqualTo("to", uid)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { result ->
                updateMenuBadge(R.id.nav_notifications, result.documents.isNotEmpty())
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true

        if (item.itemId == R.id.itemSettings) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}