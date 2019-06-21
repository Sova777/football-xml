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
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
public class PlayersAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> players = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String teamId1 = xmlReport.getTeamId1();
        String team2 = xmlReport.getTeam2();
        String teamId2 = xmlReport.getTeamId2();

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            TournamentStat stat = TournamentStat.get(players, player.getNameWithTeamKey(teamId1, team1));
            stat.name = player.getName();
            stat.team = team1;
            stat.id = player.getId();
            stat.games++;
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            TournamentStat stat = TournamentStat.get(players, player.getNameWithTeamKey(teamId2, team2));
            stat.name = player.getName();
            stat.team = team2;
            stat.id = player.getId();
            stat.games++;
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            String team = event.getTeam();
            String teamId;
            if (team.equals(team1)) {
                teamId = teamId1;
            } else if (team.equals(team2)) {
                teamId = teamId2;
            } else {
                Logger.getLogger(GoalsForAggregator.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                continue;
            }
            TournamentStat stat = TournamentStat.get(players, event.getPlayerWithTeamKey1(teamId));
            stat.name = event.getPlayer1();
            stat.team = team;
            stat.id = event.getPlayerId();

            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    TournamentStat stat2 = TournamentStat.get(players, event.getPlayerWithTeamKey2(teamId));
                    stat2.name = event.getPlayer2();
                    stat2.team = team;
                    stat2.id = event.getPlayerId2();
                    stat2.games++;
                    break;
                case RED_CARD:
                    stat.redCards++;
                    break;
                case RED_AND_YELLOW_CARD:
                    stat.redAndYellowCards++;
                    break;
                case YELLOW_CARD:
                    stat.yellowCards++;
                    break;
                case AUTOGOAL:
                    stat.autogoals++;
                    break;
                case GOAL:
                    stat.goals++;
                    break;
                case PENALTY_GOAL:
                    stat.penaltySuccess++;
                    stat.goals++;
                    break;
                case PENALTY_MISSED:
                    stat.penaltyMissed++;
                    break;
            }
        }
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        if (isPlayerInfo()) {
            for (TournamentStat pl : players.values()) {
                String name = getPlayerInfo(pl.id).getName();
                if (name != null) {
                    pl.name = name;
                }
            }
        }
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(players));
        sortedMap.putAll(players);
        out.println("<h2 id='PlayersAggregator'>Статистика игроков</h2>");
        out.println("<p class=\"text\">Статистика по игрокам не всегда верна, но она очень важна. Мне будет удобней видеть и править ошибки если она будет видна на этой странице.</p>");
        out.println("<pre>");
        String previous = null;
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = players.get(s);
            if (previous == null || !previous.equals(stat.team)) {
                out.println("===============================================================================================================================================");
                out.println("| Игрок                     | Команда           | Игр        | Голов        | Незабитые  | Удалений   | Два преду- | Предупре-  | Голы в свои |");
                out.println("|                           |                   |            | (с пенальти) | пенальти   |            | преждения  | ждений     | ворота      |");
                out.println("===============================================================================================================================================");
                previous = stat.team;
            }
            String goalsString = (stat.penaltySuccess == 0)
                    ? String.valueOf(stat.goals)
                    : String.format("%s(%s)", stat.goals, stat.penaltySuccess);
            out.printf("| %-25s | %-17s | %-10d | %-12s | %-10d | %-10d | %-10d | %-10d | %-11d |%n",
                    stat.name, stat.team, stat.games, goalsString, stat.penaltyMissed, stat.redCards, stat.redAndYellowCards, stat.yellowCards, stat.autogoals);
        }
        out.println("===============================================================================================================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private int games = 0;
        private int goals = 0;
        private int penaltySuccess = 0;
        private int penaltyMissed = 0;
        private int autogoals = 0;
        private int redCards = 0;
        private int redAndYellowCards = 0;
        private int yellowCards = 0;
        private String name = "";
        private String team = "";
        private String id = "";

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
            int compare;
            TournamentStat stat1 = map.get(key1);
            TournamentStat stat2 = map.get(key2);

            String team1 = stat1.team.replaceAll(" ", "_");
            String team2 = stat2.team.replaceAll(" ", "_");
            compare = collator.compare(team1, team2);
            if (compare != 0) {
                return compare;
            }

            String value1 = stat1.name.replaceAll(" ", "_");
            String value2 = stat2.name.replaceAll(" ", "_");
            compare = collator.compare(value1, value2);
            if (compare != 0) {
                return compare;
            }

            return collator.compare(key1, key2);
        }
        
    }

}
