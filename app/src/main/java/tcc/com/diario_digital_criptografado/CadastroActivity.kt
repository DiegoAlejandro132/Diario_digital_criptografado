package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_cadastro.*
import tcc.com.diario_digital_criptografado.model.Psicologo
import tcc.com.diario_digital_criptografado.model.Usuario
import tcc.com.diario_digital_criptografado.util.AuthUtil

class CadastroActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    private var tipo_perfil : String? = null
    private var genero : String? = null
    private var estado_registro : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)


        //seta todos os adapters dos spinners
        setAdapters()

        //usuario volta pra tela de login ao clicar em cancelar
        btn_cancelar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        //Cadastrar usuario
        btn_cadastrar.setOnClickListener {
            if(validateForm()){
                if(validatePassword()){
                    createUser()
                }else{
                    Toast.makeText(this@CadastroActivity, "As senhas tem que ser iguais", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@CadastroActivity, "Preencha todos os campos do formulario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->


    //função que cria a conta do usuario e do seu tipo de cadastro
    private fun createUser(){
        val usuario = setUserData()
        val psicologo = setPsicologoData()

        auth.createUserWithEmailAndPassword(txt_email.text.toString(), txt_senha.text.toString()).addOnCompleteListener(this) {task ->
            if(task.isSuccessful){
                if(tipo_perfil == "Usuário do diário") {
                    writeUserDatabase(usuario)
                    Toast.makeText(
                        this@CadastroActivity,
                        "Usuario cadastrado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }else{
                    if(tipo_perfil == "Psicólogo"){
                        writePsicologoDatabase(psicologo)
                        Toast.makeText(
                            this@CadastroActivity,
                            "Psicólogo cadastrado com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }else{
                        Toast.makeText(
                            this@CadastroActivity,
                            "Não foi possivel criar a conta, tente novamente mais tarde",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }else{
                Toast.makeText(this@CadastroActivity, "Usuário ja possui cadastro no sistema", Toast.LENGTH_LONG).show()
            }
        }
    }

    //função que garante que os dados do formulario estejam validos
    private fun validateForm () : Boolean {
        if ((txt_cpf.text.toString() != "" && txt_cpf != null)
            && (txt_email.text.toString() != "" && txt_email != null)
            && (txt_data_nascimento.text.toString() != "" && txt_data_nascimento != null)
            && (txt_nome.text.toString() != "" && txt_nome != null)
            && (txt_telefone.text.toString() != "" && txt_telefone != null)
            && (genero != "" && genero != "Selecione" && genero != null)
            && (tipo_perfil != null && tipo_perfil != "" && tipo_perfil != "Selecione")
            && (txt_senha.text.toString() != "" && txt_confirmar_senha != null)
        ) {
            if (tipo_perfil == "Psicólogo") {
                return estado_registro != "Selecione" && txt_numero_registro.text.toString() != "" && txt_numero_registro != null
            }
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
                tipo_perfil = p0?.getItemAtPosition(p2).toString()
                if(tipo_perfil == "Psicólogo"){
                    lbl_numero_registro.setVisibility(View.VISIBLE)
                    spn_regiao.setVisibility(View.VISIBLE)
                    lbl_estado_regiao.setVisibility(View.VISIBLE)
                    txt_numero_registro.setVisibility(View.VISIBLE)
                }else{
                    if((tipo_perfil == "Usuário do diário") || (tipo_perfil == "Selecione")){
                        lbl_numero_registro.setVisibility(View.GONE)
                        spn_regiao.setVisibility(View.GONE)
                        lbl_estado_regiao.setVisibility(View.GONE)
                        txt_numero_registro.setVisibility(View.GONE)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }


    //funções para setar o spinner do tipo de perfil
    private fun setEstadoRegiaoAdapter(){
        ArrayAdapter.createFromResource(this, R.array.estados_registro, android.R.layout.simple_list_item_1)
            .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spn_regiao.adapter = adapter
            }
        setEstadoRegiaoOnItemSelected()
    }
    private fun setEstadoRegiaoOnItemSelected(){
        spn_regiao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                estado_registro = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    //função pra unir todos os spinners
    private fun setAdapters(){
        setGeneroAdapter()
        setTipoCadastroAdapter()
        setEstadoRegiaoAdapter()
    }

    //funções para criar usuario novo no banco
    private fun setUserData() : Usuario {
        val usuario = Usuario()
        usuario.cpf = txt_cpf.text.toString()
        usuario.data_nascimento = txt_data_nascimento.text.toString()
        usuario.email = txt_email.text.toString()
        usuario.telefone = txt_telefone.text.toString()
        usuario.nome = txt_nome.text.toString()
        usuario.genero = genero.toString()
        usuario.tipo_perfil = tipo_perfil.toString()
        usuario.tem_psicologo = false
        usuario.tem_solicitacao = false
        usuario.codigo_psicologo = ""
        usuario.codigo_psicologo_solicitacao = ""

        return usuario
    }
    private fun writeUserDatabase(usuario: Usuario) {

        try{
            val userUid = AuthUtil.getCurrentUser()
            val child = "users/$userUid"
            database.child(child).setValue(usuario)
        }catch (e:Exception){
            Toast.makeText(this@CadastroActivity, "Erro ao cadastrar usuário.", Toast.LENGTH_SHORT).show()
            Log.e("writeUserDatabase", e.message.toString())
        }
    }


    //funções para criar psicologo novo no banco de dados
    private fun setPsicologoData() : Psicologo {
        val psicologo = Psicologo()
        psicologo.cpf = txt_cpf.text.toString()
        psicologo.data_nascimento = txt_data_nascimento.text.toString()
        psicologo.email = txt_email.text.toString()
        psicologo.telefone = txt_telefone.text.toString()
        psicologo.nome = txt_nome.text.toString()
        psicologo.sexo = genero.toString()
        psicologo.tipo_perfil = tipo_perfil.toString()
        psicologo.numero_registro = txt_numero_registro.text.toString()
        psicologo.estado_registro = estado_registro.toString()

        return psicologo
    }
    private fun writePsicologoDatabase(psicologo: Psicologo) {

        try{

            val userUid = AuthUtil.getCurrentUser()
            val child = "users/$userUid"
            database.child(child).setValue(psicologo)

        }catch (e:Exception){
            Toast.makeText(this@CadastroActivity, "Erro ao cadastrar psicólogo.", Toast.LENGTH_SHORT).show()
            Log.e("writePsicologoDatabase", e.message.toString())
        }
    }

}