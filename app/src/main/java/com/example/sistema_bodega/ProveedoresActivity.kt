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
import com.example.sistema_bodega.Adaptadores.ProveedorMyAdapter
import com.example.sistema_bodega.Clases.CategoriaClass
import com.example.sistema_bodega.Clases.ProveedorClass
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class ProveedoresActivity : AppCompatActivity() {

    //lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: LinearLayout

    private lateinit var proveedorRecyclerView: RecyclerView
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null

    private lateinit var empList: ArrayList<ProveedorClass>
    private lateinit var dbProveedorRef: DatabaseReference
    lateinit var mAdapter: ProveedorMyAdapter
    private lateinit var searchView: SearchView
    private lateinit var tvProvidersEncontrados: TextView
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var alertDialog: AlertDialog
    private lateinit var imaProveedor: ImageView
    private lateinit var et_NombreProveedor: TextInputEditText
    private lateinit var direccionProveedor_et: TextInputEditText
    private lateinit var et_CelularProveedor: TextInputEditText
    private lateinit var et_DireccionProveedor: TextInputEditText
    private lateinit var et_CorreoProveedor: TextInputEditText
    private lateinit var progressBarLayout: RelativeLayout
    private lateinit var  btnRegistroProveedor: AppCompatButton
    private lateinit var  btnClose: ImageView

    private var isImageSelected = false
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var storageRef: StorageReference

    private var imageUri: Uri = Uri.EMPTY // Initialize with a default value
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proveedores)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Proveedores"


        storageRef = FirebaseStorage.getInstance().reference.child("imagesProveedor")

        pref = preferences(this)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        userId = pref.prefIdUser
        searchView = findViewById(R.id.searchView) as SearchView
        searchView.queryHint = "Buscar proveedor"
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
        tvProvidersEncontrados = findViewById(R.id.tvProvidersEncontrados)
        proveedorRecyclerView = findViewById(R.id.proveedorRecyclerView)
        empList = arrayListOf<ProveedorClass>()
        proveedorRecyclerView.layoutManager = LinearLayoutManager(this)
        // productoRecyclerView.setHasFixedSize(true)
        userId = pref.prefIdUser

        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected) {//si hay internet

            getProveedorData()
        } else {
            //Toast.makeText(requireContext(), "No hay internet", Toast.LENGTH_SHORT).show()
            showConnectivityError()
        }
    }

    private fun getProveedorData() {
        proveedorRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        dbProveedorRef = FirebaseDatabase.getInstance().getReference("Proveedor")

        // Realizar la consulta para obtener los productos
        dbProveedorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                empList.clear()

                for (proveedorSnapshot in dataSnapshot.children) {
                    val proveedor = proveedorSnapshot.getValue(ProveedorClass::class.java)
                    val totalProveedoresCount = dataSnapshot.childrenCount.toInt()
                    if (proveedor?.idUuser == userId) {
                        if (totalProveedoresCount > 0) {
                            tvProvidersEncontrados.text = "Total de proveedores registrados: $totalProveedoresCount"
                        } else {
                            tvProvidersEncontrados.text = "No se encontraron proveedores"
                        }
                        empList.add(proveedor!!)

                    }

                }
                progressBar.visibility = View.GONE
                proveedorRecyclerView.visibility = View.VISIBLE
                mAdapter = ProveedorMyAdapter(empList)
                proveedorRecyclerView.adapter = mAdapter


                mAdapter.setOnItemClickListener(object : ProveedorMyAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val proveedor = mAdapter.getItem(position)
                        Toast.makeText(this@ProveedoresActivity, empList[position].nombreProveedor, Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(this@ProveedoresActivity, detalleProveedorActivity::class.java)

                        //put extras
                        intent.putExtra("idProveedor", proveedor.idProveedor)
                        startActivity(intent)
                    }

                    override fun onItemLlamarClick(position: Int) {
                        val phoneNumber = empList[position].celularProveedor

                        if (phoneNumber!!.isNotEmpty()) {
                            // Verificar si tienes el permiso CALL_PHONE antes de realizar la llamada
                            if (ContextCompat.checkSelfPermission(
                                    this@ProveedoresActivity,
                                    Manifest.permission.CALL_PHONE
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                // Realizar la llamada
                                val dialIntent = Intent(
                                    Intent.ACTION_CALL,
                                    Uri.parse("tel:$phoneNumber")
                                ) // ACTION_DIAL - ACTION_CALL
                                startActivity(dialIntent)
                            } else {
                                // Solicitar el permiso CALL_PHONE al usuario si no lo tienes
                                ActivityCompat.requestPermissions(
                                    this@ProveedoresActivity,
                                    arrayOf(Manifest.permission.CALL_PHONE),
                                    1
                                )
                            }
                        } else {
                            Toast.makeText(
                                this@ProveedoresActivity,
                                "El proveedor no tiene número de teléfono",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onItemEnviarEmailClick(position: Int) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_EMAIL,
                                arrayOf(empList[position].correoProveedor)
                            ) // Agrega la dirección de correo electrónico aquí
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "Asunto del correo"
                            ) // Agrega el asunto del correo aquí
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Contenido del correo"
                            ) // Agrega el contenido del correo aquí
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, "Enviar correo electrónico")
                        startActivity(shareIntent)
                    }

                })

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error al obtener los productos
            }
        })
    }

    fun searchList(text: String) {
        val searchList = java.util.ArrayList<ProveedorClass>()
        for (dataClass in empList) {
            if (dataClass.nombreProveedor?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
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

            R.id.addProveedorMenu -> {
                openRegistroProveedorDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openRegistroProveedorDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_registrar_proveedor, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
        imaProveedor = dialogView.findViewById<ImageView>(R.id.ImageProveedor)
        et_NombreProveedor = dialogView.findViewById<TextInputEditText>(R.id.nombreProveedor_et)
        direccionProveedor_et = dialogView.findViewById<TextInputEditText>(R.id.direccionProveedor_et)
        et_CelularProveedor = dialogView.findViewById<TextInputEditText>(R.id.celularProveedor_et)
        et_CorreoProveedor = dialogView.findViewById<TextInputEditText>(R.id.correoProveedor_et)
        progressBarLayout = dialogView.findViewById<RelativeLayout>(R.id.progressBar)
        btnRegistroProveedor = dialogView.findViewById<AppCompatButton>(R.id.saveProveedor_button)
        btnClose = dialogView.findViewById<ImageView>(R.id.btn_cerrar)
        imaProveedor.setOnClickListener {
            alertDialogImage()
        }
        btnRegistroProveedor.setOnClickListener {
            if (validarNombreProveedor() && validarCelularProveedor() && validarCorreoProveedor() && validarDireccionProveedor()) {
                //validando si celular tiene 9 digitos
                if (et_CelularProveedor.text.toString().length == 9) {
                    if (!isImageSelected) { // no se selecciono la imagen
                        uploadDatasinImage()
                    } else {
                        uploadDataconImage()
                    }
                } else {
                    et_CelularProveedor.error = "El celular debe tener 9 digitos"
                }
            }
        }
        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }
    }
    private fun uploadDatasinImage() {
        val NombrProveedor = et_NombreProveedor.text.toString()
        val CelularProveedor = et_CelularProveedor.text.toString()
        val CorreoProveedor = et_CorreoProveedor.text.toString()
        val DireccionProveedor = direccionProveedor_et.text.toString()

        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        btnRegistroProveedor.isEnabled = false
        progressBarLayout.visibility = View.VISIBLE

        val id = dbProveedorRef.push().key
        val proveedor = ProveedorClass(id, NombrProveedor, DireccionProveedor,CelularProveedor, CorreoProveedor, "", pref.prefIdUser)
        dbProveedorRef.child(id!!).setValue(proveedor).addOnCompleteListener {
            // Habilitar el botón de guardar y ocultar el ProgressBar
            btnRegistroProveedor.isEnabled = true
            progressBarLayout.visibility = View.GONE
            Toast.makeText(this, "Proveedor registrado correctamente!", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }.addOnFailureListener { e ->
            // Habilitar el botón de guardar y ocultar el ProgressBar
            btnRegistroProveedor.isEnabled = true
            progressBarLayout.visibility = View.GONE
            Toast.makeText(this, "Error al registrar el proveedor: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadDataconImage() {
        val NombrProveedor = et_NombreProveedor.text.toString()
        val CelularProveedor = et_CelularProveedor.text.toString()
        val CorreoProveedor = et_CorreoProveedor.text.toString()
        val DireccionProveedor = direccionProveedor_et.text.toString()

        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        btnRegistroProveedor.isEnabled = false
        progressBarLayout.visibility = View.VISIBLE

        val imageRef = storageRef.child("${System.currentTimeMillis()}_image.jpg")
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
                val id = dbProveedorRef.push().key
                val proveedor = ProveedorClass(id, NombrProveedor, DireccionProveedor,CelularProveedor, CorreoProveedor, image, pref.prefIdUser)
                dbProveedorRef.child(id!!).setValue(proveedor).addOnCompleteListener {
                    // Habilitar el botón de guardar y ocultar el ProgressBar
                    btnRegistroProveedor.isEnabled = true
                    progressBarLayout.visibility = View.GONE
                    Toast.makeText(this, "Proveedor registrado correctamente!", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }.addOnFailureListener { e ->
                    // Habilitar el botón de guardar y ocultar el ProgressBar
                    btnRegistroProveedor.isEnabled = true
                    progressBarLayout.visibility = View.GONE
                    Toast.makeText(this, "Error al registrar el proveedor: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                btnRegistroProveedor.isEnabled = true
                progressBarLayout.visibility = View.GONE
                Toast.makeText(this, "Error al cargar la imagen: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun validarNombreProveedor(): Boolean {
        val NombreProveedor = et_NombreProveedor.text.toString()
        return if (NombreProveedor.isEmpty()) {
            et_NombreProveedor.error = "El nombre del proveedor es obligatorio"
            false
        } else {
            et_NombreProveedor.error = null
            true
        }
    }
    private fun validarDireccionProveedor(): Boolean {
        val DireccionProveedor = direccionProveedor_et.text.toString()
        return if (DireccionProveedor.isEmpty()) {
            direccionProveedor_et.error = "La direccion del proveedor es obligatorio"
            false
        } else {
            direccionProveedor_et.error = null
            true
        }
    }

    private fun validarCelularProveedor(): Boolean {
        val CelularProveedor = et_CelularProveedor.text.toString()
        return if (CelularProveedor.isEmpty()) {
            et_CelularProveedor.error = "El celular del proveedor es obligatorio"
            false
        } else {
            et_CelularProveedor.error = null
            true
        }
    }

    private fun validarCorreoProveedor(): Boolean {
        val CorreoProveedor = et_CorreoProveedor.text.toString()
        if (CorreoProveedor.isEmpty()) {
            et_CorreoProveedor.error = "El correo del proveedor es obligatorio"
            return false
        } else {
            et_CorreoProveedor.error = null
            return true
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
            imaProveedor.setImageURI(imageUri)
        } else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
            val imageBitmap = data.extras!!.get("data") as Bitmap
            val tempUri = getImageUri(applicationContext, imageBitmap)
            imageUri = tempUri
            imaProveedor.setImageURI(imageUri)
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
            Toast.makeText(this, "Permiso de almacenamiento externo, llamadas denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 300, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_proveedor, menu)

        return super.onCreateOptionsMenu(menu)
    }
}