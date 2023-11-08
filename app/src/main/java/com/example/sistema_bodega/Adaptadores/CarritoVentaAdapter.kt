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
import com.example.sistema_bodega.Clases.VentaCarritoClass

class carritoVentaAdapter (
    private var productoList: ArrayList<VentaCarritoClass>
) :
    RecyclerView.Adapter<carritoVentaAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener
    private lateinit var QuitarVentaListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onItemQuitarVentaClick(position: Int)
    }


    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.carrito_venta_recyclerview, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = productoList[position]
        holder.tvNombreProducto.text = currentEmp.idProducto
        holder.tvPrecioProducto.text = currentEmp.precioTotal
        holder.tvcantidadProductoVenta.text = currentEmp.cantidad
        //Si la imagen está vacia se muestra una por defecto
        if (currentEmp.imageUrl!!.isEmpty()) {
            holder.ProductoImagen_iv.setImageResource(R.drawable.producto)
        } else {
            Glide.with(holder.itemView.context).load(currentEmp.imageUrl).into(holder.ProductoImagen_iv)
        }

    }

    override fun getItemCount(): Int {
        return productoList.size
    }


    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvNombreProducto : TextView = itemView.findViewById(R.id.nombreProducto_tv)
        val tvcantidadProductoVenta : TextView = itemView.findViewById(R.id.cantidadProductoVenta_tv)
        val tvPrecioProducto : TextView = itemView.findViewById(R.id.precioProducto_tv)
        val ProductoImagen_iv : ImageView = itemView.findViewById(R.id.ProductoImagen_iv)
        val quitarcarritoventa : ImageView = itemView.findViewById(R.id.quitarcarritoventa)
        //val Eliminar : ImageView = itemView.findViewById(R.id.id_eliminar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
            quitarcarritoventa.setOnClickListener {
                clickListener.onItemQuitarVentaClick(adapterPosition)
            }
        }

    }

    fun deleteItem(position: Int) {
        productoList.removeAt(position)
        notifyItemRemoved(position)
    }
    fun searchDataList(searchList: List<ProductoClass>) {
        productoList = searchList as ArrayList<VentaCarritoClass>
        notifyDataSetChanged()
    }

    fun getItem(position: Int): VentaCarritoClass {
        return productoList[position]

    }
}