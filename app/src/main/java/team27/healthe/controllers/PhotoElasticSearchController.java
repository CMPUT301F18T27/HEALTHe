package team27.healthe.controllers;

import android.content.Context;
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

/**
 * Class for add/edit/delete operations for Photo objects from the Elasticsearch server
 * @author Cody
 */
public class PhotoElasticSearchController extends ElasticSearchController{
    /**
     * Gets the photo from elastic search
     * @param id (String)
     * @param context (Context class)
     * @return true or false
     */
    public boolean getPhoto(String id, Context context) {
        verifyClient();
        Get get = new Get.Builder(test_index, id).type(photo_type).build();

        try {
            JestResult result = client.execute(get);
            if (!result.isSucceeded()) { return false; }

            Gson gson = new Gson();
            ElasticSearchPhoto es_photo = gson.fromJson(result.getSourceAsString(),ElasticSearchPhoto.class);
            if (decodeFromBase64(es_photo.getBase64String(), id, context)) {
                return true;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Adds photo to elastic search
     * Using JSON
     * @param photo (File)
     * @param id (String)
     * @return result.isSucceeded()
     */
    public boolean addPhoto(File photo, String id) {
        verifyClient();

        Gson gson = new Gson();
        ElasticSearchPhoto es_photo = new ElasticSearchPhoto(encodeFileToBase64(photo));
        String photo_json = gson.toJson(es_photo);

        Index index = new Index.Builder(photo_json).index(test_index).type(photo_type).id(id).build();

        try {
            DocumentResult result = client.execute(index);
            return result.isSucceeded();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Deletes a photo from elasticsearch
     * @param id (String)
     * @return result.isSucceeded()
     */
    public boolean deletePhoto(String id) {
        verifyClient();
        try{
            DocumentResult result = client.execute(new Delete.Builder(id)
                    .index(test_index)
                    .type(photo_type)
                    .build());
            return result.isSucceeded();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Encodes photo files to base 64
     * @param file (File)
     * @return encoded_file
     */
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

    /**
     * Decodes photo files from base 64
     * @param base64_photo (String)
     * @param id (String)
     * @param context (Context class)
     * @return true or false
     */
    private boolean decodeFromBase64(String base64_photo, String id, Context context) {
        File storage_directory = context.getFilesDir();
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
}
