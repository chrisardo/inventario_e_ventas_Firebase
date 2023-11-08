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

public final class ProveedorRecyclerBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView ProveedorImage;

  @NonNull
  public final ImageView idEditar;

  @NonNull
  public final ImageView idLlamar;

  @NonNull
  public final TextView nombreProveedorItem;

  @NonNull
  public final CardView recCard;

  private ProveedorRecyclerBinding(@NonNull CardView rootView, @NonNull ImageView ProveedorImage,
      @NonNull ImageView idEditar, @NonNull ImageView idLlamar,
      @NonNull TextView nombreProveedorItem, @NonNull CardView recCard) {
    this.rootView = rootView;
    this.ProveedorImage = ProveedorImage;
    this.idEditar = idEditar;
    this.idLlamar = idLlamar;
    this.nombreProveedorItem = nombreProveedorItem;
    this.recCard = recCard;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ProveedorRecyclerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ProveedorRecyclerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.proveedor_recycler, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ProveedorRecyclerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ProveedorImage;
      ImageView ProveedorImage = ViewBindings.findChildViewById(rootView, id);
      if (ProveedorImage == null) {
        break missingId;
      }

      id = R.id.id_editar;
      ImageView idEditar = ViewBindings.findChildViewById(rootView, id);
      if (idEditar == null) {
        break missingId;
      }

      id = R.id.id_llamar;
      ImageView idLlamar = ViewBindings.findChildViewById(rootView, id);
      if (idLlamar == null) {
        break missingId;
      }

      id = R.id.nombreProveedor_item;
      TextView nombreProveedorItem = ViewBindings.findChildViewById(rootView, id);
      if (nombreProveedorItem == null) {
        break missingId;
      }

      CardView recCard = (CardView) rootView;

      return new ProveedorRecyclerBinding((CardView) rootView, ProveedorImage, idEditar, idLlamar,
          nombreProveedorItem, recCard);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}