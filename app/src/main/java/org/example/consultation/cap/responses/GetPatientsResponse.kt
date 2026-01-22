package org.example.consultation.cap.responses

import org.example.consultation.dal.entity.Patient
import java.io.Serializable

class GetPatientsResponse(
    val success: Boolean = false,
    val patients: List<Patient> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}