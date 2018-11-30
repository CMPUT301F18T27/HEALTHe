package team27.healthe.controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.util.Base64;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import team27.healthe.model.ElasticSearchPhoto;

public class PhotoElasticSearchController extends ElasticSearchController{

    public boolean getPhoto(String id) {
        verifyClient();
        Get get = new Get.Builder(test_index, id).type(photo_type).build();

        try {
            JestResult result = client.execute(get);
            if (!result.isSucceeded()) { return false; }

            Gson gson = new Gson();
            ElasticSearchPhoto es_photo = gson.fromJson(result.getSourceAsString(),ElasticSearchPhoto.class);
            if (decodeFromBase64(es_photo.getBase64String(), id)) {
                return true;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String addPhoto(File photo, String id) {
        verifyClient();

        Gson gson = new Gson();
        ElasticSearchPhoto es_photo = new ElasticSearchPhoto(encodeFileToBase64(photo));
        String photo_json = gson.toJson(es_photo);

        Index index;
        if (id == null) {
            index = new Index.Builder(photo_json).index(test_index).type(photo_type).build();
        } else {
            index = new Index.Builder(photo_json).index(test_index).type(photo_type).id(id).build();
        }


        try {
            DocumentResult result = client.execute(index);
            return result.getId();
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
        }
        return null;
    }

    public void deletePhoto(String id) {
        verifyClient();
        try{
            client.execute(new Delete.Builder(id)
                    .index(index)
                    .type(photo_type)
                    .build());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String encodeFileToBase64(File file){
        String encoded_file = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encoded_file = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encoded_file;
    }

    private boolean decodeFromBase64(String base64_photo, String id) {
        File storage_directory = getStorageDirectory();
        if (storage_directory == null) {
            return  false;
        }

        File photo_file = new File(storage_directory.getPath() + File.separator + id + ".jpg");
        try {
            FileOutputStream file_out_stream = new FileOutputStream(photo_file);
            byte[] bytes = Base64.decode(base64_photo, Base64.DEFAULT);
            file_out_stream.write(bytes);
            file_out_stream.close();
            //orientatePhoto(photo_file);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private File getStorageDirectory() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "HEALTHe" + File.separator + "photos");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        return mediaStorageDir;
    }

    /*
    private void orientatePhoto(File photo_file) {
        // Taken from: https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
        try {
            ExifInterface ei = new ExifInterface(photo_file.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap photo = BitmapFactory.decodeFile(photo_file.getAbsolutePath());

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

            OutputStream output_stream = new FileOutputStream(photo_file);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , output_stream);
            output_stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    */
}
