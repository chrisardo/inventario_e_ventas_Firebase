package com.example.sistema_bodega

import android.widget.Toast
import com.example.sistema_bodega.Clases.detalleTicketClass

class borrador {
    /*
       dbTiketVentaRef.child(idticket).setValue(ticketVentaClass).addOnCompleteListener {
            dbVentaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val addedProducts = mutableSetOf<String>() // Usamos un Set para asegurar elementos únicos

                    for (ventaCarrito in snapshot.children) {
                        val ventaCarritoClass = ventaCarrito.getValue(VentaCarritoClass::class.java)
                        if (ventaCarritoClass?.IdUser == userId) {
                            val productoId = ventaCarritoClass?.idProducto

                            if (!addedProducts.contains(productoId)) {
                                addedProducts.add(productoId ?: "")

                                val idDetalleTicket = dbdDetalleTicketVentaRef.push().key
                                val detalleTicketVentaClass = detalleTicketClass(
                                    idDetalleTicket!!,
                                    idticket,
                                    productoId,
                                    ventaCarritoClass!!.cantidad,
                                    ventaCarritoClass.precioTotal
                                )
                                dbdDetalleTicketVentaRef.child(idDetalleTicket)
                                    .setValue(detalleTicketVentaClass)
                                    .addOnCompleteListener {
                                        dbVentaRef.child(ventaCarritoClass.IdCarritoVenta!!)
                                            .removeValue()
                                            .addOnCompleteListener {
                                                Toast.makeText(
                                                    this@CarritoVentasActivity,
                                                    "Venta realizada",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                totalSum = 0.0
                                                alertDialog.dismiss()
                                            }
                                    }
                                    .addOnFailureListener() {
                                        Toast.makeText(
                                            this@CarritoVentasActivity,
                                            "Error al guardar el detalle de la venta",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CarritoVentasActivity, "Error al guardar el ticket", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }.addOnFailureListener {
            Toast.makeText(this, "Error al guardar el ticket", Toast.LENGTH_SHORT).show()
        }


     */
}