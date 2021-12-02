package com.example.mappe3s344183s303045;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mappe3s344183s303045.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MyDialog.DialogClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public JSONArray jsonarr = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Zoomer inn på pilestredet.
        float zoom = 16.0f; // Høyere tall = mer zoomet inn. Mellom 2 og 21.
        LatLng pilestredet = new LatLng(59.920984, 10.733447);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pilestredet, zoom));

        //Kjører all kode nederst i getJSON
        getJSON task = new getJSON();
        task.execute("http://studdata.cs.oslomet.no/~dbuser14/houseout.php");



        //Sjekker når kartet blir trykket på og oppretter en markør
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

                markerOptions.title("Lat: " + latLng.latitude + ". Long: " + latLng.longitude);

                mMap.clear(); //Denne fjerner alle markører på kartet

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                mMap.addMarker(markerOptions);


                //Kjører all kode i getJSON. Kjøres hver gang bruker trykker på kartet
                //Bør finne en annen løsning på dette?
                getJSON task = new getJSON();
                task.execute("http://studdata.cs.oslomet.no/~dbuser14/houseout.php");

            }
        });


        //Sjekker når markør blir trykket på
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.getTag() == null) {
                    visDialog();

                } else {

                    String s = marker.getTag().toString();
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    String id = marker.getTag().toString();
                    for (int i = 0; i < jsonarr.length(); i++) {
                        try {
                            JSONObject j = jsonarr.getJSONObject(i);
                            String l = j.get("id").toString();
                            if (l.equals(id)) {
                                System.out.println(j.get("id").toString() + " YEEES ");
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Feil i markerclick", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onYesClick() {

    }

    @Override
    public void onNoClick() {

    }

    @Override
    public void visDialog() {
        DialogFragment dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),"Avslutt");
    }


    private class getJSON extends AsyncTask<String, Void, JSONArray> {
        JSONObject jsonObject;

        @Override
        protected JSONArray doInBackground(String... urls) {
            String s = "";
            String output = "";
            JSONArray arr = new JSONArray();

            for (String url : urls) {
                try {
                    URL urlen = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {
                        JSONArray mat = new JSONArray(output);
                        for (int i = 0; i < mat.length(); i++) {
                            JSONObject jsonobject = mat.getJSONObject(i);
                            String name = jsonobject.getString("description");

                        }
                        arr = mat;
                        return mat;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return arr;
                } catch (Exception e) {
                    System.out.println(e);
                    return null;
                }
            }
            return arr;
        }

        @Override
        protected void onPostExecute(JSONArray ss) {


            try {
                for (int i = 0; i < ss.length(); i++) {
                    Marker marker;
                    JSONObject jsonobject = null;
                    jsonobject = ss.getJSONObject(i);
                    String name = jsonobject.getString("description");
                    String stories = jsonobject.getString("stories");
                    String address = jsonobject.getString("address");
                    String id = jsonobject.getString("id");

                    double lat = Double.parseDouble(jsonobject.getString("latitude"));
                    double lng = Double.parseDouble(jsonobject.getString("longitude"));
                    LatLng nyMarker = new LatLng(lat, lng);
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(nyMarker)
                            .title(name)
                            .snippet("Stories: " + stories + "\n" +
                                    "Address: " + address + "\n" +
                                    "Address: " + address));
                    marker.setTag(id);




                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                while (jsonarr.length() > 0){
                    jsonarr.remove(0);
                }
                for (int i = 0; i < ss.length(); i++) {
                    JSONObject jsonobject;
                    jsonobject = ss.getJSONObject(i);
                    jsonarr.put(jsonobject);
                    System.out.println(jsonarr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}