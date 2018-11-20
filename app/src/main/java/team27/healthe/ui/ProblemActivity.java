package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.LocalFileController;
import team27.healthe.model.Problem;
import team27.healthe.model.ProblemsAdapter;

public class ProblemActivity extends AppCompatActivity {
    public static ListView listView;
    private ProblemsAdapter adapter;
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
                addProblem();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Problem problem = (Problem) listView.getItemAtPosition(position);
                editProblem(problem);
            }
        });

        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Problem problem = (Problem) listView.getItemAtPosition(position);
                deleteProblem(problem);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFromFile();

        adapter = new ProblemsAdapter(this, problems);
        listView.setAdapter(adapter);
    }

    public void addProblem() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Add Problem");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for problem
        final TextView problem_title = new TextView(this);
        problem_title.setText("Problem Title:");
        layout.addView(problem_title);

        // Add a TextView for problem title
        final EditText title_text = new EditText(this);
        title_text.setInputType(InputType.TYPE_CLASS_TEXT);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0,0,0,64);
        title_text.setLayoutParams(params);
        layout.addView(title_text); // Notice this is an add method

        //Add title for date started
        final TextView date_title = new TextView(this);
        date_title.setText("Date Started:");
        layout.addView(date_title);

        // Add a TextView for date
        final EditText date_text = new EditText(this);
        date_text.setInputType(InputType.TYPE_CLASS_DATETIME);
        layout.addView(date_text); // Another add method

        //Add title for problem description
        final TextView description_title = new TextView(this);
        description_title.setText("Description:");
        layout.addView(description_title);

        // Add a TextView for description
        final EditText description_text = new EditText(this);
        description_text.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(description_text); // Another add method

        dialog.setView(layout);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Adding problem...", Toast.LENGTH_SHORT).show();

                String title = title_text.getText().toString();
                Date date = new Date();
                String desc = description_text.getText().toString();

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                    date = formatter.parse(date_text.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Problem problem = new Problem(title, date, desc);

                new AddProblemES().execute(problem);

                LocalFileController file_controller = new LocalFileController(FILENAME);
                file_controller.saveProblemInFile(problem, getApplicationContext());

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();
    }

    public void deleteProblem(Problem prob) {
        final Problem problem = prob;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Delete Problem");
        dialog.setMessage("Are you sure you want to delete this problem?");

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Deleting problem...", Toast.LENGTH_SHORT).show();

                new DeleteProblem().execute(problem);

                LocalFileController file_controller = new LocalFileController(FILENAME);
                file_controller.saveProblemInFile(problem, getApplicationContext());

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();

    }

    public void editProblem(Problem prob) {
        final Problem problem = prob;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit Problem");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for problem
        final TextView problem_title = new TextView(this);
        problem_title.setText("Problem Title:");
        layout.addView(problem_title);

        // Add a TextView for problem title
        final EditText title_text = new EditText(this);
        title_text.setText(problem.getTitle());
        title_text.setInputType(InputType.TYPE_CLASS_TEXT);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0,0,0,64);
        title_text.setLayoutParams(params);
        layout.addView(title_text); // Notice this is an add method

        //Add title for date started
        final TextView date_title = new TextView(this);
        date_title.setText("Date Started:");
        layout.addView(date_title);

        // Add a TextView for date
        final EditText date_text = new EditText(this);
        date_text.setText(problem.getPdateAsString());
        date_text.setInputType(InputType.TYPE_CLASS_DATETIME);
        layout.addView(date_text); // Another add method

        //Add title for problem description
        final TextView description_title = new TextView(this);
        description_title.setText("Description:");
        layout.addView(description_title);

        // Add a TextView for description
        final EditText description_text = new EditText(this);
        description_text.setText(problem.getDescription());
        description_text.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(description_text); // Another add method

        dialog.setView(layout);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Updating problem...", Toast.LENGTH_SHORT).show();

                problem.setTitle(title_text.getText().toString());
                problem.setPdateAsDateObj(date_text.getText().toString());
                problem.setDescription(description_text.getText().toString());

                new UpdateProblem().execute(problem);

                LocalFileController file_controller = new LocalFileController(FILENAME);
                file_controller.saveProblemInFile(problem, getApplicationContext());

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();
    }

    private class createAccountAsync extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (Problem problem : problems) {
                if (es_controller.getProblem(problem.getProblemID()) != null) { // If account already exists
                    return null;
                } else {
                    es_controller.addProblem(problem);
                    return problem;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
        }
    }

    private class DeleteProblem extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ElasticSearchController es_controller = new ElasticSearchController();
            for(Problem problem:problems) {
                es_controller.deleteProblem(problem);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            List<Fragment> allFragments = getSupportFragmentManager().getFragments();
            Fragment fragment  = (ProfileFragment)allFragments.get(0);
            ((ProfileFragment) fragment).deleteProblem(problem);
        }
    }

    private class UpdateProblem extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ElasticSearchController es_controller = new ElasticSearchController();
            for(Problem problem:problems) {
                es_controller.addProblem(problem);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            List<Fragment> allFragments = getSupportFragmentManager().getFragments();
            Fragment fragment  = (ProfileFragment)allFragments.get(0);
            ((ProfileFragment) fragment).updateProblem(problem);
        }
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
