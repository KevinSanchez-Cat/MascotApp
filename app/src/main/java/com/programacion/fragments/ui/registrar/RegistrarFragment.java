package com.programacion.fragments.ui.registrar;

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
import android.location.LocationManager;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.programacion.fragments.R;
import com.programacion.fragments.bd.Sqlite;
import com.programacion.fragments.databinding.FragmentRegistrarBinding;
import com.programacion.fragments.modelo.Mascotas;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class RegistrarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    //Atributos del Layout
  //  private com.google.android.material.textfield.TextInputEditText editID;
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
    private Spinner spnRaza;
    private Spinner spnSexo;
    private Spinner spnVeterinario;
    private Button btnLimpiar;
    private Button btnGuardar;
    private Button btnCancelar;
    private com.google.android.material.floatingactionbutton.FloatingActionButton btnTomaFoto;
    //Atributos necesarios
    private DatePickerDialog datePicker;
    private Calendar cal;
    private static int anio, mes, dia;

    private Uri photoURI;
    public static final int REQUEST_TAKE_PHOTO = 101;
    public static final int REQUEST_PERMISSION_CAMERA = 100;
    public static final int REQUEST_PERMISSION_WRITE_STORANGE = 200;
    public static String img = "", sexo, veterinario, especie, raza, naci, parti;

    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;
    private final String longitud = "";
    private final String latitud = "";
    private final String r = "";
    private LocationManager locationManager;

    //FOTOGRAFIA
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;


    //Instancia a la base de datos
    public Sqlite sqlite;
    LayoutInflater inflaterLayout;

    private RegistrarViewModel registrarViewModel;
    private FragmentRegistrarBinding binding;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registrarViewModel =
                new ViewModelProvider(this).get(RegistrarViewModel.class);

        binding = FragmentRegistrarBinding.inflate(inflater, container, false);
        root = inflater.inflate(R.layout.fragment_registrar, container, false);
        initUI(root);
        iniciarFirebase();
        this.inflaterLayout=inflater;
        return root;
    }
    private void iniciarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Imagenes");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
    private void cargaArchivo() {
        img = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "." + extension(photoURI);
        ///////
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                StorageReference ref = storageReference.child(img);
                ref.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mostarToast("Imagen cargada exitosamente",1,true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostarToast( "Algo salio mal al cargar el archivo\n"+e.getCause() + "",3,true);
                    }
                });
            } else {
                StorageReference ref = storageReference.child(img);
                ref.putFile(photoURI);
                mostarToast("Imagen cargada exitosamente\nEstado: sin conexión",4,true);
            }
        }catch (Exception e){

        }

//////

    }

    private String extension(Uri photoUri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(photoUri));
    }

    public void initUI(View root) {
       // editID = (com.google.android.material.textfield.TextInputEditText) root.findViewById(R.id.editRegisID);
        editNomMas = root.findViewById(R.id.editRegNomMas);
        editNomProp = root.findViewById(R.id.editRegNomPropie);
        editDireccion = root.findViewById(R.id.editRegDireccion);
        editTelefono = root.findViewById(R.id.editRegTel);
        editEmail = root.findViewById(R.id.editRegEmail);
        editNacimiento = root.findViewById(R.id.editRegisNac);
        editColor = root.findViewById(R.id.editRegColor);
        editParticulares = root.findViewById(R.id.editRegSenas);
        btnCalendar = root.findViewById(R.id.iBtnRegisNac);
        imgFoto = root.findViewById(R.id.imgRegisFoto);
        spnEspecie = root.findViewById(R.id.spinRegEspecie);
        spnSexo = root.findViewById(R.id.spinRegSexo);
        spnVeterinario = root.findViewById(R.id.spinRegVeterinario);
        spnRaza = root.findViewById(R.id.spinRegRaza);
        btnLimpiar = root.findViewById(R.id.btnRegisLimpiar);
        btnGuardar = root.findViewById(R.id.btnRegisGuardar);
        // btnCancelar = (Button) root.findViewById(R.id.btnRegisCancelar);
        btnTomaFoto = root.findViewById(R.id.fBtnRegisFoto);

        spinnerComponent(root);
        btnCalendar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnLimpiar.setOnClickListener(this);
//        btnCancelar.setOnClickListener(this);
        btnTomaFoto.setOnClickListener(this);
    }

    private void spinnerComponent(View root) {
        ArrayAdapter<CharSequence> especieAdapter, sexoAdapter, veterinarioAdapter, razaAdapter;
        especieAdapter = ArrayAdapter.createFromResource(getContext(), R.array.especie, R.layout.spinner_item_model);
        sexoAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sx, R.layout.spinner_item_model);
        veterinarioAdapter = ArrayAdapter.createFromResource(getContext(), R.array.veterinarioARRAY, R.layout.spinner_item_model);
        razaAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opc0, R.layout.spinner_item_model);

        spnEspecie = root.findViewById(R.id.spinRegEspecie);
        spnEspecie.setAdapter(especieAdapter);

        spnRaza = root.findViewById(R.id.spinRegRaza);
        spnRaza.setAdapter(razaAdapter);

        spnSexo = root.findViewById(R.id.spinRegSexo);
        spnSexo.setAdapter(sexoAdapter);

        spnVeterinario = root.findViewById(R.id.spinRegVeterinario);
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
        limpiarEditx(editNacimiento, editTelefono, editColor, editEmail, editDireccion, editNomMas, editNomProp, editParticulares);
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
                    //  Toast.makeText(getContext(), "Cargar camara...", Toast.LENGTH_SHORT).show();
                } else if (opciones[i].equals("Elegir de galeria")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(Intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDateSet(DatePicker viewDate, int year, int month, int dayOfMonth) {
        editNacimiento.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        naci = editNacimiento.getText().toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case COD_SELECCIONA:
                if(data!=null){
                    Uri miPath = data.getData();
                    imgFoto.setImageURI(miPath);
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContext().getContentResolver().query(miPath, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String paths = cursor.getString(columnIndex);
                    cursor.close();
                    img = paths;
                    photoURI=miPath;
                    img = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "." + extension(photoURI);
                    cargaArchivo();
                    // bitmap = BitmapFactory.decodeFile(path);
                    //  imgProducto.setImageBitmap(bitmap);
                }

                break;
            case COD_FOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap imageBitmapNuevo = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

                    String tituliImge = "IMG_" + System.currentTimeMillis();
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmapNuevo, tituliImge, "Foto de la app de mascotas");
                    //muestra la imagen
                    photoURI = Uri.parse(path);

                    imgFoto.setImageBitmap(imageBitmapNuevo);
                    //img = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures" + "/" + tituliImge + ".jpg";

                    img = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "." + extension(photoURI);
                    cargaArchivo();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iBtnRegisNac:
                cal = Calendar.getInstance();
                anio = cal.get(Calendar.YEAR);
                mes = cal.get(Calendar.MONTH);
                dia = cal.get(Calendar.DAY_OF_MONTH);
                datePicker = new DatePickerDialog(getContext(), this, anio, mes, dia);
                datePicker.show();
                break;
            case R.id.btnRegisGuardar:
                if (validaVacios(editParticulares, editNomProp, editNomMas, editEmail, editDireccion, editColor, editTelefono,
                        editNacimiento)) {
                    String fechaV = naci;
                    if(validaFecha(fechaV)){
                        editNacimiento.requestFocus();
                        editNacimiento.setError("Fecha no valida");
                    }else{
                        // int id = Integer.parseInt(editID.getText().toString());
                        String nombre_masc = editNomMas.getText().toString();
                        String nombre_propi = editNomProp.getText().toString();
                        String fecha_nacimiento = naci;
                        String color = editColor.getText().toString();
                        String direccion = editDireccion.getText().toString();
                        String telefono = editTelefono.getText().toString();
                        String email = editEmail.getText().toString();
                        String particularidades = String.valueOf(editParticulares.getText());
                        String nombre_veteri = spnVeterinario.getSelectedItem().toString();
                        String raz = spnRaza.getSelectedItem().toString();
                        String esp = spnEspecie.getSelectedItem().toString();
                        String sexoAnimal = spnSexo.getSelectedItem().toString();

                        Mascotas mascota=new Mascotas();
                        mascota.setIdUI(UUID.randomUUID().toString());

                        String[] split = fecha_nacimiento.split("/");
                        String clave=nombre_masc.substring(0,2)+nombre_propi.substring(0,2)+split[0]+split[1]+split[2];
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
                        mascota.setEstatus("ACTIVO");
                        mascota.setImagen(img);

                            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                databaseReference.child("Mascotas/Activos").child(mascota.getIdUI()).setValue(mascota).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        limpiar();
                                        mostarToast("Mascota registrada exitosamente",1,true);
                                        img="";
                                    }
                                });
                            }else{
                                databaseReference.child("Mascotas/Activos").child(mascota.getIdUI()).setValue(mascota);
                                mostarToast("Mascota guardada de manera exitosa" + "\nEstado: Sin conexión",4,true);
                            }


                    }
                } else {
                    // Toast.makeText(getContext(), "Requiere llenar los campos", Toast.LENGTH_LONG).show();
                    mostarToast("Requiere llenar los campos",2,true);
                }
                break;
            case R.id.btnRegisLimpiar:
                limpiar();
                break;
            case R.id.fBtnRegisFoto:
                mostrarDialogOpciones();
                break;

        }
    }

    private boolean validaFecha(String editNacimiento) {
        String edad = "";
        String naci = editNacimiento;
        String[] dias = naci.split("/");

        int dia = Integer.parseInt(dias[0]);
        int mes = Integer.parseInt(dias[1]);
        int anio = Integer.parseInt(dias[2]);

        Calendar c = Calendar.getInstance();
        int anioC = c.get(Calendar.YEAR);
        int mesC = c.get(Calendar.MONTH)+1;
        int diaC = c.get(Calendar.DAY_OF_MONTH);

        int anios = anioC - anio;
        int meses = mesC - mes;
        int semanas = (diaC - dia) / 7;
        if (anio == anioC) {
            if (mes == mesC) {
                return dia > diaC;
            } else return mes >= mesC;
        } else return anio >= anioC;

    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.spinRegSexo:
                if (position != 0) {
                    sexo = adapterView.getItemAtPosition(position).toString();
                } else {
                    sexo = "";
                }
                break;
            case R.id.spinRegEspecie:
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
            case R.id.spinRegVeterinario:
                veterinario = adapterView.getItemAtPosition(position).toString();
                break;
            case R.id.spinRegRaza:
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
        toast.show();}catch (Exception e){

        }
    }
}