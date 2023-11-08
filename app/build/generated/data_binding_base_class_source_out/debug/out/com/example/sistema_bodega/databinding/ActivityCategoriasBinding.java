// Generated by view binder compiler. Do not edit!
package com.example.sistema_bodega.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sistema_bodega.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCategoriasBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final RecyclerView categoryRecyclerView;

  @NonNull
  public final LinearLayout ll;

  @NonNull
  public final LinearLayout progressBar;

  @NonNull
  public final SearchView searchView;

  @NonNull
  public final TextView tvCategoriasEncontrados;

  @NonNull
  public final TextView tvLoadingData;

  @NonNull
  public final TextView tvseparador;

  private ActivityCategoriasBinding(@NonNull RelativeLayout rootView,
      @NonNull RecyclerView categoryRecyclerView, @NonNull LinearLayout ll,
      @NonNull LinearLayout progressBar, @NonNull SearchView searchView,
      @NonNull TextView tvCategoriasEncontrados, @NonNull TextView tvLoadingData,
      @NonNull TextView tvseparador) {
    this.rootView = rootView;
    this.categoryRecyclerView = categoryRecyclerView;
    this.ll = ll;
    this.progressBar = progressBar;
    this.searchView = searchView;
    this.tvCategoriasEncontrados = tvCategoriasEncontrados;
    this.tvLoadingData = tvLoadingData;
    this.tvseparador = tvseparador;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCategoriasBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCategoriasBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_categorias, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCategoriasBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.categoryRecyclerView;
      RecyclerView categoryRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (categoryRecyclerView == null) {
        break missingId;
      }

      id = R.id.ll;
      LinearLayout ll = ViewBindings.findChildViewById(rootView, id);
      if (ll == null) {
        break missingId;
      }

      id = R.id.progressBar;
      LinearLayout progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.searchView;
      SearchView searchView = ViewBindings.findChildViewById(rootView, id);
      if (searchView == null) {
        break missingId;
      }

      id = R.id.tvCategoriasEncontrados;
      TextView tvCategoriasEncontrados = ViewBindings.findChildViewById(rootView, id);
      if (tvCategoriasEncontrados == null) {
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

      return new ActivityCategoriasBinding((RelativeLayout) rootView, categoryRecyclerView, ll,
          progressBar, searchView, tvCategoriasEncontrados, tvLoadingData, tvseparador);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}