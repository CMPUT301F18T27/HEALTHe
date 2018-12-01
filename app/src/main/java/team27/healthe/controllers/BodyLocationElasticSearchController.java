package team27.healthe.controllers;

import android.util.Log;

import com.google.gson.Gson;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import team27.healthe.model.BodyLocation;
import team27.healthe.model.Problem;

public class BodyLocationElasticSearchController extends ElasticSearchController {

    /**
     * Add body location to elastic search database using the associated photo's filename
     * as the id in elastic search
     * @param bl (BodyLocation Class)
     */
    public static BodyLocation addBodyLocation(BodyLocation bl) {
        verifyClient();

        Gson gson = new Gson();
        String problem_json = gson.toJson(bl);

        Index index;
        if (bl.getBodyLocationId().equals("")) {
            index = new Index.Builder(problem_json).index(test_index).type(body_location_type)
                    .build();
        }
        else {
            index = new Index.Builder(problem_json).index(test_index).type(body_location_type)
                    .id(bl.getBodyLocationId()).build();
        }

        try {
            DocumentResult result = client.execute(index);
            bl.setBodyLocationId(result.getId());
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
    public static BodyLocation getBodyLocation(String bl_id) {
        verifyClient();
        Get get = new Get.Builder(test_index, bl_id).type(body_location_type).build();

        try {
            JestResult result = client.execute(get);

            Gson gson = new Gson();
            BodyLocation body_location = gson.fromJson(result.getSourceAsString(),BodyLocation.class);
            return body_location;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Functionality not specified
//    /**
//     * Removes a specified body location for the given user (corresponding to user_id)
//     * @param bl_id (Integer)
//     */
//    public static void removeBodyLocation(String bl_id){
//        verifyClient();
//        try{
//            client.execute(new Delete.Builder(bl_id)
//                    .index(test_index)
//                    .type(body_location_type)
//                    .build());
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
}
