package tcc.com.diario_digital_criptografado;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.txt_email
import kotlinx.android.synthetic.main.activity_main.txt_senha
import tcc.com.diario_digital_criptografado.psicologoActivities.ListagemPacientesActivity
import tcc.com.diario_digital_criptografado.usuarioActivities.AgendaUsuarioActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil

class MainActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Login"

        usuarioEstaLogado()

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

                if(ConexaoUtil.estaConectado(this)){
                    progressive_login.isVisible = true
                    linear_layout_conteudo_login.visibility = View.GONE

                    val email = txt_email.text.toString()
                    val senha = txt_senha.text.toString()

                    if(validarLogin()){
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
                                    progressive_login.visibility = View.GONE
                                    linear_layout_conteudo_login.isVisible = true
                                    Toast.makeText(this, "É necessario confirmar a conta no e-mail antes de realizar o login", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                try {
                                    throw task.exception!!
                                }catch (e: FirebaseNetworkException){
                                    progressive_login.visibility = View.GONE
                                    linear_layout_conteudo_login.isVisible = true
                                    Log.e("login usuario", e.message.toString())
                                    Snackbar.make(btn_login, "Verifique a conexão com a internet e tente mais tarde", Snackbar.LENGTH_LONG).show()
                                }catch (e: FirebaseAuthInvalidCredentialsException){
                                    progressive_login.visibility = View.GONE
                                    linear_layout_conteudo_login.isVisible = true
                                    Log.e("login usuario", e.message.toString())
                                    Log.e("login usuario", e.toString())
                                    Snackbar.make(btn_login, "E-mail ou senha incorretos", Snackbar.LENGTH_LONG).show()
                                } catch (e:FirebaseAuthInvalidUserException){
                                    progressive_login.visibility = View.GONE
                                    linear_layout_conteudo_login.isVisible = true
                                    Log.e("login usuario", e.message.toString())
                                    Snackbar.make(btn_login, "E-mail ou senha incorretos", Snackbar.LENGTH_LONG).show()
                                } catch (e: FirebaseTooManyRequestsException) {
                                    progressive_login.visibility = View.GONE
                                    linear_layout_conteudo_login.isVisible = true
                                    Log.e("login usuario", e.message.toString())
                                    Toast.makeText(this, "Conta temporariamente bloqueada devido ao número de erros", Toast.LENGTH_SHORT).show()
                                    Toast.makeText(this, "Redefina sua senha ou espere alguns minutos para entrar novamente", Toast.LENGTH_SHORT).show()
                                }catch (e:Exception){
                                    progressive_login.visibility = View.GONE
                                    linear_layout_conteudo_login.isVisible = true
                                    Log.e("login usuario", e.toString())
                                    Snackbar.make(btn_login, "Houve um erro inesperado, por favor tente mais tarde", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    }else{
                        progressive_login.visibility = View.GONE
                        linear_layout_conteudo_login.isVisible = true
                        Snackbar.make(btn_login, "Insira o e-mail e senha para poder entrar!", Snackbar.LENGTH_LONG).show()
                    }
                }else{
                    Snackbar.make(btn_ir_cadastro, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
                }
            }catch (e:Exception){
                Snackbar.make(btn_ir_cadastro, "Erro ao fazer login", Snackbar.LENGTH_LONG).show()
                Log.e("login", e.message.toString())
            }

        }


    }
    

    private fun validarLogin() : Boolean{
        if((txt_email != null && txt_email.text.toString() != "")
            &&(txt_senha != null && txt_senha.text.toString() != "")){
            return true
        }
        return false
    }

    private fun usuarioEstaLogado(){

        try {
            if(AuthUtil.usuarioEstaLogado()){
                if(auth.currentUser!!.isEmailVerified){
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