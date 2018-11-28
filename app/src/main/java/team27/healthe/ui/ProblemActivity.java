package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.ProblemsAdapter;
import team27.healthe.model.User;

public class ProblemActivity extends AppCompatActivity {
    private User current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getUser(getIntent());

        if (savedInstanceState == null) {
            ProblemListFragment problem_fragment = ProblemListFragment.newInstance(current_user);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, problem_fragment).commit();
        }
    }

    private void getUser(Intent intent) {
        ElasticSearchController es_controller = new ElasticSearchController();
        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        this.current_user = es_controller.jsonToUser(user_json);
    }

}
