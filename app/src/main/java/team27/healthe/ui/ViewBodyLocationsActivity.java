package team27.healthe.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.controllers.BodyLocationElasticSearchController;
import team27.healthe.model.BodyLocation;
import team27.healthe.model.ImageAdapter;
import team27.healthe.model.ImageController;

public class ViewBodyLocationsActivity extends AppCompatActivity {
    ImageController ic;
    ImageAdapter image_adapter;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_WRITE_EXTERNAL_REQUEST_CODE = 101;
    private static final int MY_READ_EXTERNAL_REQUEST_CODE = 102;
    String current_user;
    BodyLocationElasticSearchController blesc;
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


        ic = new ImageController(getApplicationContext(), "body_locations");
        ic.refreshImageList(current_user);
        final ArrayList<String> image_list = ic.getImageList();

        setContentView(R.layout.activity_edit_body_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.body_locations_list);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
//        ArrayList<CreateList> createLists = prepareData();
        image_adapter = new ImageAdapter(getApplicationContext(), image_list);
        recyclerView.setAdapter(image_adapter);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        recyclerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int index = recyclerView.indexOfChild(view);
                editLocation(image_list.get(index));
            }
        });
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void editLocation(String file_name){
        Intent select_location = new Intent(this,SelectBodyLocationActivity.class);
        BodyLocation bl = blesc.getBodyLocation(file_name);
        if (bl != null){
            select_location.putExtra("x_loc", bl.getX());
            select_location.putExtra("y_loc", bl.getY());
        }
        else {
            System.out.println("ERROR--body location was null from elasticsearch");
        }

        select_location.putExtra("file_name",file_name);
        select_location.putExtra("current_user", current_user);
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
//            Bundle b = new Bundle();
//            b.putExtras
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
//        if (requestCode == MY_WRITE_EXTERNAL_REQUEST_CODE) {
//
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                Toast.makeText(this, "write permission granted", Toast.LENGTH_LONG).show();
//
//            } else {
//
//                Toast.makeText(this, "write permission denied", Toast.LENGTH_LONG).show();
//
//            }
//        }
//        if (requestCode == MY_READ_EXTERNAL_REQUEST_CODE) {
//
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                Toast.makeText(this, "read permission granted", Toast.LENGTH_LONG).show();
//
//            } else {
//
//                Toast.makeText(this, "read permission denied", Toast.LENGTH_LONG).show();
//
//            }
//        }
    }
}
