/*
Copyright (c) 2024, Valeriy Soldatov
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class StrongComebackDrawAggregator extends Aggregator {

    protected final ArrayList<TournamentStat> matches = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        if (!xmlReport.isValidScore()) {
            return;
        }
        int maxDifference=0;
        int goals1 = xmlReport.getGoalsInt1();
        int goals2 = xmlReport.getGoalsInt2();
        if (goals1 != goals2) {
            return;
        }

        int currentGoals1 = 0;
        int currentGoals2 = 0;
        String score = "";
        String team1 = xmlReport.getTeam1();

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            if (eventType.isAnyGoal()) {
                String team = event.getTeam();
                if (eventType.isGoal() || eventType.isPenaltyGoal()) {
                    if (team.equals(team1)) {
                        currentGoals1++;
                    } else {
                        currentGoals2++;
                    }
                } else if (eventType.isAutoGoal()) {
                    if (team.equals(team1)) {
                        currentGoals2++;
                    } else {
                        currentGoals1++;
                    }
                }
            }
            if (Math.abs(currentGoals1 - currentGoals2) > Math.abs(maxDifference)) {
                maxDifference = currentGoals1 - currentGoals2;
                score = String.format("%s:%s", currentGoals1, currentGoals2);
            }
        }
        if (Math.abs(maxDifference) > 1) {
            TournamentStat tournamentStat = new TournamentStat();
            tournamentStat.match = String.format("%s %s - %s %s:%s",
                    xmlReport.getDateString(), xmlReport.getTeam1(), xmlReport.getTeam2(), xmlReport.getGoals1(), xmlReport.getGoals2());
            tournamentStat.teamWinner = (maxDifference > 0) ? xmlReport.getTeam1() : xmlReport.getTeam2();
            tournamentStat.teamLooser = (maxDifference > 0) ? xmlReport.getTeam2() : xmlReport.getTeam1();
            tournamentStat.difference = Math.abs(maxDifference);
            tournamentStat.score = score;
            tournamentStat.date = xmlReport.getDate();
            matches.add(tournamentStat);
        }
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        Collections.sort(matches, new StatArrayComparator());
        out.println("<h2 id='StrongComebackDrawAggregator'>Сыграли вничью, играя с разницей в 2 или более мяча</h2>");
        out.println("<pre>");
        out.println("=====================================================================================================================");
        out.println("| Побеждал             | Проигрывал           | Матч                                               | Счёт | Разница |");
        out.println("=====================================================================================================================");
        for (TournamentStat stat : matches) {
            out.printf("| %-20s | %-20s | %-50s | %-4s | %-7s |%n",
                    stat.teamWinner, stat.teamLooser, stat.match, stat.score, stat.difference);
        }
        out.println("=====================================================================================================================");
        out.println( "</pre>");
    }

    static protected class TournamentStat {

        protected String teamWinner = "";
        protected String teamLooser = "";
        protected int difference = 0;
        protected String score = "";
        protected String match = "";
        protected String date = "";

    }

    static protected class StatArrayComparator implements Comparator<TournamentStat> {

        Collator collator = Collator.getInstance(new Locale("ru", "RU"));

        @Override
        public int compare(TournamentStat stat1, TournamentStat stat2) {
            String value1 = stat1.date + "|" + stat1.teamWinner + "|" + stat1.teamLooser + "|" + stat1.match;
            String value2 = stat2.date + "|" + stat2.teamWinner + "|" + stat2.teamLooser + "|" + stat2.match;
            return collator.compare(value1, value2);
        }
        
    }

}
