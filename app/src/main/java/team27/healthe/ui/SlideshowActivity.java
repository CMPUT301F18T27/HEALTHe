package team27.healthe.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class SlideshowActivity extends AppCompatActivity {
    private User current_user;
    private Record record;
    private Integer image_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        getItems(getIntent());

        setImage();

    }

    private void getItems(Intent intent) {
        ElasticSearchController es_controller = new ElasticSearchController();
        Gson gson = new Gson();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String record_json = intent.getStringExtra(RecordActivity.RECORD_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);

    }

    private void setImage() {
        ImageView image_view = (ImageView) findViewById(R.id.slideshowImage);
        TextView no_photos_text = (TextView) findViewById(R.id.noPhotosTextView);

        File image_file = getFirstPhoto();

        if (image_file == null) {
            image_view.setVisibility(View.INVISIBLE);
            no_photos_text.setVisibility(image_view.VISIBLE);

            Button next_button = (Button) findViewById(R.id.buttonNext);
            Button prev_button = (Button) findViewById(R.id.buttonPrev);

            next_button.setVisibility(image_view.GONE);
            prev_button.setVisibility(image_view.GONE);
        } else {
            no_photos_text.setVisibility(image_view.INVISIBLE);
            image_view.setVisibility(View.VISIBLE);

            image_view.setImageURI(Uri.fromFile(image_file));
        }

        if (getNextPhoto(false) != null) {
            Button next_button = (Button) findViewById(R.id.buttonNext);
            next_button.setVisibility(image_view.VISIBLE);
        }

    }

    private File getFirstPhoto(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "HEALTHe" + File.separator + "photos");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        File image_file = new File(mediaStorageDir.getPath() + File.separator + "picture_" + this.image_count.toString() + ".jpg");

        if (image_file.exists()) {return image_file;}
        return null;
    }

    private File getNextPhoto(boolean increment){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "HEALTHe" + File.separator + "photos");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        image_count++;
        File image_file = new File(mediaStorageDir.getPath() + File.separator + "picture_" + this.image_count.toString() + ".jpg");

        if (image_file.exists()) {
            if (!increment) {
                image_count--;
            }
            return image_file;
        }
        image_count--;
        return null;
    }

    private File getPrevPhoto(boolean increment){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "HEALTHe" + File.separator + "photos");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        image_count--;
        File image_file = new File(mediaStorageDir.getPath() + File.separator + "picture_" + this.image_count.toString() + ".jpg");

        if (image_file.exists()) {
            if (!increment) {
                image_count++;
            }
            return image_file;
        }
        image_count++;
        return null;
    }

    public void onClickPrev(View view) {
        ImageView image_view = (ImageView) findViewById(R.id.slideshowImage);
        Button next_button = (Button) findViewById(R.id.buttonNext);
        Button prev_button = (Button) findViewById(R.id.buttonPrev);

        File image_file = getPrevPhoto(true);
        if (image_file != null) {
            image_view.setImageURI(Uri.fromFile(image_file));
            next_button.setVisibility(view.VISIBLE);
            if (getPrevPhoto(false) == null) {
                prev_button.setVisibility(view.INVISIBLE);
            }
        } else {
            prev_button.setVisibility(view.INVISIBLE);
        }
    }

    public void onClickNext(View view) {
        ImageView image_view = (ImageView) findViewById(R.id.slideshowImage);
        Button next_button = (Button) findViewById(R.id.buttonNext);
        Button prev_button = (Button) findViewById(R.id.buttonPrev);

        File image_file = getNextPhoto(true);
        if (image_file != null) {
            image_view.setImageURI(Uri.fromFile(image_file));
            prev_button.setVisibility(view.VISIBLE);
            if (getNextPhoto(false) == null) {
                next_button.setVisibility(view.INVISIBLE);
            }
        } else {
            next_button.setVisibility(view.INVISIBLE);
        }

    }

    public void onClickAddPhoto(View view) {

    }
}
