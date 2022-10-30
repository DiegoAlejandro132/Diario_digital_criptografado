package tcc.com.diario_digital_criptografado


import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_editar_perfil.*
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil
import tcc.com.diario_digital_criptografado.util.ValidationUtil
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.Exception

@RequiresApi(Build.VERSION_CODES.M)
class EditarPerfilUsuarioActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)
        usuarioEstaLogado()

        supportActionBar?.title = "Editar perfil"


        if(ConexaoUtil.estaConectado(this)){
            trazerDadosUsuario()
        }else{
            progressive_editar_perfil.isVisible = false
            linear_layout_conteudo_editar_perfil.isVisible = true
            Snackbar.make(btn_voltar_editar_perfil, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
        }

        img_foto_perfil.setOnClickListener{
            selectImage()
        }

        btn_voltar_editar_perfil.setOnClickListener{
            finish()
        }

        btn_salvar_dados_editar_perfil.setOnClickListener{
            if(ConexaoUtil.estaConectado(this)){
                saveUserData()
                uploadImageToStorage()
            }else{
                Snackbar.make(btn_voltar_editar_perfil, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
            }

        }

        txt_editar_data_nascimento.setOnClickListener {
            selecionarDataNascimento()
        }


    }


    private fun trazerDadosUsuario(){

        try{

            if(!progressive_editar_perfil.isVisible){
                linear_layout_conteudo_editar_perfil.visibility = View.GONE
                progressive_editar_perfil.isVisible = true
            }

            database = FirebaseDatabase.getInstance().getReference("users")
            database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {

                val fotoPerfil = it.child("foto_perfil").value.toString()
                if (fotoPerfil != ""){
                    Glide.with(this).load(fotoPerfil.toUri()).into(img_foto_perfil)
                }

                txt_editar_nome_usuario.setText(it.child("nome").value.toString())
                txt_editar_data_nascimento.text = it.child("data_nascimento").value.toString()
                txt_editar_telefone.setText(it.child("telefone").value.toString())
                txt_editar_email.setText(it.child("email").value.toString())
                txt_editar_cpf.setText(it.child("cpf").value.toString())
                txt_codigo_usuario.setText(it.child(AuthUtil.getCurrentUser()!!).key.toString())


                if(it.child("tipo_perfil").value.toString() == "Psicólogo"){

                    lbl_editar_numero_registro.visibility = View.VISIBLE
                    txt_editar_numero_registro.visibility = View.VISIBLE
                    txt_editar_numero_registro.setText(it.child("numero_registro").value.toString())

                    lbl_editar_numero_regiao.visibility = View.VISIBLE
                    txt_editar_regiao.visibility = View.VISIBLE
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

            var dataErrada = Date(it)
            var localDate = dataErrada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            localDate = localDate.plusDays(1)
            var dataCerta = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(localDate)

            val valido = ValidationUtil.validarDataNascimento(localDate.dayOfMonth, localDate.monthValue, localDate.year)

            if(valido) {
                txt_editar_data_nascimento.text = dataCerta.toString()
            }else {
                Snackbar.make(txt_editar_data_nascimento, "Usuário não pode ser menor de 18 anos", Snackbar.LENGTH_LONG).show()
            }
        }

    }

}