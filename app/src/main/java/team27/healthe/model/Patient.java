package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a patient
 * @author [fill in]
 */
public class Patient extends User {
    private ArrayList<Integer> problems;
    private String user_type = "patient";

    public Patient(String userID, String email, String phone){
        super(userID, email, phone);
        this.problems = new ArrayList();
    }

    public Collection<Integer> getProblemList() {
        return this.problems;
    }

    /**
     * Adds a problem id to the patient's problem list
     * @param p (Integer) problem_id
     */
    public void addProblem(Integer p) {problems.add(p);}

    /**
     * checks if the given problem exists in the user's list
     * @param p (Integer)
     * @return True if exists, False otherwise
     */
    public Boolean hasProblem(Integer p) { return problems.contains(p); }

    // @TODO: problem title is not unique. finalize collection class to determine addressing
    public Problem getProblem(String problemTitle) {
        return null;
    }

    /**
     * Removes a problem from the user's list
     * @param p (Integer) problem id
     */
    public void removeProblem(Integer p) {problems.remove(p);}

    /**
     * Returns the number of problems in the user's problem list
     * @return size (Integer)
     */
    public Integer getProblemCount() {return problems.size();}

}
