package team27.healthe;

import android.graphics.Point;

import org.junit.Before;
import org.junit.Test;

import team27.healthe.model.BodyLocation;

public class BodyLocationTest {
    BodyLocation l;
    @Before
    public void testSetup(){
        l = new BodyLocation();
    }

    @Test
    public void setGetLocationTest(){
        Point p = l.getPoint();
        assert(p.x == 0 && p.y == 0);
        l.setPoint(1, 1);
        p = l.getPoint();
        assert(p.x == 1 && p.y == 1);
    }

    @Test
    public void constructorTest(){
        BodyLocation new_l = new BodyLocation(10, 11);
        Point p = new_l.getPoint();
        assert(p.x == 10 && p.y == 11);
    }

    @Test
    public void getLocationNameTest(){
        l.setPoint(0, 40); // supposed location of head - change when determined
        assert(l.getLocationName().equals("Head"));
    }
}
