package team27.healthe.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.notbytes.barcode_reader.BarcodeReaderActivity;

import java.util.ArrayList;
import java.util.Collection;

import team27.healthe.R;
import team27.healthe.controllers.PhotoElasticSearchController;
import team27.healthe.controllers.ProblemElasticSearchController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.model.BodyLocationPhoto;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.model.Photo;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_MESSAGE = "team27.healthe.User";
    private static final Integer BARCODE_READER_ACTIVITY_REQUEST = 27;
    private static String FILENAME = "user.sav";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadFromFile();

        EditText login_text = (EditText) findViewById(R.id.loginIdText);
        login_text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Button login = findViewById(R.id.loginButton);
                    login.performClick();
                    return true;
                }
                return false;
            }
        });

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "Error scanning QR code", Toast.LENGTH_SHORT).show();
                return;
            }

            if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
                Barcode barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE);
                login(barcode.rawValue);
            }
        }
    }

    public void onLoginClick(View view) {
        String user_id = ((TextView) findViewById(R.id.loginIdText)).getText().toString();
        login(user_id);
    }

    // onClick for the login button
    public void login(String user_id){
        if (isNetworkConnected()) {
            Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
            new getUserAsync().execute(user_id);
        }
        else {
            Toast.makeText(this, "Internet connectivity required for login", Toast.LENGTH_SHORT).show();
        }
    }

    // onClick for the account creation text
    public void createAccount(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    // Called after user is retrieved from the elastic search server
    private void handleLogin(User user, Boolean save_in_file){
        if (user != null) {
            getAllForUser(user);

            Gson gson = new Gson();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(USER_MESSAGE, gson.toJson(user));

            if (save_in_file) {
                LocalFileController file_controller = new LocalFileController();
                file_controller.saveUserInFile(user, this);
            }

            startActivity(intent);
        }
        else {
            Toast.makeText(this, "No account found for the given user ID", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    // Async class for getting user from elastic search server
    private class getUserAsync extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... user_ids) {
            UserElasticSearchController es_controller = new UserElasticSearchController();

            for (String user_id: user_ids) {
                User user = es_controller.getUser(user_id);
                return user;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            handleLogin(user, true);
        }
    }

    // Async class for getting user from elastic search server
    private class GetPatients extends AsyncTask<Collection<String>, Void, ArrayList<Patient>> {

        @Override
        protected ArrayList<Patient> doInBackground(Collection<String>... patient_id_lists) {
            UserElasticSearchController es_controller = new UserElasticSearchController();

            for (Collection<String> patient_ids: patient_id_lists) {
                ArrayList<Patient> patients = new ArrayList<>();
                for (String patient_id : patient_ids) {
                    User user = es_controller.getUser(patient_id);
                    if (user != null) {
                        if (user instanceof Patient) {
                            patients.add((Patient) user);
                        }
                    }
                }
                return patients;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Patient> patients) {
            super.onPostExecute(patients);
            LocalFileController controller = new LocalFileController();
            controller.savePatientsInFile(patients, getApplicationContext());
            for (Patient patient : patients) {
                getAllForPatient(patient);
            }
        }
    }

    // Async class for getting problems from elastic search server
    private class GetProblem extends AsyncTask<String, Void, Problem> {

        @Override
        protected Problem doInBackground(String... problem_ids) {
            ProblemElasticSearchController es_controller = new ProblemElasticSearchController();

            for (String problem_id: problem_ids) {
                return es_controller.getProblem(problem_id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            if (problem != null) {
                LocalFileController localFileController = new LocalFileController();
                localFileController.saveProblemInFile(problem, getApplicationContext());

                for (String record_id : problem.getRecords()) {
                    new GetRecord().execute(record_id);
                }
            }
        }
    }


    // Async class for getting records from elastic search server
    private class GetRecord extends AsyncTask<String, Void, Record> {

        @Override
        protected Record doInBackground(String... record_ids) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();

            for (String record_id : record_ids) {
                return es_controller.getRecord(record_id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record) {
            super.onPostExecute(record);
            if (record != null) {
                LocalFileController localFileController = new LocalFileController();
                localFileController.saveRecordInFile(record, getApplicationContext());

                for (Photo photo : record.getPhotos()) {
                    new GetPhoto().execute(photo.getId());
                }
            }
        }
    }


    // Async class for retrieving photos from elastic search
    private class GetPhoto extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... photo_ids) {
            PhotoElasticSearchController photo_controller = new PhotoElasticSearchController();

            for (String photo_id : photo_ids) {
                photo_controller.getPhoto(photo_id, getApplicationContext());
            }
            return null;
        }
    }

    private void loadFromFile() {
        LocalFileController file_controller = new LocalFileController();
        User user = file_controller.loadUserFromFile(this);
        if (user != null) {
            handleLogin(user, false);
        }
    }

    public void scanQrCode(View view) {
        // https://github.com/avaneeshkumarmaurya/Barcode-Reader
        Intent launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false);
        startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST);
    }

    // Save everything for user in elastic search locally
    private void getAllForUser(User user) {
        if (user instanceof Patient) {
            getAllForPatient((Patient) user);
        } else {
            getAllForCareProvider((CareProvider) user);
        }
    }

    // Get everything for a patient
    private void getAllForPatient(Patient patient) {
        for (String problem_id : patient.getProblemList()) {
            new GetProblem().execute(problem_id);
        }
        for (BodyLocationPhoto blp : patient.getBodyLocations()) {
            new GetPhoto().execute(blp.getBodyLocationPhotoId());
        }
    }

    // Get everything for a Care Provider
    private void getAllForCareProvider(CareProvider care_provider) {
        new GetPatients().execute(care_provider.getPatients());
    }

}
