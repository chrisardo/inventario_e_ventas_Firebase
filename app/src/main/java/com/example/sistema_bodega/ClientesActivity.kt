package com.example.sistema_bodega

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_bodega.Adaptadores.ClientesMyAdapter
import com.example.sistema_bodega.Clases.ClientesClass
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ClientesActivity : AppCompatActivity() {
    private lateinit var tvClientesEncontrados: TextView

    //lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: LinearLayout

    private lateinit var clientesRecyclerView: RecyclerView
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null

    private lateinit var empList: ArrayList<ClientesClass>
    private lateinit var dbClienteRef: DatabaseReference
    lateinit var mAdapter: ClientesMyAdapter
    private lateinit var searchView: SearchView

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var et_NombreCliente: TextInputEditText
    private lateinit var et_CelularCliente: TextInputEditText
    private lateinit var et_DniCliente: TextInputEditText
    private lateinit var btnUpdateCliente: AppCompatButton
    private lateinit var btnRegistroCliente: AppCompatButton
    private lateinit var btnClose: ImageView
    private lateinit var progressBarLayout: ProgressBar
    private lateinit var alertDialog: AlertDialog
    var clientesCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Clientes"
        // poner icono
        supportActionBar?.setIcon(R.drawable.ic_supervised_user)
        pref = preferences(this)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        searchView = findViewById<SearchView>(R.id.searchView) as SearchView
        searchView.queryHint = "Buscar clientes"
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
        tvClientesEncontrados = findViewById(R.id.tvClientesEncontrados)
        clientesRecyclerView = findViewById(R.id.clientesRecyclerView)
        empList = arrayListOf<ClientesClass>() // Inicializar la lista de empleados
        clientesRecyclerView.layoutManager = LinearLayoutManager(this)
        // productoRecyclerView.setHasFixedSize(true)
        userId = pref.prefIdUser

        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected) {//si hay internet

            getEmployeesData()
        } else {
            //Toast.makeText(requireContext(), "No hay internet", Toast.LENGTH_SHORT).show()
            showConnectivityError()
        }
    }

    private fun getEmployeesData() {
        clientesRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        dbClienteRef = FirebaseDatabase.getInstance().getReference("Clientes")
        dbClienteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                var clientesCount = 0 // Agregar variable contador
                for (clienteSnapshot in snapshot.children) {
                    val cliente = clienteSnapshot.getValue(ClientesClass::class.java)
                    val totalClientesCount = snapshot.childrenCount.toInt()

                    if (cliente?.idUuser == userId) {
                        if (totalClientesCount > 0) {
                            tvClientesEncontrados.text =
                                "Total de clientes registrados: $totalClientesCount ." // Actualizar el TextView
                        } else {
                            tvClientesEncontrados.text =
                                "No hay clientes registrados"
                        }
                        clientesCount++ // Incrementar el contador
                        empList.add(cliente!!)

                    }

                }
                progressBar.visibility = View.GONE
                clientesRecyclerView.visibility = View.VISIBLE
                mAdapter = ClientesMyAdapter(empList)
                clientesRecyclerView.adapter = mAdapter
                /*if(clientesCount>0) {
                    tvClientesEncontrados.text =
                        "Total de clientes registrados: $clientesCount ." // Actualizar el TextView
                }else{
                    tvClientesEncontrados.text =
                        "No hay clientes registrados"
                }*/
                mAdapter.setOnItemClickListener(object : ClientesMyAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val clientes = mAdapter.getItem(position)
                        openUpdateDialog(clientes.idCliente!!)
                    }

                    override fun onItemLlamarClick(position: Int) {
                        val phoneNumber = empList[position].celularCliente

                        if (phoneNumber!!.isNotEmpty()) {
                            // Verificar si tienes el permiso CALL_PHONE antes de realizar la llamada
                            if (ContextCompat.checkSelfPermission(
                                    this@ClientesActivity,
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
                                    this@ClientesActivity,
                                    arrayOf(Manifest.permission.CALL_PHONE),
                                    1
                                )
                            }
                        } else {
                            Toast.makeText(
                                this@ClientesActivity,
                                "El proveedor no tiene número de teléfono",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                clientesRecyclerView.visibility = View.VISIBLE
            }
        })
    }

    private fun openRegistroClienteDialog() {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.registrar_cliente_alert, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
        et_NombreCliente = dialogView.findViewById<TextInputEditText>(R.id.et_nombreCliente)
        et_CelularCliente = dialogView.findViewById<TextInputEditText>(R.id.et_celularCliente)
        progressBarLayout = dialogView.findViewById<ProgressBar>(R.id.progressBarLayout)
        et_DniCliente = dialogView.findViewById<TextInputEditText>(R.id.et_DniCliente)
        btnRegistroCliente = dialogView.findViewById<AppCompatButton>(R.id.btn_registrarCliente)
        btnClose = dialogView.findViewById<ImageView>(R.id.btn_cerrar)
        btnRegistroCliente.setOnClickListener {
            if (validarDniCliente() && validarCelularCliente() && validarNombreCliente()) {
                //validando si celular tiene 9 digitos
                if (et_CelularCliente.text.toString().length == 9) {
                    //validando si el dni tiene 8 digitos
                    if (et_DniCliente.text.toString().length == 8) {
                        registrarData()
                    } else {
                        et_DniCliente.error = "El DNI debe tener 8 digitos"
                    }
                } else {
                    et_CelularCliente.error = "El celular debe tener 9 digitos"
                }
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    private fun openUpdateDialog(idCliente: String) {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.cliente_editar_alert, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog.show()
        et_NombreCliente = dialogView.findViewById<TextInputEditText>(R.id.et_nombreCliente)
        et_CelularCliente = dialogView.findViewById<TextInputEditText>(R.id.et_celularCliente)
        progressBarLayout = dialogView.findViewById<ProgressBar>(R.id.progressBarLayout)
        et_DniCliente = dialogView.findViewById<TextInputEditText>(R.id.et_DniCliente)
        val checkClienteDatabase = dbClienteRef.child(idCliente)
        checkClienteDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cliente = snapshot.getValue(ClientesClass::class.java)
                et_NombreCliente.setText(cliente?.nombreCliente)
                et_CelularCliente.setText(cliente?.celularCliente)
                et_DniCliente.setText(cliente?.dniCliente)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClientesActivity, "Error al cargar datos", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        btnUpdateCliente = dialogView.findViewById<AppCompatButton>(R.id.btn_actualizarCliente)
        btnClose = dialogView.findViewById<ImageView>(R.id.btn_cerrar)
        btnUpdateCliente.setOnClickListener {
            if (validarDniCliente() || validarCelularCliente() || validarNombreCliente()) {
                //validando si celular tiene 9 digitos
                if (et_CelularCliente.text.toString().length == 9) {
                    //validando si el dni tiene 8 digitos
                    if (et_DniCliente.text.toString().length == 8) {
                        updateData(idCliente)
                    } else {
                        et_DniCliente.error = "El DNI debe tener 8 digitos"
                    }
                } else {
                    et_CelularCliente.error = "El celular debe tener 9 digitos"
                }
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun registrarData() {
        val NombreCliente = et_NombreCliente.text.toString()
        val DniCliente = et_DniCliente.text.toString()
        val CelularCliente = et_CelularCliente.text.toString()

        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarLayout.visibility = View.VISIBLE
        btnRegistroCliente.isEnabled = false

        val id = dbClienteRef.push().key
        val cliente = ClientesClass(id!!, NombreCliente, DniCliente, CelularCliente, pref.prefIdUser)
        dbClienteRef.child(id).setValue(cliente).addOnCompleteListener {
            // Habilitar el botón de guardar y ocultar el ProgressBar
            btnRegistroCliente.isEnabled = true
            progressBarLayout.visibility = View.GONE
            Toast.makeText(this, "Cliente registrado correctamente!", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()

        }.addOnFailureListener { exception ->
            // Habilitar el botón de guardar y ocultar el ProgressBar
            btnRegistroCliente.isEnabled = true
            progressBarLayout.visibility = View.GONE
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData(idCliente: String) {
        val nombreCliente = et_NombreCliente.text.toString().trim()
        val celularCliente = et_CelularCliente.text.toString().trim()
        val dniCliente = et_DniCliente.text.toString().trim()

        // Deshabilitar el botón de guardar y mostrar el ProgressBar
        progressBarLayout.visibility = View.VISIBLE
        btnUpdateCliente.isEnabled = false
        val updateCompleteListener = object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                // Habilitar el botón de guardar y ocultar el ProgressBar
                progressBarLayout.visibility = View.GONE
                btnUpdateCliente.isEnabled = true

                // Actualizar los TextView del recyclerview con los nuevos datos del cliente


                if (task.isSuccessful) {
                    Toast.makeText(this@ClientesActivity, "Cliente actualizado", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(this@ClientesActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dbClienteRef.child(idCliente).apply {
            child("nombreCliente").setValue(nombreCliente).addOnCompleteListener(updateCompleteListener)
            child("celularCliente").setValue(celularCliente).addOnCompleteListener(updateCompleteListener)
            child("dniCliente").setValue(dniCliente).addOnCompleteListener(updateCompleteListener)
        }

    }

    private fun validarDniCliente(): Boolean {
        val DniCliente = et_DniCliente.text.toString()
        return if (DniCliente.isEmpty()) {
            et_DniCliente.error = "El D.N.I del proveedor es obligatorio"
            false
        } else {
            et_DniCliente.error = null
            true
        }
    }

    private fun validarNombreCliente(): Boolean {
        val NombreProveedor = et_NombreCliente.text.toString()
        return if (NombreProveedor.isEmpty()) {
            et_NombreCliente.error = "El nombre del cliente es obligatorio"
            false
        } else {
            et_NombreCliente.error = null
            true
        }
    }

    private fun validarCelularCliente(): Boolean {
        val CelularProveedor = et_CelularCliente.text.toString()
        return if (CelularProveedor.isEmpty()) {
            et_CelularCliente.error = "El celular del proveedor es obligatorio"
            false
        } else {
            et_CelularCliente.error = null
            true
        }
    }

    fun searchList(text: String) {
        val searchList = java.util.ArrayList<ClientesClass>()
        for (dataClass in empList) {
            if (dataClass.nombreCliente?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true ||
                dataClass.dniCliente?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true
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
            R.id.addMenu -> {
                openRegistroClienteDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cliente, menu)
        return super.onCreateOptionsMenu(menu)
    }
}