package com.example.sistema_bodega.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_bodega.Clases.ClientesClass
import com.example.sistema_bodega.R

class ClientesMyAdapter ( private var clientesList: ArrayList<ClientesClass>) :
    RecyclerView.Adapter<ClientesMyAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener
    private lateinit var LlamarListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onItemLlamarClick(position: Int)
    }


    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cliente_recyclerview, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = clientesList[position]
        holder.tvClienteName.text = currentEmp.nombreCliente
        holder.tvClienteDni.text = currentEmp.dniCliente
    }

    override fun getItemCount(): Int {
        return clientesList.size
    }


    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvClienteName : TextView = itemView.findViewById(R.id.nombreCliente_tv)
        val tvClienteDni : TextView = itemView.findViewById(R.id.dniCliente_tv)
        val llamar : ImageView = itemView.findViewById(R.id.id_llamar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
            llamar.setOnClickListener {
                clickListener.onItemLlamarClick(adapterPosition)
            }
        }

    }

    fun deleteItem(position: Int) {
        clientesList.removeAt(position)
        notifyItemRemoved(position)
    }
    fun searchDataList(searchList: List<ClientesClass>) {
        clientesList = searchList as ArrayList<ClientesClass>
        notifyDataSetChanged()
    }
    fun getItem(position: Int): ClientesClass {

        return clientesList[position]

    }
}