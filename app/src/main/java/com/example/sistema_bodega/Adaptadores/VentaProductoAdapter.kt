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

class ventaProductoAdapter ( private var productoList: ArrayList<ProductoClass>) :
    RecyclerView.Adapter<ventaProductoAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener
    private lateinit var VentaListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onItemVentaClick(position: Int)
    }


    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ventas_producto_recyclerview, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = productoList[position]
        holder.tvNombreProducto.text = currentEmp.nombreProducto
        holder.tvPrecioProducto.text = currentEmp.precioProducto
        holder.codigoProducto.text = currentEmp.codigoProducto
        holder.tvStockProducto.text = currentEmp.stockProducto
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
        val tvPrecioProducto : TextView = itemView.findViewById(R.id.precioProducto_tv)
        val tvStockProducto:  TextView = itemView.findViewById(R.id.stockProducto_tv)
        val carritoVenta : ImageView = itemView.findViewById(R.id.carritoventa)
        //val Eliminar : ImageView = itemView.findViewById(R.id.id_eliminar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
            carritoVenta.setOnClickListener {
                clickListener.onItemVentaClick(adapterPosition)
            }
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