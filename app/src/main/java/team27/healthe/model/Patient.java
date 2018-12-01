package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a patient
 * @author [fill in]
 */
public class Patient extends User {
    private ArrayList<String> problems;
    private ArrayList<String> body_locations;
    private String user_type = "patient";

    public Patient(String userID, String email, String phone){
        super(userID, email, phone);
        this.problems = new ArrayList();
    }

    public Collection<String> getProblemList() {
        return this.problems;
    }

    /**
     * Adds a problem id to the patient's problem list
     * @param p (String) problem_id
     */
    public void addProblem(String p) {problems.add(p);}

    /**
     * checks if the given problem exists in the user's list
     * @param p (String)
     * @return True if exists, False otherwise
     */
    public Boolean hasProblem(String p) { return problems.contains(p); }

    // @TODO: problem title is not unique. finalize collection class to determine addressing
    public Problem getProblem(String problemTitle) {
        return null;
    }

    /**
     * Removes a problem from the user's list
     * @param p (String) problem id
     */
    public void removeProblem(String p) {problems.remove(p);}

    /**
     * Returns the number of problems in the user's problem list
     * @return size (Integer)
     */
    public Integer getProblemCount() {return problems.size();}

    public Integer getBodyLocationCount(){return body_locations.size();}

    public void addBodyLocation(String body_location_id){
        body_locations.add(body_location_id);
    }
    public ArrayList<String> getBodyLocations(){
        return body_locations;
    }


}
