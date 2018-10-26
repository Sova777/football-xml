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

import java.io.PrintStream;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import ru.mojgorod.football.xml.config.Config;
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
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();

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
                TournamentStat stat = TournamentStat.get(teams, winnerKey);
                stat.team = winnerName;
                stat.games++;
                return;
            }
        }
    }

    @Override
    public void print(final Config config, final PrintStream out, final String title) {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        out.println("<h2>Волевые победы</h2>");
        out.println("<pre>");
        out.println("=====================================");
        out.println("| Команда              | Побед      |");
        out.println("=====================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            out.printf("| %-20s | %-10d |%n",
                    stat.team, stat.games);
        }
        out.println("=====================================");
        out.println("</pre>");
    }

    static private class TournamentStat {

        private String team = "";
        private int games = 0;

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

    static private class StatComparator implements Comparator<String> {

        Collator collator = Collator.getInstance(new Locale("ru", "RU"));
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
            return collator.compare(value1, value2);
        }
        
    }

    @Override
    public void drawCharts(String title) {
    }

}
