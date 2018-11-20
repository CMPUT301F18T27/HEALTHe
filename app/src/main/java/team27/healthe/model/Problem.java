package team27.healthe.model;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Problem implements Comparable<Problem> {
    private String title;
    private Date pdate;
    private String description;
    private Collection<Integer> records;
    private Integer problem_id;
    private Integer patient_id;
    // private Collection<Record> records;

    public Problem() {
        this.title = "New Problem";
        this.pdate = new Date();
        this.description = "";
        this.records = new ArrayList<>();
    }

    public Problem(String ttl, Date date, String desc) {
        title = ttl;
        pdate = date;
        description = desc;
        records = new ArrayList<>();
        patient_id = null;
    }

    public Problem(String ttl, Date date, String desc, Collection<Integer> recs) {
        title = ttl;
        pdate = date;
        description = desc;
        records = recs;

    }

    public Problem(String ttl, String desc, Collection<Integer> recs) {
        title = ttl;
        pdate = Calendar.getInstance().getTime();
        description = desc;
        records = recs;


    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPdate() {
        return pdate;
    }

    public String getPdateAsString() {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String s = formatter.format(pdate);
        return s;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
    }

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

    public Collection<Integer> getRecords() {
        return records;
    }

    public void setRecords(Collection<Integer> records) {
        this.records = records;
    }

    public void addRecord(Integer record_id) {
        records.add(record_id);
    }

    public void removeRecord(Integer record_id) {
        records.remove(record_id);
    }

    public void setProblemID(Integer pid) {
        problem_id = pid;
    }

    public Integer getProblemID() {
        return problem_id;
    }

    public int compareTo(Problem compare_problem) {
        return this.getPdate().compareTo(compare_problem.getPdate());
    }

    public int getNumberOfRecords() {
        return records.size();
    }
}