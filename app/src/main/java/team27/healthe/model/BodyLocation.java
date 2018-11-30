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
    String body_string;
    Uri uri;

    public BodyLocation(float x, float y){
//        location = new Point(x, y);
        x_location = x;
        y_location = y;
        body_string = getLocationName();
    }

    public BodyLocation(){
//        location = new Point(0,0);
        x_location = 0;
        y_location = 0;
        body_string = "default-nochoice";//replace hardcoded value
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
}
