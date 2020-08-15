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
import java.util.HashMap;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.Utils;

/**
 *
 * @author sova
 */
public class AttendanceByTeamsAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> teamsMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();
    private final static ArrayList<String> teamNames = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();

        Integer attendanceInteger = xmlReport.getStadiumAttendanceInt();
        int attendanceValue = (attendanceInteger == null) ? 0 : attendanceInteger;

        TournamentStat stat1 = TournamentStat.get(teamsMap, teamKey1);
        stat1.attendanceHome += attendanceValue;
        stat1.gamesHome++;
        stat1.team = team1;

        TournamentStat stat2 = TournamentStat.get(teamsMap, teamKey2);
        stat2.attendanceVisitor += attendanceValue;
        stat2.gamesVisitor++;
        stat2.team = team2;
    }

    @Override
    public void afterSeason() {
        SeasonStat seasonStat = new SeasonStat();
        int index = 0;
        for (String teamId : getConfigFile().getParameterValues("teamId")) {
            TournamentStat tournamentStat = teamsMap.get(teamId);
            TeamsStat stat = new TeamsStat();
            if (tournamentStat != null) {
                stat.attendanceHome = tournamentStat.attendanceHome;
                stat.gamesHome = tournamentStat.gamesHome;
                stat.attendanceVisitor = tournamentStat.attendanceVisitor;
                stat.gamesVisitor = tournamentStat.gamesVisitor;
                stat.team = tournamentStat.team;
                if (!"".equals(stat.team)) {
                    if (teamNames.size() < index + 1) {
                        teamNames.add(stat.team);
                    } else {
                        teamNames.set(index, stat.team);
                    }
                }
            }
            seasonStat.teamsStat.add(stat);
            index++;
        }
        seasonStat.title = getSeason().getTitle();
        seasons.add(seasonStat);
        teamsMap.clear();
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        int teams = teamNames.size();
        out.println("<h2 id='AttendanceByTeamsAggregator'>Средняя посещаемость за сезон</h2>");
        out.println( "<pre>");
        out.println(Utils.repeatText("=", 19 + 26 * teams));
        out.printf("| Сезон           |");
        for (int i = 0; i < teams; i++) {
            out.printf(" %s |", Utils.centerText(teamNames.get(i), 23));
        }
        out.println();
        out.println("|                 |" + Utils.repeatText("------------|------------|", teams));
        out.println("|                 |" + Utils.repeatText(" Дома       | В гостях   |", teams));
        out.println(Utils.repeatText("=", 19 + 26 * teams));
        for (SeasonStat season : seasons) {
            out.printf("| %-15s |", season.title);
            for (TeamsStat stat : season.teamsStat) {
                int averageHome = (stat.gamesHome == 0) ? 0 : (stat.attendanceHome / stat.gamesHome);
                int averageVisitor = (stat.gamesVisitor == 0) ? 0 : (stat.attendanceVisitor / stat.gamesVisitor);
                out.printf(" %-10d | %-10d |",
                        averageHome, averageVisitor);
            }
            out.println();
        }
        out.println(Utils.repeatText("=", 19 + 26 * teams));
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private String title = "";
        ArrayList<TeamsStat> teamsStat = new ArrayList<>();

    }

    static private class TeamsStat {

        private int attendanceHome = 0;
        private int attendanceVisitor = 0;
        private int gamesHome = 0;
        private int gamesVisitor = 0;
        private String team = "";

    }

    static private class TournamentStat {

        private int attendanceHome = 0;
        private int attendanceVisitor = 0;
        private int gamesHome = 0;
        private int gamesVisitor = 0;
        private String team = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

}
