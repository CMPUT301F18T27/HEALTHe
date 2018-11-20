package team27.healthe.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.LocalFileController;
import team27.healthe.model.Problem;

public class ProblemActivity extends AppCompatActivity {
    public static ListView listView;
    public static ArrayList<Problem> problems;
    public static String FILENAME = "problems.sav";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        listView = (ListView) findViewById(R.id.problem_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Problem problem = (Problem) listView.getItemAtPosition(position);
                editProblem(view, problem);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFromFile();

    }

    public void editProblem(View view, Problem problem) {

    }

    // Async class for getting problem from elastic search server
    private class getProblemAsync extends AsyncTask<String, Void, ArrayList<Problem>> {

        @Override
        protected Problem doInBackground(String... problem_ids) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (String problem_id: problem_ids) {
                Problem problem = es_controller.getProblem(problem_id);
                problems.add(problem);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Problem> problems) {
            super.onPostExecute(problems);
            handleLogin(problems, true);
        }
    }

    private void loadFromFile() {
        LocalFileController file_controller = new LocalFileController(FILENAME);
        Problem problem = file_controller.loadProblemFromFile(this);
        if (problem != null) {
            handleLogin(problem, false);
        }
    }

}
