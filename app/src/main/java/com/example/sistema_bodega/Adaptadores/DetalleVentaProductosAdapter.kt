package com.example.sistema_bodega.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistema_bodega.Clases.detalleTicketClass
import com.example.sistema_bodega.R

class detalleVentaProductosAdapter(
    private var productoList: ArrayList<detalleTicketClass>,
) :
    RecyclerView.Adapter<detalleVentaProductosAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_detalle_lista_venta, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = productoList[position]
        holder.nombreProducto_tv.text = currentEmp.idProducto
        holder.cantidadProductoVenta_tv.text = currentEmp.cantidadProducto
        holder.precioProducto_tv.text = currentEmp.detalleprecioProducto
        holder.subTotalVentaProdcuto_tv.text = currentEmp.precioSubTotalProducto
        //Si la imagen está vacia se muestra una por defecto
        if (currentEmp.imageUrl!!.isEmpty()) {
            holder.ProductoImagen_iv.setImageResource(R.drawable.producto)
        } else {
            //imagen con glide
            Glide.with(holder.itemView.context).load(currentEmp.imageUrl).into(holder.ProductoImagen_iv)
        }
    }

    override fun getItemCount(): Int {
        return productoList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreProducto_tv: TextView = itemView.findViewById(R.id.nombreProducto_tv)
        val cantidadProductoVenta_tv: TextView = itemView.findViewById(R.id.cantidadProductoVenta_tv)
        val precioProducto_tv: TextView = itemView.findViewById(R.id.precioProducto_tv)
        val subTotalVentaProdcuto_tv: TextView = itemView.findViewById(R.id.subTotalVentaProdcuto_tv)
        val ProductoImagen_iv: ImageView = itemView.findViewById(R.id.ProductoImagen_iv)

    }

    fun deleteItem(position: Int) {
        productoList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun searchDataList(searchList: List<detalleTicketClass>) {
        productoList = searchList as ArrayList<detalleTicketClass>
        notifyDataSetChanged()
    }

    fun getItem(position: Int): detalleTicketClass {
        return productoList[position]

    }
}