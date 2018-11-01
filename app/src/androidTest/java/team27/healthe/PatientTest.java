
package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PatientTest {
    @Test
    public void newPatient() {
        String userId = "johnsmith";
        String email = "jsmith@ualberta.ca";
        String phone = "7778889999";
        Patient p = new Patient(userId, email, phone);

         assertTrue(userId == p.getUserid());
         assertTrue(email == p.getEmail());
         assertTrue(phone == p.getPhone_number());
    }
}
