package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

public class CareProvider extends User {
    private Collection<Patient> patients;
    private String user_type = "care-provider";

    public CareProvider(String userID, String email, String phone){
        super(userID, email, phone);
        this.patients = new ArrayList();
    }

    public Collection<Patient> getPatients() {
        return this.patients;
    }

    public void addPatient(Patient p) { this.patients.add(p); }

    public Boolean hasPatient(Patient p) { return patients.contains(p); }

    public Integer getPatientCount() {return patients.size();}

    public Patient getPatient(String patientID) {
        for (Patient patient:patients) {
            if (patient.userid == patientID) { return patient;}
        }
        return null; // If patient not found
    }

    public void removePatient(Patient p) {patients.remove(p);}
}
