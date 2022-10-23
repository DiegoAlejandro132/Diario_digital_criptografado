package tcc.com.diario_digital_criptografado.adapter;

import android.content.Context;
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tcc.com.diario_digital_criptografado.R

import java.util.ArrayList;

import tcc.com.diario_digital_criptografado.model.DiaFormulario;

class DiaAdapter (private val context :Context, private val pacienteslist :ArrayList<DiaFormulario>) : RecyclerView.Adapter<DiaAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: DiaAdapter.ViewHolder, position: Int) {
        val current = pacienteslist[position]
        holder.titulo.text = current.titulo.replaceFirstChar { it.toUpperCase() }
        holder.dataCard.text = current.data
        when(current.avaliacaoDia){
            "Péssimo" -> holder.imgAvaliacao
            "Ruim" -> holder.imgAvaliacao
            "Regular" ->holder.imgAvaliacao
            "Bom" -> holder.imgAvaliacao
            "Excelente" -> holder.imgAvaliacao
        }

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgAvaliacao : ImageView = itemView.findViewById(R.id.img_list_item_avalicao_dia)
        val dataCard : TextView = itemView.findViewById(R.id.txt_data_dia_formulario_item_list)
        val titulo : TextView = itemView.findViewById(R.id.txt_titulo_dia_item_list)
        val lixeira : ImageView = itemView.findViewById(R.id.btn_excluir_dia)
        val visualizar : ImageView = itemView.findViewById(R.id.btn_vizualisar_dia)
    }

}
