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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.model.CommentListAdapter;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class CommentActivity extends AppCompatActivity {
    private Record record;
    private CommentListAdapter adapter;
    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getItems(getIntent());

        adapter = new CommentListAdapter(this, record.getCommentList());

        final ListView list_view = (ListView) findViewById(R.id.commentListView);
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });
        list_view.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
    }

    private void getItems(Intent intent) {
        Gson gson = new Gson();
        String record_json = intent.getStringExtra(RecordActivity.RECORD_MESSAGE);
        this.record = gson.fromJson(record_json, Record.class);

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        ElasticSearchController es_controller = new ElasticSearchController();
        this.current_user = es_controller.jsonToUser(user_json);
    }

    private void addComment() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Add Comment");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        /*
        //Add title for email input
        final TextView patient_id = new TextView(getActivity());
        patient_id.setHint("Patient ID");
        layout.addView(patient_id);
        */

        // Add a TextView for email
        final EditText patient_text = new EditText(this);
        patient_text.setHint("Comment");
        patient_text.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(patient_text);

        dialog.setView(layout);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveComment(current_user.getUserid() + ":\n" + patient_text.getText().toString());

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();
    }

    private void saveComment(String comment) {
        this.record.addCommment(comment);
        ArrayList<String> comments = record.getCommentList();
        adapter.refresh(comments);

        Gson gson = new Gson();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RecordActivity.RECORD_MESSAGE,gson.toJson(record));
        setResult(RESULT_OK,returnIntent);
    }

}
