package tcc.com.diario_digital_criptografado

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_listagem_pacientes.*
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.adapter.PacienteAdapter
import tcc.com.diario_digital_criptografado.models.Usuario

class ListagemPacientesActivity : AppCompatActivity(){
    private lateinit var recyclerView : RecyclerView
    private lateinit var pacienteList : ArrayList<Usuario>
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listagem_pacientes)

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
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}