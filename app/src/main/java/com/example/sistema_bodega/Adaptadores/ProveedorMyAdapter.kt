package com.example.sistema_bodega.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistema_bodega.Clases.ProveedorClass
import com.example.sistema_bodega.R

class ProveedorMyAdapter ( private var proveedorList: ArrayList<ProveedorClass>) :
    RecyclerView.Adapter<ProveedorMyAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    private lateinit var EliminarListener: onItemClickListener
    private lateinit var LlamarListener: onItemClickListener
    private lateinit var enviarEmailListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onItemLlamarClick(position: Int)
        fun onItemEnviarEmailClick(position: Int)
    }


    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.proveedor_recycler, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = proveedorList[position]
        holder.tvEmpName.text = currentEmp.nombreProveedor

        //Si la imagen está vacia se muestra una por defecto
        if (currentEmp.imageUrl!!.isEmpty()) {
            holder.recImage.setImageResource(R.drawable.proveedor)
        } else {
            Glide.with(holder.recImage.context)
                .load(currentEmp.imageUrl)
                .into(holder.recImage)
        }
    }

    override fun getItemCount(): Int {
        return proveedorList.size
    }


    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val recImage: ImageView = itemView.findViewById(R.id.ProveedorImage)
        val tvEmpName : TextView = itemView.findViewById(R.id.nombreProveedor_item)
        val llamar : ImageView = itemView.findViewById(R.id.id_llamar)
        val enviarEmail : ImageView = itemView.findViewById(R.id.id_editar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
            llamar.setOnClickListener {
                clickListener.onItemLlamarClick(adapterPosition)
            }
            enviarEmail.setOnClickListener {
                clickListener.onItemEnviarEmailClick(adapterPosition)
            }
        }

    }

    fun deleteItem(position: Int) {
        proveedorList.removeAt(position)
        notifyItemRemoved(position)
    }
    fun searchDataList(searchList: List<ProveedorClass>) {
        proveedorList = searchList as ArrayList<ProveedorClass>
        notifyDataSetChanged()
    }
    fun getItem(position: Int): ProveedorClass {

        return proveedorList[position]

    }
}