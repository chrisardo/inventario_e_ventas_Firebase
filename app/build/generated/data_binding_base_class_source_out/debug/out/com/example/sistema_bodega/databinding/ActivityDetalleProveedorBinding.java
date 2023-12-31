// Generated by view binder compiler. Do not edit!
package com.example.sistema_bodega.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistema_bodega.R;
import com.google.android.material.appbar.AppBarLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityDetalleProveedorBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView Country;

  @NonNull
  public final AppBarLayout appbar;

  @NonNull
  public final ImageView imagenProveedor;

  @NonNull
  public final TextView textCategory;

  @NonNull
  public final TextView tvCelularProveedor;

  @NonNull
  public final TextView tvCorreoProveedor;

  @NonNull
  public final TextView tvDireccionProveedor;

  @NonNull
  public final TextView tvNombreProveedor;

  private ActivityDetalleProveedorBinding(@NonNull RelativeLayout rootView,
      @NonNull TextView Country, @NonNull AppBarLayout appbar, @NonNull ImageView imagenProveedor,
      @NonNull TextView textCategory, @NonNull TextView tvCelularProveedor,
      @NonNull TextView tvCorreoProveedor, @NonNull TextView tvDireccionProveedor,
      @NonNull TextView tvNombreProveedor) {
    this.rootView = rootView;
    this.Country = Country;
    this.appbar = appbar;
    this.imagenProveedor = imagenProveedor;
    this.textCategory = textCategory;
    this.tvCelularProveedor = tvCelularProveedor;
    this.tvCorreoProveedor = tvCorreoProveedor;
    this.tvDireccionProveedor = tvDireccionProveedor;
    this.tvNombreProveedor = tvNombreProveedor;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityDetalleProveedorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityDetalleProveedorBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_detalle_proveedor, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityDetalleProveedorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.Country;
      TextView Country = ViewBindings.findChildViewById(rootView, id);
      if (Country == null) {
        break missingId;
      }

      id = R.id.appbar;
      AppBarLayout appbar = ViewBindings.findChildViewById(rootView, id);
      if (appbar == null) {
        break missingId;
      }

      id = R.id.imagenProveedor;
      ImageView imagenProveedor = ViewBindings.findChildViewById(rootView, id);
      if (imagenProveedor == null) {
        break missingId;
      }

      id = R.id.textCategory;
      TextView textCategory = ViewBindings.findChildViewById(rootView, id);
      if (textCategory == null) {
        break missingId;
      }

      id = R.id.tv_celularProveedor;
      TextView tvCelularProveedor = ViewBindings.findChildViewById(rootView, id);
      if (tvCelularProveedor == null) {
        break missingId;
      }

      id = R.id.tv_correoProveedor;
      TextView tvCorreoProveedor = ViewBindings.findChildViewById(rootView, id);
      if (tvCorreoProveedor == null) {
        break missingId;
      }

      id = R.id.tv_direccionProveedor;
      TextView tvDireccionProveedor = ViewBindings.findChildViewById(rootView, id);
      if (tvDireccionProveedor == null) {
        break missingId;
      }

      id = R.id.tv_nombreProveedor;
      TextView tvNombreProveedor = ViewBindings.findChildViewById(rootView, id);
      if (tvNombreProveedor == null) {
        break missingId;
      }

      return new ActivityDetalleProveedorBinding((RelativeLayout) rootView, Country, appbar,
          imagenProveedor, textCategory, tvCelularProveedor, tvCorreoProveedor,
          tvDireccionProveedor, tvNombreProveedor);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
