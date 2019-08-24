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
package ru.mojgorod.football.xml.aggregate.aggregator;

import java.io.PrintStream;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlPlayer;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class TeamsByPlayerAggregator extends Aggregator {

    private static String playerId = getParameterValue("playerId");
    private static final HashMap<String, TournamentStat> teamMap = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            if (playerId.equals(player.getKey())) {
                TournamentStat stat = TournamentStat.get(teamMap, teamKey1);
                stat.games++;
                stat.team = team1;
            }
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            if (playerId.equals(player.getKey())) {
                TournamentStat stat = TournamentStat.get(teamMap, teamKey2);
                stat.games++;
                stat.team = team2;
            }
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            String team = event.getTeam();
            String teamIdCurrent;
            String teamCurrent;
            if (team.equals(team1)) {
                teamIdCurrent = teamKey1;
                teamCurrent = team1;
            } else if (team.equals(team2)) {
                teamIdCurrent = teamKey2;
                teamCurrent = team2;
            } else {
                Logger.getLogger(TeamsByPlayerAggregator.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                continue;
            }

            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    if (playerId.equals(event.getPlayerKey2())) {
                        TournamentStat stat = TournamentStat.get(teamMap, teamIdCurrent);
                        stat.games++;
                        stat.team = teamCurrent;
                    }
                    break;
                case GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(teamMap, teamIdCurrent);
                        stat.goals++;
                        stat.team = teamCurrent;
                    }
                    break;
                case PENALTY_GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(teamMap, teamIdCurrent);
                        stat.goals++;
                        stat.penalty++;
                        stat.team = teamCurrent;
                    }
                    break;
                case PENALTY_MISSED:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(teamMap, teamIdCurrent);
                        stat.penaltyMissed++;
                        stat.team = teamCurrent;
                    }
                    break;
                case AUTOGOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(teamMap, teamIdCurrent);
                        stat.autogoal++;
                        stat.team = teamCurrent;
                    }
                    break;
                case RED_AND_YELLOW_CARD:
                case RED_CARD:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(teamMap, teamIdCurrent);
                        stat.redCards++;
                        stat.team = teamCurrent;
                    }
                    break;
                case YELLOW_CARD:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(teamMap, teamIdCurrent);
                        stat.yellowCards++;
                        stat.team = teamCurrent;
                    }
                    break;
            }
        }
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teamMap));
        sortedMap.putAll(teamMap);
        out.println("<h2 id='TeamsByPlayerAggregator'>Статистика по командам</h2>");
        out.println("<pre>");
        out.println("==================================================================================================");
        out.println("| Команда         | Матчей     | Мячей      | Незабитые  | Забито в    | Удалений   | Предупре-  |");
        out.println("|                 |            |(с пенальти)| пенальти   | свои ворота |            | ждений     |");
        out.println("==================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teamMap.get(s);
            if (stat.games > 0 || stat.yellowCards > 0 || stat.redCards > 0) {
                String goals = (stat.penalty > 0)
                        ? String.valueOf(stat.goals) + "(" + stat.penalty + ")"
                        : String.valueOf(stat.goals);
                out.printf("| %-15s | %-10d | %-10s | %-10d | %-11d | %-10d | %-10d |%n",
                        stat.team, stat.games, goals, stat.penaltyMissed, stat.autogoal, stat.redCards, stat.yellowCards);
            }
        }
        out.println("==================================================================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private int games = 0;
        private int goals = 0;
        private int penalty = 0;
        private int penaltyMissed = 0;
        private int autogoal = 0;
        private int yellowCards = 0;
        private int redCards = 0;
        private String team = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                TournamentStat stat = new TournamentStat();
                hashStat.put(keyStat, stat);
            }
            return hashStat.get(keyStat);
        }

    }

    static private class StatComparator implements Comparator<String> {

        Collator collator = Collator.getInstance(new Locale("ru", "RU"));
        private final HashMap<String, TournamentStat> map;

        public StatComparator(final HashMap<String, TournamentStat> map) {
            this.map = map;
        }

        @Override
        public int compare(String key1, String key2) {
            TournamentStat stat1 = map.get(key1);
            TournamentStat stat2 = map.get(key2);
            return collator.compare(stat1.team, stat2.team);
        }
        
    }

}
