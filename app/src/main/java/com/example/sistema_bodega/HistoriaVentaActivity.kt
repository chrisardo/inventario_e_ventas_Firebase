package com.example.sistema_bodega

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_bodega.Adaptadores.historialVentaAdapter
import com.example.sistema_bodega.Clases.ClientesClass
import com.example.sistema_bodega.Clases.TicketVentaClass
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class HistoriaVentaActivity : AppCompatActivity() {
    private lateinit var progressBar: LinearLayout

    private lateinit var historialVentaRv: RecyclerView
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null

    private lateinit var empList: ArrayList<TicketVentaClass>
    private lateinit var dbTicketVentasRef: DatabaseReference
    private lateinit var dbClientesRef: DatabaseReference
    lateinit var mAdapter: historialVentaAdapter
    private lateinit var searchView: SearchView
    private lateinit var tvHistorialVentasEncontra: TextView
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var dbDetalleListaVentaRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historia_venta)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Historial de ventas"
        // poner icono
        supportActionBar?.setIcon(R.drawable.ic_update)
        pref = preferences(this)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        searchView = findViewById<SearchView>(R.id.searchView) as SearchView
        searchView.queryHint = "Buscar historial por serie de venta"
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
        progressBar = findViewById(R.id.progressBar)
        tvHistorialVentasEncontra = findViewById(R.id.tvHistorialVentasEncontra)
        historialVentaRv = findViewById(R.id.historiaVentaRv)
        empList = arrayListOf<TicketVentaClass>()
        historialVentaRv.layoutManager = LinearLayoutManager(this)
       // historialVentaRv.setHasFixedSize(true)
        userId = pref.prefIdUser

        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected) {//si hay internet

            getHistorialData()
        } else {
            //Toast.makeText(requireContext(), "No hay internet", Toast.LENGTH_SHORT).show()
            showConnectivityError()
        }
    }
    private fun getHistorialData() {
        historialVentaRv.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        dbTicketVentasRef = FirebaseDatabase.getInstance().getReference("TicketVentas")
        dbClientesRef = FirebaseDatabase.getInstance().getReference("Clientes")
        dbTicketVentasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                var historialCount = 0 // Agregar variable contador
                for (historialSnapshot in snapshot.children) {
                    val historialVenta= historialSnapshot.getValue(TicketVentaClass::class.java)
                    val totalHistorialCount = snapshot.childrenCount.toInt()
                    if (historialVenta?.idUser == userId) {
                        if (totalHistorialCount > 0) {
                            tvHistorialVentasEncontra.text = "Total de historial de ventas: $totalHistorialCount"
                        } else {
                            tvHistorialVentasEncontra.text = "No hay historial de ventas"
                        }
                        //si el id del cliente de la tabla historial dice "cliente varios"
                        if (historialVenta?.idCliente == "Cliente varios") {
                            historialVenta.idCliente = "Cliente varios"
                            empList.add(historialVenta!!)
                            //mAdapter.notifyDataSetChanged()
                        } else {
                            historialVenta?.let {
                                // Obtener el ID del proveedor para obtener su información
                                val clienteId = it.idCliente
                                dbClientesRef.child(clienteId!!)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(clienteSnapshot: DataSnapshot) {
                                            val clientesClass = clienteSnapshot.getValue(ClientesClass::class.java)
                                            clientesClass?.let { prov ->
                                                it.idCliente = prov.nombreCliente
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

                }
                progressBar.visibility = View.GONE
                historialVentaRv.visibility = View.VISIBLE
                mAdapter = historialVentaAdapter(empList)
                historialVentaRv.adapter = mAdapter
                mAdapter.setOnItemClickListener(object : historialVentaAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val historialVentas = mAdapter.getItem(position)
                        val intent = Intent(this@HistoriaVentaActivity, DetalleVentaActivity::class.java)
                        intent.putExtra("idTicketVenta", historialVentas?.idTicketVenta)
                        startActivity(intent)
                    }
                    override fun onItemMenuClick(position: Int, view: View) {
                        val popupMenus = PopupMenu(this@HistoriaVentaActivity, view)
                        popupMenus.inflate(R.menu.show_menu_historial_rv)
                        popupMenus.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.descargarTicket -> {
                                    val historialVentas = mAdapter.getItem(position)
                                    val intent = Intent(this@HistoriaVentaActivity, DescargarTicketVentaPdf::class.java)
                                    intent.putExtra("idTicketVenta", historialVentas?.idTicketVenta)
                                    startActivity(intent)
                                }
                                R.id.deleteHistorial_menu -> {
                                    val historialVentas = mAdapter.getItem(position)

                                    dbDetalleListaVentaRef = FirebaseDatabase.getInstance().getReference("DetalleTicketVentas")
                                    //Eliminar el detale de la venta con sus productos vendidos
                                    val builder = AlertDialog.Builder(this@HistoriaVentaActivity)
                                    builder.setTitle("Eliminar")
                                    builder.setMessage("¿Está seguro de eliminar el detalle de la venta?")
                                    builder.setPositiveButton("Si") { dialog, which ->
                                        //Eliminar el detalle de la venta
                                        dbDetalleListaVentaRef .child(historialVentas?.idTicketVenta!!).removeValue()
                                        //Eliminar el ticket de la venta
                                        dbTicketVentasRef.child(historialVentas?.idTicketVenta!!).removeValue()
                                        Toast.makeText(this@HistoriaVentaActivity, "Se eliminó el detalle de la venta", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    builder.setNegativeButton("No") { dialog, which ->
                                        //No hacer nada
                                    }
                                    val dialog: AlertDialog = builder.create()
                                    dialog.show()
                                }
                            }
                            false
                        }
                        popupMenus.show()
                        // Opcional: Mostrar íconos en el menú emergente
                        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                        popup.isAccessible = true
                        val menu = popup.get(popupMenus)
                        menu.javaClass
                            .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                            .invoke(menu, true)
                    }

                })
            }
            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                historialVentaRv.visibility = View.VISIBLE
            }
        })
    }

    fun searchList(text: String) {
        val searchList = java.util.ArrayList<TicketVentaClass>()
        for (dataClass in empList) {
            if (dataClass.serieVenta?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true ||
                dataClass.totalVenta?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }
        mAdapter.searchDataList(searchList)
    }
    private fun showConnectivityError() {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        dialogBuilder.setMessage("No hay conexión a Internet. Por favor, conéctate y vuelve a intentarlo.")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                //finish()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}