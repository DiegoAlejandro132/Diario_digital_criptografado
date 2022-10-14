package tcc.com.diario_digital_criptografado

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_meu_perfil.*
import tcc.com.diario_digital_criptografado.util.AuthUtil

class MeuPerfilActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meu_perfil)

        trazerDadosUsuario()

        btn_ir_editar_perfil.setOnClickListener{
            irEditarPerfil()
        }

        btn_voltar_meu_perfil.setOnClickListener{
            finish()
        }

        btn_copiar_codigo_usuario_meu_perfil.setOnClickListener {
            copiarCodigoUsuario()
        }

        btn_excluir_usuario.setOnClickListener{
            dialogExcluirUsuario()
        }
    }

    override fun onResume() {
        super.onResume()
        trazerDadosUsuario()
    }


    private fun trazerDadosUsuario(){
        try{
            if(!progressive_meu_perfil.isVisible){
                linear_layout_conteudo_meu_perfil.setVisibility(View.GONE)
                progressive_meu_perfil.isVisible = true
            }
            firebaseStore = FirebaseStorage.getInstance()
            storageReference = FirebaseStorage.getInstance().reference
            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                if(it.exists()){

                    val tipoUsuario = it.child(AuthUtil.getCurrentUser()!!).child("tipo_perfil").value.toString()

                    lbl_meu_nome_usuario.text = it.child(AuthUtil.getCurrentUser()!!).child("nome").value.toString()
                    lbl_meu_data_nascimento.text = it.child(AuthUtil.getCurrentUser()!!).child("data_nascimento").value.toString()
                    lbl_meu_telefone.text = it.child(AuthUtil.getCurrentUser()!!).child("telefone").value.toString()
                    lbl_meu_email.text = it.child(AuthUtil.getCurrentUser()!!).child("email").value.toString()
                    lbl_meu_cpf.text = it.child(AuthUtil.getCurrentUser()!!).child("cpf").value.toString()
                    lbl_meu_codigo_usuario.text = it.child(AuthUtil.getCurrentUser()!!).key.toString()

                    val fotoUri = it.child(AuthUtil.getCurrentUser()!!).child("foto_perfil").value.toString().toUri()
                    if(fotoUri.toString() != ""){
                        Glide.with(this).load(fotoUri).into(img_foto_meu_perfil)
                    }else{
                        Glide.with(this).load(R.drawable.imagem_perfil_default).into(img_foto_meu_perfil)
                    }

                    if(tipoUsuario == "Psicólogo"){
                        lbl_meu_numero_registro.visibility = View.VISIBLE
                        lbl_meu_numero_registro_titulo.visibility = View.VISIBLE

                        lbl_meu_regiao_registro.visibility = View.VISIBLE
                        lbl_meu_numero_registro_titulo.visibility = View.VISIBLE

                        lbl_meu_numero_registro.text = it.child(AuthUtil.getCurrentUser()!!).child("numero_registro").value.toString()
                        lbl_meu_regiao_registro.text = it.child(AuthUtil.getCurrentUser()!!).child("estado_registro").value.toString()
                    }
                    if(progressive_meu_perfil.isVisible){
                        progressive_meu_perfil.isVisible = false
                        linear_layout_conteudo_meu_perfil.isVisible = true
                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this@MeuPerfilActivity, "Houve um erro ao trazer os dados. Tente mais tarde.", Toast.LENGTH_SHORT).show()
            Log.e("trazerDadosUsuario", e.message.toString())
        }
    }

    private fun irEditarPerfil(){
        intent = Intent(this, EditarPerfilUsuarioActivity::class.java)
        startActivity(intent)
    }

    private fun copiarCodigoUsuario(){
        val codigoUsuario = lbl_meu_codigo_usuario.text

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", codigoUsuario)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Código copiado para a área de transferência", Toast.LENGTH_LONG).show()
    }

    private fun excluirUsuario() {
        val database = FirebaseDatabase.getInstance().getReference("users")
        val codigoUsuario = AuthUtil.getCurrentUser()
        var usuario = auth.currentUser!!
        usuario.delete().addOnCompleteListener() { task ->
            if (task.isSuccessful) {

                database.get().addOnSuccessListener {
                    if (it.exists()) {
                        //caso o usuario seja usuario do diario
                        if (it.child("${codigoUsuario}/tipo_perfil").value.toString() == "Usuário do diário") {
                            if (it.child("${codigoUsuario}/codigo_psicologo").value.toString() != "") {
                                val codigoPsicologo =
                                    it.child("${codigoUsuario}/codigo_psicologo").value.toString()
                                database.child("${codigoPsicologo}/pacientes/${codigoUsuario}")
                                    .setValue(null)
                                database.child("${codigoUsuario}").setValue(null)
                            }else {
                                database.child("${codigoUsuario}").setValue(null)
                            }

                        //caso o usuario seja psicologo
                        }else if (it.child("${codigoUsuario}/tipo_perfil").value.toString() == "Psicólogo"){
                            if(it.child("${codigoUsuario}/pacientes").value != null){
                                for(paciente in it.child("${codigoUsuario}/pacientes").children){
                                    val codigoPaciente = paciente.key
                                    database.child("$codigoPaciente/codigo_psicologo").setValue("")
                                    database.child("$codigoPaciente/tem_psicologo").setValue(false)
                                }
                                database.child("${codigoUsuario}").setValue(null)
                            }else{
                                database.child("${codigoUsuario}").setValue(null)
                            }
                        }

                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun dialogExcluirUsuario(){
        val dialogBuilder = AlertDialog.Builder(this@MeuPerfilActivity)
        dialogBuilder.setMessage("Deseja mesmo excluir seu perfil?\nTodos os dados serão apagados!")
            .setTitle("Excluir perfil")
            .setPositiveButton("Sim") { dialog, id ->  excluirUsuario() }
            .setNegativeButton("Não") { dialog, id ->  dialog.dismiss() }
        val b = dialogBuilder.create()
        b.show()
    }

}