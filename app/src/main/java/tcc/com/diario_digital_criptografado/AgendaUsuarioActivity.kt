package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_agenda_usuario.*
import tcc.com.diario_digital_criptografado.R

class AgendaUsuarioActivity : AppCompatActivity() {
    var ano : Int = 0
    var mes : Int = 0
    var dia : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_usuario)


        calendarView_user.setOnDateChangeListener {
                calendarView, i, i2, i3 ->

            ano = i
            mes = i2+1
            dia = i3
            val data = "${dia}-${mes}-${ano}"

            Toast.makeText(this@AgendaUsuarioActivity, "agenda : ${dia}/${mes}/${ano}", Toast.LENGTH_LONG).show()

            val intent = Intent(this, FormularioDiarioActivity::class.java)
            intent.putExtra("dataSelecionada", data)
            startActivity(intent)
        }

    }


    
}