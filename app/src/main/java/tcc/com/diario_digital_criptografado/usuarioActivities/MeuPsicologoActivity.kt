package tcc.com.diario_digital_criptografado.usuarioActivities

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_meu_psicologo.*
import kotlinx.android.synthetic.main.activity_solicitacoes.*
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.util.AuthUtil
import java.io.File

class MeuPsicologoActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meu_psicologo)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        retrievePsicologoData()

        btn_excluir_psicologo.setOnClickListener {
            dialogExcluirPsicologo()
        }

        btn_voltar_meu_psicologo.setOnClickListener {
            finish()
        }
    }

    private fun excluirPsicologo() {

        try{
            database = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!)
            database.child("codigo_psicologo").setValue("")
            database.child("tem_psicologo").setValue(false)
            linear_layout_meu_psicologo.setVisibility(View.GONE)
            lbl_sem_meu_psicologo.setVisibility(View.VISIBLE)

        }catch (e:Exception){
            Toast.makeText(this@MeuPsicologoActivity, "Erro ao excluir o psicólogo", Toast.LENGTH_SHORT).show()
            Log.e("excluirPsicologo", e.message.toString())
        }


    }

    private fun dialogExcluirPsicologo(){
        val dialogBuilder = AlertDialog.Builder(this@MeuPsicologoActivity)
        dialogBuilder.setMessage("Deseja mesmo excluir o psicólogo da sua conta?")
            .setPositiveButton("Sim", { dialog, id ->  excluirPsicologo() })
            .setNegativeButton("Não", { dialog, id ->  dialog.dismiss() })
        val b = dialogBuilder.create()
        b.show()
    }

    private fun retrievePsicologoData(){

        try{
            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                if(it.exists()){
                    if(it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo").value.toString() != ""){
                        lbl_sem_meu_psicologo.setVisibility(View.GONE)
                        val codigoPsicologo = it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo").value.toString()
                        lbl_meu_psicologo_nome.setText(it.child(codigoPsicologo).child("nome").value.toString())
                        lbl_meu_psicologo_cpf.setText(it.child(codigoPsicologo).child("cpf").value.toString())
                        lbl_meu_psicologo_numero_inscricao.setText(it.child(codigoPsicologo).child("numero_registro").value.toString())
                        lbl_meu_psicologo_estado_inscricao.setText(it.child(codigoPsicologo).child("estado_registro").value.toString())
                        linear_layout_meu_psicologo.setVisibility(View.VISIBLE)

                        val codigoPsicologoFoto = it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo").value.toString()
                        val storageRef = FirebaseStorage.getInstance().reference.child("fotos_perfil/${codigoPsicologoFoto}")

                        val localFile = File.createTempFile("tempImage", "")
                        storageRef.getFile(localFile).addOnSuccessListener{
                            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            val bitmapResize = Bitmap.createScaledBitmap(bitmap, 450,450, true)
                            img_foto_meu_psicologo.setImageBitmap(bitmapResize)
                        }.addOnFailureListener{
                            Toast.makeText(this@MeuPsicologoActivity, "Deu pau na imagem", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        linear_layout_meu_psicologo.setVisibility(View.GONE)
                        lbl_sem_meu_psicologo.setVisibility(View.VISIBLE)
                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this@MeuPsicologoActivity, "Erro ao trazer os dados do psicólogo", Toast.LENGTH_SHORT).show()
            Log.e("excluirPsicologo", e.message.toString())
        }
    }
}