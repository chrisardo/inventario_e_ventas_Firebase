package com.example.sistema_bodega

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.sistema_bodega.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var tvUserName: TextView
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null

    lateinit var homeFragment: HomeFragment
    lateinit var proveedorFragment: ProveedorFragment
    lateinit var productoFragment: ProductoFragment
    lateinit var ventasFragment: VentasFragment
    lateinit var reportesFragment: ReportesFragment
    private lateinit var botton_navigation: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref = preferences(this@MainActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_storefront)
        upArrow?.setBounds(
            0,
            0,
            resources.getDimensionPixelSize(R.dimen.icon_size),
            resources.getDimensionPixelSize(R.dimen.icon_size)
        )
        supportActionBar?.setHomeAsUpIndicator(upArrow)

        tvUserName = findViewById(R.id.tvUserName)
        //tvUserName.text = pref.prefNombreUser
        botton_navigation = findViewById(R.id.botton_navigation)
        homeFragment = HomeFragment()
        proveedorFragment = ProveedorFragment()
        productoFragment = ProductoFragment()
        ventasFragment = VentasFragment()
        reportesFragment = ReportesFragment()

        makeCurrentFragment(homeFragment)//para que al iniciar la app se muestre el home fragment
        botton_navigation.setOnNavigationItemSelectedListener {//para el menu de abajo
            when (it.itemId) {
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_producto -> makeCurrentFragment(productoFragment)
                //R.id.ic_proveedores -> makeCurrentFragment(proveedorFragment)
                R.id.ic_ventas -> makeCurrentFragment(ventasFragment)
                R.id.ic_reportes -> makeCurrentFragment(reportesFragment)

            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: androidx.fragment.app.Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    //para el menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.carritoventaMenu -> {
                startActivity(Intent(this@MainActivity, CarritoVentasActivity::class.java))
            }

            R.id.configuraciones -> {
                val intent = Intent(this@MainActivity, ConfiguracionesActivity::class.java)
                startActivity(intent)
            }

            R.id.salir -> {
                pref.prefClear()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}