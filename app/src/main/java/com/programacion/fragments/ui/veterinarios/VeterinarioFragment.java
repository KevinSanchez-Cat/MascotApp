package com.programacion.fragments.ui.veterinarios;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.programacion.fragments.R;

import java.util.ArrayList;

public class VeterinarioFragment extends Fragment {

    private VeterinarioViewModel mViewModel;
    private  Button btnPopCerrar;
    View root;
    private ListView listView;
    private ListAdapter customAdapter;
    com.google.android.material.floatingactionbutton.FloatingActionButton fBtnAgregar;
    public static VeterinarioFragment newInstance() {
        return new VeterinarioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.veterinario_fragment, container, false);
        // Inflate the layout for this fragment
        this.listView = root.findViewById(R.id.listSimpleView);
        this.fBtnAgregar=root.findViewById(R.id.fButtonVeterinariosAdd);
        this.fBtnAgregar=root.findViewById(R.id.fButtonVeterinariosAdd);

        ArrayList<ElementListView> arrayList = new ArrayList<>();
        arrayList.add(new ElementListView("Juan Alvarez", "8776443"));
        arrayList.add(new ElementListView("Jose Alvarez", "8757396"));
        arrayList.add(new ElementListView("Monica Perez", "7342679"));
        arrayList.add(new ElementListView("Ricardo Huertas", "8747634"));
        arrayList.add(new ElementListView("Veronica De La Luz", "84753567"));
        arrayList.add(new ElementListView("Gerardo Fuentes", "245543"));
        arrayList.add(new ElementListView("Sebastian Torres", "6436709"));

        this.customAdapter = new ListAdapter(getActivity(), arrayList);
        this.listView.setAdapter(customAdapter);

        registerForContextMenu(listView);

        fBtnAgregar.setOnClickListener(view -> createDialogAgregar());

        return root;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int id=v.getId();


        MenuInflater inflater2= getActivity().getMenuInflater();
        if (id == R.id.listSimpleView) {
            inflater2.inflate(R.menu.menu_contextual_veterinarios, menu);
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VeterinarioViewModel.class);
        // TODO: Use the ViewModel
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.menu_editar:
                createDialogEditar("1");
                //mostarToast("Editar |"+ info.position+" |  "+item.getTitle(),0,true);
                ElementListView elemento= (ElementListView) this.listView.getItemAtPosition(info.position);
                System.out.println("Editar: "+elemento.getNombre());
                return  true;
            case R.id.menu_consultar:
                createDialogInformacion("1");
                //mostarToast("Consultar |"+ info.position+" |  "+item.getTitle(),0,true);
                ElementListView elemento2= (ElementListView) this.listView.getItemAtPosition(info.position);
                System.out.println("Info: "+elemento2.getNombre());
                return  true;
            case R.id.menu_eliminar:
                createDialogEliminar("1");
                ElementListView elemento3= (ElementListView) this.listView.getItemAtPosition(info.position);
                System.out.println("Eliminar: "+elemento3.getNombre());
                //mostarToast("Eliminar |"+ info.position+" |  "+item.getTitle(),0,true);
                return  true;

        }
        return super.onContextItemSelected(item);
    }
    private androidx.appcompat.app.AlertDialog dialog;

    private void createDialogAgregar() {

        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        final View aboutPop = getLayoutInflater().inflate(R.layout.dialog_agregar_veterinario, null);
        btnPopCerrar = (Button) aboutPop.findViewById(R.id.btnAddCancelar);


        dialogBuilder.setView(aboutPop);
        dialog = dialogBuilder.create();
        dialog.show();
        btnPopCerrar.setOnClickListener(view -> dialog.dismiss());
    }

    private void createDialogEditar(String veterinario) {

        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        final View aboutPop = getLayoutInflater().inflate(R.layout.dialog_editar_veterinario, null);
        btnPopCerrar = (Button) aboutPop.findViewById(R.id.btnEditCancelar);


        dialogBuilder.setView(aboutPop);
        dialog = dialogBuilder.create();
        dialog.show();
        btnPopCerrar.setOnClickListener(view -> dialog.dismiss());
    }
    private void createDialogEliminar(String veterinario) {

        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        final View aboutPop = getLayoutInflater().inflate(R.layout.dialog_eliminar_veterinario, null);
        btnPopCerrar = (Button) aboutPop.findViewById(R.id.btnDelCancelar);


        dialogBuilder.setView(aboutPop);
        dialog = dialogBuilder.create();
        dialog.show();
        btnPopCerrar.setOnClickListener(view -> dialog.dismiss());
    }
    private void createDialogInformacion(String veterinario) {

        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        final View aboutPop = getLayoutInflater().inflate(R.layout.dialog_consultar_veterinario, null);
        btnPopCerrar = (Button) aboutPop.findViewById(R.id.btnReadAceptar);


        dialogBuilder.setView(aboutPop);
        dialog = dialogBuilder.create();
        dialog.show();
        btnPopCerrar.setOnClickListener(view -> dialog.dismiss());
    }

    public void mostarToast(String txt, int estatus, boolean corto) {
        LayoutInflater inflater = getLayoutInflater();

        View layout;

        if(estatus==0){
            //Default
            layout = inflater.inflate(R.layout.custom_toast_info,
                    (ViewGroup) root.findViewById(R.id.layout_base));

        }else if(estatus==1){
            //Success
            layout = inflater.inflate(R.layout.custom_toast_success,
                    (ViewGroup) root.findViewById(R.id.layout_base));

        }else if(estatus==2) {
            //Warning
            layout = inflater.inflate(R.layout.custom_toast_warning,
                    (ViewGroup) root.findViewById(R.id.layout_base));

        }else if(estatus==3){
            //Error
            layout = inflater.inflate(R.layout.custom_toast_error,
                    (ViewGroup) root.findViewById(R.id.layout_base));


        }else if(estatus==4){
            //Falla de red
            layout = inflater.inflate(R.layout.custom_toast_red,
                    (ViewGroup) root.findViewById(R.id.layout_base));


        }else if(estatus==5){
            //Falla de red
            layout = inflater.inflate(R.layout.custom_toast_sin_data,
                    (ViewGroup) root.findViewById(R.id.layout_base));


        }else{
            //Informacion
            layout = inflater.inflate(R.layout.custom_toast_info,
                    (ViewGroup) root.findViewById(R.id.layout_base));
        }

        TextView textView = layout.findViewById(R.id.txtToast);
        textView.setText(txt);
        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 50);
        if(corto){
            toast.setDuration(Toast.LENGTH_SHORT);}else{
            toast.setDuration(Toast.LENGTH_LONG);}
        toast.setView(layout);
        toast.show();
    }
}