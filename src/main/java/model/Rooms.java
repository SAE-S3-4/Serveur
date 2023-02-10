package model;

import jakarta.persistence.*;

@NamedQueries({
        @NamedQuery(name = "Rooms.findAll", query = "SELECT r FROM Rooms r"),
        @NamedQuery(name = "Rooms.getById", query = "SELECT r FROM Rooms r WHERE r.id = :id"),
})

@Table(name = "ROOMS")
@Entity
public class Rooms {
    @Id
    @Column(name="ID")
    String id;

    @Column(name = "NAME")
    String name;

    @Column(name="PASSWORD")
    String password;

    @Column(name = "STARTED")
    boolean started;

    @Column(name = "ADMIN_ID")
    String admin_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    @Override
    public String toString() {
        return "Rooms{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password=" + password +
                ", started=" + started +
                ", admin_id='" + admin_id + '\'' +
                '}';
    }
}
