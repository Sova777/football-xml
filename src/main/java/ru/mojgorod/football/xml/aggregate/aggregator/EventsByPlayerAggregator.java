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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
public class EventsByPlayerAggregator extends Aggregator {

    private static String playerId = getParameterValue("playerId");
    private final HashMap<String, TournamentStat> playersMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();
        int date = xmlReport.getDateInt();

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            if (playerId.equals(player.getKey())) {
                TournamentStat stat = TournamentStat.get(playersMap, teamKey1);
                stat.games++;
                stat.team = team1;
            }
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            if (playerId.equals(player.getKey())) {
                TournamentStat stat = TournamentStat.get(playersMap, teamKey2);
                stat.games++;
                stat.team = team2;
            }
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            String team = event.getTeam();
            String teamKey;
            if (team.equals(team1)) {
                teamKey = teamKey1;
            } else if (team.equals(team2)) {
                teamKey = teamKey2;
            } else {
                Logger.getLogger(EventsByPlayerAggregator.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                continue;
            }

            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    if (playerId.equals(event.getPlayerKey2())) {
                        TournamentStat stat = TournamentStat.get(playersMap, teamKey);
                        stat.team = team;
                        if (stat.firstGame > date) {
                            stat.firstGame = date;
                        }
                        stat.games++;
                    }
                    break;
                case GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(playersMap, teamKey);
                        stat.team = team;
                        if (stat.firstGame > date) {
                            stat.firstGame = date;
                        }
                        stat.goals++;
                    }
                    break;
                case PENALTY_GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(playersMap, teamKey);
                        stat.team = team;
                        if (stat.firstGame > date) {
                            stat.firstGame = date;
                        }
                        stat.goals++;
                        stat.penalty++;
                    }
                    break;
                case PENALTY_MISSED:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(playersMap, teamKey);
                        stat.team = team;
                        if (stat.firstGame > date) {
                            stat.firstGame = date;
                        }
                        stat.penaltyMissed++;
                    }
                    break;
                case AUTOGOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(playersMap, teamKey);
                        stat.team = team;
                        if (stat.firstGame > date) {
                            stat.firstGame = date;
                        }
                        stat.autogoal++;
                    }
                    break;                        
                case RED_AND_YELLOW_CARD:
                case RED_CARD:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(playersMap, teamKey);
                        stat.team = team;
                        if (stat.firstGame > date) {
                            stat.firstGame = date;
                        }
                        stat.redCards++;
                    }
                    break;
                case YELLOW_CARD:
                    if (playerId.equals(event.getPlayerKey1())) {
                        TournamentStat stat = TournamentStat.get(playersMap, teamKey);
                        stat.team = team;
                        if (stat.firstGame > date) {
                            stat.firstGame = date;
                        }
                        stat.yellowCards++;
                    }
                    break;
            }
        }
    }

    @Override
    public void afterSeason() {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(playersMap));
        sortedMap.putAll(playersMap);
        for (Map.Entry<String, TournamentStat> entry : sortedMap.entrySet()) {
            TournamentStat tournamentStat = entry.getValue();
            SeasonStat seasonStat = new SeasonStat();
            seasonStat.team = tournamentStat.team;
            seasonStat.firstGame = tournamentStat.firstGame;
            seasonStat.games = tournamentStat.games;
            seasonStat.goals = tournamentStat.goals;
            seasonStat.penalty = tournamentStat.penalty;
            seasonStat.penaltyMissed = tournamentStat.penaltyMissed;
            seasonStat.autogoal = tournamentStat.autogoal;
            seasonStat.redCards = tournamentStat.redCards;
            seasonStat.yellowCards = tournamentStat.yellowCards;
            seasonStat.title = getSeason().getTitle();
            seasons.add(seasonStat);
        }
        playersMap.clear();
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='EventsByPlayerAggregator'>Статистика за сезон</h2>");
        out.println("<pre>");
        out.println("====================================================================================================================");
        out.println("| Сезон           | Команда         | Матчей     | Мячей      | Незабитые  | Забито в    | Удалений   | Предупре-  |");
        out.println("|                 |                 |            |(с пенальти)| пенальти   | свои ворота |            | ждений     |");
        out.println("====================================================================================================================");
        int games = 0;
        int goals = 0;
        int penalty = 0;
        int penaltyMissed = 0;
        int autogoal = 0;
        int redCards = 0;
        int yellowCards = 0;
        for (SeasonStat season : seasons) {
            if (season.games > 0 || season.yellowCards > 0 || season.redCards > 0) {
                games += season.games;
                goals += season.goals;
                penalty += season.penalty;
                penaltyMissed += season.penaltyMissed;
                autogoal += season.autogoal;
                redCards += season.redCards;
                yellowCards += season.yellowCards;
                String seasonGoals = (season.penalty > 0)
                        ? String.valueOf(season.goals) + "(" + season.penalty + ")"
                        : String.valueOf(season.goals);
                out.printf("| %-15s | %-15s | %-10d | %-10s | %-10d | %-11d | %-10d | %-10d |%n",
                        season.title, season.team, season.games, seasonGoals, season.penaltyMissed, season.autogoal, season.redCards, season.yellowCards);
            }
        }
        out.println("-------------------------------------------------------------------------------------------------------------------");
        String allGoals = (penalty > 0)
                ? String.valueOf(goals) + "(" + penalty + ")"
                : String.valueOf(goals);
        out.printf("| %-15s | %-15s | %-10d | %-10s | %-10d | %-11d | %-10d | %-10d |%n",
                "Итого", "", games, allGoals, penaltyMissed, autogoal, redCards, yellowCards);
        out.println("====================================================================================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private String title = "";
        private String team = "";
        private int games = 0;
        private int goals = 0;
        private int penalty = 0;
        private int penaltyMissed = 0;
        private int autogoal = 0;
        private int yellowCards = 0;
        private int redCards = 0;
        private int firstGame = Integer.MAX_VALUE;

    }

    static private class TournamentStat {

        private int games = 0;
        private int goals = 0;
        private int penalty = 0;
        private int penaltyMissed = 0;
        private int autogoal = 0;
        private int yellowCards = 0;
        private int redCards = 0;
        private int firstGame = Integer.MAX_VALUE;
        private String team = "";
        private HashMap<String, Integer> teams = new HashMap<>();

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                TournamentStat stat = new TournamentStat();
                stat.teams = new HashMap<>();
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
            int date1 = stat1.firstGame;
            int date2 = stat2.firstGame;
            if (date1 < date2) {
                return -1;
            } else if (date1 > date2) {
                return 1;
            }
            return collator.compare(stat1.team, stat2.team);
        }

    }

}
