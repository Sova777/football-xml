/*
Copyright (c) 2026, Valeriy Soldatov
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
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class MatchesMostGoalsAggregator extends Aggregator {

    private final ArrayList<TournamentStat> matches = new ArrayList<>();
    private int max = -1;

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        if (!xmlReport.isValidScore()) {
            return;
        }

        String team1 = xmlReport.getTeam1();
        String team2 = xmlReport.getTeam2();

        int goals1 = xmlReport.getFinalGoalsInt1();
        int goals2 = xmlReport.getFinalGoalsInt2();
        int goals = goals1 + goals2;

        if (goals >= max) {
            String winnerName = xmlReport.isFinalWinTeam1() ? team1 : team2;
            String looserName = (winnerName.equals(team1)) ? team2 : team1;

            if (goals > max) {
                max = goals;
                matches.clear();
            }

            TournamentStat tournamentStat = new TournamentStat();
            tournamentStat.teamWinner = winnerName;
            tournamentStat.teamLooser = looserName;
            tournamentStat.match = String.format("%s %s - %s %s",
                    xmlReport.getDateString(), xmlReport.getTeam1(), xmlReport.getTeam2(), xmlReport.getFormattedScore());
            matches.add(tournamentStat);
        }
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        out.println("<h2 id='MatchesMostGoalsAggregator'>Максимальное число забитых мячей в матче</h2>");
        out.println("<pre>");
        out.println("======================================================");
        out.println("| Матч                                               |");
        out.println("======================================================");
        for (TournamentStat stat : matches) {
            out.printf("| %-50s |%n", stat.match);
        }
        out.println("======================================================");
        out.println("</pre>");
    }

    static private class TournamentStat {

        private String teamWinner = "";
        private String teamLooser = "";
        private String match = "";

    }

}
