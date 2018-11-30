package team27.healthe.model;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Local File access for handling operations involving storing/retrieving from local file
 * using JSON
 * @author Chase
 */
public class LocalFileController {
    private static final String PROBLEM_FILENAME = "problems.sav";
    private static final String USER_FILENAME = "user.sav";
    private static final String PATIENTS_FILENAME = "patients.sav";
    private static final String RECORD_FILENAME = "records.sav";

    /**
     * Empties the contents of the local file
     * @param context (Context)
     */
    public static void clearUserFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(USER_FILENAME, context.MODE_PRIVATE);
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

            FileOutputStream fos = context.openFileOutput(USER_FILENAME, Context.MODE_PRIVATE);
            fos.write(gson.toJson(user).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores a list of patient objects to local file
     * @param patients (ArrayList class)
     * @param context (Context class)
     */
    public static void savePatientsInFile(ArrayList<Patient> patients, Context context) {
        try {
            Gson gson = new Gson();

            FileOutputStream fos = context.openFileOutput(PATIENTS_FILENAME, Context.MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();

            fos = context.openFileOutput(PATIENTS_FILENAME, Context.MODE_APPEND);

            for (User user: patients) {
                fos.write((gson.toJson(user)+ "\n").getBytes());
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Collects patients from local file from the given context
     * @param context (Context class)
     * @return patients (ArrayList class)
     */
    public ArrayList<Patient> loadPatientsFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(PATIENTS_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            ElasticSearchController es_controller = new ElasticSearchController();
            ArrayList<Patient> patients = new ArrayList<>();
            String user_json = in.readLine();

            while (user_json != null) {
                patients.add((Patient) es_controller.jsonToUser(user_json));
                user_json = in.readLine();
            }
            fis.close();
            return patients;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Collects a user from local file from the given context
     * @param context (Context class)
     * @return Patient or CareProvider (User class)
     */
    public User loadUserFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(USER_FILENAME);
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
     * Add problem to local file
     * @param p (Problem class)
     * @param context (Context class)
     */
    public static void saveProblemInFile(Problem p, Context context) {
        try {
            Gson gson = new Gson();

            FileOutputStream fos = context.openFileOutput(PROBLEM_FILENAME, Context.MODE_APPEND);
            fos.write((gson.toJson(p) + "\n").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add problems to lcoal database
     * @param problems (ArrayList class)
     * @param context (Context class)
     */
    public static void saveProblemsInFile(ArrayList<Problem> problems, Context context) {
        try {
            Gson gson = new Gson();
            FileOutputStream fos = context.openFileOutput(PROBLEM_FILENAME, Context.MODE_PRIVATE);

            for (Problem problem:problems) {
                fos.write((gson.toJson(problem) + "\n").getBytes());
            }
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the problems from local file
     * @param context (Context class)
     * @return Problem (class)
     */
    public static ArrayList<Problem> loadProblemsFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(PROBLEM_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();
            ArrayList<Problem> problems = new ArrayList<>();
            String problem_json = in.readLine();

            while (problem_json != null) {
                problems.add(gson.fromJson(problem_json,Problem.class));
                problem_json = in.readLine();
            }
            fis.close();
            return problems;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeProblemFromFile(){}

    /**
     * Add problems to local database
     * @param records (ArrayList class)
     * @param context (Context class)
     */
    public static void saveRecordsInFile(ArrayList<Record> records, Context context) {
        try {
            Gson gson = new Gson();
            FileOutputStream fos = context.openFileOutput(RECORD_FILENAME, Context.MODE_PRIVATE);

            for (Record record : records) {
                fos.write((gson.toJson(record) + "\n").getBytes());
            }
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Record> loadRecordsFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(RECORD_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();
            ArrayList<Record> records = new ArrayList<>();
            String record_json = in.readLine();

            while (record_json!= null) {
                records.add(gson.fromJson(record_json,Record.class));
                record_json = in.readLine();
            }
            fis.close();
            return records;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveRecordInFile(Record r, Context context) {
        try {
            Gson gson = new Gson();

            FileOutputStream fos = context.openFileOutput(RECORD_FILENAME, Context.MODE_APPEND);
            fos.write((gson.toJson(r) + "\n").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeRecordFromFile() {}
}
