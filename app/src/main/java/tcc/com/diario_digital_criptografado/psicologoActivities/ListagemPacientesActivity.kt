package tcc.com.diario_digital_criptografado.psicologoActivities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_listagem_pacientes.*
import tcc.com.diario_digital_criptografado.AgendaUsuarioActivity
import tcc.com.diario_digital_criptografado.EditarPerfilUsuarioActivity
import tcc.com.diario_digital_criptografado.MainActivity
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.adapter.PacienteAdapter
import tcc.com.diario_digital_criptografado.model.Usuario
import tcc.com.diario_digital_criptografado.util.AuthUtil
import java.security.spec.ECField

class ListagemPacientesActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView : RecyclerView
    private lateinit var pacienteList : ArrayList<Usuario>
    private lateinit var database : DatabaseReference
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listagem_pacientes)

        setNavigationDrawer()
        setHeaderNavigationDrawer()

        recyclerView = recycler_pacientes
        recycler_pacientes.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        pacienteList = arrayListOf<Usuario>()

        getUsuarioData()
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

    private fun getUsuarioData2() {
        database = FirebaseDatabase.getInstance().getReference("users")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pacienteList.clear()
                if(snapshot.exists()){
                    for(item in snapshot.children){
                        val itemData = item.getValue(Usuario::class.java)
                        if(itemData!!.tipo_perfil == "Usuário do diário" && item.child("codigo_psicologo").value.toString() == AuthUtil.getCurrentUser())
                            pacienteList.add(itemData)
                    }
                    var adapter = PacienteAdapter(pacienteList)
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object : PacienteAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val clickedItem = pacienteList[position]
                            adapter.notifyItemChanged(position)
                            val intent = Intent(getApplicationContext(), AgendaUsuarioActivity::class.java)
                            intent.putExtra("email", clickedItem.email)
                            startActivity(intent)
                        }

                        override fun onItemLongClickListener(position: Int) {
                            val clickedItem = pacienteList[position]
                            adapter.notifyItemChanged(position)
                            val email = clickedItem.email
                            val dialogBuilder = AlertDialog.Builder(this@ListagemPacientesActivity)
                            dialogBuilder.setMessage("Deseja excluir o paciente?")
                                .setPositiveButton("Sim", { dialog, id ->  excluirPaciente(email) })
                                .setNegativeButton("Não", { dialog, id ->  dialog.dismiss() })
                            val b = dialogBuilder.create()
                            b.show()

                        }
                    })

                    val nomeUsuario = "<b>" + snapshot.child(AuthUtil.getCurrentUser()!!).child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                    val navigationView : NavigationView  = findViewById(R.id.navigation_view_listagem_pacientes)
                    val headerView : View = navigationView.getHeaderView(0)
                    val navNomeusuario : TextView = headerView.findViewById(R.id.nav_header_nome_usuario)
                    navNomeusuario.text = Html.fromHtml(nomeUsuario)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getUsuarioData(){
        try{
            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                pacienteList.clear()
                if(it.exists()){
                    for(item in it.children){
                        val itemData = item.getValue(Usuario::class.java)
                        if(itemData!!.tipo_perfil == "Usuário do diário" && item.child("codigo_psicologo").value.toString() == AuthUtil.getCurrentUser())
                            pacienteList.add(itemData)
                    }
                    var adapter = PacienteAdapter(pacienteList)
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object : PacienteAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val clickedItem = pacienteList[position]
                            adapter.notifyItemChanged(position)
                            val intent = Intent(getApplicationContext(), AgendaUsuarioActivity::class.java)
                            intent.putExtra("email", clickedItem.email)
                            startActivity(intent)
                        }

                        override fun onItemLongClickListener(position: Int) {
                            val clickedItem = pacienteList[position]
                            adapter.notifyItemChanged(position)
                            val email = clickedItem.email
                            val dialogBuilder = AlertDialog.Builder(this@ListagemPacientesActivity)
                            dialogBuilder.setMessage("Deseja excluir o paciente?")
                                .setPositiveButton("Sim", { dialog, id ->  excluirPaciente(email) })
                                .setNegativeButton("Não", { dialog, id ->  dialog.dismiss() })
                            val b = dialogBuilder.create()
                            b.show()

                        }
                    })
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
            database.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nomeUsuario = "<b>" + snapshot.child(AuthUtil.getCurrentUser()!!).child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                    Toast.makeText(this@ListagemPacientesActivity, nomeUsuario, Toast.LENGTH_SHORT).show()
                    val navigationView : NavigationView  = findViewById(R.id.navigation_view_listagem_pacientes)
                    val headerView : View = navigationView.getHeaderView(0)
                    val navNomeusuario : TextView = headerView.findViewById(R.id.nav_header_nome_usuario)
                    navNomeusuario.text = Html.fromHtml(nomeUsuario)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
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
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_acesso_perfil_psicologo -> goEditUser()
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

    private fun goEditUser(){
        intent = Intent(this, EditarPerfilUsuarioActivity::class.java)
        intent.putExtra("tipoUsuario", "Psicólogo")
        startActivity(intent)
    }

    private fun showDialogLogOut(){
        val dialogBuilder = AlertDialog.Builder(this@ListagemPacientesActivity)
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun updateUserData2(){
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(AuthUtil.getCurrentUser()!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nomeUsuario = "<b>" + snapshot.child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                val navigationView : NavigationView  = findViewById(R.id.navigation_view_listagem_pacientes)
                val headerView : View = navigationView.getHeaderView(0)
                val navNomeusuario : TextView = headerView.findViewById(R.id.nav_header_nome_usuario)
                navNomeusuario.text = Html.fromHtml(nomeUsuario)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListagemPacientesActivity, "Houve um erro na atualização dos dados.", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@ListagemPacientesActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateUserData(){

        try {
            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                val nomeUsuario = "<b>" + it.child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                val navigationView : NavigationView  = findViewById(R.id.navigation_view_listagem_pacientes)
                val headerView : View = navigationView.getHeaderView(0)
                val navNomeusuario : TextView = headerView.findViewById(R.id.nav_header_nome_usuario)
                navNomeusuario.text = Html.fromHtml(nomeUsuario)
            }.addOnFailureListener {
                Toast.makeText(this@ListagemPacientesActivity, "Houve um erro na atualização dos dados.", Toast.LENGTH_SHORT).show()
            }
        }catch (e : Exception){
            Toast.makeText(this@ListagemPacientesActivity, "Erro ao atualizar dados.", Toast.LENGTH_SHORT).show()
            Log.e("updateUserData", e.message.toString())
        }
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
}