package team27.healthe.controllers;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.model.User;

public class UserElasticSearchController extends ElasticSearchController {
    /**
     * Add user to elastic search database using userid as the id in elastic search
     * @param user (User class)
     */
    public static void addUser(User user) {
        verifyClient();

        Gson gson = new Gson();
        String user_json = gson.toJson(user);

        Index index = new Index.Builder(user_json).index(test_index).type(user_type).id(user.getUserid()).build();

        try {
            client.execute(index);
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
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
}
