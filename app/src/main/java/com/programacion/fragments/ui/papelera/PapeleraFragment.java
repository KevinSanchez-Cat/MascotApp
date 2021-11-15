package com.programacion.fragments.ui.papelera;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.programacion.fragments.R;
import com.programacion.fragments.modelo.Mascotas;
import com.programacion.fragments.ui.Mascota;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PapeleraFragment extends Fragment {
    private ExpandableListView expandableListView; //ReciclerView
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListNombres;
    private HashMap<String, Mascota> listaContactos;
    private int lastExpandedPosition = -1;
    //expandableListView

    View root;
    EditText ediBuscaNombre;
    ImageButton btnBusca;
    List<Mascotas> elements;
    //Fisebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;

    private static AlertDialog dialog;
    private static Button btnPopCerrar;
    private PapeleraViewModel mViewModel;

    public static PapeleraFragment newInstance() {
        return new PapeleraFragment();
    }


    private void iniciarFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Imagenes");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_papelera, container, false);
        iniciarFirebase();
        ediBuscaNombre = root.findViewById(R.id.editBuscConsultarID);
        btnBusca = root.findViewById(R.id.iBtnBuscarConsultarID);
        expandableListView = root.findViewById(R.id.expandableListView2);


        btnBusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ediBuscaNombre.getText().toString();
                if (id.isEmpty()) {
                    getContactos();
                    expandableListNombres = new ArrayList<>(listaCs.keySet());
                    expandableListAdapter = new CustomExpandableListAdapterPapelera(getContext(),
                            expandableListNombres, listaCs, inflater, getActivity(), container);

                    expandableListView.setAdapter(expandableListAdapter);
                    mostarToast("Actualizando...",0,true);
                } else {
                    Query queryMascota = databaseReference.child("Mascotas/Inactivos").orderByChild("nombre").equalTo(id);
                    queryMascota.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                listaCs.clear();
                                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                    Mascotas mascota = objSnapshot.getValue(Mascotas.class);
                                    Mascota m = new Mascota(mascota.getIdUI(), mascota.getNombre(),
                                            mascota.getEspecie(), mascota.getFechaNacimiento(), mascota.getImagen(), mascota.getEstatus());
                                    listaCs.put(mascota.getIdUI(), m);
                                }

                                expandableListNombres = new ArrayList<>(listaCs.keySet());
                                expandableListAdapter = new CustomExpandableListAdapterPapelera(getContext(),
                                        expandableListNombres, listaCs, inflater, getActivity(), container);
                                expandableListView.setAdapter(expandableListAdapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        databaseReference.child("Mascotas/Inactivos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCs.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Mascotas mascota = objSnapshot.getValue(Mascotas.class);
                    Mascota m = new Mascota(mascota.getIdUI(), mascota.getNombre(),
                            mascota.getEspecie(), mascota.getFechaNacimiento(), mascota.getImagen(), mascota.getEstatus());
                    listaCs.put(mascota.getIdUI(), m);
                    expandableListNombres = new ArrayList<>(listaCs.keySet());
                    expandableListAdapter = new CustomExpandableListAdapterPapelera(getContext(),
                            expandableListNombres, listaCs, inflater, getActivity(), container);

                    expandableListView.setAdapter(expandableListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mostarToast("Recuperando datos...",0,true);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

       expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int positio, long id) {

            }
        });
        return root;
    }

    HashMap<String, Mascota> listaCs = new HashMap<>();

    private void getContactos() {


        databaseReference.child("Mascotas/Inactivos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCs.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Mascotas mascota = objSnapshot.getValue(Mascotas.class);
                    Mascota m = new Mascota(mascota.getIdUI(), mascota.getNombre(),
                            mascota.getEspecie(), mascota.getFechaNacimiento(), mascota.getImagen(), mascota.getEstatus());
                    listaCs.put(mascota.getIdUI(), m);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PapeleraViewModel.class);
        // TODO: Use the ViewModel
    }


    public void mostarToast(String txt, int estatus, boolean corto) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = null;

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