/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mojgorod.football.xml.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public Integer getBirthdayInt() {
        String date = getBirthday();
        if (date == null) {
            return null;
        }
        if (date.length() != 8) {
            Logger.getLogger(FootballXmlPlayersInfo.class.getName()).log(Level.SEVERE, "Wrong date format: {0}", date);
            return null;
        }
        return Integer.valueOf(date.substring(6, 8)) + 100 * Integer.valueOf(date.substring(4, 6)) + 10000 * Integer.valueOf(date.substring(0, 4));
    }

    public Double getAge(Integer currentDate) {
        if (id == null || currentDate == null) {
            return null;
        }
        Integer birthdayInt = getBirthdayInt();
        if (birthdayInt == null) {
            return null;
        }
        int year1 = birthdayInt / 10000;
        int month1 = (birthdayInt - year1 * 10000) / 100;
        int day1 = birthdayInt - year1 * 10000 - month1 * 100;
        LocalDate date1 = LocalDate.of(year1, month1, day1);

        int year2 = currentDate / 10000;
        int month2 = (currentDate - year2 * 10000) / 100;
        int day2 = currentDate - year2 * 10000 - month2 * 100;
        LocalDate date2 = LocalDate.of(year2, month2, day2);

        LocalDate date3 = ((month1 * 100 + day1) > (month2 * 100 + day2))
                ? LocalDate.of(year2 - 1, month1, day1)
                : LocalDate.of(year2, month1, day1);

        long years = ChronoUnit.YEARS.between(date1, date2);
        long days = ChronoUnit.DAYS.between(date3, date2);
        return years + days / 366.0;
    }

}
