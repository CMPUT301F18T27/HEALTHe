package team27.healthe.controllers;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.model.User;

/**
 * Class for add/edit/delete operations for User objects from the Elasticsearch server
 * @author Cody
 */
public class UserElasticSearchController extends ElasticSearchController {
    /**
     * Add user to elastic search database using userid as the id in elastic search
     * @param user (User class)
     */
    public static boolean addUser(User user) {
        verifyClient();

        Gson gson = new Gson();
        String user_json = gson.toJson(user);

        Index index = new Index.Builder(user_json).index(test_index).type(user_type).id(user.getUserid()).build();

        try {
            DocumentResult result = client.execute(index);
            return result.isSucceeded();
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
            return false;
        }
    }

    // Get the user associated with a user_id

    /**
     * Get the user associated with a user_id
     * @param user_id (String)
     * @return Patient or CareProvider (Class)
     */
    public static User getUser(String user_id) {
        verifyClient();
        Gson gson = new Gson();
        Get get = new Get.Builder(test_index,user_id).type(user_type).build();

        try {
            JestResult result = client.execute(get);

            User user;

            // Get user type from json
            String json = result.getSourceAsString();
            Map source_map = gson.fromJson(json, Map.class);
            String user_type = source_map.get("user_type").toString();

            if (user_type.equals("patient")) {
                user = result.getSourceAsObject(Patient.class);
            } else {
                user = result.getSourceAsObject(CareProvider.class);
            }

            return user;
        }
        catch (Exception e) {
        }
        return null;
    }

    /**
     * Returns a patient or care provider from a user JSON string
     * @param user_json (String)
     * @return gson.fromJson(user_json,Patient.class) and gson.fromJson(user_json,CareProvider.class)
     */
    public User jsonToUser(String user_json) {
        Gson gson = new Gson();

        Map source_map = gson.fromJson(user_json, Map.class);
        String user_type = source_map.get("user_type").toString();

        if (user_type.equals("patient")) {
            return gson.fromJson(user_json,Patient.class);
        } else {
            return gson.fromJson(user_json,CareProvider.class);
        }
    }

    /**
     * Removes a specified user
     * @param user_id (Integer)
     */
    public static void removeUser(String user_id){
        verifyClient();
        try{
            client.execute(new Delete.Builder(user_id)
                    .index(test_index)
                    .type(user_type)
                    .build());
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
