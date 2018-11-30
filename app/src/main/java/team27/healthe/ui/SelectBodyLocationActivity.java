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
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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
import team27.healthe.model.BodyLocation;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.ImageController;
import team27.healthe.model.LocalFileController;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SelectBodyLocationActivity extends AppCompatActivity {
    BodyLocation bl;
    ElasticSearchController esc;
    ImageController ic;
    String file_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        esc = new ElasticSearchController();
        ic = new ImageController(getApplicationContext(), "body_locations");
        file_name = intent.getStringExtra("file_name");
        float x_location = 0;
        float y_location = 0;
        if (intent.hasExtra("x_loc") && intent.hasExtra("y_loc")){
            x_location = intent.getFloatExtra("x_loc", 0);
            y_location = intent.getFloatExtra("y_loc", 0);
        }
        setContentView(R.layout.activity_select_body_location);


        ImageView imageView = (ImageView) findViewById(R.id.view_select_body_location);
        if(x_location != 0 && y_location != 0) {
            Canvas c = new Canvas();
            Paint p = new Paint();
            p.setColor(Color.RED);
            c.drawLine(x_location-5, y_location, x_location+5, y_location, p);
            c.drawLine(x_location, y_location-5, x_location, y_location+5, p);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageView.onDrawForeground(c);
            }
            else {
                System.out.println("WARNING--SKIPPING CROSSHAIR DRAWING");
            }
        }
        System.out.println("loading: "+file_name);

        File imgFile = new File(file_name);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());



            imageView.setImageBitmap(myBitmap);

        }

        bl = new BodyLocation();
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    createBodyLocation(getApplicationContext(), event.getX(), event.getY());

                    //  textView.setText("Touch coordinates : " +String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                    System.out.println("X " + String.valueOf(event.getX()) + "");
                    System.out.println("y " + String.valueOf(event.getY()) + "");
//                    finish();
                }
                return true;
            }
        });


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

                bl.setLocation(bloc_text.getText().toString());
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
