package tcc.com.diario_digital_criptografado;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import tcc.com.diario_digital_criptografado.psicologoActivities.ListagemPacientesActivity
import tcc.com.diario_digital_criptografado.usuarioActivities.AgendaUsuarioActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil


class MainActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Login"


        verifyUserIsLoggedIn()

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


        //logar usuario com suas credenciais
        //dependendo do tipo de perfil, redireciona para uma tela de uso ideal
        btn_login.setOnClickListener {

            try {

                val email = txt_email.text.toString()
                val senha = txt_senha.text.toString()

                if(validateLogin()){
                    auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            if (auth.currentUser!!.isEmailVerified){
                                database = FirebaseDatabase.getInstance().getReference("users")
                                database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
                                    when (it.child("tipo_perfil").value){
                                        "Usuário do diário" -> loginUsuario()
                                        "Psicólogo" -> logInPsicologo()
                                        else -> Toast.makeText(this@MainActivity, "Erro na validação, tente novamnte mais tarde", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }else{
                                Toast.makeText(this, "É necessario confirmar a conta no email antes de realizar o login", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this@MainActivity, "Email ou senha incorreto", Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this@MainActivity, "Insira o email e senha para poder entrar!", Toast.LENGTH_LONG).show()
                }
            }catch (e:Exception){
                Toast.makeText(this@MainActivity, "Erro ao fazer login.", Toast.LENGTH_SHORT).show()
                Log.e("login", e.message.toString())
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

    private fun verifyUserIsLoggedIn(){

        try {
            if(AuthUtil.usuarioEstaLogado()){
                database = FirebaseDatabase.getInstance().getReference("users")
                database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
                    val tipoUsuario = it.child("tipo_perfil").value.toString()
                    if(tipoUsuario == "Psicólogo"){
                        val intent = Intent(this, ListagemPacientesActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else if (tipoUsuario == "Usuário do diário"){
                        val intent = Intent(this, AgendaUsuarioActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this@MainActivity, "Erro na validação do usuário.", Toast.LENGTH_SHORT).show()
            Log.e("verifyUserIsLoggedIn", e.message.toString())
        }

    }

    private fun loginUsuario(){
        startActivity(Intent(this, AgendaUsuarioActivity::class.java))
        finish()
    }

    private fun logInPsicologo(){
        startActivity(Intent(this, ListagemPacientesActivity::class.java))
        finish()
    }
}