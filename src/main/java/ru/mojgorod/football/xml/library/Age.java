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

/**
 *
 * @author sova
 */
public class Age {

    final public static Age AGE_ZERO = new Age(0, 0);
    final public static Age AGE_MAX = new Age(Integer.MAX_VALUE, 0);
    long years = 0;
    long days = 0;

    public Age(long years, long days) {
        this.years = years;
        this.days = days;
    }

    public double getDoubleValue() {
        return years + days / 366.0;
    }

    public String getStringValue() {
        return String.format("%s %s", Utils.getLocalizedYearsMessage(years), Utils.getLocalizedDaysMessage(days));
    }

    public boolean lessThan(Age age) {
        if (years < age.years) {
            return true;
        }
        if (years > age.years) {
            return false;
        }
        return days < age.days;
    }

    public boolean lessOrEqualsThan(Age age) {
        if (years < age.years) {
            return true;
        }
        if (years > age.years) {
            return false;
        }
        return days <= age.days;
    }

    public boolean moreThan(Age age) {
        if (years > age.years) {
            return true;
        }
        if (years < age.years) {
            return false;
        }
        return days > age.days;
    }

    public boolean moreOrEqualsThan(Age age) {
        if (years > age.years) {
            return true;
        }
        if (years < age.years) {
            return false;
        }
        return days > age.days;
    }

    public boolean equals(Age age) {
        return (age.years == years) && (age.days == days);
    }

}
