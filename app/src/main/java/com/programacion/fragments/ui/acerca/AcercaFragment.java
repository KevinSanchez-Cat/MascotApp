package com.programacion.fragments.ui.acerca;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.programacion.fragments.MainActivity;
import com.programacion.fragments.R;

public class AcercaFragment extends Fragment {

    private AcercaViewModel mViewModel;
    ImageView imgFace;
    ImageView imgInsta;
    ImageView imgGit;
    TextView txtVersion;

    public static AcercaFragment newInstance() {
        return new AcercaFragment();
    }

    private String urlF = "https://www.facebook.com/SKevinIvan/";
    private String urlI = "https://www.instagram.com/s.kevin_ivan/";
    private String urlG = "https://github.com/KevinSanchez-Cat";
    private String urlE = "https://www.facebook.com/SKevinIvan/";
    private String urlY = "https://www.youtube.com/channel/UCXQglh-ZfMMuWoFRtIrmKwg/featured";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_acerca, container, false);
        imgFace = root.findViewById(R.id.iBtnRedFace);
        imgInsta = root.findViewById(R.id.iBtnRedInsta);
        imgGit= root.findViewById(R.id.iBtnRedGit);
        txtVersion=root.findViewById(R.id.txtAcercaVersion);
        imgFace = root.findViewById(R.id.iBtnRedFace);
        imgFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(urlF);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        txtVersion.setText(MainActivity.version);
        imgInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(urlI);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        imgGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(urlG);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AcercaViewModel.class);
        // TODO: Use the ViewModel
    }

}