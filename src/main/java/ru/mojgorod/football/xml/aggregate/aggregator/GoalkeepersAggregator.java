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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.aggregate.SeasonManager;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class GoalkeepersAggregator implements Aggregator {


    private final HashMap<String, TournamentStat> keepers = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        final HashMap<String, TournamentStat> matchKeepers = new HashMap<>();
        String team1 = xmlReport.getTeam1();
        String teamId1 = xmlReport.getTeamId1();
        String team2 = xmlReport.getTeam2();
        String teamId2 = xmlReport.getTeamId2();

        String goalkeeperKey1 = xmlReport.getGoalkeeperKey1();
        String goalkeeperKey2 = xmlReport.getGoalkeeperKey2();

        TournamentStat statKeeper1 = getMathStat(matchKeepers, xmlReport.getGoalkeeperKey1());
        setMatchStat(statKeeper1, xmlReport.getGoalkeeper1(),
                xmlReport.getTeam1(), xmlReport.getGoalkeeperKey1(), xmlReport.getGoalkeeperKeyWithTeam1());
        statKeeper1.dry = 1;

        TournamentStat statKeeper2 = getMathStat(matchKeepers, xmlReport.getGoalkeeperKey2());
        setMatchStat(statKeeper2, xmlReport.getGoalkeeper2(),
                xmlReport.getTeam2(), xmlReport.getGoalkeeperKey2(), xmlReport.getGoalkeeperKeyWithTeam2());
        statKeeper2.dry = 1;

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            String team = event.getTeam();
            TournamentStat statCurrent;
            TournamentStat statOpponent;
            if (team.equals(team1)) {
                statCurrent = statKeeper1;
                statOpponent = statKeeper2;
            } else if (team.equals(team2)) {
                statCurrent = statKeeper2;
                statOpponent = statKeeper1;
            } else {
                Logger.getLogger(GoalsForAggregetor.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                continue;
            }

            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    if (goalkeeperKey1.equals(event.getPlayerKey1())) {
                        statCurrent.dry = 0;
                        statKeeper1 = getMathStat(matchKeepers, event.getPlayerKey2());
                        setMatchStat(statKeeper1, event.getPlayer2(),
                                team1, event.getPlayerId2(), event.getPlayerWithTeamKey2(teamId1));
                        goalkeeperKey1 = event.getPlayerKey2();
                    }
                    if (goalkeeperKey2.equals(event.getPlayerKey1())) {
                        statCurrent.dry = 0;
                        statKeeper2 = getMathStat(matchKeepers, event.getPlayerKey2());
                        setMatchStat(statKeeper2, event.getPlayer2(),
                                team2, event.getPlayerId2(), event.getPlayerWithTeamKey2(teamId2));
                        goalkeeperKey2 = event.getPlayerKey2();
                    }
                    break;
                case SUBSTITUTION_GOALKEEPER:
                    statCurrent.dry = 0;
                    if (statKeeper1.equals(statCurrent)) {
                        statKeeper1 = getMathStat(matchKeepers, event.getPlayerKey1());
                        setMatchStat(statKeeper1, event.getPlayer1(),
                                team1, event.getPlayerId(), event.getPlayerWithTeamKey1(teamId1));
                        goalkeeperKey1 = event.getPlayerKey1();
                    } else {
                        statKeeper2 = getMathStat(matchKeepers, event.getPlayerKey1());
                        setMatchStat(statKeeper2, event.getPlayer1(),
                                team2, event.getPlayerId(), event.getPlayerWithTeamKey1(teamId2));
                        goalkeeperKey2 = event.getPlayerKey1();
                    }
                    break;
                case RED_CARD:
                case RED_AND_YELLOW_CARD:
                    if (goalkeeperKey1.equals(event.getPlayerKey1())) {
                        statKeeper1.redCards++;
                        statKeeper1.dry = 0;
                        goalkeeperKey1 = "???";
                    }
                    if (goalkeeperKey2.equals(event.getPlayerKey1())) {
                        statKeeper2.redCards++;
                        statKeeper2.dry = 0;
                        goalkeeperKey2 = "???";
                    }
                    break;
                case AUTOGOAL:
                    statCurrent.goals++;
                    statCurrent.dry = 0;
                    break;
                case GOAL:
                    statOpponent.goals++;
                    statOpponent.dry = 0;
                    break;
                case PENALTY_GOAL:
                    statOpponent.penaltySuccess++;
                    statOpponent.goals++;
                    statOpponent.dry = 0;
                    break;
                case PENALTY_MISSED:
                    statOpponent.penaltyMissed++;
                    break;
            }
        }
        for (String s : matchKeepers.keySet()) {
            TournamentStat mathStat = matchKeepers.get(s);
            TournamentStat stat = TournamentStat.get(keepers, mathStat.longkey);
            stat.dry += mathStat.dry;
            stat.games += mathStat.games;
            stat.goals += mathStat.goals;
            stat.key = mathStat.key;
            stat.longkey = mathStat.longkey;
            stat.name = mathStat.name;
            stat.penaltyMissed += mathStat.penaltyMissed;
            stat.penaltySuccess += mathStat.penaltySuccess;
            stat.redCards += mathStat.redCards;
            stat.team = mathStat.team;
        }
    }

    @Override
    public void print(final SeasonManager.Config config, final PrintStream out, final String title) {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(keepers));
        sortedMap.putAll(keepers);
        out.println("<h2>Статистика вратарей</h2>");
        out.println("<pre>");
        out.println("===============================================================================================================================");
        out.println("| Вратарь                   | Команда           | Игр        | Пропущено  | Сухих      |        Пенальти         | Удалений   |");
        out.println("|                           |                   |            |            | матчей     |------------|------------| с поля     |");
        out.println("|                           |                   |            |            |            | Не забито  | Пропущено  |            |");
        out.println("===============================================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = keepers.get(s);
            out.printf("| %-25s | %-17s | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d |%n",
                    config.getPlayerInfo(stat.key).getName()/**stat.name*/, stat.team, stat.games, stat.goals, stat.dry, stat.penaltyMissed, stat.penaltySuccess, stat.redCards);
        }
        out.println("===============================================================================================================================");
        out.println( "</pre>");
    }

    private static TournamentStat getMathStat(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
        if (!hashStat.containsKey(keyStat)) {
            hashStat.put(keyStat, new TournamentStat());
        }
        return hashStat.get(keyStat);
    }

    private void setMatchStat(final TournamentStat statKeeper,
            final String name, final String team, final String key, final String longkey){
        statKeeper.name = name;
        statKeeper.team = team;
        statKeeper.games = 1;
        statKeeper.key = key;
        statKeeper.longkey = longkey;
    }

    static private class TournamentStat {

        private int games = 0;
        private int goals = 0;
        private int dry = 0;
        private int redCards = 0;
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

        private HashMap<String, TournamentStat> map;

        public StatComparator(final HashMap<String, TournamentStat> map) {
            this.map = map;
        }

        @Override
        public int compare(String key1, String key2) {
            TournamentStat stat1 = map.get(key1);
            TournamentStat stat2 = map.get(key2);
            String team1 = stat1.team;
            String team2 = stat2.team;
            int compare = team1.compareTo(team2);
            if (compare == 0) {
                String value1 = stat1.name;
                String value2 = stat2.name;
                return value1.compareTo(value2);
            }
            return compare;
        }
        
    }

}
