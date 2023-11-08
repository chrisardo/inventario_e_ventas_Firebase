package com.example.sistema_bodega

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.cardview.widget.CardView

class ConfiguracionesActivity : AppCompatActivity() {
    private lateinit var reciboTicket_cv: CardView
    private lateinit var compartir_cv: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuraciones)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Configuraciones"
         // poner icono
        supportActionBar?.setIcon(R.drawable.ic_settings)
        reciboTicket_cv = findViewById(R.id.reciboTicket_cv)
        reciboTicket_cv.setOnClickListener {
            //ir a la actividad de recibo de ticket
            val intent = Intent(this@ConfiguracionesActivity, ReciboTicketActivity::class.java)
            startActivity(intent)
        }
        compartir_cv = findViewById(R.id.compartir_cv)
        compartir_cv.setOnClickListener {
          //compartir la app
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.example.sistema_bodega")
            startActivity(Intent.createChooser(intent, "Compartir con"))
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