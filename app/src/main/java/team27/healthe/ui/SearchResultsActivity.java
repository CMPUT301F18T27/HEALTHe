package team27.healthe.ui;

// Activity for displaying search results

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import team27.healthe.R;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.controllers.SearchResultsAdapter;
import team27.healthe.model.User;

public class SearchResultsActivity extends AppCompatActivity {
    public static final String SEARCH_MESSAGE = "team27.healthe.SEARCH";
    private User current_user;
    private ArrayList<String> search_results;
    private SearchResultsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        getFromIntent();

        adapter = new SearchResultsAdapter(this, search_results);

        final ListView list_view = findViewById(R.id.search_results_list);
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String json_string = (String) list_view.getItemAtPosition(position);
                viewSearchRsult(json_string);
            }
        });

    }

    // Get items from intent
    private void getFromIntent() {
        Intent intent = getIntent();
        UserElasticSearchController es_controller = new UserElasticSearchController();
        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);

        this.search_results = intent.getStringArrayListExtra(SEARCH_MESSAGE);
        this.current_user = es_controller.jsonToUser(user_json);
    }

    // View selected search result and call proper activity
    private void viewSearchRsult(String json_string) {
        Gson gson = new Gson();
        Map hit_map = gson.fromJson(json_string, Map.class);

        if (hit_map.containsKey("problem_id")) {
            Problem problem = gson.fromJson(json_string, Problem.class);
            Intent intent = new Intent(this, ProblemInfoActivity.class);
            intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
            intent.putExtra(ProblemInfoActivity.PROBLEM_MESSAGE, gson.toJson(problem));
            startActivity(intent);


        } else {
            Record record = gson.fromJson(json_string, Record.class);
            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
            intent.putExtra(RecordActivity.RECORD_MESSAGE, gson.toJson(record));
            startActivity(intent);
        }


    }
}
