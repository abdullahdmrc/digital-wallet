package com.example.digitalwallet.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Customers")
@Data
public class Customer {

    @Id
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true, nullable = false)
    private String tckn;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

}
