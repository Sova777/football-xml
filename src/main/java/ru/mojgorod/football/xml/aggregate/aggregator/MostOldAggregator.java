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
import java.util.Map;
import java.util.TreeMap;
import ru.mojgorod.football.xml.aggregate.SeasonParameters;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlPlayer;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class MostOldAggregator implements Aggregator {

    private final HashMap<String, TournamentStat> players = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        Integer date = xmlReport.getDateInt();
        String team1 = xmlReport.getTeam1();
        String team2 = xmlReport.getTeam2();
        String match = String.format("%s %s - %s %s:%s",
                xmlReport.getDateString(), xmlReport.getTeam1(), xmlReport.getTeam2(), xmlReport.getGoals1(), xmlReport.getGoals2());

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            TournamentStat stat = TournamentStat.get(players, player.getKey());
            if (date > stat.gameDateInt) {
                stat.name = player.getName();
                stat.team = team1;
                stat.gameDateInt = date;
                stat.match = match;
            }
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            TournamentStat stat = TournamentStat.get(players, player.getKey());
            if (date > stat.gameDateInt) {
                stat.name = player.getName();
                stat.team = team2;
                stat.gameDateInt = date;
                stat.match = match;
            }
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    TournamentStat stat = TournamentStat.get(players, event.getPlayerKey2());
                    if (date > stat.gameDateInt) {
                        stat.name = event.getPlayer2();
                        stat.team = event.getTeam();
                        stat.gameDateInt = date;
                        stat.match = match;
                    }
                    break;
            }
        }
    }

    @Override
    public void print(final SeasonParameters parameters) {
        PrintStream out = parameters.getOutput();
        for (Map.Entry<String, TournamentStat> entry : players.entrySet()) {
            Double playerAge = parameters.getPlayerInfo(entry.getKey()).getAge(entry.getValue().gameDateInt);
            players.get(entry.getKey()).age = playerAge;
            players.get(entry.getKey()).name = parameters.getPlayerInfo(entry.getKey()).getName();
        }
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(players));
        sortedMap.putAll(players);
        out.println("<h2 id='MostOldAggregator'>Наиболее возрастные игроки (возраст в момент последней игры)</h2>");
        out.println("<pre>");
        out.println("===============================================================================================================");
        out.println("| Игрок                | Команда               | Возраст | Матч                                               |");
        out.println("===============================================================================================================");
        int counter = 0;
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = players.get(s);
            out.printf(Locale.US, "| %-20s | %-21s | %-7.2f | %-50s |%n",
                    stat.name, stat.team, stat.age, stat.match);
            counter++;
            if (counter >= 10) {
                break;
            }
        }
        out.println("===============================================================================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private String name = "";
        private String team = "";
        private String match = "";
        private Integer gameDateInt = 0;
        private Double age = 0.0;

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
            Double value1 = stat1.age;
            if (value1 == null) {
                value1 = 0.0;
            }
            Double value2 = stat2.age;
            if (value2 == null) {
                value2 = 0.0;
            }
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
    public void drawCharts(final SeasonParameters parameters) {
    }

}
