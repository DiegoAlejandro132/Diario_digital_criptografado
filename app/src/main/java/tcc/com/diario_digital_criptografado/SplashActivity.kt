package tcc.com.diario_digital_criptografado

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import tcc.com.diario_digital_criptografado.psicologoActivities.ListagemPacientesActivity
import tcc.com.diario_digital_criptografado.usuarioActivities.AgendaUsuarioActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil


class SplashActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.title = "Meu diário criptografado"

        val timer = object: CountDownTimer(1700, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                if (ConexaoUtil.estaConectado(this@SplashActivity)){
                    usuarioEstaLogado()
                }else{
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@SplashActivity, "Para poder entrar é necessário estar conectado à internet", Toast.LENGTH_SHORT).show()
                }
            }
        }
        timer.start()

    }

    private fun usuarioEstaLogado() {
        try {
            if(AuthUtil.usuarioEstaLogado()){
                database.get().addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, AuthUtil.getCurrentUser().toString(), Toast.LENGTH_SHORT).show()
                        val snapshot = task.result
                        if(snapshot.child(AuthUtil.getCurrentUser()!!).value != null){
                            if(auth.currentUser!!.isEmailVerified){
                                if(snapshot.child("${AuthUtil.getCurrentUser()}/tipo_perfil").value.toString() == "Usuário do diário"){
                                    val intent = Intent(this, AgendaUsuarioActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }else if(snapshot.child("${AuthUtil.getCurrentUser()}/tipo_perfil").value.toString() == "Psicólogo"){
                                    val intent = Intent(this, ListagemPacientesActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }else{
                                Toast.makeText(this, "Você precisa confirmar o email de verificação", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }else{
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }else{
                        try {
                            throw task.exception!!
                        }catch (e:Exception){
                            Log.e("usuario esta logado", e.message.toString())
                            Toast.makeText(this, "Houve um erro ao iniciar a sessão", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "erro ao verificar usuário", Toast.LENGTH_SHORT).show()
            Log.e("usuario esta logado", e.message.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}