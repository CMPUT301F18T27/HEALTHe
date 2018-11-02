package team27.healthe.model;

import android.graphics.Point;

public class BodyLocation {
    Point location;
    String body_string;

    public BodyLocation(int x, int y){
        location = new Point(x, y);
        body_string = getLocationName();
    }

    public void setPoint(int x, int y){
        location.x = x;
        location.y = y;
    }

    public Point getPoint(){
        return location;
    }

    private String getLocationName(){

        return null;
    }
}
