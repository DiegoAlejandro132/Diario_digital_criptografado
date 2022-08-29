package tcc.com.diario_digital_criptografado;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import tcc.com.diario_digital_criptografado.CadastroActivity
import tcc.com.diario_digital_criptografado.DirecionadorActivity
import tcc.com.diariodigitalcriptografado.util.AuthUtil

class MainActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Direciona para pagina de cadastro
        btn_ir_cadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        //caso o usuario esteja logado, direciona para a sua pagina ao inves de fazer login de novo
//        if(AuthUtil.userIsLoggedIn()) {
//            startActivity(Intent(this, DirecionadorActivity::class.java))
//        }

        //logar usuario com suas credenciais
        btn_login.setOnClickListener {
            val email = txt_email.text.toString()
            val senha = txt_senha.text.toString()

            auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, DirecionadorActivity::class.java))
                }else{
                    Toast.makeText(this@MainActivity, "Emai ou senha incorreto", Toast.LENGTH_LONG).show()
                }
            }
        }


    }
}