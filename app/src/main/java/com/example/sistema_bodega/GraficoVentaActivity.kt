package com.example.sistema_bodega

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.echo.holographlibrary.Bar
import com.echo.holographlibrary.BarGraph
import com.example.sistema_bodega.Adaptadores.detalleVentaProductosAdapter
import com.example.sistema_bodega.Clases.ProductoClass
import com.example.sistema_bodega.Clases.detalleTicketClass
import com.google.firebase.database.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class GraficoVentaActivity : AppCompatActivity() {
    private lateinit var yearPicker: NumberPicker
    private lateinit var yearPicker2: NumberPicker
    private lateinit var monthPicker2: NumberPicker
    private lateinit var dbTicketVentasRef: DatabaseReference
    private lateinit var dbDetalleListaVentaRef: DatabaseReference
    private lateinit var dbProductosRef: DatabaseReference
    private lateinit var puntosMeses: ArrayList<Bar>
    private lateinit var puntosProductosVendidos: ArrayList<Bar>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafico_venta)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Graficas y estadisticas de ventas"
        // poner icono
        supportActionBar?.setIcon(R.drawable.ic_analytics)

        dbTicketVentasRef = FirebaseDatabase.getInstance().getReference("TicketVentas")
        dbDetalleListaVentaRef =
            FirebaseDatabase.getInstance().getReference("DetalleTicketVentas") // Inicializa dbDetalleListaVentaRef
        dbProductosRef = FirebaseDatabase.getInstance().getReference("Productos")
        // Obtiene el año actual
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        // Para la grafica de ventas por año
        yearPicker = findViewById<NumberPicker>(R.id.yearPicker)
        // Configura el NumberPicker para mostrar los años
        yearPicker.minValue = 1900 // Cambia esto al año mínimo que desees
        yearPicker.maxValue = currentYear // Hasta el año actual
        yearPicker.value = currentYear // Establece el valor predeterminado en el año actual
        yearPicker.wrapSelectorWheel = false // Evita que el número vuelva a empezar
        puntosMeses = ArrayList<Bar>()
        yearPicker.setOnValueChangedListener { _, _, newYear ->
            // Cuando se selecciona un nuevo año, actualiza los datos de los meses de ventas
            graficarBarrasVentasAno(puntosMeses, newYear)
        }

        //Para la grafica de ventas por año y mes de las cantidades de los productos
        yearPicker2 = findViewById<NumberPicker>(R.id.yearPicker2)
        monthPicker2 = findViewById<NumberPicker>(R.id.monthPicker2)
        // Configura el NumberPicker para mostrar los años
        yearPicker2.minValue = 1900 // Cambia esto al año mínimo que desees
        yearPicker2.maxValue = currentYear // Hasta el año actual
        yearPicker2.value = currentYear // Establece el valor predeterminado en el año actual
        yearPicker2.wrapSelectorWheel = false // Evita que el número vuelva a empezar
        puntosProductosVendidos = ArrayList<Bar>()
        // Configura el NumberPicker para los meses
        val monthNames = DateFormatSymbols().months
        monthPicker2.minValue = 0 // Enero es 0, febrero es 1, etc.
        monthPicker2.maxValue = 11 // Diciembre es 11
        monthPicker2.displayedValues = monthNames // Muestra los nombres de los meses
        monthPicker2.value = currentMonth // Establece el valor predeterminado en el mes actual
        monthPicker2.wrapSelectorWheel = false // Evita que el número vuelva a empezar
        yearPicker2.setOnValueChangedListener { _, _, _ ->
            // Cuando se selecciona un nuevo año, actualiza los datos de los meses de ventas
            graficarBarrasVentasAnoMesProducto(puntosProductosVendidos)
        }

        monthPicker2.setOnValueChangedListener { _, _, _ ->
            // Cuando se selecciona un nuevo mes, actualiza los datos de los meses de ventas
            graficarBarrasVentasAnoMesProducto(puntosProductosVendidos)
        }
    }

    // funcion para mostrar la grafica de ventas por año y mes de las cantidades de los productos
    private fun graficarBarrasVentasAnoMesProducto(puntosProductosVendidos: ArrayList<Bar>) {
        val selectedYear = yearPicker2.value
        val selectedMonth = monthPicker2.value + 1
        val selectedMonthName = DateFormatSymbols().months[selectedMonth - 1]// Obtiene el nombre del mes seleccionado

        // Inicializa la lista de productos vendidos y cantidades vendidas
        val productosVendidos = ArrayList<String>()
        val cantidadesVendidas = ArrayList<Float>()

        dbTicketVentasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    puntosProductosVendidos.clear() // Limpia la lista antes de agregar nuevos datos
                    for (ticketVenta in snapshot.children) {
                        val fechaVentaTicket = ticketVenta.child("fechaVenta").getValue(String::class.java)
                        val yearVentaTicket = fechaVentaTicket!!.split("/")[2].toInt()
                        val monthVentaTicket = fechaVentaTicket.split("/")[1].toInt()
                        // Filtra por año y mes seleccionados
                        if (yearVentaTicket == selectedYear && monthVentaTicket == selectedMonth) {
                            val idVentaTicket = ticketVenta.child("idTicketVenta").getValue(String::class.java)
                            val checkdbDetalleListaVenta =
                                dbDetalleListaVentaRef.orderByChild("idTicketVenta").equalTo(idVentaTicket)
                            checkdbDetalleListaVenta.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (detalleTicketVenta in snapshot.children) {
                                            val detalleVentas =
                                                detalleTicketVenta.getValue(detalleTicketClass::class.java)
                                            if (detalleVentas?.idTicketVenta == idVentaTicket) {
                                                detalleVentas?.let {
                                                    val productoId = detalleTicketVenta.child("idProducto")
                                                        .getValue(String::class.java)
                                                    val cantidadProductoStr =
                                                        detalleTicketVenta.child("precioSubTotalProducto")
                                                            .getValue(String::class.java)
                                                    val cantidadProducto = cantidadProductoStr?.toFloatOrNull() ?: 0f
                                                    productosVendidos.add(productoId!!) // Agrega el producto vendido
                                                    cantidadesVendidas.add(cantidadProducto) // Agrega la cantidad vendida
                                                    // Unir con la tabla "Productos" para obtener los nombres de los productos por medio de su pruductoId
                                                    // Realiza una consulta adicional para obtener el nombre del producto
                                                    dbProductosRef.child(productoId)
                                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onDataChange(productoSnapshot: DataSnapshot) {
                                                                if (productoSnapshot.exists()) {
                                                                    val nombreProducto =
                                                                        productoSnapshot.child("nombreProducto")
                                                                            .getValue(String::class.java)
                                                                    val precioProductoStr =
                                                                        productoSnapshot.child("precioProducto")
                                                                            .getValue(String::class.java)
                                                                    val precioProducto =
                                                                        precioProductoStr?.toFloatOrNull() ?: 0f
                                                                    // Aquí puedes usar nombreProducto como el nombre del producto
                                                                    // Agrega el nombre del producto a la barra correspondiente
                                                                    val barra2 = Bar()
                                                                    val color2 = generarColorHexAleatorio()
                                                                    barra2.color = Color.parseColor(color2)
                                                                    barra2.name = nombreProducto
                                                                    barra2.value = cantidadProducto
                                                                    puntosProductosVendidos.add(barra2)

                                                                    // Verifica si hemos procesado todos los detalles de ventas
                                                                    if (productosVendidos.size == cantidadesVendidas.size &&
                                                                        productosVendidos.size == puntosProductosVendidos.size
                                                                    ) {
                                                                        // Actualiza la gráfica cuando se han procesado todos los datos
                                                                        val graficaProductos =
                                                                            findViewById<View>(R.id.graphBarCantidadesProductos) as BarGraph
                                                                        graficaProductos.bars = puntosProductosVendidos
                                                                    }
                                                                }
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                // Manejar errores si es necesario
                                                            }
                                                        })
                                                }
                                            }
                                        }
                                    }
                                    // Verifica si hemos procesado todos los detalles de ventas para calcular las barras
                                    /*if (productosVendidos.size == snapshot.childrenCount.toInt()) {
                                   for (i in productosVendidos.indices) {
                                       val barra2 = Bar()
                                       val color2 = generarColorHexAleatorio()
                                       barra2.color = Color.parseColor(color2)
                                       barra2.name = productosVendidos[i]
                                       barra2.value = cantidadesVendidas[i]
                                       puntosProductosVendidos.add(barra2)
                                   }

                                   val graficaProductos =
                                       findViewById<View>(R.id.graphBarCantidadesProductos) as BarGraph
                                   graficaProductos.bars = puntosProductosVendidos
                               }*/
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        this@GraficoVentaActivity,
                                        "Error al cargar los datos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores si es necesario
            }
        })
    }


    // funcion para mostrar la grafica de ventas por año
    fun graficarBarrasVentasAno(puntos: ArrayList<Bar>, selectedYear: Int) {
        // ...
        val meses = arrayOf(
            "Enero",
            "Febrero",
            "Marzo",
            "Abril",
            "Mayo",
            "Junio",
            "Julio",
            "Agosto",
            "Septiembre",
            "Octubre",
            "Noviembre",
            "Diciembre"
        )
        val cantidades = FloatArray(12) // Inicializa un array de 12 elementos para almacenar los totales por mes
        val yearActual = 2023
        dbTicketVentasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ticket in snapshot.children) {
                        val fechaVentaTicket = ticket.child("fechaVenta").getValue().toString()
                        val yearVentaTicket = fechaVentaTicket.split("/")[2].toInt() // Obtiene el año de la fecha

                        if (yearVentaTicket == selectedYear ) { // Verifica si el año coincide
                            val mesVenta = fechaVentaTicket.split("/")[1].toInt()
                            val totalVentaTicket = ticket.child("totalVenta").getValue().toString().toFloat()

                            // Obtén el nombre del mes a partir del número de mes
                            val nombreMes = meses[mesVenta - 1] // Resta 1 porque los índices del array comienzan en 0

                            // Agrega el total de venta al mes correspondiente
                            val indiceMes = meses.indexOf(nombreMes)
                            if (indiceMes >= 0) {
                                cantidades[indiceMes] += totalVentaTicket
                            }
                        }
                    }

                    // Ahora, tienes los totales de ventas por mes en el array 'cantidades'
                    // Puedes usar 'cantidades' para graficar los datos como lo haces en tu código original
                    val puntos = ArrayList<Bar>()
                    for (i in meses.indices) {
                        val barra = Bar()
                        val color = generarColorHexAleatorio()
                        barra.color = Color.parseColor(color)
                        barra.name = meses[i]
                        barra.value = cantidades[i]
                        puntos.add(barra)
                    }

                    val grafica = findViewById<View>(R.id.graphVentas) as BarGraph
                    grafica.bars = puntos
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores si es necesario
            }
        })
    }

    fun generarColorHexAleatorio(): String {
        val letras = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        var color = "#"
        for (i in 0..5) {
            color += letras[(Math.random() * 15).roundToInt()]
        }

        return color
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