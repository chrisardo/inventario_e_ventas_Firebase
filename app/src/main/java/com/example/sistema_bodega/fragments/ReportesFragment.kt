package com.example.sistema_bodega.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.example.sistema_bodega.*
import com.github.clans.fab.FloatingActionMenu

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var clientes_cv: CardView
    private lateinit var analiticasVentas_cv: CardView
    private lateinit var proveedor_cv: CardView
    private lateinit var categorias_cv: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reportes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analiticasVentas_cv = view.findViewById(R.id.analiticasVentas_cv)
        analiticasVentas_cv.setOnClickListener {
            val intent = Intent(requireActivity(), GraficoVentaActivity::class.java)
            startActivity(intent)
        }
        clientes_cv = view.findViewById(R.id.clientes_cv)
        clientes_cv.setOnClickListener {
            val intent = Intent(requireActivity(), ClientesActivity::class.java)
            startActivity(intent)
        }
        proveedor_cv = view.findViewById(R.id.proveedor_cv)
        proveedor_cv.setOnClickListener {
            val intent = Intent(requireActivity(), ProveedoresActivity::class.java)
            startActivity(intent)
        }
        categorias_cv = view.findViewById(R.id.categorias_cv)
        categorias_cv.setOnClickListener {
            val intent = Intent(requireActivity(), CategoriasActivity::class.java)
            startActivity(intent)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}