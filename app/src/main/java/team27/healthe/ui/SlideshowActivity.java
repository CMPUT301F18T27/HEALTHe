package team27.healthe.ui;

// Activity for showing a slideshow of record photos

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.controllers.OfflineController;
import team27.healthe.controllers.PhotoElasticSearchController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Photo;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class SlideshowActivity extends AppCompatActivity {
    private static final Integer PHOTO_REQUEST_CODE = 10;
    private User current_user;
    private Record record;
    private Integer image_index = 0;
    private ArrayList<File> image_files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
        }

        getItems(getIntent());
        getFiles();
        setImage();

    }

    @Override
    public void onResume() {
        super.onResume();
        checkTasks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Gson gson = new Gson();

                Photo photo = gson.fromJson(data.getStringExtra(PhotoActivity.PHOTO_ID_MESSAGE), Photo.class);
                File photo_file = new File(getMediaDirectory().getPath() + File.separator + photo.getId() + ".jpg");
                if (photo_file.exists()) {
                    image_files.add(photo_file);

                    record.addPhoto(photo);
                    new UpdateRecord().execute(record);

                    LocalFileController file_controller = new LocalFileController();
                    file_controller.saveRecordInFile(record, this);

                    setIntent();

                    updateButtons();
                }
            }
        }
    }

    // Get items from intent
    private void getItems(Intent intent) {
        UserElasticSearchController es_controller = new UserElasticSearchController();
        Gson gson = new Gson();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String record_json = intent.getStringExtra(RecordActivity.RECORD_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);
        this.record = gson.fromJson(record_json, Record.class);

    }

    // Get photo files from local directory
    private void getFiles() {
        image_files = new ArrayList<>();
        File media_directory = getMediaDirectory();
        if (media_directory != null) {
            for (Photo photo : record.getPhotos()) {
                File photo_file = new File(media_directory.getPath() + File.separator + photo.getId() + ".jpg");
                if (photo_file.exists()) {
                    image_files.add(photo_file);
                }
                else {
                    new GetPhoto().execute(photo);
                }
            }
        }
    }

    // Check for network connectivity
    private boolean isNetworkConnected() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            return true;
        }
        return false;
    }

    // Check if offline controller has tasks
    private void checkTasks() {
        if (isNetworkConnected()) {
            OfflineController controller = new OfflineController();
            if (controller.hasTasks(this)) {
                new PerformTasks().execute(true);
            }
        }
    }

    // Set the image view
    private void setImage() {
        ImageView image_view = (ImageView) findViewById(R.id.slideshowImage);
        TextView no_photos_text = (TextView) findViewById(R.id.noPhotosTextView);
        Button add_photo_button = (Button) findViewById(R.id.button21);
        if (current_user instanceof CareProvider) {
            add_photo_button.setVisibility(add_photo_button.INVISIBLE);
        }

        if (image_files.isEmpty()) {
            image_view.setVisibility(View.INVISIBLE);
            no_photos_text.setVisibility(image_view.VISIBLE);

            Button next_button = (Button) findViewById(R.id.buttonNext);
            Button prev_button = (Button) findViewById(R.id.buttonPrev);

            next_button.setVisibility(image_view.INVISIBLE);
            prev_button.setVisibility(image_view.INVISIBLE);
        } else {
            no_photos_text.setVisibility(image_view.INVISIBLE);
            image_view.setVisibility(View.VISIBLE);

            image_view.setImageURI(Uri.fromFile(image_files.get(image_index)));
        }

        if (image_files.size() > 1) {
            Button next_button = (Button) findViewById(R.id.buttonNext);
            next_button.setVisibility(image_view.VISIBLE);
        }

    }

    // get local file directory
    private File getMediaDirectory() {
        return this.getFilesDir();
    }

    // Async class for getting photo from elastic search
    private class GetPhoto extends AsyncTask<Photo, Void, String> {

        @Override
        protected String doInBackground(Photo... photos) {
            PhotoElasticSearchController photo_controller = new PhotoElasticSearchController();

            for (Photo photo : photos) {
                boolean success = photo_controller.getPhoto(photo.getId(), getApplicationContext());
                if (success) {
                    return photo.getId();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String id) {
            super.onPostExecute(id);
            if (id != null) {
                File photo_file = new File(getMediaDirectory().getPath() + File.separator + id + ".jpg");
                if (photo_file.exists()) {
                    image_files.add(photo_file);
                    updateButtons();
                }
            }
        }
    }

    // Async class for updating records in elastic search
    private class UpdateRecord extends AsyncTask<Record, Void, Record> {

        @Override
        protected Record doInBackground(Record... records) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();
            for (Record record: records) {
                if(!es_controller.addRecord(record)) {
                    return record;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record) {
            super.onPostExecute(record);
            if (record != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.addRecord(record, getApplicationContext());
            }
        }
    }

    // Async class for performing offline controller tasks
    private class PerformTasks extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            OfflineController controller = new OfflineController();
            for(Boolean bool:booleans) {
                if (bool) {
                    controller.performTasks(getApplicationContext());
                }
            }
            return null;
        }
    }

    // Set next and prev button visibilities
    private void updateButtons() {
        Button next_button = (Button) findViewById(R.id.buttonNext);
        if (image_index < image_files.size() - 1) {
            next_button.setVisibility(next_button.VISIBLE);
        }
        if (image_files.size() == 1) {
            setImage();
        }
    }

    // Set return intent
    private void setIntent() {
        Gson gson = new Gson();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        returnIntent.putExtra(RecordActivity.RECORD_MESSAGE, gson.toJson(record));
    }

    // onClick previous button
    public void onClickPrev(View view) {
        ImageView image_view = (ImageView) findViewById(R.id.slideshowImage);
        Button next_button = (Button) findViewById(R.id.buttonNext);
        Button prev_button = (Button) findViewById(R.id.buttonPrev);

        image_index--;
        image_view.setImageURI(Uri.fromFile(image_files.get(image_index)));
        next_button.setVisibility(view.VISIBLE);
        if (image_index == 0){
            prev_button.setVisibility(view.INVISIBLE);
        }
    }

    // onClick next button
    public void onClickNext(View view) {
        ImageView image_view = (ImageView) findViewById(R.id.slideshowImage);
        Button next_button = (Button) findViewById(R.id.buttonNext);
        Button prev_button = (Button) findViewById(R.id.buttonPrev);

        image_index++;
        image_view.setImageURI(Uri.fromFile(image_files.get(image_index)));
        prev_button.setVisibility(view.VISIBLE);
        if (image_index == image_files.size() - 1){
            next_button.setVisibility(view.INVISIBLE);
        }

    }

    // onClick for add photo button
    public void onClickAddPhoto(View view) {
        if (record.getPhotos().size() <= 10) {
            Gson gson = new Gson();
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
            intent.putExtra(RecordActivity.RECORD_MESSAGE, gson.toJson(record));
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        } else {
            Toast.makeText(this, "You can only attach up to 10 photos", Toast.LENGTH_SHORT).show();
        }
    }
}
