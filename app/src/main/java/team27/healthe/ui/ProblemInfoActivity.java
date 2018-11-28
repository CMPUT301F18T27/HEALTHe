package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.LocalFileController;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class ProblemInfoActivity extends AppCompatActivity {
    public static final String PROBLEM_MESSAGE = "team27.healthe.PROBLEM";

    private User current_user;
    private Problem problem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_info);

        getExtras(getIntent());
        setTextViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_problem_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_problem_edit) {
            deleteProblem();
        }

        return super.onOptionsItemSelected(item);
    }

    // Get user and problem from intent
    private void getExtras(Intent intent) {
        Gson gson = new Gson();
        ElasticSearchController es_controller = new ElasticSearchController();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String problem_json = intent.getStringExtra(PROBLEM_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);
        this.problem = gson.fromJson(problem_json, Problem.class);
    }

    private void setTextViews() {
        TextView title = (TextView) findViewById(R.id.problemTitle);
        TextView date = (TextView) findViewById(R.id.problemDate);
        TextView description = (TextView) findViewById(R.id.problemDescription);

        title.setText(problem.getTitle());
        date.setText(problem.getPdateAsString());
        description.setText(problem.getDescription());
    }

    public void onSelectRecords(View view) {
        Intent intent = new Intent(this, RecordListActivity.class);
        startActivity(intent);
    }

    private void deleteProblem() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Delete Problem");
        dialog.setMessage("Are you sure you want to delete this problem?");

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Deleting problem...", Toast.LENGTH_SHORT).show();

                new DeleteProblem().execute(problem);

                LocalFileController file_controller = new LocalFileController();
                file_controller.removeProblemFromFile();

                finish();

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();

    }

    private class DeleteProblem extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ElasticSearchController es_controller = new ElasticSearchController();
            for(Problem problem:problems) {
                es_controller.removeProblem(problem.getProblemID());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
        }
    }

}
