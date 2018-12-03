package team27.healthe.ui;

// Activity to display all records containing a geo location on a map

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.GeoLocation;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class AllGeoLocationsActivity extends AppCompatActivity implements OnMapReadyCallback  {
    public static final String PROBLEMS_MESSAGE = "team27.healthe.PROBLEMS";
    private MapView mapView;
    private GoogleMap map;
    private User current_user;
    private ArrayList<Record> records;
    private ArrayList<Marker> markers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_geo_locations);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mapViewAll);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);

        getItems(getIntent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        map.setMyLocationEnabled(true);
                    }
                }
            }
        }
    }

    // Get all items passed with the intent
    private void getItems(Intent intent) {
        UserElasticSearchController es_controller = new UserElasticSearchController();
        Gson gson = new Gson();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String problems_json = intent.getStringExtra(PROBLEMS_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);
        ArrayList<Problem> problems = gson.fromJson(problems_json, new TypeToken<ArrayList<Problem>>(){}.getType());

        ArrayList<String> record_ids = new ArrayList<>();
        for (Problem problem: problems) {
            for (String record_id:problem.getRecords()) {
                record_ids.add(record_id);
            }
        }

        new getRecordsAsync().execute(record_ids);
    }

    // Async class for getting records from elastic search server
    private class getRecordsAsync extends AsyncTask<ArrayList<String>, Void, ArrayList<Record>> {

        @Override
        protected ArrayList<Record> doInBackground(ArrayList<String>... record_id_list) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();

            for (ArrayList<String>record_ids : record_id_list) {
                ArrayList<Record> es_records = new ArrayList<>();
                for (String record_id: record_ids) {
                    Record temp_record = es_controller.getRecord(record_id);
                    if (temp_record != null) {
                        es_records.add(temp_record);
                    }
                }
                return es_records;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Record> es_records) {
            super.onPostExecute(es_records);
            records = es_records;
            setMarkers();
        }
    }

    // Place all markers on map
    private void setMarkers() {
        boolean set_camera = false;
        markers = new ArrayList<>();
        LatLngBounds.Builder latlng_builder = new LatLngBounds.Builder();
        for (Record record: records) {
            GeoLocation geo_loc = record.getGeoLocation();
            if (geo_loc != null) {
                if (geo_loc.getLatLng() != null) {
                    Marker marker = map.addMarker(new MarkerOptions().position(geo_loc.getLatLng()).title(record.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    markers.add(marker);
                    latlng_builder.include(marker.getPosition());
                    set_camera = true;
                }
            }
        }
        if (set_camera) {
            LatLngBounds bounds = latlng_builder.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

}
