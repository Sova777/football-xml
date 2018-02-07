/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mojgorod.football.xml.library;

/**
 *
 * @author sova
 */
public class FootballXmlPlayersInfo {

    private String id;
    private String name;
    private String birthday;
    private String country;

    void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

}
