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
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistema_bodega.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityConfiguracionesBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView ProveedorImagenIv;

  @NonNull
  public final TextView codigoProductoTv;

  @NonNull
  public final CardView compartirCv;

  @NonNull
  public final ImageView compartirIv;

  @NonNull
  public final CardView reciboTicketCv;

  private ActivityConfiguracionesBinding(@NonNull LinearLayout rootView,
      @NonNull ImageView ProveedorImagenIv, @NonNull TextView codigoProductoTv,
      @NonNull CardView compartirCv, @NonNull ImageView compartirIv,
      @NonNull CardView reciboTicketCv) {
    this.rootView = rootView;
    this.ProveedorImagenIv = ProveedorImagenIv;
    this.codigoProductoTv = codigoProductoTv;
    this.compartirCv = compartirCv;
    this.compartirIv = compartirIv;
    this.reciboTicketCv = reciboTicketCv;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityConfiguracionesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityConfiguracionesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_configuraciones, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityConfiguracionesBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ProveedorImagen_iv;
      ImageView ProveedorImagenIv = ViewBindings.findChildViewById(rootView, id);
      if (ProveedorImagenIv == null) {
        break missingId;
      }

      id = R.id.codigoProducto_tv;
      TextView codigoProductoTv = ViewBindings.findChildViewById(rootView, id);
      if (codigoProductoTv == null) {
        break missingId;
      }

      id = R.id.compartir_cv;
      CardView compartirCv = ViewBindings.findChildViewById(rootView, id);
      if (compartirCv == null) {
        break missingId;
      }

      id = R.id.compartir_iv;
      ImageView compartirIv = ViewBindings.findChildViewById(rootView, id);
      if (compartirIv == null) {
        break missingId;
      }

      id = R.id.reciboTicket_cv;
      CardView reciboTicketCv = ViewBindings.findChildViewById(rootView, id);
      if (reciboTicketCv == null) {
        break missingId;
      }

      return new ActivityConfiguracionesBinding((LinearLayout) rootView, ProveedorImagenIv,
          codigoProductoTv, compartirCv, compartirIv, reciboTicketCv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
