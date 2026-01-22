package org.example.consultation.cap.requests

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

class AddConsultationRequest(
    val doctorId: Int,
    val date: LocalDate,
    val startTime: LocalTime,
    val durationBetween: Int,
    val count: Int,
    val reason: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}