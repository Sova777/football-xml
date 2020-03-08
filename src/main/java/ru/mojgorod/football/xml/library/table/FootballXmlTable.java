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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author sova
 */
public class FootballXmlTable {

    private final HashMap<String, HashMap<String, TableScores>> scores = new HashMap<>();

    public void add(String teamKey1, String teamKey2, int goals1, int goals2) {
        HashMap<String, TableScores> teamStat1 = scores.get(teamKey1);
        if (teamStat1 == null) {
            teamStat1 = new HashMap<>();
            scores.put(teamKey1, teamStat1);
        }
        TableScores scoresTeam = teamStat1.get(teamKey2);
        if (scoresTeam == null) {
            scoresTeam = new TableScores();
            teamStat1.put(teamKey2, scoresTeam);
        }
        scoresTeam.add(goals1, goals2);
    }

    public List<FootballXmlTableRow> sort(String sortRules) {
        HashMap<String, FootballXmlTableRow> rows = new HashMap<>();
        for (Map.Entry<String, HashMap<String, TableScores>> scoresHash : scores.entrySet()) {
            String teamKey1 = scoresHash.getKey();
            FootballXmlTableRow row1 = rows.get(teamKey1);
            if (row1 == null) {
                row1 = new FootballXmlTableRow();
                rows.put(teamKey1, row1);
            }
            HashMap<String, TableScores> scoreValues = scoresHash.getValue();
            for (Map.Entry<String, TableScores> scoresItem : scoreValues.entrySet()) {
                String teamKey2 = scoresItem.getKey();
                FootballXmlTableRow row2 = rows.get(teamKey2);
                if (row2 == null) {
                    row2 = new FootballXmlTableRow();
                    rows.put(teamKey2, row2);
                }
                TableScores scoresValue = scoresItem.getValue();
                for (TableScore scoreItem : scoresValue.scores) {
                    int g1 = scoreItem.getG1();
                    int g2 = scoreItem.getG2();
                    if (g1 > g2) {
                        row1.setWon(row1.getWon() + 1);
                        row2.setLost(row2.getLost() + 1);
                    } else if (g1 < g2) {
                        row1.setLost(row1.getLost() + 1);
                        row2.setWon(row2.getWon() + 1);
                    } else {
                        row1.setDraws(row1.getDraws() + 1);
                        row2.setDraws(row2.getDraws() + 1);
                    }
                    row1.setTeamKey(teamKey1);
                    row1.setFor(row1.getFor() + g1);
                    row1.setAgainst(row1.getAgainst() + g2);
                    row1.setForHome(row1.getForHome() + g1);
                    row1.setAgainstHome(row1.getAgainstHome() + g2);
                    row1.setPoints(3 * row1.getWon() + row1.getDraws());
                    row2.setTeamKey(teamKey2);
                    row2.setFor(row2.getFor() + g2);
                    row2.setAgainst(row2.getAgainst() + g1);
                    row2.setForVisitor(row2.getForVisitor() + g2);
                    row2.setAgainstVisitor(row2.getAgainstVisitor() + g1);
                    row2.setPoints(3 * row2.getWon() + row2.getDraws());
                }
            }
        }
        TreeMap<String, FootballXmlTableRow> sortedMap = new TreeMap<>(new FootballXmlWonsComparator(rows, null));
        sortedMap.putAll(rows);
        int points = 0;
        int wons = 0;
        boolean isFirst = true;
        HashSet<String> listResort = new HashSet<>();
        List<FootballXmlTableRow> listTable = new ArrayList<>();
        if ("opp".equals(sortRules)) {
            for (FootballXmlTableRow row : sortedMap.values()) {
                if (row.getPoints() != points && !isFirst) {
                    List<String> teams = opponentsComparator(listResort, rows);
                    listResort.clear();
                    for (String team : teams) {
                        listTable.add(sortedMap.get(team));
                    }
                }
                points = row.getPoints();
                listResort.add(row.getTeamKey());
                isFirst = false;
            }
        } else {
            for (FootballXmlTableRow row : sortedMap.values()) {
                if ((row.getPoints() != points || row.getWon() != wons) && !isFirst) {
                    List<String> teams = opponentsComparator(listResort, rows);
                    listResort.clear();
                    for (String team : teams) {
                        listTable.add(sortedMap.get(team));
                    }
                }
                points = row.getPoints();
                wons = row.getWon();
                listResort.add(row.getTeamKey());
                isFirst = false;
            }
        }
        opponentsComparator(listResort, rows);
        List<String> teams = opponentsComparator(listResort, rows);
        for (String team : teams) {
            listTable.add(sortedMap.get(team));
        }
        return listTable;
    }

    private List<String> opponentsComparator(final HashSet<String> listResort, final HashMap<String, FootballXmlTableRow> stat) {
        List<String> array = new ArrayList<>();
        if (listResort.isEmpty()) {
            return array;
        } else if (listResort.size() == 1) {
            array.add(listResort.iterator().next());
            return array;
        }
        HashMap<String, FootballXmlTableRow> rows = new HashMap<>();
        for (String teamKey1 : listResort) {
            if (!listResort.contains(teamKey1)) {
                continue;
            }
            FootballXmlTableRow row1 = rows.get(teamKey1);
            if (row1 == null) {
                row1 = new FootballXmlTableRow();
                rows.put(teamKey1, row1);
            }
            HashMap<String, TableScores> scoresHash = scores.get(teamKey1);
            for (Map.Entry<String, TableScores> scoresItem : scoresHash.entrySet()) {
                String teamKey2 = scoresItem.getKey();
                if (!listResort.contains(teamKey2)) {
                    continue;
                }
                FootballXmlTableRow row2 = rows.get(teamKey2);
                if (row2 == null) {
                    row2 = new FootballXmlTableRow();
                    rows.put(teamKey2, row2);
                }
                TableScores scoresValue = scoresItem.getValue();
                for (TableScore scoreItem : scoresValue.scores) {
                    int g1 = scoreItem.getG1();
                    int g2 = scoreItem.getG2();
                    if (g1 > g2) {
                        row1.setWon(row1.getWon() + 1);
                        row2.setLost(row2.getLost() + 1);
                    } else if (g1 < g2) {
                        row1.setLost(row1.getLost() + 1);
                        row2.setWon(row2.getWon() + 1);
                    } else {
                        row1.setDraws(row1.getDraws() + 1);
                        row2.setDraws(row2.getDraws() + 1);
                    }
                    row1.setTeamKey(teamKey1);
                    row1.setFor(row1.getFor() + g1);
                    row1.setAgainst(row1.getAgainst() + g2);
                    row1.setForHome(row1.getForHome() + g1);
                    row1.setAgainstHome(row1.getAgainstHome() + g2);
                    row1.setPoints(3 * row1.getWon() + row1.getDraws());
                    row2.setTeamKey(teamKey2);
                    row2.setFor(row2.getFor() + g2);
                    row2.setAgainst(row2.getAgainst() + g1);
                    row2.setForVisitor(row2.getForVisitor() + g2);
                    row2.setAgainstVisitor(row2.getAgainstVisitor() + g1);
                    row2.setPoints(3 * row2.getWon() + row2.getDraws());
                }
            }
        }
        TreeMap<String, FootballXmlTableRow> sortedMap = new TreeMap<>(new FootballXmlWonsComparator(rows, stat));
        sortedMap.putAll(rows);
        for (String teamKey : sortedMap.keySet()) {
            array.add(teamKey);
        }
        return array;
    }

    public void clear() {
        scores.clear();
    }

    private static class TableScores {

        private ArrayList<TableScore> scores = new ArrayList<>();

        public void add(int goals1, int goals2) {
            scores.add(new TableScore(goals1, goals2));
        }

    }

    private static class TableScore {

        private int g1;
        private int g2;

        public TableScore(int g1, int g2) {
            this.g1 = g1;
            this.g2 = g2;
        }

        public int getG1() {
            return g1;
        }

        public int getG2() {
            return g2;
        }

    }

}
