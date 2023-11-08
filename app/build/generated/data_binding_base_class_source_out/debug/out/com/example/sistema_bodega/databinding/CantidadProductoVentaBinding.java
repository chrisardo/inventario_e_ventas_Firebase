// Generated by view binder compiler. Do not edit!
package com.example.sistema_bodega.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistema_bodega.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CantidadProductoVentaBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView aumentarCantidad;

  @NonNull
  public final LinearLayout barraSuperiorId;

  @NonNull
  public final ImageView cancelarVentaProductoImView;

  @NonNull
  public final TextView cantidadProductoVenta;

  @NonNull
  public final AppCompatButton carritoVentaProductoBtn;

  @NonNull
  public final TextView disminuirCantidad;

  @NonNull
  public final TextView nombreProductoVenta;

  @NonNull
  public final TextView precioProductoVenta;

  private CantidadProductoVentaBinding(@NonNull LinearLayout rootView,
      @NonNull TextView aumentarCantidad, @NonNull LinearLayout barraSuperiorId,
      @NonNull ImageView cancelarVentaProductoImView, @NonNull TextView cantidadProductoVenta,
      @NonNull AppCompatButton carritoVentaProductoBtn, @NonNull TextView disminuirCantidad,
      @NonNull TextView nombreProductoVenta, @NonNull TextView precioProductoVenta) {
    this.rootView = rootView;
    this.aumentarCantidad = aumentarCantidad;
    this.barraSuperiorId = barraSuperiorId;
    this.cancelarVentaProductoImView = cancelarVentaProductoImView;
    this.cantidadProductoVenta = cantidadProductoVenta;
    this.carritoVentaProductoBtn = carritoVentaProductoBtn;
    this.disminuirCantidad = disminuirCantidad;
    this.nombreProductoVenta = nombreProductoVenta;
    this.precioProductoVenta = precioProductoVenta;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CantidadProductoVentaBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CantidadProductoVentaBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.cantidad_producto_venta, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CantidadProductoVentaBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.aumentarCantidad;
      TextView aumentarCantidad = ViewBindings.findChildViewById(rootView, id);
      if (aumentarCantidad == null) {
        break missingId;
      }

      id = R.id.barraSuperiorId;
      LinearLayout barraSuperiorId = ViewBindings.findChildViewById(rootView, id);
      if (barraSuperiorId == null) {
        break missingId;
      }

      id = R.id.cancelarVentaProducto_imView;
      ImageView cancelarVentaProductoImView = ViewBindings.findChildViewById(rootView, id);
      if (cancelarVentaProductoImView == null) {
        break missingId;
      }

      id = R.id.cantidadProductoVenta;
      TextView cantidadProductoVenta = ViewBindings.findChildViewById(rootView, id);
      if (cantidadProductoVenta == null) {
        break missingId;
      }

      id = R.id.carritoVentaProducto_btn;
      AppCompatButton carritoVentaProductoBtn = ViewBindings.findChildViewById(rootView, id);
      if (carritoVentaProductoBtn == null) {
        break missingId;
      }

      id = R.id.disminuirCantidad;
      TextView disminuirCantidad = ViewBindings.findChildViewById(rootView, id);
      if (disminuirCantidad == null) {
        break missingId;
      }

      id = R.id.nombreProductoVenta;
      TextView nombreProductoVenta = ViewBindings.findChildViewById(rootView, id);
      if (nombreProductoVenta == null) {
        break missingId;
      }

      id = R.id.precioProductoVenta;
      TextView precioProductoVenta = ViewBindings.findChildViewById(rootView, id);
      if (precioProductoVenta == null) {
        break missingId;
      }

      return new CantidadProductoVentaBinding((LinearLayout) rootView, aumentarCantidad,
          barraSuperiorId, cancelarVentaProductoImView, cantidadProductoVenta,
          carritoVentaProductoBtn, disminuirCantidad, nombreProductoVenta, precioProductoVenta);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}