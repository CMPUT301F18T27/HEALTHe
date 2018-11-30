package team27.healthe.controllers;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.util.Base64;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;

public class PhotoElasticSearchController extends ElasticSearchController{

    public boolean getPhoto(String id) {
        verifyClient();
        Get get = new Get.Builder(test_index, id).type(photo_type).build();

        try {
            JestResult result = client.execute(get);

            Gson gson = new Gson();
            if (decodeFromBase64(gson.fromJson(result.getSourceAsString(),String.class), id)) {
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
        String photo_json = gson.toJson(encodeFileToBase64(photo));

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
}
