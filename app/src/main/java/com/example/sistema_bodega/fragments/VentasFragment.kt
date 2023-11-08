package com.example.sistema_bodega.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_bodega.*
import com.example.sistema_bodega.Adaptadores.ventaProductoAdapter
import com.example.sistema_bodega.Clases.ProductoClass
import com.example.sistema_bodega.Clases.VentaCarritoClass
import com.example.sistema_bodega.R
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VentasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VentasFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var floatingActionButtonMenu: FloatingActionMenu
    private lateinit var fabCarritoVentas: FloatingActionButton
    private lateinit var fabHistorialVentas: FloatingActionButton
    private lateinit var progressBar: LinearLayout
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var searchView: SearchView
    private lateinit var pref: preferences//para el shared preferences
    private lateinit var ventasRecyclerView: RecyclerView
    lateinit var mAdapter: ventaProductoAdapter
    private lateinit var dbProductoRef: DatabaseReference
    private lateinit var empList: ArrayList<ProductoClass>
    private lateinit var tvLoadingData: TextView
    private lateinit var imagenQr: ImageView
    var userId: String? = null
    var producto: ProductoClass? = null
    private lateinit var dbVentaRef: DatabaseReference

    // Agrega esta propiedad a la clase para almacenar el contenido del código QR escaneado
    private var scannedQrContent: String? = null

    // Agrega estas propiedades al inicio de tu Fragment
    private var totalSum: Double = 0.0
    private lateinit var tv_cantidadProductoVenta: TextView
    private lateinit var alertDialog: AlertDialog
    private lateinit var dbProductosRef: DatabaseReference
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
        return inflater.inflate(R.layout.fragment_ventas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = preferences(requireContext())
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        dbVentaRef = FirebaseDatabase.getInstance().getReference("VentaCarrito")
        dbProductosRef = FirebaseDatabase.getInstance().getReference("Productos")


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
        fabHistorialVentas = view.findViewById<FloatingActionButton>(R.id.fabHistorialVentas)
        fabHistorialVentas.setOnClickListener {
            val intent = Intent(requireActivity(), HistoriaVentaActivity::class.java)
            startActivity(intent)
        }
        fabCarritoVentas = view.findViewById<FloatingActionButton>(R.id.fabCarritoVentas)
        fabCarritoVentas.setOnClickListener {
            val intent = Intent(requireActivity(), CarritoVentasActivity::class.java)
            startActivity(intent)
        }
        tvLoadingData = view.findViewById(R.id.tvLoadingData)
        imagenQr = view.findViewById(R.id.imagenQr)
        imagenQr.setOnClickListener { initScanner() }
        searchView = view.findViewById(R.id.searchView)
        searchView.queryHint = "Busca producto a vender por codigo, nombre o escanee el codigo QR desde la imagen"

        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected) {//si hay internet

            // Configurar listener para el SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    // Acciones a realizar cuando se envía la búsqueda (por ejemplo, llamar a un método de búsqueda)

                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    // Acciones a realizar cuando cambia el texto de búsqueda (por ejemplo, filtrar los resultados en tiempo real)

                    // Verificar si el texto de búsqueda no está vacío antes de llamar a searchList
                    if (newText.isNotEmpty()) {
                        searchList(newText)
                        tvLoadingData.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                    } else {
                        // Si el texto de búsqueda está vacío, ocultar el RecyclerView
                        ventasRecyclerView.visibility = View.GONE
                        tvLoadingData.visibility = View.VISIBLE
                        tvLoadingData.setText("Busca producto a vender por codigo, nombre o escanee el codigo QR desde la imagen")
                    }
                    return true
                }
            })
            progressBar = view.findViewById(R.id.progressBar)
            ventasRecyclerView = view.findViewById(R.id.ventasRecyclerView)
            empList = arrayListOf<ProductoClass>() // Descomenta esta línea para inicializar empList
            ventasRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
            ventasRecyclerView.setHasFixedSize(true) // para que no cambie el tamaño
            userId = pref.prefIdUser
        } else {
            //Toast.makeText(requireContext(), "No hay internet", Toast.LENGTH_SHORT).show()
            showConnectivityError()
        }
        // Obtener la suma del total de la base de datos "VentaCarrito"
        getCarritoTotal()
    }

    private fun cantidadVentaProductoDialog(idProducto: String) {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.cantidad_producto_venta, null)
        dialogBuilder.setView(dialogView)
        val tv_nombreProductoVenta = dialogView.findViewById<TextView>(R.id.nombreProductoVenta)
        tv_cantidadProductoVenta = dialogView.findViewById<TextView>(R.id.cantidadProductoVenta)
        var tv_precioProductoVenta = dialogView.findViewById<TextView>(R.id.precioProductoVenta)
        val tv_disminuirCantidad = dialogView.findViewById<TextView>(R.id.disminuirCantidad)
        tv_disminuirCantidad.setOnClickListener {
            // Obtener el valor actual del TextView tv_cantidadProductoVenta
            var cantidadActual = tv_cantidadProductoVenta.text.toString().toInt()

            // Disminuir la cantidad en 1 si es mayor que 1
            if (cantidadActual > 1) {
                cantidadActual--

                // Actualizar el TextView con el nuevo valor de cantidad
                tv_cantidadProductoVenta.text = cantidadActual.toString()
            }
        }
        val tv_aumentarCantidad = dialogView.findViewById<TextView>(R.id.aumentarCantidad)

        val checkProductoDatabase = dbProductoRef.child(idProducto)
        checkProductoDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                producto = snapshot.getValue(ProductoClass::class.java)
                tv_nombreProductoVenta.text = producto?.nombreProducto
                tv_precioProductoVenta.text = "S/." + producto?.precioProducto.toString()

                tv_aumentarCantidad.setOnClickListener {
                    // Obtener el valor actual del TextView tv_cantidadProductoVenta
                    var cantidadActual = tv_cantidadProductoVenta.text.toString().toInt()

                    // Aumentar la cantidad en 1 si es menor que el stock
                    if (cantidadActual < producto?.stockProducto?.toInt() ?: 0) {
                        cantidadActual++

                        // Actualizar el TextView con el nuevo valor de cantidad
                        tv_cantidadProductoVenta.text = cantidadActual.toString()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No puedes agregar más unidades. Stock máximo alcanzado.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al obtener los productos", Toast.LENGTH_SHORT).show()
            }
        })

        val btnCancelar = dialogView.findViewById<ImageView>(R.id.cancelarVentaProducto_imView)
        val btnAceptar = dialogView.findViewById<AppCompatButton>(R.id.carritoVentaProducto_btn)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }
        btnAceptar.setOnClickListener {
            // If the product does not exist in the cart, show the dialog to add it
            showAddToCartDialog(idProducto)
        }
    }

    private fun showAddToCartDialog(idProducto: String) {
        val cartQuery =
            dbVentaRef.orderByChild("idProducto").equalTo(idProducto)//busca en la base de datos si existe el producto
        cartQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // If the product exists in the cart, update its quantity
                if (snapshot.exists()) {
                    for (cartSnapshot in snapshot.children) {
                        val venta = cartSnapshot.getValue(VentaCarritoClass::class.java)
                        val currentQuantity = venta?.cantidad?.toInt() ?: 0

                        // Update the quantity by adding the new quantity
                        val newQuantity = currentQuantity + tv_cantidadProductoVenta.text.toString().toInt()
                        cartSnapshot.ref.child("cantidad").setValue(newQuantity.toString())
                        // Calculate and update the new subtotal for the product
                        val precioStr = producto?.precioProducto.toString()
                        val precio = precioStr.toDouble()
                        val subtotal = newQuantity * precio
                        cartSnapshot.ref.child("precioTotal").setValue(subtotal.toString())

                        alertDialog.dismiss()
                        return
                    }
                } else {
                    //Toast.makeText(requireContext(), "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                    // If the product does not exist in the cart, add a new entry
                    val cantidad = tv_cantidadProductoVenta.text.toString().toInt()
                    val precioStr = producto?.precioProducto.toString()

                    // Convertir la cadena a un Double
                    val precio = precioStr.toDouble()
                    val total = cantidad * precio
                    //convetir el total a con 2 decimales y convertirlo a string
                    val totalStr = String.format("%.2f", total)

                    val ventaId = dbVentaRef.push().key
                    val venta = VentaCarritoClass(ventaId, idProducto, cantidad.toString(), totalStr, userId)
                    dbVentaRef.child(ventaId!!).setValue(venta).addOnCompleteListener {
                        if (it.isSuccessful) {
                            alertDialog.dismiss()
                            Toast.makeText(requireContext(), "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al agregar el producto al carrito",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al obtener el carrito", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchList(text: String) {
        // Mostrar el ProgressBar mientras se obtienen los datos de la fuente de datos
        progressBar.visibility = View.VISIBLE

        // Realizar la consulta para obtener los productos desde la fuente de datos
        dbProductoRef = FirebaseDatabase.getInstance().getReference("Productos")
        dbProductoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val filteredList = ArrayList<ProductoClass>() // Creamos una nueva lista filtrada

                for (productoSnapshot in dataSnapshot.children) {
                    val producto = productoSnapshot.getValue(ProductoClass::class.java)
                    if (producto?.idUuser == userId && (producto!!.nombreProducto?.lowercase()
                            ?.contains(text.lowercase(Locale.getDefault())) == true ||
                                producto.codigoProducto?.lowercase()
                                    ?.contains(text.lowercase(Locale.getDefault())) == true)
                    ) {
                        filteredList.add(producto!!)
                    }
                }

                if (filteredList.isEmpty()) {
                    // Si no hay coincidencias, ocultar el RecyclerView
                    ventasRecyclerView.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    tvLoadingData.visibility = View.VISIBLE
                    tvLoadingData.setText("No hay coincidencias de ese producto")
                } else {
                    tvLoadingData.visibility = View.GONE
                    // Mostrar el RecyclerView y actualizar el adaptador con los datos filtrados
                    ventasRecyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    mAdapter = ventaProductoAdapter(empList)
                    ventasRecyclerView.adapter = mAdapter
                    mAdapter.searchDataList(filteredList)
                    mAdapter.setOnItemClickListener(object : ventaProductoAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val producto = mAdapter.getItem(position)
                            Toast.makeText(requireContext(), producto.nombreProducto, Toast.LENGTH_LONG).show();
                        }

                        override fun onItemVentaClick(position: Int) {
                            val producto = mAdapter.getItem(position)
                            cantidadVentaProductoDialog(producto.idProducto!!)
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error en caso de que la consulta no se complete
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error al obtener los productos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCarritoTotal() {
        dbVentaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totalSum = 0.0 // Reiniciar la suma

                for (ventaSnapshot in dataSnapshot.children) {
                    val venta = ventaSnapshot.getValue(VentaCarritoClass::class.java)
                    if (venta?.IdUser == userId) {
                        venta?.let {
                            dbProductosRef.child(it.idProducto!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(productoSnapshot: DataSnapshot) {
                                        val producto = productoSnapshot.getValue(ProductoClass::class.java)
                                        producto?.let { prov ->
                                            it.idProducto = prov.nombreProducto
                                            totalSum += it.precioTotal?.toDoubleOrNull()!!// Sumar el total de cada producto
                                            //si el resultado da con decimales mostrarlo con 2 decimales y si no pues mostrarlo sin decimales
                                            if (totalSum % 1 == 0.0) {
                                                fabCarritoVentas.labelText = "Total: S/${totalSum.toInt()}.00"
                                            } else {
                                                // Mostrar el precio total en el FloatingActionButton con 2 decimales si es que llega a tener
                                                val totalSumStr = String.format("%.2f", totalSum)
                                                fabCarritoVentas.labelText = "Total: S/$totalSumStr"
                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Error al obtener la información del proveedor
                                    }
                                })
                        }
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error en caso de que la consulta no se complete
                Toast.makeText(requireContext(), "Error al obtener los datos del carrito", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this@VentasFragment)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea el código QR")
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(requireActivity(), "Cancelado", Toast.LENGTH_SHORT).show()
            } else {
                // Almacenar el contenido escaneado en scannedQrContent
                scannedQrContent = result.contents

                // Mostrar el contenido escaneado en un TextView u otro elemento de la interfaz
                searchView.setQuery(scannedQrContent, true)

                // Realizar una búsqueda manual con el contenido escaneado
                searchList(scannedQrContent ?: "")
                Toast.makeText(requireActivity(), "El valor escaneado es: $scannedQrContent", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
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
         * @return A new instance of fragment VentasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VentasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}