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
import java.util.Map;
import java.util.Map.Entry;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlGooalkeeperStat;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.UtilsGoalkeepers;

/**
 *
 * @author sova
 */
public class GoalkeeperEventsByPlayerAggregator extends Aggregator {


    private static String playerId = getParameterValue("playerId");
    private final HashMap<String, TournamentStat> keepers = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        final HashMap<String, FootballXmlGooalkeeperStat> matchKeepers = UtilsGoalkeepers.getMatchStat(xmlReport);
        for (Entry<String, FootballXmlGooalkeeperStat> entry : matchKeepers.entrySet()) {
            FootballXmlGooalkeeperStat mathStat = entry.getValue();
            if (playerId.equals(mathStat.getKey())) {
                TournamentStat stat = TournamentStat.get(keepers, mathStat.getLongkey());
                stat.dry += mathStat.getDry();
                stat.games += mathStat.getGames();
                stat.goals += mathStat.getGoals();
                stat.key = mathStat.getKey();
                stat.longkey = mathStat.getLongkey();
                stat.name = mathStat.getName();
                stat.penaltyMissed += mathStat.getPenaltyMissed();
                stat.penaltySuccess += mathStat.getPenaltySuccess();
                stat.redCards += mathStat.getRedCards();
                stat.yellowCards += mathStat.getYellowCards();
                stat.team = mathStat.getTeam();
            }
        }
    }

    @Override
    public void afterSeason() {
        for (Map.Entry<String, TournamentStat> entry : keepers.entrySet()) {
            TournamentStat tournamentStat = entry.getValue();
            SeasonStat seasonStat = new SeasonStat();
            seasonStat.team = tournamentStat.team;
            seasonStat.dry = tournamentStat.dry;
            seasonStat.games = tournamentStat.games;
            seasonStat.goals = tournamentStat.goals;
            seasonStat.penaltyMissed = tournamentStat.penaltyMissed;
            seasonStat.penaltySuccess = tournamentStat.penaltySuccess;
            seasonStat.redCards = tournamentStat.redCards;
            seasonStat.yellowCards = tournamentStat.yellowCards;
            seasonStat.title = getSeason().getTitle();
            seasons.add(seasonStat);
        }
        keepers.clear();
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='GoalkeeperEventsByPlayerAggregator'>Статистика за сезон</h2>");
        out.println("<pre>");
        out.println("================================================================================================================================");
        out.println("| Сезон           | Команда         | Игр        | Пропущено  | Сухих      |        Пенальти         | Удалений   | Предупре-  |");
        out.println("|                 |                 |            |            | матчей     |------------|------------| с поля     | ждений     |");
        out.println("|                 |                 |            |            |            | Не забито  | Пропущено  |            |            |");
        out.println("================================================================================================================================");
        int games = 0;
        int goals = 0;
        int dry = 0;
        int penaltyMissed = 0;
        int penaltySuccess = 0;
        int redCards = 0;
        int yellowCards = 0;
        for (SeasonStat season : seasons) {
            games += season.games;
            goals += season.goals;
            dry += season.dry;
            penaltyMissed += season.penaltyMissed;
            penaltySuccess += season.penaltySuccess;
            redCards += season.redCards;
            yellowCards += season.yellowCards;
            out.printf("| %-15s | %-15s | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d |%n",
                    season.title, season.team, season.games, season.goals, season.dry, season.penaltyMissed,
                    season.penaltySuccess, season.redCards, season.yellowCards);
        }
        out.println("--------------------------------------------------------------------------------------------------------------------------------");
        out.printf("| %-15s | %-15s | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d |%n",
                "Итого", "", games, goals, dry, penaltyMissed, penaltySuccess, redCards, yellowCards);
        out.println("================================================================================================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private String title = "";
        private int games = 0;
        private int goals = 0;
        private int dry = 0;
        private int redCards = 0;
        private int yellowCards = 0;
        private int penaltySuccess = 0;
        private int penaltyMissed = 0;
        private String team = "";

    }

    static private class TournamentStat {

        private int games = 0;
        private int goals = 0;
        private int dry = 0;
        private int redCards = 0;
        private int yellowCards = 0;
        private int penaltySuccess = 0;
        private int penaltyMissed = 0;
        private String name = "";
        private String team = "";
        private String key = "";
        private String longkey = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

}
