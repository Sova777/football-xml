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
import ru.mojgorod.football.xml.library.Age;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlPlayer;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.Utils;

/**
 *
 * @author sova
 */
public class AgeAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> teams = new HashMap<>();
    private int maxDate = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();
        Integer date = xmlReport.getDateInt();
        if (maxDate < date) {
            maxDate = date;
        }

        TournamentStat stat1 = TournamentStat.get(teams, teamKey1);
        stat1.team = team1;
        stat1.games++;

        TournamentStat stat2 = TournamentStat.get(teams, teamKey2);
        stat2.team = team2;
        stat2.games++;

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
                case SUBSTITUTION:
                    statCurrent.addPlayer(event.getPlayerKey2());
                    break;
            }
        }
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        out.println("<h2 id='AgeAggregator'>Средний возраст игроков в команде</h2>");
        out.println("<pre>");
        out.println("=================================================================================================================================");
        out.println("| Команда              | Играл хоть 1 раз                 | Не меньше чем 30% матчей         | Не меньше чем 60% матчей         |");
        out.println("|                      |-------------|--------------------|-------------|--------------------|-------------|--------------------|");
        out.println("|                      | Игроков     | Возраст            | Игроков     | Возраст            | Игроков     | Возраст            |");
        out.println("=================================================================================================================================");
        if (isPlayerInfo()) {
            for (String s : sortedMap.keySet()) {
                TournamentStat stat = teams.get(s);
                double age = 0;
                double age2 = 0;
                double age3 = 0;
                int agePlayers = 0;
                int agePlayers2 = 0;
                int agePlayers3 = 0;
                boolean isAgeValid1 = true;
                boolean isAgeValid2 = true;
                boolean isAgeValid3 = true;

                double column2 = 0.3 * stat.games;
                double column3 = 0.6 * stat.games;
                int players2 = 0;
                int players3 = 0;
                for (String key : stat.players.keySet()) {
                    Age ageValue = getPlayerInfo(key).getAge(maxDate);
                    Double playerAge = (ageValue == null) ? null : ageValue.getDoubleValue();
                    if (playerAge == null) {
                        isAgeValid1 = false;
                    } else {
                        age += playerAge;
                        agePlayers++;
                    }
                    int playerGames = stat.players.get(key);
                    if (playerGames >= column2) {
                        players2++;
                        if (playerAge == null) {
                            isAgeValid2 = false;
                        } else {
                            age2 += playerAge;
                            agePlayers2++;
                        }
                    }
                    if (playerGames >= column3) {
                        players3++;
                        if (playerAge == null) {
                            isAgeValid3 = false;
                        } else {
                            age3 += playerAge;
                            agePlayers3++;
                        }
                    }
                }
                char valid1 = isAgeValid1 ? ' ' : '*';
                char valid2 = isAgeValid2 ? ' ' : '*';
                char valid3 = isAgeValid3 ? ' ' : '*';
                out.printf(Locale.US, "| %-20s | %-11d | %-16s %c | %-11d | %-16s %c | %-11d | %-16s %c |%n",
                        stat.team, stat.players.size(), Utils.getLocalizedMessage(age / agePlayers), valid1,
                        players2, Utils.getLocalizedMessage(age2 / agePlayers2), valid2,
                        players3, Utils.getLocalizedMessage(age3 / agePlayers3), valid3);
            }
        }
        out.println("=================================================================================================================================");
        out.println( "* - означает, что на данный момент нет данных по возрасту одного или более игроков");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private int games = 0;
        private String team = "";
        private HashMap<String, Integer> players = new HashMap<>();

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
            String value1 = stat1.team;
            String value2 = stat2.team;
            return collator.compare(value1, value2);
        }
        
    }

}
