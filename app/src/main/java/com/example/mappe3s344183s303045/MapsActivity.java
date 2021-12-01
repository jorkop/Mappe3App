package com.example.mappe3s344183s303045;

import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mappe3s344183s303045.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

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

    }


    private class getJSON extends AsyncTask<String, Void, JSONArray> {
        JSONObject jsonObject;

        @Override
        protected JSONArray doInBackground(String... urls) {
            String retur = "";
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
                            retur = retur + name + "\n";

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
                    JSONObject jsonobject = null;
                    jsonobject = ss.getJSONObject(i);
                    String name = jsonobject.getString("description");
                    String stories = jsonobject.getString("stories");

                    double lat = Double.parseDouble(jsonobject.getString("latitude"));
                    double lng = Double.parseDouble(jsonobject.getString("longitude"));
                    LatLng nyMarker = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(nyMarker)
                            .title(name)
                            .snippet("Stories: " + stories));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}