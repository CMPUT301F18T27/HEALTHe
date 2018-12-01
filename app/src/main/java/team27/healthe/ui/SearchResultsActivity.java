package team27.healthe.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.SearchResult;
import team27.healthe.R;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.SearchResultsAdapter;
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

    }

    private void getFromIntent() {
        Intent intent = getIntent();
        UserElasticSearchController es_controller = new UserElasticSearchController();
        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);

        this.search_results = intent.getStringArrayListExtra(SEARCH_MESSAGE);
        this.current_user = es_controller.jsonToUser(user_json);
    }
}
