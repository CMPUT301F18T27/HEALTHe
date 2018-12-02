package team27.healthe.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.controllers.BodyLocationElasticSearchController;
import team27.healthe.controllers.BodyLocationListener;
import team27.healthe.model.BodyLocation;
import team27.healthe.controllers.BodyLocationImageAdapter;
import team27.healthe.controllers.ImageController;

public class ViewBodyLocationsActivity extends AppCompatActivity implements BodyLocationListener {
    ImageController ic;
    BodyLocationImageAdapter image_adapter;
    ArrayList<String> image_list;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_WRITE_EXTERNAL_REQUEST_CODE = 101;
    private static final int MY_READ_EXTERNAL_REQUEST_CODE = 102;
    String current_user;
    BodyLocationElasticSearchController blesc;
    @Override
    public void recyclerViewClicked(View v, int i){
        editLocation(image_list.get(i));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        blesc = new BodyLocationElasticSearchController();
        Intent intent = getIntent();

        current_user = "";
        if (intent.hasExtra("current_user")){
            current_user = intent.getStringExtra("current_user");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    | checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    | checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_CAMERA_REQUEST_CODE);
            }
        }
        if (intent.hasExtra("auto_photo")){
            if(intent.getBooleanExtra("auto_photo", false)){
                takePhoto();
            }
        }


        ic = new ImageController(getApplicationContext(), "body_locations");
        ic.refreshImageList(current_user);
        image_list = ic.getImageList();

        setContentView(R.layout.activity_edit_body_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.body_locations_list);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        image_adapter = new BodyLocationImageAdapter(getApplicationContext(), image_list, this);
        ic.setImageAdapter(image_adapter);
        recyclerView.setAdapter(image_adapter);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
//        recyclerView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                int index = recyclerView.indexOfChild(view);
//                editLocation(image_list.get(index));
//            }
//        });
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private class GetBodyLocationTask extends AsyncTask<String, Void, BodyLocation> {

        @Override
        protected BodyLocation doInBackground(String... body_location_ids) {
            BodyLocationElasticSearchController bles_controller =
                    new BodyLocationElasticSearchController();
            BodyLocation bl = null;
            for (String bl_id : body_location_ids) {
                System.out.println("Attempting to get body location for id: "+bl_id);
                File temp = new File(bl_id);
                bl = bles_controller.getBodyLocation(temp.getName());
            }
            return bl;
        }
        @Override
        protected void onPostExecute(BodyLocation bl){
            super.onPostExecute(null);

            launchEditLocation(bl);
        }
    }

    private void launchEditLocation(BodyLocation bl){
        Intent select_location = new Intent(this,SelectBodyLocationActivity.class);

        if (bl != null){
            select_location.putExtra("x_loc", bl.getX());
            select_location.putExtra("y_loc", bl.getY());
            select_location.putExtra("file_name",ic.getAbsolutePath(bl.getBodyLocationId()));
        }
        else {
            System.out.println("ERROR--body location was null from elasticsearch");
        }


        select_location.putExtra("current_user", current_user);
        startActivity(select_location);
    }

    private void editLocation(String file_name){
        new GetBodyLocationTask().execute(file_name);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent){
        if(request == REQUEST_IMAGE_CAPTURE && result == RESULT_OK){
            Bundle bundle = intent.getExtras();
            Bitmap image = (Bitmap) bundle.get("data");
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String file_name = ic.saveImage(image, ts);
            Intent select_location = new Intent(this,SelectBodyLocationActivity.class);
            select_location.putExtra("file_name",file_name);
            select_location.putExtra("current_user", current_user);
            startActivity(select_location);
            ic.refreshImageList(current_user);

            image_adapter.notifyDataSetChanged();

        }
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }
        }
    }
}
