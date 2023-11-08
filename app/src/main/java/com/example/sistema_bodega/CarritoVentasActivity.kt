package com.example.sistema_bodega

import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import kotlin.random.Random
import java.util.*
import kotlin.collections.ArrayList


import android.widget.Toast
import com.example.sistema_bodega.Adaptadores.carritoVentaAdapter
import com.example.sistema_bodega.Clases.*
import java.lang.Exception

class CarritoVentasActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var dbClienteRef: DatabaseReference
    private lateinit var pref: preferences//para el shared preferences
    private lateinit var nombreCliente_spinner: Spinner
    private lateinit var total_tv: TextView
    private lateinit var dbVentaRef: DatabaseReference

    // Agrega estas propiedades al inicio de tu Fragment
    private var totalSum: Double = 0.0
    private lateinit var spinnerIdsCliente: MutableList<String> // Declara spinnerIds como propiedad
    var userId: String? = null
    private lateinit var carritoVentaRecyclerView: RecyclerView
    lateinit var mAdapter: carritoVentaAdapter
    private lateinit var empList: ArrayList<VentaCarritoClass>
    private lateinit var dbProductosRef: DatabaseReference

    private lateinit var progressBar: RelativeLayout
    private lateinit var progressBarCarrito: RelativeLayout
    private lateinit var formaPago_spinner: Spinner
    var formaPagoSeleccionado = ""
    private lateinit var spinnerIdsFormaPago: MutableList<String> // Declara spinnerIds como propiedad

    private lateinit var dbTiketVentaRef: DatabaseReference
    private lateinit var dbdDetalleTicketVentaRef: DatabaseReference
    private lateinit var alertDialog: AlertDialog
    private lateinit var totalVenta_tv: TextView
    private lateinit var et_pagoCliente: TextInputEditText
    private lateinit var cambiovuelto_tv: TextView
    private lateinit var btnCerrar: ImageView
    private lateinit var guardarVenta_btn: AppCompatButton
    private lateinit var btnRegistroCliente: AppCompatButton
    private lateinit var  et_NombreCliente: TextInputEditText
    private lateinit var  et_DireccionCliente: TextInputEditText
    private lateinit var  et_CelularCliente: TextInputEditText
    private lateinit var  et_DniCliente: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito_ventas)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Carrito de ventas"
        // poner icono
        supportActionBar?.setIcon(R.drawable.ic_add_shopping_cart)
        pref = preferences(this)
        userId = pref.prefIdUser
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        dbVentaRef = FirebaseDatabase.getInstance().getReference("VentaCarrito")
        dbProductosRef = FirebaseDatabase.getInstance().getReference("Productos")
        dbTiketVentaRef = FirebaseDatabase.getInstance().getReference("TicketVentas")
        dbdDetalleTicketVentaRef = FirebaseDatabase.getInstance().getReference("DetalleTicketVentas")
        nombreCliente_spinner = findViewById(R.id.nombreCliente_spinner)
        formaPago_spinner = findViewById(R.id.formaPago_spinner)
        cargarSpinnerPago()
        total_tv = findViewById(R.id.total_tv)
        dbClienteRef = FirebaseDatabase.getInstance().getReference("Clientes")
        cargarSpinnerClientes()
        // Obtener la suma del total de la base de datos "VentaCarrito"
        getImporteTotal()
        carritoVentaRecyclerView = findViewById(R.id.carritoVentaRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        empList = arrayListOf<VentaCarritoClass>()
        carritoVentaRecyclerView.layoutManager = LinearLayoutManager(this)
        carritoVentaRecyclerView.setHasFixedSize(true)//para que no cambie el tamaño
        // Fetch and display the products in the RecyclerView
        getProductsFromFirebase()
    }

    private fun mostrarGuardarVentaDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_guardar_venta, null)
        dialogBuilder.setView(dialogView)

        //importe total de la venta
        val importeTotal = totalSum.toString()
        totalVenta_tv = dialogView.findViewById<TextView>(R.id.totalVenta_tv)
        et_pagoCliente = dialogView.findViewById<TextInputEditText>(R.id.et_pagoCliente)
        cambiovuelto_tv = dialogView.findViewById<TextView>(R.id.cambiovuelto_tv)
        progressBarCarrito = dialogView.findViewById<RelativeLayout>(R.id.progressBar)
        val totalSumStr = String.format("%.2f", totalSum)//para que solo muestre 2 decimales
        totalVenta_tv.text = "$totalSumStr"
        //totalVenta_tv.text = importeTotal

        //calcular cambio
        // Configura el TextWatcher para el campo de pago del cliente
        et_pagoCliente.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pagoClienteText = et_pagoCliente.text.toString()
                if (pagoClienteText.isNotEmpty()) {
                    val pagoCliente = pagoClienteText.toDouble()

                    if (pagoCliente >= importeTotal.toDouble()) {
                        val cambioVenta = pagoCliente - importeTotal.toDouble()
                        //para que solo muestre 2 decimales
                        val cambioVentaStr = String.format("%.2f", cambioVenta)
                        //mostrar cambio
                        cambiovuelto_tv.text = cambioVentaStr
                        //cambiovuelto_tv.text = cambioVenta.toString()
                    } else {
                        cambiovuelto_tv.text = "Pago insuficiente"
                    }
                } else {
                    cambiovuelto_tv.text = "0.0" // O algún otro valor predeterminado
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No se usa
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No se usa
            }
        })


        guardarVenta_btn = dialogView.findViewById<AppCompatButton>(R.id.guardarVenta_btn)
        guardarVenta_btn.setOnClickListener {
            val pagoClienteText = et_pagoCliente.text.toString()
            if (pagoClienteText.isNotEmpty()) {
                val pagoCliente = pagoClienteText.toDouble()
                if (pagoCliente >= importeTotal.toDouble()) {
                    TicketVentaProductos()//guardar ticket de venta
                } else {
                    cambiovuelto_tv.text = "Pago insuficiente"

                }
            } else {
                cambiovuelto_tv.text = "0.0" // O algún otro valor predeterminado
                et_pagoCliente.error = "Ingrese un monto"
            }
        }
        btnCerrar = dialogView.findViewById<ImageView>(R.id.btn_cerrar)
        btnCerrar.setOnClickListener {
            alertDialog.dismiss()//cerrar el dialog
        }
        alertDialog = dialogBuilder.create()
        alertDialog.show()


    }

    private fun TicketVentaProductos() {
        //desabilitar el boton guardar y mostrar el progressBar
        progressBarCarrito.visibility = View.VISIBLE
        guardarVenta_btn.isEnabled = false

        //obtener la fecha actual
        var fechaActual = Date(System.currentTimeMillis())
        //convertir formato de fecha: de "2020-11-10" a "10/11/2020"
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val fechaActualString = sdf.format(fechaActual)
        //obtener la hora actual
        val horaActual = Calendar.getInstance().time
        //convertir formato de hora: a 10:52:57
        val sdfHora = SimpleDateFormat("HH:mm:ss")
        val horaActualString = sdfHora.format(horaActual)

        //importe total de la venta
        //val importeTotal = totalSum.toString()
        val totalSumStr = String.format("%.2f", totalSum)//para que solo muestre 2 decimales
        //numero serie de la venta
        val numeroDeSerie = generarNumeroDeSerie()
        //Toast.makeText(this, "Numero de serie: $numeroDeSerie fecha actual $fechaActualString", Toast.LENGTH_LONG.show()

        //Guardar el id del cliente, id del usuario la fecha el precio de importe total en la bd "TicketVentaProductos"
        val idticket = dbTiketVentaRef.push().key
//si selecciono el cliente "Seleccione un cliente" guardar con el nombre del cliente "Cliente varios"
        if (nombreCliente_spinner.selectedItemPosition == 0) {
            val ticketVentaClass = TicketVentaClass(
                idticket!!,
                "Cliente varios",
                userId,
                fechaActualString,
                horaActualString,
                totalSumStr,
                formaPagoSeleccionado,
                "estado",
                numeroDeSerie,
                et_pagoCliente.text.toString(),
                cambiovuelto_tv.text.toString()
            )
            dbTiketVentaRef.child(idticket).setValue(ticketVentaClass).addOnCompleteListener {
                dbVentaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val addedProducts = mutableSetOf<String>() // Usamos un Set para asegurar elementos únicos
                        for (ventaCarrito in snapshot.children) {
                            val ventaCarritoClass = ventaCarrito.getValue(VentaCarritoClass::class.java)
                            if (ventaCarritoClass?.IdUser == userId) {
                                val productoId = ventaCarritoClass?.idProducto
                                dbProductosRef.child(productoId ?: "")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(productSnapshot: DataSnapshot) {
                                            if (productSnapshot.exists()) {
                                                // El producto existe en la tabla de productos
                                                if (!addedProducts.contains(productoId)) {
                                                    addedProducts.add(productoId ?: "")

                                                    // Aquí puedes agregar el producto a la tabla DetalleTicketVentas
                                                    val idDetalleTicket = dbdDetalleTicketVentaRef.push().key
                                                    val detalleTicketVentaClass = detalleTicketClass(
                                                        idDetalleTicket!!,
                                                        idticket,
                                                        productoId,
                                                        ventaCarritoClass!!.cantidad,
                                                        ventaCarritoClass.precioTotal
                                                    )
                                                    dbdDetalleTicketVentaRef.child(idDetalleTicket)
                                                        .setValue(detalleTicketVentaClass)
                                                        .addOnCompleteListener {
                                                            dbVentaRef.child(ventaCarritoClass.IdCarritoVenta!!)
                                                                .removeValue()
                                                                .addOnCompleteListener {
                                                                    //Habilitar el boton guardar y ocultar el progressBar
                                                                    guardarVenta_btn.isEnabled = true
                                                                    progressBarCarrito.visibility = View.GONE
                                                                    Toast.makeText(
                                                                        this@CarritoVentasActivity,
                                                                        "Venta realizada",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    totalSum = 0.0
                                                                    alertDialog.dismiss()
                                                                    finish()
                                                                }
                                                        }
                                                        .addOnFailureListener() {
                                                            //Habilitar el boton guardar y ocultar el progressBar
                                                            guardarVenta_btn.isEnabled = true
                                                            progressBarCarrito.visibility = View.GONE
                                                            Toast.makeText(
                                                                this@CarritoVentasActivity,
                                                                "Error al guardar el detalle de la venta",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                            } else {
                                                // El producto no existe en la tabla de productos
                                                Toast.makeText(
                                                    this@CarritoVentasActivity,
                                                    "El producto no existe en la base de datos",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(
                                                this@CarritoVentasActivity,
                                                "Error al verificar el producto",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    })
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@CarritoVentasActivity, "Error al guardar el ticket", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar el ticket", Toast.LENGTH_SHORT).show()
            }
        } else {
            //obtener el id del cliente seleccionado, el id del usuario logueado, la fecha, el precio total, la forma de pago, el estado y la serie
            var clienteSeleccionado = spinnerIdsCliente[nombreCliente_spinner.selectedItemPosition - 1]
            val ticketVentaClass = TicketVentaClass(
                idticket!!,
                clienteSeleccionado,
                userId,
                fechaActualString,
                horaActualString,
                totalSumStr,
                formaPagoSeleccionado,
                "estado",
                numeroDeSerie,
                et_pagoCliente.text.toString(),
                cambiovuelto_tv.text.toString()
            )
            //guardar en la bd "TicketVentaProductos"
            dbTiketVentaRef.child(idticket).setValue(ticketVentaClass).addOnCompleteListener {
                dbVentaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val addedProducts = mutableSetOf<String>() // Usamos un Set para asegurar elementos únicos

                        for (ventaCarrito in snapshot.children) {
                            val ventaCarritoClass = ventaCarrito.getValue(VentaCarritoClass::class.java)
                            if (ventaCarritoClass?.IdUser == userId) {
                                val productoId = ventaCarritoClass?.idProducto

                                dbProductosRef.child(productoId ?: "")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(productSnapshot: DataSnapshot) {
                                            if (productSnapshot.exists()) {
                                                // El producto existe en la tabla de productos
                                                if (!addedProducts.contains(productoId)) {
                                                    addedProducts.add(productoId ?: "")

                                                    // Aquí puedes agregar el producto a la tabla DetalleTicketVentas
                                                    val idDetalleTicket = dbdDetalleTicketVentaRef.push().key
                                                    val detalleTicketVentaClass = detalleTicketClass(
                                                        idDetalleTicket!!,
                                                        idticket,
                                                        productoId,
                                                        ventaCarritoClass!!.cantidad,
                                                        ventaCarritoClass.precioTotal
                                                    )
                                                    dbdDetalleTicketVentaRef.child(idDetalleTicket)
                                                        .setValue(detalleTicketVentaClass)
                                                        .addOnCompleteListener {
                                                            dbVentaRef.child(ventaCarritoClass.IdCarritoVenta!!)
                                                                .removeValue()
                                                                .addOnCompleteListener {
                                                                    //Habilitar el boton guardar y ocultar el progressBar
                                                                    guardarVenta_btn.isEnabled = true
                                                                    progressBarCarrito.visibility = View.GONE
                                                                    Toast.makeText(
                                                                        this@CarritoVentasActivity,
                                                                        "Venta realizada",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    totalSum = 0.0
                                                                    alertDialog.dismiss()
                                                                    finish()
                                                                }
                                                        }
                                                        .addOnFailureListener() {
                                                            //Habilitar el boton guardar y ocultar el progressBar
                                                            guardarVenta_btn.isEnabled = true
                                                            progressBarCarrito.visibility = View.GONE
                                                            Toast.makeText(
                                                                this@CarritoVentasActivity,
                                                                "Error al guardar el detalle de la venta",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                            } else {
                                                // El producto no existe en la tabla de productos
                                                Toast.makeText(
                                                    this@CarritoVentasActivity,
                                                    "El producto no existe en la base de datos",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(
                                                this@CarritoVentasActivity,
                                                "Error al verificar el producto",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    })
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@CarritoVentasActivity, "Error al guardar el ticket", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar el ticket", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun generarNumeroDeSerie(): String {
        val numeroDeSerie = StringBuilder()

        // Generar los primeros 8 dígitos aleatorios
        for (i in 1..8) {
            val digito = Random.nextInt(10) // Genera un número aleatorio entre 0 y 9
            numeroDeSerie.append(digito)
        }

        // Asegurarse de que el número de serie tenga 9 dígitos
        while (numeroDeSerie.length < 9) {
            numeroDeSerie.insert(0, '0') // Agrega ceros a la izquierda hasta tener 9 dígitos
        }

        return numeroDeSerie.toString()
    }

    private fun getProductsFromFirebase() {
        // Ocultar el recyclerview y mostrar el progressBar
        carritoVentaRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
// Observe the "VentaCarrito" node in Firebase
        dbVentaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                empList.clear() // Clear the existing list before adding new items
                for (ventaSnapshot in dataSnapshot.children) {
                    val venta = ventaSnapshot.getValue(VentaCarritoClass::class.java)
                    if (venta?.IdUser == userId) {
                        venta?.let {
                            //empList.add(it)
                            // Obtener el ID del proveedor para obtener su información
                            val prodcutoId = it.idProducto

                            // Realizar la consulta para obtener la información nombre del producto
                            dbProductosRef.child(prodcutoId!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(productoSnapshot: DataSnapshot) {
                                        val producto = productoSnapshot.getValue(ProductoClass::class.java)
                                        producto?.let { prov ->
                                            it.idProducto = prov.nombreProducto
                                            //imagen del producto
                                            it.imageUrl = prov.imageUrl
                                            empList.add(it)

                                            mAdapter.notifyDataSetChanged()
                                            // Mostrar el recyclerview y ocultar el progressBar
                                            progressBar.visibility = View.GONE
                                            carritoVentaRecyclerView.visibility = View.VISIBLE
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Error al obtener la información del proveedor
                                    }
                                })
                        }
                    }
                }
                mAdapter = carritoVentaAdapter(empList)
                carritoVentaRecyclerView.adapter = mAdapter
                mAdapter.setOnItemClickListener(object : carritoVentaAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val productos = mAdapter.getItem(position)
                        Toast.makeText(
                            this@CarritoVentasActivity,
                            "Producto: " + productos.IdCarritoVenta,
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onItemQuitarVentaClick(position: Int) {
                        val productos = mAdapter.getItem(position)
                        AlertDialog.Builder(this@CarritoVentasActivity)
                            .setTitle("Quitar producto")
                            .setMessage("¿Estás seguro de quitar este producto del carrito?")
                            .setPositiveButton("Sí") { _, _ ->
                                try {
                                    //codigo
                                    val dbQuitarCarritoRef =
                                        FirebaseDatabase.getInstance().getReference("VentaCarrito")
                                            .child(productos.IdCarritoVenta!!)
                                    dbQuitarCarritoRef.removeValue()
                                        .addOnSuccessListener { // File deleted successfully
                                            Toast.makeText(
                                                this@CarritoVentasActivity,
                                                "Eliminado: " + productos.IdCarritoVenta,
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            mAdapter.notifyDataSetChanged()
                                        }.addOnFailureListener { error ->
                                            Toast.makeText(
                                                this@CarritoVentasActivity,
                                                "Deleting Err ${error.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Uh-oh, an error occurred!
                                        }
                                } catch (e: Exception) {
                                    //caso que hubiera fallas en el codigo
                                }
                            }
                            .setNegativeButton("No") { _, _ ->
                                // No hacer nada
                            }
                            .show()
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@CarritoVentasActivity,
                    "Error al obtener los datos del carrito",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun getImporteTotal() {
        dbVentaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totalSum = 0.0 // Reiniciar la suma

                for (ventaSnapshot in dataSnapshot.children) {
                    val venta = ventaSnapshot.getValue(VentaCarritoClass::class.java)
                    if (venta?.IdUser == userId) {
                        venta?.let {// Obtener el ID del proveedor para obtener su información
                            dbProductosRef.child(it.idProducto!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(productoSnapshot: DataSnapshot) {
                                        val producto = productoSnapshot.getValue(ProductoClass::class.java)
                                        producto?.let { prov ->
                                            it.idProducto = prov.nombreProducto
                                            totalSum += it.precioTotal?.toDoubleOrNull()!!
                                            //si el resultado da con decimales mostrarlo con 2 decimales y si no pues mostrarlo sin decimales
                                            if (totalSum % 1 == 0.0) {
                                                total_tv.text = "Total: S/${totalSum.toInt()}.00"
                                            } else {
                                                // Mostrar el precio total en el FloatingActionButton con 2 decimales si es que llega a tener
                                                val totalSumStr = String.format("%.2f", totalSum)
                                                total_tv.text = "Total: S/$totalSumStr"
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
                Toast.makeText(
                    this@CarritoVentasActivity,
                    "Error al obtener los datos del carrito",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    private fun cargarSpinnerPago() {
        val formaPago = arrayOf("Forma de pago", "Efectivo", "Tarjeta de crédito", "Tarjeta de débito")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formaPago)
        formaPago_spinner.adapter = adapter
// Agregar el listener para capturar la selección del Spinner
        formaPago_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) { // Obtener el valor seleccionado del Spinner
                val formaPago = parent.getItemAtPosition(position).toString()
                if (formaPago != "Forma de pago") {
// Se seleccionó un elemento válido
                    if (formaPago == "Efectivo") {
                        formaPagoSeleccionado = "Efectivo"// Guardar el valor seleccionado
                    } else if (formaPago == "Tarjeta de crédito") {
                        formaPagoSeleccionado = "Tarjeta de crédito"
                    } else if (formaPago == "Tarjeta de débito") {
                        formaPagoSeleccionado = "Tarjeta de débito"
                    }
                } else {
//mensaje toast que debe seleccionar una forma de pago
//Toast.makeText(this@CarritoVentasActivity, "Seleccione forma de pago", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {
// No hacer nada
            }
        }

    }

    private fun cargarSpinnerClientes() {
// Escuchar los datos de Firebase y configurar el adaptador del Spinner
        dbClienteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val spinnerData: MutableList<String> = mutableListOf("Seleccionar cliente")
                spinnerIdsCliente = mutableListOf() // Inicializa spinnerIds

// Iterar a través de los datos obtenidos del dataSnapshot
                for (snapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(ClientesClass::class.java)
                    if (value?.idUuser == userId) {
                        value?.nombreCliente?.let {
                            val idCliente = snapshot.key
                            val nombreCliente = value.nombreCliente
                            if (idCliente != null && nombreCliente != null) { // Verificar que el ID y el nombre del proveedor no sean nulos
                                val spinnerItem = "$nombreCliente" // Combinar ID y nombre del proveedor
                                spinnerData.add(spinnerItem)
                                spinnerIdsCliente.add(idCliente)
                            }
                        }
                    }
                }

// Configurar el adaptador del Spinner
                val adapter = ArrayAdapter<String>(
                    this@CarritoVentasActivity,
                    android.R.layout.simple_spinner_item,
                    spinnerData
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Asignar el adaptador al Spinner
                nombreCliente_spinner.adapter = adapter
// Agregar el listener para capturar la selección del Spinner
                nombreCliente_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedData = spinnerData[position] // Obtener el elemento seleccionado del Spinner
                        if (selectedData == "Seleccionar cliente") {
                            // Se seleccionó la opción "Seleccionar opción"
                            // Realiza las acciones necesarias (por ejemplo, limpiar el RecyclerView)
                        } else {
                            // Se seleccionó un elemento válido
                            val selectedItemId =
                                spinnerIdsCliente[nombreCliente_spinner.selectedItemPosition - 1] // Restar 1 para obtener el ID correspondiente al elemento seleccionado
                            /*Toast.makeText(
                                this@RegistrarProductoActivity,
                                "ID: $selectedItemId" + " Texto: ${selectedData.toString()}",
                                Toast.LENGTH_SHORT
                            ).show() // Mostrar el ID en un Toast*/
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No se seleccionó ningún elemento
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
// Manejo de errores, si es necesario
            }
        })
    }

    private fun openRegistroClienteDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.registrar_cliente_alert, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
        et_NombreCliente = dialogView.findViewById<TextInputEditText>(R.id.et_nombreCliente)
        et_CelularCliente = dialogView.findViewById<TextInputEditText>(R.id.et_celularCliente)
        progressBar = dialogView.findViewById<RelativeLayout>(R.id.progressBar)
        et_DniCliente = dialogView.findViewById<TextInputEditText>(R.id.et_DniCliente)
        btnRegistroCliente = dialogView.findViewById<AppCompatButton>(R.id.btn_registrarCliente)
        btnCerrar = dialogView.findViewById<ImageView>(R.id.btn_cerrar)
        btnRegistroCliente.setOnClickListener {
            if (validarDniCliente() && validarCelularCliente() && validarNombreCliente()) {
                //validando si celular tiene 9 digitos
                if (et_CelularCliente.text.toString().length == 9) {
                    //validando si el dni tiene 8 digitos
                    if (et_DniCliente.text.toString().length == 8) {
                        registrarData()
                    } else {
                        et_DniCliente.error = "El DNI debe tener 8 digitos"
                    }
                } else {
                    et_CelularCliente.error = "El celular debe tener 9 digitos"
                }
            } else {
                Toast.makeText(this, "Completa los datos", Toast.LENGTH_SHORT).show()
            }
        }
        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.venderMenu -> {
                if (userId != null) {//si hay usuario logueado
                    if (formaPagoSeleccionado != "") {
                        if (totalSum > 0) {//si hay productos en el carrito
                            //si selecciono un cliente, Agregar la venta a la base de datos "Ventas"
                            mostrarGuardarVentaDialog()
                        } else {
                            Toast.makeText(this, "No hay productos en el carrito", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Seleccione una forma de pago", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.addClienteMenu -> {
                openRegistroClienteDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_carrito, menu)

        return super.onCreateOptionsMenu(menu)
    }

    private fun registrarData() {
        val NombreCliente = et_NombreCliente.text.toString()
        val DniCliente = et_DniCliente.text.toString()
        val CelularCliente = et_CelularCliente.text.toString()

        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBar.visibility = View.VISIBLE
        btnRegistroCliente.isEnabled = false

        val id = dbClienteRef.push().key
        val cliente = ClientesClass(id!!, NombreCliente, DniCliente, CelularCliente, pref.prefIdUser)
        dbClienteRef.child(id).setValue(cliente).addOnCompleteListener {
            // Habilitar el botón de guardar y ocultar el ProgressBar
            btnRegistroCliente.isEnabled = true
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Cliente registrado correctamente!", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()

        }.addOnFailureListener { exception ->
            // Habilitar el botón de guardar y ocultar el ProgressBar
            btnRegistroCliente.isEnabled = true
            progressBar.visibility = View.GONE
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun validarDniCliente(): Boolean {
        val DniCliente = et_DniCliente.text.toString()
        return if (DniCliente.isEmpty()) {
            et_DniCliente.error = "El D.N.I del proveedor es obligatorio"
            false
        } else {
            et_DniCliente.error = null
            true
        }
    }

    private fun validarNombreCliente(): Boolean {
        val NombreProveedor = et_NombreCliente.text.toString()
        return if (NombreProveedor.isEmpty()) {
            et_NombreCliente.error = "El nombre del cliente es obligatorio"
            false
        } else {
            et_NombreCliente.error = null
            true
        }
    }

    private fun validarCelularCliente(): Boolean {
        val CelularProveedor = et_CelularCliente.text.toString()
        return if (CelularProveedor.isEmpty()) {
            et_CelularCliente.error = "El celular del proveedor es obligatorio"
            false
        } else {
            et_CelularCliente.error = null
            true
        }
    }
}