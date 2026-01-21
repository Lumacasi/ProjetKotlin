package com.example.projetkotlin.ui.consultation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.projetkotlin.data.network.NetworkManager
import org.example.consultation.dal.entity.Consultation

class ConsultationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                ConsultationScreen()
            }
        }
    }
}

@Composable
fun ConsultationScreen() {
    val scope = rememberCoroutineScope()
    val networkManager = remember { NetworkManager() }

    var consultations by remember { mutableStateOf<List<Consultation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val result = networkManager.getConsultations(null)
        consultations = result
        isLoading = false
    }

    // Ajout de statusBarsPadding() pour éviter la caméra
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // <--- La solution magique
            .padding(16.dp)
    ) {
        Text(
            text = "Mes Consultations",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Blue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(consultations) { item ->
                    ConsultationCard(item)
                }
            }
        }
    }
}

@Composable
fun ConsultationCard(consultation: Consultation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = consultation.patient?.let { "${it.firstName} ${it.lastName}" } ?: "Libre",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "Motif : ${consultation.reason ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Affichage de l'heure et date à droite
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = consultation.hour?.toString() ?: "--:--",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Blue
                )
                Text(
                    text = consultation.date?.toString() ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}