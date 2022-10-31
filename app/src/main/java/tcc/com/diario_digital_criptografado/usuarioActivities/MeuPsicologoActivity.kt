package tcc.com.diario_digital_criptografado.usuarioActivities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_formulario_diario.*
import kotlinx.android.synthetic.main.activity_meu_psicologo.*
import tcc.com.diario_digital_criptografado.MainActivity
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil
import tcc.com.diario_digital_criptografado.util.FotoUtil

@RequiresApi(Build.VERSION_CODES.M)
class MeuPsicologoActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meu_psicologo)
        usuarioEstaLogado()

        supportActionBar?.title = "Meu psicólogo"

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        if(ConexaoUtil.estaConectado(this)){
            FotoUtil.definirFotoPerfil()
            retrievePsicologoData()
        }else{
            progressive_meu_psicologo.isVisible = false
            linear_layout_conteudo_meu_psicologo.isVisible = true
            Snackbar.make(btn_voltar_meu_psicologo, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
        }

        btn_excluir_psicologo.setOnClickListener {
            if (ConexaoUtil.estaConectado(this)){
                dialogExcluirPsicologo()
            }else{
                Snackbar.make(btn_voltar_meu_psicologo, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
            }
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
            .setTitle("Excluir psicólogo")
            .setPositiveButton("Sim", { dialog, id ->  excluirPsicologo() })
            .setNegativeButton("Não", { dialog, id ->  dialog.dismiss() })
        val b = dialogBuilder.create()
        b.show()
    }

    private fun retrievePsicologoData(){

        try{

            if(!progressive_meu_psicologo.isVisible){
                linear_layout_conteudo_meu_psicologo.visibility = View.GONE
                progressive_meu_psicologo.isVisible = true
            }

            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                if(it.exists()){
                    if(it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo").value.toString() != ""){
                        lbl_sem_meu_psicologo.visibility = View.GONE
                        val codigoPsicologo = it.child(AuthUtil.getCurrentUser()!!).child("codigo_psicologo").value.toString()
                        lbl_meu_psicologo_nome.setText(it.child(codigoPsicologo).child("nome").value.toString())
                        lbl_meu_psicologo_cpf.setText(it.child(codigoPsicologo).child("cpf").value.toString())
                        lbl_meu_psicologo_numero_inscricao.setText(it.child(codigoPsicologo).child("numero_registro").value.toString())
                        lbl_meu_psicologo_estado_inscricao.setText(it.child(codigoPsicologo).child("estado_registro").value.toString())
                        lbl_codigo_psicologo_meu_psicologo.setText(codigoPsicologo)
                        linear_layout_meu_psicologo.setVisibility(View.VISIBLE)

                        val fotoUri = it.child(codigoPsicologo).child("foto_perfil").value.toString().toUri()
                        if(fotoUri.toString() != ""){
                            Glide.with(this).load(fotoUri).into(img_foto_meu_psicologo)
                        }else{
                            Glide.with(this).load(R.drawable.imagem_perfil_default).into(img_foto_meu_psicologo)
                        }

                        if(progressive_meu_psicologo.isVisible){
                            progressive_meu_psicologo.isVisible = false
                            linear_layout_conteudo_meu_psicologo.isVisible = true
                        }

                    }else{
                        linear_layout_meu_psicologo.setVisibility(View.GONE)
                        lbl_sem_meu_psicologo.setVisibility(View.VISIBLE)

                        if(progressive_meu_psicologo.isVisible){
                            progressive_meu_psicologo.isVisible = false
                            linear_layout_conteudo_meu_psicologo.isVisible = true
                        }

                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this@MeuPsicologoActivity, "Erro ao trazer os dados do psicólogo", Toast.LENGTH_SHORT).show()
            Log.e("excluirPsicologo", e.message.toString())
        }
    }

    private fun usuarioEstaLogado(){
        if(!AuthUtil.usuarioEstaLogado()){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}