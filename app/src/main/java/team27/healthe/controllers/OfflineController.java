package team27.healthe.controllers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import team27.healthe.model.Photo;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class OfflineController {
    private static final String FILENAME = "offline.sav";
    private static final String ADD = "ADD";
    private static final String DELETE = "DELETE";
    private static final String SPLITTER = ":";
    private static final String USER = "USER";
    private static final String PROBLEM = "PROBLEM";
    private static final String RECORD = "RECORD";
    private static final String PHOTO = "PHOTO";

    public static void addUser(User user, Context context) {
        Gson gson = new Gson();
        writeToFile(ADD + SPLITTER + USER + SPLITTER + gson.toJson(user) + "\n", context);
    }

    public static void addProblem(Problem problem, Context context) {
        Gson gson = new Gson();
        writeToFile(ADD + SPLITTER + PROBLEM + SPLITTER + gson.toJson(problem) + "\n", context);
    }

    public static void deleteProblem(Problem problem, Context context) {
        Gson gson = new Gson();
        writeToFile(DELETE + SPLITTER + PROBLEM + SPLITTER + gson.toJson(problem) + "\n", context);
    }

    public static void addRecord(Record record, Context context) {
        Gson gson = new Gson();
        writeToFile(ADD + SPLITTER + RECORD + SPLITTER + gson.toJson(record) + "\n", context);
    }

    public static void deleteRecord(Record record, Context context) {
        Gson gson = new Gson();
        writeToFile(DELETE + SPLITTER + RECORD + SPLITTER + gson.toJson(record) + "\n", context);
    }

    public static void addPhoto(Photo photo, Context context) {
        Gson gson = new Gson();
        writeToFile(ADD + SPLITTER + PHOTO + SPLITTER + gson.toJson(photo) + "\n", context);
    }

    public static void deletePhoto(Photo photo, Context context) {
        Gson gson = new Gson();
        writeToFile(DELETE + SPLITTER + PHOTO + SPLITTER + gson.toJson(photo) + "\n", context);
    }

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

    public static void performTasks(Context context) {
        ArrayList<String> task_strings = getTaskStrings(context);
        for (String task: task_strings) {
            String [] parts = task.split(SPLITTER, 3);
            String operation = parts[0];
            String type = parts[1];
            String object = parts[2];

            performTask(operation, type, object, context);
        }
    }

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

    private static void performTask(String operation, String type, String object, Context context) {
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
                        //TODO: handle photos
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
                        //TODO: handle photos
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

    private static void writeToFile(String string, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write((string).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeTaskToFile(String operation, String type, String object, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write((operation + SPLITTER + type + SPLITTER + object + "\n").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Seems below code is unneeded
    /*
    private class AddUser extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            UserElasticSearchController es_controller = new UserElasticSearchController();
            for (User user : users) {
                es_controller.addUser(user);
            }
            return null;
        }
    }

    private class AddProblem extends AsyncTask<Problem, Void, Void> {

        @Override
        protected Void doInBackground(Problem... problems) {
            ProblemElasticSearchController es_controller = new ProblemElasticSearchController();
            for (Problem problem : problems) {
                es_controller.addProblem(problem);
            }
            return null;
        }
    }

    private class DeleteProblem extends AsyncTask<Problem, Void, Void> {

        @Override
        protected Void doInBackground(Problem... problems) {
            ProblemElasticSearchController es_controller = new ProblemElasticSearchController();
            for (Problem problem : problems) {
                es_controller.removeProblem(problem.getProblemID());
            }
            return null;
        }
    }

    private class AddRecord extends AsyncTask<Record, Void, Void> {

        @Override
        protected Void doInBackground(Record... records) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();
            for (Record record : records) {
                es_controller.addRecord(record);
            }
            return null;
        }
    }

    private class DeleteRecord extends AsyncTask<Record, Void, Void> {

        @Override
        protected Void doInBackground(Record... records) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();
            for (Record record : records) {
                es_controller.removeRecord(record.getRecordID());
            }
            return null;
        }
    }

    // TODO: Handle photos

    private class AddPhoto extends AsyncTask<Photo, Void, Void> {

        @Override
        protected Void doInBackground(Photo... photos) {
            PhotoElasticSearchController es_controller = new PhotoElasticSearchController();
            for (Photo photo : photos) {
                es_controller.addPhoto(photo, photo.getId());
            }
            return null;
        }
    }

    */
}
