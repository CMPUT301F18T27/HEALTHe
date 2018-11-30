package team27.healthe.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * ElasticSearch wrapper for handling operations involving storing/retrieving from elasticsearch
 * server
 * @author Chase
 */
public class ElasticSearchController {
    private static JestDroidClient client;
    private static String node = "http://cmput301.softwareprocess.es:8080";
    private static String index = "cmput301f18t27";
    private static String test_index = "cmput301f18t27test";
    private static String user_type = "user";
    private static String problem_type = "problem";
    private static String image_type = "img";
    private static String body_location_type = "body_location";

    public ElasticSearchController() {
        verifyClient();
    }

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

    /**
     * Add problem to elastic search database using problem id as the id in elastic search
     * @param p (Problem Class)
     */
    public static Problem addProblem(Problem p) {
        verifyClient();

        Gson gson = new Gson();
        String problem_json = gson.toJson(p);

        Index index;
        if (p.getProblemID().equals("")) {
            index = new Index.Builder(problem_json).index(test_index).type(problem_type).build();
        }
        else {
            index = new Index.Builder(problem_json).index(test_index).type(problem_type).id(p.getProblemID()).build();
        }

        try {
            DocumentResult result = client.execute(index);
            p.setProblemID(result.getId());
            return p;
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
            return null;
        }
    }

    /**
     * Get the problem from a given problem id
     * @param problem_id (Integer)
     * @return Problem (Class)
     */
    public static Problem getProblem(String problem_id) {
        verifyClient();
        Get get = new Get.Builder(test_index, problem_id).type(problem_type).build();

        try {
            JestResult result = client.execute(get);

            Gson gson = new Gson();
            Problem problem = gson.fromJson(result.getSourceAsString(),Problem.class);
            return problem;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes a specified problem for the given user (corresponding to user_id)
     * @param problem_id (Integer)
     */
    public static void removeProblem(String problem_id){
        verifyClient();
        try{
            client.execute(new Delete.Builder(problem_id)
                    .index("problem")
                    .type("problem")
                    .build());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void addImage(Bitmap image, String filename){
        String image_string;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image_string = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        verifyClient();

        Gson gson = new Gson();
        String image_json = gson.toJson(image_string);

        Index index = new Index.Builder(image_json).index(test_index).type(image_type).id(filename).build();
//        Index index;
//        if (.getProblemID().equals("")) {
//            index = new Index.Builder(problem_json).index(test_index).type(problem_type).build();
//        }
//        else {
//            index = new Index.Builder(problem_json).index(test_index).type(problem_type).id(p.getProblemID()).build();
//        }

        try {
            client.execute(index);
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
        }

    }

    public static Bitmap getImage(String filename) {
        verifyClient();
        Get get = new Get.Builder(test_index, filename).type(image_type).build();

        try {
            JestResult result = client.execute(get);

            Gson gson = new Gson();
            String image_string = gson.fromJson(result.getSourceAsString(), String.class);
            byte[] decode_string = Base64.decode(image_string, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decode_string,0, decode_string.length);
//            Problem problem = gson.fromJson(result.getSourceAsString(),Problem.class);
            return image;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create connection to elastic search server
     */
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

    /**
     * Converts json object to Problem object
     * @param problem_json (String)
     * @return Problem (Class)
     */
    public Problem jsonToProblem(String problem_json) {
        Gson gson = new Gson();

        Map source_map = gson.fromJson(problem_json, Map.class);
        String problem_type = source_map.get("problem_type").toString();
        return gson.fromJson(problem_json, Problem.class);
    }

    /**
     * Converts json object to User object
     * @param user_json (String)
     * @return Patient or CareProvider (Class)
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


}
