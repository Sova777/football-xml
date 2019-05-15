/*
Copyright (c) 2018, Valeriy Soldatov
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of the football.mojgorod.ru nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ru.mojgorod.football.xml.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;
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
            Logger.getLogger(FootballXmlPlayersInfo.class.getName()).log(Level.SEVERE, "Wrong date format: {0}, Player ID: {1}", new Object[]{date, id});
            return null;
        }
        return Integer.valueOf(date.substring(6, 8)) + 100 * Integer.valueOf(date.substring(4, 6)) + 10000 * Integer.valueOf(date.substring(0, 4));
    }

    public Age getAge(Integer currentDate) {
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

        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        if (!cal.isLeapYear(year2)) {
            if (month1 == 2 && day1 == 29) {
                day1 = 28;
            }
        }

        LocalDate date3 = ((month1 * 100 + day1) > (month2 * 100 + day2))
                ? LocalDate.of(year2 - 1, month1, day1)
                : LocalDate.of(year2, month1, day1);

        long years = ChronoUnit.YEARS.between(date1, date2);
        long days = ChronoUnit.DAYS.between(date3, date2);
        return new Age(years, days);
    }

}
