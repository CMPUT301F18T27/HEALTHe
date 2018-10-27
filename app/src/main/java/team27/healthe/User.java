package team27.healthe;

public abstract class User {
    protected String userid;
    protected String email;
    protected String phone_number;

    public User(String uid, String e, String p){
        userid = uid;
        email = e;
        phone_number = p;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
