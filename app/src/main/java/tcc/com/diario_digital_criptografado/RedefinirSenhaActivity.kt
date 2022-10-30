package tcc.com.diario_digital_criptografado

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_adicionar_paciente.*
import kotlinx.android.synthetic.main.activity_redefinir_senha.*
import tcc.com.diario_digital_criptografado.util.ConexaoUtil

@RequiresApi(Build.VERSION_CODES.M)
class RedefinirSenhaActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redefinir_senha)

        supportActionBar?.title = "Redefinição de senha"


        btn_redefinir_senha.setOnClickListener {
            if (ConexaoUtil.estaConectado(this)){
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
            }else{
                Snackbar.make(btn_redefinir_senha, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
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