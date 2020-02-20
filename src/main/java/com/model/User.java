package com.model;

import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table (name = "user", schema = "contextual_v2")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;

    @Column(name = "userName", length = 50)
    private String userName;

    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;

    @Column(name = "password", length = 50, nullable = false)
    private String password;

    @Column(name = "age")
    private int age;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "loginFrequency")
    private int loginFrequency;

    @Column(name = "lastLogin")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastLogin;
}
