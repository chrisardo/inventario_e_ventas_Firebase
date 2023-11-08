package com.example.sistema_bodega

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.bumptech.glide.Glide
import com.example.sistema_bodega.Clases.empresaClass
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import java.io.ByteArrayOutputStream

class ReciboTicketActivity : AppCompatActivity() {
    private lateinit var imLogoEmpresa: ImageView
    private lateinit var tv_nombreEmpresa: TextView
    private lateinit var tv_rucEmpresa: TextView
    private lateinit var tv_celularEmpresa: TextView
    private lateinit var tv_direccionEmpresa: TextView
    private lateinit var tv_descripcionEmpresa: TextView
    private lateinit var databaseEmpresaRef: DatabaseReference
    private lateinit var databaseUsarioRef: DatabaseReference
    private lateinit var pref: preferences//para el shared preferences
    private lateinit var tv_razonScialEmpresa: TextView
    private lateinit var dialogLayout: View
    private lateinit var nombreEmpresa_editText: TextInputEditText
    private lateinit var razonSocial_editText: TextInputEditText
    private lateinit var ruc_editText: TextInputEditText
    private lateinit var celular_editText: TextInputEditText
    private lateinit var direccion_editText: TextInputEditText
    private lateinit var descripcion_editText: TextInputEditText
    private lateinit var cancelarEmpresa_imView: ImageView
    private lateinit var alertDialog: AlertDialog
    var userId: String? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var editarEmpresa_btn: AppCompatButton
    private lateinit var dialog: AlertDialog
    private val PICK_IMAGE_REQUEST = 1
    private var isImageSelected = false
    private lateinit var imageUri: Uri
    private lateinit var btnActualizarImage: AppCompatButton
    private lateinit var btnCancelarImage: AppCompatButton
    private lateinit var imLogoEmpresaDialog: ImageView
    private lateinit var progressBarLayout: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recibo_ticket)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Recibo / Ticket"
        pref = preferences(this)
        databaseEmpresaRef = FirebaseDatabase.getInstance().getReference("empresa")
        databaseUsarioRef = FirebaseDatabase.getInstance().getReference("usuarios")

        // poner icono
        supportActionBar?.setIcon(R.drawable.ic_store)
        imLogoEmpresa = findViewById(R.id.imLogoEmpresa)
        tv_nombreEmpresa = findViewById(R.id.tv_nombreEmpresa)
        tv_rucEmpresa = findViewById(R.id.tv_rucEmpresa)
        tv_razonScialEmpresa = findViewById(R.id.tv_razonScialEmpresa)
        tv_celularEmpresa = findViewById(R.id.tv_celularEmpresa)
        tv_direccionEmpresa = findViewById(R.id.tv_direccionEmpresa)
        tv_descripcionEmpresa = findViewById(R.id.tv_descripcionEmpresa)
        imLogoEmpresa.setOnClickListener {
            alertDialogImage()
        }

        userId = pref.prefIdUser
        obtenerDatosEmpresa()
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
        val storageReference = FirebaseStorage.getInstance().reference
        // Nombre del archivo de imagen en la base de datos
        val imageFileName = "empresa_${userId}.jpg"
        val imageRef = storageReference.child("imagesEmpresas/$imageFileName")
        val uploadTask = imageRef.putFile(imageUri)
        // Subir imagen a Firebase Storage
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // Guardar la URL de la imagen en la base de datos
                val imageDbRef = FirebaseDatabase.getInstance().getReference("empresa")
                    .child(userId!!)
                    .child("imagenEmpresa")
                imageDbRef.setValue(downloadUri.toString())
                    .addOnCompleteListener(OnCompleteListener<Void> { task ->
                        if (task.isSuccessful) {
                            // Habilitar el botón de guardar y ocultar el ProgressBar
                            progressBarLayout.visibility = View.GONE
                            btnActualizarImage.isEnabled = true
                            //pOner la imagen  del alert dialog en el imageview
                            Glide.with(this)
                                .load(downloadUri)
                                .into(imLogoEmpresa)

                            // Mostrar un mensaje de éxito al usuario
                            Toast.makeText(this, "Imagen actualizada", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        } else {
                            // Mostrar un mensaje al usuario si ocurre un error
                            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_LONG).show()
                        }
                    })
            } else {
                // Mostrar un mensaje al usuario si ocurre un error
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    private fun editarEmpresaAlert() {
        //Mostrar datos en el alertDialog
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        //builder.setTitle("Editar Producto")
        dialogLayout = inflater.inflate(R.layout.alert_info_empresa_edit, null)
        builder.setView(dialogLayout)

        nombreEmpresa_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_nombreEmpresa)
        razonSocial_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_razonSocial)
        ruc_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_ruc)
        celular_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_celular)
        direccion_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_direccion)
        progressBar = dialogLayout.findViewById<ProgressBar>(R.id.progressBarLayout)
        descripcion_editText = dialogLayout.findViewById<TextInputEditText>(R.id.et_descripcion)
        editarEmpresa_btn = dialogLayout.findViewById<AppCompatButton>(R.id.editarEmpresa_btn)
        cancelarEmpresa_imView = dialogLayout.findViewById<ImageView>(R.id.cancelarEmpresa_imView)
        //obtener los datos en el edittext del alrterdialog
        val checkEmpresaDatabase = databaseEmpresaRef.orderByChild("idUser").equalTo(userId)
        checkEmpresaDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (empresa in snapshot.children) {
                        val nombreEmpresa = empresa.child("nombreEmpresa").getValue(String::class.java)
                        val razonSocialEmpresa = empresa.child("razonSocial").getValue(String::class.java)
                        val rucEmpresa = empresa.child("ruc").getValue(String::class.java)
                        val direccionEmpresa = empresa.child("direccion").getValue(String::class.java)
                        val descripcionEmpresa = empresa.child("informacionEmpresa").getValue(String::class.java)
                        val celularEmpresa = empresa.child("celular").getValue(String::class.java)
                        nombreEmpresa_editText.setText(nombreEmpresa)
                        razonSocial_editText.setText(razonSocialEmpresa)
                        ruc_editText.setText(rucEmpresa)
                        celular_editText.setText(celularEmpresa)
                        direccion_editText.setText(direccionEmpresa)
                        descripcion_editText.setText(descripcionEmpresa)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReciboTicketActivity, "Error al obtener datos de la empresa", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        editarEmpresa_btn.setOnClickListener {
            if (!validarNombreEditado() && !validarRazonSocialEditado() && !validarDireccionEditado() && !validarRucEditado() && !validarCelularEditado() && !validarDescripcionEditado()) {
                val celularInput = celular_editText.text.toString().trim()
                //validando si celular tiene 9 digitos
                if (celular_editText.text.toString().length == 9) {
                    updateEmpresaData()
                } else {
                    celular_editText.error = "El celular debe tener 9 digitos"
                }
            }
        }
        cancelarEmpresa_imView.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun updateEmpresaData() {
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        editarEmpresa_btn.isEnabled = false
        progressBar.visibility = View.GONE

        val updateCompleteListener = OnCompleteListener<Void> { task ->
            // Actualizar los TextView con los nuevos datos del producto
            if (task.isSuccessful) {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                editarEmpresa_btn.isEnabled = true
                progressBar.visibility = View.VISIBLE

                // Actualizar los textview con los nuevos datos
                tv_nombreEmpresa.text = nombreEmpresa_editText.text.toString().trim()
                tv_razonScialEmpresa.text = razonSocial_editText.text.toString().trim()
                tv_rucEmpresa.text = ruc_editText.text.toString().trim()
                tv_celularEmpresa.text = celular_editText.text.toString().trim()
                tv_direccionEmpresa.text = direccion_editText.text.toString().trim()
                tv_descripcionEmpresa.text = descripcion_editText.text.toString().trim()


                //Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                editarEmpresa_btn.isEnabled = true
                progressBar.visibility = View.VISIBLE
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }

        }
        val nombreEmpresa = nombreEmpresa_editText.text.toString().trim()
        val razonSocial = razonSocial_editText.text.toString().trim()
        val ruc = ruc_editText.text.toString().trim()
        val celular = celular_editText.text.toString().trim()
        val direccion = direccion_editText.text.toString().trim()
        val descripcion = descripcion_editText.text.toString().trim()

        databaseEmpresaRef.child(userId!!).apply {
            child("nombreEmpresa").setValue(nombreEmpresa)
                .addOnCompleteListener(updateCompleteListener)
            child("razonSocial").setValue(razonSocial)
                .addOnCompleteListener(updateCompleteListener)
            child("ruc").setValue(ruc)
                .addOnCompleteListener(updateCompleteListener)
            child("celular").setValue(celular)
                .addOnCompleteListener(updateCompleteListener)
            child("direccion").setValue(direccion)
                .addOnCompleteListener(updateCompleteListener)
            child("informacionEmpresa").setValue(descripcion)
                .addOnCompleteListener(updateCompleteListener)
        }


    }

    private fun validarNombreEditado(): Boolean {
        val nombreEmpresaInput = nombreEmpresa_editText.text.toString().trim()
        return if (nombreEmpresaInput.isEmpty()) {
            nombreEmpresa_editText.error = "El nombre de la empresa no puede estar vacío"
            true
        } else {

            nombreEmpresa_editText.error = null
            false
        }
    }

    private fun validarRazonSocialEditado(): Boolean {
        val razonSocialInput = razonSocial_editText.text.toString().trim()
        return if (razonSocialInput.isEmpty()) {
            razonSocial_editText.error = "La razón social no puede estar vacío"
            true
        } else {

            razonSocial_editText.error = null
            false
        }
    }

    private fun validarDireccionEditado(): Boolean {
        val direccionInput = direccion_editText.text.toString().trim()
        return if (direccionInput.isEmpty()) {
            direccion_editText.error = "La dirección no puede estar vacío"
            true
        } else {

            false
        }
    }

    private fun validarCelularEditado(): Boolean {
        val celularInput = celular_editText.text.toString().trim()
        return if (celularInput.isEmpty()) {
            celular_editText.error = "El celular no puede estar vacío"
            true
        } else {
            celular_editText.error = null
            false
        }
    }

    private fun validarDescripcionEditado(): Boolean {
        val descripcionInput = descripcion_editText.text.toString().trim()
        return if (descripcionInput.isEmpty()) {
            descripcion_editText.error = "La descripción no puede estar vacío"
            true
        } else {
            descripcion_editText.error = null
            false
        }
    }

    private fun validarRucEditado(): Boolean {
        val rucInput = ruc_editText.text.toString().trim()
        return if (rucInput.isEmpty()) {
            ruc_editText.error = "El RUC no puede estar vacío"
            true
        } else {
            ruc_editText.error = null
            false
        }
    }

    private fun obtenerDatosEmpresa() {
        //obtener datos de la empresa
        val checkEmpresaDatabase = databaseEmpresaRef.orderByChild("idUser").equalTo(userId)
        checkEmpresaDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (empresa in snapshot.children) {
                        val nombreEmpresa = empresa.child("nombreEmpresa").getValue(String::class.java)
                        val razonSocialEmpresa = empresa.child("razonSocial").getValue(String::class.java)
                        val rucEmpresa = empresa.child("ruc").getValue(String::class.java)
                        val direccionEmpresa = empresa.child("direccion").getValue(String::class.java)
                        val descripcionEmpresa = empresa.child("informacionEmpresa").getValue(String::class.java)
                        val logoEmpresa = empresa.child("imagenEmpresa").getValue(String::class.java)
                        val celularEmpresa = empresa.child("celular").getValue(String::class.java)
                        tv_nombreEmpresa.text = nombreEmpresa
                        tv_razonScialEmpresa.text = razonSocialEmpresa
                        tv_rucEmpresa.text = rucEmpresa
                        tv_direccionEmpresa.text = direccionEmpresa
                        tv_celularEmpresa.text = celularEmpresa
                        tv_descripcionEmpresa.text = descripcionEmpresa
                        //Si la imagen está vacia se muestra una por defecto
                        if (logoEmpresa.isNullOrEmpty()) {

                        } else {
                            Glide.with(this@ReciboTicketActivity)
                                .load(logoEmpresa)
                                .into(imLogoEmpresa)
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReciboTicketActivity, "Error al obtener datos de la empresa", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.editarempresaMenu -> {
                editarEmpresaAlert()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recibo_ticket, menu)

        return super.onCreateOptionsMenu(menu)
    }
}