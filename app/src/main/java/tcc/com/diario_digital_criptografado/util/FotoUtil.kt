package tcc.com.diario_digital_criptografado.util

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

internal object FotoUtil {

    fun definirFotoPerfil(){
        val storageReference = FirebaseStorage.getInstance().getReference("fotos_perfil/${AuthUtil.getCurrentUser()}")
        storageReference.downloadUrl.addOnSuccessListener {
            if (it != null){
                val fotoUri = it.toString()
                val database = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!)
                database.child("foto_perfil").setValue(fotoUri)
            }
        }
    }
}