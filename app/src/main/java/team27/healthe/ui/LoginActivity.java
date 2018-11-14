package team27.healthe.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.concurrent.FutureTask;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Patient;
import team27.healthe.model.User;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_MESSAGE = "team27.healthe.User";
    public static final String USER_TYPE_MESSAGE = "team27.healthe.UserType";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // onClick for the login button
    public void login(View view){
        String user_id = ((TextView) findViewById(R.id.loginIdText)).getText().toString();
        Toast.makeText(this, "Logging in...", Toast.LENGTH_LONG).show();
        new getUserAsync().execute(user_id);
    }

    // onClick for the account creation text
    public void createAccount(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    // Called after user is retrieved from the elastic search server
    private void handleLogin(User user){
        if (user != null) {
            Gson gson = new Gson();

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(USER_MESSAGE, gson.toJson(user));

            if (user instanceof Patient) { intent.putExtra(USER_TYPE_MESSAGE, "patient"); }
            else { intent.putExtra(USER_TYPE_MESSAGE, "care-provider"); }

            startActivity(intent);
        }
        else {
            Toast.makeText(this, "No account found for the given user ID", Toast.LENGTH_LONG).show();
        }
    }

    // Async class for getting user from elastic search server
    private class getUserAsync extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... user_ids) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (String user_id: user_ids) {
                User user = es_controller.getUser(user_id);
                return user;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            handleLogin(user);
        }
    }

}
