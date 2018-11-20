package team27.healthe.model;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Local File access for handling operations involving storing/retrieving from local file
 * using JSON
 * @author Chase
 */
public class LocalFileController {
    private static String FILENAME;

    public LocalFileController(String filename) {
        this.FILENAME = filename;
    }

    /**
     * Empties the contents of the local file
     * @param context (Context)
     */
    public static void clearUserFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, context.MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores a user object to local file
     * @param user (User class)
     * @param context (Context class)
     */
    public static void saveUserInFile(User user, Context context) {
        try {
            Gson gson = new Gson();

            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(gson.toJson(user).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Collects a user from local file from the given context
     * @param context (Context class)
     * @return Patient or CareProvider (User class)
     */
    public User loadUserFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            String user_json = in.readLine();

            if (user_json != null) {
                ElasticSearchController es_controller = new ElasticSearchController();
                fis.close();
                return es_controller.jsonToUser(user_json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add problem to elastic search database using problem id as the id in elastic search
     * @param p (Problem class)
     * @param context (Context class)
     */
    public static void saveProblemInFile(Problem p, Context context) {
        try {
            Gson gson = new Gson();

            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(gson.toJson(p).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the problem from a given problem id
     * @param problem_id (Integer)
     * @param user_id (String)
     * @param context (Context class)
     * @return Problem (class)
     */
    public static Problem loadProblemFromFile(Integer problem_id, String user_id, Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            String problem_json = in.readLine();

            if (problem_json != null) {
                ElasticSearchController es_controller = new ElasticSearchController();
                fis.close();
                return es_controller.jsonToProblem(problem_json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeProblemFromFile(){}
}
