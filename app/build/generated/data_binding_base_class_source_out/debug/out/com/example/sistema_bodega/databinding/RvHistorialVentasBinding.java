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

public final class RvHistorialVentasBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView carritoventa;

  @NonNull
  public final TextView fechaventaTv;

  @NonNull
  public final TextView horaventaTv;

  @NonNull
  public final ImageView mMenus;

  @NonNull
  public final TextView nombreClienteTv;

  @NonNull
  public final CardView recCard;

  @NonNull
  public final TextView serieVentaTv;

  @NonNull
  public final TextView tipoPagoTv;

  @NonNull
  public final TextView totalVentaTv;

  @NonNull
  public final TextView tvTotal;

  private RvHistorialVentasBinding(@NonNull CardView rootView, @NonNull ImageView carritoventa,
      @NonNull TextView fechaventaTv, @NonNull TextView horaventaTv, @NonNull ImageView mMenus,
      @NonNull TextView nombreClienteTv, @NonNull CardView recCard, @NonNull TextView serieVentaTv,
      @NonNull TextView tipoPagoTv, @NonNull TextView totalVentaTv, @NonNull TextView tvTotal) {
    this.rootView = rootView;
    this.carritoventa = carritoventa;
    this.fechaventaTv = fechaventaTv;
    this.horaventaTv = horaventaTv;
    this.mMenus = mMenus;
    this.nombreClienteTv = nombreClienteTv;
    this.recCard = recCard;
    this.serieVentaTv = serieVentaTv;
    this.tipoPagoTv = tipoPagoTv;
    this.totalVentaTv = totalVentaTv;
    this.tvTotal = tvTotal;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static RvHistorialVentasBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RvHistorialVentasBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.rv_historial_ventas, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RvHistorialVentasBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.carritoventa;
      ImageView carritoventa = ViewBindings.findChildViewById(rootView, id);
      if (carritoventa == null) {
        break missingId;
      }

      id = R.id.fechaventa_tv;
      TextView fechaventaTv = ViewBindings.findChildViewById(rootView, id);
      if (fechaventaTv == null) {
        break missingId;
      }

      id = R.id.horaventa_tv;
      TextView horaventaTv = ViewBindings.findChildViewById(rootView, id);
      if (horaventaTv == null) {
        break missingId;
      }

      id = R.id.mMenus;
      ImageView mMenus = ViewBindings.findChildViewById(rootView, id);
      if (mMenus == null) {
        break missingId;
      }

      id = R.id.nombreCliente_tv;
      TextView nombreClienteTv = ViewBindings.findChildViewById(rootView, id);
      if (nombreClienteTv == null) {
        break missingId;
      }

      CardView recCard = (CardView) rootView;

      id = R.id.serieVenta_tv;
      TextView serieVentaTv = ViewBindings.findChildViewById(rootView, id);
      if (serieVentaTv == null) {
        break missingId;
      }

      id = R.id.tipoPago_tv;
      TextView tipoPagoTv = ViewBindings.findChildViewById(rootView, id);
      if (tipoPagoTv == null) {
        break missingId;
      }

      id = R.id.totalVenta_tv;
      TextView totalVentaTv = ViewBindings.findChildViewById(rootView, id);
      if (totalVentaTv == null) {
        break missingId;
      }

      id = R.id.tv_total;
      TextView tvTotal = ViewBindings.findChildViewById(rootView, id);
      if (tvTotal == null) {
        break missingId;
      }

      return new RvHistorialVentasBinding((CardView) rootView, carritoventa, fechaventaTv,
          horaventaTv, mMenus, nombreClienteTv, recCard, serieVentaTv, tipoPagoTv, totalVentaTv,
          tvTotal);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}