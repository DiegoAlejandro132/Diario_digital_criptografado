package tcc.com.diario_digital_criptografado.util

import com.google.firebase.auth.FirebaseAuth

internal object AuthUtil {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): String? {
        val user = auth.currentUser;
        return if (user !== null)
            user.uid;
        else
            null;
    }

    fun userIsLoggedIn(): Boolean {
        return getCurrentUser() != null
    }
}