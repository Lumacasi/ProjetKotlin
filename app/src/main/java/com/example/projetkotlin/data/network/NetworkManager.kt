package com.example.projetkotlin.data.network

import com.example.projetkotlin.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.labo.protocole.traitement.LOGIN.ReponseLOGIN
import org.labo.protocole.traitement.LOGIN.RequeteLOGIN
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class NetworkManager {
    // Cette fonction doit s'appeler exactement sendLogin
    suspend fun sendLogin(login: String, pass: String): ReponseLOGIN? {
        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)
                // Timeout de 5 secondes pour éviter que l'UI ne reste figée en cas de souci réseau
                socket.soTimeout = 5000

                // 1. CRÉER LE FLUX DE SORTIE EN PREMIER
                val oos = ObjectOutputStream(socket.getOutputStream())
                // 2. FLUSH IMMÉDIAT : Envoie le header pour débloquer le 'new ObjectInputStream' du serveur
                oos.flush()

                // 3. CRÉER LE FLUX D'ENTRÉE APRÈS
                val ois = ObjectInputStream(socket.getInputStream())

                // 4. ENVOI DE LA REQUÊTE
                val requete = RequeteLOGIN(login, pass)
                oos.writeObject(requete)
                oos.flush()

                // 5. RÉCEPTION DE LA RÉPONSE
                return@withContext ois.readObject() as? ReponseLOGIN
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext ReponseLOGIN(false, "Erreur détaillée: ${e.javaClass.simpleName} - ${e.message}", null)
            } finally {
                socket?.close()
            }
        }
    }
}