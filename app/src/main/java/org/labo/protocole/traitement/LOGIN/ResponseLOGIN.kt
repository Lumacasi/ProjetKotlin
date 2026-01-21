package org.labo.protocole.traitement.LOGIN

import java.io.Serializable

data class ReponseLOGIN(
    val Valide: Boolean,
    val Token: String?,
    val id: Int?
) : Serializable {
    companion object {
        private const val serialVersionUID = 0L // On met 0 pour coller au serveur
    }
}