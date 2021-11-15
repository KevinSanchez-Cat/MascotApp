package com.programacion.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.programacion.fragments.modelo.Usuarios;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class LogginActivity extends AppCompatActivity {

    ImageView imgLogo;
    TextView txtBienvenida;
    TextView txtRegistrase;
    TextView txtOlvidasteContrasenia;
    com.google.android.material.textfield.TextInputEditText editEmail;
    com.google.android.material.textfield.TextInputEditText editPassword;
    Button btnLoggin;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSigInClient;
    public static final int RC_SIGN_IN=0;
    private Uri photoURI;
    public static String img = "";
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggin);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Perfiles");
        mAuth = FirebaseAuth.getInstance();
        imgLogo=findViewById(R.id.imgLoggLogo);
        txtBienvenida=findViewById(R.id.txtLoggBienvida);
        txtRegistrase=findViewById(R.id.txtLogRegistrarse);
        txtOlvidasteContrasenia=findViewById(R.id.txtLogOlviContra);
        btnLoggin=findViewById(R.id.btnIniciarSession);
        editEmail=findViewById(R.id.editLogUsuario);
        editPassword=findViewById(R.id.editLogPass);

        txtRegistrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transsitionBack();
            }
        });
        btnLoggin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(valida()){
                  iniciarSession();
              }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSigInClient= GoogleSignIn.getClient(this,gso);
        signInButton=findViewById(R.id.logginGoogle);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sigInWithGoogle();
            }
        });

        txtOlvidasteContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LogginActivity.this,RecuperarPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
    public void sigInWithGoogle(){
        Intent sigInt=mGoogleSigInClient.getSignInIntent();
        startActivityForResult(sigInt,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e){
            //    mostarToast("Falló Google\n"+e.getCause(),3,true);
            }
        }
    }

    public void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                   @Override
                                   public void onComplete(@NonNull Task<AuthResult> task) {
                                       if(task.isSuccessful()){
                                           FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                         //  mostarToast("User "+user.getDisplayName(),0,true);
                                           Query queryUsuarios = databaseReference.child("Usuarios").orderByChild("email").equalTo(user.getEmail());
                                           queryUsuarios.addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                   if (snapshot.exists()) {
                                                       Intent intent = new Intent(LogginActivity.this, MascotasActivity.class);
                                                       startActivity(intent);
                                                       finish();
                                                   }else{

                                                       Usuarios usuario = new Usuarios();
                                                       //    usuario.setUid(UUID.randomUUID().toString());
                                                       usuario.setUid(user.getUid());
                                                       String nombre="";
                                                       String apellido="";
                                                       String phone="";
                                                       String imagen="";
                                                       if(user.getDisplayName()!=null){
                                                           String username[]=user.getDisplayName().split(" ");

                                                           if(username.length==0){
                                                               nombre=user.getEmail();
                                                           }else  if(username.length==1){
                                                               nombre=username[0];
                                                           }else{
                                                               nombre=username[0];
                                                               for (int i=1; i<username.length;i++){
                                                                   apellido+=username[i];
                                                               }
                                                           }
                                                       }else{
                                                           nombre=user.getEmail();
                                                       }
                                                       if(user.getPhoneNumber()!=null){
                                                           if(user.getPhoneNumber().isEmpty()){
                                                               phone="Sin telefono";
                                                           }else{
                                                               phone= user.getPhoneNumber();
                                                           }
                                                       }else{
                                                           phone="Sin telefono";
                                                       }
                                                       if(user.getPhotoUrl()!=null){
                                                           if(user.getPhotoUrl().toString().isEmpty()){
                                                               imagen="";
                                                           }else{
                                                               imagen=user.getPhotoUrl().toString();
                                                           }
                                                       }else{
                                                           imagen="";
                                                       }

                                                       usuario.setNombre(nombre);
                                                       usuario.setApellido(apellido);
                                                       usuario.setTelefono(phone);
                                                       usuario.setImagen(imagen);
                                                       usuario.setEmail(user.getEmail());
                                                       usuario.setContrasenia("Authenticacion por Google");
                                                       databaseReference.child("Usuarios").child(usuario.getUid()).setValue(usuario).addOnSuccessListener(
                                                               new OnSuccessListener<Void>() {
                                                                   @Override
                                                                   public void onSuccess(Void unused) {
                                                                       Intent intent = new Intent(LogginActivity.this, MascotasActivity.class);
                                                                       startActivity(intent);
                                                                       finish();
                                                                   }
                                                               }
                                                       );
                                                   }
                                               }

                                               @Override
                                               public void onCancelled(@NonNull DatabaseError error) {

                                               }
                                           });

                                       }else{
                                           mostarToast("Falló conexión con Google",3,true);

                                       }
                                   }
                               }

        );

    }


    private void iniciarSession() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LogginActivity.this, MascotasActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    mostarToast("Datos incorrectos",2,true);
                }
            }
        });

    }

    private boolean valida() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        boolean valicion = true;


        if (email.isEmpty()) {
            editEmail.setError("Se necesita llenar este campo");
            valicion = false;
        }
        if (password.isEmpty()) {
            editPassword.setError("Se necesita llenar este campo");
            valicion = false;
        }
        return valicion;
    }

    public void transsitionBack(){
        Intent intent=new Intent(LogginActivity.this,RegistrarseActivity.class);
       /* Pair[] pairs=new Pair[2];
        pairs[0]=new Pair<View,String>(imgLogo,"logoImageTrans");
        pairs[1]=new Pair<View,String>(txtBienvenida,"textTrans");

        /*pairs[2]=new Pair<View,String>(editEmail,"emailTrans");
        pairs[3]=new Pair<View,String>(editPassword,"passTrans");
        pairs[4]=new Pair<View,String>(btnLoggin,"buttonTrans");
        pairs[5]=new Pair<View,String>(txtRegistrase,"iniciaSessionTrans");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(LogginActivity.this,pairs);
            startActivity(intent,options.toBundle());
        }else{
            startActivity(intent);
        }*/
       startActivity(intent);
        finish();

    }

    private void cargaArchivo() {
        img = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date()) + "." + extension(photoURI);
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)LogginActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                StorageReference ref = storageReference.child(img);

                ref.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      //  mostarToast("La imagen se cargo exitosamente", 1, true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // mostarToast(e.getCause() + "", 3, true);
                    }
                });
            } else {
                StorageReference ref = storageReference.child(img);
                ref.putFile(photoURI);
               // mostarToast("La imagen se cargo exitosamente" + "\nEstado: sin conexión", 4, true);
            }
        }catch (Exception e){

        }


    }

    private String extension(Uri photoUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(photoUri));
    }
    public void mostarToast(String txt, int estatus, boolean corto) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = null;

        if(estatus==0){
            //Default
            layout = inflater.inflate(R.layout.custom_toast_info,
                    (ViewGroup) findViewById(R.id.layout_base));

        }else if(estatus==1){
            //Success
            layout = inflater.inflate(R.layout.custom_toast_success,
                    (ViewGroup) findViewById(R.id.layout_base));

        }else if(estatus==2) {
            //Warning
            layout = inflater.inflate(R.layout.custom_toast_warning,
                    (ViewGroup) findViewById(R.id.layout_base));

        }else if(estatus==3){
            //Error
            layout = inflater.inflate(R.layout.custom_toast_error,
                    (ViewGroup) findViewById(R.id.layout_base));


        }else if(estatus==4){
            //Falla de red
            layout = inflater.inflate(R.layout.custom_toast_red,
                    (ViewGroup) findViewById(R.id.layout_base));


        }else if(estatus==5){
            //Falla de red
            layout = inflater.inflate(R.layout.custom_toast_sin_data,
                    (ViewGroup) findViewById(R.id.layout_base));


        }else{
            //Informacion
            layout = inflater.inflate(R.layout.custom_toast_info,
                    (ViewGroup) findViewById(R.id.layout_base));
        }

        TextView textView = layout.findViewById(R.id.txtToast);
        textView.setText(txt);
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 50);
        if(corto){
            toast.setDuration(Toast.LENGTH_SHORT);}else{
            toast.setDuration(Toast.LENGTH_LONG);}
        toast.setView(layout);
        toast.show();
    }


}