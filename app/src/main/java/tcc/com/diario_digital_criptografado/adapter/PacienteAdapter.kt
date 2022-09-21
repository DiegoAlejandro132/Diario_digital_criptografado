package tcc.com.diario_digital_criptografado.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.models.Usuario

class PacienteAdapter (private val pacienteslist : ArrayList<Usuario>) : RecyclerView.Adapter<PacienteAdapter.ViewHolder>() {

    private lateinit var listener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
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

    }


    override fun getItemCount(): Int {
        return pacienteslist.size
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val nomeList : TextView = itemView.findViewById(R.id.txt_nome_list)
        val telefoneList : TextView = itemView.findViewById(R.id.txt_telefone_list)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }


}