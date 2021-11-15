package com.programacion.fragments.ui.papelera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionScene;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomExpandableListAdapterPapelera extends BaseExpandableListAdapter {

    private String img;
    private int id;
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
    ViewGroup root;
    private Uri photoURI;
    public String sexo, veterinario, especie, raza;

    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;


    private Context context;
    private List<String> listTitulo;
    private HashMap<String, Mascota> expandableListDetalles;
    private Activity activity;

    LayoutInflater layoutInflater;

    public CustomExpandableListAdapterPapelera(Context context,
                                               List<String> listTitulo,
                                               HashMap<String, Mascota> expandableListDetalles, LayoutInflater inflater, Activity activity, ViewGroup root) {
        this.root = root;
        this.context = context;
        this.layoutInflater = inflater;
        this.listTitulo = listTitulo;
        this.expandableListDetalles = expandableListDetalles;
        this.activity = activity;
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Imagenes");
    }


    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Mascota mascota = (Mascota) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_exp_papelera, null);
        }

        //  CircleImageView circleImageView = convertView.findViewById(R.id.circleIMG);

        // Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), mascota.getImagen());
        // circleImageView.setImageBitmap(bitmap);

        LinearLayout layoutRestaurar = convertView.findViewById(R.id.ilistPapRest);
        LinearLayout layoutEliminar = convertView.findViewById(R.id.ilistPapEliDef);
        LinearLayout layouInfo = convertView.findViewById(R.id.ilistPapInfo);


        layoutRestaurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ids = String.valueOf(mascota.getId());
                String textoID = "";

                android.app.AlertDialog.Builder dialogo = new android.app.AlertDialog.Builder(context);
                //View dialogView=LayoutInflater.from(getContext()).inflate(R.layout.dialog_eliminar,null);
                View dialogView = layoutInflater.inflate(R.layout.dialog_restaurar, null);

                dialogo.setView(dialogView);
                //dialogo.setCancelable(false);
                AlertDialog dialog_elimAlertDialog = dialogo.create();

                dialog_elimAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogView.findViewById(R.id.btnSiRes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Query queryMascota = databaseReference.child("Mascotas/Inactivos").orderByChild("idUI").equalTo(ids);
                        queryMascota.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                        Mascotas mascotaOring = objSnapshot.getValue(Mascotas.class);
                                        Mascotas mascotaNueva=new Mascotas();

                                        mascotaNueva.setIdUI(UUID.randomUUID().toString());
                                        mascotaNueva.setImagen(mascotaOring.getImagen());
                                        mascotaNueva.setClaveMascota(mascotaOring.getClaveMascota());
                                        mascotaNueva.setNombre(mascotaOring.getNombre());
                                        mascotaNueva.setNombrePropietario(mascotaOring.getNombrePropietario());
                                        mascotaNueva.setDireccion(mascotaOring.getDireccion());
                                        mascotaNueva.setTelefono(mascotaOring.getTelefono());
                                        mascotaNueva.setEmail(mascotaOring.getEmail());
                                        mascotaNueva.setEspecie(mascotaOring.getEspecie());
                                        mascotaNueva.setFechaNacimiento(mascotaOring.getFechaNacimiento());
                                        mascotaNueva.setSexo(mascotaOring.getSexo());
                                        mascotaNueva.setRaza(mascotaOring.getRaza());
                                        mascotaNueva.setColor(mascotaOring.getColor());
                                        mascotaNueva.setParticularidades(mascotaOring.getParticularidades());
                                        mascotaNueva.setVeterinario(mascotaOring.getVeterinario());
                                        mascotaNueva.setEstatus("ACTIVO");
                                        try {
                                            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                                            if (networkInfo != null && networkInfo.isConnected()) {
                                                databaseReference.child("Mascotas/Activos").child(mascotaNueva.getIdUI()).setValue(mascotaNueva).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mostarToast("Registro restaurado exitosamente",1,true);
                                                        databaseReference.child("Mascotas/Inactivos").child(mascota.getId()).removeValue();
                                                    }
                                                });
                                            }else{
                                                databaseReference.child("Mascotas/Activos").child(mascota.getId()).setValue(mascotaNueva).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mostarToast("Registro restaurado exitosamente",1,true);
                                                        databaseReference.child("Mascotas/Inactivos").child(mascota.getId()).removeValue();
                                                    }
                                                });
                                            }
                                        }catch (Exception e){

                                        }

                                    }
                                }else{
                                    System.out.println("Error?");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        dialog_elimAlertDialog.dismiss();

                    }
                });

                dialogView.findViewById(R.id.btnNoRes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mostarToast("Accion cancelada",0,true);
                        dialog_elimAlertDialog.dismiss();
                    }
                });
                dialog_elimAlertDialog.show();
            }
        });

        layoutEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ids = "";
                ids = String.valueOf(mascota.getId());
                String textoID = "";

                android.app.AlertDialog.Builder dialogo = new android.app.AlertDialog.Builder(context);
                //View dialogView=LayoutInflater.from(getContext()).inflate(R.layout.dialog_eliminar,null);
                View dialogView = layoutInflater.inflate(R.layout.dialog_eliminar_def, null);

                dialogo.setView(dialogView);
                //dialogo.setCancelable(false);
                AlertDialog dialog_elimAlertDialog = dialogo.create();

                dialog_elimAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogView.findViewById(R.id.btnSiDef).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Task<Void> task = databaseReference.child("Mascotas/Inactivos").child(mascota.getId()).removeValue();
                        mostarToast("Registro eliminado exitosamente",1,true);

                        dialog_elimAlertDialog.dismiss();
                    }
                });

                dialogView.findViewById(R.id.btnNoDef).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mostarToast("Accion cancelada",0,true);
                        dialog_elimAlertDialog.dismiss();
                    }
                });
                dialog_elimAlertDialog.show();
            }
        });

        layouInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAboutDialog(mascota);
            }
        });


        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        convertView.startAnimation(animation);

        return convertView;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        Mascota mascota = (Mascota) getChild(groupPosition, 0);

        if (convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.list_group, null);

        }

        TextView txtNombre = convertView.findViewById(R.id.textItemNom);
        TextView txtEspecie = convertView.findViewById(R.id.textItemEspecie);
        TextView txtedad = convertView.findViewById(R.id.textItemEdad);
        CircleImageView circleImageView = convertView.findViewById(R.id.circleIMG);

        String edad = "";
        String naci = mascota.getEdad().toString();
        String dias[] = naci.toString().split("/");

        int dia = Integer.parseInt(dias[0]);
        int mes = Integer.parseInt(dias[1]);
        int anio = Integer.parseInt(dias[2]);

        Calendar c = Calendar.getInstance();
        int anioC = c.get(Calendar.YEAR);
        int mesC = c.get(Calendar.MONTH);
        int diaC = c.get(Calendar.DAY_OF_MONTH);

        int anios = anioC - anio;
        int meses = mesC - mes;
        int semanas = (diaC - dia) / 7;
        if ((anios) >= 1) {
            if (anios == 1) {
                edad = anios + " año";
            } else {
                edad = anios + " años";
            }

        } else {
            if (meses < 1) {
                edad = semanas + " semanas";
            } else if (meses == 1) {
                edad = meses + " mes";
            } else {
                edad = meses + " meses";
            }
        }


        if (activity != null) {
            if (mascota.getImagen().equals("")) {
                circleImageView.setImageResource(R.drawable.perro_1);
            } else {
                storageReference.child(mascota.getImagen()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).into(circleImageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostarToast("Ups! Ha ocurrido un erro al recuperar la imagen\n"+e.getCause() ,3,false);
                    }
                });
            }

        } else {
            circleImageView.setImageResource(R.drawable.perro_1);
        }


        txtedad.setText(edad);
        txtNombre.setText(String.valueOf(mascota.getNombreMascota()).toString().toUpperCase());
        txtEspecie.setText(String.valueOf(mascota.getEspecie()));

        //Animation animation=AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);


        //  Animation animationA = AnimationUtils.loadAnimation(context,(isExpanded)?R.anim.slide:R.anim.fade_transition);
        // convertView.startAnimation(animationA);
        return convertView;
    }

    private int lastposition = -1;

    @Override
    public int getGroupCount() {
        return this.listTitulo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listTitulo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListDetalles.get(this.listTitulo.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void mostarToast(String txt, int estatus, boolean corto) {
        try {LayoutInflater inflater = layoutInflater;

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
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 50);
        if(corto){
            toast.setDuration(Toast.LENGTH_SHORT);}else{
            toast.setDuration(Toast.LENGTH_LONG);}
        toast.setView(layout);
        toast.show();}catch (Exception e){

        }
    }
    Mascotas mascotasObj = null;

    public void createNewAboutDialog(Mascota mascota) {

        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        final View aboutPop = layoutInflater.inflate(R.layout.dialog_mascota, null);
        btnPopCerrar = (Button) aboutPop.findViewById(R.id.btnCerrarDialog);
        TextView txtID = aboutPop.findViewById(R.id.textDetID);
        TextView txtNomMas = aboutPop.findViewById(R.id.textDetNomMas);
        TextView txtNomProp = aboutPop.findViewById(R.id.textDetNomProp);
        TextView txtNomVet = aboutPop.findViewById(R.id.textDetVet);
        TextView txtDir = aboutPop.findViewById(R.id.textDetDirec);
        TextView txtTel = aboutPop.findViewById(R.id.textDetTel);
        TextView txtEmail = aboutPop.findViewById(R.id.textDetEmail);
        TextView txtEspecie = aboutPop.findViewById(R.id.textDetEsp);
        TextView txtNac = aboutPop.findViewById(R.id.textDetNac);
        TextView txtSexo = aboutPop.findViewById(R.id.textDetSex);
        TextView txtRaza = aboutPop.findViewById(R.id.textDetRaz);
        TextView txtColor = aboutPop.findViewById(R.id.textDetCol);
        TextView txtParticularidades = aboutPop.findViewById(R.id.textDetPart);
        ImageView imageView = aboutPop.findViewById(R.id.imgDetalleFoto);

        Query queryMascota = databaseReference.child("Mascotas/Inactivos").orderByChild("idUI").equalTo(mascota.getId());
        queryMascota.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                        mascotasObj = objSnapshot.getValue(Mascotas.class);
                        txtID.setText(mascotasObj.getClaveMascota());
                        txtNomMas.setText(mascotasObj.getNombre());
                        txtNomProp.setText(mascotasObj.getNombrePropietario());
                        txtNomVet.setText(mascotasObj.getVeterinario());
                        txtDir.setText(mascotasObj.getDireccion());
                        txtTel.setText(mascotasObj.getTelefono());
                        txtEmail.setText(mascotasObj.getEmail());
                        txtEspecie.setText(mascotasObj.getEspecie());
                        txtNac.setText(mascotasObj.getFechaNacimiento());
                        txtSexo.setText(mascotasObj.getSexo());
                        txtRaza.setText(mascotasObj.getRaza());
                        txtColor.setText(mascotasObj.getColor());
                        txtParticularidades.setText(mascotasObj.getParticularidades());

                        cargaImagen(imageView, mascota.getImagen());


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        dialogBuilder.setView(aboutPop);
        dialog = dialogBuilder.create();
        dialog.show();


        btnPopCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //cerrar
    }

    private androidx.appcompat.app.AlertDialog dialog;
    private Button btnPopCerrar;

    private void cargaImagen(ImageView ivFoto, String img) {
        storageReference.child(img).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(ivFoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mostarToast("Ups! Ha ocurrido un erro al recuperar la imagen\n"+e.getCause() ,3,false);
            }
        });

    }
}
