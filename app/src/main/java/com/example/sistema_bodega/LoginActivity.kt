package com.example.sistema_bodega

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.sistema_bodega.Clases.usuariosClass
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class LoginActivity : AppCompatActivity() {
    private lateinit var userName: TextInputEditText
    private lateinit var loginPassword: TextInputEditText
    private lateinit var loginButton: AppCompatButton

    private lateinit var pref: preferences//para el shared preferences

    private lateinit var nameFromDB: String
    private lateinit var usernameFromDB: String
    private lateinit var passwordFromDB: String
    private lateinit var estadoFromDB: String
    private lateinit var signupRedirectText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pref = preferences(this@LoginActivity)

        userName = findViewById(R.id.emailEt)
        loginPassword = findViewById(R.id.passET)
        loginButton = findViewById(R.id.button)
        signupRedirectText = findViewById(R.id.tvRegistro)

        loginButton.setOnClickListener {
            if (validarUsername() && validarPassword()) {
                checkUser()
            }
        }

        signupRedirectText = findViewById(R.id.tvRegistro)
        signupRedirectText.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
    private fun validarUsername(): Boolean {
        val userName_et = userName.text.toString().trim()
        return if (userName_et.isEmpty()) {
            userName.error = "Username cannot be empty"
            false
        } else {
            userName.error = null
            true
        }
    }

    private fun validarPassword(): Boolean {
        val password_et = loginPassword.text.toString().trim()
        return if (password_et.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }

    private fun checkUser() {
        val userName_et = userName.text.toString().trim()
        val password_et = loginPassword.text.toString().trim()

        val reference = FirebaseDatabase.getInstance().getReference("usuarios")
        val checkUserDatabase = reference.orderByChild("userName").equalTo(userName_et)
        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val user = item.getValue<usuariosClass>()
                        userName.error = null
                        passwordFromDB = snapshot.child(userName_et).child("password").getValue(String::class.java) as String
                        if (passwordFromDB == password_et) {
                            val IdUser = reference.push().key!!
                            userName.error = null
                            pref.prefStatus = true
                            pref.prefIdUser = user!!.idUser
                            pref.prefNombreUser = user!!.Nombre
                            nameFromDB = snapshot.child(userName_et).child("nombre").getValue(String::class.java) as String
                            estadoFromDB = snapshot.child(userName_et).child("email").getValue(String::class.java) as String

                            if(estadoFromDB == "Inactivo"){
                                Toast.makeText(this@LoginActivity, "Usuario Inactivo", Toast.LENGTH_SHORT).show()
                                return
                            }else{
                                pref.prefLevel = user!!.Nivel

                                if (nameFromDB == "Administrador") {
                                    Toast.makeText(this@LoginActivity, "Bienvenido Admin", Toast.LENGTH_SHORT).show()
                                } else {
                                    var intent: Intent? = null
                                    intent = Intent(this@LoginActivity, MainActivity::class.java)

                                    startActivity(intent)
                                    finish()
                                }
                            }

                        } else {
                            loginPassword.error = "Invalid Credentials"
                            loginPassword.requestFocus()
                        }
                    }
                } else {
                    userName.error = "User does not exist"
                    userName.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
                Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (pref.prefStatus) {
            if (pref.prefLevel == "Administrador") {
                //Intent(this, AdminActivity::class.java)
                Toast.makeText(this@LoginActivity, "Bienvenido Admin", Toast.LENGTH_SHORT).show()
            } else {
                var intent: Intent? = null
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}