package com.example.mappe3s344183s303045;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MyDialog.DialogClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public JSONArray jsonarr = new JSONArray();
    String longitude, latitude, beskrivelse, etasje, adresse, innlong, innlat;



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


        // Zoomer inn p?? pilestredet.
        float zoom = 16.0f; // H??yere tall = mer zoomet inn. Mellom 2 og 21.
        LatLng pilestredet = new LatLng(59.920984, 10.733447);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pilestredet, zoom));

        //Kj??rer all kode nederst i getJSON
        getJSON task = new getJSON();
        task.execute("http://studdata.cs.oslomet.no/~dbuser14/houseout.php");


        //Sjekker n??r kartet blir trykket p?? og oppretter en mark??r
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

               String result = addresseSjekk(latLng.latitude,latLng.longitude);

                markerOptions.title(result);


                //markerOptions.title("Lat: " + latLng.latitude + ". Long: " + latLng.longitude);
                if (!result.equals("null null")) {
                    mMap.clear(); //Denne fjerner alle mark??rer p?? kartet

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    mMap.addMarker(markerOptions);

                    //Kj??rer all kode i getJSON. Kj??res hver gang bruker trykker p?? kartet
                    //B??r finne en annen l??sning p?? dette?
                    getJSON task = new getJSON();
                    task.execute("http://studdata.cs.oslomet.no/~dbuser14/houseout.php");
                } else {
                    Toast.makeText(getApplicationContext(), "Ikke gyldig adresse. \n Pr??v igjen", Toast.LENGTH_SHORT).show();
                }

            }




        });


        //Sjekker n??r mark??r blir trykket p??
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.getTag() != null) {
                    String s = marker.getTag().toString();

                    String id = marker.getTag().toString();
                    for (int i = 0; i < jsonarr.length(); i++) {
                        try {
                            JSONObject j = jsonarr.getJSONObject(i);
                            String l = j.get("id").toString();
                            if (l.equals(id)) {
                                System.out.println(j.get("id").toString() + " YEEES ");
                                visDialog(s, (j.get("description")).toString(), (j.get("stories")).toString(), (j.get("address")).toString(), (j.get("longitude")).toString(), (j.get("latitude")).toString());
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Feil i markerclick", Toast.LENGTH_SHORT).show();
                        }


                    }
                } else {
                    Double dlng = marker.getPosition().longitude;
                    String lng = String.valueOf(dlng);
                    Double dlat = marker.getPosition().latitude;
                    String lat = String.valueOf(dlat);

                    String adresse = addresseSjekk(dlat,dlng);

                    if (!adresse.equals("null null")) {
                        visDialog("", "", "", adresse, lng, lat);
                    } else {
                        Toast.makeText(getApplicationContext(), "idiot", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });
    }

    public String addresseSjekk(Double latitude, Double longitude) {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getThoroughfare()).append(" ").append(address.getSubThoroughfare());
                result = sb.toString();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            Log.e("Feil", "Unable connect to Geocoder", e);
        }
        return result;

    }



    @Override
    public void lagreInfo() {
        /*
        if ((findViewById(R.id.etasjer).toString()).equals("Hallo")){
            Toast.makeText(getApplicationContext(), "Jada!", Toast.LENGTH_SHORT).show();
        }

         */
    }

    public void sletthus() {

    }


    @Override
    public void visDialog(String id, String besk, String etasj, String adress, String lng, String lat) {

        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("besk", besk);
        bundle.putString("etasj", etasj);
        bundle.putString("adress", adress);
        bundle.putString("lng", lng);
        bundle.putString("lat", lat);

        DialogFragment dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), "Avslutt");
        dialog.setArguments(bundle);


    }

    public void slett(String i) {
        putInfo task = new putInfo();
        task.execute("http://studdata.cs.oslomet.no/~dbuser14/housedelete.php/?id=" + i);
        mMap.clear();
        getJSON task2 = new getJSON();
        task2.execute("http://studdata.cs.oslomet.no/~dbuser14/houseout.php");
    }

    public void lagre(String besk, String etasj, String adress, String lng, String lat){
        GetLocationTask lokasjon = new GetLocationTask(adress);
        lokasjon.execute();

        beskrivelse = besk;
        etasje = etasj;
        adresse = adress;
        innlong = lng;
        innlat = lat;





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

                    try {
                        double lat = Double.parseDouble(jsonobject.getString("latitude"));
                        double lng = Double.parseDouble(jsonobject.getString("longitude"));

                    LatLng nyMarker = new LatLng(lat, lng);
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(nyMarker)
                            .title(address)
                            .snippet("Endre info..."));
                    marker.setTag(id);
                    }catch(Exception e ){
                        System.out.println("Feil: " + e);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                while (jsonarr.length() > 0) {
                    jsonarr.remove(0);
                }
                for (int i = 0; i < ss.length(); i++) {
                    JSONObject jsonobject;
                    jsonobject = ss.getJSONObject(i);
                    jsonarr.put(jsonobject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private class putInfo extends AsyncTask<String, Void, JSONArray> {
        JSONObject jsonObject;

        @Override
        protected JSONArray doInBackground(String... urls) {
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
                    conn.disconnect();
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

        }
    }


    private class GetLocationTask extends AsyncTask<Void, Void, Double[]> {
        JSONObject jsonObject;
        String address;
        Double[] lokasjon = new Double[2];

        public GetLocationTask(String a) {
            this.address = a;
        }

        @Override
        protected Double[] doInBackground(Void... params) {
            String s = "";
            String output = "";
            String query = "https://maps.googleapis.com/maps/api/geocode/json?address="+ address.replaceAll(" ", "%20") + "";
            try {
                URL urlen = new URL(query);
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
                jsonObject = new JSONObject(output.toString());
                conn.disconnect();
                Double lon = Double.valueOf(0);
                Double lat = Double.valueOf(0);
                lon =((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lokasjon = new Double[]{lon, lat};

                return lokasjon;
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
            return lokasjon;
        }

        @Override
        protected void onPostExecute(Double[] resultat) {
            longitude = resultat[0].toString();
            latitude = resultat[1].toString();

            putInfo task = new putInfo();

            if (innlong.equals(longitude) && innlat.equals(latitude)) {
                String a = "http://studdata.cs.oslomet.no/~dbuser14/housein.php/?description=" + beskrivelse +
                        "&stories=" + etasje + "&address=" + adresse + "&longitude=" + innlong + "&latitude=" + innlat;
                String s = a.replaceAll(" ", "%20");
                task.execute(s);
                mMap.clear();
                getJSON task2 = new getJSON();
                task2.execute("http://studdata.cs.oslomet.no/~dbuser14/houseout.php");
            } else {
                String a = "http://studdata.cs.oslomet.no/~dbuser14/housein.php/?description=" + beskrivelse +
                        "&stories=" + etasje + "&address=" + adresse + "&longitude=" + longitude + "&latitude=" + latitude;
                String s = a.replaceAll(" ", "%20");
                task.execute(s);
                mMap.clear();
                getJSON task2 = new getJSON();
                task2.execute("http://studdata.cs.oslomet.no/~dbuser14/houseout.php");
            }


        }
    }
}
