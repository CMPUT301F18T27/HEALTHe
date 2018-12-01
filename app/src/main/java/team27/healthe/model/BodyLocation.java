package team27.healthe.model;

import android.graphics.Point;
import android.net.Uri;

/** Represents body location screen
 * @author Chris
 *
 */
public class BodyLocation {
//    Point location;
    float x_location;
    float y_location;
    String patient_id;//associated patient
    String body_location_id;//for elasticsearch
    String body_string;//for search/titles
    Uri uri;

    public BodyLocation(float x, float y){
//        location = new Point(x, y);
        x_location = x;
        y_location = y;
        body_string = "";
    }

    public BodyLocation(){
//        location = new Point(0,0);
        x_location = 0;
        y_location = 0;
        body_string = "";//replace hardcoded value
    }

    public void setPoint(float x, float y){
        x_location = x;
        y_location = y;
    }

    public float getX(){
        return x_location;
    }
    public float getY(){
        return y_location;
    }

    /**
     *
     * @return location name string (for display purposes) based on location
     */
    public String getLocationName(){

        return body_string;
    }
    public void setLocation(String body_location){
        body_string = body_location;
    }

    public Uri getUri(){
        return uri;
    }

    public void setUri(Uri u){
        uri = u;
    }

    public void setPatientId(String id){
        patient_id = id;
    }

    public String getPatientId(){
        return patient_id;
    }

    public void setBodyLocationId(String body_location_id){
        this.body_location_id = body_location_id;
    }

    public String getBodyLocationId(){
        return body_location_id;
    }
}
