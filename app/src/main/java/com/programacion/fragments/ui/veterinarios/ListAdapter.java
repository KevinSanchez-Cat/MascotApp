package com.programacion.fragments.ui.veterinarios;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.programacion.fragments.R;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<ElementListView> {

    private Activity activity;
    private ArrayList<ElementListView> arrayList;

    public ListAdapter(Activity activity, ArrayList<ElementListView> arrayList) {
        super(activity, R.layout.element_lista_cuenta, arrayList);
        this.activity = activity;
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView =
                    LayoutInflater.from(getContext())
                            .inflate(R.layout.element_lista_veterinario, parent, false);
        }

        TextView txtCampo= convertView.findViewById(R.id.txtNombreVete);
        TextView txtInformacion = convertView.findViewById(R.id.txtCedulaVete);

        txtCampo.setText(arrayList.get(position).getNombre());
        txtInformacion.setText("Ced. Prof. : "+arrayList.get(position).getCedula());

        return convertView;
    }


}
