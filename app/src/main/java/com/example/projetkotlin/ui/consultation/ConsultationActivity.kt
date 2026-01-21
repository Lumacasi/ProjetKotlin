package com.example.projetkotlin.ui.consultation

import android.app.DatePickerDialog
import android.graphics.drawable.Icon
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.projetkotlin.data.network.NetworkManager
import org.example.consultation.dal.entity.Consultation
import org.example.consultation.dal.entity.Patient
import org.example.consultation.cap.requests.SearchConsultationsRequest
import java.time.LocalDate
import java.util.*

class ConsultationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val doctorId = intent.getIntExtra("DOCTOR_ID", -1)
        setContent {
            ConsultationScreen(doctorId = doctorId)
        }
    }
}

@Composable
fun ConsultationScreen(doctorId: Int) {
    val context = LocalContext.current
    val networkManager = remember { NetworkManager() }

    // États pour les filtres
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // États pour les données
    var consultations by remember { mutableStateOf<List<Consultation>>(emptyList()) }
    var patients by remember { mutableStateOf<List<Patient>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showPatientDialog by remember { mutableStateOf(false) }

    // DatePickerDialog setup
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Chargement des consultations (se déclenche quand un filtre change)
    LaunchedEffect(selectedPatient, selectedDate) {
        isLoading = true
        patients = networkManager.getAllPatients()
        val request = SearchConsultationsRequest(
            doctorId = doctorId,
            patientId = selectedPatient?.id,
            date = selectedDate
        )
        consultations = networkManager.getConsultations(request)
        isLoading = false
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Text(
                text = "Mes Consultations",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // BARRE DE FILTRES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filtre Date
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDate != null) Color(0xFFF5F5F5) else Color(0xFF6200EE),
                        contentColor = if (selectedDate != null) Color.Black else Color.White,
                    )
                ) {
                    Text(text = selectedDate?.toString() ?: "Date", maxLines = 1)
                }

                // Filtre Patient
                Button(
                    onClick = { showPatientDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPatient != null) Color(0xFFF5F5F5) else Color(
                            0xFF6200EE
                        ),
                        contentColor = if (selectedDate != null) Color.Black else Color.White,
                    )
                ) {
                    Text(text = selectedPatient?.lastName ?: "Patient", maxLines = 1)
                }

                // Reset
                if (selectedDate != null || selectedPatient != null) {
                    IconButton(onClick = {
                        selectedDate = null
                        selectedPatient = null
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Reset", tint = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Blue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(consultations) { item ->
                        ConsultationCard(item)
                    }
                }
            }
        }

        // Dialogue de sélection du patient
        if (showPatientDialog) {
            AlertDialog(
                onDismissRequest = { showPatientDialog = false },
                title = { Text("Sélectionner un patient") },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        item {
                            TextButton(onClick = {
                                selectedPatient = null; showPatientDialog = false
                            }) {
                                Text("Tous les patients")
                            }
                        }
                        items(patients) { patient ->
                            TextButton(
                                onClick = { selectedPatient = patient; showPatientDialog = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "${patient.firstName} ${patient.lastName}",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}

@Composable
fun ConsultationCard(consultation: Consultation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = consultation.patient?.let { "${it.firstName} ${it.lastName}" } ?: "Libre",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (consultation.patient == null) Color(0xFF2E7D32) else Color.Black
                )
                Text(
                    text = "Motif : ${consultation.reason ?: "Non spécifié"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

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