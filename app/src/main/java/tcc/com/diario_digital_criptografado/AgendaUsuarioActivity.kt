package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_agenda_usuario.*
import kotlinx.android.synthetic.main.activity_formulario_diario.*
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.Validation

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AgendaUsuarioActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var emailUsuarioSelecionado : String
    private lateinit var tipoUsuario : String
    override fun onCreate(savedInstanceState: Bundle?) {
        verifyUser()
        Log.d("msg", "1")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_usuario)


        calendarView_user.setOnDateChangeListener {
                calendarView, i, i2, i3 ->

            var ano = i
            var mes = i2+1
            var dia = i3
            val data = "${dia}-${mes}-${ano}"

            var formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            val diaAtual = LocalDateTime.now()
            val dataFormatada = diaAtual.format(formatter)

            val intentEmail = intent
            emailUsuarioSelecionado = if(tipoUsuario == "Psicólogo"){
                intentEmail.getStringExtra("email").toString()
            }else ""

            if(Validation.validateDateCalendar(data, dataFormatada)){
                val intent = Intent(this, FormularioDiarioActivity::class.java)
                intent.putExtra("dataSelecionada", data)
                if(tipoUsuario == "Psicólogo")
                    intent.putExtra("emailUsuarioSelecionado", emailUsuarioSelecionado)
                startActivity(intent)
            }else{
                Toast.makeText(this@AgendaUsuarioActivity, "Data futura não é permitida", Toast.LENGTH_SHORT).show()
            }
            }
        }

    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->

    private fun verifyUser(){
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
            tipoUsuario = it.child("tipo_perfil").value.toString()
            if(tipoUsuario == "Psicólogo"){
                val intent = intent
                emailUsuarioSelecionado = intent.getStringExtra("email").toString()
            }else if (tipoUsuario == "Usuário do diário"){
                emailUsuarioSelecionado = ""
            }
        }
    }
}