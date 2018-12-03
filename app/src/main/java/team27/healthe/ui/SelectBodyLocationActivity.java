package team27.healthe.ui;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.picasso.Picasso;

import java.io.File;

import team27.healthe.R;
import team27.healthe.controllers.BodyLocationPhotoElasticSearchController;
import team27.healthe.controllers.PhotoElasticSearchController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.BodyLocation;
import team27.healthe.controllers.ImageController;
import team27.healthe.model.BodyLocationPhoto;
import team27.healthe.model.Patient;
import team27.healthe.model.Record;
import team27.healthe.model.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SelectBodyLocationActivity extends AppCompatActivity {
    BodyLocation bl;
    ImageController ic;
    String file_name;
    String current_user;
    File image_file;
    String record_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ic = new ImageController(getApplicationContext(), "body_locations");
        file_name = intent.getStringExtra("file_name");
        float x_location = 0;
        float y_location = 0;
        current_user = "";
        if (intent.hasExtra("x_loc") && intent.hasExtra("y_loc")){
            x_location = intent.getFloatExtra("x_loc", 0);
            y_location = intent.getFloatExtra("y_loc", 0);
        }
        if (intent.hasExtra("current_user")){
            current_user = intent.getStringExtra("current_user");
        }
        if (intent.hasExtra("record_id")){
            record_id = intent.getStringExtra("record_id");
        }

        setContentView(R.layout.activity_select_body_location);

        ImageView imageView = (ImageView) findViewById(R.id.view_select_body_location);

        System.out.println("loading: "+file_name);

        //test values
//        x_location = 50;
//        y_location = 50;
        image_file = new File(file_name);

        if(image_file.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());

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


        bl = new BodyLocation();
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    createBodyLocation(getApplicationContext(), event.getRawX(), event.getRawY());
                    System.out.println("X " + String.valueOf(event.getRawX()) + "");
                    System.out.println("y " + String.valueOf(event.getRawY()) + "");
                }
                return true;
            }
        });


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


    public void createBodyLocation(Context c, float x_set, float y_set){
        bl.setPoint(x_set, y_set);
        new createBodyLocationTask().execute(record_id);
//        new BodyLocationTask().execute();
    }

    private class createBodyLocationTask extends AsyncTask<String, Void, Record> {
        @Override
        protected Record doInBackground(String... record_ids) {
            RecordElasticSearchController res = new RecordElasticSearchController();

            for (String record_id: record_ids) {
                Record r = res.getRecord(record_id);
                return r;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record r) {
            super.onPostExecute(r);
            updateRecord(r);
        }
//        @Override
//        protected User doInBackground(String... user_ids) {
//            UserElasticSearchController ues = new UserElasticSearchController();
//
//            for (String user_id: user_ids) {
//                User user = ues.getUser(user_id);
//                return user;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(User user) {
//            super.onPostExecute(user);
//            getRecord(user);
//        }

    }

//    private void getRecord(User u){
//        cur_patient = (Patient) u;
//        new updateRecordTask().execute(record_id);
//    }



    private void updateRecord(Record r){
        r.setBodyLocation(bl);
        new updateRecordTask().execute(r);
    }

    private class updateRecordTask extends AsyncTask<Record, Void, Void> {

        @Override
        protected Void doInBackground(Record... records) {
            RecordElasticSearchController res = new RecordElasticSearchController();

            for (Record record: records) {
                res.addRecord(record);
//                Record r = res.getRecord(record_id);
//                return r;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
//            updateRecord();
            finish();
        }
    }
//    private class BodyLocationPhotoTask extends AsyncTask<BodyLocationPhoto, Void, Void> {
//
//        @Override
//        protected Void doInBackground(BodyLocationPhoto... body_locations_photos) {
//            BodyLocationPhotoElasticSearchController bles_controller =
//                    new BodyLocationPhotoElasticSearchController();
//            for (BodyLocationPhoto bl : body_locations_photos) {
//                bles_controller.addBodyLocationPhoto(bl);
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void v){
//            super.onPostExecute(null);
//
//            callPhotoTask();
//        }
//    }
//
//    private void callPhotoTask(){
//        new PhotoTask().execute(image_file);
//    }
//
//    private class PhotoTask extends AsyncTask<File, Void, Void> {
//        @Override
//        protected Void doInBackground(File... files) {
//            PhotoElasticSearchController pes_controller =
//                    new PhotoElasticSearchController();
//            for (File f : files) {
//                pes_controller.addPhoto(f, f.getName());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void v){
//            super.onPostExecute(null);
//            startUpdateUser();
//        }
//    }
//    protected void startUpdateUser(){
//        new getUserAsync().execute(current_user);
//    }
//
//    // Async class for getting user from elastic search server

//    private class updateUserAsync extends AsyncTask<User, Void, Void>{
//        @Override
//        protected Void doInBackground(User... users){
//            UserElasticSearchController ues = new UserElasticSearchController();
//            for (User user: users) {
//                ues.addUser(user);
//            }
//            return null;
//        }
//    }
//    protected void updateUser(User user){
//        Patient p = (Patient) user;
//        p.addBodyLocation(bl);//.getBodyLocationId()
//        new updateUserAsync().execute(p);
//    }


}
