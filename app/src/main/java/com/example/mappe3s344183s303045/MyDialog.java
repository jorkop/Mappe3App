package com.example.mappe3s344183s303045;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

public class MyDialog extends DialogFragment {

    private DialogClickListener callback;

    public interface DialogClickListener {
        public void onYesClick();
        public void onNoClick();

        void visDialog();
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        try{
            callback=(DialogClickListener)getActivity();
        }
        catch (ClassCastException e){
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

        return view;
    }

}