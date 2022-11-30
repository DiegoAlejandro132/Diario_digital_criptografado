package tcc.com.diario_digital_criptografado.psicologoActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.activity_listagem_pacientes.*
import kotlinx.android.synthetic.main.header_navigation_drawer.*
import tcc.com.diario_digital_criptografado.*
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.adapter.PacienteAdapter
import tcc.com.diario_digital_criptografado.model.Usuario
import tcc.com.diario_digital_criptografado.usuarioActivities.AgendaUsuarioActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil
import tcc.com.diario_digital_criptografado.util.FotoUtil

class ListagemPacientesActivity : AppCompatActivity(){
    private lateinit var recyclerView : RecyclerView
    private lateinit var pacienteList : ArrayList<Usuario>
    private lateinit var database : DatabaseReference
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listagem_pacientes)

        supportActionBar?.title = "Meus pacientes"


        setNavigationDrawer()

        usuarioEstaLogado()

        if(ConexaoUtil.estaConectado(this)){
            setHeaderNavigationDrawer()
            FotoUtil.definirFotoPerfil()
            listarDadosPacientes()
        }else{
            Snackbar.make(textView12, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
        }

        recyclerView = recycler_pacientes
        recycler_pacientes.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        pacienteList = arrayListOf<Usuario>()


        swipe_listagem_pacientes.setOnRefreshListener {
            if(ConexaoUtil.estaConectado(this)){
                listarDadosPacientes()
            }else{
                Snackbar.make(textView12, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
                swipe_listagem_pacientes.isRefreshing = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(ConexaoUtil.estaConectado(this)){
            setHeaderNavigationDrawer()
            FotoUtil.definirFotoPerfil()
            listarDadosPacientes()
        }else{
            Snackbar.make(textView12, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
        }
    }

    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->


    private fun listarDadosPacientes(){
        try{

            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {

                pacienteList.clear()
                if(it.exists()){
                    for(item in it.children){
                        val itemData = item.getValue(Usuario::class.java)
                        if(itemData!!.tipo_perfil == "Usuário do diário" && item.child("codigo_psicologo").value.toString() == AuthUtil.getCurrentUser()
                            && item.child("tem_psicologo").value == true)
                            pacienteList.add(itemData)
                    }
                    if(pacienteList.size == 0)
                        lbl_sem_pacientes.isVisible = true
                    else
                        lbl_sem_pacientes.visibility = View.GONE

                    var adapter = PacienteAdapter(this@ListagemPacientesActivity , pacienteList,)
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object : PacienteAdapter.onItemClickListener{
                        override fun selecionarPaciente(position: Int) {
                            if(ConexaoUtil.estaConectado(this@ListagemPacientesActivity)){
                                val clickedItem = pacienteList[position]
                                adapter.notifyItemChanged(position)
                                val intent = Intent(this@ListagemPacientesActivity, AgendaUsuarioActivity::class.java)
                                intent.putExtra("email", clickedItem.email)
                                startActivity(intent)
                            }else{
                                Snackbar.make(textView12, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
                            }

                        }

                        override fun excluirPaciente(position: Int) {
                            try{
                                if(ConexaoUtil.estaConectado(this@ListagemPacientesActivity)){
                                    val clickedItem = pacienteList[position]
                                    val email = clickedItem.email

                                    excluirPaciente(email)
                                    pacienteList.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                }else{
                                    Snackbar.make(textView12, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
                                }
                            }catch (e:Exception){
                                Log.e("erronoadapter", e.message.toString())
                            }

                        }
                    })

                    if(swipe_listagem_pacientes.isRefreshing){
                        swipe_listagem_pacientes.isRefreshing = false
                    }
                }
            }
        }catch (e : Exception){
            Log.e("getUsuarioData", e.message.toString())
        }
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean{

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setHeaderNavigationDrawer(){

        try {
            database = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!)
            database.get().addOnSuccessListener {
                val nomeUsuario = it.child("nome").value.toString().replaceFirstChar { it.toUpperCase() }
                val emailUsuario = it.child("email").value.toString()
                val fotoPerfil = it.child("foto_perfil").value.toString()
                if(fotoPerfil != "")
                    Glide.with(applicationContext).load(fotoPerfil.toUri()).into(nav_header_foto_perfil)

                nav_header_nome_usuario.text = nomeUsuario
                nav_header_email_usuario.text = emailUsuario
            }

        }catch (e : Exception){
            Toast.makeText(this@ListagemPacientesActivity, "Erro de dados na barra de menu.", Toast.LENGTH_SHORT).show()
            Log.e("HeaderNavigationDrawer", e.message.toString())
        }

    }

    private fun setNavigationDrawer(){
        val drawerLayout : DrawerLayout = drawer_layout_listagem_pacientes
        val navView : NavigationView = navigation_view_listagem_pacientes
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.itemIconTintList = null
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_acesso_perfil_psicologo -> irVisualizarPerfil()
                R.id.nav_adicionar_paciente -> irAdicionarPaciente()
                R.id.nav_logout_psicologo -> showDialogLogOut()
                R.id.nav_politica_privacidade_psicologo -> irPoliticaPrivacidade()
            }
            true
        }

    }

    private fun irAdicionarPaciente() {
        intent = Intent(this, AdicionarPacienteActivity::class.java)
        startActivity(intent)
    }

    private fun irVisualizarPerfil(){
        intent = Intent(this, MeuPerfilActivity::class.java)
        startActivity(intent)
    }

    private fun irPoliticaPrivacidade(){
        intent = Intent(this, PoliticaDePrivacidadeActivity::class.java)
        startActivity(intent)
    }

    private fun showDialogLogOut(){
        val dialogBuilder = AlertDialog.Builder(this@ListagemPacientesActivity)
        dialogBuilder.setMessage("Deseja encerrar a sessão?")
            .setTitle("Encerrar sessão")
            .setPositiveButton("Sim") { dialog, id ->  signOut() }
            .setNegativeButton("Não") { dialog, id ->  dialog.dismiss()}
        val b = dialogBuilder.create()
        b.show()
    }

    private fun signOut(){
        Firebase.auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }


    private fun excluirPaciente(email : String){

        try{
            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                if(it.exists()){
                    for(user in it.children){
                        if(user.child("email").value.toString() == email){
                            database.child(user.key.toString()).child("codigo_psicologo").setValue("")
                            database.child(user.key.toString()).child("tem_psicologo").setValue(false)
                            database.child(AuthUtil.getCurrentUser()!!).child("pacientes").child(user.key.toString()).setValue(null)
                            break
                        }
                    }

                }
            }
        }catch (e : Exception){
            Log.e("excluirPaciente", e.message.toString())
        }
    }

    private fun usuarioEstaLogado(){
        if(!AuthUtil.usuarioEstaLogado()){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}