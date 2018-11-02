package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PatientTest {
    @Test
    public void newPatient() {
        String userId = "johntitor";
        String email = "jtitor@ualberta.ca";
        String phone = "7778889999";
        Patient p = new Patient(userId, email, phone);

        assertEquals(userId, p.getUserid());
        assertEquals(email, p.getEmail());
        assertEquals(phone, p.getPhone_number());
    }

    @Test
    public void addProblem() {
        String userId = "johntitor";
        String email = "jtitor@ualberta.ca";
        String phone = "7778889999";
        Patient p = new Patient(userId, email, phone);

    }
}
