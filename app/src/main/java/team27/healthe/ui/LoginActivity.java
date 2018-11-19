package team27.healthe.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.FutureTask;

import team27.healthe.R;
import team27.healthe.model.CareProvider;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.User;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_MESSAGE = "team27.healthe.User";


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

    }

    // onClick for the login button
    public void login(View view){
        if (isNetworkConnected()) {
            String user_id = ((TextView) findViewById(R.id.loginIdText)).getText().toString();
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
            handleLogin(user, true);
        }
    }

    private void loadFromFile() {
        LocalFileController file_controller = new LocalFileController();
        User user = file_controller.loadUserFromFile(this);
        if (user != null) {
            handleLogin(user, false);
        }
    }

}
