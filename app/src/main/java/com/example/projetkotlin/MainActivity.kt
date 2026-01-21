package com.example.projetkotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.projetkotlin.ui.login.LoginActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirection immédiate vers l'écran de Login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Ferme MainActivity pour ne pas revenir dessus avec le bouton retour
    }
}