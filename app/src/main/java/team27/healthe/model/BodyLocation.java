package team27.healthe.model;

import android.graphics.Point;

/** Represents body location screen
 * @author Chris
 *
 */
public class BodyLocation {
    Point location;
    String body_string;

    public BodyLocation(int x, int y){
        location = new Point(x, y);
        body_string = getLocationName();
    }

    public BodyLocation(){
        location = new Point(0,0);
        body_string = "default-nochoice";//replace hardcoded value
    }

    public void setPoint(int x, int y){
        location.set(x, y);
    }

    public Point getPoint(){
        return location;
    }

    /**
     *
     * @return location name string (for display purposes) based on location
     */
    public String getLocationName(){

        return null;
    }
}
