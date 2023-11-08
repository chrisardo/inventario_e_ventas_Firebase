package com.example.sistema_bodega

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sistema_bodega.Adaptadores.detalleVentaProductosAdapter
import com.example.sistema_bodega.Clases.ProductoClass
import com.example.sistema_bodega.Clases.TicketVentaClass
import com.example.sistema_bodega.Clases.detalleTicketClass
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.qrcode.encoder.QRCode
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import java.text.SimpleDateFormat
import java.util.*
import com.itextpdf.text.Image // Importa la clase Image de iText
import com.itextpdf.text.pdf.PdfPTable // Importa la clase PdfPTable de iText
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DescargarTicketVentaPdf : AppCompatActivity() {
    private val REQUEST_PERMISSION = 1
    private val CREATE_FILE_REQUEST_CODE = 2
    private lateinit var bundle: Bundle
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null
    private lateinit var dbProductosRef: DatabaseReference
    var idTicketVenta: String? = null
    private lateinit var dbTicketVentasRef: DatabaseReference
    private lateinit var dbClientesRef: DatabaseReference
    private lateinit var dbDetalleListaVentaRef: DatabaseReference
    private lateinit var databaseEmpresaRef: DatabaseReference
    private lateinit var databaseUsarioRef: DatabaseReference
    private lateinit var empList: ArrayList<detalleTicketClass>
    lateinit var mAdapter: detalleVentaProductosAdapter
    private lateinit var serieVenta: String
    private lateinit var fechaVenta: String
    private lateinit var horaVenta: String
    private lateinit var formaPago: String
    private lateinit var totalVenta: String
    private lateinit var pagoCliente: String
    private lateinit var vuelto: String
    private lateinit var nombreEmpresa: String
    private lateinit var direccionEmpresa: String
    private lateinit var celularEmpresa: String
    private lateinit var razonSocialEmpresa: String
    private lateinit var rucEmpresa: String
    private lateinit var logoEmpresa: String
    private lateinit var descripcionEmpresa: String
    private lateinit var idCliente: String

    // URL de la imagen obtenida desde la base de datos de Firebase
    private var imageUrl: String? = null
    private lateinit var nombreCliente: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descargar_ticket_venta_pdf)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION
            )
        } else {
            createAndSavePdf()
        }
        pref = preferences(this)
        userId = pref.prefIdUser
        dbTicketVentasRef = FirebaseDatabase.getInstance().getReference("TicketVentas")
        dbClientesRef = FirebaseDatabase.getInstance().getReference("Clientes")
        databaseEmpresaRef = FirebaseDatabase.getInstance().getReference("empresa")
        databaseUsarioRef = FirebaseDatabase.getInstance().getReference("usuarios")
        bundle = intent.extras!!
        idTicketVenta = bundle.getString("idTicketVenta")
        obtenerDetalleListaProductos()
        empList = arrayListOf<detalleTicketClass>()
        val checkTicketVentaDatabase = dbTicketVentasRef.orderByChild("idTicketVenta").equalTo(idTicketVenta)
        checkTicketVentaDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ticketVenta in snapshot.children) {
                        serieVenta = ticketVenta.child("serieVenta").getValue(String::class.java).toString()
                        fechaVenta = ticketVenta.child("fechaVenta").getValue(String::class.java).toString()
                        horaVenta = ticketVenta.child("horaVenta").getValue(String::class.java).toString()
                        formaPago = ticketVenta.child("formaPago").getValue(String::class.java).toString()
                        totalVenta = ticketVenta.child("totalVenta").getValue(String::class.java).toString()
                        pagoCliente = ticketVenta.child("pagoCliente").getValue(String::class.java).toString()
                        vuelto = ticketVenta.child("vuelto").getValue(String::class.java).toString()
                        idCliente = ticketVenta.child("idCliente").getValue(String::class.java).toString()
                        var ticketVenta = ticketVenta.getValue(TicketVentaClass::class.java)

                        if (ticketVenta?.idUser == userId) {
                            val checkProveedorDatabase =
                                dbClientesRef.orderByChild("idCliente").equalTo(ticketVenta?.idCliente)
                            checkProveedorDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (cliente in snapshot.children) {
                                            nombreCliente =
                                                cliente.child("nombreCliente").getValue(String::class.java).toString()

                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        this@DescargarTicketVentaPdf,
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
                Toast.makeText(this@DescargarTicketVentaPdf, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })

        //Para obtener los datos de la empresa
        val checkEmpresaDatabase = databaseEmpresaRef.orderByChild("idUser").equalTo(userId)
        checkEmpresaDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (empresa in snapshot.children) {
                        nombreEmpresa = empresa.child("nombreEmpresa").getValue(String::class.java).toString()
                        razonSocialEmpresa = empresa.child("razonSocial").getValue(String::class.java).toString()
                        rucEmpresa = empresa.child("ruc").getValue(String::class.java).toString()
                        direccionEmpresa = empresa.child("direccion").getValue(String::class.java).toString()
                        descripcionEmpresa = empresa.child("informacionEmpresa").getValue(String::class.java).toString()
                        logoEmpresa = empresa.child("imagenEmpresa").getValue(String::class.java).toString()
                        celularEmpresa = empresa.child("celular").getValue(String::class.java).toString()

                        //Si la imagen está vacia se muestra una por defecto


                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DescargarTicketVentaPdf, "Error al obtener datos de la empresa", Toast.LENGTH_SHORT)
                    .show()
            }
        })


    }

    private fun obtenerDetalleListaProductos() {
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
                                // Realizar la consulta para obtener la información del proveedor
                                dbProductosRef.child(productoId!!)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(clienteSnapshot: DataSnapshot) {
                                            val productoClass = clienteSnapshot.getValue(ProductoClass::class.java)

                                            productoClass?.let { prov ->
                                                //nombre y el precio del producto en el detalle de la venta
                                                it.idProducto = prov.nombreProducto
                                                it.detalleprecioProducto = prov.precioProducto

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

                    mAdapter = detalleVentaProductosAdapter(empList)

                } else {
                    Toast.makeText(this@DescargarTicketVentaPdf, "No hay productos en la lista", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DescargarTicketVentaPdf, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createAndSavePdf() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        intent.putExtra(Intent.EXTRA_TITLE, "$fileName.pdf")
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val outputStream = contentResolver.openOutputStream(uri)
                outputStream?.use { stream ->
                    val pageSize = Rectangle(50f, 100f) // Define el tamaño del ticket
                    val document = Document(PageSize.A6)
                    PdfWriter.getInstance(document, stream)
                    document.open()
                    val smallFont = Font(Font.FontFamily.TIMES_ROMAN, 5f)
                    val smallFont2 = Font(Font.FontFamily.TIMES_ROMAN, 8f, Font.BOLD)
                    //Agregar contenido de la empresa
                    document.add(Paragraph("Empresa: " + nombreEmpresa, smallFont))
                    //Si logoEmpresa está vacia del la tabla de empresa se muestre una por defecto y si no pues se muestra la imagen de la empresa en el pdf
                    // Verificar si logoEmpresa está vacío
                    if (logoEmpresa == "") {
                    } else {
                        // Si logoEmpresa no está vacío, muestra la imagen de la empresa
                        try {
                            val empresaImage = Image.getInstance(logoEmpresa)
                            empresaImage.scaleAbsolute(30f, 30f) // Ajusta el tamaño de la imagen
                            document.add(empresaImage)
                        } catch (e: Exception) {
                            // Maneja cualquier excepción que pueda ocurrir al cargar la imagen
                            e.printStackTrace()
                            //Mostrar emensaje
                            Toast.makeText(
                                this@DescargarTicketVentaPdf,
                                "Error al cargar la imagen de la empresa",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    document.add(Paragraph("Razón Social: " + razonSocialEmpresa, smallFont))
                    document.add(Paragraph("RUC: " + rucEmpresa, smallFont))
                    document.add(Paragraph("Dirección: " + direccionEmpresa, smallFont))
                    document.add(Paragraph("Celular: " + celularEmpresa, smallFont))
                    document.add(Paragraph("Descripción: " + descripcionEmpresa, smallFont))
                    document.add(
                        Paragraph(
                            "-----------------------------------------------------------------",
                            smallFont
                        )
                    )

                    // Agregar contenido del ticket de venta
                    document.add(Paragraph("Ticket de Venta", smallFont2))
                    document.add(Paragraph("Serie de venta:" + serieVenta, smallFont))
                    document.add(Paragraph("Venta realizada el :" + fechaVenta + horaVenta, smallFont))

                    //si el id del cliente de la tabla historial dice "Cliente varios"
                    if (idCliente == "Cliente varios") {
                        document.add(Paragraph("Cliente: Varios", smallFont))
                    } else {
                        document.add(Paragraph("Cliente:" + nombreCliente, smallFont))
                    }
                    document.add(Paragraph("-----Lista de Productos vendidos-----",Font(Font.FontFamily.TIMES_ROMAN, 8f, Font.BOLD)))
                    document.add(Paragraph(""))
                    document.add(Paragraph("Nombre | Cantidad | P. Unit | Valor", smallFont))
                    document.add(Paragraph("-----------------------------------------------------------------",smallFont ))
                    for (detalleVenta in empList) {
                        document.add(
                            Paragraph(
                                detalleVenta.idProducto + " | " + detalleVenta.cantidadProducto + " | " + detalleVenta.detalleprecioProducto + " | S/." + detalleVenta.detalleprecioProducto,
                                smallFont
                            )
                        )
                        //operacion para sacar el subtotal de los productos
                        val cantidad = detalleVenta.cantidadProducto
                    }
                    document.add(Paragraph(""))
                    document.add(Paragraph("-----Fin de la lista de productos-----"))
                    //subtotal de la venta
                    document.add(Paragraph("Subtotal: S/." + totalVenta, smallFont))
                    //T0tal de la venta
                    document.add(Paragraph("Total: S/." + totalVenta, smallFont))
                    document.add(Paragraph(""))
                    //meotodo de pago del cliente    - valor del pago del cliente
                    document.add(Paragraph("Metodo de pago " + " - " + "Valor", smallFont))
                    document.add(Paragraph("-----------------------------", smallFont))
                    document.add(Paragraph("Forma de Pago:" + formaPago + " " + " - S/." + pagoCliente, smallFont))
                    document.add(Paragraph(" "))
                    document.add(Paragraph("Vuelto: S/." + vuelto, smallFont))
                    document.add(Paragraph("Gracias por su compra", smallFont2))
                    document.add(Paragraph("-----Fin del Ticket de Venta-----", smallFont2))
                    document.add(Paragraph(""))
                    document.close()

                    Toast.makeText(this, "PDF guardado en: $uri", Toast.LENGTH_LONG).show()

                    // Cerrar esta actividad después de guardar el PDF
                    finish()
                }
            }
        }
    }
    private fun readBytesFromStream(inputStream: InputStream): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } != -1) {
            outputStream.write(buffer, 0, length)
        }
        inputStream.close()
        return outputStream.toByteArray()
    }
    private fun downloadImageAndAddToPdf(document: Document) {
        databaseEmpresaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val imageUrl = dataSnapshot.getValue(String::class.java)
                    if (imageUrl != null && imageUrl.isNotEmpty()) {
                        val image = Image.getInstance(getBytesFromUrl(imageUrl))
                        image.scaleAbsolute(200f, 200f) // Ajusta el tamaño de la imagen
                        document.add(image)
                    } else {
                        // Si la URL de la imagen en la base de datos está vacía, puedes manejarlo aquí
                    }
                } else {
                    // Si no se encuentra la imagen en la base de datos, puedes manejarlo aquí
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Maneja el error si ocurre uno
            }
        })
    }
    private fun getBytesFromUrl(url: String): ByteArray {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input: InputStream = connection.inputStream
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        input.close()
        return output.toByteArray()
    }
}