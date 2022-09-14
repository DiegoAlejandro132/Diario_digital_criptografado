package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Build
import android.os.Bundle

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_agenda_usuario.*

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AgendaUsuarioActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_usuario)


        calendarView_user.setOnDateChangeListener {
                calendarView, i, i2, i3 ->

            var ano = i
            var mes = i2+1
            var dia = i3
            val data = "${dia}-${mes}-${ano}"

            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val diaAtual = LocalDateTime.now()
            val dataFormatada = diaAtual.format(formatter)
            Toast.makeText(this@AgendaUsuarioActivity, "agenda : ${dataFormatada}", Toast.LENGTH_LONG).show()
            val intent = Intent(this, FormularioDiarioActivity::class.java)
            intent.putExtra("dataSelecionada", data)
            startActivity(intent)
            }
        }

    }

    /*private fun validateDate(data : String, dataAtual : String) : Boolean{
        var dia : Int
        var mes : Int
        var ano : Int
        if (data[2].equals("-")){
            dia = "${data[1]}".toInt()
            if(data[3].equals("-")){
                mes = "${data[2]}".toInt()
                ano = "${data[4]}${data[5]}${data[6]}${data[7]}".toInt()
            }else{
                mes = "${data[2]}${data[3]}".toInt()
                ano = "${data[5]}${data[6]}${data[7]}${data[8]}".toInt()
            }
        }else{
            dia = "${data[0]}${data[1]}".toInt()
            if(data[4].equals("-")){
                mes = "${data[3]}".toInt()
                ano = "${data[5]}${data[6]}${data[7]}${data[8]}".toInt()
            }else{
                mes = "${data[3]}${data[4]}".toInt()
                ano = "${data[6]}${data[7]}${data[8]}${data[9]}".toInt()
            }
        }
        Toast.makeText(this@AgendaUsuarioActivity, "comemorar", Toast.LENGTH_LONG).show()
        val total = dia + (mes * 30)  + (ano * 365)

        val diaAtual = "${dataAtual[0]}${dataAtual[1]}".toInt()
        val mesAtual = "${dataAtual[3]}${dataAtual[4]}".toInt()
        val anoAtual = "${dataAtual[6]}${dataAtual[7]}${dataAtual[8]}${dataAtual[9]}".toInt()
        val totalAtual = diaAtual + (mesAtual * 30) + (anoAtual * 365)

        if(total <= totalAtual){
            return true
        }
        return false
    }*/