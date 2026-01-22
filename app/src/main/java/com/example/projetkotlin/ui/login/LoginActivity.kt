package com.example.projetkotlin.ui.login

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.projetkotlin.R
import com.example.projetkotlin.data.network.NetworkManager
import kotlinx.coroutines.launch
import com.example.projetkotlin.ui.consultation.ConsultationActivity

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkManager = remember { NetworkManager() }

    var username by remember { mutableStateOf("Alice") }
    var password by remember { mutableStateOf("Dupont") }
    var statusMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.title_login),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.label_username)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.label_password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        statusMessage = context.getString(R.string.login_error)
                        return@Button
                    }

                    isLoading = true
                    statusMessage = context.getString(R.string.status_connecting)

                    scope.launch {
                        try {
                            val response = networkManager.sendLogin(username, password)

                            isLoading = false
                            if (response != null && response.success) {
                                val intent = Intent(context, ConsultationActivity::class.java).apply {
                                    val id = response.doctor?.id ?: -1
                                    putExtra("DOCTOR_ID", id)
                                }
                                context.startActivity(intent)
                            } else {
                                statusMessage = context.getString(R.string.login_error)
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            statusMessage = "Erreur réseau : ${e.localizedMessage}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.btn_login))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusMessage,
                color = if (statusMessage.contains("réussie") || statusMessage.contains("successful"))
                    Color(0xFF4CAF50) else Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}