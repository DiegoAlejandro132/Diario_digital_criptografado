package tcc.com.diario_digital_criptografado.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ImageUtil {
    companion object{
        fun getProfileImage(): Bitmap? {
            val storageref = FirebaseStorage.getInstance().reference.child("fotos_perfil/${AuthUtil.getCurrentUser()}")

            val localFile = File.createTempFile("tempImage", "")
            storageref.getFile(localFile)
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            return bitmap
        }
    }
}