package org.example.consultation.cap.requests

import java.io.Serializable
import java.time.LocalDate

class SearchConsultationsRequest(
    val doctorId: Int? = null,
    val patientId: Int? = null,
    val date: LocalDate? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}