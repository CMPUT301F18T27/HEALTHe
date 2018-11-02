package team27.healthe.model;

import java.util.Collection;

public class CareProvider extends User {
    private Collection<Patient> patients;

    CareProvider(String userID, String email, String phone){
        super(userID, email, phone);
    }

    public Collection<Patient> getPatients() {
        return this.patients;
    }

    public void addPatient(Patient p) {}

    public Patient getPatient(String patientID) {
        return null;
    }

    public void removePatient(Patient p) {}
}
