package team27.healthe.controllers;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import team27.healthe.model.CareProvider;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

/**
 * Local File access for handling operations involving storing/retrieving from local file
 * using JSON
 * @author Chase
 */
public class LocalFileController {
    private static final String USER_FILENAME = "user";
    private static final String FILE_TYPE = ".sav";

    /**
     * Empties the contents of the local file
     * @param context (Context)
     */
    public static void clearUserFile(Context context) {
        saveStringInFile("", USER_FILENAME, context);
    }

    /**
     * Stores a user object to local file
     * @param user (User class)
     * @param context (Context class)
     */
    public static void saveUserInFile(User user, Context context) {
        Gson gson = new Gson();
        saveStringInFile(gson.toJson(user), USER_FILENAME, context);
    }

    /**
     * Collects a user from local file from the given context
     * @param context (Context class)
     * @return Patient or CareProvider (User class)
     */
    public User loadUserFromFile(Context context) {
        try {
            String user_json = getStringFromFile(USER_FILENAME, context);

            if (user_json != null) {
                UserElasticSearchController es_controller = new UserElasticSearchController();
                return es_controller.jsonToUser(user_json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Stores a list of patient objects to local file
     * @param patients (ArrayList class)
     * @param context (Context class)
     */
    public static void savePatientsInFile(ArrayList<Patient> patients, Context context) {
        for (Patient patient: patients) {
            savePatientInFile(patient, context);
        }
    }

    private static void savePatientInFile(Patient patient, Context context){
        Gson gson = new Gson();
        saveStringInFile(gson.toJson(patient), patient.getUserid(), context);
    }


    /**
     * Collects patients from local file from the given context
     * @param context (Context class)
     * @return patients (ArrayList class)
     */
    public ArrayList<Patient> loadPatientsFromFile(CareProvider care_provider, Context context) {
        ArrayList<Patient> patients = new ArrayList<>();
        for (String patient_id: care_provider.getPatients()) {
            Patient patient = loadPatientFromFile(patient_id, context);
            if (patient != null) {
                patients.add(patient);
            }
        }
        return patients;
    }

    private static Patient loadPatientFromFile (String patient_id, Context context) {
        try {
            UserElasticSearchController es_controller = new UserElasticSearchController();
            String user_json = getStringFromFile(patient_id, context);
            return (Patient) es_controller.jsonToUser(user_json);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add problem to local file
     * @param problem (Problem class)
     * @param context (Context class)
     */
    public static void saveProblemInFile(Problem problem, Context context) {
        Gson gson = new Gson();
        saveStringInFile(gson.toJson(problem), problem.getProblemID(), context);
    }

    /**
     * Add problems to lcoal database
     * @param problems (ArrayList class)
     * @param context (Context class)
     */
    public static void saveProblemsInFile(ArrayList<Problem> problems, Context context) {
        for (Problem problem: problems) {
            saveProblemInFile(problem, context);
        }
    }

    /**
     * Get the problems from local file
     * @param context (Context class)
     * @return Problem (class)
     */
    public static ArrayList<Problem> loadProblemsFromFile(Patient patient, Context context) {
        ArrayList<Problem> problems = new ArrayList<>();
        for (String problem_id: patient.getProblemList()) {
            Problem problem = loadProblemFromFile(problem_id, context);
            if (problem != null) {
                problems.add(problem);
            }
        }
        return problems;
    }

    private static Problem loadProblemFromFile(String problem_id,  Context context) {
        try {
            Gson gson = new Gson();
            String problem_json = getStringFromFile(problem_id, context);

            return gson.fromJson(problem_json, Problem.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add problems to local database
     * @param records (ArrayList class)
     * @param context (Context class)
     */
    public static void saveRecordsInFile(ArrayList<Record> records, Context context) {
        for (Record record: records) {
            saveRecordInFile(record, context);
        }
    }

    public static void saveRecordInFile(Record record, Context context) {
        Gson gson = new Gson();
        saveStringInFile(gson.toJson(record), record.getRecordID(), context);
    }

    public static ArrayList<Record> loadRecordsFromFile(Problem problem, Context context) {
        ArrayList<Record> records = new ArrayList<>();
        for (String record_id: problem.getRecords()) {
            Record record = loadRecordFromFile(record_id, context);
            if (record != null) {
                records.add(record);
            }
        }
        return records;
    }

    private static Record loadRecordFromFile(String record_id, Context context) {
        try {
            Gson gson = new Gson();
            String record_json = getStringFromFile(record_id, context);
            return gson.fromJson(record_json, Record.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getStringFromFile(String filename, Context context) {
        try {
            FileInputStream fis = context.openFileInput(filename + FILE_TYPE);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            String file_string = in.readLine();
            fis.close();

            return file_string;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteAllFiles() {
        //TODO: find and delete all .sav files in local directory
    }

    private static void saveStringInFile(String string_to_save ,String filename, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(filename + FILE_TYPE, Context.MODE_PRIVATE);
            fos.write((string_to_save).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
