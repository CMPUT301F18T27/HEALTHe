package team27.healthe.model;

import org.elasticsearch.common.UUID;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * Represents a Patient's problem and provides controls (accessing and editing info)
 * @author [fill in]
 * @author Chris
 */
public class Problem implements Comparable<Problem> {
    private String title;
    private Date pdate;
    private String description;
    private Collection<String> records;
    private String problem_id;
    private String patient_id;

    public Problem() {
        this.title = "New Problem";
        this.pdate = new Date();
        this.description = "";
        this.records = new ArrayList<>();
        this.problem_id = UUID.randomUUID().toString();
        this.patient_id = "";
    }

    public Problem(String ttl, Date date, String desc) {
        title = ttl;
        pdate = date;
        description = desc;
        records = new ArrayList<>();
        this.problem_id = UUID.randomUUID().toString();
        this.patient_id = "";
    }

    public Problem(String ttl, Date date, String desc, String p_id) {
        title = ttl;
        pdate = date;
        description = desc;
        records = new ArrayList<>();
        patient_id = p_id;
        this.problem_id = UUID.randomUUID().toString();
    }

    public Problem(String ttl, Date date, String desc, Collection<String> recs) {
        title = ttl;
        pdate = date;
        description = desc;
        records = recs;
        this.problem_id = UUID.randomUUID().toString();
        this.patient_id = "";
    }

    public Problem(String ttl, String desc, Collection<String> recs) {
        title = ttl;
        pdate = new Date();
        description = desc;
        records = recs;
        this.problem_id = UUID.randomUUID().toString();
        this.patient_id = "";
    }

    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPdate() {
        return pdate;
    }

    /**
     * Converts a Date object to String
     * @return s (date - String)
     */
    public String getPdateAsString() {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String s = formatter.format(pdate);
        return s;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
    }

    /**
     * Converts a string date to Date object
     * and sets the date to that converted object
     * @param strdate (String)
     */
    public void setPdateAsDateObj(String strdate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            Date date = formatter.parse(strdate);
            this.pdate = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<String> getRecords() {
        return records;
    }

    public void setRecords(Collection<String> records) {
        this.records = records;
    }

    public void addRecord(String record_id) {
        records.add(record_id);
    }

    public void removeRecord(String record_id) {
        records.remove(record_id);
    }

    public void setProblemID(String pid) {
        problem_id = pid;
    }

    public String getProblemID() {
        return problem_id;
    }

    public String getPatientID(){
        return patient_id;
    }

    public void setPatientID(String pat_id){
        patient_id = pat_id;
    }

    /**
     * Comparable implementation (compares by date)
     * @param compare_problem (Problem class)
     * @return see Date.compareTo()
     */
    public int compareTo(Problem compare_problem) {
        return this.getPdate().compareTo(compare_problem.getPdate());
    }

    /**
     * Returns the size of the records list
     * @return size (int)
     */
    public int getNumberOfRecords() {
        if (records.isEmpty()) {
            return 0;
        }
        return records.size();
    }
}