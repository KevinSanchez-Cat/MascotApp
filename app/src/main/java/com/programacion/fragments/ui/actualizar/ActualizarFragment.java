package com.programacion.fragments.ui.actualizar;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.programacion.fragments.R;

public class ActualizarFragment extends Fragment implements   DatePickerDialog.OnDateSetListener{

    private ActualizarViewModel mViewModel;
    private com.google.android.material.textfield.TextInputEditText editNacimiento;
    public static ActualizarFragment newInstance() {
        return new ActualizarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_actualizar, container, false);
        editNacimiento.findViewById(R.id.editActualizaNac);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ActualizarViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onDateSet(DatePicker viewDate, int year, int month, int dayOfMonth) {
        editNacimiento.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
    }
}