package com.mioai.gamehub;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.util.StringTokenizer;

public class AvatarGenerator extends Fragment
{

    ImageView background;
    ImageView eyes;
    ImageView mouth;
    ImageView nose;
    Spinner spinner_eye;
    Spinner spinner_mouth;
    Spinner spinner_nose;
    Spinner spinner_body;
    private int currentMouth;
    private int currentEyes;
    private int currentNose;
    private int currentBody;
    Button saveButton;


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

        saveButton = view.findViewById(R.id.save_Avatar);
        //Setting up the Spinner
        spinner_mouth = view.findViewById(R.id.spinnerMouths);
        spinner_eye = view.findViewById(R.id.spinnerEyes);
        spinner_nose = view.findViewById(R.id.spinnerNoses);
        spinner_body = view.findViewById(R.id.spinnerBody);

        ArrayAdapter<CharSequence> eyeAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.avatar_eyes,android.R.layout.simple_spinner_item);
        eyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_eye.setAdapter(eyeAdapter);

        ArrayAdapter<CharSequence> mouthAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.avatar_mouths,android.R.layout.simple_spinner_item);
        mouthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_mouth.setAdapter(mouthAdapter);

        ArrayAdapter<CharSequence> bgAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.avatar_body,android.R.layout.simple_spinner_item);
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_body.setAdapter(bgAdapter);

        ArrayAdapter<CharSequence> noseAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.avatar_nose,android.R.layout.simple_spinner_item);
        noseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nose.setAdapter(noseAdapter);



        SharedPreferences shared = getActivity().getPreferences(Context.MODE_PRIVATE);


        String Avatar_Key = "";

        Avatar_Key = shared.getString("Avatar_Key","");
        if(Avatar_Key.equals(""))
        {
            currentMouth = 0;
            currentEyes = 0;
            currentBody = 0;
            currentNose = 0;
        }
        else
        {
            StringTokenizer st = new StringTokenizer(Avatar_Key);

            currentEyes = Integer.parseInt(st.nextToken("."));
            currentMouth = Integer.parseInt(st.nextToken("."));
            currentNose= Integer.parseInt(st.nextToken("."));
            currentBody = Integer.parseInt(st.nextToken("."));
        }


        spinner_mouth.setSelection(currentMouth);
        spinner_eye.setSelection(currentEyes);
        spinner_body.setSelection(currentBody);
        spinner_nose.setSelection(currentNose);



        spinner_mouth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                switch(item) {
                    case ":P":
                        mouth.setImageResource(R.drawable.avatar_mouths_p);
                        currentMouth=position;
                        break;
                    case ":O":
                        mouth.setImageResource(R.drawable.avatar_mouths_o);
                        currentMouth=position;
                        break;
                    case "Sad":
                        mouth.setImageResource(R.drawable.avatar_mouths_sad);
                        currentMouth=position;
                        break;
                    case "Smile":
                        mouth.setImageResource(R.drawable.avatar_mouths_smile);
                        currentMouth=position;
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
                        currentEyes=position;
                        eyes.setImageResource(R.drawable.avatar_eyes_alien);
                        break;
                    case "Angry":
                        currentEyes=position;
                        eyes.setImageResource(R.drawable.avatar_eyes_angry);
                        break;
                    case "Shocked":
                        currentEyes=position;
                        eyes.setImageResource(R.drawable.avatar_eyes_shocked);
                        break;
                    case "Sly":
                        currentEyes=position;
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

        spinner_body.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                switch(item) {
                    case "Realistic":
                        currentBody=position;
                        background.setImageResource(R.drawable.avatar_body_real);
                        break;
                    case "Round":
                        currentBody=position;
                        background.setImageResource(R.drawable.avatar_body_round);
                        break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                background.setImageResource(android.R.color.transparent);
            }
        });

        spinner_nose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                switch(item) {
                    case "Nose":
                        currentNose=position;
                        nose.setImageResource(R.drawable.avatar_nose);
                        break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                nose.setImageResource(android.R.color.transparent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor edit = shared.edit();
                edit.putString("Avatar_Key",currentEyes+"."+currentMouth+"."+currentNose+"."+currentBody);
                edit.commit();
            }
        });



        return view;
    }

}
