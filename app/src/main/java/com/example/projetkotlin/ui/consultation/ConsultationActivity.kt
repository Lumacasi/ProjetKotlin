package com.example.projetkotlin.ui.consultation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projetkotlin.data.network.NetworkManager
import kotlinx.coroutines.launch
import org.example.consultation.cap.requests.AddConsultationRequest
import org.example.consultation.cap.requests.SearchConsultationsRequest
import org.example.consultation.cap.requests.UpdateConsultationRequest
import org.example.consultation.dal.entity.Consultation
import org.example.consultation.dal.entity.Patient
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow

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
    val scope = rememberCoroutineScope()
    val networkManager = remember { NetworkManager() }

    // États pour les filtres et données
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var consultations by remember { mutableStateOf<List<Consultation>>(emptyList()) }
    var patients by remember { mutableStateOf<List<Patient>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // États pour les dialogues
    var showPatientDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // États pour le formulaire d'ajout (Planification)
    var addDate by remember { mutableStateOf(LocalDate.now()) }
    var addStartTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var addCount by remember { mutableStateOf("4") }
    var addDuration by remember { mutableStateOf("30") }

    // États pour la sélection et l'édition
    var selectedConsultation by remember { mutableStateOf<Consultation?>(null) }
    var editDate by remember { mutableStateOf(LocalDate.now()) }
    var editTime by remember { mutableStateOf(LocalTime.now()) }
    var editReason by remember { mutableStateOf("") }
    var editPatient by remember { mutableStateOf<Patient?>(null) }
    var showEditPatientSelection by remember { mutableStateOf(false) }

    // Pickers
    val addDatePickerDialog = DatePickerDialog(context, { _, y, m, d -> addDate = LocalDate.of(y, m + 1, d) }, addDate.year, addDate.monthValue - 1, addDate.dayOfMonth)
    val addTimePickerDialog = TimePickerDialog(context, { _, h, min -> addStartTime = LocalTime.of(h, min) }, addStartTime.hour, addStartTime.minute, true)
    val editDatePickerDialog = DatePickerDialog(context, { _, y, m, d -> editDate = LocalDate.of(y, m + 1, d) }, editDate.year, editDate.monthValue - 1, editDate.dayOfMonth)
    val editTimePickerDialog = TimePickerDialog(context, { _, h, min -> editTime = LocalTime.of(h, min) }, editTime.hour, editTime.minute, true)
    val filterDatePickerDialog = DatePickerDialog(context, { _, y, m, d -> selectedDate = LocalDate.of(y, m + 1, d) }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    var showDetailsDialog by remember { mutableStateOf(false) }

    val refreshData = {
        scope.launch {
            isLoading = true
            patients = networkManager.getAllPatients()
            val request = SearchConsultationsRequest(doctorId, selectedPatient?.id, selectedDate)
            consultations = networkManager.getConsultations(request)
            isLoading = false
        }
    }

    LaunchedEffect(selectedPatient, selectedDate) { refreshData() }

    LaunchedEffect(showEditDialog) {
        if (showEditDialog && selectedConsultation != null) {
            editDate = selectedConsultation!!.date ?: LocalDate.now()
            editTime = selectedConsultation!!.hour ?: LocalTime.now()
            editReason = selectedConsultation!!.reason ?: ""
            editPatient = selectedConsultation!!.patient
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White
            ) {
                // Utilisation d'une Row pour répartir l'espace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // BOUTON AJOUTER (1/3 de la largeur)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Planifier", tint = Color.White)
                        }
                    }

                    // BOUTON MODIFIER (1/3 de la largeur)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        IconButton(onClick = {
                            if (selectedConsultation != null) {
                                showEditDialog = true
                            } else {
                                Toast.makeText(context, "Sélectionnez une ligne", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = Color.White)
                        }
                    }

                    // BOUTON SUPPRIMER (1/3 de la largeur)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        IconButton(onClick = {
                            val consultation = selectedConsultation
                            if (consultation == null) {
                                Toast.makeText(context, "Sélectionnez une ligne", Toast.LENGTH_SHORT).show()
                            }
                            // CONDITION DE BLOCAGE : Si un patient est assigné
                            else if (consultation.patient != null) {
                                Toast.makeText(context, "Impossible de supprimer : un patient est déjà assigné", Toast.LENGTH_LONG).show()
                            }
                            else {
                                showDeleteDialog = true
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("Mes Consultations", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { filterDatePickerDialog.show() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (selectedDate != null) Color(0xFFE3F2FD) else Color(0xFF6200EE), contentColor = if (selectedDate != null) Color.Black else Color.White)) {
                    Text(selectedDate?.toString() ?: "Date")
                }
                Button(onClick = { showPatientDialog = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (selectedPatient != null) Color(0xFFE3F2FD) else Color(0xFF6200EE), contentColor = if (selectedPatient != null) Color.Black else Color.White)) {
                    Text(selectedPatient?.lastName ?: "Patient")
                }
                if (selectedDate != null || selectedPatient != null) {
                    IconButton(onClick = { selectedDate = null; selectedPatient = null }) { Icon(Icons.Default.Clear, "Reset", tint = Color.Red) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF6200EE)) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(consultations) { item ->
                        ConsultationCard(
                            consultation = item,
                            isSelected = selectedConsultation?.id == item.id,
                            onSelect = { selectedConsultation = if (selectedConsultation?.id == item.id) null else item },
                            onLongClick = {
                                selectedConsultation = item // On le sélectionne aussi par confort
                                showDetailsDialog = true
                            }
                        )
                    }
                }
            }
        }

        // --- DIALOGUES ---

        if (showAddDialog) {
            AlertDialog(onDismissRequest = { showAddDialog = false }, title = { Text("Planifier") }, text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { addDatePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) { Text("Date : $addDate") }
                    OutlinedButton(onClick = { addTimePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) { Text("Heure : $addStartTime") }
                    OutlinedTextField(value = addCount, onValueChange = { addCount = it }, label = { Text("Nombre") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = addDuration, onValueChange = { addDuration = it }, label = { Text("Durée (min)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }, confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val resp = networkManager.addConsultations(AddConsultationRequest(doctorId, addDate, addStartTime, addDuration.toIntOrNull() ?: 30, addCount.toIntOrNull() ?: 1))
                        if (resp?.success == true) { showAddDialog = false; refreshData() }
                        else Toast.makeText(context, resp?.message ?: "Erreur", Toast.LENGTH_LONG).show()
                    }
                }) { Text("Générer") }
            })
        }

        if (showEditDialog && selectedConsultation != null) {
            AlertDialog(onDismissRequest = { showEditDialog = false }, title = { Text("Modifier") }, text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { editDatePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) { Text("Date : $editDate") }
                    OutlinedButton(onClick = { editTimePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) { Text("Heure : $editTime") }
                    OutlinedTextField(value = editReason, onValueChange = { editReason = it }, label = { Text("Motif") }, modifier = Modifier.fillMaxWidth())
                    Text("Patient : ${editPatient?.let { "${it.firstName} ${it.lastName}" } ?: "LIBRE"}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showEditPatientSelection = true }) { Text("Choisir") }
                        if (editPatient != null) Button(onClick = { editPatient = null }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("Libérer") }
                    }
                }
            }, confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val idToUpdate = selectedConsultation?.id
                        if (idToUpdate != null) {
                            val req = UpdateConsultationRequest(idToUpdate, editDate, editTime, editReason, editPatient?.id)
                            if (networkManager.updateConsultation(req)) { showEditDialog = false; selectedConsultation = null; refreshData() }
                        }
                    }
                }) { Text("Enregistrer") }
            })
        }

        if (showEditPatientSelection) {
            AlertDialog(onDismissRequest = { showEditPatientSelection = false }, title = { Text("Choisir Patient") }, text = {
                LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                    items(patients) { p -> TextButton(onClick = { editPatient = p; showEditPatientSelection = false }, modifier = Modifier.fillMaxWidth()) { Text("${p.firstName} ${p.lastName}") } }
                }
            }, confirmButton = {})
        }

        if (showDeleteDialog && selectedConsultation != null) {
            AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("Supprimer ?") }, text = { Text("Supprimer le créneau du ${selectedConsultation?.date} à ${selectedConsultation?.hour} ?") }, confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Red), onClick = {
                    scope.launch {
                        val idToDelete = selectedConsultation?.id
                        if (idToDelete != null) {
                            if (networkManager.deleteConsultation(idToDelete)) { showDeleteDialog = false; selectedConsultation = null; refreshData() }
                        }
                    }
                }) { Text("Supprimer", color = Color.White) }
            })
        }

        if (showPatientDialog) {
            AlertDialog(onDismissRequest = { showPatientDialog = false }, title = { Text("Filtrer Patient") }, text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    item { TextButton(onClick = { selectedPatient = null; showPatientDialog = false }) { Text("Tous") } }
                    items(patients) { p -> TextButton(onClick = { selectedPatient = p; showPatientDialog = false }) { Text("${p.firstName} ${p.lastName}") } }
                }
            }, confirmButton = {})
        }

        if (showDetailsDialog && selectedConsultation != null) {
            AlertDialog(
                onDismissRequest = { showDetailsDialog = false },
                title = {
                    Text(
                        "Détails de la consultation",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF6200EE)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f) // Prend 60% de la hauteur de l'écran
                            .verticalScroll(rememberScrollState()), // Permet de scroller si le motif est géant
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Info Patient
                        Column {
                            Text("Patient", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                            Text(
                                text = selectedConsultation?.patient?.let { "${it.firstName} ${it.lastName}" } ?: "LIBRE",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        HorizontalDivider(thickness = 0.5.dp)

                        // Date et Heure
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Date", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                                Text(selectedConsultation?.date?.toString() ?: "N/A")
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Heure", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                                Text(selectedConsultation?.hour?.toString() ?: "N/A")
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp)

                        // Motif (Le but principal)
                        Column {
                            Text("Motif complet", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = selectedConsultation?.reason ?: "Aucun motif précisé.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDetailsDialog = false }) {
                        Text("Fermer")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConsultationCard(
    consultation: Consultation,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onSelect() },
                onLongClick = { onLongClick() }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF8F9FA)
        ),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF6200EE)) else null,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = consultation.patient?.let { "${it.firstName} ${it.lastName}" } ?: "LIBRE",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (consultation.patient == null) Color(0xFF2E7D32) else Color.Black
                )

                // --- MODIFICATION ICI ---
                Text(
                    text = "Motif : ${consultation.reason ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1, // Limite à une seule ligne
                    overflow = TextOverflow.Ellipsis // Ajoute les "..." si le texte dépasse
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = consultation.hour?.toString() ?: "--:--", color = Color(0xFF6200EE))
                Text(text = consultation.date?.toString() ?: "", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}