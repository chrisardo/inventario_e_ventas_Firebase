package com.example.sistema_bodega.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.sistema_bodega.Adaptadores.ProductoMyAdapter
import com.example.sistema_bodega.Clases.*
import com.example.sistema_bodega.R
import com.example.sistema_bodega.detalleProductoActivity
import com.example.sistema_bodega.preferences
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit  var tv_cantidadProducto: TextView
    private lateinit  var tv_cantidadCategory: TextView
    private lateinit  var tv_cantidadProveedores: TextView
    private lateinit  var tv_cantiClientes: TextView
    private lateinit  var tv_cantidadVentas: TextView
    private lateinit  var tvUserName: TextView
    var userId: String? = null
    private lateinit var databaseUsarioRef: DatabaseReference
    private lateinit var dbProductoRef: DatabaseReference
    private lateinit var dbCateogriaRef: DatabaseReference
    private lateinit var dbProveedorRef: DatabaseReference
    private lateinit var dbClientesRef: DatabaseReference
    private lateinit var dbVentasRef: DatabaseReference
    private lateinit var pref: preferences//para el shared preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = preferences(view.context)
        databaseUsarioRef = FirebaseDatabase.getInstance().getReference("usuarios")
        dbProductoRef = FirebaseDatabase.getInstance().getReference("Productos")
        dbCateogriaRef = FirebaseDatabase.getInstance().getReference("Categoria")
        dbProveedorRef = FirebaseDatabase.getInstance().getReference("Proveedor")
        dbClientesRef = FirebaseDatabase.getInstance().getReference("Clientes")
        dbVentasRef = FirebaseDatabase.getInstance().getReference("TicketVentas")

        tv_cantidadProducto = view.findViewById<TextView>(R.id.tv_cantidadProducto)
        tv_cantidadCategory = view.findViewById<TextView>(R.id.tv_cantidadCategory)
        tv_cantidadProveedores = view.findViewById<TextView>(R.id.tv_cantidadProveedores)
        tv_cantiClientes = view.findViewById<TextView>(R.id.tv_cantiClientes)
        tv_cantidadVentas = view.findViewById<TextView>(R.id.tv_cantidadVentas)
        tvUserName = view.findViewById<TextView>(R.id.tvUserName)

        userId = pref.prefIdUser
        obtenerDatosUsuarios()
        obtenerCantidadProductos()
        obtenerCantidadCategorias()
        obtenerCantidadProveedores()
        obtenerCantidadClientes()
        obtenerCantidadVentas()
    }

    private fun obtenerDatosUsuarios(){
        //obtener datos del usuario
        val checkUserDatabase = databaseUsarioRef.orderByChild("idUser").equalTo(userId)
        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val nombreUsuario = userSnapshot.child("nombre").getValue(String::class.java)
                        tvUserName.text = "Bienvenido: $nombreUsuario"
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private fun obtenerCantidadProductos(){
        // Realizar la consulta para obtener los productos
        dbProductoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (productoSnapshot in dataSnapshot.children) {
                    val producto = productoSnapshot.getValue(ProductoClass::class.java)
                    val totalProductCount = dataSnapshot.childrenCount.toInt()

                    if (producto?.idUuser == userId) {
                        if (totalProductCount > 0) {
                            tv_cantidadProducto.text ="Registrados: $totalProductCount" // Actualizar el TextView
                        } else {
                            tv_cantidadProducto.text = "No hay productos registradas"
                        }
                        //empList.add(producto!!)
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error al obtener los productos
            }
        })
    }

    private fun obtenerCantidadCategorias(){
        // Realizar la consulta para obtener los productos
        dbCateogriaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (categoriaSnapshot in dataSnapshot.children) {
                    val categoria = categoriaSnapshot.getValue(CategoriaClass::class.java)
                    val totalCategoryCount = dataSnapshot.childrenCount.toInt()

                    if (categoria?.idUuser == userId) {
                        if (totalCategoryCount > 0) {
                            tv_cantidadCategory.text ="Registradas: $totalCategoryCount" // Actualizar el TextView
                        } else {
                            tv_cantidadCategory.text = "No hay categorias registradas"
                        }
                        //empList.add(producto!!)
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error al obtener los productos
            }
        })
    }

    private fun obtenerCantidadProveedores(){
        // Realizar la consulta para obtener los productos
        dbProveedorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (proveedorSnapshot in dataSnapshot.children) {
                    val proveedor = proveedorSnapshot.getValue(ProveedorClass::class.java)
                    val totalProveedorCount = dataSnapshot.childrenCount.toInt()

                    if (proveedor?.idUuser == userId) {
                        if (totalProveedorCount > 0) {
                            tv_cantidadProveedores.text ="Registrados: $totalProveedorCount" // Actualizar el TextView
                        } else {
                            tv_cantidadProveedores.text = "No hay proveedores registrados"
                        }
                        //empList.add(producto!!)
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error al obtener los productos
            }
        })
    }

    private fun obtenerCantidadClientes(){
        // Realizar la consulta para obtener los productos
        dbClientesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (clienteSnapshot in dataSnapshot.children) {
                    val cliente = clienteSnapshot.getValue(ClientesClass::class.java)
                    val totalClienteCount = dataSnapshot.childrenCount.toInt()

                    if (cliente?.idUuser == userId) {
                        if (totalClienteCount > 0) {
                            tv_cantiClientes.text ="Registrados: $totalClienteCount" // Actualizar el TextView
                        } else {
                            tv_cantiClientes.text = "No hay clientes registrados"
                        }
                        //empList.add(producto!!)
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error al obtener los productos
            }
        })
    }

    private fun obtenerCantidadVentas(){
        // Realizar la consulta para obtener los productos
        dbVentasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (ventasSnapshot in dataSnapshot.children) {
                    val ventas = ventasSnapshot.getValue(TicketVentaClass::class.java)
                    val totalVentasCount = dataSnapshot.childrenCount.toInt()

                    if (ventas?.idUser == userId) {
                        if (totalVentasCount > 0) {
                            tv_cantidadVentas.text ="Registradas: $totalVentasCount" // Actualizar el TextView
                        } else {
                            tv_cantidadVentas.text = "No hay ventas registradas"
                        }
                        //empList.add(producto!!)
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error al obtener los productos
            }
        })
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}