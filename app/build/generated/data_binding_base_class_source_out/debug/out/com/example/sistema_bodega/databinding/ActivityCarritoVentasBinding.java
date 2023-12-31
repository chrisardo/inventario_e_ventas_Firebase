// Generated by view binder compiler. Do not edit!
package com.example.sistema_bodega.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistema_bodega.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCarritoVentasBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageView ClienteImagenIv;

  @NonNull
  public final ImageView PagoImagenIv;

  @NonNull
  public final RecyclerView carritoVentaRecyclerView;

  @NonNull
  public final Spinner formaPagoSpinner;

  @NonNull
  public final Spinner nombreClienteSpinner;

  @NonNull
  public final RelativeLayout progressBar;

  @NonNull
  public final CardView recCard;

  @NonNull
  public final TextView totalTv;

  @NonNull
  public final TextView tvLoadingData;

  @NonNull
  public final TextView tvseparador;

  private ActivityCarritoVentasBinding(@NonNull RelativeLayout rootView,
      @NonNull ImageView ClienteImagenIv, @NonNull ImageView PagoImagenIv,
      @NonNull RecyclerView carritoVentaRecyclerView, @NonNull Spinner formaPagoSpinner,
      @NonNull Spinner nombreClienteSpinner, @NonNull RelativeLayout progressBar,
      @NonNull CardView recCard, @NonNull TextView totalTv, @NonNull TextView tvLoadingData,
      @NonNull TextView tvseparador) {
    this.rootView = rootView;
    this.ClienteImagenIv = ClienteImagenIv;
    this.PagoImagenIv = PagoImagenIv;
    this.carritoVentaRecyclerView = carritoVentaRecyclerView;
    this.formaPagoSpinner = formaPagoSpinner;
    this.nombreClienteSpinner = nombreClienteSpinner;
    this.progressBar = progressBar;
    this.recCard = recCard;
    this.totalTv = totalTv;
    this.tvLoadingData = tvLoadingData;
    this.tvseparador = tvseparador;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCarritoVentasBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCarritoVentasBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_carrito_ventas, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCarritoVentasBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ClienteImagen_iv;
      ImageView ClienteImagenIv = ViewBindings.findChildViewById(rootView, id);
      if (ClienteImagenIv == null) {
        break missingId;
      }

      id = R.id.PagoImagen_iv;
      ImageView PagoImagenIv = ViewBindings.findChildViewById(rootView, id);
      if (PagoImagenIv == null) {
        break missingId;
      }

      id = R.id.carritoVentaRecyclerView;
      RecyclerView carritoVentaRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (carritoVentaRecyclerView == null) {
        break missingId;
      }

      id = R.id.formaPago_spinner;
      Spinner formaPagoSpinner = ViewBindings.findChildViewById(rootView, id);
      if (formaPagoSpinner == null) {
        break missingId;
      }

      id = R.id.nombreCliente_spinner;
      Spinner nombreClienteSpinner = ViewBindings.findChildViewById(rootView, id);
      if (nombreClienteSpinner == null) {
        break missingId;
      }

      id = R.id.progressBar;
      RelativeLayout progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.recCard;
      CardView recCard = ViewBindings.findChildViewById(rootView, id);
      if (recCard == null) {
        break missingId;
      }

      id = R.id.total_tv;
      TextView totalTv = ViewBindings.findChildViewById(rootView, id);
      if (totalTv == null) {
        break missingId;
      }

      id = R.id.tvLoadingData;
      TextView tvLoadingData = ViewBindings.findChildViewById(rootView, id);
      if (tvLoadingData == null) {
        break missingId;
      }

      id = R.id.tvseparador;
      TextView tvseparador = ViewBindings.findChildViewById(rootView, id);
      if (tvseparador == null) {
        break missingId;
      }

      return new ActivityCarritoVentasBinding((RelativeLayout) rootView, ClienteImagenIv,
          PagoImagenIv, carritoVentaRecyclerView, formaPagoSpinner, nombreClienteSpinner,
          progressBar, recCard, totalTv, tvLoadingData, tvseparador);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
