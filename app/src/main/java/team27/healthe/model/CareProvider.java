package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

/** Represents the CareProvider (User-subclass)
 * @author [fill in]
 */
public class CareProvider extends User {
    /**
     * Patients associated with the CareProvider (patient ids)
     */
    private Collection<String> patients;
    private String user_type = "care-provider"; //For identification in elastic search

    public CareProvider(String userID, String email, String phone){
        super(userID, email, phone);
        this.patients = new ArrayList();
    }

    public Collection<String> getPatients() {
        return this.patients;
    }
  
    /**
     * Adds patient p to the care-provider's list of patients
     * @param p (Patient class)
     */
    public void addPatient(String p) { this.patients.add(p); }

    /**
     * Checks if patient p is in the care-provider's patient list
     * @param p (Patient class)
     * @return void
     */
    public Boolean hasPatient(String p) { return patients.contains(p); }

    /**
     * Gets the number of patients the care-provider has
     * @return no. patients (Integer)
     */
    public int getPatientCount() {return patients.size();}


    /**
     * Removes a patient from the care-provider's list of patients
     * @param p (Patient Class)
     */
    public void removePatient(String p) {patients.remove(p);}
}
