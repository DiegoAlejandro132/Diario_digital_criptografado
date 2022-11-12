package tcc.com.diario_digital_criptografado.usuarioActivities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_meu_perfil.*
import kotlinx.android.synthetic.main.activity_meu_psicologo.*
import kotlinx.android.synthetic.main.activity_solicitacoes.*
import tcc.com.diario_digital_criptografado.MainActivity
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil
import tcc.com.diario_digital_criptografado.util.FotoUtil
import kotlin.Exception

class SolicitacoesActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitacoes)

        supportActionBar?.title = "Minhas solicitações"
        usuarioEstaLogado()

        if(ConexaoUtil.estaConectado(this)){
            FotoUtil.definirFotoPerfil()
            trazerDadosPsicologo()
        }else{
            progressive_solicitacoes.visibility = View.GONE
            linear_layout_conteudo_solicitacoes.isVisible = true
            btn_voltar_solicitacoes.isVisible = true
            Snackbar.make(btn_voltar_solicitacoes, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
        }

        var textoObservacao = "Antes de aceitar qualquer solicitação \nverifique a veracidade dos dados acessando o <a href='https://cadastro.cfp.org.br/'>cadastro nacional de psicólogos</a>"
        lbl_observacoes_solicitacoes.text = Html.fromHtml(textoObservacao)
        lbl_observacoes_solicitacoes.movementMethod = LinkMovementMethod.getInstance()

        btn_aceitar_solicitacao.setOnClickListener {
            if (ConexaoUtil.estaConectado(this)){
                dialogAceitarSolicitacao()
            }else{
                Snackbar.make(btn_voltar_solicitacoes, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
            }
        }

        btn_rejeitar_solicitacao.setOnClickListener {
            if (ConexaoUtil.estaConectado(this)){
                dialogRejeitarSolicitacao()
            }else{
                Snackbar.make(btn_voltar_solicitacoes, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
            }
        }

        btn_voltar_solicitacoes.setOnClickListener {
            finish()
        }

        linear_layout_duvida_solicitacao_psicologo.setOnClickListener {
            if (linear_layout_conteudo_duvida_solicitacao_psicologo.isVisible) {
                linear_layout_conteudo_duvida_solicitacao_psicologo.visibility = View.GONE
                btn_esconder_duvida_solicitacao_psicologo.visibility = View.GONE
                btn_mostrar_duvida_solicitacao_psicologo.isVisible = true
            } else{
                linear_layout_conteudo_duvida_solicitacao_psicologo.isVisible = true
                btn_esconder_duvida_solicitacao_psicologo.isVisible = true
                btn_mostrar_duvida_solicitacao_psicologo.visibility = View.GONE
            }
        }

    }

    private fun aceitarSolicitacao(){

        try{
            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                if(it.exists()){

                    val codigoPsicologo = it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo_solicitacao").value.toString()
                    database.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo").setValue(it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo_solicitacao").value.toString())
                    database.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo_solicitacao").setValue("")
                    database.child(AuthUtil.getCurrentUser()!!).child("tem_psicologo").setValue(true)
                    database.child(AuthUtil.getCurrentUser()!!).child("tem_solicitacao").setValue(false)
                    database.child(codigoPsicologo).child("pacientes").child(AuthUtil.getCurrentUser()!!).setValue(it.child(AuthUtil.getCurrentUser()!!).child("nome").value.toString())

                    linear_layout_dados_psicologo_solicitacao.setVisibility(View.GONE)
                    lbl_sem_solicitacoes.setVisibility(View.VISIBLE)

                }
            }
        }catch (e:Exception){
            Toast.makeText(this@SolicitacoesActivity, "Erro ao aceitar a solicitação.", Toast.LENGTH_SHORT).show()
            Log.e("aceitarSolicitacao", e.message.toString())
        }
    }

    private fun dialogAceitarSolicitacao(){
        val dialogBuilder = AlertDialog.Builder(this@SolicitacoesActivity)
        dialogBuilder.setMessage("Deseja mesmo aceitar? Você pesquisou a veracidade dos dados do psicólogo?")
            .setTitle("Aceitar Solicitação")
            .setPositiveButton("Sim") { dialog, id -> aceitarSolicitacao() }
            .setNegativeButton("Não") { dialog, id -> dialog.dismiss() }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun rejeitarSolicitacao() {
        try {
            database = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!)
            database.child("tem_solicitacao").setValue(false)
            database.child("codigo_psicologo_solicitacao").setValue("")
            linear_layout_dados_psicologo_solicitacao.setVisibility(View.GONE)
            lbl_sem_solicitacoes.setVisibility(View.VISIBLE)
            Toast.makeText(this@SolicitacoesActivity, "Solicitação rejeitada com sucesso.", Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            Toast.makeText(this@SolicitacoesActivity, "Houve um erro de conexão ao realizar a ação. Tente mais tarde", Toast.LENGTH_SHORT).show()
            Log.e("aceitarSolicitacao", e.message!!)
        }
    }

    private fun dialogRejeitarSolicitacao(){
        val dialogBuilder = AlertDialog.Builder(this@SolicitacoesActivity)
        dialogBuilder.setMessage("Deseja mesmo rejeitar?")
            .setTitle("Rejeitar solicitação")
            .setPositiveButton("Sim", { dialog, id ->  rejeitarSolicitacao() })
            .setNegativeButton("Não", { dialog, id ->  dialog.dismiss() })
        val b = dialogBuilder.create()
        b.show()
    }

    private fun trazerDadosPsicologo(){

        try{

            if(!progressive_solicitacoes.isVisible){
                linear_layout_conteudo_meu_perfil.setVisibility(View.GONE)
                progressive_solicitacoes.isVisible = true
            }

            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {

                if(it.exists()){

                    if(it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo_solicitacao").value.toString() != ""
                        && it.child(AuthUtil.getCurrentUser()!!).child("tem_solicitacao").value == true){

                        val codigoPsicologo = it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo_solicitacao").value.toString()
                        lbl_nome_psicologo_solicitacao.setText(it.child(codigoPsicologo).child("nome").value.toString())
                        lbl_cpf_psiclogo_solicitacao.setText(it.child(codigoPsicologo).child("cpf").value.toString())
                        lbl_numero_inscricao_psicologo_solicitacao.setText(it.child(codigoPsicologo).child("numero_registro").value.toString())
                        lbl_regiao_inscricao_psicologo_solicitacao.setText(it.child(codigoPsicologo).child("estado_registro").value.toString())
                        lbl_codigo_psicologo_solicitacao.setText(codigoPsicologo)

                        val fotoUri = it.child(codigoPsicologo).child("foto_perfil").value.toString().toUri()
                        if(fotoUri.toString() != ""){
                            Glide.with(this).load(fotoUri).into(img_foto_psicologo_solicitacao)
                        }else{
                            Glide.with(this).load(R.drawable.imagem_perfil_default).into(img_foto_psicologo_solicitacao)
                        }


                        if(progressive_solicitacoes.isVisible){
                            progressive_solicitacoes.isVisible = false
                            linear_layout_conteudo_solicitacoes.isVisible = true
                        }

                        lbl_sem_solicitacoes.visibility = View.GONE
                        linear_layout_dados_psicologo_solicitacao.isVisible = true
                        btn_voltar_solicitacoes.isVisible = true

                    }else{

                        lbl_sem_solicitacoes.setVisibility(View.VISIBLE)
                        linear_layout_dados_psicologo_solicitacao.setVisibility(View.GONE)
                        btn_voltar_solicitacoes.isVisible = true

                        if(progressive_solicitacoes.isVisible){
                            progressive_solicitacoes.isVisible = false
                            linear_layout_conteudo_solicitacoes.isVisible = true
                        }

                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this@SolicitacoesActivity, "Erro ao trazer os dados do psiólogo.", Toast.LENGTH_SHORT).show()
            Log.e("retrievePsicologoData", e.message.toString())
        }
    }

    private fun usuarioEstaLogado(){
        if(!AuthUtil.usuarioEstaLogado()){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}