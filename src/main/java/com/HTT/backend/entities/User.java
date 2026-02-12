package com.HTT.backend.entities;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String email;
    private String password;
    @Column(nullable=true)
    private String verificationToken;
    @Column(nullable=true)
    private String resetPasswordToken;
    @Column(nullable=true)
    private LocalDateTime tokenExpirationTime;
    private int accountVerified;
    
    @Column(columnDefinition = "TINYINT(1)")
    private boolean adminPermit;

    private LocalDateTime lastActive;
    @Column(nullable=true)
    private String imageUrl;
    
    // @OneToOne(cascade=CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name="resume_id",referencedColumnName="id")
    // private ResumeCV resumeCV;

    // @OneToMany(mappedBy="user")
    // private List<InternApplication> appliedInternships;

    // @OneToMany(mappedBy="user")
    // private List<EventRegistration> registeredEvents;
    

    public User(String name, String email, String password, int accountVerified){
        this.name = name;
        this.email = email;
        this.password = password;
        this.adminPermit = false;
        this.accountVerified = accountVerified;
    }
    public User(String name, String email, String password,String verificationToken, int accountVerified){
        this.name = name;
        this.email = email;
        this.password = password;
        this.verificationToken = verificationToken;
        this.adminPermit = false;
        this.accountVerified = accountVerified;
    }
    public User(int id, String name, String email, String password, int accountVerified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.accountVerified = accountVerified;
        this.adminPermit = false;
    }
    public User(int id, String name, String email, String password,String verificationToken, int accountVerified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.verificationToken = verificationToken;
        this.accountVerified = accountVerified;
        this.adminPermit = false;
    }
    public User(String name, String email,String password, boolean adminPermit) {
        this.adminPermit = adminPermit;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.adminPermit) {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    } else {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}

