package com.programacion.fragments.ui.inicio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.programacion.fragments.HomeFragment;
import com.programacion.fragments.R;
import com.programacion.fragments.bd.Sql;
import com.programacion.fragments.bd.Sqlite;
import com.programacion.fragments.databinding.FragmentInicioBinding;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InicioFragment extends Fragment {

    public static VideoView vidWeb;
    private InicioModel inicioModel;
    private FragmentInicioBinding binding;

    ImageCarousel carousel;
    ImageCarousel carousel2;
    ImageCarousel carousel3;
    List<CarouselItem> list = new ArrayList<>();
    List<CarouselItem> list2 = new ArrayList<>();
    List<CarouselItem> list3 = new ArrayList<>();

    Button btn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        inicioModel =
                new ViewModelProvider(this).get(InicioModel.class);

        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        carousel = root.findViewById(R.id.carruselInicio);
        carousel2 = root.findViewById(R.id.carousel2);
        carousel3 = root.findViewById(R.id.carousel3);

        list2.add(new CarouselItem(R.drawable.cat_1));
        list2.add(new CarouselItem(R.drawable.veterinaria_8));
        list2.add(new CarouselItem(R.drawable.veterinaria_7));
        list2.add(new CarouselItem(R.drawable.veterinario_1));
        list2.add(new CarouselItem(R.drawable.veterinaria_10));

        carousel2.setData(list2);

        list3.add(new CarouselItem(R.drawable.cat_1));
        list3.add(new CarouselItem(R.drawable.veterinaria_8));
        list3.add(new CarouselItem(R.drawable.veterinaria_7));
        list3.add(new CarouselItem(R.drawable.veterinaria_9));
        list3.add(new CarouselItem(R.drawable.veterinaria_10));

        carousel3.setData(list3);


        list.add(new CarouselItem(R.drawable.veterinario_1));
        list.add(new CarouselItem(R.drawable.veterinaria_7));
        list.add(new CarouselItem(R.drawable.veterinaria_8));
        list.add(new CarouselItem(R.drawable.veterinaria_9));
        list.add(new CarouselItem(R.drawable.veterinaria_10));

        carousel.setData(list);

 /*       btn=binding.button;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Sqlite sqlite=new Sqlite(getContext());
                if (sqlite.abrirDB() != null) {

                    sqlite.cerrarDB();
                }
            }
        });*/

        vidWeb = binding.vidWeb;
        vidWeb.setVideoURI(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.vidma_recorder_09112021_222847));
        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(vidWeb);
        vidWeb.setMediaController(mediaController);
       // vidWeb.start();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}