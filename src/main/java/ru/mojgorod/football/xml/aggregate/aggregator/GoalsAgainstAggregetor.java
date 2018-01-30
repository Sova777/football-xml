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
package ru.mojgorod.football.xml.aggregate.aggregator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class GoalsAgainstAggregetor implements Aggregator {

    private final HashMap<String, TournamentStat> teams = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamId1();
        if ("".equals(teamKey1)) {
            teamKey1 = team1;
        }
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamId2();
        if ("".equals(teamKey2)) {
            teamKey2 = team2;
        }
        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            if (event.isAnyGoal()) {
                String team = event.getTeam();
                String key = null;
                String teamValue = null;
                if (team.equals(team1)) {
                    key = event.isAutoGoal() ? teamKey1 : teamKey2;
                    teamValue = event.isAutoGoal() ? xmlReport.getTeam1() : xmlReport.getTeam2();
                } else if (team.equals(team2)) {
                    key = event.isAutoGoal() ? teamKey2 : teamKey1;
                    teamValue = event.isAutoGoal() ? xmlReport.getTeam2() : xmlReport.getTeam1();
                } else {
                    Logger.getLogger(GoalsAgainstAggregetor.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                }
                TournamentStat stat;
                if (!teams.containsKey(key)) {
                    stat = new TournamentStat();
                    teams.put(key, stat);
                } else {
                    stat = teams.get(key);
                }
                stat.team = teamValue;
                Integer timeInt = event.getTimeInt();
                int time = (timeInt == null) ? 0 : timeInt;
                if (time < 15) {
                    stat.time0++;
                } else if (time < 30) {
                    stat.time15++;
                } else if (time < 45) {
                    stat.time30++;
                } else if (time < 46) {
                    stat.time45++;
                } else if (time < 60) {
                    stat.time46++;
                } else if (time < 75) {
                    stat.time60++;
                } else if (time < 90) {
                    stat.time75++;
                } else {
                    stat.time90++;
                }
                if (time < 46) {
                    stat.half1++;
                } else {
                    stat.half2++;
                }
            }
        }

    }

    @Override
    public void print(final String title) {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        System.out.println("==============================================================================================================");
        System.out.println("| Команда              | 0-14  | 15-29 | 30-44 | 45+   | 46-59 | 60-74 | 75-89 | 90+   | 1-й тайм | 2-й тайм |");
        System.out.println("==============================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            System.out.printf("| %-20s | %-5d | %-5d | %-5d | %-5d | %-5d | %-5d | %-5d | %-5d | %-8d | %-8d |%n",
                    stat.team, stat.time0, stat.time15, stat.time30, stat.time45,
                    stat.time46, stat.time60, stat.time75, stat.time90, stat.half1, stat.half2);
        }
        System.out.println("==============================================================================================================");
    }

    static private class TournamentStat {

        private String team = "";
        private int time0 = 0;
        private int time15 = 0;
        private int time30 = 0;
        private int time45 = 0;
        private int time46 = 0;
        private int time60 = 0;
        private int time75 = 0;
        private int time90 = 0;
        private int half1 = 0;
        private int half2 = 0;

    }

    static private class StatComparator implements Comparator<String> {

        private HashMap<String, TournamentStat> map;

        public StatComparator(final HashMap<String, TournamentStat> map) {
            this.map = map;
        }

        @Override
        public int compare(String key1, String key2) {
            TournamentStat stat1 = map.get(key1);
            TournamentStat stat2 = map.get(key2);
            String value1 = stat1.team;
            String value2 = stat2.team;
            return value1.compareTo(value2);
        }
        
    }

}
