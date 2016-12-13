package com.droidablebee.springboot.rest.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
public class Person {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="person_id")
    private Long id;

    @NotNull
    @Column(name="first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name="middle_name")
    private String middleName;

    @Column(name="dob")
    private Date dateOfBirth;
    
    @Valid
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "person_id")
	private Set<Address> addresses;

    protected Person() {}

    public Person(Long id, String firstName, String lastName) {
    	this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public Person(String firstName, String lastName) {
    	this.firstName = firstName;
    	this.lastName = lastName;
    }
    
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

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}
	
	public void addAddress(Address address) {
		
		if (getAddresses() == null) {
			setAddresses(new HashSet<>());
		}
		getAddresses().add(address);
	}

}