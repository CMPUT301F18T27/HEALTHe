package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

/** Represents the CareProvider (User-subclass)
 * @author [fill in]
 */
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

    public ArrayList<Patient> getPatientsArray() {
        return new ArrayList<Patient>(this.patients);
    }
  
    /**
     * Adds patient p to the care-provider's list of patients
     * @param p (Patient class)
     */
    public void addPatient(Patient p) { this.patients.add(p); }

    /**
     * Checks if patient p is in the care-provider's patient list
     * @param p (Patient class)
     * @return void
     */
    public Boolean hasPatient(Patient p) { return patients.contains(p); }

    /**
     * Gets the number of patients the care-provider has
     * @return no. patients (Integer)
     */
    public Integer getPatientCount() {return patients.size();}

    /**
     * Get a patient object from a given patient ID
     * @param patientID (String)
     * @return Patient (class) if found, null otherwise
     */
    public Patient getPatient(String patientID) {
        for (Patient patient:patients) {
            if (patient.userid == patientID) { return patient;}
        }
        return null; // If patient not found
    }

    /**
     * Removes a patient from the care-provider's list of patients
     * @param p (Patient Class)
     */
    public void removePatient(Patient p) {patients.remove(p);}
}
