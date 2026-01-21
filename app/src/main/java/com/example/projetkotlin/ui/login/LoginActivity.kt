package com.example.projetkotlin.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.projetkotlin.data.network.NetworkManager
import com.example.projetkotlin.ui.consultation.ConsultationActivity
import com.example.projetkotlin.utils.Constants
import kotlinx.coroutines.launch
import org.example.consultation.cap.responses.*
import org.example.consultation.cap.requests.*

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Pour lancer des tâches asynchrones

    var username by remember { mutableStateOf("Medecin1") }
    var password by remember { mutableStateOf("1234") }
    var statusMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color.White // Fond blanc fixe
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Connexion Hôpital (SSL)",
                style = MaterialTheme.typography.headlineMedium,
                color = androidx.compose.ui.graphics.Color.Black // Texte noir fixe
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nom d'utilisateur") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isLoading) return@Button

                    println("DEBUG LOGIN: Saisie = '$username' / '$password'")

                    isLoading = true
                    statusMessage = "Connexion..."

                    val networkManager = NetworkManager()

                    scope.launch {
                        // ICI : On utilise bien 'username' et 'password' que tu as défini plus haut
                        val response = networkManager.sendLogin(username, password)

                        isLoading = false // On arrête le chargement
                        if (response != null) {
                            // On utilise .success car c'est le champ du nouveau serveur
                            if (response.success) {
                                Log.d("LOGIN", "Succès ! Token : ${response.token}")
                                val intent = Intent(context, ConsultationActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                statusMessage = "Identifiants incorrects"
                            }
                        } else {
                            statusMessage = "Erreur de connexion au serveur"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Se connecter")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = statusMessage, color = androidx.compose.ui.graphics.Color.Black)
        }
    }
}
