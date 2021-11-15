package com.programacion.fragments;

import android.Manifest;
import android.app.Activity;
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
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.programacion.fragments.modelo.Usuarios;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class RegistrarseActivity extends AppCompatActivity {


    TextView txtNuevaCuenta;
    TextView txtIniciaSession;
    TextView txtPoliticas;
    de.hdodenhof.circleimageview.CircleImageView imgFoto;
    com.google.android.material.textfield.TextInputEditText editNombre;
    com.google.android.material.textfield.TextInputEditText editApellido;
    com.google.android.material.textfield.TextInputEditText editEmail;
    com.google.android.material.textfield.TextInputEditText editPassword;
    com.google.android.material.textfield.TextInputEditText editConfirmPassword;
    com.google.android.material.textfield.TextInputEditText editTelefono;
    private com.google.android.material.floatingactionbutton.FloatingActionButton btnTomarFoto;
    Button btnRegistar;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    public static final int REQUEST_PERMISSION_CAMERA = 100;
    private Uri photoURI;
    public static String img = "";
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Perfiles");

        iniciarFirebase();


        imgFoto = findViewById(R.id.imgRegFoto);
        txtNuevaCuenta = findViewById(R.id.txtRegRegistrate);
        txtIniciaSession = findViewById(R.id.txtRegIniciaSession);
        txtPoliticas = findViewById(R.id.txtPoliticas);
        btnRegistar = findViewById(R.id.btnRegistrar);
        editNombre = findViewById(R.id.editRegNombre);
        editApellido = findViewById(R.id.editRegApellido);
        editEmail = findViewById(R.id.editRegEmail);
        editPassword = findViewById(R.id.editRegPass);
        editConfirmPassword = findViewById(R.id.editRegConfirPass);
        editTelefono=findViewById(R.id.editRegPhone);
        btnTomarFoto=findViewById(R.id.fButtonRegistrar);

        btnRegistar.setEnabled(false);

        CheckBox checkBox = findViewById(R.id.checkBoxTerminos);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    btnRegistar.setEnabled(true);
                } else {

                    btnRegistar.setEnabled(false);
                }
            }
        });

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogOpciones();
            }
        });
        txtPoliticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAboutDialog();
            }
        });
        txtIniciaSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transsitionBack();
            }
        });
        btnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valida()) {
                    if (registrarUsuario()) {
                        mostarToast("Registro exitoso", 1, true);
                    } else {
                        mostarToast("Error al agregar al usuario", 3, true);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case COD_SELECCIONA:
                if (data != null) {
                    Uri miPath = data.getData();
                    imgFoto.setImageURI(miPath);
                    /*String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContext().getContentResolver().query(miPath, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String paths = cursor.getString(columnIndex);
                    cursor.close();
                    img = paths;*/
                    photoURI = miPath;
                    cargaArchivo();
                    // bitmap = BitmapFactory.decodeFile(path);
                    //  imgProducto.setImageBitmap(bitmap);
                }
                break;

            case COD_FOTO:
             /*   if (requestCode == COD_FOTO && resultCode == Activity.RESULT_OK && data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "IMG_" + System.currentTimeMillis(), null);
                    photoURI = Uri.parse(path);
                    imgFoto.setImageURI(photoURI);

                    cargaArchivo();
                }*/
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
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmapNuevo, tituliImge, "Foto de la app de mascotas");
                    //muestra la imagen
                    //  photoURI = Uri.parse(path);
                    imgFoto.setImageBitmap(imageBitmapNuevo);
                    photoURI = Uri.parse(path);
                    cargaArchivo();
                }
                break;

        }
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

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private boolean registrarUsuario() {
        boolean exitoRegistro = true;

        mAuth.createUserWithEmailAndPassword(editEmail.getText().toString().trim(), editPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                           @Override
                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                               if (task.isSuccessful()) {
                                                   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                   Usuarios usuario = new Usuarios();
                                                   //    usuario.setUid(UUID.randomUUID().toString());
                                                   usuario.setUid(user.getUid());
                                                   usuario.setNombre(editNombre.getText().toString().trim());
                                                   usuario.setApellido(editApellido.getText().toString().trim());
                                                   usuario.setEmail(editEmail.getText().toString().trim());
                                                   usuario.setTelefono(editTelefono.getText().toString().trim());
                                                   usuario.setContrasenia(editPassword.getText().toString().trim());
                                                   usuario.setImagen(img);
                                                   databaseReference.child("Usuarios").child(usuario.getUid()).setValue(usuario).addOnSuccessListener(
                                                           new OnSuccessListener<Void>() {
                                                               @Override
                                                               public void onSuccess(Void unused) {
                                                                   Intent intent = new Intent(RegistrarseActivity.this, MascotasActivity.class);
                                                                   startActivity(intent);
                                                                   finish();
                                                               }
                                                           }
                                                   );


                                               } else {

                                                   mostarToast("Ups! Ha ocurrido un error al registrar. Intentelo de nuevo", 3, true);
                                               }
                                           }


                                       }
                );

        return exitoRegistro;
    }

    private boolean valida() {
        String nombre = editNombre.getText().toString().trim();
        String apellido = editApellido.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        boolean valicion = true;

        if (nombre.isEmpty()) {
            editNombre.setError("Nombre no válido");
            valicion = false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Correo invalido");
            valicion = false;
        }
        if (password.isEmpty() || password.length() < 8) {
            editPassword.setError("Se necesitan más de 8 caracteres");
            valicion = false;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            editPassword.setError("Se necesita por lo menos un número");
            valicion = false;
        } else if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            editPassword.setError("Se necesita por lo menos una letra mayuscula");
            valicion = false;
        } else {
            if (!password.equals(confirmPassword)) {
                editConfirmPassword.setError("Las contraseñas no son iguales");
                valicion = false;
            }
        }

        return valicion;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        transsitionBack();
    }

    public void mostrarDialogOpciones() {
        CharSequence[] opciones = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, COD_FOTO);
            }
        } else {
            ActivityCompat.requestPermissions(
                    RegistrarseActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA
            );
        }
    }

    public void transsitionBack() {
        Intent intent = new Intent(RegistrarseActivity.this, LogginActivity.class);
        /*Pair[] pairs=new Pair[2];
        pairs[0]=new Pair<View,String>(imgLogo,"logoImageTrans");
        pairs[1]=new Pair<View,String>(txtNuevaCuenta,"textTrans");
        /*pairs[2]=new Pair<View,String>(editNombre,"nombreTrans");
        pairs[3]=new Pair<View,String>(editApellido,"apellidoTrans");
        pairs[4]=new Pair<View,String>(editEmail,"emailTrans");
        pairs[5]=new Pair<View,String>(editPassword,"passTrans");
        pairs[6]=new Pair<View,String>(editConfirmPassword,"confirmarPassTrans");
        pairs[7]=new Pair<View,String>(btnRegistar,"buttonTrans");
        pairs[8]=new Pair<View,String>(txtIniciaSession,"iniciaSessionTrans");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(RegistrarseActivity.this,pairs);
            startActivity(intent,options.toBundle());

        }else{
            startActivity(intent);

        }*/
        startActivity(intent);
        finish();

    }

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnPopCerrar;

    public void createNewAboutDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View aboutPop = getLayoutInflater().inflate(R.layout.pop_politicas, null);
        btnPopCerrar = (Button) aboutPop.findViewById(R.id.btnPopCerrar);
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

    private void cargaArchivo() {
        img = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "." + extension(photoURI);
        ///////
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                StorageReference ref = storageReference.child(img);
                ref.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mostarToast("Imagen cargada exitosamente", 1, true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostarToast("Algo salio mal al cargar el archivo\n" + e.getCause() + "", 3, true);
                    }
                });
            } else {
                StorageReference ref = storageReference.child(img);
                ref.putFile(photoURI);
                mostarToast("Imagen cargada exitosamente\nEstado: sin conexión", 4, true);
            }
        } catch (Exception e) {

        }

//////

    }

    private String extension(Uri photoUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(photoUri));
    }

    public void mostarToast(String txt, int estatus, boolean corto) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = null;

        if (estatus == 0) {
            //Default
            layout = inflater.inflate(R.layout.custom_toast_info,
                    (ViewGroup) findViewById(R.id.layout_base));

        } else if (estatus == 1) {
            //Success
            layout = inflater.inflate(R.layout.custom_toast_success,
                    (ViewGroup) findViewById(R.id.layout_base));

        } else if (estatus == 2) {
            //Warning
            layout = inflater.inflate(R.layout.custom_toast_warning,
                    (ViewGroup) findViewById(R.id.layout_base));

        } else if (estatus == 3) {
            //Error
            layout = inflater.inflate(R.layout.custom_toast_error,
                    (ViewGroup) findViewById(R.id.layout_base));


        } else if (estatus == 4) {
            //Falla de red
            layout = inflater.inflate(R.layout.custom_toast_red,
                    (ViewGroup) findViewById(R.id.layout_base));


        } else if (estatus == 5) {
            //Falla de red
            layout = inflater.inflate(R.layout.custom_toast_sin_data,
                    (ViewGroup) findViewById(R.id.layout_base));


        } else {
            //Informacion
            layout = inflater.inflate(R.layout.custom_toast_info,
                    (ViewGroup) findViewById(R.id.layout_base));
        }

        TextView textView = layout.findViewById(R.id.txtToast);
        textView.setText(txt);
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 50);
        if (corto) {
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.setView(layout);
        toast.show();
    }
}