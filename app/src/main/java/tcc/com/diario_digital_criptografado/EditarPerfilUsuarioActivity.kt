package tcc.com.diario_digital_criptografado


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_editar_perfil_psicologo.*
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.*
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.btn_salvar_dados_editar_perfil
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.img_foto_perfil
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.txt_editar_cpf
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.txt_editar_data_nascimento
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.txt_editar_email
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.txt_editar_nome_usuario
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.txt_editar_telefone
import tcc.com.diario_digital_criptografado.util.AuthUtil
import java.io.File
import java.lang.Exception

class EditarPerfilUsuarioActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference

    private var imageUri: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private lateinit var tipoUsuarioGlobal : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tipoUsuario = intent.getStringExtra("tipoUsuario")
        tipoUsuarioGlobal = tipoUsuario!!
        if(tipoUsuario == "Usuário do diário"){
            Toast.makeText(this, tipoUsuario, Toast.LENGTH_SHORT).show()
            setContentView(R.layout.activity_editar_perfil_usuario)

        }else if(tipoUsuario == "Psicólogo"){
            Toast.makeText(this, tipoUsuario, Toast.LENGTH_SHORT).show()
            setContentView(R.layout.activity_editar_perfil_psicologo)
        }

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        retrieveUserData()

        img_foto_perfil.setOnClickListener{
            selectImage()
        }

        btn_salvar_dados_editar_perfil.setOnClickListener{
            saveUserData()
            uploadImage()
        }

        btn_voltar_editar_perfil.setOnClickListener{
            finish()
        }
    }


    private fun retrieveUserData(){
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
            txt_editar_nome_usuario.setText(it.child("nome").value.toString())
            txt_editar_data_nascimento.setText(it.child("data_nascimento").value.toString())
            txt_editar_telefone.setText(it.child("telefone").value.toString())
            txt_editar_email.setText(it.child("email").value.toString())
            txt_editar_cpf.setText(it.child("cpf").value.toString())
            if(tipoUsuarioGlobal == "Psicólogo"){
                txt_editar_numero_registro.setText(it.child("numero_registro").value.toString())
                txt_editar_regiao.setText(it.child("estado_registro").value.toString())
            }
        }
        val nomeImagem = AuthUtil.getCurrentUser()
        val storageref = FirebaseStorage.getInstance().reference.child("fotos_perfil/${nomeImagem}")

        val localFile = File.createTempFile("tempImage", "")
        storageref.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            val bitmapResize = Bitmap.createScaledBitmap(bitmap, 400,500, true)
            img_foto_perfil.setImageBitmap(bitmapResize)
        }.addOnFailureListener{
            Toast.makeText(this, "Deu pau na imagem", Toast.LENGTH_SHORT).show()
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
            val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val bitmapResize = Bitmap.createScaledBitmap(bitmap,400, 500, true)
            img_foto_perfil.setImageBitmap(bitmapResize)
        }
    }

    private fun uploadImage(){
        if(imageUri != null){
            val storageReference = FirebaseStorage.getInstance().getReference("fotos_perfil/${AuthUtil.getCurrentUser()}")
            storageReference.putFile(imageUri!!)
        }
    }

}