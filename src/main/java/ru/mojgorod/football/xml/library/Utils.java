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

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author sova
 */
public class Utils {

    private final static String[] MESSAGES_YEARS = new String[] { "год", "года", "лет" };
    private final static String[] MESSAGES_DAYS = new String[] { "день", "дня", "дней" };

    public static String getMonthName(int month) {
        switch(month) {
            case 1:
                return "Январь";
            case 2:
                return "Февраль";
            case 3:
                return "Март";
            case 4:
                return "Апрель";
            case 5:
                return "Май";
            case 6:
                return "Июнь";
            case 7:
                return "Июль";
            case 8:
                return "Август";
            case 9:
                return "Сентябрь";
            case 10:
                return "Октябрь";
            case 11:
                return "Ноябрь";
            case 12:
                return "Декабрь";
        }
        throw new RuntimeException("Wrong month. Expected: number from 1 to 12, Real: " + month);
    }

    private static String getLocalizedMessage(long number, String[] messages) {
        int index = 2;
        long last2Numbers = number % 100;
        if (last2Numbers > 4 && last2Numbers < 20) {
            index = 2;
        } else {
            long lastNumber = number % 10;
            if (lastNumber == 0) {
                index = 2;
            } else if (lastNumber == 1) {
                index = 0;
            } else if (lastNumber > 1 && lastNumber < 5) {
                index = 1;
            }
        }
        return String.format("%d %s", number, messages[index]);
    }

    public static String getLocalizedYearsMessage(long years) {
        return getLocalizedMessage(years, MESSAGES_YEARS);
    }

    public static String getLocalizedDaysMessage(long days) {
        return getLocalizedMessage(days, MESSAGES_DAYS);
    }

    public static String getLocalizedMessage(Age age) {
        return String.format("%s %s", getLocalizedMessage(age.years, MESSAGES_YEARS),
                getLocalizedMessage(age.days, MESSAGES_DAYS));
    }

    public static String getLocalizedMessage(double yearsValue) {
        long years = (long)yearsValue;
        long days = (long)((yearsValue - years) * 366.0);
        return String.format("%s %s", getLocalizedMessage(years, MESSAGES_YEARS),
                getLocalizedMessage(days, MESSAGES_DAYS));
    }

    public static double medianInteger(ArrayList<Integer> array) {
        if (array.isEmpty()) {
            return 0.0;
        }
        Collections.sort(array);
        double median;
        if (array.size() % 2 == 0) {
            median = ((double) array.get(array.size() / 2) + (double) array.get(array.size() / 2 - 1)) / 2;
        } else {
            median = (double) array.get(array.size() / 2);
        }
        return median;
    }

    public static double medianDouble(ArrayList<Double> array) {
        if (array.isEmpty()) {
            return 0.0;
        }
        Collections.sort(array);
        double median;
        if (array.size() % 2 == 0) {
            median = (array.get(array.size() / 2) + array.get(array.size() / 2 - 1)) / 2;
        } else {
            median = array.get(array.size() / 2);
        }
        return median;
    }

    public static String centerText(String text, int length) {
        if (text == null) {
            text = "";
        }
        int textLength = text.length();
        String spaces = String.join("", Collections.nCopies((length - textLength) / 2, " "));
        return spaces + text + spaces + (((length - textLength) % 2 == 0) ? "" : " ");
    }

    public static String repeatText(String text, int times) {
        if (text == null) {
            return "";
        }
        return String.join("", Collections.nCopies(times, text));
    }

    public static String convertDateToString(Integer date) {
        if (date == null) {
            return "";
        }
        int year = date / 10000;
        int month = date / 100 - (100 * year);
        int day = date - (10000 * year) - (100 * month);
        return String.format("%02d.%02d.%04d", day, month, year);
    }

    public static String convertDateToIso8601(Integer date) {
        if (date == null) {
            return "";
        }
        int year = date / 10000;
        int month = date / 100 - (100 * year);
        int day = date - (10000 * year) - (100 * month);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

}
