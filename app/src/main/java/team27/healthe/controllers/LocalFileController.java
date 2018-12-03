package team27.healthe.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

/**
 * Local File access for handling operations involving storing/retrieving from local file
 * using JSON
 * @author Chase/Cody
 */
public class LocalFileController {
    private static final String USER_FILENAME = "user";
    private static final String FILE_TYPE = ".sav";
    private static final String IMAGE_FILE_TYPE = ".jpg";

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

    public static Problem loadProblemFromFile(String problem_id,  Context context) {
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

    /**
     * Saves record in the file
     * @param record (Record class)
     * @param context (Context class)
     */
    public static void saveRecordInFile(Record record, Context context) {
        Gson gson = new Gson();
        saveStringInFile(gson.toJson(record), record.getRecordID(), context);
    }

    /**
     * This loads a list of records from the file
     * @param problem (Problem class)
     * @param context (Context class)
     * @return records (class)
     */
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

    /**
     * This loads a single record from a file
     * @param record_id (String)
     * @param context (Context class)
     * @return gson.fromJson(record_json, Record.class)
     */
    public static Record loadRecordFromFile(String record_id, Context context) {
        try {
            Gson gson = new Gson();
            String record_json = getStringFromFile(record_id, context);
            return gson.fromJson(record_json, Record.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads string to a file
     * @param filename (String)
     * @param context (Context class)
     * @return file_string
     */
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

    /**
     * Deletes all files in a local directory
     * @param context (Context class)
     */
    public void deleteAllFiles(Context context) {
        String path = context.getFilesDir().getAbsolutePath(); // Path to local files directory
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile() && (file.getName().endsWith(FILE_TYPE) || file.getName().endsWith(IMAGE_FILE_TYPE))) {
                    file.delete();
            }
        }
    }

    /**
     * Saves string to a file
     * @param string_to_save (String)
     * @param filename (String)
     * @param context (Context class)
     */
    private static void saveStringInFile(String string_to_save ,String filename, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(filename + FILE_TYPE, Context.MODE_PRIVATE);
            fos.write((string_to_save).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the body location file
     * @param c (Context class)
     * @return (file)
     */
    public static File getBodyLocationFile(Context c){
        File file = new File(c.getFilesDir().getAbsolutePath());
        if (!file.exists()){
            if(!file.mkdirs()) {
                return null;
            }
        }
        return file;

    }

    /**
     * Saves and compresses an image
     * @param bitmap (bitmap)
     * @param file_name (String)
     * @param context (Context class)
     * @return File(photo_file_name)
     */
    public File saveImage(Bitmap bitmap, String file_name, Context context) {
        String photo_file_name = context.getFilesDir().getAbsolutePath() + File.separator + file_name + ".jpg";
        try {

            OutputStream output_stream = new FileOutputStream(photo_file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80 , output_stream);
            output_stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(photo_file_name);
    }

    /**
     * Deletes an image
     * @param image_id (String)
     * @param context (Context)
     */
    public void deleteImage(String image_id, Context context) {
        String photo_file_name = context.getFilesDir().getAbsolutePath() + File.separator + image_id + ".jpg";
        File image = new File(photo_file_name);
        if (image.exists()) {
            image.delete();
        }
    }
}
