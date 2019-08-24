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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlGooalkeeperStat;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.UtilsGoalkeepers;

/**
 *
 * @author sova
 */
public class GoalkeeperTeamsByPlayerAggregator extends Aggregator {


    private static String playerId = getParameterValue("playerId");
    private static final HashMap<String, TournamentStat> teams = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        final HashMap<String, FootballXmlGooalkeeperStat> matchKeepers = UtilsGoalkeepers.getMatchStat(xmlReport);
        for (Entry<String, FootballXmlGooalkeeperStat> entry : matchKeepers.entrySet()) {
            FootballXmlGooalkeeperStat mathStat = entry.getValue();
            if (playerId.equals(mathStat.getKey())) {
                TournamentStat stat = TournamentStat.get(teams, mathStat.getTeamKey());
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

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        out.println("<h2 id='GoalkeeperTeamsByPlayerAggregator'>Статистика по командам</h2>");
        out.println("<pre>");
        out.println("==============================================================================================================");
        out.println("| Команда         | Игр        | Пропущено  | Сухих      |        Пенальти         | Удалений   | Предупре-  |");
        out.println("|                 |            |            | матчей     |------------|------------| с поля     | ждений     |");
        out.println("|                 |            |            |            | Не забито  | Пропущено  |            |            |");
        out.println("==============================================================================================================");
        for (Map.Entry<String, TournamentStat> entry : sortedMap.entrySet()) {
            TournamentStat stat = entry.getValue();
            out.printf("| %-15s | %-10s | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d |%n",
                    stat.team, stat.games, stat.goals, stat.dry, stat.penaltyMissed,
                    stat.penaltySuccess, stat.redCards, stat.yellowCards);
        }
        out.println("==============================================================================================================");
        out.println( "</pre>");
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
            return collator.compare(stat1.team, stat2.team);
        }
        
    }

}
