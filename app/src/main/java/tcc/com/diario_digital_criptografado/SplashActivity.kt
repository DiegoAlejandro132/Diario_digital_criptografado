package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_cadastro.*
import tcc.com.diario_digital_criptografado.psicologoActivities.ListagemPacientesActivity
import tcc.com.diario_digital_criptografado.usuarioActivities.AgendaUsuarioActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil

@RequiresApi(Build.VERSION_CODES.M)
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.title = "Seu diário criptografado"


        //contador para mostrar splash antes de iniciar tarefa
        object : CountDownTimer(1500, 1000) {

            override fun onTick(millisUntilFinished: Long) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
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
        }.start()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun usuarioEstaLogado(){
        val database = FirebaseDatabase.getInstance().getReference("users")
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            if(auth.currentUser!!.isEmailVerified){
                database.get().addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        val snapshot = task.result
                        if(snapshot.child("${AuthUtil.getCurrentUser()}/tipo_perfil").value.toString() == "Usuário do diário"){
                            val intent = Intent(this, AgendaUsuarioActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else if(snapshot.child("${AuthUtil.getCurrentUser()}/tipo_perfil").value.toString() == "Psicólogo"){
                            val intent = Intent(this, ListagemPacientesActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }else{
                        try {
                            throw task.exception!!
                        }catch (e:Exception){
                            Log.e("usuario esta logado", e.message.toString())
                            Toast.makeText(this, "Houve um erro ao iniciar a sessão", Toast.LENGTH_SHORT).show()
                        }
                    }
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
    }
}