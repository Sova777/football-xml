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
public class EventsByTeamsAggregator extends Aggregator {

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

            boolean isTeam1 = false;
            boolean isTeam2 = false;
            FootballEventType eventType = event.getEventType();
            String team = event.getTeam();
            if (team.equals(team1)) {
                isTeam1 = true;
            } else if (team.equals(team2)) {
                isTeam2 = true;
            } else {
                Logger.getLogger(EventsBySeasonAggregator.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
            }
            switch (eventType) {
                case RED_AND_YELLOW_CARD:
                case RED_CARD:
                    if (isTeam1) {
                        stat1.redCards++;
                        stat2.redCardsOpponent++;
                    } else if (isTeam2) {
                        stat2.redCards++;
                        stat1.redCardsOpponent++;
                    }
                    break;
                case YELLOW_CARD:
                    if (isTeam1) {
                        stat1.yellowCards++;
                        stat2.yellowCardsOpponent++;
                    } else if (isTeam2) {
                        stat2.yellowCards++;
                        stat1.yellowCardsOpponent++;
                    }
                    break;
                case PENALTY_GOAL:
                case PENALTY_MISSED:
                    if (isTeam1) {
                        stat1.penalty++;
                        stat2.penaltyOpponent++;
                    } else if (isTeam2) {
                        stat2.penalty++;
                        stat1.penaltyOpponent++;
                    }
                    break;
            }
        }
    }

    @Override
    public void afterSeason() {
        SeasonStat seasonStat = new SeasonStat();
        int index = 0;
        for (String teamId : getConfigFile().getParameterValues("teamId")) {
            TournamentStat tournamentStat = teamsMap.get(teamId);
            TeamsStat stat = new TeamsStat();
            if (tournamentStat != null) {
                stat.redCards = tournamentStat.redCards;
                stat.redCardsOpponent = tournamentStat.redCardsOpponent;
                stat.yellowCards = tournamentStat.yellowCards;
                stat.yellowCardsOpponent = tournamentStat.yellowCardsOpponent;
                stat.penalty = tournamentStat.penalty;
                stat.penaltyOpponent = tournamentStat.penaltyOpponent;
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
        out.println("<h2 id='EventsByTeamsAggregator'>Удаления, предупреждения, пробито пенальти</h2>");
        out.println("<p class=\"text\">Перед скобками указана статистика команды, а в скобках аналогичное событие у соперника.</p>");
        out.println( "<pre>");
        out.println(Utils.repeatText("=", 19 + 36 * teams));
        out.printf("| Сезон           |");
        for (int i = 0; i < teams; i++) {
            out.printf(" %s |", Utils.centerText(teamNames.get(i), 33));
        }
        out.println();
        out.println("|                 |" + Utils.repeatText("-----------|-----------|-----------|", teams));
        out.println("|                 |" + Utils.repeatText(" Удалений  | Предупреж.| Пенальти  |", teams));
        out.println(Utils.repeatText("=", 19 + 36 * teams));
        for (SeasonStat season : seasons) {
            out.printf("| %-15s |", season.title);
            for (TeamsStat stat : season.teamsStat) {
                String redCards = stat.redCards + "(" + stat.redCardsOpponent + ")";
                String yellowCards = stat.yellowCards + "(" + stat.yellowCardsOpponent + ")";
                String penalty = stat.penalty + "(" + stat.penaltyOpponent + ")";
                out.printf(" %-9s | %-9s | %-9s |",
                        redCards, yellowCards, penalty);
            }
            out.println();
        }
        out.println(Utils.repeatText("=", 19 + 36 * teams));
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private String title = "";
        ArrayList<TeamsStat> teamsStat = new ArrayList<>();

    }

    static private class TeamsStat {

        private int yellowCards = 0;
        private int yellowCardsOpponent = 0;
        private int redCards = 0;
        private int redCardsOpponent = 0;
        private int penalty = 0;
        private int penaltyOpponent = 0;
        private String team = "";

    }

    static private class TournamentStat {

        private int yellowCards = 0;
        private int yellowCardsOpponent = 0;
        private int redCards = 0;
        private int redCardsOpponent = 0;
        private int penalty = 0;
        private int penaltyOpponent = 0;
        private String team = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

}
