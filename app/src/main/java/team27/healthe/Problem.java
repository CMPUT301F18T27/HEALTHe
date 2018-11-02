package team27.healthe;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Problem {
    private String title;
    private Date pdate;
    private String description;
    private List<Record> records;

    public Problem(String ttl, Date date, String desc){
        title = ttl;
        pdate = date;
        description = desc;

    }

    public Problem(String ttl, String desc) {
        title = ttl;
        pdate = Calendar.getInstance().getTime();
        description = desc;
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

    public void setPdate(Date pdate) {
        this.pdate = pdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }
}
