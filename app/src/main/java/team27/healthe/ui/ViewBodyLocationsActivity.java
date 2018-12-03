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

import org.elasticsearch.common.UUID;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.controllers.BodyLocationElasticSearchController;
import team27.healthe.controllers.BodyLocationListener;
import team27.healthe.controllers.BodyLocationPhotoElasticSearchController;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.controllers.OfflineController;
import team27.healthe.controllers.PhotoElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.BodyLocation;
import team27.healthe.controllers.BodyLocationImageAdapter;
import team27.healthe.controllers.ImageController;
import team27.healthe.model.BodyLocationPhoto;
import team27.healthe.model.Patient;
import team27.healthe.model.Photo;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class ViewBodyLocationsActivity extends AppCompatActivity implements BodyLocationListener {
    BodyLocationImageAdapter image_adapter;
    public static final String GET_LOCATION_MESSAGE = "team27.healthe.GET_LOCATION";
    public static final String SET_FAB_MESSAGE = "team27.healthe.SET_FAB";
    public static final String BODY_LOCATION_MESSAGE = "team27.healthe.BODY_LOCATION";

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_WRITE_EXTERNAL_REQUEST_CODE = 101;
    private static final int MY_READ_EXTERNAL_REQUEST_CODE = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Patient current_user;

    private boolean set_fab;
    private boolean get_location;

    BodyLocationElasticSearchController blesc;
    @Override
    public void recyclerViewClicked(View v, int i){
        if (get_location) {
            BodyLocationPhoto blp = current_user.getBodyLocations().get(i);

            BodyLocation body_locatoin = new BodyLocation();
            body_locatoin.setBodyLocationId(blp.getBodyLocationPhotoId());
            body_locatoin.setLocation(blp.getBodyLocation());
            launchEditLocation(body_locatoin);
        } else {
            requestTitle(current_user.getBodyLocations().get(i), true);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFromIntent();


        blesc = new BodyLocationElasticSearchController();

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

        setContentView(R.layout.activity_edit_body_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.body_locations_list);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);

        image_adapter = new BodyLocationImageAdapter(getApplicationContext(), current_user.getBodyLocations(), this);
        recyclerView.setAdapter(image_adapter);
        image_adapter.notifyDataSetChanged();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (!set_fab) {
            fab.hide();
        }
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

    private void getFromIntent() {
        Intent intent = getIntent();
        UserElasticSearchController controller = new UserElasticSearchController();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        current_user = (Patient) controller.jsonToUser(user_json);

        set_fab = intent.getBooleanExtra(SET_FAB_MESSAGE, true);
        get_location = intent.getBooleanExtra(GET_LOCATION_MESSAGE, false);

        getPhotos();

    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private void launchEditLocation(BodyLocation bl){
        Gson gson = new Gson();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        //returnIntent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        returnIntent.putExtra(BODY_LOCATION_MESSAGE, gson.toJson(bl));
        finish();
    }

    private void getPhotos() {
        for (BodyLocationPhoto body_photo : current_user.getBodyLocations()) {
            String file_name = this.getFilesDir() + File.separator + body_photo.getBodyLocationPhotoId() + ".jpg";
            File file = new File(file_name);
            if (!file.exists()) {
                new GetPhoto().execute(body_photo.getBodyLocationPhotoId());
            }
        }
    }


    @Override
    protected void onActivityResult(int request, int result, Intent intent){
        if(request == REQUEST_IMAGE_CAPTURE && result == RESULT_OK){
            LocalFileController file_controller = new LocalFileController();

            Bundle bundle = intent.getExtras();
            Bitmap image = (Bitmap) bundle.get("data");

            String file_name = UUID.randomUUID().toString();
            File image_file = file_controller.saveImage(image, file_name, this);
            new AddPhoto().execute(image_file);

            BodyLocationPhoto body_location_photo = new BodyLocationPhoto();
            body_location_photo.setBodyLocationPhotoId(file_name);

            requestTitle(body_location_photo, false);
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



    private void requestTitle(final BodyLocationPhoto bl_p, final boolean allow_delete){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Label Body Location");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for body location input
        final TextView bloc_title = new TextView(this);
        bloc_title.setText("Body Location: ");
        layout.addView(bloc_title);

        // Add a TextView for body location
        final EditText bloc_text = new EditText(this);
        bloc_text.setInputType(InputType.TYPE_CLASS_TEXT);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0,0,0,64);
        bloc_text.setLayoutParams(params);
        bloc_text.setText(bl_p.getBodyLocation());
        layout.addView(bloc_text); // Notice this is an add method


        dialog.setView(layout); // Again this is a set method, not add

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (allow_delete) {
                    current_user.removeBodyLocationPhoto(bl_p);
                }

                Toast.makeText(getApplicationContext(), "Adding Body Location", Toast.LENGTH_SHORT).show();
                bl_p.setBodyLocation(bloc_text.getText().toString());
                current_user.addBodyLocation(bl_p);

                LocalFileController file_controller = new LocalFileController();
                file_controller.saveUserInFile(current_user, getApplicationContext());
                new UpdateUser().execute(current_user);

                image_adapter.notifyDataSetChanged();

                if (!get_location) {
                    Gson gson = new Gson();
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    returnIntent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
                }

            }
        });

        if (allow_delete) {
            dialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    current_user.removeBodyLocationPhoto(bl_p);

                    LocalFileController file_controller = new LocalFileController();
                    file_controller.deleteImage(bl_p.getBodyLocationPhotoId(), getApplicationContext());
                    new DeletePhoto().execute(bl_p.getBodyLocationPhotoId());

                    file_controller.saveUserInFile(current_user, getApplicationContext());
                    new UpdateUser().execute(current_user);

                    image_adapter.notifyDataSetChanged();
                }
            });
        }
        dialog.show();
    }



    private class UpdateUser extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... users) {
            UserElasticSearchController es_controller = new UserElasticSearchController();
            for (User user : users) {
                if(!es_controller.addUser(user)) {
                    return user;
                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(user != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.addUser(user, getApplicationContext());
            }
        }
    }

    private class AddPhoto extends AsyncTask<File, Void, File> {

        @Override
        protected File doInBackground(File... files) {
            PhotoElasticSearchController photo_controller = new PhotoElasticSearchController();

            for (File file : files) {
                String file_name = file.getName();
                boolean succeeded = photo_controller.addPhoto(file, file_name.substring(0, file_name.length() - 4));
                if (!succeeded) {
                    return file;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file != null) {
                OfflineController controller = new OfflineController();
                controller.addPhoto(file, getApplicationContext());
            }
        }
    }

    private class GetPhoto extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... photo_ids) {
            PhotoElasticSearchController photo_controller = new PhotoElasticSearchController();

            for (String photo_id : photo_ids) {
                return photo_controller.getPhoto(photo_id, getApplicationContext());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                image_adapter.notifyDataSetChanged();
            }
        }
    }

    private class DeletePhoto extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... photo_ids) {
            PhotoElasticSearchController photo_controller = new PhotoElasticSearchController();

            for (String photo_id : photo_ids) {
                photo_controller.deletePhoto(photo_id);
            }
            return null;
        }
    }
}
