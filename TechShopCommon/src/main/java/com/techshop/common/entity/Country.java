package com.techshop.common.entity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "countries")
public class Country  extends IdBasedEntity{
    @Column(length = 45)
    private String name;

    @Column(length = 5)
    private String code;

    @OneToMany(mappedBy = "country")
    private Set<State> states= new LinkedHashSet<>();

    public Country() {
    }

    public Country(Long id) {
        this.id = id;
    }

    public Country(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Country(String name) {
        this.name = name;
    }

    public Country(Long id, String name, String code, Set<State> states) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.states = states;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//    public Set<State> getStates() {
//        return states;
//    }
//
//    public void setStates(Set<State> states) {
//        this.states = states;
//    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
//                ", states=" + states +
                '}';
    }
}
