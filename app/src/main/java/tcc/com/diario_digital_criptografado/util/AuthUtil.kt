package tcc.com.diario_digital_criptografado.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import tcc.com.diario_digital_criptografado.models.Usuario

internal object AuthUtil {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    public val usuarioLogado: Usuario? = null;

    private fun retrieveUserData() : Usuario {
        if(usuarioLogado == null) {
            usuarioLogado = database.reference.child("users").child(getCurrentUser()!!).get(Usuario.class);
        }

        return usuarioLogado;
    }

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