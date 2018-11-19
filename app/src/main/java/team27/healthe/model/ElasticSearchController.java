package team27.healthe.model;

import android.util.Log;

import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.Map;

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

public class ElasticSearchController {
    private static JestDroidClient client;
    private static String node = "http://cmput301.softwareprocess.es:8080";
    private static String index = "cmput301f18t27";
    private static String test_index = "cmput301f18t27test";
    private static String user_type = "user";

    public ElasticSearchController() {
        verifyClient();
    }

    // Add user to elastic search database using userid as the id in elastic search
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

    // Create connection to elastic search server
    public static void verifyClient() {
    // Code from LonelyTwitter
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(node);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

    public String userToJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

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
}
