package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_cadastro.*
import tcc.com.diario_digital_criptografado.MainActivity
import tcc.com.diario_digital_criptografado.R
import tcc.com.diariodigitalcriptografado.models.Usuario

class CadastroActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private var tipo_perfil : Int? = null
    private var genero : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        setGeneroAdapter()
        setTipoCadastroAdapter()


        btn_cancelar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        btn_cadastrar.setOnClickListener {
            val usuario = setUserData()

            if(validateForm()){
                if(validatePassword()){
                    auth.createUserWithEmailAndPassword(usuario.email, txt_senha.text.toString()).addOnCompleteListener(this) {task ->
                        if(task.isSuccessful){
                            writeUserDatabase(usuario)
                            Toast.makeText(this@CadastroActivity, "Usuario Cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        }else{
                            Toast.makeText(this@CadastroActivity, "Usuário ja possui cadastro no sistema", Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this@CadastroActivity, "As senhas tem que ser iguais", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@CadastroActivity, "Preencha todos os campos do formulario", Toast.LENGTH_SHORT).show()
            }
        }
    }



    //função que garante que os dados do formulario estejam validos
    private fun validateForm () : Boolean{
        if((txt_cpf.text.toString() != "" && txt_cpf.text.toString() != null)
            && (txt_email.text.toString() != "" &&  txt_email.text.toString() != null)
            && (txt_data_nascimento.text.toString() != "" && txt_data_nascimento.text.toString() != null)
            && (txt_nome.text.toString() != "" && txt_nome.text.toString() != null)
            && (txt_telefone.text.toString() != "" && txt_telefone.text.toString() != null)
            && (genero != "" && genero != null)
            && (tipo_perfil != null)
            && (txt_senha.text.toString() != "" && txt_confirmar_senha.text.toString() != null))
        {
            return true
        }
        return false
    }
    private fun validatePassword() : Boolean{
        if(txt_senha.text.toString().equals(txt_confirmar_senha.text.toString())){
            return true
        }
        return false
    }

    //funões para setar o spinner de genero
    private fun setGeneroAdapter(){
        ArrayAdapter.createFromResource(this, R.array.array_genero, android.R.layout.simple_list_item_1)
            .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spn_genero.adapter = adapter
            }
        setGeneroOnItemSelected()
    }
    private fun setGeneroOnItemSelected(){
        spn_genero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                genero = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }
    //funções para setar o spinner do tipo de perfil
    private fun setTipoCadastroAdapter(){
        ArrayAdapter.createFromResource(this, R.array.array_tipo_perfil, android.R.layout.simple_list_item_1)
            .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spn_tipo_perfil.adapter = adapter
            }
        setTipoCadastroOnItemSelected()
    }
    private fun setTipoCadastroOnItemSelected(){
        spn_tipo_perfil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                tipo_perfil = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    //funções para criar usuario novo no banco
    private fun setUserData() : Usuario {
        val nome = txt_nome.text.toString()
        val cpf = txt_cpf.text.toString()
        val data_nascimento = txt_data_nascimento.text.toString()
        val telefone = txt_telefone.text.toString()
        val senha = txt_senha.text.toString()
        val email = txt_email.text.toString()


        val usuario = Usuario()
        usuario.cpf = cpf
        usuario.data_nascimento = data_nascimento
        usuario.email = email
        usuario.telefone = telefone
        usuario.nome = nome
        usuario.genero = genero.toString()
        usuario.id_perfil = tipo_perfil.toString().toInt()

        return usuario
    }
    private fun writeUserDatabase(usuario: Usuario) {
        val userUid = getCurrentUser()
        val child = "user/$userUid"
        database.reference.child(child).setValue(usuario)
    }
    private fun getCurrentUser(): String? {
        val usuario = auth.currentUser;
        return if (usuario !== null)
            usuario.uid;
        else
            null;
    }
}