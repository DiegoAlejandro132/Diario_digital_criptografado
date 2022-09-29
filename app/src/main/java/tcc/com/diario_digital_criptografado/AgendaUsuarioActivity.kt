package tcc.com.diario_digital_criptografado

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        verifyUser()
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

            var formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
            } else {
                TODO("VERSION.SDK_INT < O")
            }

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

    private fun verifyUser(){
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
            if(it.exists()){
                tipoUsuario = it.child("tipo_perfil").value.toString()
                val nomeUsuario = "<b>" + it.child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                val navigationView : NavigationView  = findViewById(R.id.navigation_view_agenda)
                val headerView : View = navigationView.getHeaderView(0)
                val navNomeusuario : TextView = headerView.findViewById(R.id.nav_header_nome_usuario)
                navNomeusuario.text = Html.fromHtml(nomeUsuario)
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
                R.id.nav_acesso_perfil_usuario -> goEditUser()
                R.id.nav_acesso_meu_psicologo -> Toast.makeText(this, "acessar meu psicologo", Toast.LENGTH_SHORT).show()
                R.id.nav_acesso_solicitacoes -> Toast.makeText(this, "acessar solicitações", Toast.LENGTH_SHORT).show()
                R.id.nav_logout_usuario -> showDialogLogOut()
                //opções de acesso do psicologo
                R.id.nav_acesso_perfil_psicologo -> goEditUser()
                R.id.nav_adicionar_paciente -> Toast.makeText(this, "acessar adicionar paciente", Toast.LENGTH_SHORT).show()
                R.id.nav_logout_psicologo -> showDialogLogOut()
            }
            true
        }
    }
    private fun showDialogLogOut(){
        auth = Firebase.auth
        val dialogBuilder = AlertDialog.Builder(this@AgendaUsuarioActivity)
        dialogBuilder.setMessage("Você deseja mesmo fazer log out? Ao tentar entrar novamente precisara realizar novo log in.")
            .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, id ->  signOut() })
            .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, id ->  dialog.dismiss()})
        val b = dialogBuilder.create()
        b.show()
    }

    private fun signOut(){
        Firebase.auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun updateUserData(){
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(AuthUtil.getCurrentUser()!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nomeUsuario = "<b>" + snapshot.child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                val navigationView : NavigationView  = findViewById(R.id.navigation_view_agenda)
                val headerView : View = navigationView.getHeaderView(0)
                val navNomeusuario : TextView = headerView.findViewById(R.id.nav_header_nome_usuario)
                navNomeusuario.text = Html.fromHtml(nomeUsuario)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AgendaUsuarioActivity, "Houve um erro na atualização dos dados.", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@AgendaUsuarioActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun goEditUser(){
        intent = Intent(this, EditarPerfilUsuarioActivity::class.java)
        intent.putExtra("tipoUsuario", tipoUsuario)
        startActivity(intent)
    }

}