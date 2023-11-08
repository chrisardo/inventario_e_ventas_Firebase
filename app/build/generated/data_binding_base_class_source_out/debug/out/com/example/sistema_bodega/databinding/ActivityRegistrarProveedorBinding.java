// Generated by view binder compiler. Do not edit!
package com.example.sistema_bodega.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

public final class ActivityRegistrarProveedorBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final ImageView ImageProveedor;

  @NonNull
  public final TextInputLayout celularLayout;

  @NonNull
  public final TextInputEditText celularProveedorEt;

  @NonNull
  public final TextInputLayout correoLayout;

  @NonNull
  public final TextInputEditText correoProveedorEt;

  @NonNull
  public final TextInputLayout nombreLayout;

  @NonNull
  public final TextInputEditText nombreProveedorEt;

  @NonNull
  public final RelativeLayout progressBar;

  @NonNull
  public final ProgressBar progressBarLayout;

  @NonNull
  public final AppCompatButton saveProveedorButton;

  private ActivityRegistrarProveedorBinding(@NonNull ScrollView rootView,
      @NonNull ImageView ImageProveedor, @NonNull TextInputLayout celularLayout,
      @NonNull TextInputEditText celularProveedorEt, @NonNull TextInputLayout correoLayout,
      @NonNull TextInputEditText correoProveedorEt, @NonNull TextInputLayout nombreLayout,
      @NonNull TextInputEditText nombreProveedorEt, @NonNull RelativeLayout progressBar,
      @NonNull ProgressBar progressBarLayout, @NonNull AppCompatButton saveProveedorButton) {
    this.rootView = rootView;
    this.ImageProveedor = ImageProveedor;
    this.celularLayout = celularLayout;
    this.celularProveedorEt = celularProveedorEt;
    this.correoLayout = correoLayout;
    this.correoProveedorEt = correoProveedorEt;
    this.nombreLayout = nombreLayout;
    this.nombreProveedorEt = nombreProveedorEt;
    this.progressBar = progressBar;
    this.progressBarLayout = progressBarLayout;
    this.saveProveedorButton = saveProveedorButton;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRegistrarProveedorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRegistrarProveedorBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_registrar_proveedor, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRegistrarProveedorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ImageProveedor;
      ImageView ImageProveedor = ViewBindings.findChildViewById(rootView, id);
      if (ImageProveedor == null) {
        break missingId;
      }

      id = R.id.celularLayout;
      TextInputLayout celularLayout = ViewBindings.findChildViewById(rootView, id);
      if (celularLayout == null) {
        break missingId;
      }

      id = R.id.celularProveedor_et;
      TextInputEditText celularProveedorEt = ViewBindings.findChildViewById(rootView, id);
      if (celularProveedorEt == null) {
        break missingId;
      }

      id = R.id.correoLayout;
      TextInputLayout correoLayout = ViewBindings.findChildViewById(rootView, id);
      if (correoLayout == null) {
        break missingId;
      }

      id = R.id.correoProveedor_et;
      TextInputEditText correoProveedorEt = ViewBindings.findChildViewById(rootView, id);
      if (correoProveedorEt == null) {
        break missingId;
      }

      id = R.id.nombreLayout;
      TextInputLayout nombreLayout = ViewBindings.findChildViewById(rootView, id);
      if (nombreLayout == null) {
        break missingId;
      }

      id = R.id.nombreProveedor_et;
      TextInputEditText nombreProveedorEt = ViewBindings.findChildViewById(rootView, id);
      if (nombreProveedorEt == null) {
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

      id = R.id.saveProveedor_button;
      AppCompatButton saveProveedorButton = ViewBindings.findChildViewById(rootView, id);
      if (saveProveedorButton == null) {
        break missingId;
      }

      return new ActivityRegistrarProveedorBinding((ScrollView) rootView, ImageProveedor,
          celularLayout, celularProveedorEt, correoLayout, correoProveedorEt, nombreLayout,
          nombreProveedorEt, progressBar, progressBarLayout, saveProveedorButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}