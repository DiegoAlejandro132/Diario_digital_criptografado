package tcc.com.diario_digital_criptografado.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.model.Usuario
import tcc.com.diario_digital_criptografado.psicologoActivities.ListagemPacientesActivity

class PacienteAdapter (private val pacienteslist : ArrayList<Usuario>) : RecyclerView.Adapter<PacienteAdapter.ViewHolder>() {

    private lateinit var database : DatabaseReference

    private lateinit var listener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
        fun onItemLongClickListener(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.model_item_list_paciente, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = pacienteslist[position]
        holder.nomeList.text = current.nome
        holder.telefoneList.text = current.telefone
        holder.emailList.text = current.email

    }

    override fun getItemCount(): Int {
        return pacienteslist.size
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val nomeList : TextView = itemView.findViewById(R.id.txt_nome_list)
        val telefoneList : TextView = itemView.findViewById(R.id.txt_telefone_list)
        val emailList : TextView = itemView.findViewById(R.id.txt_email_list)


        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

            itemView.setOnLongClickListener {
                listener.onItemLongClickListener(adapterPosition)
                true
            }
        }
    }


}