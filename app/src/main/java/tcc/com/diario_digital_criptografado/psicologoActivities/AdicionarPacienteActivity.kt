package tcc.com.diario_digital_criptografado.psicologoActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_adicionar_paciente.*
import tcc.com.diario_digital_criptografado.MainActivity
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.util.AuthUtil

class AdicionarPacienteActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_paciente)

        usuarioEstaLogado()

         btn_enviar_solicitacao.setOnClickListener {
             enviarSolicitacao()
         }

        btn_voltar_adicionar_paciente.setOnClickListener {
            finish()
        }
    }


    private fun enviarSolicitacao(){
        try{

            var codigoPaciente = txt_codigo_paciente.text.toString()
            if(codigoPaciente != ""){
                database = FirebaseDatabase.getInstance().getReference("users").child(codigoPaciente)
                database.get().addOnSuccessListener {
                    if(it.value != null){
                        if(it.child("tem_psicologo").value == false && it.child("tem_solicitacao").value == false){
                            database.child("tem_solicitacao").setValue(true)
                            database.child("codigo_psicologo_solicitacao").setValue(AuthUtil.getCurrentUser())
                        }
                    }
                    if(lbl_advertencia_adicionar_paciente.visibility == View.VISIBLE){
                        lbl_advertencia_adicionar_paciente.setVisibility(View.INVISIBLE)
                    }
                    Toast.makeText(this@AdicionarPacienteActivity, "Solicitação enivada com sucesso.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@AdicionarPacienteActivity, "O campo do código não pode ser vazio", Toast.LENGTH_SHORT).show()
            }
        }catch (e : Exception){

            Toast.makeText(this@AdicionarPacienteActivity, "Houve um erro ao tentar enviar a solicitação, tente mais tarde.", Toast.LENGTH_SHORT).show()
            Log.e("eviarSolicitacao", e.message.toString())

        }
    }

    private fun usuarioEstaLogado(){
        if(!AuthUtil.usuarioEstaLogado()){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}