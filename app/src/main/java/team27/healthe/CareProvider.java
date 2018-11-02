package team27.healthe;

import java.util.Collection;

public class CareProvider extends User {
    private Collection<Patient> patients;

    CareProvider(String userID, String email, String phone){
        super(userID, email, phone);
    }

    public void addPatient(Patient p) {}

    public Patient getPatient() {
        return null;
    }

    public Collection<Patient> getPatientList() {
        return null;
    }
}
