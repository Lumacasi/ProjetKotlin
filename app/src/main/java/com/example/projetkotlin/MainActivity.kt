package com.example.projetkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projetkotlin.ui.login.LoginScreen
import com.example.projetkotlin.ui.consultation.ConsultationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {
                // Écran de Login
                composable("login") {
                    LoginScreen(onLoginSuccess = { doctorId ->
                        // On navigue vers les consultations en passant l'ID dans l'URL
                        navController.navigate("consultations/$doctorId")
                    })
                }

                // Écran des Consultations avec paramètre ID
                composable(
                    route = "consultations/{doctorId}",
                    arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: -1
                    ConsultationScreen(doctorId = doctorId)
                }
            }
        }
    }
}