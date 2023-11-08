package com.example.sistema_bodega.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_bodega.R
import com.example.sistema_bodega.Clases.TicketVentaClass

class historialVentaAdapter  (
    private var productoList: ArrayList<TicketVentaClass>
) :
    RecyclerView.Adapter<historialVentaAdapter.ViewHolder>() {
//HistorialVentaAdapter
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onItemMenuClick(position: Int, view: View)
    }


    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_historial_ventas, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = productoList[position]
        holder.serieVenta_tv.text = currentEmp.serieVenta
        holder.nombreCliente_tv.text = currentEmp.idCliente
        holder.fechaventa_tv.text = currentEmp.fechaVenta
        holder.horaventa_tv.text = currentEmp.horaVenta
        holder.tipoPago_tv.text = currentEmp.formaPago
        holder.totalVenta_tv.text = currentEmp.totalVenta
    }

    override fun getItemCount(): Int {
        return productoList.size
    }


    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val serieVenta_tv : TextView = itemView.findViewById(R.id.serieVenta_tv)
        val nombreCliente_tv : TextView = itemView.findViewById(R.id.nombreCliente_tv)
        val fechaventa_tv : TextView = itemView.findViewById(R.id.fechaventa_tv)
        val horaventa_tv : TextView = itemView.findViewById(R.id.horaventa_tv)
        val tipoPago_tv : TextView = itemView.findViewById(R.id.tipoPago_tv)
        val totalVenta_tv : TextView = itemView.findViewById(R.id.totalVenta_tv)
        val mMenus : ImageView = itemView.findViewById(R.id.mMenus)
        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
            mMenus.setOnClickListener {
                clickListener.onItemMenuClick(adapterPosition, it)
            }
        }

    }

    fun deleteItem(position: Int) {
        productoList.removeAt(position)
        notifyItemRemoved(position)
    }
    fun searchDataList(searchList: List<TicketVentaClass>) {
        productoList = searchList as ArrayList<TicketVentaClass>
        notifyDataSetChanged()
    }

    fun getItem(position: Int): TicketVentaClass {
        return productoList[position]

    }
}