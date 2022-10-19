package tcc.com.diario_digital_criptografado.psicologoActivities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_listagem_pacientes.*
import kotlinx.android.synthetic.main.activity_meu_perfil.*
import kotlinx.android.synthetic.main.header_navigation_drawer.*
import tcc.com.diario_digital_criptografado.*
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.adapter.PacienteAdapter
import tcc.com.diario_digital_criptografado.model.Usuario
import tcc.com.diario_digital_criptografado.usuarioActivities.AgendaUsuarioActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil

class ListagemPacientesActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView : RecyclerView
    private lateinit var pacienteList : ArrayList<Usuario>
    private lateinit var database : DatabaseReference
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listagem_pacientes)

        usuarioEstaLogado()

        supportActionBar?.title = "Meus pacientes"

        setNavigationDrawer()
        setHeaderNavigationDrawer()

        recyclerView = recycler_pacientes
        recycler_pacientes.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        pacienteList = arrayListOf<Usuario>()

        listarDadosPacientes()

        swipe_listagem_pacientes.setOnRefreshListener {
            listarDadosPacientes()
        }
    }

    override fun onResume() {
        super.onResume()
        setHeaderNavigationDrawer()
        listarDadosPacientes()
    }

    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->


    private fun listarDadosPacientes(){
        try{

            val storageReference = FirebaseStorage.getInstance().getReference("fotos_perfil/${AuthUtil.getCurrentUser()}")
            storageReference.downloadUrl.addOnSuccessListener {
                if(!(it == null || it.toString() == ""))
                    Glide.with(this).load(it).into(nav_header_foto_perfil)
            }.addOnFailureListener {
                Toast.makeText(this, "Não foi posivel carregar a foto de perfil.", Toast.LENGTH_SHORT).show()
            }

            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                pacienteList.clear()
                if(it.exists()){
                    for(item in it.children){
                        val itemData = item.getValue(Usuario::class.java)
                        if(itemData!!.tipo_perfil == "Usuário do diário" && item.child("codigo_psicologo").value.toString() == AuthUtil.getCurrentUser())
                            pacienteList.add(itemData)
                    }
                    if(pacienteList.size == 0)
                        lbl_sem_pacientes.isVisible = true
                    else
                        lbl_sem_pacientes.visibility = View.GONE

                    var adapter = PacienteAdapter(this@ListagemPacientesActivity , pacienteList,)
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object : PacienteAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val clickedItem = pacienteList[position]
                            adapter.notifyItemChanged(position)
                            val intent = Intent(getApplicationContext(), AgendaUsuarioActivity::class.java)
                            intent.putExtra("email", clickedItem.email)
                            startActivity(intent)
                        }

                        override fun excluirPaciente(position: Int) {
                            val clickedItem = pacienteList[position]
                            val email = clickedItem.email

                            adapter.notifyItemChanged(position)
                            pacienteList.removeAt(position)
                            excluirPaciente(email)

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

            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                val nomeUsuario = it.child(AuthUtil.getCurrentUser()!!).child("nome").value.toString().replaceFirstChar { it.toUpperCase() }
                val emailUsuario = it.child(AuthUtil.getCurrentUser()!!).child("email").value.toString()

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
                R.id.nav_adicionar_paciente -> goAdicionarPaciente()
                R.id.nav_logout_psicologo -> showDialogLogOut()
            }
            true
        }

    }

    private fun goAdicionarPaciente() {
        intent = Intent(this, AdicionarPacienteActivity::class.java)
        startActivity(intent)
    }

    private fun irVisualizarPerfil(){
        intent = Intent(this, MeuPerfilActivity::class.java)
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