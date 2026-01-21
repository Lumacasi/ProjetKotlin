package org.labo.protocole.traitement.LOGIN

import java.io.Serializable

data class RequeteLOGIN(
    val Login: String,
    val Password: String
) : Serializable {
    companion object {
        private const val serialVersionUID = 0L // On met 0 ici aussi
    }
}