package team27.healthe.controllers;

import android.util.Log;

import com.google.gson.Gson;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import team27.healthe.model.BodyLocation;
import team27.healthe.model.BodyLocationPhoto;

/**
 * Class for add/edit/delete operations for BodyLocation objects from the Elasticsearch server
 * @author Chris
 */
public class BodyLocationPhotoElasticSearchController extends ElasticSearchController {

    /**
     * Add body location to elastic search database using the associated photo's filename
     * as the id in elastic search
     * @param bl (BodyLocation Class)
     */
    public static BodyLocationPhoto addBodyLocationPhoto(BodyLocationPhoto bl) {
        verifyClient();
        Gson gson = new Gson();
        String bl_json = gson.toJson(bl);

        Index index;
        if (bl.getBodyLocationPhotoId().equals("")) {
            index = new Index.Builder(bl_json).index(test_index).type(body_location_type)
                    .build();
        }
        else {
            index = new Index.Builder(bl_json).index(test_index).type(body_location_type)
                    .id(bl.getBodyLocationPhotoId()).build();
        }

        try {
            DocumentResult result = client.execute(index);
            bl.setBodyLocationPhotoId(result.getId());
            return bl;
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
            return null;
        }
    }

    /**
     * Get the bodylocation from a given body location id (filename)
     * @param bl_id (String)
     * @return BodyLocation (Class)
     */
    public static BodyLocationPhoto getBodyLocationPhoto(String bl_id) {
        verifyClient();
        Get get = new Get.Builder(test_index, bl_id).type(body_location_type).build();

        try {
            JestResult result = client.execute(get);

            Gson gson = new Gson();
            BodyLocationPhoto body_location = gson.fromJson(result.getSourceAsString(),BodyLocationPhoto.class);
            return body_location;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes a specified body location from the given body location id (bl_id)
     * @param bl_id (Integer)
     */
    public static void removeBodyLocationPhoto(String bl_id){
        verifyClient();
        try{
            client.execute(new Delete.Builder(bl_id)
                    .index(test_index)
                    .type(body_location_type)
                    .build());
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}