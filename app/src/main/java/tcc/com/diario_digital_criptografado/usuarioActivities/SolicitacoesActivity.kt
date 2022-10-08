package tcc.com.diario_digital_criptografado.usuarioActivities


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_solicitacoes.*
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.util.AuthUtil
import java.io.File
import kotlin.Exception

class SolicitacoesActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitacoes)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        retrievePsicologoData()

        var textoObservacao = "Antes de aceitar qualquer solicitação \nverifique a veracidade dos dados no <a href='https://cadastro.cfp.org.br/'>cadastro nacional de psicólogos</a>"
        lbl_observacoes_solicitacoes.text = Html.fromHtml(textoObservacao)
        lbl_observacoes_solicitacoes.movementMethod = LinkMovementMethod.getInstance()

        btn_aceitar_solicitacao.setOnClickListener {
            dialogAceitarSolicitacao()
        }

        btn_rejeitar_solicitacao.setOnClickListener {
            dialogRejeitarSolicitacao()
        }

        btn_voltar_solicitacoes.setOnClickListener {
            finish()
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
            .setPositiveButton("Sim", { dialog, id ->  aceitarSolicitacao() })
            .setNegativeButton("Não", { dialog, id ->  dialog.dismiss()})
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
            .setPositiveButton("Sim", { dialog, id ->  rejeitarSolicitacao() })
            .setNegativeButton("Não", { dialog, id ->  dialog.dismiss() })
        val b = dialogBuilder.create()
        b.show()
    }

    private fun retrievePsicologoData(){

        try{
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

                        val codigoPsicologoFoto = it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo_solicitacao").value.toString()
                        val storageRef = FirebaseStorage.getInstance().reference.child("fotos_perfil/${codigoPsicologoFoto}")

                        val localFile = File.createTempFile("tempImage", "")
                        storageRef.getFile(localFile).addOnSuccessListener{
                            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            val bitmapResize = Bitmap.createScaledBitmap(bitmap, 450,450, true)
                            img_foto_psicologo_soliitacao.setImageBitmap(bitmapResize)
                        }.addOnFailureListener{
                            Toast.makeText(this@SolicitacoesActivity, "Não foi possível carregar a imagem", Toast.LENGTH_SHORT).show()
                        }
                        lbl_sem_solicitacoes.setVisibility(View.GONE)
                        linear_layout_dados_psicologo_solicitacao.setVisibility(View.VISIBLE)

                    }else{

                        lbl_sem_solicitacoes.setVisibility(View.VISIBLE)
                        linear_layout_dados_psicologo_solicitacao.setVisibility(View.GONE)

                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this@SolicitacoesActivity, "Erro ao trazer os dados do psiólogo.", Toast.LENGTH_SHORT).show()
            Log.e("retrievePsicologoData", e.message.toString())
        }
    }
}