package com.javadoh.toptestquiz.activities.pruebas;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by luiseliberal on 05-07-2015.
 */
public class FragmentosPrueba extends Fragment {

        private static final String IMAGEN_PREGUNTA = "imagenPregunta";
        private static final String TEXTO_PREGUNTA = "textoPregunta";
        private int pregunta;

        public static FragmentosPrueba newInstance(int pregunta) {
            FragmentosPrueba fragment = new FragmentosPrueba();
            Bundle args = new Bundle();
            args.putInt(IMAGEN_PREGUNTA, pregunta);
            args.putString(TEXTO_PREGUNTA, "");
            fragment.setArguments(args);
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(getArguments() != null) {
                pregunta = getArguments().getInt(IMAGEN_PREGUNTA);
            }
        }

        public FragmentosPrueba() {
        }

    /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);

            ImageView imagenView = (ImageView) rootView.findViewById(R.id.imageView1);
            imagenView.setImageResource(imagen);
            return rootView;
        }
        */
    }
