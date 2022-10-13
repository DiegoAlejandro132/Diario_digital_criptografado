package tcc.com.diario_digital_criptografado.usuarioActivities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_agenda_usuario.*
import kotlinx.android.synthetic.main.header_navigation_drawer.*
import tcc.com.diario_digital_criptografado.MainActivity
import tcc.com.diario_digital_criptografado.MeuPerfilActivity
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.psicologoActivities.AdicionarPacienteActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.Validation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AgendaUsuarioActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private lateinit var emailUsuarioSelecionado : String
    private lateinit var tipoUsuario : String

    private lateinit var toggle : ActionBarDrawerToggle

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        trazerDadosUsuario()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_usuario)

        setNavigationDrawer()
        //caso o usuario esteja logado, direciona para a sua pagina ao inves de fazer login de novo
        if(!AuthUtil.userIsLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        calendarView_user.setOnDateChangeListener {
                calendarView, i, i2, i3 ->

            var ano = i
            var mes = i2+1
            var dia = i3
            val data = "${dia}-${mes}-${ano}"

            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")


            val diaAtual = LocalDateTime.now()
            val dataFormatada = diaAtual.format(formatter)

            val intentEmail = intent
            emailUsuarioSelecionado = if(tipoUsuario == "Psicólogo"){
                intentEmail.getStringExtra("email").toString()
            }else ""

            if(Validation.validateDateCalendar(data, dataFormatada)){
                val intent = Intent(this, FormularioDiarioActivity::class.java)
                intent.putExtra("dataSelecionada", data)
                if(tipoUsuario == "Psicólogo")
                    intent.putExtra("emailUsuarioSelecionado", emailUsuarioSelecionado)
                startActivity(intent)
            }else{
                Toast.makeText(this@AgendaUsuarioActivity, "Data futura não é permitida", Toast.LENGTH_SHORT).show()
            }
        }

        btn_voltar_agenda.setOnClickListener{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        updateUserData()
    }


    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->

    private fun trazerDadosUsuario(){

        try{
            database = FirebaseDatabase.getInstance().getReference("users")
            database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
                if(it.exists()){
                    tipoUsuario = it.child("tipo_perfil").value.toString()
                    val nomeUsuario = "<b>" + it.child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                    nav_header_nome_usuario.text = Html.fromHtml(nomeUsuario)
                    if(tipoUsuario == "Psicólogo"){
                        val intent = intent
                        emailUsuarioSelecionado = intent.getStringExtra("email").toString()
                        navigation_view_agenda.inflateMenu(R.menu.navigation_drawer_psicologo)
                        btn_voltar_agenda.setVisibility(View.VISIBLE)
                    }else if (tipoUsuario == "Usuário do diário"){
                        emailUsuarioSelecionado = ""
                        navigation_view_agenda.inflateMenu(R.menu.navigation_drawer_usuario)

                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this@AgendaUsuarioActivity, "Erro ao verificar o usuário.", Toast.LENGTH_SHORT).show()
            Log.e("verifyUser", e.message.toString())
        }
    }
    override fun onOptionsItemSelected(item : MenuItem) : Boolean{

       if(toggle.onOptionsItemSelected(item)){
           return true
       }
        return super.onOptionsItemSelected(item)
    }

    private fun setNavigationDrawer(){
        val drawerLayout : DrawerLayout = drawer_layout_agenda_usuario
        val navView : NavigationView = navigation_view_agenda
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                //opções do acesso do usuario comum
                R.id.nav_acesso_perfil_usuario -> irVisualizarPefil()
                R.id.nav_acesso_meu_psicologo -> goMeuPsicologo()
                R.id.nav_acesso_solicitacoes -> goSolicitacoes()
                R.id.nav_logout_usuario -> showDialogLogOut()
                //opções de acesso do psicologo
                R.id.nav_acesso_perfil_psicologo -> irVisualizarPefil()
                R.id.nav_adicionar_paciente -> goAdicionarPaciente()
                R.id.nav_logout_psicologo -> showDialogLogOut()
            }
            true
        }
    }
    private fun showDialogLogOut(){
        val dialogBuilder = AlertDialog.Builder(this@AgendaUsuarioActivity)
        dialogBuilder.setMessage("Deseja encerrar a sessão?")
            .setPositiveButton("Sim") { dialog, id ->  signOut() }
            .setNegativeButton("Não") { dialog, id ->  dialog.dismiss() }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun signOut(){

        try{
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }catch (e:Exception){
            Toast.makeText(this@AgendaUsuarioActivity, "Erro ao sair.", Toast.LENGTH_SHORT).show()
            Log.e("signOut", e.message.toString())
        }
    }

    private fun updateUserData(){

        try{
            database = FirebaseDatabase.getInstance().getReference("users")
            database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
                if(it.exists()){
                    tipoUsuario = it.child("tipo_perfil").value.toString()
                    val nomeUsuario = "<b>" + it.child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                    nav_header_nome_usuario.text = Html.fromHtml(nomeUsuario)
                    if(tipoUsuario == "Psicólogo"){
                        val intent = intent
                        emailUsuarioSelecionado = intent.getStringExtra("email").toString()
                        btn_voltar_agenda.setVisibility(View.VISIBLE)
                    }else if (tipoUsuario == "Usuário do diário"){
                        emailUsuarioSelecionado = ""
                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this@AgendaUsuarioActivity, "Erro ao verificar o usuário.", Toast.LENGTH_SHORT).show()
            Log.e("verifyUser", e.message.toString())
        }
    }

    private fun irVisualizarPefil(){
        intent = Intent(this, MeuPerfilActivity::class.java)
        intent.putExtra("tipoUsuario", tipoUsuario)
        startActivity(intent)
    }

    private fun goSolicitacoes(){
        intent = Intent(this, SolicitacoesActivity::class.java)
        startActivity(intent)
    }

    private fun goMeuPsicologo(){
        intent = Intent(this, MeuPsicologoActivity::class.java)
        startActivity(intent)
    }

    private fun goAdicionarPaciente() {
        intent = Intent(this, AdicionarPacienteActivity::class.java)
        startActivity(intent)
    }


}