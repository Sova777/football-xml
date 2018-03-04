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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.config.Config;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlPlayer;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class TeamsAggregator implements Aggregator {

    private final HashMap<String, TournamentStat> teams = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();

        TournamentStat stat1 = TournamentStat.get(teams, teamKey1);
        stat1.team = team1;

        TournamentStat stat2 = TournamentStat.get(teams, teamKey2);
        stat2.team = team2;

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            stat1.addPlayer(player.getKey());
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            stat2.addPlayer(player.getKey());
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            String team = event.getTeam();
            TournamentStat statCurrent;
            TournamentStat statOpponent;
            if (team.equals(team1)) {
                statCurrent = stat1;
                statOpponent = stat2;
            } else if (team.equals(team2)) {
                statCurrent = stat2;
                statOpponent = stat1;
            } else {
                Logger.getLogger(GoalsForAggregetor.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                continue;
            }

            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    statCurrent.addPlayer(event.getPlayerKey1());
                    statCurrent.addPlayer(event.getPlayerKey2());
                    break;
                case RED_CARD:
                    statCurrent.redCards++;
                    break;
                case YELLOW_CARD:
                    statCurrent.yellowCards++;
                    break;
                case RED_AND_YELLOW_CARD:
                    statCurrent.redAndYellowCards++;
                    break;
                case PENALTY_GOAL:
                    statCurrent.penaltySuccess1++;
                    statOpponent.penaltySuccess2++;
                    break;
                case PENALTY_MISSED:
                    statCurrent.penaltyMissed1++;
                    statOpponent.penaltyMissed2++;
                    break;
                case AUTOGOAL:
                    statCurrent.autoGoals++;
                    break;
            }
        }
    }

    @Override
    public void print(final Config config, final PrintStream out, final String title) {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        out.println("<h2>Командная статистика</h2>");
        out.println("<pre>");
        out.println("===================================================================================================================");
        out.println("| Команда              | Игроков    | Удалений   | Два преду- | Предупре-  |   Пенальти (забито)     | Забито в   |");
        out.println("|                      |            |            | преждения  | ждений     |------------|------------| свои       |");
        out.println("|                      |            |            |            |            | Пробили    | Соперник   | ворота     |");
        out.println("===================================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            String penaltiesFor = String.format("%d(%d)", stat.penaltySuccess1 + stat.penaltyMissed1, stat.penaltySuccess1);
            String penaltiesAgainst = String.format("%d(%d)", stat.penaltySuccess2 + stat.penaltyMissed2, stat.penaltySuccess2);
            out.printf("| %-20s | %-10d | %-10d | %-10d | %-10d | %-10s | %-10s | %-10d |%n",
                    stat.team, stat.players.size(), stat.redCards, stat.redAndYellowCards, stat.yellowCards, penaltiesFor, penaltiesAgainst, stat.autoGoals);
        }
        out.println("===================================================================================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private int redCards = 0;
        private int redAndYellowCards = 0;
        private int yellowCards = 0;
        private int autoGoals = 0;
        private int penaltySuccess1 = 0;
        private int penaltySuccess2 = 0;
        private int penaltyMissed1 = 0;
        private int penaltyMissed2 = 0;
        private Set<String> players = new HashSet<>();
        private String team = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

        public void addPlayer(final String keyPlayer) {
            if (!players.contains(keyPlayer)) {
                players.add(keyPlayer);
            }
        }

    }

    static private class StatComparator implements Comparator<String> {

        Collator collator = Collator.getInstance(new Locale("ru", "RU"));
        private HashMap<String, TournamentStat> map;

        public StatComparator(final HashMap<String, TournamentStat> map) {
            this.map = map;
        }

        @Override
        public int compare(String key1, String key2) {
            TournamentStat stat1 = map.get(key1);
            TournamentStat stat2 = map.get(key2);
            String value1 = stat1.team;
            String value2 = stat2.team;
            return collator.compare(value1, value2);
        }

    }
    
}
