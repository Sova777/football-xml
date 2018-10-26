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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import ru.mojgorod.football.xml.config.Config;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class TopScoresAggregator implements Aggregator {

    private final HashMap<String, TournamentStat> players = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String team2 = xmlReport.getTeam2();

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case GOAL:
                case PENALTY_GOAL:
                    TournamentStat stat = TournamentStat.get(players, event.getPlayerKey1());
                    stat.name = event.getPlayer1();
                    stat.addTeam(event.getTeam());
                    stat.id = event.getPlayerKey1();
                    stat.goals++;
                    if (eventType.equals(FootballEventType.PENALTY_GOAL)) {
                        stat.penalty++;
                    }
                    break;
            }
        }
    }

    @Override
    public void print(final Config config, final PrintStream out, final String title) {
        final int max = 10;
        if (config.isPlayerInfo()) {
            for (TournamentStat pl : players.values()) {
                String fixedName = config.getPlayerInfo(pl.id).getName();
                if (fixedName != null) {
                    pl.name = config.getPlayerInfo(pl.id).getName();
                }
            }
        }
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(players));
        sortedMap.putAll(players);
        out.println("<h2>Список лучших бомбардиров</h2>");
        out.println("<pre>");
        out.println("====================================================================");
        out.println("| Игрок                     | Команда               | Голов        |");
        out.println("|                           |                       | (с пенальти) |");
        out.println("====================================================================");
        int index = 0;
        int previous = Integer.MAX_VALUE;
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = players.get(s);
            if (index >= max && stat.goals != previous) {
                break;
            }
            previous = stat.goals;
            StringBuilder teams = new StringBuilder("");
            int counter = 0;
            for (String item : stat.teams) {
                if (counter != 0) {
                    teams.append(", ");
                }
                teams.append(item);
                counter++;
            }
            String goals = stat.penalty == 0 ? String.valueOf(stat.goals) : String.format("%s(%s)", stat.goals, stat.penalty);
            out.printf("| %-25s | %-21s | %-12s |%n", stat.name, teams, goals);
            index++;
        }
        out.println("====================================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        Collator collator = Collator.getInstance(new Locale("ru", "RU"));
        private int goals = 0;
        private int penalty = 0;
        private String name = "";
        private Set<String> teams = new TreeSet<>(collator);
        private String id = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

        public void addTeam(final String team) {
            if (!teams.contains(team)) {
                teams.add(team);
            }
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
            int value1 = stat1.goals;
            int value2 = stat2.goals;
            if (value1 < value2) {
                return 1;
            } else if (value1 > value2) {
                return -1;
            }
            String name1 = stat1.name;
            String name2 = stat2.name;
            return collator.compare(name1, name2);
        }
        
    }

    @Override
    public void drawCharts(String title) {
    }

}
