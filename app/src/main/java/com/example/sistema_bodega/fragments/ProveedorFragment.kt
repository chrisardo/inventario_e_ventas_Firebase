package com.example.sistema_bodega.fragments

import android.content.Context
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_bodega.*
import com.example.sistema_bodega.Adaptadores.ProveedorMyAdapter
import com.example.sistema_bodega.Clases.ProveedorClass
import com.example.sistema_bodega.R
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProveedorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProveedorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var floatingActionButtonMenu: FloatingActionMenu
    private lateinit var fABAdd: FloatingActionButton

    //lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: LinearLayout

    private lateinit var proveedorRecyclerView: RecyclerView
    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null

    private lateinit var empList: ArrayList<ProveedorClass>
    private lateinit var dbProveedorRef: DatabaseReference
    lateinit var mAdapter: ProveedorMyAdapter
    private lateinit var searchView: SearchView
    private lateinit var tvProvidersEncontrados: TextView
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proveedor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = preferences(requireContext())
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Obtener la referencia al elemento en el que deseas asignar el evento onClick
        floatingActionButtonMenu = view.findViewById<FloatingActionMenu>(R.id.fabPrincipal)
        floatingActionButtonMenu.setClosedOnTouchOutside(false)//para cerrar el menu flotante
        //Al tocar la pantalla se cierra el menu flotante principal
        floatingActionButtonMenu.setOnMenuToggleListener { opened ->
            if (opened) {//si esta abierto
                //Toast.makeText(requireContext(), "Menu abierto", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(requireContext(), "Menu cerrado", Toast.LENGTH_SHORT).show()
            }
        }
        floatingActionButtonMenu.setOnMenuButtonClickListener {
            if (floatingActionButtonMenu.isOpened) {//si esta abierto
                floatingActionButtonMenu.close(true)
            } else {
                floatingActionButtonMenu.open(true)
            }
        }

        fABAdd = view.findViewById<FloatingActionButton>(R.id.fabAddProveedor)
        fABAdd.setOnClickListener {
            val intent = Intent(requireActivity(), Registrar_Proveedor_Activity::class.java)
            startActivity(intent)
        }
        searchView = view.findViewById(R.id.searchView)
        searchView.queryHint = "Buscar proveedor"
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
        progressBar = view.findViewById(R.id.progressBar)
        tvProvidersEncontrados = view.findViewById(R.id.tvProvidersEncontrados)
        proveedorRecyclerView = view.findViewById(R.id.proveedorRecyclerView)
        empList = arrayListOf<ProveedorClass>()
        proveedorRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        // productoRecyclerView.setHasFixedSize(true)
        userId = pref.prefIdUser

        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected) {//si hay internet

            getProveedorData()
        } else {
            //Toast.makeText(requireContext(), "No hay internet", Toast.LENGTH_SHORT).show()
            showConnectivityError()
        }
    }

    private fun getProveedorData() {
        proveedorRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        dbProveedorRef = FirebaseDatabase.getInstance().getReference("Proveedor")

        // Realizar la consulta para obtener los productos
        dbProveedorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                empList.clear()

                for (proveedorSnapshot in dataSnapshot.children) {
                    val proveedor = proveedorSnapshot.getValue(ProveedorClass::class.java)
                    val totalProveedoresCount = dataSnapshot.childrenCount.toInt()
                    if (proveedor?.idUuser == userId) {
                        if (totalProveedoresCount > 0) {
                            tvProvidersEncontrados.text = "Total de proveedores registrados: $totalProveedoresCount"
                        } else {
                            tvProvidersEncontrados.text = "No se encontraron proveedores"
                        }
                        empList.add(proveedor!!)

                    }

                }
                progressBar.visibility = View.GONE
                proveedorRecyclerView.visibility = View.VISIBLE
                mAdapter = ProveedorMyAdapter(empList)
                proveedorRecyclerView.adapter = mAdapter


                mAdapter.setOnItemClickListener(object : ProveedorMyAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val proveedor = mAdapter.getItem(position)
                        Toast.makeText(requireContext(), empList[position].nombreProveedor, Toast.LENGTH_LONG).show()
                        val intent = Intent(requireActivity(), detalleProveedorActivity::class.java)

                        //put extras
                        intent.putExtra("idProveedor", proveedor.idProveedor)
                        startActivity(intent)
                    }

                    override fun onItemLlamarClick(position: Int) {
                        val phoneNumber = empList[position].celularProveedor

                        if (phoneNumber!!.isNotEmpty()) {
                            // Verificar si tienes el permiso CALL_PHONE antes de realizar la llamada
                            if (ContextCompat.checkSelfPermission(
                                    requireContext(),
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
                                    requireActivity(),
                                    arrayOf(Manifest.permission.CALL_PHONE),
                                    1
                                )
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "El proveedor no tiene número de teléfono",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onItemEnviarEmailClick(position: Int) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_EMAIL,
                                arrayOf(empList[position].correoProveedor)
                            ) // Agrega la dirección de correo electrónico aquí
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "Asunto del correo"
                            ) // Agrega el asunto del correo aquí
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Contenido del correo"
                            ) // Agrega el contenido del correo aquí
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, "Enviar correo electrónico")
                        startActivity(shareIntent)
                    }

                })

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error al obtener los productos
            }
        })
    }
    fun searchList(text: String) {
        val searchList = java.util.ArrayList<ProveedorClass>()
        for (dataClass in empList) {
            if (dataClass.nombreProveedor?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }
        mAdapter.searchDataList(searchList)
    }

    private fun showConnectivityError() {
        val dialogBuilder = android.app.AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage("No hay conexión a Internet. Por favor, conéctate y vuelve a intentarlo.")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                //finish()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Si el permiso CALL_PHONE fue otorgado, realizar la llamada nuevamente
            // Puedes llamar a la función onItemLlamarClick() aquí para que realice la llamada
        } else {
            Toast.makeText(requireContext(), "Permiso de llamada denegado", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProveedorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProveedorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}