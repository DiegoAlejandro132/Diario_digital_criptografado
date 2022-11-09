package tcc.com.diario_digital_criptografado.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import tcc.com.diario_digital_criptografado.MainActivity

internal object AuthUtil {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): String? {
        val user = auth.currentUser;
        return if (user !== null)
            user.uid;
        else
            null;
    }

    fun usuarioEstaLogado(): Boolean {
        return getCurrentUser() != null
    }


}