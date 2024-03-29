package tcc.com.diario_digital_criptografado.usuarioActivities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_formulario_diario.*
import tcc.com.diario_digital_criptografado.MainActivity
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.model.Diario
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil
import tcc.com.diario_digital_criptografado.util.CriptografiaUtil
import tcc.com.diario_digital_criptografado.util.FotoUtil

class FormularioDiarioActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference

    private lateinit var avaliacao_dia : String
    private lateinit var dataSelecionada : String
    private lateinit var dataSelecionadaLong : String
    private var tipoUsuario : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_diario)
        usuarioEstaLogado()


        dataSelecionada = intent.getStringExtra("dataSelecionada").toString()
        dataSelecionadaLong = intent.getStringExtra("dataSelecionadaLong").toString()

        supportActionBar?.title = "Como foi o dia?"
        val textoActionBar = "<b>Dia: $dataSelecionada </b>" 
        supportActionBar?.subtitle = Html.fromHtml(textoActionBar)

        if(ConexaoUtil.estaConectado(this)){
            FotoUtil.definirFotoPerfil()
            trazerDadosDia(dataSelecionada)
        }else{
            linear_layout_conteudo_formulario.isVisible = true
            progressive_formulario.visibility = View.GONE
            Snackbar.make(btn_voltar_formulario_diario, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
        }

        onRadioButtonClicked(checkboxGroup)

        btn_salvar_diario.setOnClickListener{
            if(ConexaoUtil.estaConectado(this)){
                dialogSalvar()
            }else{
                Snackbar.make(btn_voltar_formulario_diario, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
            }
        }

        btn_voltar_formulario_diario.setOnClickListener{
            if(tipoUsuario == "Usuário do diário")
                dialogVotlar()
            else
                finish()
        }
    }

    override fun onResume() {
        super.onResume()
        usuarioEstaLogado()
        FotoUtil.definirFotoPerfil()
    }

    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->

    //salva os dados inseridos no formulario do dia

    private fun salvarDadosDia(){
        try{
            if(ConexaoUtil.estaConectado(this)){
                database = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!).child("dias")
                val dia = Diario()

                val diario = CriptografiaUtil.encrypt(txt_diario.text.toString())
                val avaliacaoDia = CriptografiaUtil.encrypt(avaliacao_dia)
                val sentimentosBons = CriptografiaUtil.encrypt(txt_sentimentos_bons.text.toString())
                val sentimentosRuins = CriptografiaUtil.encrypt(txt_sentimentos_ruins.text.toString())
                val titulo = CriptografiaUtil.encrypt(txt_titulo_dia.text.toString())
                val data = CriptografiaUtil.encrypt(dataSelecionada)
                val dataLong = CriptografiaUtil.encrypt(dataSelecionadaLong)
                val modificadoEm = CriptografiaUtil.encrypt(System.currentTimeMillis().toString())

                dia.sentimentos_bons = sentimentosBons
                dia.sentimentos_ruins = sentimentosRuins
                dia.diario = diario
                dia.avaliacaoDia = avaliacaoDia
                dia.titulo = titulo
                dia.data = data
                dia.modificado_em = modificadoEm
                dia.data_long = dataLong
                if(!diaVazio())
                    database.child(dataSelecionada).setValue(dia)
                //startActivity(Intent(this, AgendaUsuarioActivity::class.java))
                finish()
            }else{
                Snackbar.make(btn_voltar_formulario_diario, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
            }

        }catch (e:Exception){
            Log.e("salvarDadosDia", e.message.toString())
            Snackbar.make(btn_voltar_formulario_diario, "Não foi possível salvar os dados", Snackbar.LENGTH_LONG).show()
        }

    }

    private fun diaVazio(): Boolean{
        return txt_diario.text.toString() == "" && avaliacao_dia == "" && txt_sentimentos_bons.text.toString() == ""
                && txt_sentimentos_ruins.text.toString() == "" && txt_titulo_dia.text.toString() == ""
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
    private fun trazerDadosDia(dataSelecionada : String){

        try {

            if(!progressive_formulario.isVisible){
                linear_layout_conteudo_formulario.visibility = View.GONE
                progressive_formulario.isVisible = true
            }

            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                if(it.value != null){
                    if(it.child(AuthUtil.getCurrentUser()!!).child("tipo_perfil").value.toString() == "Usuário do diário"){

                        val tituloDia = CriptografiaUtil.decrypt(if(it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("titulo").value != null) it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("titulo").value.toString() else "")
                        txt_titulo_dia.setText(tituloDia)

                        val sentimentosBons = CriptografiaUtil.decrypt(if(it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("sentimentos_bons").value != null) it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("sentimentos_bons").value.toString() else "")
                        txt_sentimentos_bons.setText(sentimentosBons)

                        val sentimetosRuins = CriptografiaUtil.decrypt(if(it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("sentimentos_ruins").value != null) it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("sentimentos_ruins").value.toString() else "")
                        txt_sentimentos_ruins.setText(sentimetosRuins)

                        val diario = CriptografiaUtil.decrypt(if(it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("diario").value != null) it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("diario").value.toString() else "")
                        txt_diario.setText(diario)

                        val avaliacaoDia = CriptografiaUtil.decrypt(if(it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("avaliacaoDia").value != null) it.child(AuthUtil.getCurrentUser()!!).child("dias").child(dataSelecionada).child("avaliacaoDia").value.toString() else "")
                        avaliacao_dia = avaliacaoDia

                        when(avaliacao_dia){
                            "Péssimo" -> radio_pessimo.isChecked = true
                            "Ruim" -> radio_ruim.isChecked = true
                            "Regular" -> radio_regular.isChecked = true
                            "Bom" -> radio_bom.isChecked = true
                            "Excelente" -> radio_excelente.isChecked = true
                        }

                        linear_layout_conteudo_formulario.isVisible = true


                    }else if(it.child(AuthUtil.getCurrentUser()!!).child("tipo_perfil").value.toString() == "Psicólogo"){
                        val intent = intent
                        val emailUsuarioSelecionado = intent.getStringExtra("emailUsuarioSelecionado").toString()
                        for(item in it.children){
                            if(item.child("email").value.toString() == emailUsuarioSelecionado){

                                if(item.child("codigo_psicologo").value != AuthUtil.getCurrentUser() || item.child("tem_psicologo").value == false) {
                                    linear_layout_conteudo_formulario.visibility = View.GONE
                                    lbl_psicologo_nao_autorizado_formulario.isVisible = true
                                }else{
                                    val sentimentosBons = CriptografiaUtil.decrypt(if(item.child("dias").child(dataSelecionada).child("sentimentos_bons").value != null) item.child("dias").child(dataSelecionada).child("sentimentos_bons").value.toString() else "")
                                    txt_sentimentos_bons.setText(sentimentosBons)

                                    val sentimentosRuins = CriptografiaUtil.decrypt(if(item.child("dias").child(dataSelecionada).child("sentimentos_ruins").value != null) item.child("dias").child(dataSelecionada).child("sentimentos_ruins").value.toString() else "")
                                    txt_sentimentos_ruins.setText(sentimentosRuins)

                                    val avaliacaoDia = CriptografiaUtil.decrypt(if(item.child("dias").child(dataSelecionada).child("avaliacaoDia").value != null) item.child("dias").child(dataSelecionada).child("avaliacaoDia").value.toString() else "")
                                    avaliacao_dia = avaliacaoDia

                                    when(avaliacao_dia){
                                        "Péssimo" -> radio_pessimo.isChecked = true
                                        "Ruim" -> radio_ruim.isChecked = true
                                        "Regular" -> radio_regular.isChecked = true
                                        "Bom" -> radio_bom.isChecked = true
                                        "Excelente" -> radio_excelente.isChecked = true
                                    }
                                    definirVisualizaçãoPsicologo()
                                }
                            }
                        }
                    }
                }

                if(progressive_formulario.isVisible){
                    progressive_formulario.isVisible = false
                }

            }.addOnFailureListener {
                Toast.makeText(this@FormularioDiarioActivity, "Erro ao trazer os dados do dia.", Toast.LENGTH_SHORT).show()
                Log.e("retrieveDayData", it.message.toString())
            }
        }catch (e:Exception){
            Toast.makeText(this@FormularioDiarioActivity, "Erro ao trazer os dados do dia.", Toast.LENGTH_SHORT).show()
            Log.e("retrieveDayData", e.message.toString())
        }

    }


    //caso quem acesse seja um psicologo, algumas informações do diario não poderão ser vistas por ele
    private fun definirVisualizaçãoPsicologo(){
        linear_layout_conteudo_formulario.isVisible = true
        lbl_psicologo_nao_autorizado_formulario.visibility = View.GONE

        linear_layout_formulario_diario.visibility = View.GONE
        layout_formulario_titulo.visibility = View.GONE
        btn_salvar_diario.visibility = View.GONE
        txt_sentimentos_bons.isEnabled = false
        txt_sentimentos_bons.hint = ""
        txt_sentimentos_ruins.isEnabled = false
        txt_sentimentos_ruins.hint = ""
        radio_pessimo.isClickable = false
        radio_ruim.isClickable = false
        radio_regular.isClickable = false
        radio_bom.isClickable = false
        radio_excelente.isClickable = false
    }

    private fun usuarioEstaLogado(){
        if(!AuthUtil.usuarioEstaLogado()){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun dialogVotlar(){
        val dialogBuilder = AlertDialog.Builder(this@FormularioDiarioActivity)
        dialogBuilder.setMessage("Caso você saia, os dados anotados não serão salvos")
            .setTitle("Deseja mesmo sair?")
            .setPositiveButton("Sim") { dialog, id ->  finish() }
            .setNegativeButton("Não") { dialog, id ->  dialog.dismiss() }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun dialogSalvar(){
        val dialogBuilder = AlertDialog.Builder(this@FormularioDiarioActivity)
        dialogBuilder.setMessage("Você deseja salvar os dados?")
            .setTitle("Salvar dados?")
            .setPositiveButton("Sim") { dialog, id ->  salvarDadosDia() }
            .setNegativeButton("Não") { dialog, id ->  dialog.dismiss() }
        val b = dialogBuilder.create()
        b.show()
    }


}