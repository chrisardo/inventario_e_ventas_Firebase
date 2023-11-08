package com.example.sistema_bodega.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistema_bodega.Clases.ProductoClass
import com.example.sistema_bodega.R

class ProductoMyAdapter ( private var productoList: ArrayList<ProductoClass>) :
    RecyclerView.Adapter<ProductoMyAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    private lateinit var EliminarListener: onItemClickListener
    private lateinit var LlamarListener: onItemClickListener
    private lateinit var enviarEmailListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        //fun onItemDeleteClick(position: Int)
    }


    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.producto_recycler, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = productoList[position]
        holder.tvNombreProducto.text = currentEmp.nombreProducto
        holder.tvNombreCategoria.text = currentEmp.idCategoria
        holder.tvPrecioProducto.text = currentEmp.precioProducto
        holder.tvstockProducto.text = currentEmp.stockProducto
        holder.codigoProducto.text = currentEmp.codigoProducto
        //Si la imagen está vacia se muestra una por defecto
        if (currentEmp.imageUrl!!.isEmpty()) {
            holder.recImage.setImageResource(R.drawable.producto)
        } else {
            Glide.with(holder.recImage.context)
                .load(currentEmp.imageUrl)
                .into(holder.recImage)
        }
    }

    override fun getItemCount(): Int {
        return productoList.size
    }


    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val recImage: ImageView = itemView.findViewById(R.id.ProveedorImagen_iv)
        val codigoProducto : TextView = itemView.findViewById(R.id.codigoProducto_tv)
        val tvNombreProducto : TextView = itemView.findViewById(R.id.nombreProducto_tv)
        val tvNombreCategoria : TextView = itemView.findViewById(R.id.nombreCategoria_tv)
        val tvPrecioProducto : TextView = itemView.findViewById(R.id.precioProducto_tv)
        val tvstockProducto : TextView = itemView.findViewById(R.id.stockProducto_tv)
        //val Eliminar : ImageView = itemView.findViewById(R.id.id_eliminar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
            /*Eliminar.setOnClickListener {
                clickListener.onItemDeleteClick(adapterPosition)
            }*/
        }

    }

    fun deleteItem(position: Int) {
        productoList.removeAt(position)
        notifyItemRemoved(position)
    }
    fun searchDataList(searchList: List<ProductoClass>) {
        productoList = searchList as ArrayList<ProductoClass>
        notifyDataSetChanged()
    }

    fun getItem(position: Int): ProductoClass {

        return productoList[position]

    }
}