package team27.healthe.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.elasticsearch.common.UUID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import team27.healthe.R;
import team27.healthe.controllers.PhotoElasticSearchController;
import team27.healthe.model.Photo;

public class PhotoActivity extends AppCompatActivity {
    public static final String PHOTO_ID_MESSAGE = "team27.healthe.ID";
    public static final String SUCCESS_MESSAGE = "team27.healthe.SUCCESS";
    public static final String FILENAME_MESSAGE = "team27.healthe.FILENAME";
    private static final Integer PHOTO_REQUEST_CODE = 100;
    private Uri photo_uri;
    private File photo_file;
    private String file_name = UUID.randomUUID().toString();
    private boolean has_photo = false;
    private boolean has_bodylocation = false;
    private boolean saving = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
        }

    }

    // onClick for take photo button
    public void takePhoto(View view) {
        if(saving){return;} // If saving is in progress do nothing

        boolean delete = false;
        File temp_file = photo_file;
        if (photo_file != null) {
            delete = true;
        }

        photo_file = getOutputMediaFile();
        if (delete) {
            temp_file.delete();
        }
        if (photo_file != null) {
            photo_uri = Uri.fromFile(photo_file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri);

            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Unable to access external storage", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickBodyLocation(View view) {
        if(saving){return;} // If saving is in progress do nothing
        this.has_bodylocation = true; //temp
    }

    public void onClickSave (View view) {
        if(saving){return;} // If saving is in progress do nothing

        if (!has_photo) {
            Toast.makeText(this, "You must take a photo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!has_bodylocation) {
            Toast.makeText(this, "You must add a body location", Toast.LENGTH_SHORT).show();
            return;
        }
        saving = true;
        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();
        new AddPhoto().execute(photo_file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                compressImage();
                ImageView image_view = findViewById(R.id.imagePhoto);
                image_view.setImageURI(photo_uri);
                this.has_photo = true;
            }
        }
    }

    private void compressImage() {
        try {
            Bitmap photo_bitmap = BitmapFactory.decodeFile(photo_file.getAbsolutePath());

            Bitmap rotated_photo = orientatePhoto(photo_bitmap);
            if(rotated_photo != null) {
                photo_bitmap = rotated_photo;
            }

            Integer scale = photo_bitmap.getHeight()/512;
            photo_bitmap = Bitmap.createScaledBitmap(photo_bitmap, photo_bitmap.getWidth()/scale, photo_bitmap.getHeight()/scale, true);

            OutputStream output_stream = new FileOutputStream(photo_file);
            photo_bitmap.compress(Bitmap.CompressFormat.JPEG, 30 , output_stream);
            output_stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap orientatePhoto(Bitmap photo) {
        // Taken from: https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
        try {
            ExifInterface ei = new ExifInterface(photo_file.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(photo, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(photo, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(photo, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = photo;
            }
            return rotatedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private static File getMediaDirectory() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "HEALTHe" + File.separator + "photos");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        return mediaStorageDir;
    }

    private File getOutputMediaFile(){
        File mediaStorageDir = getMediaDirectory();
        if (mediaStorageDir == null) {
            return null;
        }

        File filename = new File(mediaStorageDir.getPath() + File.separator + this.file_name + ".jpg");

        return filename;
    }

    private class AddPhoto extends AsyncTask<File, Void, Boolean> {

        @Override
        protected Boolean doInBackground(File... files) {
            PhotoElasticSearchController photo_controller = new PhotoElasticSearchController();

            for (File file : files) {
                return photo_controller.addPhoto(file, null);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean isSucceeded) {
            super.onPostExecute(isSucceeded);
            //TODO: Check if photo uploaded successfully

            Gson gson = new Gson();
            Intent returnIntent = new Intent();
            setResult(RESULT_OK,returnIntent);
            returnIntent.putExtra(PHOTO_ID_MESSAGE,file_name);
            returnIntent.putExtra(SUCCESS_MESSAGE, true);
            finish();
        }
    }


}
