package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CareProviderTest {
    @Test
    public void newCareProvider() {
        String userId = "robinwilliams";
        String email = "rwilliams@ualberta.ca";
        String phone = "4445556666";
        CareProvider c = new CareProvider(userId, email, phone);

        assertEquals(userId, c.getUserid());
        assertEquals(email, c.getEmail());
        assertEquals(phone, c.getPhone_number());

    }

    @Test
    public void addPatient() {
        String userId = "robinwilliams";
        String email = "rwilliams@ualberta.ca";
        String phone = "4445556666";
        CareProvider c = new CareProvider(userId, email, phone);

        String patientId = "johntitor";
        String patientEmail = "jtitor@ualberta.ca";
        String patientPhone = "7778889999";
        Patient p = new Patient(patientId, patientEmail, patientPhone);


    }
}
