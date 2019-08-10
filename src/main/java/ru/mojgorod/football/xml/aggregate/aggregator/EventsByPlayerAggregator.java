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

    private static String playerId = getConfigFile().getParameterValue("playerId");
    private final HashMap<String, TournamentStat> playersMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        TournamentStat stat = TournamentStat.get(playersMap, playerId);

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            if (playerId.equals(player.getKey())) {
                stat.games++;
            }
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            if (playerId.equals(player.getKey())) {
                stat.games++;
            }
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    if (playerId.equals(event.getPlayerKey2())) {
                        stat.games++;
                    }
                    break;
                case GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        stat.goals++;
                    }
                    break;
                case PENALTY_GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        stat.goals++;
                        stat.penalty++;
                    }
                    break;
                case PENALTY_MISSED:
                    if (playerId.equals(event.getPlayerKey1())) {
                        stat.penaltyMissed++;
                    }
                    break;
                case AUTOGOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        stat.autogoal++;
                    }
                    break;                        
                case RED_AND_YELLOW_CARD:
                case RED_CARD:
                    if (playerId.equals(event.getPlayerKey1())) {
                        stat.redCards++;
                    }
                    break;
                case YELLOW_CARD:
                    if (playerId.equals(event.getPlayerKey1())) {
                        stat.yellowCards++;
                    }
                    break;
            }
        }
    }

    @Override
    public void afterSeason() {
        SeasonStat seasonStat = new SeasonStat();
        TournamentStat tournamentStat = playersMap.get(playerId);
        seasonStat.games = tournamentStat.games;
        seasonStat.goals = tournamentStat.goals;
        seasonStat.penalty = tournamentStat.penalty;
        seasonStat.penaltyMissed = tournamentStat.penaltyMissed;
        seasonStat.autogoal = tournamentStat.autogoal;
        seasonStat.redCards = tournamentStat.redCards;
        seasonStat.yellowCards = tournamentStat.yellowCards;
        seasonStat.title = getSeason().getTitle();
        seasons.add(seasonStat);
        playersMap.clear();
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='EventsByPlayerAggregator'>Статистика за сезон</h2>");
        out.println("<pre>");
        out.println("==================================================================================================");
        out.println("| Сезон           | Матчей     | Мячей      | Незабитые  | Забито в    | Удалений   | Предупре-  |");
        out.println("|                 |            |(с пенальти)| пенальти   | свои ворота |            | ждений     |");
        out.println("==================================================================================================");
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
                out.printf("| %-15s | %-10d | %-10s | %-10d | %-11d | %-10d | %-10d |%n",
                        season.title, season.games, seasonGoals, season.penaltyMissed, season.autogoal, season.redCards, season.yellowCards);
            }
        }
        out.println("--------------------------------------------------------------------------------------------------");
        String allGoals = (penalty > 0)
                ? String.valueOf(goals) + "(" + penalty + ")"
                : String.valueOf(goals);
        out.printf("| %-15s | %-10d | %-10s | %-10d | %-11d | %-10d | %-10d |%n",
                "Итого", games, allGoals, penaltyMissed, autogoal, redCards, yellowCards);
        out.println("==================================================================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private String title = "";
        private int games = 0;
        private int goals = 0;
        private int penalty = 0;
        private int penaltyMissed = 0;
        private int autogoal = 0;
        private int yellowCards = 0;
        private int redCards = 0;

    }

    static private class TournamentStat {

        private int games = 0;
        private int goals = 0;
        private int penalty = 0;
        private int penaltyMissed = 0;
        private int autogoal = 0;
        private int yellowCards = 0;
        private int redCards = 0;
        private HashMap<String, Integer> players = new HashMap<>();

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                TournamentStat stat = new TournamentStat();
                stat.players = new HashMap<>();
                hashStat.put(keyStat, stat);
            }
            return hashStat.get(keyStat);
        }

    }

}
