package com.example.projetkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projetkotlin.ui.login.LoginScreen
import com.example.projetkotlin.ui.consultation.ConsultationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Création du contrôleur de navigation
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(onLoginSuccess = {
                        navController.navigate("consultations")
                        // En retirant popUpTo, le bouton "retour" système reviendra au login
                    })
                }
                // Suppression de /{doctorName} dans la route
                composable("consultations") {
                    ConsultationScreen()
                }
            }
        }
    }
}