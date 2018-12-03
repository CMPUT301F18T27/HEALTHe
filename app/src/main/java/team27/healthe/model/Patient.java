package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a patient
 * @author Cody
 * @author Chris
 */
public class Patient extends User {
    private ArrayList<String> problems;
    private ArrayList<BodyLocationPhoto> body_locations;
    private String user_type = "patient";

    public Patient(String userID, String email, String phone){
        super(userID, email, phone);
        this.problems = new ArrayList();
        this.body_locations = new ArrayList<>();
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
     * @return size (int)
     */
    public int getProblemCount() {return problems.size();}

    /**
     * Returns number of body location (body location photos) this patient has associated with them
     * @return size (int)
     */
    public int getBodyLocationCount(){return body_locations.size();}

    public void addBodyLocation(BodyLocationPhoto body_location_photo){ body_locations.add(body_location_photo); }

    public ArrayList<BodyLocationPhoto> getBodyLocations(){
        return body_locations;
    }

    public void removeBodyLocationPhoto(BodyLocationPhoto blp) {
        body_locations.remove(blp);
    }


}
