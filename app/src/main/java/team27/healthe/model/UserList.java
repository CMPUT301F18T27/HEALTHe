package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

public class UserList {
    private Collection<User> users;

    public UserList() {
        this.users = new ArrayList<User>();
    }

    public void addUser(User u) {}

    public Boolean hasUser(User u) {
        return false;
    }

    public User getUser(String userID) {
        return null;
    }

    public void removeUser(User u) {}
}
