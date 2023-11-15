package org.entities;

import jakarta.persistence.*;
import java.util.Date;


@Entity
@Table(name="USERSS")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class User{

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="name", length=30, nullable = false)
    private String name;

    @Column(name="phone", length=12, nullable = false)
    private String phone;

    @Column(name="street", length=30, nullable = false)
    private String street;

    @Column(name="city", length=25, nullable = false)
    private String city;

    @Column(name="email", length=25, nullable = false)
    private String email;

    @Column(name="zipcode", length=5, nullable = false)
    private String zipcode;

    public User(){
    }

    public User(String name,String phone,String street,String city,String email,String zipcode){
        this.name=name;
        this.phone=phone;
        this.street=street;
        this.city=city;
        this.email=email;
        this.zipcode=zipcode;

    }

    public String getName(){
        return name;
    }
    
    public String getPhone(){
        return phone;
    }

    public String getStreet(){
        return street;
    }

    public String getCity(){
        return city;
    }

    public String getEmail(){
        return email;
    }

    public String getZipcode(){
        return zipcode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }





}