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
import java.util.HashMap;
import java.util.Map.Entry;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlGooalkeeperStat;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.UtilsGoalkeepers;

/**
 *
 * @author sova
 */
public class GoalkeeperMatchesByPlayerAggregator extends Aggregator {

    private static String playerId = getConfigFile().getParameterValue("playerId");
    private static final ArrayList<TournamentStat> matches = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        final HashMap<String, FootballXmlGooalkeeperStat> matchKeepers = UtilsGoalkeepers.getMatchStat(xmlReport);
        for (Entry<String, FootballXmlGooalkeeperStat> entry : matchKeepers.entrySet()) {
            FootballXmlGooalkeeperStat matchStat = entry.getValue();
            if (playerId.equals(matchStat.getKey())) {
                TournamentStat stat = new TournamentStat();
                stat.date = xmlReport.getDateInt();
                stat.match = String.format("%s %s - %s %s:%s",
                        xmlReport.getDateString(), xmlReport.getTeam1(), xmlReport.getTeam2(), xmlReport.getGoals1(), xmlReport.getGoals2());
                stat.goals = matchStat.getGoals();
                stat.penalty = matchStat.getPenaltySuccess();
                stat.penaltyMissed = matchStat.getPenaltyMissed();
                stat.autogoal = matchStat.getAutogoals();
                stat.yellowCards = matchStat.getYellowCards();
                stat.redCards = matchStat.getRedCards();
                stat.from = matchStat.getFrom();
                stat.till = matchStat.getTill();
                matches.add(stat);
            }
        }
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        Collections.sort(matches, new StatComparator());
//        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(matches));
//        sortedMap.putAll(matches);
        out.println("<h2 id='GoalkeeperMatchesByPlayerAggregator'>Все матчи</h2>");
        out.println("<pre>");
        out.println("============================================================================================================================================");
        out.println("| Матч                                               | Время игры         | Пропущено  |        Пенальти         | Удалений   | Предупре-  |");
        out.println("|                                                    |                    |            |------------|------------| с поля     | ждений     |");
        out.println("|                                                    |                    |            | Не забито  | Пропущено  |            |            |");
        out.println("============================================================================================================================================");
        for (TournamentStat stat : matches) {
            String goals = String.valueOf(stat.goals);
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
            String penalty = stat.penalty == 0 ? "" : String.valueOf(stat.penalty);
            String autogoal = stat.autogoal == 0 ? "" : String.valueOf(stat.autogoal);
            String redCards = stat.redCards == 0 ? "" : String.valueOf(stat.redCards);
            String yellowCards = stat.yellowCards == 0 ? "" : String.valueOf(stat.yellowCards);
            out.printf("| %-50s | %-18s | %-10s | %-10s | %-10s | %-10s | %-10s |%n",
                    stat.match, time, goals, penaltyMissed, penalty, redCards, yellowCards);
        }
        out.println("============================================================================================================================================");
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
