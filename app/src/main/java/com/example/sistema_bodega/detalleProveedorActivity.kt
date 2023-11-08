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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import java.io.ByteArrayOutputStream

class detalleProveedorActivity : AppCompatActivity() {
    private lateinit var databaseProveedorRef: DatabaseReference
    private lateinit var imagenProveedor: ImageView
    private lateinit var tv_nombreProveedor: TextView
    private lateinit var tv_celularProveedor: TextView
    private lateinit var tv_correoProveedor: TextView
    private lateinit var tv_direccionProveedor: TextView
    private lateinit var btn_actualizarProveedor: AppCompatButton
    private lateinit var btn_cancelarProveedor: ImageView
    private lateinit var bundle: Bundle
    private lateinit var idProveedor: String
    private lateinit var nombreProveedor: String
    private lateinit var celularProveedor: String
    private lateinit var correoProveedor: String
    private lateinit var direccionProveedor: String
    private lateinit var imProveedor: String
    private lateinit var dialogLayout: View
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var et_nombreProveedor: TextInputEditText
    private lateinit var et_celularProveedor: TextInputEditText
    private lateinit var et_correoProveedor: TextInputEditText
    private lateinit var alertDialog: AlertDialog
    private var isImageSelected = false
    private lateinit var imageUri: Uri
    private lateinit var dialog: AlertDialog
    private lateinit var progressBarLayout: ProgressBar
    private lateinit var btnActualizarImage: AppCompatButton
    private lateinit var btnCancelarImage: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_proveedor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detalle Proveedor"
        databaseProveedorRef = FirebaseDatabase.getInstance().getReference("Proveedor")

        imagenProveedor = findViewById(R.id.imagenProveedor)
        tv_nombreProveedor = findViewById(R.id.tv_nombreProveedor)
        tv_celularProveedor = findViewById(R.id.tv_celularProveedor)
        tv_correoProveedor = findViewById(R.id.tv_correoProveedor)
        tv_direccionProveedor = findViewById(R.id.tv_direccionProveedor)

        bundle = intent.extras!!
        idProveedor = bundle.getString("idProveedor").toString()
        val checkProveedorDatabase = databaseProveedorRef.child(idProveedor)
        checkProveedorDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    nombreProveedor = snapshot.child("nombreProveedor").value.toString()
                    celularProveedor = snapshot.child("celularProveedor").value.toString()
                    correoProveedor = snapshot.child("correoProveedor").value.toString()
                    imProveedor = snapshot.child("imageUrl").value.toString()
                    direccionProveedor = snapshot.child("direccionProveedor").value.toString()

                    tv_nombreProveedor.text = nombreProveedor
                    tv_celularProveedor.text = celularProveedor
                    tv_correoProveedor.text = correoProveedor
                    tv_direccionProveedor.text = direccionProveedor

                    //Si la imagen está vacia se muestra una por defecto
                    if (imProveedor == "") {
                        Glide.with(this@detalleProveedorActivity).load(R.drawable.proveedor).into(imagenProveedor)
                    } else {
                        Glide.with(this@detalleProveedorActivity).load(imProveedor).into(imagenProveedor)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@detalleProveedorActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
        imagenProveedor.setOnClickListener {
            alertDialogImage()
        }
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


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            //imagenProveedor.setImageURI(imageUri)
            alertDialogActulizarImage()
            //uploadImageToDatabase(imageUri) // Guardar la imagen en la base de datos

        } else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
            val imageBitmap = data.extras!!.get("data") as Bitmap
            val tempUri = getImageUri(applicationContext, imageBitmap)
            imageUri = tempUri
            //imagenProveedor.setImageURI(imageUri)
            alertDialogActulizarImage()
            // uploadImageToDatabase(imageUri) // Guardar la imagen en la base de datos
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun alertDialogActulizarImage() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.alert_image_get, null)
        builder.setView(mDialogView)
        val imageObtenida = mDialogView.findViewById<ImageView>(R.id.imagenObtenida)
        progressBarLayout = mDialogView.findViewById<ProgressBar>(R.id.progressBarLayout)
        btnActualizarImage = mDialogView.findViewById<AppCompatButton>(R.id.btn_actualizarImagen)
        btnCancelarImage = mDialogView.findViewById<AppCompatButton>(R.id.btn_cancelarImagen)
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
        val imageFileName = "proveedor_${idProveedor}.jpg" // Nombre del archivo de imagen en la base de datos
        val imageRef = storageReference.child("imagesProveedor/$imageFileName")

        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Obtener la URL de descarga de la imagen
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Guardar la URL de la imagen en la base de datos
                    databaseProveedorRef.child(idProveedor).child("imageUrl").setValue(imageUrl)
                        .addOnCompleteListener {
                            imagenProveedor.setImageURI(imageUri)
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


    private fun openUpdateDialog(
    ) {
        // Mostrar datos en el alertDialog
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        dialogLayout = inflater.inflate(R.layout.dialog_editar_proveedor, null)
        builder.setView(dialogLayout)

        et_nombreProveedor = dialogLayout.findViewById(R.id.et_nombreProveedor)
        et_celularProveedor = dialogLayout.findViewById(R.id.et_celularProveedor)
        et_correoProveedor = dialogLayout.findViewById(R.id.et_correoProveedor)
        progressBarLayout = dialogLayout.findViewById<ProgressBar>(R.id.progressBarLayout)
        val checkProveedorDatabase = databaseProveedorRef.child(idProveedor)
        checkProveedorDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    nombreProveedor = snapshot.child("nombreProveedor").value.toString()
                    celularProveedor = snapshot.child("celularProveedor").value.toString()
                    correoProveedor = snapshot.child("correoProveedor").value.toString()

                    et_nombreProveedor.setText(nombreProveedor)
                    et_celularProveedor.setText(celularProveedor)
                    et_correoProveedor.setText(correoProveedor)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@detalleProveedorActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })

        btn_actualizarProveedor = dialogLayout.findViewById(R.id.btn_actualizarProveedor)
        btn_cancelarProveedor = dialogLayout.findViewById(R.id.btn_cancelarProveedor)

        alertDialog = builder.create()
        alertDialog.show()

        btn_cancelarProveedor.setOnClickListener {
            alertDialog.dismiss()
        }

        btn_actualizarProveedor.setOnClickListener {
            if (validarNombreProveedorEditado() || validarCelularProveedorEditado() || validarCorreoProveedorEditado()) {
                // Realizar alguna acción adicional si se valida correctamente

            }
        }
    }


    private fun validarNombreProveedorEditado(): Boolean {
        val nombreProveedorEditado = et_nombreProveedor.text.toString().trim()
        return if (nombreProveedorEditado.isEmpty()) {
            et_nombreProveedor.error = "Campo vacío, por favor ingrese el nombre del proveedor"
            true
        } else {
            if (!nombreProveedor.equals(
                    et_nombreProveedor.text.toString().trim()
                )
            ) {// Si el nombre del proveedor es diferente al que se encuentra en la base de datos
                // Deshabilitar el botón de guardar y mostrar el ProgressBar
                progressBarLayout.visibility = View.VISIBLE
                btn_actualizarProveedor.isEnabled = false
                databaseProveedorRef.child(idProveedor).child("nombreProveedor")
                    .setValue(et_nombreProveedor.text.toString().trim())
                    .addOnCompleteListener {
                        // Actualizar el TextView con el nuevo nombre del producto
                        tv_nombreProveedor.text = et_nombreProveedor.text.toString().trim()
                        // Habilitar el botón de guardar y ocultar el ProgressBar
                        btn_actualizarProveedor.isEnabled = true
                        progressBarLayout.visibility = View.GONE
                        Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    }.addOnFailureListener { error ->
                        // Habilitar el botón de guardar y ocultar el ProgressBar
                        btn_actualizarProveedor.isEnabled = true
                        progressBarLayout.visibility = View.GONE
                        Toast.makeText(this, "Error al actualizar el nombre: ${error.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                et_nombreProveedor.error = null
                return true
            } else {
                return false
            }
            false
        }

    }

    private fun validarCelularProveedorEditado(): Boolean {
        val celularProveedorEditado = et_celularProveedor.text.toString().trim()
        return if (celularProveedorEditado.isEmpty()) {
            et_celularProveedor.error = "Campo vacío, por favor ingrese el celular del proveedor"
            true
        } else {
            if (!celularProveedor.equals(et_celularProveedor.text.toString().trim())) {
                if (et_celularProveedor.text.toString().trim().length == 9) {
                    progressBarLayout.visibility = View.VISIBLE
                    btn_actualizarProveedor.isEnabled = false
                    databaseProveedorRef.child(idProveedor).child("celularProveedor")
                        .setValue(et_celularProveedor.text.toString().trim())
                        .addOnCompleteListener {
                            tv_celularProveedor.text = et_celularProveedor.text.toString().trim()
                            btn_actualizarProveedor.isEnabled = true
                            progressBarLayout.visibility = View.GONE
                            Toast.makeText(this, "Celular actualizado", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        }.addOnFailureListener { error ->
                            btn_actualizarProveedor.isEnabled = true
                            progressBarLayout.visibility = View.GONE
                            alertDialog.dismiss()
                            Toast.makeText(this, "Error al actualizar el celular: ${error.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    et_celularProveedor.error = null
                } else {
                    et_celularProveedor.error = "El celular debe tener exactamente 9 dígitos"
                    return true
                }
            } else {
                alertDialog.dismiss()
                return false
            }
            false
        }
    }


    private fun validarCorreoProveedorEditado(): Boolean {
        val correoProveedorEditado = et_correoProveedor.text.toString().trim()
        return if (correoProveedorEditado.isEmpty()) {
            et_correoProveedor.error = "Campo vacío, por favor ingrese el correo del proveedor"
            true
        } else {
            if (!correoProveedor.equals(et_correoProveedor.text.toString().trim())) {
                // Deshabilitar el botón de guardar y mostrar el ProgressBar
                progressBarLayout.visibility = View.VISIBLE
                btn_actualizarProveedor.isEnabled = false
                databaseProveedorRef.child(idProveedor).child("correoProveedor")
                    .setValue(et_correoProveedor.text.toString().trim())
                    .addOnCompleteListener {
                        // Actualizar el TextView con el nuevo nombre del producto
                        tv_correoProveedor.text = et_correoProveedor.text.toString().trim()
                        // Habilitar el botón de guardar y ocultar el ProgressBar
                        btn_actualizarProveedor.isEnabled = true
                        progressBarLayout.visibility = View.GONE
                        Toast.makeText(this, "Correo actualizado", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    }.addOnFailureListener {
                        // Habilitar el botón de guardar y ocultar el ProgressBar
                        btn_actualizarProveedor.isEnabled = true
                        progressBarLayout.visibility = View.GONE
                        Toast.makeText(this, "Correo no actualizado", Toast.LENGTH_SHORT).show()
                    }
                et_correoProveedor.error = null
                return true
            } else {
                alertDialog.dismiss()
                return false
            }
            false
        }

    }


    //Boton de regreso
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.menu_editar -> {
                openUpdateDialog()
                return true
            }

            R.id.llamar -> {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${celularProveedor}")
                startActivity(intent)

            }

            R.id.enviar_email -> {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_EMAIL,
                        arrayOf(correoProveedor)
                    ) // Agrega la dirección de correo electrónico aquí
                    putExtra(Intent.EXTRA_SUBJECT, "Asunto del correo") // Agrega el asunto del correo aquí
                    putExtra(Intent.EXTRA_TEXT, "Contenido del correo") // Agrega el contenido del correo aquí
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, "Enviar correo electrónico")
                startActivity(shareIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.proveedor_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }
}