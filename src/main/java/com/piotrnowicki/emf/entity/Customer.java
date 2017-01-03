package com.piotrnowicki.emf.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceProperty;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@NamedQueries({
        @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c"),
        @NamedQuery(name = Customer.DELETE_ALL, query = "DELETE FROM Customer c") })
public class Customer {

    public final static String FIND_ALL = "Customer.findAll";
    public final static String DELETE_ALL = "Customer.deleteAll";

    @Id
    @GeneratedValue
    @PersistenceContext(properties = {@PersistenceProperty(name = "", value = "") })
    private Long id;

    private String firstName;

    private String lastName;

    private Date date;

    @ElementCollection()
    @CollectionTable(name = "ITEM_ALIASES", joinColumns = @JoinColumn(name = "THE_ITEM_ID"))
    private List<String> details;

    // -------------------------------------------------------------------------------||
    // Constructors
    // ------------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * For JPA purposes only.
     */
    Customer() {
    }

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // -------------------------------------------------------------------------------||
    // Lifecycle methods
    // -------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    @PrePersist
    public void prePersist() {
        System.out.println("PRE PERSIST");
        date = new Date();
    }

    @PostPersist
    public void postPersist() {
        System.out.println("POST PERSIST "+date);
    }
    
    @PreUpdate
    public void preUpdate() {
        System.out.println("PRE UPDATE");
        date = new Date();
    }

    @PostUpdate
    public void postUpdate() {
        System.out.println("POST UPTADE "+date);
    }

    // -------------------------------------------------------------------------------||
    // Getters and setters
    // -----------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDate() {
        return date;
    }

    // -------------------------------------------------------------------------------||
    // Contract
    // ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this,
                ToStringStyle.SIMPLE_STYLE);

        builder.append("id", id);
        builder.append("firstName", firstName);
        builder.append("lastName", lastName);
        builder.append("date", date);

        return builder.toString();
    }

}
