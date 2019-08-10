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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlPlayer;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class MatchesByPlayerAggregator extends Aggregator {

    private static String playerId = getConfigFile().getParameterValue("playerId");
    private static final ArrayList<TournamentStat> matches = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        boolean isPlayed = false;
        int goals = 0;
        int penalty = 0;
        int penaltyMissed = 0;
        int autogoal = 0;
        int yellowCards = 0;
        int redCards = 0;
        String from = "";
        String till = "";

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            if (playerId.equals(player.getKey())) {
                isPlayed = true;
                break;
            }
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            if (playerId.equals(player.getKey())) {
                isPlayed = true;
                break;
            }
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    if (playerId.equals(event.getPlayerKey2())) {
                        isPlayed = true;
                        from = event.getTime();
                    }
                    if (playerId.equals(event.getPlayerKey1())) {
                        till = event.getTime();
                    }
                    break;
                case GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        goals++;
                    }
                    break;
                case PENALTY_GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        goals++;
                        penalty++;
                    }
                    break;
                case PENALTY_MISSED:
                    if (playerId.equals(event.getPlayerKey1())) {
                        penaltyMissed++;
                    }
                    break;
                case AUTOGOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        autogoal++;
                    }
                    break;
                case YELLOW_CARD:
                    if (playerId.equals(event.getPlayerKey1())) {
                        yellowCards++;
                    }
                    break;
                case RED_AND_YELLOW_CARD:
                case RED_CARD:
                    if (playerId.equals(event.getPlayerKey1())) {
                        redCards++;
                    }
                    break;
            }
        }

        if (isPlayed) {
            TournamentStat stat = new TournamentStat();
            stat.date = xmlReport.getDateInt();
            stat.match = String.format("%s %s - %s %s:%s",
                    xmlReport.getDateString(), xmlReport.getTeam1(), xmlReport.getTeam2(), xmlReport.getGoals1(), xmlReport.getGoals2());
            stat.goals = goals;
            stat.penalty = penalty;
            stat.penaltyMissed = penaltyMissed;
            stat.autogoal = autogoal;
            stat.yellowCards = yellowCards;
            stat.redCards = redCards;
            stat.from = from;
            stat.till = till;
            matches.add(stat);
        }
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        Collections.sort(matches, new StatComparator());
//        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(matches));
//        sortedMap.putAll(matches);
        out.println("<h2 id='MatchesByPlayerAggregator'>Все матчи</h2>");
        out.println("<pre>");
        out.println("===================================================================================================================================");
        out.println("| Матч                                     | Замены             | Мячей      | Незабитые  | Забито в    | Удалений   | Предупре-  |");
        out.println("|                                          |                    |(с пенальти)| пенальти   | свои ворота |            | ждений     |");
        out.println("===================================================================================================================================");
        for (TournamentStat stat : matches) {
            String goals = (stat.penalty > 0)
                    ? String.valueOf(stat.goals) + "(" + stat.penalty + ")"
                    : String.valueOf(stat.goals);
            if ("0".equals(goals)) {
                goals = "";
            }
            String time = "";
            if (!"".equals(stat.from)) {
                time += "с " + stat.from;
            }
            if (!"".equals(stat.till)) {
                time += (("".equals(time)) ? "по " : " по") + stat.till;
            }
            if (!"".equals(time)) {
                time += " мин.";
            }
            String penaltyMissed = stat.penaltyMissed == 0 ? "" : String.valueOf(stat.penaltyMissed);
            String autogoal = stat.autogoal == 0 ? "" : String.valueOf(stat.autogoal);
            String redCards = stat.redCards == 0 ? "" : String.valueOf(stat.redCards);
            String yellowCards = stat.yellowCards == 0 ? "" : String.valueOf(stat.yellowCards);
            out.printf("| %-40s | %-18s | %-10s | %-10s | %-11s | %-10s | %-10s |%n",
                    stat.match, time, goals, penaltyMissed, autogoal, redCards, yellowCards);
        }
        out.println("===================================================================================================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private int date = 0;
        private int games = 0;
        private int goals = 0;
        private int penalty = 0;
        private int penaltyMissed = 0;
        private int autogoal = 0;
        private int yellowCards = 0;
        private int redCards = 0;
        private String from = "";
        private String till = "";
        private String match = "";

    }

    static private class StatComparator implements Comparator<TournamentStat> {

        @Override
        public int compare(TournamentStat stat1, TournamentStat stat2) {
            int date1 = stat1.date;
            int date2 = stat2.date;
            if (date1 < date2) {
                return -1;
            } else if (date1 > date2) {
                return 1;
            }
            return 0;
        }
        
    }

}
