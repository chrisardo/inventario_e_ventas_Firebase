package com.example.sistema_bodega

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sistema_bodega.Clases.ProveedorClass
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class Registrar_Proveedor_Activity : AppCompatActivity() {
    lateinit var ImageProveedor: ImageView
    lateinit var nombreProveedor: TextInputEditText
    lateinit var celularProveedor: TextInputEditText
    lateinit var correonProveedor: TextInputEditText
    lateinit var saveButton: AppCompatButton
    private lateinit var imageUri: Uri
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var pref: preferences//para el shared preferences
    private var isImageSelected = false

    private lateinit var progressBarLayout: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_proveedor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Verificar la conexión a Internet
        if (!isConnected()) {
            showConnectivityError()
            return
        }
        pref = preferences(this@Registrar_Proveedor_Activity)
        databaseRef = FirebaseDatabase.getInstance().getReference("Proveedor")

        storageRef = FirebaseStorage.getInstance().reference.child("imagesProveedor")
        progressBarLayout = findViewById(R.id.progressBarLayout)
        ImageProveedor = findViewById(R.id.ImageProveedor)
        nombreProveedor = findViewById(R.id.nombreProveedor_et)
        celularProveedor = findViewById(R.id.celularProveedor_et)
        correonProveedor = findViewById(R.id.correoProveedor_et)
        ImageProveedor.setOnClickListener {
            alertDialogImage()
        }
        saveButton = findViewById(R.id.saveProveedor_button)
        saveButton.setOnClickListener {
            if ( isImageSelected() && validarNombreProveedor() && validarCelularProveedor() && validarCorreoProveedor()) {

                //validando si tiene 9 digitos
                if (celularProveedor.text.toString().length == 9) {
                    uploadData()
                } else {
                    celularProveedor.error = "El celular debe tener 9 digitos"
                }
            }
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            ImageProveedor.setImageURI(imageUri)
        } else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
            val imageBitmap = data.extras!!.get("data") as Bitmap
            val tempUri = getImageUri(applicationContext, imageBitmap)
            imageUri = tempUri
            ImageProveedor.setImageURI(imageUri)
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

    private fun uploadData() {

        val NombreProveedor = nombreProveedor.text.toString()
        val CelularProveedor = celularProveedor.text.toString()
        val CorreoProveedor = correonProveedor.text.toString()
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarLayout.visibility = View.VISIBLE
        saveButton.isEnabled = false

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
                val id = databaseRef.push().key
                val proveedor =
                    ProveedorClass(id!!, NombreProveedor, CelularProveedor, CorreoProveedor, image, pref.prefIdUser)
                databaseRef.child(id).setValue(proveedor).addOnCompleteListener {
                    // Habilitar el botón de guardar y ocultar el ProgressBar
                    saveButton.isEnabled = true
                    progressBarLayout.visibility = View.GONE
                    Toast.makeText(this, "Proveedor registrado correctamente!", Toast.LENGTH_SHORT).show()
                    vaciarCampos()
                }.addOnFailureListener { exception ->
                    // Habilitar el botón de guardar y ocultar el ProgressBar
                    saveButton.isEnabled = true
                    progressBarLayout.visibility = View.GONE
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show() }
            } else {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                saveButton.isEnabled = true
                progressBarLayout.visibility = View.GONE
                showToast("Error al cargar la imagen: ${task.exception?.message}")
            }
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
    private fun vaciarCampos() {
        nombreProveedor.setText("")
        celularProveedor.setText("")
        correonProveedor.setText("")
        ImageProveedor.setImageResource(R.drawable.ic_image)
    }
    private fun validarNombreProveedor(): Boolean {
        val NombreProveedor = nombreProveedor.text.toString()
        return if (NombreProveedor.isEmpty()) {
            nombreProveedor.error = "El nombre del proveedor es obligatorio"
            false
        } else {
            nombreProveedor.error = null
            true
        }
    }

    private fun validarCelularProveedor(): Boolean {
        val CelularProveedor = celularProveedor.text.toString()
        return if (CelularProveedor.isEmpty()) {
            celularProveedor.error = "El celular del proveedor es obligatorio"
            false
        } else {
            celularProveedor.error = null
            true
        }
    }

    private fun validarCorreoProveedor(): Boolean {
        val CorreoProveedor = correonProveedor.text.toString()
        if (CorreoProveedor.isEmpty()) {
            correonProveedor.error = "El correo del proveedor es obligatorio"
            return false
        } else {
            correonProveedor.error = null
            return true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        }
        return super.onOptionsItemSelected(item)
    }

}