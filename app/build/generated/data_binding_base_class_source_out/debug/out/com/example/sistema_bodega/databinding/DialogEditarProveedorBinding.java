// Generated by view binder compiler. Do not edit!
package com.example.sistema_bodega.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistema_bodega.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DialogEditarProveedorBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout barraSuperiorId;

  @NonNull
  public final AppCompatButton btnActualizarProveedor;

  @NonNull
  public final ImageView btnCancelarProveedor;

  @NonNull
  public final TextInputLayout descripcionProductoLayout;

  @NonNull
  public final TextInputEditText etCelularProveedor;

  @NonNull
  public final TextInputEditText etCorreoProveedor;

  @NonNull
  public final TextInputEditText etDireccionProveedor;

  @NonNull
  public final TextInputEditText etNombreProveedor;

  @NonNull
  public final TextInputLayout nombreProductoLayout;

  @NonNull
  public final TextInputLayout precioLayout;

  @NonNull
  public final RelativeLayout progressBar;

  @NonNull
  public final ProgressBar progressBarLayout;

  @NonNull
  public final TextInputLayout stockProductoLayout;

  private DialogEditarProveedorBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout barraSuperiorId, @NonNull AppCompatButton btnActualizarProveedor,
      @NonNull ImageView btnCancelarProveedor, @NonNull TextInputLayout descripcionProductoLayout,
      @NonNull TextInputEditText etCelularProveedor, @NonNull TextInputEditText etCorreoProveedor,
      @NonNull TextInputEditText etDireccionProveedor, @NonNull TextInputEditText etNombreProveedor,
      @NonNull TextInputLayout nombreProductoLayout, @NonNull TextInputLayout precioLayout,
      @NonNull RelativeLayout progressBar, @NonNull ProgressBar progressBarLayout,
      @NonNull TextInputLayout stockProductoLayout) {
    this.rootView = rootView;
    this.barraSuperiorId = barraSuperiorId;
    this.btnActualizarProveedor = btnActualizarProveedor;
    this.btnCancelarProveedor = btnCancelarProveedor;
    this.descripcionProductoLayout = descripcionProductoLayout;
    this.etCelularProveedor = etCelularProveedor;
    this.etCorreoProveedor = etCorreoProveedor;
    this.etDireccionProveedor = etDireccionProveedor;
    this.etNombreProveedor = etNombreProveedor;
    this.nombreProductoLayout = nombreProductoLayout;
    this.precioLayout = precioLayout;
    this.progressBar = progressBar;
    this.progressBarLayout = progressBarLayout;
    this.stockProductoLayout = stockProductoLayout;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogEditarProveedorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogEditarProveedorBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_editar_proveedor, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogEditarProveedorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.barraSuperiorId;
      LinearLayout barraSuperiorId = ViewBindings.findChildViewById(rootView, id);
      if (barraSuperiorId == null) {
        break missingId;
      }

      id = R.id.btn_actualizarProveedor;
      AppCompatButton btnActualizarProveedor = ViewBindings.findChildViewById(rootView, id);
      if (btnActualizarProveedor == null) {
        break missingId;
      }

      id = R.id.btn_cancelarProveedor;
      ImageView btnCancelarProveedor = ViewBindings.findChildViewById(rootView, id);
      if (btnCancelarProveedor == null) {
        break missingId;
      }

      id = R.id.descripcionProductoLayout;
      TextInputLayout descripcionProductoLayout = ViewBindings.findChildViewById(rootView, id);
      if (descripcionProductoLayout == null) {
        break missingId;
      }

      id = R.id.et_celularProveedor;
      TextInputEditText etCelularProveedor = ViewBindings.findChildViewById(rootView, id);
      if (etCelularProveedor == null) {
        break missingId;
      }

      id = R.id.et_correoProveedor;
      TextInputEditText etCorreoProveedor = ViewBindings.findChildViewById(rootView, id);
      if (etCorreoProveedor == null) {
        break missingId;
      }

      id = R.id.et_direccionProveedor;
      TextInputEditText etDireccionProveedor = ViewBindings.findChildViewById(rootView, id);
      if (etDireccionProveedor == null) {
        break missingId;
      }

      id = R.id.et_nombreProveedor;
      TextInputEditText etNombreProveedor = ViewBindings.findChildViewById(rootView, id);
      if (etNombreProveedor == null) {
        break missingId;
      }

      id = R.id.nombreProductoLayout;
      TextInputLayout nombreProductoLayout = ViewBindings.findChildViewById(rootView, id);
      if (nombreProductoLayout == null) {
        break missingId;
      }

      id = R.id.precioLayout;
      TextInputLayout precioLayout = ViewBindings.findChildViewById(rootView, id);
      if (precioLayout == null) {
        break missingId;
      }

      id = R.id.progressBar;
      RelativeLayout progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.progressBarLayout;
      ProgressBar progressBarLayout = ViewBindings.findChildViewById(rootView, id);
      if (progressBarLayout == null) {
        break missingId;
      }

      id = R.id.stockProductoLayout;
      TextInputLayout stockProductoLayout = ViewBindings.findChildViewById(rootView, id);
      if (stockProductoLayout == null) {
        break missingId;
      }

      return new DialogEditarProveedorBinding((LinearLayout) rootView, barraSuperiorId,
          btnActualizarProveedor, btnCancelarProveedor, descripcionProductoLayout,
          etCelularProveedor, etCorreoProveedor, etDireccionProveedor, etNombreProveedor,
          nombreProductoLayout, precioLayout, progressBar, progressBarLayout, stockProductoLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
