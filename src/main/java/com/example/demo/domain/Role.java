package com.example.demo.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "role_name")
    private String roleName;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "family_id")
    )
    private Set<Family> families;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Family> getFamilies() {
        return families;
    }

    public void setFamilies(Set<Family> families) {
        this.families = families;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }
}
