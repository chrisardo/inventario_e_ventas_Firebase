package com.example.sistema_bodega

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_bodega.Adaptadores.detalleVentaProductosAdapter
import com.example.sistema_bodega.Clases.ProductoClass
import com.example.sistema_bodega.Clases.TicketVentaClass
import com.example.sistema_bodega.Clases.detalleTicketClass
import com.google.firebase.database.*

class DetalleVentaActivity : AppCompatActivity() {
    private lateinit var dbTicketVentasRef: DatabaseReference
    private lateinit var dbClientesRef: DatabaseReference
    private lateinit var dbDetalleListaVentaRef: DatabaseReference
    private lateinit var serieVenta_tv: TextView
    private lateinit var fechaventa_tv: TextView
    private lateinit var horaventa_tv: TextView
    private lateinit var tipoPago_tv: TextView
    private lateinit var totalVenta_tv: TextView
    private lateinit var nombreCliente_tv: TextView
    private lateinit var bundle: Bundle
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var progressBar: LinearLayout
    private lateinit var detalleVentaProductoRv: RecyclerView
    private lateinit var empList: ArrayList<detalleTicketClass>
    lateinit var mAdapter: detalleVentaProductosAdapter
    private lateinit var dbProductosRef: DatabaseReference
    private lateinit var vueltoCliente_tv: TextView
    private lateinit var pagoCliente_tv: TextView
    var idTicketVenta: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_venta)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        pref = preferences(this)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        userId = pref.prefIdUser
        dbTicketVentasRef = FirebaseDatabase.getInstance().getReference("TicketVentas")
        dbClientesRef = FirebaseDatabase.getInstance().getReference("Clientes")

        serieVenta_tv = findViewById(R.id.serieVenta_tv)
        fechaventa_tv = findViewById(R.id.fechaventa_tv)
        horaventa_tv = findViewById(R.id.horaventa_tv)
        tipoPago_tv = findViewById(R.id.tipoPago_tv)
        totalVenta_tv = findViewById(R.id.totalVenta_tv)
        nombreCliente_tv = findViewById(R.id.nombreCliente_tv)
        pagoCliente_tv = findViewById(R.id.pagoCliente_tv)
        vueltoCliente_tv = findViewById(R.id.vueltoCliente_tv)
        progressBar = findViewById(R.id.progressBar)
        detalleVentaProductoRv = findViewById(R.id.detalleVentaProductoRv)
        empList = arrayListOf<detalleTicketClass>()
        detalleVentaProductoRv.layoutManager = LinearLayoutManager(this)

        bundle = intent.extras!!
        idTicketVenta = bundle.getString("idTicketVenta")

        obtenerDetalleListaProductos()
        val checkTicketVentaDatabase = dbTicketVentasRef.orderByChild("idTicketVenta").equalTo(idTicketVenta)
        checkTicketVentaDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ticketVenta in snapshot.children) {
                        val serieVenta = ticketVenta.child("serieVenta").getValue(String::class.java)
                        val fechaVenta = ticketVenta.child("fechaVenta").getValue(String::class.java)
                        val horaVenta = ticketVenta.child("horaVenta").getValue(String::class.java)
                        val formaPago = ticketVenta.child("formaPago").getValue(String::class.java)
                        val totalVenta = ticketVenta.child("totalVenta").getValue(String::class.java)
                        val pagoCliente = ticketVenta.child("pagoCliente").getValue(String::class.java)
                        val vuelto = ticketVenta.child("vuelto").getValue(String::class.java)
                        val idCliente = ticketVenta.child("idCliente").getValue(String::class.java)
                        val ticketVenta = ticketVenta.getValue(TicketVentaClass::class.java)
                        serieVenta_tv.text = serieVenta
                        fechaventa_tv.text = fechaVenta
                        horaventa_tv.text = horaVenta
                        tipoPago_tv.text = formaPago
                        totalVenta_tv.text = totalVenta
                        pagoCliente_tv.text = pagoCliente
                        vueltoCliente_tv.text = vuelto
                        if (ticketVenta?.idUser == userId) {
                            //si el id del cliente de la tabla historial dice "cliente varios"
                            if (ticketVenta?.idCliente == "Cliente varios") {
                                nombreCliente_tv.text = ticketVenta?.idCliente
                            } else {
                                val checkProveedorDatabase =
                                    dbClientesRef.orderByChild("idCliente").equalTo(ticketVenta?.idCliente)
                                checkProveedorDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            for (cliente in snapshot.children) {
                                                val nombreCliente =
                                                    cliente.child("nombreCliente").getValue(String::class.java)
                                                /*val apellidoCliente = cliente.child("apellidoCliente").getValue(String::class.java)
                                            val nombreCompleto = "$nombreCliente $apellidoCliente"*/

                                                nombreCliente_tv.text = nombreCliente
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            this@DetalleVentaActivity,
                                            "Error al cargar los datos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                            }
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetalleVentaActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun obtenerDetalleListaProductos() {

        //Mostrar el progressbar y ocultar el recyclerview
        detalleVentaProductoRv.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        //Llenar el recyclerview de acuerdo al idTicketVenta
        dbDetalleListaVentaRef = FirebaseDatabase.getInstance().getReference("DetalleTicketVentas")
        dbProductosRef = FirebaseDatabase.getInstance().getReference("Productos")
        val checkdbDetalleListaVenta = dbDetalleListaVentaRef.orderByChild("idTicketVenta").equalTo(idTicketVenta)
        checkdbDetalleListaVenta.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                if (snapshot.exists()) {
                    for (detalleTicketVenta in snapshot.children) {
                        val detalleVentas = detalleTicketVenta.getValue(detalleTicketClass::class.java)
                        //condicion para verificar el idticketventa
                        if (detalleVentas?.idTicketVenta == idTicketVenta) {
                            //empList.add(detalleVentas!!)
                            detalleVentas?.let {
                                // Obtener el ID del proveedor para obtener su información
                                val productoId = it.idProducto
                                // Realizar la consulta para obtener la información del producto
                                dbProductosRef.child(productoId!!)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(clienteSnapshot: DataSnapshot) {
                                            val productoClass = clienteSnapshot.getValue(ProductoClass::class.java)

                                            productoClass?.let { prov ->
                                            //nombre y el precio del producto en el detalle de la venta
                                                it.idProducto = prov.nombreProducto
                                                it.detalleprecioProducto = prov.precioProducto
                                                //imagen
                                                it.imageUrl= prov.imageUrl

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
                        detalleVentaProductoRv.visibility = View.VISIBLE
                        mAdapter = detalleVentaProductosAdapter(empList)
                        detalleVentaProductoRv.adapter = mAdapter

                }else{
                    progressBar.visibility = View.GONE
                    detalleVentaProductoRv.visibility == View.VISIBLE
                    Toast.makeText(this@DetalleVentaActivity, "No hay productos en la lista", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetalleVentaActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.eliminarDetalle-> {
                //Eliminar el detale de la venta con sus productos vendidos
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Eliminar")
                builder.setMessage("¿Está seguro de eliminar el detalle de la venta?")
                builder.setPositiveButton("Si") { dialog, which ->
                    //Eliminar el detalle de la venta
                    dbDetalleListaVentaRef .child(idTicketVenta!!).removeValue()
                    //Eliminar el ticket de la venta
                    dbTicketVentasRef.child(idTicketVenta!!).removeValue()
                    Toast.makeText(this, "Se eliminó el detalle de la venta", Toast.LENGTH_SHORT).show()
                    finish()
                }
                builder.setNegativeButton("No") { dialog, which ->
                    //No hacer nada
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

                return true
            }
            R.id.reportepdf-> {
                //Enviar el id del ticketVenta al activity de DescarTicketVentaPdf
                val intent = Intent(this, DescargarTicketVentaPdf::class.java)
                intent.putExtra("idTicketVenta", idTicketVenta)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detalle_ticket_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }
}