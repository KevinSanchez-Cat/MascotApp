package com.programacion.fragments.ui.busqueda;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class BusquedaFragment extends Fragment implements View.OnClickListener {


    private com.google.android.material.textfield.TextInputEditText editBuscaID;
    private ImageButton iBtnBuscarID;
    private TextView textID;
    private TextView textNomMas;
    private TextView textNomProp;
    private TextView textDireccion;
    private TextView textTelefono;
    private TextView textEmail;
    private TextView textEspecie;
    private TextView textNac;
    private TextView textSexo;
    private TextView textRaza;
    private TextView textColor;
    private TextView textPart;
    private TextView textVeterinario;
    private TextView textEstatus;
    private ImageView imgFoto;
    private ImageView imgEstatus;

    private String id;
    private String nom_mas;
    private String nom_pro_;
    private String nom_vet;
    private String dir;
    private String tel;
    private String email;
    private String nac;
    private String color;
    private String parti;
    private String imgA;
    private String estatus;

    public String sexo, especie, raza;

    private Uri photoURI;

    //Fisebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;

    Mascotas mascota;
    View root;
    private BusquedaViewModel mViewModel;

    public static BusquedaFragment newInstance() {
        return new BusquedaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_busqueda, container, false);
        init(root);
        iniciarFirebase();
        return root;
    }

    private void iniciarFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Imagenes");
    }

    private void cargaImagen(ImageView ivFoto) {

        if (getActivity() != null) {
            storageReference.child(mascota.getImagen()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getContext()).load(uri).into(ivFoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mostarToast( e.getCause() + "",3,false);
                }
            });
        }
    }


    private void init(View root) {
        editBuscaID = (com.google.android.material.textfield.TextInputEditText) root.findViewById(R.id.editSearchID);
        iBtnBuscarID = (ImageButton) root.findViewById(R.id.iBtnSearchID);
        textID = (TextView) root.findViewById(R.id.textSearchID);
        textNomMas = (TextView) root.findViewById(R.id.textSearchNomMas);
        textNomProp = (TextView) root.findViewById(R.id.textSearchNomProp);
        textDireccion = (TextView) root.findViewById(R.id.textSearchDirec);
        textTelefono = (TextView) root.findViewById(R.id.textSearchTel);
        textEmail = (TextView) root.findViewById(R.id.textSearchEmail);
        textEspecie = (TextView) root.findViewById(R.id.textSearchEsp);
        textNac = (TextView) root.findViewById(R.id.textSearchNac);
        textSexo = (TextView) root.findViewById(R.id.textSearchSex);
        textRaza = (TextView) root.findViewById(R.id.textSearchRaz);
        textColor = (TextView) root.findViewById(R.id.textSearchCol);
        textPart = (TextView) root.findViewById(R.id.textSearchPart);
        textEstatus = (TextView) root.findViewById(R.id.textSearchEstado);
        imgEstatus = root.findViewById(R.id.imgSearchEstado);
        textVeterinario = (TextView) root.findViewById(R.id.textSearchVet);
        imgFoto = root.findViewById(R.id.imgSearchFoto);
        iBtnBuscarID.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BusquedaViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iBtnSearchID:
                String ids = editBuscaID.getText().toString();
                if (ids.isEmpty()) {
                    mostarToast("Llena el campo del nombre",2,true);
                    textID.setText("id");
                    textNomMas.setText("nombre de la mascota");
                    textNomProp.setText("nombre del propietario");
                    textColor.setText("color");
                    textEspecie.setText("especie");
                    textRaza.setText("raza");
                    textSexo.setText("sexo");
                    textVeterinario.setText("nombre del veterinario");
                    textDireccion.setText("dirección");
                    textTelefono.setText("telefono");
                    textPart.setText("Particularidades");
                    textEmail.setText("e-mail");
                    textNac.setText("fecha de nacimiento");
                    imgFoto.setImageResource(R.drawable.perro_1);
                    imgEstatus.setImageResource(R.drawable.check);
                    textEstatus.setText("Estatus");
                } else {

                    id = editBuscaID.getText().toString();
                    Query queryMascota = databaseReference.child("Mascotas/Activos").orderByChild("claveMascota").equalTo(id);
                    queryMascota.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                    mascota = objSnapshot.getValue(Mascotas.class);
                                    textID.setText(String.valueOf(mascota.getClaveMascota()));
                                    textNomMas.setText(mascota.getNombre());
                                    textNomProp.setText(mascota.getNombrePropietario());
                                    textColor.setText(mascota.getColor());
                                    textEspecie.setText(mascota.getEspecie());
                                    textRaza.setText(mascota.getRaza());
                                    textSexo.setText(mascota.getSexo());
                                    textVeterinario.setText(mascota.getVeterinario());
                                    textDireccion.setText(mascota.getDireccion());
                                    textTelefono.setText(mascota.getTelefono());
                                    textPart.setText(mascota.getParticularidades());
                                    textEmail.setText(mascota.getEmail());
                                    textNac.setText(mascota.getFechaNacimiento());
                                    textEstatus.setText(mascota.getEstatus().toLowerCase());
                                    if (mascota.getEstatus().equals("ACTIVO")) {
                                        imgEstatus.setImageResource(R.drawable.check_azul);
                                    } else {
                                        imgEstatus.setImageResource(R.drawable.trash);
                                    }
                                }
                                try {
                                    ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                    if (networkInfo != null && networkInfo.isConnected()) {
                                        cargaImagen(imgFoto);
                                    } else {
                                        mostarToast("En estos momento no se puede observar la foto\n" +
                                                "ya que usted se encuentra sin conexión, esto no afecta la modificación",4,true);
                                    }
                                }catch (Exception e){

                                }

                            } else {

                                Query queryMascota = databaseReference.child("Mascotas/Inactivos").orderByChild("claveMascota").equalTo(id);
                                queryMascota.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                                mascota = objSnapshot.getValue(Mascotas.class);
                                                mascota = objSnapshot.getValue(Mascotas.class);
                                                textID.setText(String.valueOf(mascota.getClaveMascota()));
                                                textNomMas.setText(mascota.getNombre());
                                                textNomProp.setText(mascota.getNombrePropietario());
                                                textColor.setText(mascota.getColor());
                                                textEspecie.setText(mascota.getEspecie());
                                                textRaza.setText(mascota.getRaza());
                                                textSexo.setText(mascota.getSexo());
                                                textVeterinario.setText(mascota.getVeterinario());
                                                textDireccion.setText(mascota.getDireccion());
                                                textTelefono.setText(mascota.getTelefono());
                                                textPart.setText(mascota.getParticularidades());
                                                textEmail.setText(mascota.getEmail());
                                                textNac.setText(mascota.getFechaNacimiento());
                                                textEstatus.setText(mascota.getEstatus().toLowerCase());
                                                if (mascota.getEstatus().equals("ACTIVO")) {
                                                    imgEstatus.setImageResource(R.drawable.check_azul);
                                                } else {
                                                    imgEstatus.setImageResource(R.drawable.trash);
                                                }
                                                try {
                                                    ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                                    if (networkInfo != null && networkInfo.isConnected()) {
                                                        cargaImagen(imgFoto);
                                                    } else {
                                                        mostarToast("En estos momento no se puede observar la foto\n" +
                                                                "ya que usted se encuentra sin conexión, esto no afecta la modificación",4,true);
                                                    }
                                                }catch (Exception e){

                                                }

                                            }
                                        }else{

                                            if (getContext() != null) {
                                                mostarToast("No se encontro el ID",5,true);
                                                textID.setText("id");
                                                textNomMas.setText("nombre de la mascota");
                                                textNomProp.setText("nombre del propietario");
                                                textColor.setText("color");
                                                textEspecie.setText("especie");
                                                textRaza.setText("raza");
                                                textSexo.setText("sexo");
                                                textVeterinario.setText("nombre del veterinario");
                                                textDireccion.setText("dirección");
                                                textTelefono.setText("telefono");
                                                textPart.setText("Particularidades");
                                                textEmail.setText("e-mail");
                                                textNac.setText("fecha de nacimiento");
                                                imgFoto.setImageResource(R.drawable.perro_1);
                                                imgEstatus.setImageResource(R.drawable.check_azul);
                                                textEstatus.setText("Estatus");
  }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                break;
        }
    }

    public void mostarToast(String txt, int estatus, boolean corto) {
       try{ LayoutInflater inflater = getLayoutInflater();

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
        toast.show();}catch (Exception e){

       }
    }
}