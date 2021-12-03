package com.example.mappe3s344183s303045;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;


public class MyDialog extends DialogFragment {

    private DialogClickListener callback;

    public interface DialogClickListener {
        public void lagreInfo();

        public void sletthus();

        void visDialog(String id, String besk, String etasj, String adress, String lng, String lat);
    }

    DialogClickListener listener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (DialogClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Kallende klasse må implementere interfacet!");
        }

    }


/*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle("Hei").setPositiveButton("Hei",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton){
                        callback.onYesClick();
                    }
                }).setNegativeButton("ikkehei",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int whichButton)
                    { callback.onNoClick();
                    }}).create();
    }

 */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        String idinn = getArguments().getString("id");
        String etasjer = getArguments().getString("etasj");
        String beskrivelse = getArguments().getString("besk");
        String addresse = getArguments().getString("adress");
        String lng = getArguments().getString("lng");
        String lat = getArguments().getString("lat");

        EditText et = view.findViewById(R.id.etasjer);
        EditText bes = view.findViewById(R.id.beskrivelse);
        EditText adresse = view.findViewById(R.id.adresse);
        LinearLayout lagre = (LinearLayout) view.findViewById(R.id.lagre);
        LinearLayout slett = (LinearLayout) view.findViewById(R.id.slett);

        if (!idinn.equals("")) {
            et.setText(etasjer);
            bes.setText(beskrivelse);
            adresse.setText(addresse);
        }

        lagre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (et.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Skriv inn etasjer", Toast.LENGTH_SHORT).show();
                } else if (bes.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Skriv inn beskrivelse", Toast.LENGTH_SHORT).show();
                } else if (adresse.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Skriv inn adresse", Toast.LENGTH_SHORT).show();
                } else {

                    ((MapsActivity)getActivity()).slett(idinn);

                    ((MapsActivity)getActivity()).lagre(bes.getText().toString(),et.getText().toString(), adresse.getText().toString(),lng,lat);
                    getDialog().dismiss();
                    Toast.makeText(getContext(), "Lagret hus!!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        slett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapsActivity)getActivity()).slett(idinn);
                getDialog().dismiss();
                Toast.makeText(getContext(), "Slettet hus!", Toast.LENGTH_SHORT).show();
            }

        });


        return view;
    }
}