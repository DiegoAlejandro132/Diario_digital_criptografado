package tcc.com.diario_digital_criptografado.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.model.Usuario
import tcc.com.diario_digital_criptografado.util.CriptografiaUtil

class PacienteAdapter (private val context : Context, private val pacienteslist : ArrayList<Usuario>) : RecyclerView.Adapter<PacienteAdapter.ViewHolder>() {

    private lateinit var listener : onItemClickListener

    interface onItemClickListener{
        fun selecionarPaciente(position : Int)
        //fun onItemLongClickListener(position : Int)
        fun excluirPaciente(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.model_item_list_paciente, parent)
            return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = pacienteslist[position]
        holder.nomeList.text = current.nome.replaceFirstChar { it.toUpperCase() }
        holder.telefoneList.text = current.telefone
        holder.emailList.text = current.email
        val diasRuins = CriptografiaUtil.decrypt(current.diasRuinsConsecutivos)
        if(diasRuins == "true")
            holder.alerta.background.setTint(Color.parseColor("#FF0000"))
        else
            holder.alerta.background.setTint(Color.parseColor("#11cc00"))
        if(pacienteslist[position].foto_perfil != "")
            Glide.with(context).load(pacienteslist[position].foto_perfil).into(holder.fotoPerfil)
        else
            Glide.with(context).load(R.drawable.imagem_perfil_default).into(holder.fotoPerfil)
    }

    override fun getItemCount(): Int {
        return pacienteslist.size
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val nomeList : TextView = itemView.findViewById(R.id.txt_nome_list)
        val telefoneList : TextView = itemView.findViewById(R.id.txt_telefone_list)
        val emailList : TextView = itemView.findViewById(R.id.txt_email_list)
        val alerta : CardView = itemView.findViewById(R.id.alerta_dias_ruins_paciente)
        val lixeira : ImageView = itemView.findViewById(R.id.btn_excluir_paciente)
        val fotoPerfil : ImageView = itemView.findViewById(R.id.img_fotoPerfil_card_listagem)


        init {
            itemView.setOnClickListener{
                listener.selecionarPaciente(adapterPosition)
            }

//            itemView.setOnLongClickListener {
//                listener.onItemLongClickListener(adapterPosition)
//                true
//            }

            lixeira.setOnClickListener {
                listener.excluirPaciente(adapterPosition)
            }
        }

    }


}