package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_formulario_diario.*
import tcc.com.diario_digital_criptografado.models.DiaFormulario
import tcc.com.diario_digital_criptografado.util.AuthUtil

class FormularioDiarioActivity : AppCompatActivity() {
    private val database = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_diario)


        val intent = intent
        val dataSelecionada = intent.getStringExtra("dataSelecionada")
        txt_data.setText(dataSelecionada)

        btn_salvar_diario.setOnClickListener(){
            val dia = setDayData()
            writeDaydata(dia, dataSelecionada!!)
            val intent = Intent(this,AgendaUsuarioActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setDayData() : DiaFormulario{
        val pergunta1 = txt_pergunta1.text.toString()
        val pergunta2 = txt_pergunta2.text.toString()

        val dia = DiaFormulario()
        dia.pergunta1 = pergunta1
        dia.pergunta2 = pergunta2

        return dia
    }

    private fun writeDaydata(dia : DiaFormulario, data : String){
        val userId = AuthUtil.getCurrentUser()
        val child = "users/${userId}/${data}"
        database.child(child).setValue(dia)
    }

}