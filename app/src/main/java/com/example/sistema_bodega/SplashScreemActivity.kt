package com.example.sistema_bodega

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SplashScreemActivity : AppCompatActivity() {
    private lateinit var pref: preferences//para el shared preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screem)

        pref = preferences(this@SplashScreemActivity)
        Handler().postDelayed({
            if (pref.prefStatus) {
                if (pref.prefLevel == "Administrador") {
                    //Intent(this, AdminActivity::class.java)
                    Toast.makeText(this@SplashScreemActivity, "Bienvenido Admin", Toast.LENGTH_SHORT).show()
                } else {
                    var intent: Intent? = null
                    intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                var intent: Intent? = null
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1000)
    }
}