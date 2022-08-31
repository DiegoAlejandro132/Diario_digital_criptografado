package tcc.com.diario_digital_criptografado.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

internal object AuthUtil {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usuarioLogado = database.reference.child("users").child(getCurrentUser()!!).get()

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