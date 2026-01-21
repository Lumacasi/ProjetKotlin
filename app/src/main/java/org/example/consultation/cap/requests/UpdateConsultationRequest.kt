package org.example.consultation.cap.requests

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

class UpdateConsultationRequest(
    val consultationId: Int,
    val date: LocalDate,
    val time: LocalTime,
    val reason: String?,
    val patientId: Int? // null si on veut libérer la place
) : Serializable {
    companion object { private const val serialVersionUID = 1L }
}