package com.example.sistema_bodega

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sistema_bodega.Clases.ProductoClass
import com.example.sistema_bodega.Clases.empresaClass
import com.example.sistema_bodega.Clases.usuariosClass
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import java.io.ByteArrayOutputStream

class Registro2Activity : AppCompatActivity() {
    lateinit var bundle: Bundle
    lateinit var et_nombreEmpresa: TextInputEditText
    lateinit var et_rucEmpresa: TextInputEditText
    lateinit var et_direccionEmpresa: TextInputEditText
    lateinit var et_descripcionEmpresa: TextInputEditText
    lateinit var et_razonSocialEmpresa: TextInputEditText
    lateinit var registrarseButton: AppCompatButton
    private lateinit var progressBarLayout: RelativeLayout
    lateinit var img_logoempresa: ImageView
    lateinit var database: FirebaseDatabase
    lateinit var dbEmpresa: DatabaseReference
    lateinit var reference: DatabaseReference
    private var isImageSelected = false
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    private lateinit var nombre: String
    private lateinit var userName: String
    private lateinit var correo: String
    private lateinit var celular: String
    private lateinit var contrasena: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        database = FirebaseDatabase.getInstance()
        dbEmpresa = database.getReference("empresa")
        reference = database.getReference("usuarios")

        //obteniendo los datos del registroactivity
        bundle = intent.extras!!
        nombre = bundle.getString("nombre").toString()
        userName = bundle.getString("userName").toString()
        correo = bundle.getString("correo").toString()
        celular = bundle.getString("celular").toString()
        contrasena = bundle.getString("contrasena").toString()
        img_logoempresa = findViewById(R.id.img_logoempresa)
        et_nombreEmpresa = findViewById(R.id.et_nombreEmpresa)
        et_rucEmpresa = findViewById(R.id.et_rucEmpresa)
        et_razonSocialEmpresa = findViewById(R.id.et_razonSocialEmpresa)
        et_direccionEmpresa = findViewById(R.id.et_direccionEmpresa)
        et_descripcionEmpresa = findViewById(R.id.et_descripcionEmpresa)
        registrarseButton = findViewById(R.id.signup_button)
        progressBarLayout = findViewById(R.id.progressBarLayout)
        img_logoempresa.setOnClickListener { alertDialogImage() }
        registrarseButton.setOnClickListener {
            if (validarNombreEmpresa() && validarRucEmpresa() && validarDireccionEmpresa() && validarDescripcionEmpresa()) {
                if (!isImageSelected) { // no se selecciono la imagen
                    uploadsinlogoEmpresa()
                } else {
                    uploadDataconImage()
                }
            }
        }
    }

    private fun uploadsinlogoEmpresa() {
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        registrarseButton.isEnabled = false
        progressBarLayout.visibility = View.VISIBLE
        val IdUser = reference.push().key!!
        val usuariosClass = usuariosClass(
            IdUser,
            nombre,
            userName,
            correo,
            contrasena,
            "Usuario",
            "Activo"
        )
        reference.child(userName).setValue(usuariosClass).addOnCompleteListener {
            val empresaClass = empresaClass(
                et_nombreEmpresa.text.toString(),
                et_razonSocialEmpresa.text.toString(),
                et_rucEmpresa.text.toString(),
                "",
                et_direccionEmpresa.text.toString(),
                celular,
                et_descripcionEmpresa.text.toString(),
                IdUser
            )
            dbEmpresa.child(IdUser).setValue(empresaClass).addOnCompleteListener {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                registrarseButton.isEnabled = true
                progressBarLayout.visibility = View.GONE
                Toast.makeText(
                    this@Registro2Activity,
                    "Registrado correctamente!",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@Registro2Activity, LoginActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener() {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                registrarseButton.isEnabled = true
                progressBarLayout.visibility = View.GONE
                Toast.makeText(
                    this@Registro2Activity,
                    "Error al registrar el usuario!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener() {
            // Habilitar el botón de guardar y ocultar el ProgressBar
            registrarseButton.isEnabled = true
            progressBarLayout.visibility = View.GONE
            Toast.makeText(
                this@Registro2Activity,
                "Error al registrar el usuario!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadDataconImage() {
        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        registrarseButton.isEnabled = false
        progressBarLayout.visibility = View.VISIBLE
        val storageReference = FirebaseStorage.getInstance().reference
        val imageFileName =
            "${System.currentTimeMillis()}_image.jpg" // Nombre del archivo de imagen en la base de datos

        val imageRef = storageReference.child("imagesEmpresas/$imageFileName")
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
                val IdUser = reference.push().key!!
                val usuariosClass = usuariosClass(
                    IdUser,
                    nombre,
                    userName,
                    correo,
                    contrasena,
                    "Usuario",
                    "Activo"
                )
                reference.child(userName).setValue(usuariosClass).addOnCompleteListener {
                    val empresaClass = empresaClass(
                        et_nombreEmpresa.text.toString(),
                        et_razonSocialEmpresa.text.toString(),
                        et_rucEmpresa.text.toString(),
                        image,
                        et_direccionEmpresa.text.toString(),
                        celular,
                        et_descripcionEmpresa.text.toString(),
                        IdUser
                    )
                    dbEmpresa.child(IdUser).setValue(empresaClass).addOnCompleteListener {
                        // Habilitar el botón de guardar y ocultar el ProgressBar
                        registrarseButton.isEnabled = true
                        progressBarLayout.visibility = View.GONE
                        Toast.makeText(
                            this@Registro2Activity,
                            "Registrado correctamente!",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@Registro2Activity, LoginActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener() {
                        // Habilitar el botón de guardar y ocultar el ProgressBar
                        registrarseButton.isEnabled = true
                        progressBarLayout.visibility = View.GONE
                        Toast.makeText(
                            this@Registro2Activity,
                            "Error al registrar el usuario!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener() {
                    // Habilitar el botón de guardar y ocultar el ProgressBar
                    registrarseButton.isEnabled = true
                    progressBarLayout.visibility = View.GONE
                    Toast.makeText(
                        this@Registro2Activity,
                        "Error al registrar el usuario!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                registrarseButton.isEnabled = true
                progressBarLayout.visibility = View.GONE
                Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun validarNombreEmpresa(): Boolean {
        val nombreEmpresa_et = et_nombreEmpresa.text.toString().trim()
        return if (nombreEmpresa_et.isEmpty()) {
            et_nombreEmpresa.error = "Ingresa el nombre de sy empresa"
            false
        } else {
            et_nombreEmpresa.error = null
            true
        }
    }

    private fun validarRucEmpresa(): Boolean {
        val rucEmpresa_et = et_rucEmpresa.text.toString().trim()
        return if (rucEmpresa_et.isEmpty()) {
            et_rucEmpresa.error = "Debe ingresar ruc"
            false
        } else {
            et_rucEmpresa.error = null
            true
        }
    }

    private fun validarDireccionEmpresa(): Boolean {
        val direccionEmpresa_et = et_direccionEmpresa.text.toString().trim()
        return if (direccionEmpresa_et.isEmpty()) {
            et_direccionEmpresa.error = "Debe ingresar ruc"
            false
        } else {
            et_direccionEmpresa.error = null
            true
        }
    }

    private fun validarDescripcionEmpresa(): Boolean {
        val descripcionEmpresa_et = et_descripcionEmpresa.text.toString().trim()
        return if (descripcionEmpresa_et.isEmpty()) {
            et_descripcionEmpresa.error = "Debe ingresar descripcion"
            false
        } else {
            et_descripcionEmpresa.error = null
            true
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
                //Toast.makeText(this, "El valor escaneado es: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                imageUri = data.data!!
                img_logoempresa.setImageURI(imageUri)
            } else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                val tempUri = getImageUri(applicationContext, imageBitmap)
                imageUri = tempUri
                img_logoempresa.setImageURI(imageUri)
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