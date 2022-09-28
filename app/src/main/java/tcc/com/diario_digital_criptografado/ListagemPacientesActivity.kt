package tcc.com.diario_digital_criptografado

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.header_navigation_drawer.*
import tcc.com.diario_digital_criptografado.adapter.PacienteAdapter
import tcc.com.diario_digital_criptografado.models.Usuario
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

        setNavigationDrawer()

        recyclerView = recycler_pacientes
        recycler_pacientes.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        pacienteList = arrayListOf<Usuario>()

        getUsuarioData()
    }

    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->

    private fun getUsuarioData() {
        database = FirebaseDatabase.getInstance().getReference("users")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pacienteList.clear()
                if(snapshot.exists()){
                    for(item in snapshot.children){
                        val itemData = item.getValue(Usuario::class.java)
                        if(itemData!!.tipo_perfil == "Usuário do diário") pacienteList.add(itemData)
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
                    })
                    val nomeusuario = "<b>" + snapshot.child(AuthUtil.getCurrentUser()!!).child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                    nav_header_nome_usuario.setText(Html.fromHtml(nomeusuario))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean{

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
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
                R.id.nav_adicionar_paciente -> Toast.makeText(this, "acessar adicionar paciente", Toast.LENGTH_SHORT).show()
                R.id.nav_logout_psicologo -> showDialogLogOut()
            }
            true
        }

    }

    private fun goEditUser(){
        intent = Intent(this, EditarPerfilUsuarioActivity::class.java)
        intent.putExtra("tipoUsuario", "Psicólogo")
        startActivity(intent)
    }

    private fun showDialogLogOut(){
        auth = Firebase.auth
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
        startActivity(intent)
        finish()
    }
}