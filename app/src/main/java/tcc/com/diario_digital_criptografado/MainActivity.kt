package tcc.com.diario_digital_criptografado;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import tcc.com.diario_digital_criptografado.models.Usuario
import tcc.com.diario_digital_criptografado.util.AuthUtil


class MainActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Direciona para pagina de cadastro
        btn_ir_cadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        //direciona para pagina de redefinição de senha
        btn_ir_redefinir_senha.setOnClickListener {
            val intent = Intent(this, RedefinirSenhaActivity::class.java)
            startActivity(intent)
        }

        //caso o usuario esteja logado, direciona para a sua pagina ao inves de fazer login de novo
//        if(AuthUtil.userIsLoggedIn()) {
//            startActivity(Intent(this, DirecionadorActivity::class.java))
//        }

        //logar usuario com suas credenciais
        //dependendo do tipo de perfil, redireciona para uma tela de uso ideal
        btn_login.setOnClickListener {
            val email = txt_email.text.toString()
            val senha = txt_senha.text.toString()

            if(validateLogin()){
                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        database = FirebaseDatabase.getInstance().getReference("users")
                        database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
                             val tipo_perfil = it.child("tipo_perfil").value

                             when (tipo_perfil){
                                 "Usuário do diário" -> startActivity(Intent(this, AgendaUsuarioActivity::class.java))
                                 "Psicólogo" -> startActivity(Intent(this, ListagemPacientesActivity::class.java))
                                 else -> Toast.makeText(this@MainActivity, "Erro na validação, tente novamnte mais tarde", Toast.LENGTH_LONG).show()
                             }
                        }
                    }else{
                        Toast.makeText(this@MainActivity, "Email ou senha incorreto", Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(this@MainActivity, "Insira o email e senha para poder entrar!", Toast.LENGTH_LONG).show()
            }
        }


    }
    

    private fun validateLogin() : Boolean{
        if((txt_email != null && txt_email.text.toString() != "")
            &&(txt_senha != null && txt_senha.text.toString() != "")){
            return true
        }
        return false
    }
}