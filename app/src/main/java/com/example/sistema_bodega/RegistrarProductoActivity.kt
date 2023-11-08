package com.example.sistema_bodega

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.integration.android.IntentIntegrator
import java.io.ByteArrayOutputStream

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.MediaStore
import android.view.Menu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sistema_bodega.Clases.CategoriaClass
import com.example.sistema_bodega.Clases.ProductoClass
import com.example.sistema_bodega.Clases.ProveedorClass

class RegistrarProductoActivity : AppCompatActivity() {
    lateinit var imageProducto: ImageView
    private lateinit var imagenQr: ImageView
    private lateinit var tvQr: TextInputEditText
    lateinit var nombreProducto: TextInputEditText
    lateinit var precioProducto: TextInputEditText
    lateinit var stockProducto: TextInputEditText
    lateinit var descripcionProducto: TextInputEditText
    lateinit var nombreProveedor_spinner: Spinner
    lateinit var nombreCategoria_spinner: Spinner
    lateinit var saveButton: AppCompatButton
    private lateinit var imageUri: Uri
    private lateinit var databaseRefProveedor: DatabaseReference
    private lateinit var databaseRefCategoria: DatabaseReference
    private lateinit var databaseProductoRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var pref: preferences // para el shared preferences
    var userId: String? = null
    private lateinit var spinnerIdsProveedor: MutableList<String> // Declara spinnerIds como propiedad

    private lateinit var spinnerIdsCategoria: MutableList<String> // Declara spinnerIds como propiedad
    private var isImageSelected = false

    private lateinit var progressBarLayout: ProgressBar
    private lateinit var selectedItemIdProveedor: String
    private lateinit var selectedItemIdCategoria: String
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var imaCategoria: ImageView
    private lateinit var et_NombreCategoria: TextInputEditText
    private lateinit var btnRegistroCategoria: AppCompatButton
    private lateinit var btnClose: ImageView

    // Variable global para almacenar el valor escaneado del código QR
    private var qrScanResult: String? = null
    private lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_producto)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Registrar Producto"
        // poner icono en el action bar
        supportActionBar?.setIcon(R.drawable.ic_store)
        // Verificar la conexión a Internet
        if (!isConnected()) {
            showConnectivityError()
            return
        }
        pref = preferences(this@RegistrarProductoActivity)
        userId = pref.prefIdUser
        databaseProductoRef = FirebaseDatabase.getInstance().getReference("Productos")
        storageRef = FirebaseStorage.getInstance().reference.child("imagesProducto")
        imagenQr = findViewById(R.id.imagenQr)
        imageProducto = findViewById(R.id.imageProducto)
        nombreProducto = findViewById(R.id.nombreProducto_et)
        precioProducto = findViewById(R.id.precioProducto_et)
        stockProducto = findViewById(R.id.stockProducto_et)
        descripcionProducto = findViewById(R.id.descripcionProducto_et)
        nombreProveedor_spinner = findViewById(R.id.nombreProveedor_spinner)
        nombreCategoria_spinner = findViewById(R.id.nombreCategoria_spinner)
        saveButton = findViewById(R.id.saveProducto_button)
        imagenQr.setOnClickListener { initScanner() }
        tvQr = findViewById(R.id.tvQr_et)
        imageProducto.setOnClickListener { alertDialogImage() }
        saveButton.setOnClickListener {
            if (validarSpinnerCategoria() && validarSpinnerProveedor() && !validarCodigoProducto() && !validarNombreProducto() && !validarStockProducto() && !validarPrecioProducto() && !validarDescripcionProducto()) {
                if (!isImageSelected) { // no se selecciono la imagen
                    uploadDatasinImage()
                } else {
                    uploadDataconImage()
                }
            }

        }

        progressBarLayout = findViewById(R.id.progressBarLayout)
        cargarSpinnerProveedor()
        cargarSpinnerCategoria()
    }

    private fun uploadDataconImage() {
        val codigoProducto = tvQr.text.toString()
        val NombreProducto = nombreProducto.text.toString()
        val PrecioProducto = precioProducto.text.toString()
        val StockProducto = stockProducto.text.toString()
        val DescripcionProducto = descripcionProducto.text.toString()
        selectedItemIdProveedor = spinnerIdsProveedor[nombreProveedor_spinner.selectedItemPosition - 1]
        selectedItemIdCategoria = spinnerIdsCategoria[nombreCategoria_spinner.selectedItemPosition - 1]
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarLayout.visibility = View.VISIBLE
        saveButton.isEnabled = false


        val storageReference = FirebaseStorage.getInstance().reference
        val imageFileName =
            "${System.currentTimeMillis()}_image.jpg" // Nombre del archivo de imagen en la base de datos

        val imageRef = storageReference.child("imagesProducto/$imageFileName")
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val downloadUrl = task.result
                val image = downloadUrl.toString()
                val id = databaseProductoRef.push().key!!
                val producto = ProductoClass(
                    id,
                    codigoProducto,
                    NombreProducto,
                    PrecioProducto,
                    StockProducto,
                    DescripcionProducto,
                    image,
                    selectedItemIdProveedor,
                    selectedItemIdCategoria,
                    pref.prefIdUser
                )
                databaseProductoRef.child(id.toString()).setValue(producto).addOnCompleteListener {
                    // Habilitar el botón de guardar y ocultar el ProgressBar
                    saveButton.isEnabled = true
                    progressBarLayout.visibility = View.GONE
                    Toast.makeText(this, "Producto registrado correctamente!", Toast.LENGTH_LONG).show()
                    finish()
                    //vaciarCampos()
                }.addOnFailureListener {
                    // Habilitar el botón de guardar y ocultar el ProgressBar
                    saveButton.isEnabled = true
                    progressBarLayout.visibility = View.GONE
                    Toast.makeText(this, "Error al registrar el producto", Toast.LENGTH_LONG).show()
                }

            } else {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                saveButton.isEnabled = true
                progressBarLayout.visibility = View.GONE
                showToast("Error al cargar la imagen: ${task.exception?.message}")
            }
        }

        //}
    }

    private fun uploadDatasinImage() {
        val codigoProducto = tvQr.text.toString()
        val NombreProducto = nombreProducto.text.toString()
        val PrecioProducto = precioProducto.text.toString()
        val StockProducto = stockProducto.text.toString()
        val DescripcionProducto = descripcionProducto.text.toString()
        selectedItemIdProveedor = spinnerIdsProveedor[nombreProveedor_spinner.selectedItemPosition - 1]
        selectedItemIdCategoria = spinnerIdsCategoria[nombreCategoria_spinner.selectedItemPosition - 1]
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarLayout.visibility = View.VISIBLE
        saveButton.isEnabled = false

        val id = databaseProductoRef.push().key!!
        val producto = ProductoClass(
            id,
            codigoProducto,
            NombreProducto,
            PrecioProducto,
            StockProducto,
            DescripcionProducto,
            "",
            selectedItemIdProveedor,
            selectedItemIdCategoria,
            pref.prefIdUser
        )
        databaseProductoRef.child(id.toString()).setValue(producto).addOnCompleteListener {
            // Habilitar el botón de guardar y ocultar el ProgressBar
            saveButton.isEnabled = true
            progressBarLayout.visibility = View.GONE
            Toast.makeText(this, "Producto registrado correctamente!", Toast.LENGTH_LONG).show()
            finish()
            //vaciarCampos()
        }.addOnFailureListener {
            // Habilitar el botón de guardar y ocultar el ProgressBar
            saveButton.isEnabled = true
            progressBarLayout.visibility = View.GONE
            Toast.makeText(this, "Error al registrar el producto", Toast.LENGTH_LONG).show()
        }
    }

    private fun vaciarCampos() {
        tvQr.setText("")
        nombreProducto.setText("")
        precioProducto.setText("")
        stockProducto.setText("")
        descripcionProducto.setText("")
        imageProducto.setImageResource(R.drawable.ic_image)
    }

    private fun isImageSelected(): Boolean {
        // Verifica si se ha seleccionado una imagen desde galería o cámara
        return if (!isImageSelected) {
            showToast("Seleccione una imagen del producto")
            false
        } else {
            true
        }
    }

    private fun validarSpinnerProveedor(): Boolean {
        return if (nombreProveedor_spinner.selectedItemPosition <= 0) {
            showToast("Seleccione un proveedor")
            false
        } else {

            true
        }
    }

    private fun validarSpinnerCategoria(): Boolean {
        return if (nombreCategoria_spinner.selectedItemPosition <= 0) {
            showToast("Seleccione una Categoria")
            false
        } else {

            true
        }
    }

    private fun validarCodigoProducto(): Boolean {
        val codigoProductoInput = tvQr.text.toString().trim()
        return if (codigoProductoInput.isEmpty() && qrScanResult?.isEmpty() != false) {
            tvQr.error = "El campo debe contener un número válido o escanear un código QR"
            true
        } else {
            tvQr.error = null
            false
        }
    }

    private fun validarNombreProducto(): Boolean {
        val nombreProductoInput = nombreProducto.text.toString().trim()
        return if (nombreProductoInput.isEmpty()) {
            nombreProducto.error = "El campo no puede estar vacío"
            true
        } else {
            nombreProducto.error = null
            false
        }
    }

    private fun validarStockProducto(): Boolean {
        val stockProductoInput = stockProducto.text.toString().trim()
        return if (stockProductoInput.isEmpty()) {
            stockProducto.error = "El campo no puede estar vacío"
            true
        } else {
            stockProducto.error = null
            false
        }
    }

    private fun validarDescripcionProducto(): Boolean {
        val descripcionProductoInput = descripcionProducto.text.toString().trim()
        return if (descripcionProductoInput.isEmpty()) {
            descripcionProducto.error = "El campo no puede estar vacío"
            true
        } else {
            descripcionProducto.error = null
            false
        }
    }

    private fun validarPrecioProducto(): Boolean {
        val precioProductoInput = precioProducto.text.toString().trim()
        return if (precioProductoInput.isEmpty()) {
            precioProducto.error = "El campo no puede estar vacío"
            true
        } else {
            precioProducto.error = null
            false
        }
    }

    private fun alertDialogImage() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.alert_opcion_imagen, null)
        builder.setView(mDialogView)
        val btnCamaraimagen = mDialogView.findViewById<LinearLayout>(R.id.camaraLayout)
        val btnGaleriaimagen = mDialogView.findViewById<LinearLayout>(R.id.galeriaLayout)
        val dialog: AlertDialog = builder.create()
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
                tvQr.setText(result.contents)
                //Toast.makeText(this, "El valor escaneado es: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                imageUri = data.data!!
                imageProducto.setImageURI(imageUri)
                //Si escogio la imagen de categoria del alert porner la imagen en el imageview

            } else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                val tempUri = getImageUri(applicationContext, imageBitmap)
                imageUri = tempUri
                imageProducto.setImageURI(imageUri)
            } else {
                super.onActivityResult(requestCode, resultCode, data)
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
            isImageSelected = true // Para que no se caiga la app si no se selecciona una imagen
        } else {
            // El permiso fue denegado, muestra un mensaje o toma alguna acción apropiada
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 300, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun cargarSpinnerProveedor() {
        databaseRefProveedor = FirebaseDatabase.getInstance().getReference("Proveedor")

        // Escuchar los datos de Firebase y configurar el adaptador del Spinner
        databaseRefProveedor.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val spinnerData: MutableList<String> = mutableListOf("Seleccionar opción")
                spinnerIdsProveedor = mutableListOf() // Inicializa spinnerIds

                // Iterar a través de los datos obtenidos del dataSnapshot
                for (snapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(ProveedorClass::class.java)
                    if (value?.idUuser == userId) {
                        value?.nombreProveedor?.let {
                            val idProveedor = snapshot.key
                            val nombreProveedor = value.nombreProveedor
                            if (idProveedor != null && nombreProveedor != null) { // Verificar que el ID y el nombre del proveedor no sean nulos
                                val spinnerItem = "$nombreProveedor" // Combinar ID y nombre del proveedor
                                spinnerData.add(spinnerItem)
                                spinnerIdsProveedor.add(idProveedor)
                            }
                        }
                    }
                }

                // Configurar el adaptador del Spinner
                val adapter = ArrayAdapter<String>(
                    this@RegistrarProductoActivity,
                    android.R.layout.simple_spinner_item,
                    spinnerData
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Asignar el adaptador al Spinner
                nombreProveedor_spinner.adapter = adapter
                // Agregar el listener para capturar la selección del Spinner
                nombreProveedor_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedData = spinnerData[position] // Obtener el elemento seleccionado del Spinner
                        if (selectedData == "Seleccionar opción") {
                            // Se seleccionó la opción "Seleccionar opción"
                            // Realiza las acciones necesarias (por ejemplo, limpiar el RecyclerView)
                        } else {
                            // Se seleccionó un elemento válido
                            val selectedItemId =
                                spinnerIdsProveedor[nombreProveedor_spinner.selectedItemPosition - 1] // Restar 1 para obtener el ID correspondiente al elemento seleccionado
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

    private fun cargarSpinnerCategoria() {
        databaseRefCategoria = FirebaseDatabase.getInstance().getReference("Categoria")

        // Escuchar los datos de Firebase y configurar el adaptador del Spinner
        databaseRefCategoria.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val spinnerData: MutableList<String> = mutableListOf("Seleccionar opción")
                spinnerIdsCategoria = mutableListOf() // Inicializa spinnerIds

                // Iterar a través de los datos obtenidos del dataSnapshot
                for (snapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(CategoriaClass::class.java)
                    if (value?.idUuser == userId) {
                        value?.nombreCategoria?.let {
                            val idCategoria = snapshot.key
                            val nombreCategoria = value.nombreCategoria
                            if (idCategoria != null && nombreCategoria != null) { // Verificar que el ID y el nombre del proveedor no sean nulos
                                val spinnerItem = "$nombreCategoria" // Combinar ID y nombre del proveedor
                                spinnerData.add(spinnerItem)
                                spinnerIdsCategoria.add(idCategoria)
                            }
                        }
                    }
                }

                // Configurar el adaptador del Spinner
                val adapterCategoria = ArrayAdapter<String>(
                    this@RegistrarProductoActivity,
                    android.R.layout.simple_spinner_item,
                    spinnerData
                )
                adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Asignar el adaptador al Spinner
                nombreCategoria_spinner.adapter = adapterCategoria
                // Agregar el listener para capturar la selección del Spinner
                nombreCategoria_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedData = spinnerData[position] // Obtener el elemento seleccionado del Spinner
                        if (selectedData == "Seleccionar opción") {
                            // Se seleccionó la opción "Seleccionar opción"
                            // Realiza las acciones necesarias (por ejemplo, limpiar el RecyclerView)
                        } else {
                            // Se seleccionó un elemento válido
                            val selectedItemId =
                                spinnerIdsCategoria[nombreCategoria_spinner.selectedItemPosition - 1] // Restar 1 para obtener el ID correspondiente al elemento seleccionado
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


    private fun initScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea el código QR")
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    private fun isConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showConnectivityError() {
        val dialogBuilder = AlertDialog.Builder(this)
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

            R.id.proveedorMenu -> {
                //enviar a la actividad de registrar proveedor
                val intent = Intent(this, ProveedoresActivity::class.java)
                startActivity(intent)
            }

            R.id.categoriaMenu -> {
                //enviar a la actividad de registrar categoria
                val intent = Intent(this, CategoriasActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.registrar_producto_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

}
