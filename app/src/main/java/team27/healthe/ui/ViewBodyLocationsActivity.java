package team27.healthe.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.controllers.BodyLocationPhotoElasticSearchController;
import team27.healthe.controllers.BodyLocationListener;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.BodyLocation;
import team27.healthe.controllers.BodyLocationImageAdapter;
import team27.healthe.controllers.ImageController;
import team27.healthe.model.BodyLocationPhoto;
import team27.healthe.model.Patient;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class ViewBodyLocationsActivity extends AppCompatActivity implements BodyLocationListener {
    ImageController ic;
    BodyLocationImageAdapter image_adapter;
//    ArrayList<String> image_list;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_WRITE_EXTERNAL_REQUEST_CODE = 101;
    private static final int MY_READ_EXTERNAL_REQUEST_CODE = 102;
    String current_user;
    BodyLocationPhotoElasticSearchController blesc;
    BodyLocationPhoto blp;
    boolean new_body_location;
    boolean auto_photo;
    String record_id;
    Record record;
    @Override
    public void recyclerViewClicked(View v, int i){
        File tmp = new File(ic.get(i));
        editLocation(tmp.getName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivity();
        checkPermissions();

        if(auto_photo){
            takePhoto(new_body_location);
        }

        ic = new ImageController(getApplicationContext(), "body_locations");
        ic.refreshImageList(current_user);
//        image_list = ic.getImageList();



        setContentView(R.layout.activity_edit_body_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.body_locations_list);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        image_adapter = new BodyLocationImageAdapter(getApplicationContext(), ic, this);
        ic.setImageAdapter(image_adapter);
        recyclerView.setAdapter(image_adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto(new_body_location);
            }
        });
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent){
        if(request == REQUEST_IMAGE_CAPTURE && result == RESULT_OK){
            Bundle bundle = intent.getExtras();
            Bitmap image = (Bitmap) bundle.get("data");
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String file_name = ic.saveImage(image, ts);
            blp = new BodyLocationPhoto();
            File temp = new File(file_name);
            blp.setPatientId(current_user);
            blp.setBodyLocationPhotoId(temp.getName());
            requestTitle(blp);
            callUpdateUserTask();


//            Intent select_location = new Intent(this,SelectBodyLocationActivity.class);
//            select_location.putExtra("file_name",file_name);
//            select_location.putExtra("current_user", current_user);
//            startActivity(select_location);
            ic.refreshImageList(current_user);
            image_adapter.notifyDataSetChanged();
        }
        else if (request == REQUEST_IMAGE_CAPTURE_NEW_BODYLOCATION && result == RESULT_OK){
            Bundle bundle = intent.getExtras();
            Bitmap image = (Bitmap) bundle.get("data");
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String file_name = ic.saveImage(image, ts);
            File temp = new File(file_name);
            callUpdateUserTask();
            System.out.println("NEW BODY LOCATION FILENAME"+file_name);
            Intent select_location = new Intent(this,SelectBodyLocationActivity.class);
            select_location.putExtra("file_name",file_name);
            select_location.putExtra("current_user", current_user);
            select_location.putExtra("record_id", record_id);
            startActivity(select_location);
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

    private void setupActivity(){
        new_body_location = false;
//        blp = new BodyLocationPhoto();

        blesc = new BodyLocationPhotoElasticSearchController();
        Intent intent = getIntent();

        current_user = "";
        if (intent.hasExtra("current_user")){
            current_user = intent.getStringExtra("current_user");
        }
        if (intent.hasExtra("new_body_location")){
            new_body_location = intent.getBooleanExtra("new_body_location",false);
        }
        if (intent.hasExtra("auto_photo")){
            auto_photo = intent.getBooleanExtra("auto_photo", false);
        }
        if (intent.hasExtra("record_id")){
            record_id = intent.getStringExtra("record_id");
        }
        if (intent.hasExtra("record")){
            Gson gson = new Gson();
            record = gson.fromJson(intent.getStringExtra("record"), Record.class);
        }

    }

    private void checkPermissions(){
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
    }

    private void callUpdateUserTask(){
        new getUserTask().execute(current_user);
    }

    private class getUserTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... user_ids) {
            UserElasticSearchController ues_controller =
                    new UserElasticSearchController();
            for (String uid : user_ids) {
                User u = ues_controller.getUser(uid);
                return u;
            }
            return null;
        }
        @Override
        protected void onPostExecute(User u){
            super.onPostExecute(u);
            addBodyLocation(u);
//            callPhotoTask();
        }
    }
    private void addBodyLocation(User u){
        Patient p = (Patient) u;
        p.addBodyLocation(blp.getBodyLocationPhotoId());
        new updateUserTask().execute(p);
    }

    private class updateUserTask extends AsyncTask<Patient, Void, Patient> {

        @Override
        protected Patient doInBackground(Patient... patients) {
            UserElasticSearchController ues_controller =
                    new UserElasticSearchController();
            for (Patient p : patients) {
                ues_controller.addUser(p);
                return p;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Patient p){
            super.onPostExecute(p);
            ic.refresh(p);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE_NEW_BODYLOCATION = 1;

    private void takePhoto(boolean new_bl) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if(new_bl){
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_NEW_BODYLOCATION);
            }
            else{
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void launchSelectLocation(BodyLocationPhoto blp){
        Intent select_location = new Intent(this,SelectBodyLocationActivity.class);
        BodyLocation bl = record.getBodyLocation();
        if (bl != null){
            select_location.putExtra("x_loc", bl.getX());
            select_location.putExtra("y_loc", bl.getY());
            System.out.println("NEW BODY LOCATION FILENAME"+ic.getAbsolutePath(bl.getBodyLocationId()));
            select_location.putExtra("file_name",ic.getAbsolutePath(bl.getBodyLocationId()));
        }
        else {
            System.out.println("ERROR--body location was null from elasticsearch");
//            bl = new BodyLocation();
            select_location.putExtra("file_name", ic.getAbsolutePath(blp.getBodyLocationPhotoId()));
        }
        select_location.putExtra("current_user", current_user);
        select_location.putExtra("record_id", record_id);
//        select_location.putExtra("file_name", bl.)
        startActivity(select_location);
    }

    private void editLocation(String file_name){
        new GetBodyLocationPhotoTask().execute(file_name);
    }

    private void requestTitle(final BodyLocationPhoto bl_p){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Label Body Location");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for email input
        final TextView bloc_title = new TextView(this);
        bloc_title.setText("Body Location: ");
        layout.addView(bloc_title);

        // Add a TextView for email
        final EditText bloc_text = new EditText(this);
//        bloc_text.setText(current_user.getEmail());
        bloc_text.setInputType(InputType.TYPE_CLASS_TEXT);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0,0,0,64);
        bloc_text.setLayoutParams(params);
        layout.addView(bloc_text); // Notice this is an add method


        dialog.setView(layout); // Again this is a set method, not add

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Adding Body Location", Toast.LENGTH_SHORT).show();
//                BodyLocationPhotoElasticSearchController blesc = new BodyLocationPhotoElasticSearchController();
//                PhotoElasticSearchController pesc = new PhotoElasticSearchController();
                bl_p.setBodyLocation(bloc_text.getText().toString());
                new SetBodyLocationPhotoTask().execute(bl_p);
//                blp.setPatientId(current_user);
//                blp.setBodyLocationId(image_file.getName());
//                new SetBodyLocationPhotoTask().execute(blp);

//                blesc.addBodyLocationPhoto(bl);
//                pesc.addPhoto(image_file, image_file.getName());
//                System.out.println("FILENAME: "+file_name+"\nbody location text: "+bl.getLocationName());
//                finish();

            }
        })
                .setNegativeButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        ic.deleteImage(bl_p);
                    }
                });
        dialog.show();
    }

    private class SetBodyLocationPhotoTask extends AsyncTask<BodyLocationPhoto, Void, Void> {

        @Override
        protected Void doInBackground(BodyLocationPhoto... body_locations_photos) {
            BodyLocationPhotoElasticSearchController bles_controller =
                    new BodyLocationPhotoElasticSearchController();
            for (BodyLocationPhoto bl_p : body_locations_photos) {
                bles_controller.addBodyLocationPhoto(bl_p);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v){
            super.onPostExecute(null);

//            callPhotoTask();
        }
    }

    private class GetBodyLocationPhotoTask extends AsyncTask<String, Void, BodyLocationPhoto> {

        @Override
        protected BodyLocationPhoto doInBackground(String... body_location_ids) {
            BodyLocationPhotoElasticSearchController bles_controller =
                    new BodyLocationPhotoElasticSearchController();
            BodyLocationPhoto bl = null;
            for (String bl_id : body_location_ids) {
                System.out.println("Attempting to get body location for id: "+bl_id);
                bl = bles_controller.getBodyLocationPhoto(bl_id);
            }
            return bl;
        }
        @Override
        protected void onPostExecute(BodyLocationPhoto blp){
            super.onPostExecute(blp);
            if(new_body_location == false){
                requestTitle(blp);
            }
            else{
                launchSelectLocation(blp);
            }
        }
    }
}
