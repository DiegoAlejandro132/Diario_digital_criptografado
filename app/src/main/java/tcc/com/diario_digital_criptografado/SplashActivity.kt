package tcc.com.diario_digital_criptografado

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import tcc.com.diario_digital_criptografado.psicologoActivities.ListagemPacientesActivity
import tcc.com.diario_digital_criptografado.usuarioActivities.AgendaUsuarioActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.title = "Seu diário criptografado"


        //contador para mostrar splash antes de iniciar tarefa
        object : CountDownTimer(1500, 1000) {

            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                usuarioEstaLogado()
            }
        }.start()


    }

    private fun usuarioEstaLogado(){
        var database = FirebaseDatabase.getInstance().getReference("users")
        if(AuthUtil.getCurrentUser() != null){
            database.get().addOnSuccessListener {
                if(it.child("${AuthUtil.getCurrentUser()}/tipo_perfil").value.toString() == "Usuário do diário"){
                    val intent = Intent(this, AgendaUsuarioActivity::class.java)
                    startActivity(intent)
                    finish()
                }else if(it.child("${AuthUtil.getCurrentUser()}/tipo_perfil").value.toString() == "Psicólogo"){
                    val intent = Intent(this, ListagemPacientesActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "deu pau", Toast.LENGTH_SHORT).show()
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