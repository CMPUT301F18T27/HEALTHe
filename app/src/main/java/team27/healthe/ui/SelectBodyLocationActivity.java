package team27.healthe.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.picasso.Picasso;

import com.google.gson.Gson;

import org.elasticsearch.common.UUID;

import java.io.File;

import io.searchbox.core.DocumentResult;
import team27.healthe.R;
import team27.healthe.controllers.BodyLocationElasticSearchController;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.controllers.OfflineController;
import team27.healthe.controllers.PhotoElasticSearchController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.BodyLocation;
import team27.healthe.controllers.ImageController;
import team27.healthe.model.BodyLocationPhoto;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.model.Record;
import team27.healthe.model.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SelectBodyLocationActivity extends AppCompatActivity {
    private static final int REQUEST_BODY_LOCATION_CODE = 27;
    private User current_user;
    private Record record;
    private File image_file;
    private ImageView imageView;
    private boolean setPoint = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_body_location);

        getFromIntent();

        if (current_user instanceof CareProvider) {
            Button set_point = findViewById(R.id.buttonSetPoint);
            Button set_body_button = findViewById(R.id.buttonSetBodyLocation);

            set_body_button.setVisibility(set_body_button.INVISIBLE);
            set_point.setVisibility(set_point.INVISIBLE);
        }

        imageView = (ImageView) findViewById(R.id.view_select_body_location);


        String file_name = this.getFilesDir() + File.separator + record.getBodyLocation().getBodyLocationId() + ".jpg";
        image_file = new File(file_name);

        setUI();


        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (setPoint) {
                        LocalFileController file_controller = new LocalFileController();

                        record.getBodyLocation().setPoint(event.getRawX(), event.getRawY());

                        file_controller.saveRecordInFile(record, getApplicationContext());
                        new UpdateRecord().execute(record);
                        updateReturnIntent();

                        drawPoint();
                        setPoint = false;
                    }
                }
                return true;
            }
        });


    }

    private void setUI() {
        if (image_file.exists()) {
            TextView no_body_location = (TextView) findViewById(R.id.textNoBodyLocation);
            no_body_location.setVisibility(no_body_location.INVISIBLE);

            Bitmap bitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
            imageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));

            if (record.getBodyLocation() != null) {
                drawPoint();
            }

        } else {
            imageView.setVisibility(imageView.INVISIBLE);

            Button set_point = findViewById(R.id.buttonSetPoint);
            set_point.setVisibility(set_point.INVISIBLE);
        }
    }

    private void getFromIntent() {
        Intent intent = getIntent();
        UserElasticSearchController controller = new UserElasticSearchController();
        Gson gson = new Gson();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String record_json = intent.getStringExtra(RecordActivity.RECORD_MESSAGE);

        current_user = controller.jsonToUser(user_json);
        record = gson.fromJson(record_json, Record.class);


    }

    private void drawPoint() {
        Bitmap myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
        float x_location = record.getBodyLocation().getX();
        float y_location = record.getBodyLocation().getY();

        if(x_location != 0 && y_location != 0) {
            float x_scaled = posScale(x_location, myBitmap.getWidth(), "x");
            float y_scaled = posScale(y_location, myBitmap.getHeight(), "y");
            Bitmap temp_bmp = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(),Bitmap.Config.RGB_565);
            Canvas c = new Canvas(temp_bmp);
            Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
            p.setStrokeWidth(2);
            p.setColor(Color.RED);
            c.drawBitmap(myBitmap, 0, 0, null);

            c.drawLine(x_scaled-5, y_scaled, x_scaled+5, y_scaled, p);
            c.drawLine(x_scaled, y_scaled-5, x_scaled, y_scaled+5, p);

            imageView.setImageDrawable(new BitmapDrawable(getResources(), temp_bmp));
            System.out.println("CANVAS SHOULD BE DRAWN!");
        }
        else{
            imageView.setImageBitmap(myBitmap);
        }
    }


    private float posScale(float val, float max, String orientation){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if(orientation.equals("x")){
            float scale = max/size.x;
            return scale*val;
        }
        else if (orientation.equals("y")){
            float scale = max/size.y;
            return scale*val;
        }
        return 0;
    }

    public void onClickSetBodyLocation(View view) {
        Gson gson = new Gson();
        Intent intent = new Intent(this, ViewBodyLocationsActivity.class);
        intent.putExtra(ViewBodyLocationsActivity.GET_LOCATION_MESSAGE, true);
        intent.putExtra(ViewBodyLocationsActivity.SET_FAB_MESSAGE, false);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        startActivityForResult(intent, REQUEST_BODY_LOCATION_CODE);
    }

    public void onCLickSetPoint(View view) {
        setPoint = true;
        Toast.makeText(getApplicationContext(), "Touch photo to set reference point", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent){
        if(request == REQUEST_BODY_LOCATION_CODE && result == RESULT_OK){
            LocalFileController file_controller = new LocalFileController();
            Gson gson = new Gson();
            String body_location_json = intent.getStringExtra(ViewBodyLocationsActivity.BODY_LOCATION_MESSAGE);
            BodyLocation body_location = gson.fromJson(body_location_json, BodyLocation.class);
            record.setBodyLocation(body_location);

            file_controller.saveRecordInFile(record, this);
            new UpdateRecord().execute(record);
            updateReturnIntent();

            String file_name = this.getFilesDir() + File.separator + record.getBodyLocation().getBodyLocationId() + ".jpg";
            image_file = new File(file_name);

            setUI();
        }
    }

    private void updateReturnIntent() {
        Gson gson = new Gson();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        returnIntent.putExtra(RecordActivity.RECORD_MESSAGE, gson.toJson(record));
    }

    private class UpdateRecord extends AsyncTask<Record, Void, Record> {

        @Override
        protected Record doInBackground(Record... records) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();
            for (Record record: records) {
                if(!es_controller.addRecord(record)) {
                    return record;
                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record){
            super.onPostExecute(record);
            if (record != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.addRecord(record, getApplicationContext());
            }
        }
    }
}
