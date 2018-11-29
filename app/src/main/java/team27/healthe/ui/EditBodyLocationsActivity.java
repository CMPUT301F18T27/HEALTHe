package team27.healthe.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.model.ImageAdapter;
import team27.healthe.model.ImageController;

public class EditBodyLocationsActivity extends AppCompatActivity {
    ImageController ic;
    ImageAdapter image_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ic = new ImageController(getApplicationContext(), "app_body_locations");
        ArrayList<String> image_list = ic.getImageList();

        setContentView(R.layout.activity_edit_body_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.body_locations_list);
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
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, 1);
//            File image_file = ic.getImageFile(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
//            System.out.println("DEBUG---"+image_file.getAbsolutePath());
//            Uri uri = FileProvider.getUriForFile(this, "healthe.fileprovider", image_file);
//            System.out.println("URI--"+uri.getPath());
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent){
        if(request == REQUEST_IMAGE_CAPTURE && result == RESULT_OK){
            System.out.println("awdawdawdaw");
            Bundle bundle = intent.getExtras();
            Bitmap image = (Bitmap) bundle.get("data");
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            ic.saveImage(getApplicationContext(), image, ts);
            ic.refreshImageList();
            image_adapter.notifyDataSetChanged();

        }
    }
}
