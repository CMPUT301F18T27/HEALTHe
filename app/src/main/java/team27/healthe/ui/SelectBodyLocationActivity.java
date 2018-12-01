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

import io.searchbox.core.Get;
import team27.healthe.R;
import team27.healthe.controllers.BodyLocationElasticSearchController;
import team27.healthe.controllers.PhotoElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.BodyLocation;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.controllers.ImageController;
import team27.healthe.model.Patient;
import team27.healthe.model.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SelectBodyLocationActivity extends AppCompatActivity {
    BodyLocation bl;
    ElasticSearchController esc;
    ImageController ic;
    String file_name;
    String current_user;
    File image_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        esc = new ElasticSearchController();
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

    private class BodyLocationTask extends AsyncTask<BodyLocation, Void, Void> {

        @Override
        protected Void doInBackground(BodyLocation... body_locations) {
            BodyLocationElasticSearchController bles_controller =
                    new BodyLocationElasticSearchController();
            for (BodyLocation bl : body_locations) {
                bles_controller.addBodyLocation(bl);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v){
            super.onPostExecute(null);

            callPhotoTask();
        }
    }
    private void callPhotoTask(){
        new PhotoTask().execute(image_file);
    }
    private class PhotoTask extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... files) {
            PhotoElasticSearchController pes_controller =
                    new PhotoElasticSearchController();
            for (File f : files) {
                pes_controller.addPhoto(f, f.getName());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            super.onPostExecute(null);
            startUpdateUser();
        }
    }
    protected void startUpdateUser(){
        new getUserAsync().execute(current_user);
    }

    // Async class for getting user from elastic search server
    private class getUserAsync extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... user_ids) {
            UserElasticSearchController ues = new UserElasticSearchController();

            for (String user_id: user_ids) {
                User user = ues.getUser(user_id);
                return user;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            updateUser(user);

        }
    }
    private class updateUserAsync extends AsyncTask<User, Void, Void>{
        @Override
        protected Void doInBackground(User... users){
            UserElasticSearchController ues = new UserElasticSearchController();
            for (User user: users) {
                ues.addUser(user);
            }
            return null;
        }
    }
    protected void updateUser(User user){
        Patient p = (Patient) user;
        p.addBodyLocation(bl.getBodyLocationId());
        new updateUserAsync().execute(p);
    }

    public void createBodyLocation(Context c, float x_set, float y_set){

        System.out.println("DEBUG-----"+bl.getLocationName());

        bl.setPoint(x_set, y_set);


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Label Body Location");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for email input
        final TextView bloc_title = new TextView(this);
        bloc_title.setText("Body Location: ");
        layout.addView(bloc_title);

        // Add a TextView for email
        final EditText bloc_text = new EditText(this);
//        bloc_text.setText(current_user.getEmail());
        bloc_text.setInputType(InputType.TYPE_CLASS_TEXT);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0,0,0,64);
        bloc_text.setLayoutParams(params);
        layout.addView(bloc_text); // Notice this is an add method


        dialog.setView(layout); // Again this is a set method, not add

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Adding Body Location", Toast.LENGTH_SHORT).show();
//                BodyLocationElasticSearchController blesc = new BodyLocationElasticSearchController();
//                PhotoElasticSearchController pesc = new PhotoElasticSearchController();
                bl.setLocation(bloc_text.getText().toString());
                bl.setPatientId(current_user);
                bl.setBodyLocationId(image_file.getName());
                new BodyLocationTask().execute(bl);

//                blesc.addBodyLocation(bl);
//                pesc.addPhoto(image_file, image_file.getName());
                System.out.println("FILENAME: "+file_name+"\nbody location text: "+bl.getLocationName());
                finish();

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();

        System.out.println("DEBUG-----"+bl.getLocationName());

    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100);
//    }

//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }

//    private void hide() {
//        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mControlsView.setVisibility(View.GONE);
//        mVisible = false;
//
//        // Schedule a runnable to remove the status and navigation bar after a delay
//        mHideHandler.removeCallbacks(mShowPart2Runnable);
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
//    }
//
//    @SuppressLint("InlinedApi")
//    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }

//    /**
//     * Schedules a call to hide() in delay milliseconds, canceling any
//     * previously scheduled calls.
//     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
}
