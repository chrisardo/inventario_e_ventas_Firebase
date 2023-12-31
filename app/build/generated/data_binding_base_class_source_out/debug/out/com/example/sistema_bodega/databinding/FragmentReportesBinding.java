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
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistema_bodega.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentReportesBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageView ClienteImagenIv;

  @NonNull
  public final ImageView analiticasIv;

  @NonNull
  public final CardView analiticasVentasCv;

  @NonNull
  public final CardView categoriasCv;

  @NonNull
  public final ImageView categoriasIv;

  @NonNull
  public final CardView clientesCv;

  @NonNull
  public final TextView clientesTv;

  @NonNull
  public final CardView proveedorCv;

  @NonNull
  public final ImageView proveedorIv;

  private FragmentReportesBinding(@NonNull RelativeLayout rootView,
      @NonNull ImageView ClienteImagenIv, @NonNull ImageView analiticasIv,
      @NonNull CardView analiticasVentasCv, @NonNull CardView categoriasCv,
      @NonNull ImageView categoriasIv, @NonNull CardView clientesCv, @NonNull TextView clientesTv,
      @NonNull CardView proveedorCv, @NonNull ImageView proveedorIv) {
    this.rootView = rootView;
    this.ClienteImagenIv = ClienteImagenIv;
    this.analiticasIv = analiticasIv;
    this.analiticasVentasCv = analiticasVentasCv;
    this.categoriasCv = categoriasCv;
    this.categoriasIv = categoriasIv;
    this.clientesCv = clientesCv;
    this.clientesTv = clientesTv;
    this.proveedorCv = proveedorCv;
    this.proveedorIv = proveedorIv;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentReportesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentReportesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_reportes, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentReportesBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ClienteImagen_iv;
      ImageView ClienteImagenIv = ViewBindings.findChildViewById(rootView, id);
      if (ClienteImagenIv == null) {
        break missingId;
      }

      id = R.id.analiticas_iv;
      ImageView analiticasIv = ViewBindings.findChildViewById(rootView, id);
      if (analiticasIv == null) {
        break missingId;
      }

      id = R.id.analiticasVentas_cv;
      CardView analiticasVentasCv = ViewBindings.findChildViewById(rootView, id);
      if (analiticasVentasCv == null) {
        break missingId;
      }

      id = R.id.categorias_cv;
      CardView categoriasCv = ViewBindings.findChildViewById(rootView, id);
      if (categoriasCv == null) {
        break missingId;
      }

      id = R.id.categorias_iv;
      ImageView categoriasIv = ViewBindings.findChildViewById(rootView, id);
      if (categoriasIv == null) {
        break missingId;
      }

      id = R.id.clientes_cv;
      CardView clientesCv = ViewBindings.findChildViewById(rootView, id);
      if (clientesCv == null) {
        break missingId;
      }

      id = R.id.clientes_tv;
      TextView clientesTv = ViewBindings.findChildViewById(rootView, id);
      if (clientesTv == null) {
        break missingId;
      }

      id = R.id.proveedor_cv;
      CardView proveedorCv = ViewBindings.findChildViewById(rootView, id);
      if (proveedorCv == null) {
        break missingId;
      }

      id = R.id.proveedor_iv;
      ImageView proveedorIv = ViewBindings.findChildViewById(rootView, id);
      if (proveedorIv == null) {
        break missingId;
      }

      return new FragmentReportesBinding((RelativeLayout) rootView, ClienteImagenIv, analiticasIv,
          analiticasVentasCv, categoriasCv, categoriasIv, clientesCv, clientesTv, proveedorCv,
          proveedorIv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
