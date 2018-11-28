package team27.healthe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class RecordActivity extends AppCompatActivity {
    public static final String RECORD_MESSAGE = "team27.healthe.RECORD";
    private static final Integer GEO_REQUEST_CODE = 4;
    private static final Integer COMMENT_REQUEST_CODE = 5;
    private User current_user;
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getItems(getIntent());
        setTextViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GEO_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                Gson gson = new Gson();
                String record_json = data.getStringExtra(RECORD_MESSAGE);
                this.record = gson.fromJson(record_json, Record.class);
            }
        }

        if (requestCode == COMMENT_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                Gson gson = new Gson();
                String record_json = data.getStringExtra(RECORD_MESSAGE);
                this.record = gson.fromJson(record_json, Record.class);
            }
        }
    }

    private void getItems(Intent intent) {
        ElasticSearchController es_controller = new ElasticSearchController();
        Gson gson = new Gson();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String record_json = intent.getStringExtra(RECORD_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);
        this.record = gson.fromJson(record_json, Record.class);
    }

    private void setTextViews() {
        TextView title = (TextView) findViewById(R.id.recordTitle);
        TextView date = (TextView) findViewById(R.id.recordDate);
        TextView description = (TextView) findViewById(R.id.recordDescription);

        title.setText(record.getTitle());
        date.setText(record.getRdate().toString());
        description.setText(record.getDescription());
    }

    public void onClickComments(View view) {
        Gson gson = new Gson();
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, COMMENT_REQUEST_CODE);
    }

    public void onClickPhotos(View view){
        Gson gson = new Gson();
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivity(intent);

    }

    public void onClickBodyLocation(View view){
        Gson gson = new Gson();
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivity(intent);

    }

    public void onClickGeoLocation(View view){
        Gson gson = new Gson();
        Intent intent = new Intent(this, GeoLocationActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, GEO_REQUEST_CODE);

    }

}
