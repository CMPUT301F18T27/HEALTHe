package team27.healthe.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import team27.healthe.R;
import team27.healthe.model.CareProvider;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Patient;
import team27.healthe.model.User;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    // onClick for create patient account
    public void createPatientAccount(View view) {
        // Get input values
        String user_id = ((TextView) findViewById(R.id.textUserid)).getText().toString();
        String email = ((TextView) findViewById(R.id.textEmail)).getText().toString();
        String number = ((TextView) findViewById(R.id.textNumber)).getText().toString();

        if (userIdIsValid(user_id)) {
            Patient patient = new Patient(user_id, email, number);
            new createAccountAsync().execute(patient);
        }
        else {
            Toast.makeText(this, "User ID must be at least 8 characters", Toast.LENGTH_SHORT).show();
        }
    }

    // onClick for create care provider account
    public void createCareTakerAccount(View view) {
        // Get input values
        String user_id = ((TextView) findViewById(R.id.textUserid)).getText().toString();
        String email = ((TextView) findViewById(R.id.textEmail)).getText().toString();
        String number = ((TextView) findViewById(R.id.textNumber)).getText().toString();

        if (userIdIsValid(user_id)) {
            CareProvider care_provider = new CareProvider(user_id, email, number);
            new createAccountAsync().execute(care_provider);
        }
        else {
            Toast.makeText(this, "User ID must be at least 8 characters", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean userIdIsValid(String user_id) {
        if (user_id.length() > 7) {return true;}
        else {return false;}
    }

    // Displays message to user based on success of account creation
    private void informUser(User user) {
        if (user == null) {
            Toast.makeText(this, "An account already exists for the user ID", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (user instanceof Patient) {
            Toast.makeText(this, "Patient account created", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Care provider account created", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // Async class for elastic search operations
    private class createAccountAsync extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... users) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (User user : users) {
                if (es_controller.getUser(user.getUserid()) != null) { // If account already exists
                    return null;
                } else {
                    es_controller.addUser(user);
                    return user;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            informUser(user);

        }
    }
}
