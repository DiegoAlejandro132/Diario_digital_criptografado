package tcc.com.diario_digital_criptografado.adapter;

import android.content.Context;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tcc.com.diario_digital_criptografado.R

import java.util.ArrayList;

import tcc.com.diario_digital_criptografado.model.Diario;

class DiaAdapter (private val context :Context, private val diaList :ArrayList<Diario>) : RecyclerView.Adapter<DiaAdapter.ViewHolder>(){

    private lateinit var listener : onItemClickListener

    interface onItemClickListener{
        fun visualizarDia(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.model_item_list_registro_diario, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: DiaAdapter.ViewHolder, position: Int) {
        val current = diaList[position]
        holder.titulo.text = current.titulo.replaceFirstChar { it.toUpperCase() }
        holder.dataCard.text = current.data
        holder.avaliacao.text = current.avaliacaoDia

    }

    override fun getItemCount(): Int {
        return diaList.size
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val dataCard : TextView = itemView.findViewById(R.id.txt_data_dia_formulario_item_list)
        val titulo : TextView = itemView.findViewById(R.id.txt_titulo_dia_item_list)
        val avaliacao : TextView = itemView.findViewById(R.id.txt_avaliacao_dia_card_list)
        val visualizar : ImageView = itemView.findViewById(R.id.btn_vizualisar_dia)

        init {
            visualizar.setOnClickListener {
                listener.visualizarDia(adapterPosition)
            }
        }

    }

}
