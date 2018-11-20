package team27.healthe.model;

import android.util.Log;

import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
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
     * @param user_id (String)
     */
    public static void addProblem(Problem p, String user_id) {
        verifyClient();

        Gson gson = new Gson();
        String problem_json = gson.toJson(p);
        Patient patient = (Patient)getUser(user_id);
        Collection<Integer> plist = patient.getProblemList();
        Integer problem_id_index;
        System.out.println("plist size" + plist.size());
        if (plist.size() > 0){
            problem_id_index = Collections.max(patient.getProblemList()) + 1;
        } else {
            problem_id_index = 0;
        }
        System.out.println("problem_id_index" + problem_id_index);
        p.setProblemID(problem_id_index);
        patient.addProblem(problem_id_index);
        addUser(patient);
        String ins_problem_id = user_id + "-" + problem_id_index.toString();

        Index index = new Index.Builder(problem_json)
                .index(test_index).type(problem_type).id(ins_problem_id).build();//--.id(p.getProblemID().toString())

        try {
            client.execute(index);
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
        }
    }

    /**
     * Get the problem from a given problem id
     * @param problem_id (Integer)
     * @param user_id (String)
     * @return Problem (Class)
     */
    public static Problem getProblem(Integer problem_id, String user_id) {
        verifyClient();
        String es_problem_id = user_id + "-" + problem_id.toString();
        Get get = new Get.Builder(test_index, es_problem_id).type(problem_type).build();



        try {
            JestResult result = client.execute(get);
            Problem p;
            p = result.getSourceAsObject(Problem.class);
            return p;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes a specified problem for the given user (corresponding to user_id)
     * @param problem_id (Integer)
     * @param user_id (String)
     */
    public static void removeProblem(Integer problem_id, String user_id){
        verifyClient();
        String es_problem_id = user_id + "-" + problem_id.toString();
        try{
            client.execute(new Delete.Builder(es_problem_id)
                    .index("problem")
                    .type("problem")
                    .build());
        } catch (Exception e){

        }

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
