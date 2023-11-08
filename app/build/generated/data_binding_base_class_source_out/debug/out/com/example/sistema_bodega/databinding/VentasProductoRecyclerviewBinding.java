// Generated by view binder compiler. Do not edit!
package com.example.sistema_bodega.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistema_bodega.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class VentasProductoRecyclerviewBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView ProveedorImagenIv;

  @NonNull
  public final ImageView carritoventa;

  @NonNull
  public final TextView codigoProductoTv;

  @NonNull
  public final TextView nombreProductoTv;

  @NonNull
  public final TextView precioProductoTv;

  @NonNull
  public final CardView recCard;

  @NonNull
  public final TextView stockProductoTv;

  private VentasProductoRecyclerviewBinding(@NonNull CardView rootView,
      @NonNull ImageView ProveedorImagenIv, @NonNull ImageView carritoventa,
      @NonNull TextView codigoProductoTv, @NonNull TextView nombreProductoTv,
      @NonNull TextView precioProductoTv, @NonNull CardView recCard,
      @NonNull TextView stockProductoTv) {
    this.rootView = rootView;
    this.ProveedorImagenIv = ProveedorImagenIv;
    this.carritoventa = carritoventa;
    this.codigoProductoTv = codigoProductoTv;
    this.nombreProductoTv = nombreProductoTv;
    this.precioProductoTv = precioProductoTv;
    this.recCard = recCard;
    this.stockProductoTv = stockProductoTv;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static VentasProductoRecyclerviewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static VentasProductoRecyclerviewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.ventas_producto_recyclerview, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static VentasProductoRecyclerviewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ProveedorImagen_iv;
      ImageView ProveedorImagenIv = ViewBindings.findChildViewById(rootView, id);
      if (ProveedorImagenIv == null) {
        break missingId;
      }

      id = R.id.carritoventa;
      ImageView carritoventa = ViewBindings.findChildViewById(rootView, id);
      if (carritoventa == null) {
        break missingId;
      }

      id = R.id.codigoProducto_tv;
      TextView codigoProductoTv = ViewBindings.findChildViewById(rootView, id);
      if (codigoProductoTv == null) {
        break missingId;
      }

      id = R.id.nombreProducto_tv;
      TextView nombreProductoTv = ViewBindings.findChildViewById(rootView, id);
      if (nombreProductoTv == null) {
        break missingId;
      }

      id = R.id.precioProducto_tv;
      TextView precioProductoTv = ViewBindings.findChildViewById(rootView, id);
      if (precioProductoTv == null) {
        break missingId;
      }

      CardView recCard = (CardView) rootView;

      id = R.id.stockProducto_tv;
      TextView stockProductoTv = ViewBindings.findChildViewById(rootView, id);
      if (stockProductoTv == null) {
        break missingId;
      }

      return new VentasProductoRecyclerviewBinding((CardView) rootView, ProveedorImagenIv,
          carritoventa, codigoProductoTv, nombreProductoTv, precioProductoTv, recCard,
          stockProductoTv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
