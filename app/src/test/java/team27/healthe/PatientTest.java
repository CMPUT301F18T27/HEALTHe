package team27.healthe;

import org.junit.Test;

public class PatientTest {
    @Test
    public void newPatient() {
        String name = "John Smith";
        String email = "jsmith@ualberta.ca";
        String number = "7778889999";
        Patient p = new Patient(name, email, number);

        
    }
}
