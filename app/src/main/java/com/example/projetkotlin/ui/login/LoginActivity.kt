package com.example.projetkotlin.ui.login

import android.content.Intent
import android.os.Bundle
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
import org.labo.protocole.traitement.LOGIN.ReponseLOGIN
import org.labo.protocole.traitement.LOGIN.RequeteLOGIN

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

                    println("DEBUG LOGIN: Nom d'utilisateur saisi = '$username'")
                    println("DEBUG LOGIN: Mot de passe saisi = '$password'")

                    isLoading = true
                    statusMessage = "Connexion..."



                    scope.launch {
                        // Appel au NetworkManager avec les objets
                        // Assurez-vous que la méthode dans NetworkManager s'appelle bien sendLoginRequest
                        val reponse = NetworkManager().sendLogin(username, password)

                        isLoading = false

                        if (reponse is ReponseLOGIN) { // Le "is" déclenche le Smart Cast en Kotlin
                            if (reponse.Valide) { // Maintenant .Valide n'est plus en rouge
                                statusMessage = "Connexion réussie !"
                                context.startActivity(Intent(context, ConsultationActivity::class.java))
                            } else {
                                statusMessage = "Identifiants incorrects"
                            }
                        } else {
                            statusMessage = "Erreur de communication avec le serveur"
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
