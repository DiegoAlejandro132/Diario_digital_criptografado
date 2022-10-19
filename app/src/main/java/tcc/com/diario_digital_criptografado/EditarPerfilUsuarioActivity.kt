package tcc.com.diario_digital_criptografado


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_cadastro.*
import kotlinx.android.synthetic.main.activity_editar_perfil.*
import kotlinx.android.synthetic.main.activity_meu_perfil.*
import kotlinx.android.synthetic.main.header_navigation_drawer.*
import tcc.com.diario_digital_criptografado.util.AuthUtil
import java.util.*
import kotlin.Exception

class EditarPerfilUsuarioActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)
        usuarioEstaLogado()

        supportActionBar?.title = "Editar perfil"


        trazerDadosUsuario()

        img_foto_perfil.setOnClickListener{
            selectImage()
        }

        btn_voltar_editar_perfil.setOnClickListener{
            finish()
        }

        btn_salvar_dados_editar_perfil.setOnClickListener{
            saveUserData()
            uploadImageToStorage()
        }

        txt_editar_data_nascimento.setOnClickListener {
            selecionarDataNascimento()
        }


    }


    private fun trazerDadosUsuario(){

        try{

            if(!progressive_editar_perfil.isVisible){
                linear_layout_conteudo_editar_perfil.setVisibility(View.GONE)
                progressive_editar_perfil.isVisible = true
            }

            val storageReference = FirebaseStorage.getInstance().getReference("fotos_perfil/${AuthUtil.getCurrentUser()}")
            storageReference.downloadUrl.addOnSuccessListener {
                if(!(it == null || it.toString() == ""))
                    Glide.with(this).load(it).into(img_foto_perfil)
            }.addOnFailureListener {
                Toast.makeText(this, "Não foi posivel carregar a foto de perfil.", Toast.LENGTH_SHORT).show()
            }

            database = FirebaseDatabase.getInstance().getReference("users")
            database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
                txt_editar_nome_usuario.setText(it.child("nome").value.toString())
                txt_editar_data_nascimento.text = it.child("data_nascimento").value.toString()
                txt_editar_telefone.setText(it.child("telefone").value.toString())
                txt_editar_email.setText(it.child("email").value.toString())
                txt_editar_cpf.setText(it.child("cpf").value.toString())
                txt_codigo_usuario_psicologo.setText(it.child(AuthUtil.getCurrentUser()!!).key.toString())


                if(it.child("tipo_perfil").value.toString() == "Psicólogo"){

                    lbl_editar_numero_registro.setVisibility(View.VISIBLE)
                    txt_editar_numero_registro.setVisibility(View.VISIBLE)
                    txt_editar_numero_registro.setText(it.child("numero_registro").value.toString())

                    lbl_editar_numero_regiao.setVisibility(View.VISIBLE)
                    txt_editar_regiao.setVisibility(View.VISIBLE)
                    txt_editar_regiao.setText(it.child("estado_registro").value.toString())

                }

                if(progressive_editar_perfil.isVisible){
                    progressive_editar_perfil.isVisible = false
                    linear_layout_conteudo_editar_perfil.isVisible = true
                }

            }
        }catch (e:Exception){
            Toast.makeText(this@EditarPerfilUsuarioActivity, "Erro ao trazer os dados do usuário.", Toast.LENGTH_SHORT).show()
            Log.e("retrieveUserData", e.message.toString())
        }
    }

    private fun saveUserData() {
        try {
            if(txt_editar_nome_usuario.text.toString() == ""
                || txt_editar_data_nascimento.text.toString() == ""
                || txt_editar_telefone.text.toString() == ""){
                Toast.makeText(this, "Preencha todos os campos antes de salvar.", Toast.LENGTH_SHORT).show()
            }else{
                database = FirebaseDatabase.getInstance().getReference("users")
                database.child(AuthUtil.getCurrentUser()!!).child("nome").setValue(txt_editar_nome_usuario.text.toString())
                database.child(AuthUtil.getCurrentUser()!!).child("data_nascimento").setValue(txt_editar_data_nascimento.text.toString())
                database.child(AuthUtil.getCurrentUser()!!).child("telefone").setValue(txt_editar_telefone.text.toString())
                Toast.makeText(this, "Dados atualizados com sucesso.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch(e : Exception){
            Log.d("saveUserdataException:", e.toString())
            Toast.makeText(this, "Infelizmente houve um erro ao atualizar os dados, por favor tente mais tarde.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            Glide.with(this).load(imageUri).into(img_foto_perfil)
        }
    }

    private fun uploadImageToStorage(){
        try{
            if(imageUri != null){
                val storageReference = FirebaseStorage.getInstance().getReference("fotos_perfil/${AuthUtil.getCurrentUser()}")
                storageReference.putFile(imageUri!!)
            }
        }catch (e:Exception){
            Toast.makeText(this@EditarPerfilUsuarioActivity, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show()
            Log.e("uploadImage", e.message.toString())
        }
    }

    private fun usuarioEstaLogado(){
        if(!AuthUtil.usuarioEstaLogado()){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun selecionarDataNascimento(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione sua data de nascimento")
                .build()
        datePicker.show(supportFragmentManager, "tag")

        datePicker.addOnPositiveButtonClickListener {

            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val data = "${utc.get(Calendar.DAY_OF_MONTH)}-${utc.get(Calendar.MONTH)+1}-${utc.get(
                Calendar.YEAR)}"
            txt_editar_data_nascimento.text = data

        }

    }

}