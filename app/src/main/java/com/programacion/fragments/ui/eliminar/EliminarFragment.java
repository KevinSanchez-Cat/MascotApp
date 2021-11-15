package com.programacion.fragments.ui.eliminar;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.File;
import java.util.UUID;

public class EliminarFragment extends Fragment implements View.OnClickListener {

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
    private Button btnEliminar;
    private Button btnCancelar;
    private ImageView imgFoto;

    private String img;
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

    private Uri photoURI;
    public String sexo, veterinario, especie, raza;

    LayoutInflater inflaterLayout;

    Mascotas mascota;
    //Fisebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private EliminarViewModel mViewModel;

    public static EliminarFragment newInstance() {
        return new EliminarFragment();
    }

    View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_eliminar, container, false);
        init(root);
        this.inflaterLayout=inflater;
        iniciarFirebase();
        return root;
    }

    private void iniciarFirebase() {
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
                    mostarToast(e.getCause() + "",3,false);
                }
            });
        }
    }

    private void init(View root) {
        editBuscaID = (com.google.android.material.textfield.TextInputEditText) root.findViewById(R.id.editBuscaElimID);
        iBtnBuscarID = (ImageButton) root.findViewById(R.id.iBtnBuscarElimID);
        textID = (TextView) root.findViewById(R.id.textElimID);
        textNomMas = (TextView) root.findViewById(R.id.textElimNomMas);
        textNomProp = (TextView) root.findViewById(R.id.textElimNomProp);
        textDireccion = (TextView) root.findViewById(R.id.textElimDirec);
        textTelefono = (TextView) root.findViewById(R.id.textElimTel);
        textEmail = (TextView) root.findViewById(R.id.textElimEmail);
        textEspecie = (TextView) root.findViewById(R.id.textElimEsp);
        textNac = (TextView) root.findViewById(R.id.textElimNac);
        textSexo = (TextView) root.findViewById(R.id.textElimSex);
        textRaza = (TextView) root.findViewById(R.id.textElimRaz);
        textColor = (TextView) root.findViewById(R.id.textElimCol);
        textPart = (TextView) root.findViewById(R.id.textElimPart);
        textVeterinario = (TextView) root.findViewById(R.id.textElimVet);
        btnEliminar = (Button) root.findViewById(R.id.btnElimEliminar);

        imgFoto = root.findViewById(R.id.imgElimFoto);
        iBtnBuscarID.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EliminarViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iBtnBuscarElimID:
                String ids = editBuscaID.getText().toString();
                if (ids.isEmpty()) {
                     mostarToast("LLena el campo ID", 2,true);
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
                } else {
                    String idse = editBuscaID.getText().toString();
                    Query queryMascota = databaseReference.child("Mascotas/Activos").orderByChild("claveMascota").equalTo(idse);
                    queryMascota.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                    mascota = objSnapshot.getValue(Mascotas.class);
                                    id = mascota.getIdUI();
                                    nom_mas = mascota.getNombre();
                                    nom_pro_ = mascota.getNombrePropietario();
                                    nom_vet = mascota.getVeterinario();
                                    color = mascota.getColor();
                                    especie = mascota.getEspecie();
                                    raza = mascota.getRaza();
                                    sexo = mascota.getSexo();
                                    dir = mascota.getDireccion();
                                    tel = mascota.getTelefono();
                                    parti = mascota.getParticularidades();
                                    email = mascota.getEmail();
                                    nac = mascota.getFechaNacimiento();

                                    textID.setText(mascota.getClaveMascota());
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
                                    img = mascota.getImagen();
                                    imgA = mascota.getImagen();

                                }
                                try {
                                    ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                    if (networkInfo != null && networkInfo.isConnected()) {
                                        cargaImagen(imgFoto);
                                    } else {
                                        mostarToast("En estos momento no se puede observar la foto\nya que usted se encuentra sin conexión",4,false);
                                    }
                                }catch (Exception e){

                                }

                            } else {
                                Query queryMascota = databaseReference.child("Mascotas/Inactivos").orderByChild("claveMascota").equalTo(idse);
                                queryMascota.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                                mascota = objSnapshot.getValue(Mascotas.class);
                                                id = mascota.getIdUI();
                                                nom_mas = mascota.getNombre();
                                                nom_pro_ = mascota.getNombrePropietario();
                                                nom_vet = mascota.getVeterinario();
                                                color = mascota.getColor();
                                                especie = mascota.getEspecie();
                                                raza = mascota.getRaza();
                                                sexo = mascota.getSexo();
                                                dir = mascota.getDireccion();
                                                tel = mascota.getTelefono();
                                                parti = mascota.getParticularidades();
                                                email = mascota.getEmail();
                                                nac = mascota.getFechaNacimiento();

                                                textID.setText(mascota.getClaveMascota());
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
                                                img = mascota.getImagen();
                                                imgA = mascota.getImagen();
                                                try {
                                                    ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                                    if (networkInfo != null && networkInfo.isConnected()) {
                                                        cargaImagen(imgFoto);
                                                    } else {
                                                        mostarToast("En estos momento no se puede observar la foto\nya que usted se encuentra sin conexión",4,false);
                                                    }
                                                }catch (Exception e){

                                                }


                                            }
                                        } else {
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

                                                id = "";
                                                nom_mas = "";
                                                nom_pro_ = "";
                                                nom_vet = "";
                                                color = "";
                                                especie = "";
                                                raza = "";
                                                sexo = "";
                                                dir = "";
                                                tel = "";
                                                parti = "";
                                                email = "";
                                                nac = "";
                                                img = "";
                                                imgA = "";
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

            case R.id.btnElimEliminar:
                if(mascota==null){
                    mostarToast("Ingrese un ID válido",2,true);
                }else{
                    ids = mascota.getClaveMascota();
                    String textoID = "ID: " + ids + "\n" +
                            "NOMBRE DE LA MASCOTA: " + nom_mas + "\n" +
                            "NOMBRE DEL DUEÑO: " + nom_pro_ + "\n" +
                            "NOMBRE DEL VETERINARIO: " + nom_vet + "\n" +
                            "ESPECIE: " + especie + "\n" +
                            "RAZA: " + raza + "\n" +
                            "SEXO: " + sexo + "\n" +
                            "COLOR: " + color + "\n" +
                            "DIRECCION: " + dir + "\n" +
                            "TELEFONO: " + tel + "\n" +
                            "EMAIL: " + email + "\n" +
                            "FECHA DEL NACIMIENTO: " + nac + "\n";

                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    //View dialogView=LayoutInflater.from(getContext()).inflate(R.layout.dialog_eliminar,null);
                    View dialogView = inflater.inflate(R.layout.dialog_eliminar, null);

                    dialogo.setView(dialogView);
                    //dialogo.setCancelable(false);
                    AlertDialog dialog_elimAlertDialog = dialogo.create();

                    dialog_elimAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView txtInfo = dialogView.findViewById(R.id.textElemento);
                    txtInfo.setText(textoID);

                    dialogView.findViewById(R.id.btnDiagSi).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mascota.getEstatus().equals("ACTIVO")) {
                                Task<Void> task = databaseReference.child("Mascotas/Activos").child(mascota.getIdUI()).removeValue();
                            } else {
                                Task<Void> task = databaseReference.child("Mascotas/Inactivos").child(mascota.getIdUI()).removeValue();
                            }

                            mostarToast("Registro eliminado exitosamente",1,true);

                            dialog_elimAlertDialog.dismiss();
                        }
                    });
                    dialogView.findViewById(R.id.btnDiagSiPap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Mascotas mascotaNueva = new Mascotas();
                            mascotaNueva.setIdUI(UUID.randomUUID().toString());
                            mascotaNueva.setImagen(mascota.getImagen());
                            mascotaNueva.setClaveMascota(mascota.getClaveMascota());
                            mascotaNueva.setNombre(mascota.getNombre());
                            mascotaNueva.setNombrePropietario(mascota.getNombrePropietario());
                            mascotaNueva.setDireccion(mascota.getDireccion());
                            mascotaNueva.setTelefono(mascota.getTelefono());
                            mascotaNueva.setEmail(mascota.getEmail());
                            mascotaNueva.setEspecie(mascota.getEspecie());
                            mascotaNueva.setFechaNacimiento(mascota.getFechaNacimiento());
                            mascotaNueva.setSexo(mascota.getSexo());
                            mascotaNueva.setRaza(mascota.getRaza());
                            mascotaNueva.setColor(mascota.getColor());
                            mascotaNueva.setParticularidades(mascota.getParticularidades());
                            mascotaNueva.setVeterinario(mascota.getVeterinario());
                            mascotaNueva.setEstatus("INACTIVO");
                            if (mascota.getEstatus().equals("ACTIVO")) {
                                databaseReference.child("Mascotas/Inactivos").child(mascotaNueva.getIdUI()).setValue(mascotaNueva).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Task<Void> task = databaseReference.child("Mascotas/Activos").child(mascota.getIdUI()).removeValue();
                                        mostarToast("Registro enviado a la papelera exitosamente",1,true);
                                    }
                                });
                            } else {
                                mostarToast("El registro ya se encuentra en la papelera",0,true);
                            }
                            dialog_elimAlertDialog.dismiss();
                        }
                    });
                    dialogView.findViewById(R.id.btnDiagNo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mostarToast("Acción cancelada",0,true);
                            dialog_elimAlertDialog.dismiss();
                        }
                    });
                    dialog_elimAlertDialog.show();
                }


                break;
        }
    }


    public void mostarToast(String txt, int estatus, boolean corto) {
        try {
            LayoutInflater inflater = this.inflaterLayout;

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
        }catch (Exception e){

        }

    }
}