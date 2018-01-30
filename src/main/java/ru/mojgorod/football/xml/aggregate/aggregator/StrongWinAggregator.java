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
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class StrongWinAggregator implements Aggregator {

    private final HashMap<String, TournamentStat> teams = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (!xmlReport.isValidScore()) {
            return;
        }
        int goals1 = xmlReport.getGoalsInt1();
        int goals2 = xmlReport.getGoalsInt2();
        if (goals1 == goals2) {
            return;
        }

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

        int difference = 0;
        String winnerKey = (goals1 > goals2) ? teamKey1 : teamKey2;
        String winnerName = (goals1 > goals2) ? team1 : team2;
        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            if (eventType.isAnyGoal()) {
                String team = event.getTeam();
                if (eventType.isGoal() || eventType.isPenaltyGoal()) {
                    if (team.equals(winnerName)) {
                        difference++;
                    } else {
                        difference--;
                    }
                } else if (eventType.isAutoGoal()) {
                    if (team.equals(winnerName)) {
                        difference--;
                    } else {
                        difference++;
                    }
                }
            }
            if (difference < 0) {
                TournamentStat stat;
                if (!teams.containsKey(winnerKey)) {
                    stat = new TournamentStat();
                    teams.put(winnerKey, stat);
                } else {
                    stat = teams.get(winnerKey);
                }
                stat.team = winnerName;
                stat.games++;
                return;
            }
        }
    }

    @Override
    public void print(final String title) {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        System.out.println("=====================================");
        System.out.println("| Команда              | Побед      |");
        System.out.println("=====================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            System.out.printf("| %-20s | %-10d |%n",
                    stat.team, stat.games);
        }
        System.out.println("=====================================");
    }

    static private class TournamentStat {

        private String team = "";
        private int games = 0;

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
            if (stat1.games > stat2.games) {
                return -1;
            } else if (stat1.games < stat2.games) {
                return 1;
            }
            return value1.compareTo(value2);
        }
        
    }

}
