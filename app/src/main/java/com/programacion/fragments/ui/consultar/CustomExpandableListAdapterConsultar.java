package com.programacion.fragments.ui.consultar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class CustomExpandableListAdapterConsultar extends BaseExpandableListAdapter {

    ViewGroup root;
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
    Fragment fragment;
    private Uri photoURI;
    public String sexo, veterinario, especie, raza;


    private Context context;
    private List<String> listTitulo;
    private HashMap<String, Mascota> expandableListDetalles;
    private Activity activity;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    public final Calendar c = Calendar.getInstance();

    final int mesC = c.get(Calendar.MONTH);
    final int diaC = c.get(Calendar.DAY_OF_MONTH);
    final int anioC = c.get(Calendar.YEAR);

    LayoutInflater layoutInflater;


    private com.google.android.material.textfield.TextInputEditText editID;
    private com.google.android.material.textfield.TextInputEditText editNomMas;
    private com.google.android.material.textfield.TextInputEditText editNomProp;
    private com.google.android.material.textfield.TextInputEditText editDireccion;
    private com.google.android.material.textfield.TextInputEditText editTelefono;
    private com.google.android.material.textfield.TextInputEditText editEmail;
    private com.google.android.material.textfield.TextInputEditText editNacimiento;
    private com.google.android.material.textfield.TextInputEditText editColor;
    private com.google.android.material.textfield.TextInputEditText editParticulares;

    private ImageButton btnCalendar;
    private ImageView imgFoto;
    private Spinner spnEspecie;
    private AutoCompleteTextView spnRaza;
    private Spinner spnSexo;
    private Spinner spnVeterinario;
    private Button btnLimpiar;
    private Button btnEditar;
    private Button btnCancelar;


    private static final String CERO = "0";
    private static final String BARRA = "/";

    private boolean bndCal, bndMasc;

    //Atributos necesarios
    private DatePickerDialog datePicker;
    private Calendar cal;
    private static int anio, mes, dia;

    private static Mascotas mascotasObj = null;
    private androidx.appcompat.app.AlertDialog dialog;
    private Button btnPopCerrar;


    public CustomExpandableListAdapterConsultar(Context context,
                                                List<String> listTitulo,
                                                HashMap<String, Mascota> expandableListDetalles, LayoutInflater inflater, Activity activity, ViewGroup root) {
        this.context = context;
        this.layoutInflater = inflater;
        this.listTitulo = listTitulo;
        this.expandableListDetalles = expandableListDetalles;
        this.activity = activity;
        this.root = root;
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Imagenes");
    }


    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Mascota mascota = (Mascota) getChild(groupPosition, childPosition);

        LayoutInflater layoutInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = layoutInflater.inflate(R.layout.item_exp, null);
        LinearLayout layoutEditar = convertView.findViewById(R.id.ilistEdit);
        LinearLayout layoutEliminar = convertView.findViewById(R.id.ilistDel);
        LinearLayout layouInfo = convertView.findViewById(R.id.ilistInfo);

        layoutEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar(mascota);
            }
        });

        layoutEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ids = String.valueOf(mascota.getId());
                String textoID = "";

                AlertDialog.Builder dialogo = new AlertDialog.Builder(context);
                //View dialogView=LayoutInflater.from(getContext()).inflate(R.layout.dialog_eliminar,null);
                View dialogView = layoutInflater.inflate(R.layout.dialog_eliminar_papelera, null);

                dialogo.setView(dialogView);
                //dialogo.setCancelable(false);
                AlertDialog dialog_elimAlertDialog = dialogo.create();

                dialog_elimAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogView.findViewById(R.id.btnSiOpc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReference.child("Mascotas/Activos").child(mascota.getId()).removeValue();
                        mostarToast("Registro eliminado exitosamente", 1, true);
                        dialog_elimAlertDialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.btnSiPapOpc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Mascotas[] mascotaPapelera = {null};
                        Query queryMascota = databaseReference.child("Mascotas/Activos").orderByChild("idUI").equalTo(mascota.getId());
                        queryMascota.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                    mascotaPapelera[0] = objSnapshot.getValue(Mascotas.class);
                                    mascotaPapelera[0].setIdUI(UUID.randomUUID().toString());
                                    mascotaPapelera[0].setEstatus("INACTIVO");
                                    databaseReference.child("Mascotas/Inactivos").child(mascotaPapelera[0].getIdUI()).setValue(mascotaPapelera[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Task<Void> task = databaseReference.child("Mascotas/Activos").child(mascota.getId()).removeValue();

                                            mostarToast("Registro enviado a la papelera", 1, true);
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        dialog_elimAlertDialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.btnNoOpc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mostarToast("Acción cancelada", 0, true);
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


    private void cargaImagen(ImageView ivFoto, Mascota mascota) {

        if (activity != null) {
            storageReference.child(mascota.getImagen()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(ivFoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mostarToast("Ups! Ha ocurrido un erro al recuperar la imagen\n" + e.getCause(), 3, false);
                }
            });
        }
    }

    Mascotas mascotasPop = null;

    //Instancia a la base de datos
    public void editar(Mascota mascota) {


        AlertDialog.Builder dialogo = new AlertDialog.Builder(context);
        //View dialogView=LayoutInflater.from(getContext()).inflate(R.layout.dialog_eliminar,null);
        View dialogView = layoutInflater.inflate(R.layout.fragment_actualizar, null);
        dialogo.setView(dialogView);

        //dialogo.setCancelable(false);
        AlertDialog dialog_elimAlertDialog = dialogo.create();
        editID = dialogView.findViewById(R.id.editActualizaID);
        editNomMas = dialogView.findViewById(R.id.ediActualizaNomMas);
        editNomProp = dialogView.findViewById(R.id.editActualizaNomPropie);
        editDireccion = dialogView.findViewById(R.id.editActualizaDireccion);
        editTelefono = dialogView.findViewById(R.id.editActualizaTel);
        editEmail = dialogView.findViewById(R.id.editActualizaEmail);
        editNacimiento = dialogView.findViewById(R.id.editActualizaNac);
        editColor = dialogView.findViewById(R.id.editActualizaColor);
        editParticulares = dialogView.findViewById(R.id.editActualizaSenas);
        btnCalendar = dialogView.findViewById(R.id.iBtnActualizaNac);
        imgFoto = dialogView.findViewById(R.id.imgActualizaFoto);
        spnEspecie = dialogView.findViewById(R.id.spinActualizaEspecie);
        spnRaza = dialogView.findViewById(R.id.spinActualizaRaza);
        spnSexo = dialogView.findViewById(R.id.spinActualizaSexo);
        spnVeterinario = dialogView.findViewById(R.id.spinActualizaVeterinario);
        btnLimpiar = dialogView.findViewById(R.id.btnActualizaLimpiar);
        btnEditar = dialogView.findViewById(R.id.btnActualizaGuardar);
        btnCancelar = dialogView.findViewById(R.id.btnActualizaCancelar);

        spinnerComponent(dialogView);

        Query queryMascota = databaseReference.child("Mascotas/Activos").orderByChild("idUI").equalTo(mascota.getId());
        queryMascota.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                        mascotasObj = objSnapshot.getValue(Mascotas.class);
                        editID.setText(mascotasObj.getClaveMascota());
                        editNomMas.setText(mascotasObj.getNombre());
                        editNomProp.setText(mascotasObj.getNombrePropietario());
                        nom_vet = mascotasObj.getVeterinario();
                        editDireccion.setText(mascotasObj.getDireccion());
                        editTelefono.setText(mascotasObj.getTelefono());
                        editEmail.setText(mascotasObj.getEmail());
                        especie = mascotasObj.getEspecie();
                        editNacimiento.setText(mascotasObj.getFechaNacimiento());
                        sexo = mascotasObj.getSexo();
                        raza = mascotasObj.getRaza();
                        editColor.setText(mascotasObj.getColor());
                        editParticulares.setText(mascotasObj.getParticularidades());

                        cargaImagen(imgFoto, mascota.getImagen());

                        spnEspecie.setSelection(obtenerPosicion(spnEspecie, especie));
                        ArrayAdapter<CharSequence> especieAdapter;
                        switch (obtenerPosicion(spnEspecie, especie)) {
                            case 1:
                                especieAdapter = ArrayAdapter.createFromResource(context, R.array.opc1, R.layout.spinner_item_model);
                                break;
                            case 2:
                                especieAdapter = ArrayAdapter.createFromResource(context, R.array.opc2, R.layout.spinner_item_model);
                                break;
                            default:
                                especieAdapter = ArrayAdapter.createFromResource(context, R.array.opc0, R.layout.spinner_item_model);
                                break;
                        }
                        spnRaza.setAdapter(especieAdapter);

                        spnSexo.setSelection(obtenerPosicion(spnSexo, sexo));
                        spnVeterinario.setSelection(obtenerPosicion(spnVeterinario, nom_vet));

//
                        String position = "";
                        for (int i = 0; i < especieAdapter.getCount(); i++) {
                            String s = String.valueOf(especieAdapter.getItem(i));
                            if (s.equalsIgnoreCase(raza)) {
                                position = s;
                            }
                        }
                        spnRaza.setText(position);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog recogeFecha = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayofMonth) {
                        final int mesActual = month + 1;
                        String diaf = (dayofMonth < 10) ? CERO + String.valueOf(dayofMonth) : String.valueOf(dayofMonth);
                        String mesf = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                        editNacimiento.setText("" + diaf + BARRA + mesf + BARRA + year);
                    }
                }, anioC, mesC, diaC);
                recogeFecha.show();
            }
        });


        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombre_masc = editNomMas.getText().toString();
                String nombre_propi = editNomProp.getText().toString();
                String fecha_nacimiento = editNacimiento.getText().toString();
                String color = editColor.getText().toString();
                String direccion = editDireccion.getText().toString();
                String telefono = editTelefono.getText().toString();
                String email = editEmail.getText().toString();
                String particularidades = String.valueOf(editParticulares.getText());
                String nombre_veteri = spnVeterinario.getSelectedItem().toString();
                String raz = spnRaza.getText().toString();
                String esp = spnEspecie.getSelectedItem().toString();
                String sexoAnimal = spnSexo.getSelectedItem().toString();
                Mascotas mascotaNueva = new Mascotas();
                mascotaNueva.setIdUI(mascota.getId());


                String[] split = fecha_nacimiento.split("/");
                String clave = nombre_masc.substring(0, 2) + nombre_propi.substring(0, 2) + split[0] + split[1] + split[2];
                mascotaNueva.setClaveMascota(clave.toUpperCase());

                mascotaNueva.setNombre(nombre_masc);
                mascotaNueva.setNombrePropietario(nombre_propi);
                mascotaNueva.setFechaNacimiento(fecha_nacimiento);
                mascotaNueva.setColor(color);
                mascotaNueva.setDireccion(direccion);
                mascotaNueva.setTelefono(telefono);
                mascotaNueva.setEmail(email);
                mascotaNueva.setParticularidades(particularidades);
                mascotaNueva.setVeterinario(nombre_veteri);
                mascotaNueva.setRaza(raz);
                mascotaNueva.setEspecie(esp);
                mascotaNueva.setSexo(sexoAnimal);
                mascotaNueva.setEstatus(mascota.getEstatus());
                mascotaNueva.setImagen(mascota.getImagen());

                databaseReference.child("Mascotas/Activos").child(mascota.getId()).setValue(mascotaNueva);
                //  mostarToast("Registro editado");
                mostarToast("Actualización exitosa", 1, true);
                dialog_elimAlertDialog.dismiss();
            }
        });
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiar();
            }
        });


        dialogView.findViewById(R.id.btnActualizaCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_elimAlertDialog.dismiss();
            }
        });

        dialog_elimAlertDialog.show();
    }

    private void spinnerComponent(View root) {
        ArrayAdapter<CharSequence> especieAdapter, sexoAdapter, veterinarioAdapter, razaAdapter;
        especieAdapter = ArrayAdapter.createFromResource(context, R.array.especie, R.layout.spinner_item_model);
        sexoAdapter = ArrayAdapter.createFromResource(context, R.array.sx, R.layout.spinner_item_model);
        veterinarioAdapter = ArrayAdapter.createFromResource(context, R.array.veterinarioARRAY, R.layout.spinner_item_model);
        razaAdapter = ArrayAdapter.createFromResource(context, R.array.opc0, R.layout.spinner_item_model);

        spnEspecie = root.findViewById(R.id.spinActualizaEspecie);
        spnEspecie.setAdapter(especieAdapter);

        spnRaza = root.findViewById(R.id.spinActualizaRaza);
        spnRaza.setAdapter(razaAdapter);

        spnSexo = root.findViewById(R.id.spinActualizaSexo);
        spnSexo.setAdapter(sexoAdapter);

        spnVeterinario = root.findViewById(R.id.spinActualizaVeterinario);
        spnVeterinario.setAdapter(veterinarioAdapter);

        spnVeterinario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    veterinario = adapterView.getItemAtPosition(i).toString();
                } else {
                    veterinario = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<CharSequence> especieAdapter;

                switch (i) {
                    case 1:
                        especieAdapter = ArrayAdapter.createFromResource(context, R.array.opc1, R.layout.spinner_item_model);
                        break;
                    case 2:
                        especieAdapter = ArrayAdapter.createFromResource(context, R.array.opc2, R.layout.spinner_item_model);
                        break;
                    default:
                        especieAdapter = ArrayAdapter.createFromResource(context, R.array.opc0, R.layout.spinner_item_model);
                        break;
                }
                spnRaza.setAdapter(especieAdapter);
                if (i != 0) {
                    especie = adapterView.getItemAtPosition(i).toString();
                } else {
                    especie = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnSexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    sexo = adapterView.getItemAtPosition(i).toString();
                } else {
                    sexo = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnRaza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    raza = adapterView.getItemAtPosition(i).toString();
                } else {
                    raza = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    public boolean validaVacios(EditText... o) {
        boolean b = true;
        for (EditText x : o) {
            if (x.getText().toString().isEmpty()) {
                b = false;
                x.requestFocus();
                x.setError("Campo obligatorio");
            }
        }
        return b;
    }

    private void limpiar() {
        limpiarEditx(editNacimiento, editTelefono, editColor, editEmail, editDireccion, editID, editNomMas, editNomProp, editParticulares);
        imgFoto.setImageResource(R.drawable.perro_1);
        sexo = "";
        veterinario = "";
        especie = "";
        raza = "";

        ArrayAdapter<CharSequence> especieAdapter, sexoAdapter, veterinarioAdapter, razaAdapter;
        especieAdapter = ArrayAdapter.createFromResource(context, R.array.especie, R.layout.spinner_item_model);
        sexoAdapter = ArrayAdapter.createFromResource(context, R.array.sx, R.layout.spinner_item_model);
        veterinarioAdapter = ArrayAdapter.createFromResource(context, R.array.veterinarioARRAY, R.layout.spinner_item_model);
        razaAdapter = ArrayAdapter.createFromResource(context, R.array.opc0, R.layout.spinner_item_model);

        spnEspecie.setAdapter(especieAdapter);
        spnRaza.setAdapter(razaAdapter);
        spnSexo.setAdapter(sexoAdapter);
        spnVeterinario.setAdapter(veterinarioAdapter);

    }


    private void limpiarEditx(EditText... o) {
        for (EditText x : o) {
            x.setText("");
        }
    }

    private static int obtenerPosicion(Spinner spinner, String item) {
        int position = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                position = i;
            }
        }
        return position;
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
                        mostarToast("Ups! Ha ocurrido un erro al recuperar la imagen\n" + e.getCause(), 3, false);
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

   /* public void mostarToast(String txt) {
        LayoutInflater inflater = layoutInflater;
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) root.findViewById(R.id.layout_base));
        TextView textView = layout.findViewById(R.id.txtToast);
        textView.setText(txt);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
*/

    private void createNewAboutDialog(Mascota mascota) {

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

        Query queryMascota = databaseReference.child("Mascotas/Activos").orderByChild("idUI").equalTo(mascota.getId());
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

    public void mostarToast(String txt, int estatus, boolean corto) {
    try {
        LayoutInflater inflater = layoutInflater;

        View layout = null;

        if (estatus == 0) {
            //Default
            layout = inflater.inflate(R.layout.custom_toast_info,
                    (ViewGroup) root.findViewById(R.id.layout_base));

        } else if (estatus == 1) {
            //Success
            layout = inflater.inflate(R.layout.custom_toast_success,
                    (ViewGroup) root.findViewById(R.id.layout_base));

        } else if (estatus == 2) {
            //Warning
            layout = inflater.inflate(R.layout.custom_toast_warning,
                    (ViewGroup) root.findViewById(R.id.layout_base));

        } else if (estatus == 3) {
            //Error
            layout = inflater.inflate(R.layout.custom_toast_error,
                    (ViewGroup) root.findViewById(R.id.layout_base));


        } else if (estatus == 4) {
            //Falla de red
            layout = inflater.inflate(R.layout.custom_toast_red,
                    (ViewGroup) root.findViewById(R.id.layout_base));


        } else if (estatus == 5) {
            //Falla de red
            layout = inflater.inflate(R.layout.custom_toast_sin_data,
                    (ViewGroup) root.findViewById(R.id.layout_base));


        } else {
            //Informacion
            layout = inflater.inflate(R.layout.custom_toast_info,
                    (ViewGroup) root.findViewById(R.id.layout_base));
        }

        TextView textView = layout.findViewById(R.id.txtToast);
        textView.setText(txt);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 50);
        if (corto) {
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.setView(layout);
        toast.show();
    }catch (Exception e){

    }

    }

    private void cargaImagen(ImageView ivFoto, String img) {
        storageReference.child(img).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(ivFoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mostarToast("Ups! Ha ocurrido un erro al recuperar la imagen\n" + e.getCause(), 3, false);
            }
        });

    }

}
