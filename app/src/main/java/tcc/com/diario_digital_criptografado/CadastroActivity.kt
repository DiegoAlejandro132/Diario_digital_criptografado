package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_agenda_usuario.*
import kotlinx.android.synthetic.main.activity_cadastro.*
import kotlinx.android.synthetic.main.activity_cadastro.txt_email
import kotlinx.android.synthetic.main.activity_cadastro.txt_senha
import kotlinx.android.synthetic.main.activity_main.*
import tcc.com.diario_digital_criptografado.model.Psicologo
import tcc.com.diario_digital_criptografado.model.Usuario
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ValidationUtil
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CadastroActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    private var tipo_perfil : String? = null
    private var genero : String? = null
    private var estado_registro : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        supportActionBar?.title = "Cadastro"

        //seta todos os adapters dos spinners
        setAdapters()

        txt_data_nascimento.setOnClickListener{

            selecionarDataNascimento()

        }

        //usuario volta pra tela de login ao clicar em cancelar
        btn_cancelar.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))
        }

        //Cadastrar usuario
        btn_cadastrar.setOnClickListener {
            if(validarCadastro()){
                linear_layout_conteudo_cadastrar.visibility = View.GONE
                progressive_cadastro.isVisible = true
                createUser()
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
        auth.createUserWithEmailAndPassword(txt_email.text.toString(), txt_senha.text.toString()).addOnCompleteListener(this) {task ->
            if(task.isSuccessful){
                if(tipo_perfil == "Usuário do diário") {
                    criarUsuario()
                    Toast.makeText(this@CadastroActivity, "Usuario cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                    Firebase.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(this@CadastroActivity, "Email de confirmação enviado para ${txt_email.text}", Toast.LENGTH_LONG).show()
                        }
                    }
                    finish()
                }else{
                    if(tipo_perfil == "Psicólogo"){
                        writePsicologoDatabase()
                        Toast.makeText(this@CadastroActivity, "Psicólogo cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                        Firebase.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{
                            if(it.isSuccessful){
                                Toast.makeText(this@CadastroActivity, "Email de confirmação enviado para ${txt_email.text}", Toast.LENGTH_LONG).show()
                            }
                        }
                        finish()
                    }else{
                        linear_layout_conteudo_cadastrar.isVisible = true
                        progressive_cadastro.visibility = View.GONE
                        Toast.makeText(this@CadastroActivity, "Não foi possivel criar a conta psicologo", Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                try {
                    linear_layout_conteudo_cadastrar.isVisible = true
                    progressive_cadastro.visibility = View.GONE
                    throw task.exception!!
                }catch (e:FirebaseNetworkException){
                    Log.e("criar usuario", e.message.toString())
                    Snackbar.make(btn_cadastrar, "Verifique a conexão com a internet e tente mais tarde", Snackbar.LENGTH_LONG).show()
                }catch (e:FirebaseAuthUserCollisionException){
                    Log.e("criar usuario", e.message.toString())
                    Snackbar.make(btn_cadastrar, "O email inserido ja está em uso", Snackbar.LENGTH_LONG).show()
                }catch (e:Exception){
                    Log.e("criar usuario", e.toString())
                    Snackbar.make(btn_cadastrar, "Houve um erro inesperado, por favor tente mais tarde", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    //função que garante que os dados do formulario estejam validos
    private fun validarCadastro() : Boolean{

        validarNome()
        validarCpf()
        validarSenhas()
        validarSpinners()
        validarEmail()
        validarDataNascimento()
        validarTelefone()
        if(tipo_perfil == "Psicólogo"){
            validarPsicologo()
        }

        return validarNome() && validarCpf() && validarSenhas() && validarSpinners()
                && validarEmail() && validarDataNascimento() && if(tipo_perfil == "Psicólogo") validarPsicologo() else true
    }

    private fun validarNome() : Boolean{
        val valido = txt_nome.text.toString() != "" && txt_nome.text[0].toString() != " "
                && txt_nome.text[txt_nome.text.length - 1].toString() != " " && "1" !in txt_nome.text
                && "2" !in txt_nome.text && "3" !in txt_nome.text && "4" !in txt_nome.text
                && "5" !in txt_nome.text && "6" !in txt_nome.text && "7" !in txt_nome.text
                && "8" !in txt_nome.text && "9" !in txt_nome.text

        if(!valido)
            lbl_advertencia_cadastrar_usuario_nome.isVisible = true
        else
            lbl_advertencia_cadastrar_usuario_nome.visibility = View.GONE

        return valido
    }

    private fun validarSpinners() : Boolean{
        val valido = genero != "" && genero != "Selecione" && tipo_perfil != "" && tipo_perfil != "Selecione"

        if(valido)
            lbl_advertencia_cadastrar_usuario_spiners.visibility = View.GONE
        else
            lbl_advertencia_cadastrar_usuario_spiners.isVisible = true

        return valido
    }

    private fun validarEmail() : Boolean{
        val email = txt_email.text.toString()
        val valido = " " !in email && "@" in email && ".com" in email

        if(valido)
            lbl_advertencia_cadastrar_usuario_email.visibility = View.GONE
        else
            lbl_advertencia_cadastrar_usuario_email.isVisible = true

        return valido
    }

    private fun validarSenhas() : Boolean{
        val senha = txt_senha.text.toString()
        val confirmarSenha = txt_confirmar_senha.text.toString()

        val senha1Valida = senha != "" && " " !in senha && senha.length >= 6
        val senhasIguais = senha.equals(confirmarSenha)

        if(senha1Valida)
            linear_layout_cadastrar_senha_invalida.visibility = View.GONE
        else
            linear_layout_cadastrar_senha_invalida.isVisible = true

        if(senhasIguais)
            lbl_advertencia_cadastrar_usuario_senhas.visibility = View.GONE
        else
            lbl_advertencia_cadastrar_usuario_senhas.isVisible = true

        return senha1Valida && senhasIguais
    }

    private fun validarPsicologo() : Boolean{
        var valido = true
        if (tipo_perfil == "Psicólogo"){
            val numeroRegistro = txt_numero_registro.text.toString()
            valido = numeroRegistro != "" && " " !in numeroRegistro
                    && estado_registro != "Selecione"
        }

        if(valido)
            lbl_advertencia_cadastrar_usuario_psicologo.visibility = View.GONE
        else
            lbl_advertencia_cadastrar_usuario_psicologo.isVisible = true

        return valido
    }

    private fun validarDataNascimento() : Boolean{
        val dataNascimento = txt_data_nascimento.text.toString()
        val valido = dataNascimento != ""

        if(valido)
            lbl_advertencia_cadastrar_usuario_data_nascimento.visibility = View.GONE
        else {
            lbl_advertencia_cadastrar_usuario_data_nascimento.isVisible = true
        }

        return valido
    }

    private fun validarCpf() : Boolean{
        val valido = txt_cpf.text.toString() != "" && txt_cpf.text!![0].toString() != " "
                && txt_cpf.text.toString().length == 14

        var cpf = txt_cpf.text.toString().replace(".", "").replace("-", "")
        var cpfValido = ""

        var i = 10
        var total1 = 0
        for(n in cpf){
            cpfValido += n
            total1 += (n.toString().toInt() * i)
            i--
            if(i == 1){
                break
            }
        }
        val totalPrimeiroDigito = total1 % 11
        val primeiroDigito = if(totalPrimeiroDigito < 2) 0 else 11 - totalPrimeiroDigito
        cpfValido += primeiroDigito.toString()


        var j = 11
        var total2 = 0
        for(n in cpf){
            total2 += (n.toString().toInt() * j)
            j--
            if(j == 1){
                break
            }
        }
        val totalSegundoDigito = total2 % 11
        val segundoDigito = if(totalSegundoDigito < 2) 0 else 11 - totalSegundoDigito

        cpfValido += segundoDigito

        if(valido && cpf == cpfValido){
            lbl_advertencia_cpf_invalido.visibility = View.GONE
        }else{
            lbl_advertencia_cpf_invalido.isVisible = true
        }

        return valido && cpf == cpfValido
    }

    private fun validarTelefone() : Boolean{
        val telefone = txt_telefone.text.toString().replace("(", "").replace(")", "")
        val valido = telefone != "" && telefone.length == 11

        if(valido)
            lbl_advertencia_cadastrar_usuario_telefone.visibility = View.GONE
        else
            lbl_advertencia_cadastrar_usuario_telefone.isVisible = true

        return valido
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

                    linear_layout_cadastro_psicologo.isVisible = true

                }else{
                    if((tipo_perfil == "Usuário do diário") || (tipo_perfil == "Selecione")){

                        linear_layout_cadastro_psicologo.visibility = View.GONE

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

    private fun criarUsuario() {

        try{

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
            usuario.foto_perfil = ""

            val userUid = AuthUtil.getCurrentUser()
            val child = "users/$userUid"
            database.child(child).setValue(usuario)
        }catch (e:Exception){
            Toast.makeText(this@CadastroActivity, "Erro ao cadastrar usuário.", Toast.LENGTH_SHORT).show()
            Log.e("writeUserDatabase", e.message.toString())
        }
    }


    private fun writePsicologoDatabase() {

        try{

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
            psicologo.foto_perfil = ""

            val userUid = AuthUtil.getCurrentUser()
            val child = "users/$userUid"
            database.child(child).setValue(psicologo)

        }catch (e:Exception){
            Toast.makeText(this@CadastroActivity, "Erro ao cadastrar psicólogo.", Toast.LENGTH_SHORT).show()
            Log.e("writePsicologoDatabase", e.message.toString())
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
                txt_data_nascimento.text = dataCerta.toString()
                lbl_advertencia_cadastrar_usuario_menor_de_idade.visibility = View.GONE
            }else {
                lbl_advertencia_cadastrar_usuario_menor_de_idade.isVisible = true
            }
        }


    }

}