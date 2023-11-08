package com.example.sistema_bodega

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sistema_bodega.Clases.CategoriaClass
import com.example.sistema_bodega.Clases.ProveedorClass
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import java.io.ByteArrayOutputStream

class detalleProductoActivity : AppCompatActivity() {
    private lateinit var imagenProducto: ImageView
    private lateinit var tv_nombreProducto: TextView
    private lateinit var tv_codigoProducto: TextView
    private lateinit var tv_stockProducto: TextView
    private lateinit var tv_precioProducto: TextView
    private lateinit var tv_descripcionProducto: TextView
    private lateinit var tv_proveedor: TextView
    private lateinit var tv_categoria: TextView
    private lateinit var idProducto: String
    private lateinit var codigoProducto: String
    private lateinit var nombreProducto: String
    private lateinit var stockProducto: String
    private lateinit var precioProducto: String
    private lateinit var descripcionProducto: String
    private lateinit var proveedor: String
    private lateinit var categoria: String
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var bundle: Bundle
    private lateinit var imProducto: String
    private lateinit var editarButton: AppCompatButton
    private lateinit var eliminarButton: AppCompatButton
    private lateinit var databaseProveedorRef: DatabaseReference
    private lateinit var databaseCategoriaRef: DatabaseReference
    private lateinit var spinnerIdsProveedor: MutableList<String> // Declara spinnerIds como propiedad
    private lateinit var spinnerIdsCategoria: MutableList<String> // Declara spinnerIds como propiedad
    private lateinit var codigoProducto_editText: TextInputEditText
    private lateinit var nombreProducto_editText: TextInputEditText
    private lateinit var stockProducto_editText: TextInputEditText
    private lateinit var precioProducto_editText: TextInputEditText
    private lateinit var descripcionProducto_editText: TextInputEditText
    private lateinit var btn_actualizarProducto: AppCompatButton
    private lateinit var btn_cancelarProducto: ImageView
    private lateinit var reference: DatabaseReference
    private lateinit var alertDialog: AlertDialog
    lateinit var nombreProveedor_spinner: Spinner
    lateinit var nombreCategoria_spinner: Spinner
    private lateinit var dialogLayout: View
    private lateinit var nombreProveedor: String
    private lateinit var nombreCategoria: String

    // Variable global para almacenar el valor escaneado del código QR
    private var qrScanResult: String? = null
    private var isImageSelected = false
    private lateinit var progressBarLayout: ProgressBar

    private lateinit var btnActualizarImage: AppCompatButton
    private lateinit var btnCancelarImage: AppCompatButton
    private lateinit var imagenQr: ImageView
    private lateinit var imageUri: Uri
    private lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detalle Producto"
        reference = FirebaseDatabase.getInstance().getReference("Productos")
        databaseProveedorRef = FirebaseDatabase.getInstance().getReference("Proveedor")
        databaseCategoriaRef = FirebaseDatabase.getInstance().getReference("Categoria")

        imagenProducto = findViewById(R.id.imProducto)
        tv_codigoProducto = findViewById(R.id.tv_codigoProducto)
        tv_nombreProducto = findViewById(R.id.tv_nombreProducto)
        tv_stockProducto = findViewById(R.id.tv_stockProducto)
        tv_precioProducto = findViewById(R.id.tv_precioProducto)
        tv_descripcionProducto = findViewById(R.id.tv_descripcionProducto)
        tv_proveedor = findViewById(R.id.tv_proveedor)
        tv_categoria = findViewById(R.id.tv_categoria)
        editarButton = findViewById(R.id.btn_editarProducto)
        eliminarButton = findViewById(R.id.btn_eliminarProducto)
        bundle = intent.extras!!
        idProducto = bundle.getString("idProducto").toString()
        val checkProductoDatabase = reference.orderByChild("idProducto").equalTo(idProducto)
        checkProductoDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (producto in snapshot.children) {

                        codigoProducto = producto.child("codigoProducto").value.toString()
                        nombreProducto = producto.child("nombreProducto").value.toString()
                        stockProducto = producto.child("stockProducto").value.toString()
                        precioProducto = producto.child("precioProducto").value.toString()
                        descripcionProducto = producto.child("descripcionProducto").value.toString()
                        proveedor = producto.child("idProveedor").value.toString()
                        categoria = producto.child("idCategoria").value.toString()
                        imProducto = producto.child("imageUrl").value.toString()
                        tv_codigoProducto.text = codigoProducto
                        tv_stockProducto.text = stockProducto
                        tv_precioProducto.text = precioProducto
                        tv_descripcionProducto.text = descripcionProducto

                        tv_nombreProducto.text = nombreProducto
                        //Si la imagen está vacia se muestra una por defecto
                        if (imProducto == "") {
                            Glide.with(this@detalleProductoActivity).load(R.drawable.producto).into(imagenProducto)
                        } else {
                            Glide.with(this@detalleProductoActivity).load(imProducto).into(imagenProducto)
                        }
                       // Glide.with(this@detalleProductoActivity).load(imProducto).into(imagenProducto)

                        // Realizar la consulta para obtener la información del proveedor
                        val checkProveedorDatabase = FirebaseDatabase.getInstance().getReference("Proveedor")
                            .orderByChild("idProveedor").equalTo(proveedor)
                        checkProveedorDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (proveedor in snapshot.children) {
                                        tv_proveedor.text = proveedor.child("nombreProveedor").value.toString()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    this@detalleProductoActivity,
                                    "Error al cargar los datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                        // Realizar la consulta para obtener la información de la categoria
                        val checkCategoriaDatabase = FirebaseDatabase.getInstance().getReference("Categoria")
                            .orderByChild("idCategoria").equalTo(categoria)
                        checkCategoriaDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (categoria in snapshot.children) {
                                        tv_categoria.text = categoria.child("nombreCategoria").value.toString()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    this@detalleProductoActivity,
                                    "Error al cargar los datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@detalleProductoActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
        imagenProducto.setOnClickListener { alertDialogImage() }
        editarButton.setOnClickListener {
            openUpdateDialog()
        }
        eliminarButton.setOnClickListener {
            AlertDialog.Builder(this).also {
                it.setTitle("Eliminar Producto" + nombreProducto)
                it.setMessage("¿Está seguro que desea eliminar el producto?")
                it.setPositiveButton("Si") { dialog, which ->
                    val dbref = FirebaseDatabase.getInstance().getReference("Productos")
                        .child(intent.getStringExtra("idProducto").toString())
                    val storage = FirebaseStorage.getInstance()
                    val storageReference = storage.getReferenceFromUrl(imProducto)
                    storageReference.delete().addOnSuccessListener {
                        dbref.removeValue()
                        Toast.makeText(this, "Eliminado:" + nombreProducto, Toast.LENGTH_SHORT).show()
                        finish()

                    }.addOnFailureListener { error ->
                        Toast.makeText(
                            this,
                            "Deleting Err ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                        // Uh-oh, an error occurred!
                    }
                }
                it.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
            }.create().show()
        }
    }

    private fun openUpdateDialog(
    ) {
        //Mostrar datos en el alertDialog
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        //builder.setTitle("Editar Producto")
        dialogLayout = inflater.inflate(R.layout.item_editarproducto, null)
        builder.setView(dialogLayout)
        codigoProducto_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_codigoProducto)
        nombreProducto_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_nombreProducto)
        stockProducto_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_stockProducto)
        precioProducto_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_precioProducto)
        descripcionProducto_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_descripcionProducto)
        btn_actualizarProducto = dialogLayout.findViewById<AppCompatButton>(R.id.editarProducto_btn)
        btn_cancelarProducto = dialogLayout.findViewById<ImageView>(R.id.cancelarProducto_imView)
        nombreProveedor_spinner = dialogLayout.findViewById<Spinner>(R.id.nombreProveedor_spinner)
        nombreCategoria_spinner = dialogLayout.findViewById<Spinner>(R.id.nombreCategoria_spinner)
        progressBarLayout = dialogLayout.findViewById<ProgressBar>(R.id.progressBarLayout)
        imagenQr = dialogLayout.findViewById<ImageView>(R.id.imagenQr)

        val checkProductoDatabase = reference.orderByChild("idProducto").equalTo(idProducto)
        checkProductoDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (producto in snapshot.children) {

                        codigoProducto = producto.child("codigoProducto").value.toString()
                        nombreProducto = producto.child("nombreProducto").value.toString()
                        stockProducto = producto.child("stockProducto").value.toString()
                        precioProducto = producto.child("precioProducto").value.toString()
                        descripcionProducto = producto.child("descripcionProducto").value.toString()
                        proveedor = producto.child("idProveedor").value.toString()
                        categoria = producto.child("idCategoria").value.toString()

                        codigoProducto_editText.setText(codigoProducto)
                        nombreProducto_editText.setText(nombreProducto)
                        stockProducto_editText.setText(stockProducto)
                        precioProducto_editText.setText(precioProducto)
                        descripcionProducto_editText.setText(descripcionProducto)


                        // Realizar la consulta para obtener la información del proveedor
                        val checkProveedorDatabase = FirebaseDatabase.getInstance().getReference("Proveedor")
                            .orderByChild("idProveedor").equalTo(proveedor)
                        checkProveedorDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (proveedor in snapshot.children) {
                                        nombreProveedor = proveedor.child("nombreProveedor").value.toString()


                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    this@detalleProductoActivity,
                                    "Error al cargar los datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                        val checkCategoriaDatabase = FirebaseDatabase.getInstance().getReference("Categoria")
                            .orderByChild("idCategoria").equalTo(categoria)
                        checkCategoriaDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (categoria in snapshot.children) {
                                        nombreCategoria = categoria.child("nombreCategoria").value.toString()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    this@detalleProductoActivity,
                                    "Error al cargar los datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@detalleProductoActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
        alertDialog = builder.create()
        alertDialog.show()
        imagenQr.setOnClickListener { initScanner() }
        btn_cancelarProducto.setOnClickListener {
            alertDialog.dismiss()
        }
        btn_actualizarProducto.setOnClickListener {
            //condicion validando campos
            if (validarCategoriaEditado() && validarProveedorEditado() && !validarCodigoProductoEditado() && !validarNombreProductoEditado() && !validarStockProductoEditado() && !validarPrecioProductoEditado() && !validarDescripcionProductoEditado()) {
               updateProductData()
            }

        }
        cargarSpinnerProveedor()
        cargarSpinnerCategoria()
    }

    private fun updateProductData() {
        val idProducto = intent.getStringExtra("idProducto").toString()
        val selectedProveedorId = spinnerIdsProveedor[nombreProveedor_spinner.selectedItemPosition - 1]
        val selectedCategoriaId = spinnerIdsCategoria[nombreCategoria_spinner.selectedItemPosition - 1]

        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarLayout.visibility = View.VISIBLE
        btn_actualizarProducto.isEnabled = false

        val updateCompleteListener = OnCompleteListener<Void> { task ->
            // Habilitar el botón de guardar y ocultar el ProgressBar
            btn_actualizarProducto.isEnabled = true
            progressBarLayout.visibility = View.GONE

            // Actualizar los TextView con los nuevos datos del producto
            tv_proveedor.text = nombreProveedor_spinner.selectedItem.toString()
            tv_categoria.text = nombreCategoria_spinner.selectedItem.toString()
            tv_codigoProducto.text = codigoProducto_editText.text.toString()
            tv_nombreProducto.text = nombreProducto_editText.text.toString()
            tv_stockProducto.text = stockProducto_editText.text.toString()
            tv_precioProducto.text = precioProducto_editText.text.toString()
            tv_descripcionProducto.text = descripcionProducto_editText.text.toString()
            if (task.isSuccessful) {
                // Actualizar los TextView con los nuevos datos del producto


                Toast.makeText(this@detalleProductoActivity, "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                showToast("Error al actualizar el producto")
            }

            alertDialog.dismiss()
        }

        reference.child(idProducto).apply {
            child("codigoProducto").setValue(codigoProducto_editText.text.toString())
                .addOnCompleteListener(updateCompleteListener)
            child("nombreProducto").setValue(nombreProducto_editText.text.toString())
                .addOnCompleteListener(updateCompleteListener)
            child("stockProducto").setValue(stockProducto_editText.text.toString())
                .addOnCompleteListener(updateCompleteListener)
            child("precioProducto").setValue(precioProducto_editText.text.toString())
                .addOnCompleteListener(updateCompleteListener)
            child("descripcionProducto").setValue(descripcionProducto_editText.text.toString())
                .addOnCompleteListener(updateCompleteListener)
            child("idProveedor").setValue(selectedProveedorId)
                .addOnCompleteListener(updateCompleteListener)
            child("idCategoria").setValue(selectedCategoriaId)
                .addOnCompleteListener(updateCompleteListener)
        }
    }

    private fun validarCategoriaEditado(): Boolean {
        return if (nombreCategoria_spinner.selectedItemPosition <= 0) {//Si no se selecciona una categoria
            showToast("Seleccione una categoria")
            false
        } else {
            true
        }
    }

    private fun validarProveedorEditado(): Boolean {
        return if (nombreProveedor_spinner.selectedItemPosition <= 0) {//Si no se selecciona un proveedor
            showToast("Seleccione un proveedor")
            false
        } else {
            true
        }
    }

    private fun validarCodigoProductoEditado(): Boolean {
        val codigoProductoInput = codigoProducto_editText.text.toString().trim()
        return if (codigoProductoInput.isEmpty() && qrScanResult?.isEmpty() != false) {
            codigoProducto_editText.error = "El código del producto es requerido"
            true
        } else {
            codigoProducto_editText.error = null
            false
        }

    }

    private fun validarNombreProductoEditado(): Boolean {
        val nombreProductoInput = nombreProducto_editText.text.toString().trim()
        return if (nombreProductoInput.isEmpty()) {
            nombreProducto_editText.error = "El nombre del producto es requerido"
            true
        } else {
            nombreProducto_editText.error = null
            false
        }
    }

    private fun validarStockProductoEditado(): Boolean {
        val stockProductoInput = stockProducto_editText.text.toString().trim()
        return if (stockProductoInput.isEmpty()) {
            stockProducto_editText.error = "El stock del producto es requerido"
            true
        } else {
            stockProducto_editText.error = null
            false
        }
    }

    private fun validarPrecioProductoEditado(): Boolean {
        val precioProductoInput = precioProducto_editText.text.toString().trim()
        return if (precioProductoInput.isEmpty()) {
            precioProducto_editText.error = "El precio del producto es requerido"
            true
        } else {
            precioProducto_editText.error = null
            false
        }
    }

    private fun validarDescripcionProductoEditado(): Boolean {
        val descripcionProductoInput = descripcionProducto_editText.text.toString().trim()
        return if (descripcionProductoInput.isEmpty()) {
            descripcionProducto_editText.error = "La descripción del producto es requerida"
            true
        } else {
            descripcionProducto_editText.error = null
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun cargarSpinnerProveedor() {
        // Escuchar los datos de Firebase y configurar el adaptador del Spinner
        databaseProveedorRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val spinnerData: MutableList<String> = mutableListOf("Seleccionar opción")
                spinnerIdsProveedor = mutableListOf() // Inicializa spinnerIds

                // Iterar a través de los datos obtenidos del dataSnapshot
                for (proveedorSnapshot in dataSnapshot.children) {
                    val value = dataSnapshot.getValue(ProveedorClass::class.java)
                    if (value?.idUuser == userId) {
                        nombreProveedor = proveedorSnapshot.child("nombreProveedor").value.toString()
                        val idProveedor = proveedorSnapshot.child("idProveedor").value.toString()
                        if (idProveedor != null && nombreProveedor != null) {
                            spinnerIdsProveedor.add(idProveedor)
                            spinnerData.add(nombreProveedor)
                        }
                    }
                }
                // Configurar el adaptador del Spinner
                val adapter = ArrayAdapter<String>(
                    this@detalleProductoActivity,
                    android.R.layout.simple_spinner_item,
                    spinnerData
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Asignar el adaptador al Spinner
                nombreProveedor_spinner.adapter = adapter

                // Obtener el índice del proveedor actualmente asignado al producto
                val proveedorIndex = spinnerIdsProveedor.indexOf(proveedor)

                // Establecer la selección del Spinner en el proveedor actual
                nombreProveedor_spinner.setSelection(proveedorIndex + 1)
                // Agregar el listener para capturar la selección del Spinner
                nombreProveedor_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedData = spinnerData[position] // Obtener el elemento seleccionado del Spinner
                        if (selectedData == "Seleccionar opción") {
                            // Se seleccionó la opción "Seleccionar opción"
                            // Realiza las acciones necesarias (por ejemplo, limpiar el RecyclerView)
                        } else {
                            // Se seleccionó un elemento válido
                            //val selectedItemId =
                            //  spinnerIds[position] // Restar 1 para obtener el ID correspondiente al elemento seleccionado
                            val selectedItemId =
                                spinnerIdsProveedor[nombreProveedor_spinner.selectedItemPosition - 1] // Restar 1 para obtener el ID correspondiente al elemento seleccionado
                            /*Toast.makeText(
                                this@detalleProductoActivity,
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

    private fun cargarSpinnerCategoria() {
        // Escuchar los datos de Firebase y configurar el adaptador del Spinner
        databaseCategoriaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val spinnerDataCategoria: MutableList<String> = mutableListOf("Seleccionar opción")
                spinnerIdsCategoria = mutableListOf() // Inicializa spinnerIds

                // Iterar a través de los datos obtenidos del dataSnapshot
                for (categoriaSnapshot in dataSnapshot.children) {
                    val value = dataSnapshot.getValue(CategoriaClass::class.java)
                    if (value?.idUuser == userId) {
                        nombreCategoria = categoriaSnapshot.child("nombreCategoria").value.toString()
                        val idCategoria = categoriaSnapshot.child("idCategoria").value.toString()
                        if (idCategoria != null && nombreCategoria != null) {
                            spinnerIdsCategoria.add(idCategoria)
                            spinnerDataCategoria.add(nombreCategoria)
                        }
                    }
                }
                // Configurar el adaptador del Spinner
                val adapterCategoria = ArrayAdapter<String>(
                    this@detalleProductoActivity,
                    android.R.layout.simple_spinner_item,
                    spinnerDataCategoria
                )
                adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Asignar el adaptador al Spinner
                nombreCategoria_spinner.adapter = adapterCategoria

                // Obtener el índice del proveedor actualmente asignado al producto
                val categoriaIndex = spinnerIdsCategoria.indexOf(categoria)

                // Establecer la selección del Spinner en el proveedor actual
                nombreCategoria_spinner.setSelection(categoriaIndex + 1)
                // Agregar el listener para capturar la selección del Spinner
                nombreCategoria_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedData =
                            spinnerDataCategoria[position] // Obtener el elemento seleccionado del Spinner
                        if (selectedData == "Seleccionar opción") {
                            // Se seleccionó la opción "Seleccionar opción"
                            // Realiza las acciones necesarias (por ejemplo, limpiar el RecyclerView)
                        } else {
                            // Se seleccionó un elemento válido
                            //val selectedItemId =
                            //  spinnerIds[position] // Restar 1 para obtener el ID correspondiente al elemento seleccionado
                            val selectedItemId =
                                spinnerIdsCategoria[nombreCategoria_spinner.selectedItemPosition - 1] // Restar 1 para obtener el ID correspondiente al elemento seleccionado
                            /*Toast.makeText(
                                this@detalleProductoActivity,
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

    private fun alertDialogImage() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.alert_opcion_imagen, null)
        builder.setView(mDialogView)
        val btnCamaraimagen = mDialogView.findViewById<LinearLayout>(R.id.camaraLayout)
        val btnGaleriaimagen = mDialogView.findViewById<LinearLayout>(R.id.galeriaLayout)

        dialog = builder.create()
        dialog.show()
        btnGaleriaimagen.setOnClickListener {
            openImagePicker()
            dialog.dismiss()
        }
        btnCamaraimagen.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }
    }

    private fun initScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea el código QR")
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
        isImageSelected = true
    }

    private fun openCamera() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val requestCode = 1 // Puedes elegir cualquier número de código de solicitud
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // El permiso no está otorgado, solicitarlo al usuario
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            // El permiso está otorgado, realizar la operación de escritura en el almacenamiento externo
            // Aquí puedes llamar a la función que intenta insertar la imagen nuevamente
            // El permiso de la cámara ya está otorgado, abrir la cámara
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 0)
            isImageSelected = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)


        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                codigoProducto_editText.setText(result.contents)
                //Toast.makeText(this, "El valor escaneado es: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                imageUri = data.data!!
                alertDialogActulizarImage()

            } else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                val tempUri = getImageUri(applicationContext, imageBitmap)
                imageUri = tempUri
                alertDialogActulizarImage()
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }

    }

    private fun alertDialogActulizarImage() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.alert_image_get, null)
        builder.setView(mDialogView)
        val imageObtenida = mDialogView.findViewById<ImageView>(R.id.imagenObtenida)
        btnActualizarImage = mDialogView.findViewById<AppCompatButton>(R.id.btn_actualizarImagen)
        btnCancelarImage = mDialogView.findViewById<AppCompatButton>(R.id.btn_cancelarImagen)
        progressBarLayout = mDialogView.findViewById<ProgressBar>(R.id.progressBarLayout)
        imageObtenida.setImageURI(imageUri)
        dialog = builder.create()
        dialog.show()
        btnActualizarImage.setOnClickListener {
            uploadImageToDatabase(imageUri)
        }
        btnCancelarImage.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun uploadImageToDatabase(imageUri: Uri) {
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarLayout.visibility = View.VISIBLE
        btnActualizarImage.isEnabled = false
        val storageReference = FirebaseStorage.getInstance().reference// Referencia a la carpeta de imágenes
        val imageFileName = "product_${idProducto}.jpg" // Nombre del archivo de imagen en la base de datos
        val imageRef = storageReference.child("imagesProducto/$imageFileName")

        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Obtener la URL de descarga de la imagen
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Guardar la URL de la imagen en la base de datos
                    reference.child(idProducto).child("imageUrl").setValue(imageUrl)
                        .addOnCompleteListener {
                            imagenProducto.setImageURI(imageUri)
                            // Habilitar el botón de guardar y ocultar el ProgressBar
                            btnActualizarImage.isEnabled = true
                            progressBarLayout.visibility = View.GONE
                            Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .addOnFailureListener { error ->
                            // Habilitar el botón de guardar y ocultar el ProgressBar
                            btnActualizarImage.isEnabled = true
                            progressBarLayout.visibility = View.GONE
                            Toast.makeText(this, "Error al guardar la imagen: ${error.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            } else {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                btnActualizarImage.isEnabled = true
                progressBarLayout.visibility = View.GONE
                Toast.makeText(this, "Error al subir la imagen: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // El permiso fue otorgado, realizar la operación de escritura en el almacenamiento externo
            // Aquí puedes llamar a la función que intenta insertar la imagen nuevamente
            // El permiso de la cámara ya está otorgado, abrir la cámara
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 0)
            isImageSelected = true
        } else {
            // El permiso fue denegado, muestra un mensaje o toma alguna acción apropiada
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.archivar -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Archivar producto")
                    setMessage("¿Está seguro de archivar el producto?")
                    setPositiveButton("Si") { _, _ ->
                        // Deshabilitar el botón de guardar y mostrar el ProgressBar
                        progressBarLayout.visibility = View.VISIBLE
                        btn_actualizarProducto.isEnabled = false
                        reference.child(intent.getStringExtra("idProducto").toString()).child("estadoProducto")
                            .setValue("Archivado")
                            .addOnCompleteListener {
                                // Habilitar el botón de guardar y ocultar el ProgressBar
                                btn_actualizarProducto.isEnabled = true
                                progressBarLayout.visibility = View.GONE
                                Toast.makeText(this@detalleProductoActivity, "Producto archivado", Toast.LENGTH_SHORT)
                                    .show()
                                finish()
                            }.addOnFailureListener {
                                // Habilitar el botón de guardar y ocultar el ProgressBar
                                btn_actualizarProducto.isEnabled = true
                                progressBarLayout.visibility = View.GONE
                                Toast.makeText(
                                    this@detalleProductoActivity,
                                    "Error al archivar el producto",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    setNegativeButton("No") { _, _ ->
                        // Respondió "No" a la pregunta
                    }
                }.create().show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detalleproducto_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }
}