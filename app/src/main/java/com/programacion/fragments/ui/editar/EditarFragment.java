package com.programacion.fragments.ui.editar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
import com.google.firebase.storage.UploadTask;
import com.programacion.fragments.R;
import com.programacion.fragments.databinding.FragmentEditarBinding;
import com.programacion.fragments.modelo.Mascotas;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private com.google.android.material.textfield.TextInputEditText editBuscaID;
    private com.google.android.material.textfield.TextInputEditText editID;
    private com.google.android.material.textfield.TextInputEditText editNomMas;
    private com.google.android.material.textfield.TextInputEditText editNomProp;
    private com.google.android.material.textfield.TextInputEditText editDireccion;
    private com.google.android.material.textfield.TextInputEditText editTelefono;
    private com.google.android.material.textfield.TextInputEditText editEmail;
    private com.google.android.material.textfield.TextInputEditText editNacimiento;
    private com.google.android.material.textfield.TextInputEditText editColor;
    private com.google.android.material.textfield.TextInputEditText editParticulares;

    private ImageButton btnBuscaID;
    private ImageButton btnCalendar;
    private ImageView imgFoto;
    private Spinner spnEspecie;
    private Spinner spnRaza;
    private Spinner spnSexo;
    private Spinner spnVeterinario;
    private Button btnLimpiar;
    private Button btnEditar;
    private Button btnCancelar;
    private com.google.android.material.floatingactionbutton.FloatingActionButton btnTomaFoto;

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
    private boolean bndCal, bndMasc;

    //Atributos necesarios
    private DatePickerDialog datePicker;
    private Calendar cal;
    private static int anio, mes, dia;

    private Uri photoURI;
    public static final int REQUEST_TAKE_PHOTO = 101;
    public static final int REQUEST_PERMISSION_CAMERA = 100;
    public static final int REQUEST_PERMISSION_WRITE_STORANGE = 200;
    public static String img = "", sexo, veterinario, especie, raza;
    LayoutInflater inflaterLayout;

    //FOTOGRAFIA
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;


    //Fisebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;


    Mascotas mascota;

    private EditarViewModel editarViewModel;
    private FragmentEditarBinding binding;
    View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editarViewModel =
                new ViewModelProvider(this).get(EditarViewModel.class);

        binding = FragmentEditarBinding.inflate(inflater, container, false);
        root = inflater.inflate(R.layout.fragment_editar, container, false);
        initUI(root);
        iniciarFirebase();
        this.inflaterLayout=inflater;
        return root;
    }

    private void iniciarFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Imagenes");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initUI(View root) {
        editBuscaID = root.findViewById(R.id.editBuscEditID);
        editID = root.findViewById(R.id.editEditID);
        editNomMas = root.findViewById(R.id.ediEditNomMas);
        editNomProp = root.findViewById(R.id.editEditNomPropie);
        editDireccion = root.findViewById(R.id.editEditDireccion);
        editTelefono = root.findViewById(R.id.editEditTel);
        editEmail = root.findViewById(R.id.editEditEmail);
        editNacimiento = root.findViewById(R.id.editEditNac);
        editColor = root.findViewById(R.id.editEditColor);
        editParticulares = root.findViewById(R.id.editEditSenas);
        btnBuscaID = root.findViewById(R.id.iBtnBuscarEditID);
        btnCalendar = root.findViewById(R.id.iBtnEditNac);
        imgFoto = root.findViewById(R.id.imgEditFoto);
        spnEspecie = root.findViewById(R.id.spinEditEspecie);
        spnRaza = root.findViewById(R.id.spinEditRaza);
        spnSexo = root.findViewById(R.id.spinEditSexo);
        spnVeterinario = root.findViewById(R.id.spinEdiVeterinario);
        btnLimpiar = root.findViewById(R.id.btnEditLimpiar);
        btnEditar = root.findViewById(R.id.btnEditGuardar);
        btnTomaFoto = root.findViewById(R.id.fBtnEditFoto);


        spinnerComponent(root);
        btnCalendar.setOnClickListener(this);
        btnBuscaID.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnLimpiar.setOnClickListener(this);
        btnTomaFoto.setOnClickListener(this);

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
                    mostarToast(e.getCause() + "", 3, false);
                }
            });
        }
    }

    private void cargaArchivo() {
        img = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date()) + "." + extension(photoURI);
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                StorageReference ref = storageReference.child(img);

                ref.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mostarToast("La imagen se cargo exitosamente", 1, true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostarToast(e.getCause() + "", 3, true);
                    }
                });
            } else {
                StorageReference ref = storageReference.child(img);
                ref.putFile(photoURI);
                mostarToast("La imagen se cargo exitosamente" + "\nEstado: sin conexión", 4, true);
            }
        }catch (Exception e){

        }


    }

    private String extension(Uri photoUri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(photoUri));
    }

    private void spinnerComponent(View root) {
        ArrayAdapter<CharSequence> especieAdapter, sexoAdapter, veterinarioAdapter, razaAdapter;
        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.especie, R.layout.spinner_item_model);
        sexoAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sx, R.layout.spinner_item_model);
        veterinarioAdapter = ArrayAdapter.createFromResource(getContext(), R.array.veterinarioARRAY, R.layout.spinner_item_model);
        razaAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc0, R.layout.spinner_item_model);

        spnEspecie = root.findViewById(R.id.spinEditEspecie);
        spnEspecie.setAdapter(especieAdapter);

        spnRaza = root.findViewById(R.id.spinEditRaza);
        spnRaza.setAdapter(razaAdapter);

        spnSexo = root.findViewById(R.id.spinEditSexo);
        spnSexo.setAdapter(sexoAdapter);

        spnVeterinario = root.findViewById(R.id.spinEdiVeterinario);
        spnVeterinario.setAdapter(veterinarioAdapter);

        spnVeterinario.setOnItemSelectedListener(this);
        spnEspecie.setOnItemSelectedListener(this);
        spnSexo.setOnItemSelectedListener(this);
        spnRaza.setOnItemSelectedListener(this);

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
        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.especie, R.layout.spinner_item_model);
        sexoAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sx, R.layout.spinner_item_model);
        veterinarioAdapter = ArrayAdapter.createFromResource(getContext(), R.array.veterinarioARRAY, R.layout.spinner_item_model);
        razaAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc0, R.layout.spinner_item_model);

        spnEspecie.setAdapter(especieAdapter);
        spnRaza.setAdapter(razaAdapter);
        spnSexo.setAdapter(sexoAdapter);
        spnVeterinario.setAdapter(veterinarioAdapter);

    }

    public void mostrarDialogOpciones() {
        CharSequence[] opciones = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar foto")) {
                    abrirCamera();
                    //   Toast.makeText(getContext(), "Cargar camara...", Toast.LENGTH_SHORT).show();
                } else if (opciones[i].equals("Elegir de galeria")) {

                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    private void abrirCamera() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, COD_FOTO);
            }
        } else {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA
            );
        }
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
    public void onDateSet(DatePicker viewDate, int year, int month, int dayOfMonth) {
        editNacimiento.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case COD_SELECCIONA:
                if (data != null) {
                    Uri miPath = data.getData();
                    imgFoto.setImageURI(miPath);
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContext().getContentResolver().query(miPath, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String paths = cursor.getString(columnIndex);
                    cursor.close();
                    img = paths;
                    photoURI = miPath;
                    cargaArchivo();
                    // bitmap = BitmapFactory.decodeFile(path);
                    //  imgProducto.setImageBitmap(bitmap);
                }
                break;

            case COD_FOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Bundle extras = data.getExtras();
                    //optiene la imagen
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    //hacer un mapeo para la imagen
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    //Comprime la imagen
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    //Rota la imagen
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap imageBitmapNuevo = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

                    //Guarda
                    String tituliImge = "IMG_" + System.currentTimeMillis();
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmapNuevo, tituliImge, "Foto de la app de mascotas");
                    //muestra la imagen
                    //  photoURI = Uri.parse(path);
                    imgFoto.setImageBitmap(imageBitmapNuevo);
                    photoURI = Uri.parse(path);
                    cargaArchivo();
                }
                break;

        }
    }

    String estatus = "";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iBtnEditNac:
                cal = Calendar.getInstance();
                anio = cal.get(Calendar.YEAR);
                mes = cal.get(Calendar.MONTH);
                dia = cal.get(Calendar.DAY_OF_MONTH);
                datePicker = new DatePickerDialog(getContext(), this, anio, mes, dia);
                datePicker.show();
                break;
            case R.id.btnEditGuardar:
                String txtID = editID.getText().toString();
                if (txtID.isEmpty()) {
                    mostarToast("No es posible actualizar por ID", 2, true);
                } else {
                    if (validaVacios(editNomMas, editNomProp, editColor, editDireccion, editTelefono, editEmail, editParticulares, editNacimiento)) {
                        String fechaV = editNacimiento.getText().toString();
                        if (validaFecha(fechaV)) {
                            editNacimiento.requestFocus();
                            editNacimiento.setError("Fecha no valida");
                        } else {
                            String nombre_masc = editNomMas.getText().toString();
                            String nombre_propi = editNomProp.getText().toString();
                            String fecha_nacimiento = editNacimiento.getText().toString();
                            String color = editColor.getText().toString();
                            String direccion = editDireccion.getText().toString();
                            String telefono = editTelefono.getText().toString();
                            String email = editEmail.getText().toString();
                            String particularidades = String.valueOf(editParticulares.getText());
                            String nombre_veteri = spnVeterinario.getSelectedItem().toString();
                            String raz = spnRaza.getSelectedItem().toString();
                            String esp = spnEspecie.getSelectedItem().toString();
                            String sexoAnimal = spnSexo.getSelectedItem().toString();

                            mascota.setIdUI(mascota.getIdUI());
                            String[] split = fecha_nacimiento.split("/");
                            String clave = nombre_masc.substring(0, 2) + nombre_propi.substring(0, 2) + split[0] + split[1] + split[2];
                            mascota.setClaveMascota(clave.toUpperCase());

                            mascota.setNombre(nombre_masc);
                            mascota.setNombrePropietario(nombre_propi);
                            mascota.setFechaNacimiento(fecha_nacimiento);
                            mascota.setColor(color);
                            mascota.setDireccion(direccion);
                            mascota.setTelefono(telefono);
                            mascota.setEmail(email);
                            mascota.setParticularidades(particularidades);
                            mascota.setVeterinario(nombre_veteri);
                            mascota.setRaza(raz);
                            mascota.setEspecie(esp);
                            mascota.setSexo(sexoAnimal);
                            mascota.setEstatus(mascota.getEstatus());
                            mascota.setImagen(img);
                            id = mascota.getIdUI();
                            try {
                                ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.isConnected()) {

                                    if (mascota.getEstatus().equals("ACTIVO")) {
                                        databaseReference.child("Mascotas/Activos").child(id).setValue(mascota).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mostarToast("Actualización exitosa", 1, true);
                                                limpiar();
                                                img = "";
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mostarToast("Ups!, algo falló\n" + e.getCause(), 3, true);
                                            }
                                        });
                                    } else {
                                        databaseReference.child("Mascotas/Inactivos").child(id).setValue(mascota).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mostarToast("Actualización exitosa", 1, true);
                                                limpiar();
                                                img = "";
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mostarToast("Ups!, algo falló\n" + e.getCause(), 3, true);
                                            }
                                        });
                                    }

                                } else {
                                    if (mascota.getEstatus().equals("ACTIVO")) {
                                        databaseReference.child("Mascotas/Activos").child(id).setValue(mascota).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mostarToast("Actualización exitosa", 1, true);
                                                limpiar();
                                                img = "";
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mostarToast("Ups!, algo falló\n" + e.getCause(), 3, true);
                                            }
                                        });
                                    } else {
                                        databaseReference.child("Mascotas/Inactivos").child(id).setValue(mascota).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mostarToast("Actualización exitosa", 1, true);
                                                limpiar();
                                                img = "";
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mostarToast("Ups!, algo falló\n" + e.getCause(), 3, true);
                                            }
                                        });
                                    }
                                    mostarToast("Se actualizó sin conexión", 3, true);
                                }

                            }catch (Exception e){

                            }


                        }

                    } else {
                        mostarToast("Hay campos vacios", 2, true);
                    }

                }

                break;
            case R.id.btnEditLimpiar:
                limpiar();
                break;
            case R.id.fBtnEditFoto:
                mostrarDialogOpciones();
                break;
            case R.id.iBtnBuscarEditID:
                String idString = editBuscaID.getText().toString();
                if (idString.isEmpty()) {
                    // Toast.makeText(getContext(), "Llene el campo del ID", Toast.LENGTH_LONG).show();
                    mostarToast("Llene el campo ID para buscar", 0, true);
                    editID.setText("");
                    editNomMas.setText("");
                    editNomProp.setText("");
                    editColor.setText("");
                    editDireccion.setText("");
                    editTelefono.setText("");
                    editParticulares.setText("");
                    editEmail.setText("");
                    editNacimiento.setText("");

                    imgFoto.setImageResource(R.drawable.perro_1);
                    ArrayAdapter<CharSequence> especieAdapter, sexoAdapter, veterinarioAdapter, razaAdapter;
                    especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.especie, R.layout.spinner_item_model);
                    sexoAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sx, R.layout.spinner_item_model);
                    veterinarioAdapter = ArrayAdapter.createFromResource(getContext(), R.array.veterinarioARRAY, R.layout.spinner_item_model);
                    razaAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc0, R.layout.spinner_item_model);

                    spnEspecie.setAdapter(especieAdapter);
                    spnRaza.setAdapter(razaAdapter);
                    spnSexo.setAdapter(sexoAdapter);
                    spnVeterinario.setAdapter(veterinarioAdapter);
                } else {

                    id = editBuscaID.getText().toString();

                    Query queryMascota = databaseReference.child("Mascotas/Activos").orderByChild("claveMascota").equalTo(id);
                    queryMascota.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                    mascota = objSnapshot.getValue(Mascotas.class);
                                    editID.setText(mascota.getClaveMascota());
                                    editNomMas.setText(mascota.getNombre());
                                    editNomProp.setText(mascota.getNombrePropietario());
                                    editColor.setText(mascota.getColor());
                                    editDireccion.setText(mascota.getDireccion());
                                    editTelefono.setText(mascota.getTelefono());
                                    editEmail.setText(mascota.getEmail());
                                    editParticulares.setText(mascota.getParticularidades());
                                    editNacimiento.setText(mascota.getFechaNacimiento());
                                    especie = mascota.getEspecie();
                                    raza = mascota.getRaza();
                                    sexo = mascota.getSexo();
                                    nom_vet = mascota.getVeterinario();
                                    img = mascota.getImagen();
                                    estatus = mascota.getEstatus();
                                }
                                try {
                                    ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                    if (networkInfo != null && networkInfo.isConnected()) {
                                        cargaImagen(imgFoto);
                                    } else {
                                        mostarToast("En estos momento no se puede observar la foto\nya que usted se encuentra sin conexión", 4, false);
                                    }

                                } catch (Exception e) {

                                }


                                spnEspecie.setSelection(obtenerPosicion(spnEspecie, especie));
                                ArrayAdapter<CharSequence> especieAdapter;
                                switch (obtenerPosicion(spnEspecie, especie)) {
                                    case 1:
                                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc1, R.layout.spinner_item_model);
                                        break;
                                    case 2:
                                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc2, R.layout.spinner_item_model);
                                        break;
                                    default:
                                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc0, R.layout.spinner_item_model);
                                        break;
                                }
                                spnRaza.setAdapter(especieAdapter);
                                spnRaza.setSelection(obtenerPosicion(spnRaza, raza));

                                spnSexo.setSelection(obtenerPosicion(spnSexo, sexo));
                                spnVeterinario.setSelection(obtenerPosicion(spnVeterinario, nom_vet));

                                bndCal = true;
                                bndMasc = true;

                            } else {
                                Query queryMascota = databaseReference.child("Mascotas/Inactivos").orderByChild("claveMascota").equalTo(id);
                                queryMascota.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                                mascota = objSnapshot.getValue(Mascotas.class);
                                                editID.setText(mascota.getClaveMascota());
                                                editNomMas.setText(mascota.getNombre());
                                                editNomProp.setText(mascota.getNombrePropietario());
                                                editColor.setText(mascota.getColor());
                                                editDireccion.setText(mascota.getDireccion());
                                                editTelefono.setText(mascota.getTelefono());
                                                editEmail.setText(mascota.getEmail());
                                                editParticulares.setText(mascota.getParticularidades());
                                                editNacimiento.setText(mascota.getFechaNacimiento());
                                                especie = mascota.getEspecie();
                                                raza = mascota.getRaza();
                                                sexo = mascota.getSexo();
                                                nom_vet = mascota.getVeterinario();
                                                img = mascota.getImagen();
                                                estatus = mascota.getEstatus();

                                                try {
                                                    ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                                    if (networkInfo != null && networkInfo.isConnected()) {
                                                        cargaImagen(imgFoto);
                                                    } else {
                                                        mostarToast("En estos momento no se puede observar la foto\nya que usted se encuentra sin conexión", 4, false);
                                                    }
                                                } catch (Exception e) {

                                                }


                                                spnEspecie.setSelection(obtenerPosicion(spnEspecie, especie));
                                                ArrayAdapter<CharSequence> especieAdapter;
                                                switch (obtenerPosicion(spnEspecie, especie)) {
                                                    case 1:
                                                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc1, R.layout.spinner_item_model);
                                                        break;
                                                    case 2:
                                                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc2, R.layout.spinner_item_model);
                                                        break;
                                                    default:
                                                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc0, R.layout.spinner_item_model);
                                                        break;
                                                }
                                                spnRaza.setAdapter(especieAdapter);
                                                spnRaza.setSelection(obtenerPosicion(spnRaza, raza));

                                                spnSexo.setSelection(obtenerPosicion(spnSexo, sexo));
                                                spnVeterinario.setSelection(obtenerPosicion(spnVeterinario, nom_vet));

                                                //cargarImagen2();
                                                bndCal = true;
                                                bndMasc = true;
                                            }
                                        } else {
                                            if (getContext() != null) {
                                                mostarToast("No se encontró el registro", 5, true);
                                                editID.setText("");
                                                editNomMas.setText("");
                                                editNomProp.setText("");
                                                editColor.setText("");
                                                editDireccion.setText("");
                                                editTelefono.setText("");
                                                editParticulares.setText("");
                                                editEmail.setText("");
                                                editNacimiento.setText("");
                                                imgFoto.setImageResource(R.drawable.perro_1);
                                                ArrayAdapter<CharSequence> especieAdapter, sexoAdapter, veterinarioAdapter, razaAdapter;
                                                especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.especie, R.layout.spinner_item_model);
                                                sexoAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sx, R.layout.spinner_item_model);
                                                veterinarioAdapter = ArrayAdapter.createFromResource(getContext(), R.array.veterinarioARRAY, R.layout.spinner_item_model);
                                                razaAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc0, R.layout.spinner_item_model);

                                                spnEspecie.setAdapter(especieAdapter);
                                                spnRaza.setAdapter(razaAdapter);
                                                spnSexo.setAdapter(sexoAdapter);
                                                spnVeterinario.setAdapter(veterinarioAdapter);
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

    private boolean validaFecha(String editNacimiento) {
        String edad = "";

        String naci = editNacimiento.toString();
        String dias[] = naci.toString().split("/");

        int dia = Integer.parseInt(dias[0]);
        int mes = Integer.parseInt(dias[1]);
        int anio = Integer.parseInt(dias[2]);

        Calendar c = Calendar.getInstance();
        int anioC = c.get(Calendar.YEAR);
        int mesC = c.get(Calendar.MONTH) + 1;
        int diaC = c.get(Calendar.DAY_OF_MONTH);
        int anios = anioC - anio;
        int meses = mesC - mes;
        int semanas = (diaC - dia) / 7;
        if (anio == anioC) {
            if (mes == mesC) {
                if (dia <= diaC) {
                    return false;
                } else {
                    return true;
                }
            } else if (mes < mesC) {
                return false;
            } else {
                return true;
            }
        } else if (anio < anioC) {
            return false;
        } else {
            return true;
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.spinEditSexo:
                if (position != 0) {
                    sexo = adapterView.getItemAtPosition(position).toString();
                } else {
                    sexo = "";
                }
                break;
            case R.id.spinEditEspecie:
                ArrayAdapter<CharSequence> especieAdapter;

                switch (position) {
                    case 1:
                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc1, R.layout.spinner_item_model);
                        break;
                    case 2:
                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc2, R.layout.spinner_item_model);
                        break;
                    default:
                        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc0, R.layout.spinner_item_model);
                        break;
                }
                spnRaza.setAdapter(especieAdapter);
                if (position != 0) {
                    especie = adapterView.getItemAtPosition(position).toString();
                } else {
                    especie = "";
                }
                break;
            case R.id.spinEdiVeterinario:
                if (position != 0) {
                    veterinario = adapterView.getItemAtPosition(position).toString();
                } else {
                    veterinario = "";
                }
                break;
            case R.id.spinEditRaza:
                if (position != 0) {
                    raza = adapterView.getItemAtPosition(position).toString();
                } else {
                    raza = "";
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void mostarToast(String txt, int estatus, boolean corto) {
      try {
          LayoutInflater inflater = this.inflaterLayout;

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
          Toast toast = new Toast(getContext());
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

}
