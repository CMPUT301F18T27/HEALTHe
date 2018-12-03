package team27.healthe.controllers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import team27.healthe.model.Photo;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

/**
 * Handles the saving of information when the user is offline and updates the database when the user goes back online
 * Using JSON
 * @author Chase
 */
public class OfflineController {
    private static final String FILENAME = "offline.sav";
    private static final String ADD = "ADD";
    private static final String DELETE = "DELETE";
    private static final String SPLITTER = ":";
    private static final String USER = "USER";
    private static final String PROBLEM = "PROBLEM";
    private static final String RECORD = "RECORD";
    private static final String PHOTO = "PHOTO";

    /**
     * Adding a user
     * @param user (User class)
     * @param context (Context class)
     */
    public static void addUser(User user, Context context) {
        Gson gson = new Gson();
        writeToFile(ADD + SPLITTER + USER + SPLITTER + gson.toJson(user) + "\n", context);
    }

    /**
     * Adding a problem
     * @param problem (Problem class)
     * @param context (Context class)
     */
    public static void addProblem(Problem problem, Context context) {
        Gson gson = new Gson();
        writeToFile(ADD + SPLITTER + PROBLEM + SPLITTER + gson.toJson(problem) + "\n", context);
    }

    /**
     * Deleting a problem
     * @param problem (Problem class)
     * @param context (Context class)
     */
    public static void deleteProblem(Problem problem, Context context) {
        Gson gson = new Gson();
        writeToFile(DELETE + SPLITTER + PROBLEM + SPLITTER + gson.toJson(problem) + "\n", context);
    }

    /**
     * Adding a record
     * @param record (Record class)
     * @param context (Context class)
     */
    public static void addRecord(Record record, Context context) {
        Gson gson = new Gson();
        writeToFile(ADD + SPLITTER + RECORD + SPLITTER + gson.toJson(record) + "\n", context);
    }

    /**
     * Deleting a record
     * @param record (Record class)
     * @param context (Context class)
     */
    public static void deleteRecord(Record record, Context context) {
        Gson gson = new Gson();
        writeToFile(DELETE + SPLITTER + RECORD + SPLITTER + gson.toJson(record) + "\n", context);
    }

    /**
     * Adding a photo
     * @param photo (Photo class)
     * @param context (Context ckass)
     */
    public static void addPhoto(File photo, Context context) {
        Gson gson = new Gson();
        writeToFile(ADD + SPLITTER + PHOTO + SPLITTER + gson.toJson(photo) + "\n", context);
    }

    /**
     * Deleting a photo
     * @param photo (Photo class)
     * @param context (Context class)
     */
    public static void deletePhoto(Photo photo, Context context) {
        Gson gson = new Gson();
        writeToFile(DELETE + SPLITTER + PHOTO + SPLITTER + gson.toJson(photo) + "\n", context);
    }

    /**
     * Clears tasks
     * @param context (Context class)
     */
    public static void clearTasks(Context context) {
        clearFile(context);
    }

    /**
     * Checks to see if information needs to be saved
     * @param context (Context class)
     * @return true
     */
    public static boolean hasTasks(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            String task = in.readLine();
            if (task.equals("")) {
                return false;
            } else {
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Splits the information that needs to be updated
     * @param context (Context class)
     */
    public void performTasks(Context context) {
        ArrayList<String> task_strings = getTaskStrings(context);
        for (String task: task_strings) {
            String [] parts = task.split(SPLITTER, 3);
            String operation = parts[0];
            String type = parts[1];
            String object = parts[2];

            performTask(operation, type, object, context);
        }
    }

    /**
     * Gets a list of strings that needs to be updated
     * @param context (Context class)
     * @return task_strings
     */
    private static ArrayList<String> getTaskStrings(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            ArrayList<String> task_strings = new ArrayList();

            String task = in.readLine();
            while (task != null) {
                task_strings.add(task);
                task = in.readLine();
            }
            clearFile(context);
            return task_strings;

        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Updates the elastic search with the new info that was saved offline
     * Using JSON
     * @param operation (String)
     * @param type (String)
     * @param object (String)
     * @param context (Context class)
     */
    private void performTask(String operation, String type, String object, Context context) {
        Gson gson = new Gson();
        UserElasticSearchController es_controller = new UserElasticSearchController();
        boolean success = false;
        try {
            if (operation.equals(ADD)) {
                switch (type) {
                    case USER:
                        success = UserElasticSearchController.addUser(es_controller.jsonToUser(object));
                        break;
                    case PROBLEM:
                        success = ProblemElasticSearchController.addProblem(gson.fromJson(object, Problem.class));
                        break;
                    case RECORD:
                        success = RecordElasticSearchController.addRecord(gson.fromJson(object, Record.class));
                        break;
                    case PHOTO:
                        PhotoElasticSearchController photo_controlller = new PhotoElasticSearchController();

                        File photo_file = gson.fromJson(object, File.class);
                        String id = photo_file.getName().substring(0, photo_file.getName().length() - 4);

                        success = photo_controlller.addPhoto(photo_file, id);
                        break;
                }
            } else {
                switch (type) {
                    case PROBLEM:
                        success = ProblemElasticSearchController.removeProblem(gson.fromJson(object, Problem.class).getProblemID());
                        break;
                    case RECORD:
                        success = RecordElasticSearchController.removeRecord(gson.fromJson(object, Record.class).getRecordID());
                        break;
                    case PHOTO:
                        //TODO: object will likely be id not file
                        PhotoElasticSearchController photo_controlller = new PhotoElasticSearchController();

                        File photo_file = gson.fromJson(object, File.class);
                        String id = photo_file.getName().substring(0, photo_file.getName().length() - 4);

                        success = photo_controlller.deletePhoto(id);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Server Error", "Elastic search is down again... :(");
        }

        if (!success) {
            writeTaskToFile(operation, type, object, context);
        }
    }

    /**
     * Writes to the file
     * @param string (String)
     * @param context (Context class)
     */
    private static void writeToFile(String string, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write((string).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the file
     * @param context (Context class)
     */
    private static void clearFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the offline information to the file
     * @param operation (String)
     * @param type (String)
     * @param object (String)
     * @param context (Context class)
     */
    private static void writeTaskToFile(String operation, String type, String object, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write((operation + SPLITTER + type + SPLITTER + object + "\n").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
