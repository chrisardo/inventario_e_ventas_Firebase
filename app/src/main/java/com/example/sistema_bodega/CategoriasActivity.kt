package com.example.sistema_bodega

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistema_bodega.Adaptadores.CategoryMyAdapter
import com.example.sistema_bodega.Clases.CategoriaClass
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class CategoriasActivity : AppCompatActivity() {
    private lateinit var pref: preferences
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var mAdapter: CategoryMyAdapter
    private var userId: String? = null
    private var empList: ArrayList<CategoriaClass> = arrayListOf()
    private var searchPosition: Int = -1
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var progressBar: LinearLayout
    private lateinit var alertDialog: AlertDialog
    private lateinit var et_NombreCategoria: TextInputEditText
    private lateinit var progressBarLayout: ProgressBar
    private lateinit var progressBarEditar: ProgressBar
    private lateinit var imaCategoria: ImageView
    private lateinit var btnRegistroCategoria: AppCompatButton
    private lateinit var btnActualizarCategoria: AppCompatButton
    private lateinit var btnClose: ImageView
    private var isImageSelected = false
    private val PICK_IMAGE_REQUEST = 1

    //private lateinit var imageUri: Uri
    private var imageUri: Uri = Uri.EMPTY // Initialize with a default value
    private var idCategoria: String? = null
    var categoriaCount = 0
    private lateinit var tvCategoriasEncontrados: TextView

    private lateinit var storageRef: StorageReference
    private lateinit var dbCategoriaRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Categorias"
        // poner icono
        supportActionBar?.setIcon(R.drawable.ic_category)
        pref = preferences(this)
        dbCategoriaRef = FirebaseDatabase.getInstance().getReference("Categoria")
        storageRef = FirebaseStorage.getInstance().reference.child("imagesCategoria")
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)

        tvCategoriasEncontrados = findViewById(R.id.tvCategoriasEncontrados)
        searchView = findViewById(R.id.searchView)
        progressBar = findViewById(R.id.progressBar)

        setupViews()
        setupListeners()

        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected) {
            fetchCategoryData()
        } else {
            showConnectivityError()
        }
    }

    private fun setupViews() {
        empList = arrayListOf<CategoriaClass>()
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryRecyclerView.setHasFixedSize(true)
        userId = pref.prefIdUser

    }

    private fun fetchCategoryData() {//Obtener datos de la base de datos
        userId = pref.prefIdUser
        progressBar.visibility = View.VISIBLE
        dbCategoriaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    empList.clear()
                    for (empSnapshot in snapshot.children) {
                        val emp = empSnapshot.getValue(CategoriaClass::class.java)
                        val totalCategoryCount = snapshot.childrenCount.toInt()
                        if (emp?.idUuser == userId) {
                            if (totalCategoryCount > 0) {
                                tvCategoriasEncontrados.text =
                                    "Total de categoria registrados: $totalCategoryCount ." // Actualizar el TextView
                            } else {
                                tvCategoriasEncontrados.text =
                                    "No hay categorias registradas"
                            }
                            //categoriaCount++
                            empList.add(emp!!)

                        }
                    }
                    //mAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                    mAdapter = CategoryMyAdapter(empList)
                    categoryRecyclerView.adapter = mAdapter
                    /*if (categoriaCount > 0) {
                        tvCategoriasEncontrados.text =
                            "Total de categoria registrados: $categoriaCount ." // Actualizar el TextView
                    } else {
                        tvCategoriasEncontrados.text =
                            "No hay categorias registradas"
                    }*/
                    mAdapter.setOnItemClickListener(object : CategoryMyAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val categoria = mAdapter.getItem(position)
                            idCategoria = categoria.idCategoria!!
                            openEditarCategoriaDialog(categoria.idCategoria!!)
                        }
                    })

                } else {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@CategoriasActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openEditarCategoriaDialog(idCategoria: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_editar_categoria, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
        imaCategoria = dialogView.findViewById<ImageView>(R.id.ImageCategoria)
        et_NombreCategoria = dialogView.findViewById<TextInputEditText>(R.id.et_nombreCategoria)
        progressBarEditar = dialogView.findViewById<ProgressBar>(R.id.progressBarEditar)
        val checkCateogoriaDatabase = dbCategoriaRef.child(idCategoria)
        checkCateogoriaDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val categoria = snapshot.getValue(CategoriaClass::class.java)
                    et_NombreCategoria.setText(categoria?.nombreCategoria)
                    //Si la imagen está vacia se muestra una por defecto
                    if (categoria?.imageUrl == "") {
                        Glide.with(this@CategoriasActivity).load(R.drawable.ic_category).into(imaCategoria)
                    } else {
                        Glide.with(this@CategoriasActivity).load(categoria?.imageUrl).into(imaCategoria)
                    }
                    //isImageSelected = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CategoriasActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
        btnActualizarCategoria = dialogView.findViewById<AppCompatButton>(R.id.editarCategoria_button)
        btnClose = dialogView.findViewById<ImageView>(R.id.btn_cerrar)
        imaCategoria.setOnClickListener {
            alertDialogImage()
        }
        btnActualizarCategoria.setOnClickListener {
            if (validarNombreCategoria()) {
                //updateEditCategoria(idCategoria)

                updateEditCategoria(imageUri, idCategoria!!)
            }
        }
        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun uploadDataconImage() {

        val NombreCategoria = et_NombreCategoria.text.toString()
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarLayout.visibility = View.VISIBLE
        btnRegistroCategoria.isEnabled = false
        val storageReference = FirebaseStorage.getInstance().reference
        val imageFileName = "categoria_${idCategoria}.jpg" // Nombre del archivo de imagen en la base de datos
        val imageRef = storageReference.child("imagesCategoria/$imageFileName")
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
                val id = dbCategoriaRef.push().key
                val categoria =
                    CategoriaClass(id!!, NombreCategoria, image, pref.prefIdUser)
                dbCategoriaRef.child(id).setValue(categoria).addOnCompleteListener {
                    // Habilitar el botón de guardar y ocultar el ProgressBar
                    btnRegistroCategoria.isEnabled = true
                    progressBarLayout.visibility = View.GONE
                    Toast.makeText(this, "Categoria registrado correctamente!", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }
            } else {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                btnRegistroCategoria.isEnabled = true
                progressBarLayout.visibility = View.GONE
                showToast("Error al cargar la imagen: ${task.exception?.message}")
            }
        }
    }

    private fun uploadDatasinImage() {
        val NombreCategoria = et_NombreCategoria.text.toString()
        val id = dbCategoriaRef.push().key
        val categoria = CategoriaClass(id!!, NombreCategoria, "", pref.prefIdUser)
        dbCategoriaRef.child(id).setValue(categoria).addOnCompleteListener {
            // Habilitar el botón de guardar y ocultar el ProgressBar
            btnRegistroCategoria.isEnabled = true
            progressBarLayout.visibility = View.GONE
            Toast.makeText(this, "Categoria registrado correctamente!", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }
    }

    private fun isImageSelected(): Boolean {
        return if (!isImageSelected) {
            showToast("Seleccione una imagen o tome una foto")
            false
        } else {
            true
        }
    }

    private fun updateEditCategoria(imageUri: Uri, idCategoria: String) {
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarEditar.visibility = View.VISIBLE
        btnActualizarCategoria.isEnabled = false
        val storageReference = FirebaseStorage.getInstance().reference
        val imageFileName = "categoria_${idCategoria}.jpg" // Nombre del archivo de imagen en la base de datos
        val imageRef = storageReference.child("imagesCategoria/$imageFileName")
        val uploadTask = imageRef.putFile(imageUri)
        /*val imageRef = storageRef.child("${System.currentTimeMillis()}_image.jpg")
        val uploadTask = imageRef.putFile(imageUri)*/
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Obtener la URL de descarga de la imagen
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Actualizar los datos en la base de datos
                    val categoria =
                        CategoriaClass(idCategoria, et_NombreCategoria.text.toString(), imageUrl, pref.prefIdUser)
                    val categoriaRef = FirebaseDatabase.getInstance().getReference("Categoria")
                    categoriaRef.child(idCategoria).setValue(categoria).addOnCompleteListener {
                        // Habilitar el botón de guardar y ocultar el ProgressBar
                        btnActualizarCategoria.isEnabled = true
                        progressBarEditar.visibility = View.GONE
                        Toast.makeText(this, "Categoria actualizado correctamente!", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    }.addOnFailureListener() {
                        // Habilitar el botón de guardar y ocultar el ProgressBar
                        btnActualizarCategoria.isEnabled = true
                        progressBarEditar.visibility = View.GONE
                        Toast.makeText(this, "Error al actualizar la categoria: ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this, "Error al subir la imagen: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun alertDialogImage() {
        val builder = android.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.alert_opcion_imagen, null)
        builder.setView(mDialogView)
        val btnCamaraimagen = mDialogView.findViewById<LinearLayout>(R.id.camaraLayout)
        val btnGaleriaimagen = mDialogView.findViewById<LinearLayout>(R.id.galeriaLayout)
        val dialog: android.app.AlertDialog = builder.create()
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            imaCategoria.setImageURI(imageUri)
        } else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
            val imageBitmap = data.extras!!.get("data") as Bitmap
            val tempUri = getImageUri(applicationContext, imageBitmap)
            imageUri = tempUri
            imaCategoria.setImageURI(imageUri)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
        inImage.compress(Bitmap.CompressFormat.JPEG, 300, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun setupListeners() {
        searchView.queryHint = "Buscar categoria"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val position = searchList(newText)
                if (position != -1) {
                    //showProductDialog(newText, position)
                }
                return false
            }
        })
    }

    private fun openRegistroCategoriaDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_registrar_categoria, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
        imaCategoria = dialogView.findViewById<ImageView>(R.id.ImageCategoria)
        et_NombreCategoria = dialogView.findViewById<TextInputEditText>(R.id.et_nombreCategoria)
        progressBarLayout = dialogView.findViewById<ProgressBar>(R.id.progressBarLayout)
        btnRegistroCategoria = dialogView.findViewById<AppCompatButton>(R.id.saveCategoria_button)
        btnClose = dialogView.findViewById<ImageView>(R.id.btn_cerrar)
        imaCategoria.setOnClickListener {
            alertDialogImage()
        }
        btnRegistroCategoria.setOnClickListener {
            if (validarNombreCategoria()) {
                if (!isImageSelected) { // no se selecciono la imagen
                    uploadDatasinImage()
                } else {
                    uploadDataconImage()
                }
            }
        }
        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun validarNombreCategoria(): Boolean {
        val NombreProveedor = et_NombreCategoria.text.toString()
        return if (NombreProveedor.isEmpty()) {
            et_NombreCategoria.error = "El nombre de la categoria es obligatorio"
            false
        } else {
            et_NombreCategoria.error = null
            true
        }
    }

    private fun searchList(text: String): Int {
        val searchList = ArrayList<CategoriaClass>()
        searchPosition = -1
        for ((index, dataClass) in empList.withIndex()) {
            if (dataClass.nombreCategoria?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true) {
                searchList.add(dataClass)
                searchPosition = index
            }
        }
        mAdapter.searchDataList(searchList)
        return searchPosition
    }


    private fun showConnectivityError() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("No hay conexión a Internet. Por favor, conéctate y vuelve a intentarlo.")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.addMenu -> {
                openRegistroCategoriaDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cliente, menu)

        return super.onCreateOptionsMenu(menu)
    }
}