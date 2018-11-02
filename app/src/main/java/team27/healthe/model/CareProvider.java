package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

public class CareProvider extends User {
    private Collection<Patient> patients;

    public CareProvider(String userID, String email, String phone){
        super(userID, email, phone);
        this.patients = new ArrayList<>();
    }

    public Collection<Patient> getPatients() {
        return this.patients;
    }

    public void addPatient(Patient p) {}

    public Boolean hasPatient(Patient p) {
        return false;
    }

    public Patient getPatient(String patientID) {
        return null;
    }

    public void removePatient(Patient p) {}
}
