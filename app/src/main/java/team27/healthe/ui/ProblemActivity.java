package team27.healthe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.User;

public class ProblemActivity extends AppCompatActivity {
    public static final String VIEWING_USER_MESSAGE = "team27.healthe.VIEWING_USER";
    private User current_user;
    private User viewing_user;
    private ProblemListFragment problem_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getUser(getIntent());

        if (savedInstanceState == null) {
            problem_fragment = ProblemListFragment.newInstance(current_user, viewing_user);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, problem_fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_problem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_problem_map_view) {
            problem_fragment.getAllGeoLocations();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUser(Intent intent) {
        ElasticSearchController es_controller = new ElasticSearchController();
        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String viewing_user_json = intent.getStringExtra(VIEWING_USER_MESSAGE);
        this.current_user = es_controller.jsonToUser(user_json);
        this.viewing_user = es_controller.jsonToUser(viewing_user_json);

    }

}
