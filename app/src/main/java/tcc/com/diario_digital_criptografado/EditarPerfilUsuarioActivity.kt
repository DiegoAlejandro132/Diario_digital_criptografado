package tcc.com.diario_digital_criptografado

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_editar_perfil_usuario.*
import tcc.com.diario_digital_criptografado.models.Psicologo
import tcc.com.diario_digital_criptografado.util.AuthUtil
import java.lang.Exception

class EditarPerfilUsuarioActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference

    private lateinit var tipoUsuario : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil_usuario)

        retrieveUserData()

        btn_salvar_dados_editar_perfil.setOnClickListener{
            saveUserData()
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
            tipoUsuario = it.child("tipo_perfil").value.toString()
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
}