package com.mioai.gamehub;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

public class AvatarGenerator extends Fragment
{

    ImageView background;
    ImageView eyes;
    ImageView mouth;
    ImageView nose;
    Spinner spinner_eye;
    Spinner spinner_mouth;
    Spinner spinner_nose;
    Spinner spinner_background;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        //Init the view and the layouts
        View view = inflater.inflate(R.layout.avatar_generator, container, false);
        background= view.findViewById(R.id.avBody);
        eyes=view.findViewById(R.id.avEyes);
        mouth= view.findViewById(R.id.avMouth);
        nose = view.findViewById(R.id.avNose);

        //Setting up the Spinner
        spinner_mouth = view.findViewById(R.id.spinnerMouths);
        spinner_eye = view.findViewById(R.id.spinnerEyes);
        //spinner_nose = view.findViewById(R.id.spinnerNoses);
        //spinner_background = view.findViewById(R.id.spinnerBackgrounds);

        ArrayAdapter<CharSequence> eyeAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.avatar_eyes,android.R.layout.simple_spinner_item);
        eyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_eye.setAdapter(eyeAdapter);

        ArrayAdapter<CharSequence> mouthAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.avatar_mouths,android.R.layout.simple_spinner_item);
        mouthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_mouth.setAdapter(mouthAdapter);

        spinner_mouth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                switch(item) {
                    case ":P":
                        mouth.setImageResource(R.drawable.avatar_mouths_p);
                        break;
                    case ":O":
                        mouth.setImageResource(R.drawable.avatar_mouths_o);
                        break;
                    case "Sad":
                        mouth.setImageResource(R.drawable.avatar_mouths_sad);
                        break;
                    case "Smile":
                        mouth.setImageResource(R.drawable.avatar_mouths_smile);
                        break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                mouth.setImageResource(android.R.color.transparent);
            }
        });

        spinner_eye.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                switch(item) {
                    case "Alien":
                        eyes.setImageResource(R.drawable.avatar_eyes_alien);
                        break;
                    case "Angry":
                        eyes.setImageResource(R.drawable.avatar_eyes_angry);
                        break;
                    case "Shocked":
                        eyes.setImageResource(R.drawable.avatar_eyes_shocked);
                        break;
                    case "Sly":
                        eyes.setImageResource(R.drawable.avatar_eyes_sly);
                        break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                eyes.setImageResource(android.R.color.transparent);
            }
        });



        /*
        ArrayAdapter<CharSequence> bgAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.avatar_background_names,android.R.layout.simple_spinner_item);
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_background.setAdapter(bgAdapter);

        ArrayAdapter<CharSequence> bgAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.avatar_background_names,android.R.layout.simple_spinner_item);
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_background.setAdapter(bgAdapter);
        */




        //background.setImageResource();

        //view.findViewById(R.id.rvIcons1).setOnClickListener(c);
        //recAdapter.setClickListener(this);


        return view;
    }



}
