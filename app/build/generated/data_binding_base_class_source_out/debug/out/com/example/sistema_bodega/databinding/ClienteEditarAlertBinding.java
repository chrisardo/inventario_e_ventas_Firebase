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

public final class ClienteEditarAlertBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout barraSuperiorId;

  @NonNull
  public final AppCompatButton btnActualizarCliente;

  @NonNull
  public final ImageView btnCerrar;

  @NonNull
  public final TextInputLayout celuarClienteLayout;

  @NonNull
  public final TextInputLayout dniLayout;

  @NonNull
  public final TextInputEditText etCelularCliente;

  @NonNull
  public final TextInputEditText etDniCliente;

  @NonNull
  public final TextInputEditText etNombreCliente;

  @NonNull
  public final TextInputLayout nombreProductoLayout;

  @NonNull
  public final RelativeLayout progressBar;

  @NonNull
  public final ProgressBar progressBarLayout;

  private ClienteEditarAlertBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout barraSuperiorId, @NonNull AppCompatButton btnActualizarCliente,
      @NonNull ImageView btnCerrar, @NonNull TextInputLayout celuarClienteLayout,
      @NonNull TextInputLayout dniLayout, @NonNull TextInputEditText etCelularCliente,
      @NonNull TextInputEditText etDniCliente, @NonNull TextInputEditText etNombreCliente,
      @NonNull TextInputLayout nombreProductoLayout, @NonNull RelativeLayout progressBar,
      @NonNull ProgressBar progressBarLayout) {
    this.rootView = rootView;
    this.barraSuperiorId = barraSuperiorId;
    this.btnActualizarCliente = btnActualizarCliente;
    this.btnCerrar = btnCerrar;
    this.celuarClienteLayout = celuarClienteLayout;
    this.dniLayout = dniLayout;
    this.etCelularCliente = etCelularCliente;
    this.etDniCliente = etDniCliente;
    this.etNombreCliente = etNombreCliente;
    this.nombreProductoLayout = nombreProductoLayout;
    this.progressBar = progressBar;
    this.progressBarLayout = progressBarLayout;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ClienteEditarAlertBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ClienteEditarAlertBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.cliente_editar_alert, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ClienteEditarAlertBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.barraSuperiorId;
      LinearLayout barraSuperiorId = ViewBindings.findChildViewById(rootView, id);
      if (barraSuperiorId == null) {
        break missingId;
      }

      id = R.id.btn_actualizarCliente;
      AppCompatButton btnActualizarCliente = ViewBindings.findChildViewById(rootView, id);
      if (btnActualizarCliente == null) {
        break missingId;
      }

      id = R.id.btn_cerrar;
      ImageView btnCerrar = ViewBindings.findChildViewById(rootView, id);
      if (btnCerrar == null) {
        break missingId;
      }

      id = R.id.celuarClienteLayout;
      TextInputLayout celuarClienteLayout = ViewBindings.findChildViewById(rootView, id);
      if (celuarClienteLayout == null) {
        break missingId;
      }

      id = R.id.dniLayout;
      TextInputLayout dniLayout = ViewBindings.findChildViewById(rootView, id);
      if (dniLayout == null) {
        break missingId;
      }

      id = R.id.et_celularCliente;
      TextInputEditText etCelularCliente = ViewBindings.findChildViewById(rootView, id);
      if (etCelularCliente == null) {
        break missingId;
      }

      id = R.id.et_DniCliente;
      TextInputEditText etDniCliente = ViewBindings.findChildViewById(rootView, id);
      if (etDniCliente == null) {
        break missingId;
      }

      id = R.id.et_nombreCliente;
      TextInputEditText etNombreCliente = ViewBindings.findChildViewById(rootView, id);
      if (etNombreCliente == null) {
        break missingId;
      }

      id = R.id.nombreProductoLayout;
      TextInputLayout nombreProductoLayout = ViewBindings.findChildViewById(rootView, id);
      if (nombreProductoLayout == null) {
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

      return new ClienteEditarAlertBinding((LinearLayout) rootView, barraSuperiorId,
          btnActualizarCliente, btnCerrar, celuarClienteLayout, dniLayout, etCelularCliente,
          etDniCliente, etNombreCliente, nombreProductoLayout, progressBar, progressBarLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
