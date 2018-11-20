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
    private static String problem_type = "problem";

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

    // Add problem to elastic search database using problem id as the id in elastic search
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

    // Get the problem from a given problem id
    public static Problem getProblem(Integer problem_id, String user_id) {
        verifyClient();
        String es_problem_id = user_id + "-" + problem_id.toString();
        Get get = new Get.Builder(test_index, es_problem_id).type(problem_type).build();

//        Get get2 = new Get.Builder()

        try {
            JestResult result = client.execute(get);

            Problem p;

            p = result.getSourceAsObject(Problem.class);

            return p;
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
