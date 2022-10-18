package tcc.com.diario_digital_criptografado

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_redefinir_senha.*


class RedefinirSenhaActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redefinir_senha)

        supportActionBar?.title = "Redefinição de senha"


        btn_redefinir_senha.setOnClickListener {
            if(validateEmail()){
                auth.sendPasswordResetEmail(txt_email_redefinir.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@RedefinirSenhaActivity, "Caso o email possua cadastro, será enviado um email de redefinição de senha", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@RedefinirSenhaActivity, "Falha no processo", Toast.LENGTH_LONG).show()

                    }
                }
            }else{
                Toast.makeText(this@RedefinirSenhaActivity, "Você precisa inserir um email!", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun validateEmail() : Boolean{
        if (txt_email_redefinir != null && txt_email_redefinir.text.toString() != ""){
            return true
        }
        return false
    }
}