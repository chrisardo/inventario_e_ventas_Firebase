package com.example.sistema_bodega.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistema_bodega.Clases.CategoriaClass
import com.example.sistema_bodega.R

class CategoryMyAdapter(private var categoryList: ArrayList<CategoriaClass>) :
    RecyclerView.Adapter<CategoryMyAdapter.ViewHolder>() {

    private var mListener: onItemClickListener? = null

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener?) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_recyclerview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = categoryList[position]
        holder.bind(currentEmp)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recImage: ImageView = itemView.findViewById(R.id.CategoryImage)
        private val tvEmpName: TextView = itemView.findViewById(R.id.nombreCategory_item)

        init {
            itemView.setOnClickListener {
                mListener?.onItemClick(adapterPosition)
            }
        }

        fun bind(categoriaClass: CategoriaClass) {
            tvEmpName.text = categoriaClass.nombreCategoria
            //Si la imagen está vacia se muestra una por defecto
            if (categoriaClass.imageUrl!!.isEmpty()) {
                recImage.setImageResource(R.drawable.ic_category)
            } else {
                Glide.with(recImage.context)
                    .load(categoriaClass.imageUrl)
                    .into(recImage)
            }


        }
    }

    fun deleteItem(position: Int) {
        categoryList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun searchDataList(searchList: List<CategoriaClass>) {
        categoryList = searchList as ArrayList<CategoriaClass>
        notifyDataSetChanged()
    }

    fun getItem(position: Int): CategoriaClass {

        return categoryList[position]

    }
}
