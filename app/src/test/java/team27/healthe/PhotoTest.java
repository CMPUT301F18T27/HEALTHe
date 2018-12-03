package team27.healthe;

import org.junit.Test;

import team27.healthe.model.BodyLocation;
import team27.healthe.model.Photo;

import static org.junit.Assert.*;

public class PhotoTest {
    @Test
    public void testNewPatientConstructor() {
        Photo p1 = new Photo();
        // random string id
        assert (p1.getId() instanceof String);

        String photoID = "23j35";
        Photo p2 = new Photo(photoID);
        assertEquals(photoID, p2.getId());

        BodyLocation bodyLoc = new BodyLocation();
        Photo p3 = new Photo(bodyLoc);
    }

    @Test
    public void testID() {
        String pid = "3f4e6";
        Photo p = new Photo(pid);

        assertEquals(pid, p.getId());
        String newpid = "3sd9vc3345";
        p.setId(newpid);
        assertEquals(newpid, p.getId());
    }
}