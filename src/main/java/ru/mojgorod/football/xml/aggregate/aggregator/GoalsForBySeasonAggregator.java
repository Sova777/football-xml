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
import java.util.ArrayList;
import java.util.HashMap;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class GoalsForBySeasonAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> teamsMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        int goals1 = xmlReport.getGoalsInt1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();
        int goals2 = xmlReport.getGoalsInt2();
        TournamentStat stat1 = TournamentStat.get(teamsMap, teamKey1);
        stat1.name = team1;
        stat1.goals += goals1;
        TournamentStat stat2 = TournamentStat.get(teamsMap, teamKey2);
        stat2.name = team2;
        stat2.goals += goals2;
    }

    @Override
    public void afterSeason() {
        int maxGoals = 0;
        int minGoals = Integer.MAX_VALUE;
        StringBuilder maxTeams = new StringBuilder("");
        StringBuilder minTeams = new StringBuilder("");

        for (TournamentStat team : teamsMap.values()) {
            int goalsValue = team.goals;
            if (goalsValue >= maxGoals) {
                if (goalsValue == maxGoals) {
                    maxTeams.append(", ").append(team.name);
                } else {
                    maxGoals = goalsValue;
                    maxTeams = new StringBuilder(team.name);
                }
            }
            if (goalsValue <= minGoals) {
                if (goalsValue == minGoals) {
                    minTeams.append(", ").append(team.name);
                } else {
                    minGoals = goalsValue;
                    minTeams = new StringBuilder(team.name);
                }
            }
        }

        SeasonStat stat = new SeasonStat();
        stat.goalsMax = maxGoals;
        stat.teamsMax = maxTeams.toString();
        stat.goalsMin = minGoals;
        stat.teamsMin = minTeams.toString();
        stat.title = getSeason().getTitle();
        teamsMap.clear();
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='GoalsForBySeasonAggregator'>Самые результативные и наименее результативные команды</h2>");
        out.println("<pre>");
        out.println("=====================================================================================================");
        out.println("| Сезон           | Мячей      | Команда                   | Мячей      | Команда                   |");
        out.println("=====================================================================================================");
        for (SeasonStat stat : seasons) {
            out.printf("| %-15s | %-10d | %-25s | %-10d | %-25s |%n", stat.title, stat.goalsMax, stat.teamsMax,
                    stat.goalsMin, stat.teamsMin);
        }
        out.println("=====================================================================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private int goalsMax = 0;
        private String teamsMax = "";
        private int goalsMin = 0;
        private String teamsMin = "";
        private String title = "";

    }

    static private class TournamentStat {

        private int goals = 0;
        private String name = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

}
