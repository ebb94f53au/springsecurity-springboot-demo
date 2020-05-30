package com.siyang.springsecurityspringbootdemo.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author siyang
 * @create 2020-05-29 10:03
 */
@Entity
@Data
@Table(name="se_role")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ManyToMany(targetEntity = Permission.class,fetch=FetchType.EAGER)
    @JoinTable(name="se_role_permission",
            joinColumns = {@JoinColumn(name = "roleId",referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permissionId", referencedColumnName = "id")}
    )
    private Set<Permission> permissions = new HashSet<>();
}
