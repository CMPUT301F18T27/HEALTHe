package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

public class UserList {
    private Collection<User> users;

    public UserList() {
        this.users = new ArrayList();
    }

    public Collection<User> getUsers() {
        return null;
    }

    public void addUser(User u) {users.add(u);}

    public Boolean hasUser(User u) { return users.contains(u); }

    public User getUser(String userID) {
        for (User user: users) {
            if (user.userid == userID) {return user;}
        }
        return null;
    }

    public void removeUser(User u) {users.remove(u);}
}
