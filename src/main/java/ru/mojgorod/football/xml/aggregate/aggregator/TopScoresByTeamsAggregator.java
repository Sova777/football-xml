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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.Utils;

/**
 *
 * @author sova
 */
public class TopScoresByTeamsAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> teamsMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();
    private final static ArrayList<String> teamNames = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();

        TournamentStat stat1 = TournamentStat.get(teamsMap, teamKey1);
        stat1.team = team1;
        TournamentStat stat2 = TournamentStat.get(teamsMap, teamKey2);
        stat2.team = team2;

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            String team = event.getTeam();
            TournamentStat statCurrent;
            if (team.equals(team1)) {
                statCurrent = stat1;
            } else if (team.equals(team2)) {
                statCurrent = stat2;
            } else {
                Logger.getLogger(GoalsForAggregator.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                continue;
            }

            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case GOAL:
                case PENALTY_GOAL:
                    statCurrent.addPlayer(event.getPlayerKey1());
                    break;
            }
        }

    }

    @Override
    public void afterSeason() {
        SeasonStat seasonStat = new SeasonStat();
        int index = 0;
        if (isPlayerInfo()) {
            for (String teamId : getConfigFile().getParameterValues("teamId")) {
                TournamentStat tournamentStat = teamsMap.get(teamId);
                TeamsStat stat = new TeamsStat();
                if (tournamentStat != null) {
                    int maxGoals = 0;
                    StringBuilder players = new StringBuilder("");
                    for (Map.Entry<String, Integer> entry : tournamentStat.players.entrySet()) {
                        String key = entry.getKey();
                        int goals = entry.getValue();
                        if (goals >= maxGoals) {
                            if (isPlayerInfo()) {
                                String fixedName = getPlayerInfo(key).getName();
                                if (fixedName != null) {
                                    if (goals == maxGoals) {
                                        players.append(", ").append(fixedName);
                                    } else {
                                        players = new StringBuilder(fixedName);
                                    }
                                }
                            }
                            maxGoals = goals;
                        }
                    }
                    stat.team = tournamentStat.team;
                    stat.players = players.toString();
                    stat.goals = maxGoals;
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
        }
        seasonStat.title = getSeason().getTitle();
        seasons.add(seasonStat);
        teamsMap.clear();
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        int teams = teamNames.size();
        out.println("<h2 id='TopScoresByTeamsAggregator'>Список лучших бомбардиров клуба</h2>");
        out.println( "<pre>");
        out.println(Utils.repeatText("=", 19 + 37 * teams));
        out.printf("| Сезон           |");
        for (int i = 0; i < teams; i++) {
            out.printf(" %s |", Utils.centerText(teamNames.get(i), 34));
        }
        out.println();
        out.println(Utils.repeatText("=", 19 + 37 * teams));
        for (SeasonStat season : seasons) {
            out.printf("| %-15s |", season.title);
            for (TeamsStat stat : season.teamsStat) {
                String players = stat.players;
                if (players.length() > 29) {
                    players = players.substring(0, 26) + "...";
                }
                out.printf(" %-2d | %-29s |", stat.goals, players);
            }
            out.println();
        }
        out.println(Utils.repeatText("=", 19 + 37 * teams));
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private String title = "";
        ArrayList<TeamsStat> teamsStat = new ArrayList<>();

    }

    static private class TeamsStat {

        private int goals = 0;
        private String players = "";
        private String team = "";

    }

    static private class TournamentStat {

        private HashMap<String, Integer> players = new HashMap<>();
        private String team = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                TournamentStat stat = new TournamentStat();
                stat.players = new HashMap<>();
                hashStat.put(keyStat, stat);
            }
            return hashStat.get(keyStat);
        }

        public void addPlayer(final String keyPlayer) {
            Integer value = players.get(keyPlayer);
            if (value == null) {
                players.put(keyPlayer, 1);
            } else {
                players.put(keyPlayer, value + 1);
            }
        }
    }

}
