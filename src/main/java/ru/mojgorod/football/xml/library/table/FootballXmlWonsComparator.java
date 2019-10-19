/*
Copyright (c) 2019, Valeriy Soldatov
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
package ru.mojgorod.football.xml.library.table;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author sova
 */
public class FootballXmlWonsComparator implements Comparator<String> {

    Collator collator = Collator.getInstance(new Locale("ru", "RU"));
    private final HashMap<String, FootballXmlTableRow> rows;

    public FootballXmlWonsComparator(HashMap<String, FootballXmlTableRow> rows) {
        this.rows = rows;
    }

    @Override
    public int compare(String teamKey1, String teamKey2) {
        FootballXmlTableRow row1 = rows.get(teamKey1);
        FootballXmlTableRow row2 = rows.get(teamKey2);

        int points1 = row1.getPoints();
        int points2 = row2.getPoints();
        if (points1 > points2) {
            return -1;
        } else if (points1 < points2) {
            return 1;
        }

        int won1 = row1.getWon();
        int won2 = row2.getWon();
        if (won1 > won2) {
            return -1;
        } else if (won1 < won2) {
            return 1;
        }

        int for1 = row1.getFor();
        int for2 = row2.getFor();
        int against1 = row1.getAgainst();
        int against2 = row2.getAgainst();
        int diff1 = for1 - against1;
        int diff2 = for2 - against2;
        if (diff1 > diff2) {
            return -1;
        } else if (diff1 < diff2) {
            return 1;
        }

        if (for1 > for2) {
            return -1;
        } else if (for1 < for2) {
            return 1;
        }

        return collator.compare(teamKey1, teamKey2);
    }

}
