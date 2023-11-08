// Generated by view binder compiler. Do not edit!
package com.example.sistema_bodega.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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

public final class ActivityRegistrarProductoBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final TextInputEditText descripcionProductoEt;

  @NonNull
  public final TextInputLayout descripcionProductoLayout;

  @NonNull
  public final ImageView imageProducto;

  @NonNull
  public final ImageView imagenQr;

  @NonNull
  public final Spinner nombreCategoriaSpinner;

  @NonNull
  public final TextInputEditText nombreProductoEt;

  @NonNull
  public final TextInputLayout nombreProductoLayout;

  @NonNull
  public final Spinner nombreProveedorSpinner;

  @NonNull
  public final TextInputLayout precioLayout;

  @NonNull
  public final TextInputEditText precioProductoEt;

  @NonNull
  public final RelativeLayout progressBar;

  @NonNull
  public final ProgressBar progressBarLayout;

  @NonNull
  public final AppCompatButton saveProductoButton;

  @NonNull
  public final TextInputEditText stockProductoEt;

  @NonNull
  public final TextInputLayout stockProductoLayout;

  @NonNull
  public final TextInputEditText tvQrEt;

  @NonNull
  public final TextInputLayout tvQrLayout;

  private ActivityRegistrarProductoBinding(@NonNull ScrollView rootView,
      @NonNull TextInputEditText descripcionProductoEt,
      @NonNull TextInputLayout descripcionProductoLayout, @NonNull ImageView imageProducto,
      @NonNull ImageView imagenQr, @NonNull Spinner nombreCategoriaSpinner,
      @NonNull TextInputEditText nombreProductoEt, @NonNull TextInputLayout nombreProductoLayout,
      @NonNull Spinner nombreProveedorSpinner, @NonNull TextInputLayout precioLayout,
      @NonNull TextInputEditText precioProductoEt, @NonNull RelativeLayout progressBar,
      @NonNull ProgressBar progressBarLayout, @NonNull AppCompatButton saveProductoButton,
      @NonNull TextInputEditText stockProductoEt, @NonNull TextInputLayout stockProductoLayout,
      @NonNull TextInputEditText tvQrEt, @NonNull TextInputLayout tvQrLayout) {
    this.rootView = rootView;
    this.descripcionProductoEt = descripcionProductoEt;
    this.descripcionProductoLayout = descripcionProductoLayout;
    this.imageProducto = imageProducto;
    this.imagenQr = imagenQr;
    this.nombreCategoriaSpinner = nombreCategoriaSpinner;
    this.nombreProductoEt = nombreProductoEt;
    this.nombreProductoLayout = nombreProductoLayout;
    this.nombreProveedorSpinner = nombreProveedorSpinner;
    this.precioLayout = precioLayout;
    this.precioProductoEt = precioProductoEt;
    this.progressBar = progressBar;
    this.progressBarLayout = progressBarLayout;
    this.saveProductoButton = saveProductoButton;
    this.stockProductoEt = stockProductoEt;
    this.stockProductoLayout = stockProductoLayout;
    this.tvQrEt = tvQrEt;
    this.tvQrLayout = tvQrLayout;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRegistrarProductoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRegistrarProductoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_registrar_producto, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRegistrarProductoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.descripcionProducto_et;
      TextInputEditText descripcionProductoEt = ViewBindings.findChildViewById(rootView, id);
      if (descripcionProductoEt == null) {
        break missingId;
      }

      id = R.id.descripcionProductoLayout;
      TextInputLayout descripcionProductoLayout = ViewBindings.findChildViewById(rootView, id);
      if (descripcionProductoLayout == null) {
        break missingId;
      }

      id = R.id.imageProducto;
      ImageView imageProducto = ViewBindings.findChildViewById(rootView, id);
      if (imageProducto == null) {
        break missingId;
      }

      id = R.id.imagenQr;
      ImageView imagenQr = ViewBindings.findChildViewById(rootView, id);
      if (imagenQr == null) {
        break missingId;
      }

      id = R.id.nombreCategoria_spinner;
      Spinner nombreCategoriaSpinner = ViewBindings.findChildViewById(rootView, id);
      if (nombreCategoriaSpinner == null) {
        break missingId;
      }

      id = R.id.nombreProducto_et;
      TextInputEditText nombreProductoEt = ViewBindings.findChildViewById(rootView, id);
      if (nombreProductoEt == null) {
        break missingId;
      }

      id = R.id.nombreProductoLayout;
      TextInputLayout nombreProductoLayout = ViewBindings.findChildViewById(rootView, id);
      if (nombreProductoLayout == null) {
        break missingId;
      }

      id = R.id.nombreProveedor_spinner;
      Spinner nombreProveedorSpinner = ViewBindings.findChildViewById(rootView, id);
      if (nombreProveedorSpinner == null) {
        break missingId;
      }

      id = R.id.precioLayout;
      TextInputLayout precioLayout = ViewBindings.findChildViewById(rootView, id);
      if (precioLayout == null) {
        break missingId;
      }

      id = R.id.precioProducto_et;
      TextInputEditText precioProductoEt = ViewBindings.findChildViewById(rootView, id);
      if (precioProductoEt == null) {
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

      id = R.id.saveProducto_button;
      AppCompatButton saveProductoButton = ViewBindings.findChildViewById(rootView, id);
      if (saveProductoButton == null) {
        break missingId;
      }

      id = R.id.stockProducto_et;
      TextInputEditText stockProductoEt = ViewBindings.findChildViewById(rootView, id);
      if (stockProductoEt == null) {
        break missingId;
      }

      id = R.id.stockProductoLayout;
      TextInputLayout stockProductoLayout = ViewBindings.findChildViewById(rootView, id);
      if (stockProductoLayout == null) {
        break missingId;
      }

      id = R.id.tvQr_et;
      TextInputEditText tvQrEt = ViewBindings.findChildViewById(rootView, id);
      if (tvQrEt == null) {
        break missingId;
      }

      id = R.id.tvQrLayout;
      TextInputLayout tvQrLayout = ViewBindings.findChildViewById(rootView, id);
      if (tvQrLayout == null) {
        break missingId;
      }

      return new ActivityRegistrarProductoBinding((ScrollView) rootView, descripcionProductoEt,
          descripcionProductoLayout, imageProducto, imagenQr, nombreCategoriaSpinner,
          nombreProductoEt, nombreProductoLayout, nombreProveedorSpinner, precioLayout,
          precioProductoEt, progressBar, progressBarLayout, saveProductoButton, stockProductoEt,
          stockProductoLayout, tvQrEt, tvQrLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}