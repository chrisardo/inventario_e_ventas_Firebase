package com.example.sistema_bodega.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_bodega.*
import com.example.sistema_bodega.Adaptadores.ProductoMyAdapter
import com.example.sistema_bodega.Clases.CategoriaClass
import com.example.sistema_bodega.Clases.ProductoClass
import com.example.sistema_bodega.R
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
//import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var floatingActionButtonMenu: FloatingActionMenu
    private lateinit var fABAdd: FloatingActionButton

    //lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: LinearLayout

    private lateinit var productoRecyclerView: RecyclerView
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null

    private lateinit var empList: ArrayList<ProductoClass>
    private lateinit var dbProductoRef: DatabaseReference
    private lateinit var dbCategoriaRef: DatabaseReference
    lateinit var mAdapter: ProductoMyAdapter
    private lateinit var searchView: SearchView
    private lateinit var tvProductEncontrados: TextView
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_producto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = preferences(requireContext())
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Obtener la referencia al elemento en el que deseas asignar el evento onClick
        floatingActionButtonMenu = view.findViewById<FloatingActionMenu>(R.id.fabPrincipal)
        floatingActionButtonMenu.setClosedOnTouchOutside(false)//para cerrar el menu flotante
        //Al tocar la pantalla se cierra el menu flotante principal
        floatingActionButtonMenu.setOnMenuToggleListener { opened ->
            if (opened) {//si esta abierto
                //Toast.makeText(requireContext(), "Menu abierto", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(requireContext(), "Menu cerrado", Toast.LENGTH_SHORT).show()
            }
        }
        floatingActionButtonMenu.setOnMenuButtonClickListener {
            if (floatingActionButtonMenu.isOpened) {//si esta abierto
                floatingActionButtonMenu.close(true)
            } else {
                floatingActionButtonMenu.open(true)
            }
        }

        fABAdd = view.findViewById<FloatingActionButton>(R.id.fabAddProducto)
        fABAdd.setOnClickListener {
            val intent = Intent(requireActivity(), RegistrarProductoActivity::class.java)
            startActivity(intent)
        }
        searchView = view.findViewById(R.id.searchView)
        searchView.queryHint = "Buscar producto"
        // Configurar listener para el SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Acciones a realizar cuando se envía la búsqueda (por ejemplo, llamar a un método de búsqueda)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Acciones a realizar cuando cambia el texto de búsqueda (por ejemplo, filtrar los resultados en tiempo real)
                searchList(newText)
                return true
            }
        })
        progressBar = view.findViewById(R.id.progressBar)
        tvProductEncontrados = view.findViewById(R.id.tvProductEncontrados)
        productoRecyclerView = view.findViewById(R.id.rvProductos)
        empList = arrayListOf<ProductoClass>()
        productoRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        //productoRecyclerView.setHasFixedSize(true)//para que no cambie el tamaño
        userId = pref.prefIdUser

        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected) {//si hay internet

            getProductosData()
        } else {
            //Toast.makeText(requireContext(), "No hay internet", Toast.LENGTH_SHORT).show()
            showConnectivityError()
        }
    }

    private fun getProductosData() {
        productoRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        dbProductoRef = FirebaseDatabase.getInstance().getReference("Productos")
        dbCategoriaRef = FirebaseDatabase.getInstance().getReference("Categoria")

        // Realizar la consulta para obtener los productos
        dbProductoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                empList.clear()

                for (productoSnapshot in dataSnapshot.children) {
                    val producto = productoSnapshot.getValue(ProductoClass::class.java)
                    val totalProductCount = dataSnapshot.childrenCount.toInt()

                    if (producto?.idUuser == userId) {
                        if (totalProductCount > 0) {
                            tvProductEncontrados.text =
                                "Total de productos registrados: $totalProductCount ." // Actualizar el TextView
                        } else {
                            tvProductEncontrados.text =
                                "No hay productos registradas"
                        }
                        //empList.add(producto!!)

                        producto?.let {
                            // Obtener el ID del proveedor para obtener su información
                            val categoriaId = it.idCategoria

                            // Realizar la consulta para obtener la información del proveedor
                            dbCategoriaRef.child(categoriaId!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(proveedorSnapshot: DataSnapshot) {
                                        val categoria = proveedorSnapshot.getValue(CategoriaClass::class.java)

                                        categoria?.let { prov ->
                                            it.idCategoria = prov.nombreCategoria
                                            empList.add(it)

                                            mAdapter.notifyDataSetChanged()
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Error al obtener la información del proveedor
                                    }
                                })
                        }

                    }

                }
                progressBar.visibility = View.GONE
                productoRecyclerView.visibility = View.VISIBLE
                mAdapter = ProductoMyAdapter(empList)
                productoRecyclerView.adapter = mAdapter


                mAdapter.setOnItemClickListener(object : ProductoMyAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val producto = mAdapter.getItem(position)
                        Toast.makeText(requireContext(), empList[position].nombreProducto, Toast.LENGTH_LONG).show()
                        val intent = Intent(requireActivity(), detalleProductoActivity::class.java)

                        //put extras
                        intent.putExtra("idProducto", producto.idProducto)
                        startActivity(intent)
                    }
                    /*

                override fun onItemDeleteClick(position: Int) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Eliminar elemento ${empList[position].nombreProducto}")
                        .setMessage("¿Estás seguro de eliminar este elemento?")
                        .setPositiveButton("Sí") { _, _ ->
                            try{
                                //codigo
                                val dbRef = FirebaseDatabase.getInstance().getReference("Productos")
                                    .child(empList[position].idProducto!!)
                                val storage = FirebaseStorage.getInstance()
                                val storageReference =
                                    storage.getReferenceFromUrl(empList[position].imageUrl!!)
                                storageReference.delete().addOnSuccessListener {
                                    // File deleted successfully
                                    dbRef.removeValue()
                                    Toast.makeText(requireActivity(), "Eliminado: " + empList[position].nombreProducto, Toast.LENGTH_SHORT)
                                        .show()
                                    mAdapter.notifyDataSetChanged()
                                }.addOnFailureListener { error ->
                                    Toast.makeText(
                                        requireActivity(),
                                        "Deleting Err ${error.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    // Uh-oh, an error occurred!
                                }
                            }catch (e:Exception){
                                //caso que hubiera fallas en el codigo
                            }
                        }
                        .setNegativeButton("No") { _, _ ->
                            // No hacer nada
                        }
                        .show()
                }

                 */

                })

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error al obtener los productos
            }
        })
    }

    private fun checkInternetConnection(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun searchList(text: String) {
        val searchList = java.util.ArrayList<ProductoClass>()
        for (dataClass in empList) {
            if (dataClass.nombreProducto?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true ||
                dataClass.codigoProducto?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }

        mAdapter.searchDataList(searchList)
    }

    private fun showConnectivityError() {
        val dialogBuilder = android.app.AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage("No hay conexión a Internet. Por favor, conéctate y vuelve a intentarlo.")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                //finish()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}