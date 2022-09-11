package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_formulario_diario.*
import tcc.com.diario_digital_criptografado.models.DiaFormulario
import tcc.com.diario_digital_criptografado.util.AuthUtil

class FormularioDiarioActivity : AppCompatActivity() {
    private val database = Firebase.database.reference
    private lateinit var dbRef : DatabaseReference
    private var avaliacao_dia : String = ""
    private lateinit var pergunta1 : Any
    private lateinit var pergunta2 : Any
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_diario)
        verifyUser()

        val intent = intent
        val dataSelecionada = intent.getStringExtra("dataSelecionada")
        txt_data.setText("Dia: " + dataSelecionada)
        retrieveDayData(dataSelecionada!!)
        onRadioButtonClicked(checkboxGroup)

        btn_salvar_diario.setOnClickListener(){
            val dia = setDayData()
            writeDaydata(dia, dataSelecionada!!)
            val intent = Intent(this,AgendaUsuarioActivity::class.java)
            startActivity(intent)
        }
    }

    //salva os dados inseridos no formulario do dia
    private fun setDayData() : DiaFormulario{
        val pergunta1 = txt_pergunta1.text.toString()
        val pergunta2 = txt_diario.text.toString()

        val dia = DiaFormulario()
        dia.pergunta1 = pergunta1
        dia.pergunta2 = pergunta2
        dia.avaliacaoDia = avaliacao_dia

        return dia
    }
    private fun writeDaydata(dia : DiaFormulario, data : String){
        val userId = AuthUtil.getCurrentUser()
        val child = "users/${userId}/${data}"
        database.child(child).setValue(dia)
    }

    //função para uso do radio buttom
    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked


            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_pessimo ->
                    if (checked) {
                        avaliacao_dia = view.text.toString()
                    }
                R.id.radio_ruim ->
                    if (checked) {
                        avaliacao_dia = view.text.toString()
                    }
                R.id.radio_regular ->
                    if (checked) {
                        avaliacao_dia = view.text.toString()
                    }
                R.id.radio_bom ->
                    if (checked) {
                        avaliacao_dia = view.text.toString()
                    }
                R.id.radio_excelente ->
                    if (checked) {
                        avaliacao_dia = view.text.toString()
                    }
            }
        }
    }
    //traz os dados do dia e os passa para os campos no layout
    private fun retrieveDayData(dataSelecionada : String){
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(AuthUtil.getCurrentUser()!!).child(dataSelecionada).get().addOnSuccessListener {
            if(it.value != null){
                pergunta1 = it.child("pergunta1").value!!
                pergunta2 = it.child("pergunta2").value!!
                avaliacao_dia = it.child("avaliacaoDia").value.toString()

                txt_pergunta1.setText(pergunta1.toString())
                txt_diario.setText(pergunta2.toString())
                when(avaliacao_dia){
                    "Péssimo" -> radio_pessimo.setChecked(true)
                    "Ruim" -> radio_ruim.setChecked(true)
                    "Regular" -> radio_regular.setChecked(true)
                    "Bom" -> radio_bom.setChecked(true)
                    "Excelente" -> radio_excelente.setChecked(true)
                }
            }
        }
    }

    //caso quem acesse seja um psicologo, algumas informações do diario não poderão ser vistas por ele
    private fun verifyUser(){
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
            val tipo_usuario = it.child("tipo_perfil").value.toString()
            if(tipo_usuario == "Psicólogo"){
                txt_diario.setVisibility(View.INVISIBLE);
            }
        }
    }

}