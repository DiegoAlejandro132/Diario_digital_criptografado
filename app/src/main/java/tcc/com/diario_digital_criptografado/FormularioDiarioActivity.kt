package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_formulario_diario.*
import tcc.com.diario_digital_criptografado.models.DiaFormulario
import tcc.com.diario_digital_criptografado.util.AuthUtil

class FormularioDiarioActivity : AppCompatActivity() {
    private val database = Firebase.database.reference
    private lateinit var dbRef : DatabaseReference

    private lateinit var avaliacao_dia : String
    private lateinit var dataSelecionada : String
    private lateinit var emailUsuarioSelecionado : String

    override fun onCreate(savedInstanceState: Bundle?) {
        verifyUser()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_diario)

        dataSelecionada = intent.getStringExtra("dataSelecionada").toString()

        txt_data.setText("Dia: ${dataSelecionada}")
        retrieveDayData(dataSelecionada)
        onRadioButtonClicked(checkboxGroup)

        btn_salvar_diario.setOnClickListener(){
            val dia = setDayData()
            writeDaydata(dia, dataSelecionada!!)
            val intent = Intent(this,AgendaUsuarioActivity::class.java)
            startActivity(intent)
        }

        btn_voltar_formulario_diario.setOnClickListener(){
            finish()
        }
    }

    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->

    //salva os dados inseridos no formulario do dia
    private fun setDayData() : DiaFormulario{
        val dia = DiaFormulario()
        dia.pergunta1 = txt_pergunta1.text.toString()
        dia.pergunta2 = txt_diario.text.toString()
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

            val checked = view.isChecked

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
        dbRef.get().addOnSuccessListener {
            if(it.value != null){
                if(it.child(AuthUtil.getCurrentUser()!!).child("tipo_perfil").value.toString() == "Usuário do diário"){
                    txt_pergunta1.setText(if(it.child(AuthUtil.getCurrentUser()!!).child(dataSelecionada).child("pergunta1").value != null) it.child(AuthUtil.getCurrentUser()!!).child(dataSelecionada).child("pergunta1").value.toString() else "")
                    txt_diario.setText(if(it.child(AuthUtil.getCurrentUser()!!).child(dataSelecionada).child("pergunta2").value != null) it.child(AuthUtil.getCurrentUser()!!).child(dataSelecionada).child("pergunta2").value.toString() else "")
                    avaliacao_dia = it.child(AuthUtil.getCurrentUser()!!).child(dataSelecionada).child("avaliacaoDia").value.toString()

                    when(avaliacao_dia){
                        "Péssimo" -> radio_pessimo.setChecked(true)
                        "Ruim" -> radio_ruim.setChecked(true)
                        "Regular" -> radio_regular.setChecked(true)
                        "Bom" -> radio_bom.setChecked(true)
                        "Excelente" -> radio_excelente.setChecked(true)
                    }
                }else{
                    for(item in it.children){
                        if(item.child("email").value.toString() == emailUsuarioSelecionado){
                            txt_pergunta1.setText(if(item.child(dataSelecionada).child("pergunta1").value != null) item.child(dataSelecionada).child("pergunta1").value.toString() else "")
                            txt_diario.setText(if(item.child(dataSelecionada).child("pergunta2").value != null) item.child(dataSelecionada).child("pergunta2").value.toString() else "" )
                            avaliacao_dia = item.child(dataSelecionada).child("avaliacaoDia").value.toString()

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
            }
        }
    }


    //caso quem acesse seja um psicologo, algumas informações do diario não poderão ser vistas por ele
    private fun verifyUser(){
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
            val tipo_usuario = it.child("tipo_perfil").value.toString()
            if(tipo_usuario == "Psicólogo"){
                val intent = intent
                emailUsuarioSelecionado = intent.getStringExtra("emailUsuarioSelecionado").toString()
                txt_diario.setVisibility(View.INVISIBLE)
                btn_salvar_diario.setVisibility(View.INVISIBLE)
                txt_pergunta1.setEnabled(false)
                radio_pessimo.setClickable(false)
                radio_ruim.setClickable(false)
                radio_regular.setClickable(false)
                radio_bom.setClickable(false)
                radio_excelente.setClickable(false)
                text_view_txt_diario.setVisibility(View.INVISIBLE)
            }else{
                emailUsuarioSelecionado = ""
            }
        }
    }

}