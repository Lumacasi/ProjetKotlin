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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projetkotlin.R
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

    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var consultations by remember { mutableStateOf<List<Consultation>>(emptyList()) }
    var patients by remember { mutableStateOf<List<Patient>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showPatientDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var addDate by remember { mutableStateOf(LocalDate.now()) }
    var addStartTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var addCount by remember { mutableStateOf("4") }
    var addDuration by remember { mutableStateOf("30") }

    var selectedConsultation by remember { mutableStateOf<Consultation?>(null) }
    var editDate by remember { mutableStateOf(LocalDate.now()) }
    var editTime by remember { mutableStateOf(LocalTime.now()) }
    var editReason by remember { mutableStateOf("") }
    var editPatient by remember { mutableStateOf<Patient?>(null) }
    var showEditPatientSelection by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.label_planifier), tint = Color.White)
                        }
                    }

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        IconButton(onClick = {
                            if (selectedConsultation != null) {
                                showEditDialog = true
                            } else {
                                Toast.makeText(context, context.getString(R.string.msg_select_line), Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.label_modifier), tint = Color.White)
                        }
                    }

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        IconButton(onClick = {
                            val consultation = selectedConsultation
                            if (consultation == null) {
                                Toast.makeText(context, context.getString(R.string.msg_select_line), Toast.LENGTH_SHORT).show()
                            }
                            else if (consultation.patient != null) {
                                Toast.makeText(context, context.getString(R.string.error_delete_patient), Toast.LENGTH_LONG).show()
                            }
                            else {
                                showDeleteDialog = true
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.label_supprimer), tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text(stringResource(R.string.title_consultations), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { filterDatePickerDialog.show() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (selectedDate != null) Color(0xFFE3F2FD) else Color(0xFF6200EE), contentColor = if (selectedDate != null) Color.Black else Color.White)) {
                    Text(selectedDate?.toString() ?: stringResource(R.string.btn_date))
                }
                Button(onClick = { showPatientDialog = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (selectedPatient != null) Color(0xFFE3F2FD) else Color(0xFF6200EE), contentColor = if (selectedPatient != null) Color.Black else Color.White)) {
                    Text(selectedPatient?.lastName ?: stringResource(R.string.btn_patient))
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
                                selectedConsultation = item
                                showDetailsDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(onDismissRequest = { showAddDialog = false }, title = { Text(stringResource(R.string.label_planifier)) }, text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { addDatePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) { Text("${stringResource(R.string.btn_date)} : $addDate") }
                    OutlinedButton(onClick = { addTimePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) { Text("${stringResource(R.string.label_heure)} : $addStartTime") }
                    OutlinedTextField(value = addCount, onValueChange = { addCount = it }, label = { Text(stringResource(R.string.label_nombre)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = addDuration, onValueChange = { addDuration = it }, label = { Text(stringResource(R.string.label_duree)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }, confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val resp = networkManager.addConsultations(AddConsultationRequest(doctorId, addDate, addStartTime, addDuration.toIntOrNull() ?: 30, addCount.toIntOrNull() ?: 1))
                        if (resp?.success == true) { showAddDialog = false; refreshData() }
                        else Toast.makeText(context, resp?.message ?: "Erreur", Toast.LENGTH_LONG).show()
                    }
                }) { Text(stringResource(R.string.label_generer)) }
            })
        }

        if (showEditDialog && selectedConsultation != null) {
            AlertDialog(onDismissRequest = { showEditDialog = false }, title = { Text(stringResource(R.string.label_modifier)) }, text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { editDatePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) { Text("${stringResource(R.string.btn_date)} : $editDate") }
                    OutlinedButton(onClick = { editTimePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) { Text("${stringResource(R.string.label_heure)} : $editTime") }
                    OutlinedTextField(value = editReason, onValueChange = { editReason = it }, label = { Text(stringResource(R.string.label_motif)) }, modifier = Modifier.fillMaxWidth())
                    Text("${stringResource(R.string.btn_patient)} : ${editPatient?.let { "${it.firstName} ${it.lastName}" } ?: stringResource(R.string.status_libre)}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showEditPatientSelection = true }) { Text(stringResource(R.string.label_choisir)) }
                        if (editPatient != null) Button(onClick = { editPatient = null }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text(stringResource(R.string.label_liberer)) }
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
                }) { Text(stringResource(R.string.label_enregistrer)) }
            })
        }

        if (showEditPatientSelection) {
            AlertDialog(onDismissRequest = { showEditPatientSelection = false }, title = { Text(stringResource(R.string.label_choisir_patient)) }, text = {
                LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                    items(patients) { p -> TextButton(onClick = { editPatient = p; showEditPatientSelection = false }, modifier = Modifier.fillMaxWidth()) { Text("${p.firstName} ${p.lastName}") } }
                }
            }, confirmButton = {})
        }

        if (showDeleteDialog && selectedConsultation != null) {
            AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text(stringResource(R.string.dialog_delete_title)) }, text = { Text("${stringResource(R.string.dialog_delete_confirm)} ${selectedConsultation?.date} à ${selectedConsultation?.hour} ?") }, confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Red), onClick = {
                    scope.launch {
                        val idToDelete = selectedConsultation?.id
                        if (idToDelete != null) {
                            if (networkManager.deleteConsultation(idToDelete)) { showDeleteDialog = false; selectedConsultation = null; refreshData() }
                        }
                    }
                }) { Text(stringResource(R.string.label_supprimer), color = Color.White) }
            })
        }

        if (showPatientDialog) {
            AlertDialog(onDismissRequest = { showPatientDialog = false }, title = { Text(stringResource(R.string.label_filtrer_patient)) }, text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    item { TextButton(onClick = { selectedPatient = null; showPatientDialog = false }) { Text(stringResource(R.string.btn_all)) } }
                    items(patients) { p -> TextButton(onClick = { selectedPatient = p; showPatientDialog = false }) { Text("${p.firstName} ${p.lastName}") } }
                }
            }, confirmButton = {})
        }

        if (showDetailsDialog && selectedConsultation != null) {
            AlertDialog(
                onDismissRequest = { showDetailsDialog = false },
                title = {
                    Text(
                        stringResource(R.string.dialog_details_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF6200EE)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(stringResource(R.string.btn_patient), style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                            Text(
                                text = selectedConsultation?.patient?.let { "${it.firstName} ${it.lastName}" } ?: stringResource(R.string.status_libre),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        HorizontalDivider(thickness = 0.5.dp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(stringResource(R.string.btn_date), style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                                Text(selectedConsultation?.date?.toString() ?: "N/A")
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(stringResource(R.string.label_heure), style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                                Text(selectedConsultation?.hour?.toString() ?: "N/A")
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp)

                        Column {
                            Text(stringResource(R.string.label_motif_complet), style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = selectedConsultation?.reason ?: stringResource(R.string.label_aucun_motif),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDetailsDialog = false }) {
                        Text(stringResource(R.string.label_fermer))
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
                    text = consultation.patient?.let { "${it.firstName} ${it.lastName}" } ?: stringResource(R.string.status_libre),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (consultation.patient == null) Color(0xFF2E7D32) else Color.Black
                )

                Text(
                    text = "${stringResource(R.string.label_motif)} : ${consultation.reason ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = consultation.hour?.toString() ?: "--:--", color = Color(0xFF6200EE))
                Text(text = consultation.date?.toString() ?: "", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}