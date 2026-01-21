package org.example.consultation.cap.requests

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

class AddConsultationRequest(
    val doctorId: Int,
    val date: LocalDate,
    val startTime: LocalTime,
    val durationMinutes: Int,
    val count: Int
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L // Doit être identique au serveur
    }
}