package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Record;

public class RecordActivity extends AppCompatActivity {
    private Record current_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getRecordFromIntent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSlideshow();
            }
        });
        
    }

    private void getRecordFromIntent() {
        Intent intent = getIntent();
        String record_json = intent.getStringExtra(RecordListActivity.RECORD_INFO);
        // TODO: decide on elastic search?
        //ElasticSearchController es_controller = new ElasticSearchController();
        //this.current_record = es_controller.jsonToRecord(record_json);

    }

    private void showSlideshow() {

    }

}
