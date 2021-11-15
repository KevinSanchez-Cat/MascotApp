package com.programacion.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public  static  String version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            PackageInfo info=getPackageManager().getPackageInfo(getPackageName(), 0);
            version= String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //Para mostrar en toda la pantalla
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);
        ImageView logo = findViewById(R.id.imgVMainLogo);

        TextView title = findViewById(R.id.txtMainTitle);
        title.setAnimation(animation2);
        logo.setAnimation(animation1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                if (user != null&& account!=null) {
                    Intent intent = new Intent(MainActivity.this, MascotasActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(MainActivity.this, LogginActivity.class);
              /*  Pair[] pairs=new Pair[1];
                pairs[0]=new Pair<View,String>(logo,"logoImageTrans");
                pairs[1]=new Pair<View,String>(title,"textTrans");

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                }else{
                    startActivity(intent);
                }*/
                    startActivity(intent);

                    //startActivity(intent);
                }
                finish();


            }
        }, 3000);

    }
}