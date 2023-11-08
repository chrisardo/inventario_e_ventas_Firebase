package com.example.sistema_bodega

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.sistema_bodega.Clases.empresaClass
import com.example.sistema_bodega.Clases.usuariosClass
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class RegistroActivity : AppCompatActivity() {
    lateinit var nombre: TextInputEditText
    lateinit var userName: TextInputEditText
    lateinit var correo: TextInputEditText
    lateinit var celular: TextInputEditText
    lateinit var contrasena: TextInputEditText
    lateinit var nextsignup_button: AppCompatButton

    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference

    private lateinit var progressBarLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        nombre = findViewById(R.id.signup_name)
        userName = findViewById(R.id.signup_username)
        correo = findViewById(R.id.signup_email)
        celular = findViewById(R.id.signup_phone)
        contrasena = findViewById(R.id.signup_password)
        nextsignup_button = findViewById(R.id.nextsignup_button)
        progressBarLayout = findViewById(R.id.progressBarLayout)
        nextsignup_button.setOnClickListener {
            if (validarNombre() && validarUsername() && validarCorreo() && validarCelular() && validarPassword()) {
                if (celular.text.toString().trim().length == 9) {

                    val email_et = correo.text.toString()
                    database = FirebaseDatabase.getInstance()
                    reference = database.getReference("usuarios")
                    var dbEmpresa = database.getReference("empresa")
                    val checkUserDatabase = reference.orderByChild("email").equalTo(userName.text.toString())
                    checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                //Existe el username
                                correo.error = "Correo existe"
                                correo.requestFocus()
                            } else {
                                val intent = Intent(this@RegistroActivity, Registro2Activity::class.java)
                                //put extras
                                intent.putExtra("nombre", nombre.text.toString())
                                intent.putExtra("userName", userName.text.toString())
                                intent.putExtra("correo", correo.text.toString())
                                intent.putExtra("celular", celular.text.toString())
                                intent.putExtra("contrasena", contrasena.text.toString())
                                startActivity(intent)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle onCancelled event
                            Toast.makeText(this@RegistroActivity, error.message, Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    celular.error = "El celular debe tener exactamente 9 dígitos"
                }
            }
        }
    }

    private fun validarNombre(): Boolean {
        val nombre_et = nombre.text.toString().trim()
        return if (nombre_et.isEmpty()) {
            nombre.error = "Debe ingresar un nombre"
            false
        } else {
            nombre.error = null
            true
        }
    }

    private fun validarUsername(): Boolean {
        val userName_et = userName.text.toString().trim()
        return if (userName_et.isEmpty()) {
            userName.error = "Debe ingresar un nombre de usuario"
            false
        } else {
            userName.error = null
            true
        }
    }

    private fun validarCorreo(): Boolean {
        val correo_et = correo.text.toString().trim()
        return if (correo_et.isEmpty()) {
            correo.error = "Debe ingresar un correo"
            false
        } else {
            correo.error = null
            true
        }
    }

    private fun validarCelular(): Boolean {
        val celular_et = celular.text.toString().trim()
        return if (celular_et.isEmpty()) {
            celular.error = "Debe ingresar un celular"
            false
        } else {
            celular.error = null
            true
        }
    }

    private fun validarPassword(): Boolean {
        val password_et = contrasena.text.toString().trim()
        var checkPassword = "^" +
                "(?=.*[0-9])" +         //al menos 1 digito
                "(?=.*[a-z])" +         //al menos 1 minuscula
                "(?=.*[A-Z])" +         //al menos 1 mayuscula
                "(?=.*[@#$%^&+=])" +    //al menos 1 caracter especial
                "(?=\\S+$)" +           //sin espacios
                ".{2,}" +               //al menos 4 caracteres
                "$"
        return if (password_et.isEmpty()) {
            contrasena.error = "Debe ingresar una contraseña"
            false
        }/* else if (!password_et.matches(checkPassword.toRegex())) {
            contraseña.error = "La contraseña es muy débil"
            return false
        } */ else {
            contrasena.error = null
            true
        }
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