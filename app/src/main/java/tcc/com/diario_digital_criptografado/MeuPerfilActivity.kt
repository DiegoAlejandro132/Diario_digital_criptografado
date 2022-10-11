package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_meu_perfil.*
import tcc.com.diario_digital_criptografado.util.AuthUtil
import java.io.File

class MeuPerfilActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

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

    }


    private fun trazerDadosUsuario(){
        try{
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

                    if(tipoUsuario == "Psicólogo"){
                        lbl_meu_numero_registro.visibility = View.VISIBLE
                        lbl_meu_numero_registro_titulo.visibility = View.VISIBLE

                        lbl_meu_regiao_registro.visibility = View.VISIBLE
                        lbl_meu_numero_registro_titulo.visibility = View.VISIBLE

                        lbl_meu_numero_registro.text = it.child(AuthUtil.getCurrentUser()!!).child("numero_registro").value.toString()
                        lbl_meu_regiao_registro.text = it.child(AuthUtil.getCurrentUser()!!).child("estado_registro").value.toString()
                    }

                    val nomeImagem = AuthUtil.getCurrentUser()
                    val storageref = FirebaseStorage.getInstance().reference.child("fotos_perfil/${nomeImagem}")

                    val localFile = File.createTempFile("tempImage", "")
                    storageref.getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        val bitmapResize = Bitmap.createScaledBitmap(bitmap, 450,450, true)
                        img_foto_meu_perfil.setImageBitmap(bitmapResize)
                    }.addOnFailureListener{
                        Toast.makeText(this, "Não foi possível carregar a imagem", Toast.LENGTH_SHORT).show()
                    }

                }
            }.addOnFailureListener {
                Toast.makeText(this@MeuPerfilActivity, "nao pegou snapshot", Toast.LENGTH_SHORT).show()
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

}