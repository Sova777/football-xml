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
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.Age;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlPlayer;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class MostYoungBySeasonAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> players = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        Integer date = xmlReport.getDateInt();
        String team1 = xmlReport.getTeam1();
        String team2 = xmlReport.getTeam2();

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            TournamentStat stat = TournamentStat.get(players, player.getKey());
            if (date < stat.gameDateMinInt) {
                stat.name = player.getName();
                stat.teamMin = team1;
                stat.gameDateMinInt = date;
            }
            if (date > stat.gameDateMaxInt) {
                stat.name = player.getName();
                stat.teamMax = team1;
                stat.gameDateMaxInt = date;
            }
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            TournamentStat stat = TournamentStat.get(players, player.getKey());
            if (date < stat.gameDateMinInt) {
                stat.name = player.getName();
                stat.teamMin = team2;
                stat.gameDateMinInt = date;
            }
            if (date > stat.gameDateMaxInt) {
                stat.name = player.getName();
                stat.teamMax = team2;
                stat.gameDateMaxInt = date;
            }
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    TournamentStat stat = TournamentStat.get(players, event.getPlayerKey2());
                    if (date < stat.gameDateMinInt) {
                        stat.name = event.getPlayer2();
                        stat.teamMin = event.getTeam();
                        stat.gameDateMinInt = date;
                    }
                    if (date > stat.gameDateMaxInt) {
                        stat.name = event.getPlayer2();
                        stat.teamMax = event.getTeam();
                        stat.gameDateMaxInt = date;
                    }
                    break;
            }
        }
    }

    @Override
    public void afterSeason() {
        String nameMin = "";
        String nameMax = "";
        Age ageMin = Age.AGE_MAX;
        Age ageMax = Age.AGE_ZERO;
        String teamMin = "";
        String teamMax = "";
        for (Map.Entry<String, TournamentStat> entry : players.entrySet()) {
            String key = entry.getKey();
            TournamentStat stat = entry.getValue();
            Age playerAgeMin = getPlayerInfo(entry.getKey()).getAge(stat.gameDateMinInt);
            Age playerAgeMax = getPlayerInfo(entry.getKey()).getAge(stat.gameDateMaxInt);
            stat.name = getPlayerInfo(key).getName();
            if (playerAgeMin != null) {
                if (playerAgeMin.lessThan(ageMin)) {
                    ageMin = playerAgeMin;
                    teamMin = stat.teamMin;
                    nameMin = stat.name;
                }
                if (playerAgeMax.moreThan(ageMax)) {
                    ageMax = playerAgeMax;
                    teamMax = stat.teamMax;
                    nameMax = stat.name;
                }
            }
        }
        if (ageMin.getDoubleValue() >= Integer.MAX_VALUE) {
            ageMin = Age.AGE_ZERO;
        }

        SeasonStat stat = new SeasonStat();
        stat.nameMin = nameMin;
        stat.nameMax = nameMax;
        stat.ageMin = ageMin;
        stat.ageMax = ageMax;
        stat.teamMin = teamMin;
        stat.teamMax = teamMax;
        stat.title = getSeason().getTitle();
        players.clear();
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='MostYoungBySeasonAggregator'>Наиболее молодой и наиболее возрастной игрок сезона</h2>");
        out.println("<pre>");
        out.println("=======================================================================================================================================================");
        out.println("| Сезон           |                       Наиболее молодой игрок                    |                      Наиболее возрастной игрок                  |");
        out.println("|                 |----------------------|-----------------------|------------------|----------------------|-----------------------|------------------|");
        out.println("|                 | Игрок                | Команда               | Возраст          | Игрок                | Команда               | Возраст          |");
        out.println("=======================================================================================================================================================");
        for (SeasonStat stat : seasons) {
            out.printf(Locale.US, "| %-15s | %-20s | %-21s | %-16s | %-20s | %-21s | %-16s |%n",
                    stat.title, stat.nameMin, stat.teamMin, stat.ageMin.getStringValue(),
                    stat.nameMax, stat.teamMax, stat.ageMax.getStringValue());
        }
        out.println("=======================================================================================================================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private String nameMin = "";
        private String nameMax = "";
        private String teamMin = "";
        private String teamMax = "";
        private Age ageMin = null;
        private Age ageMax = null;
        private String title = "";

    }

    static private class TournamentStat {

        private String name = "";
        private String teamMin = "";
        private String teamMax = "";
        private Integer gameDateMinInt = Integer.MAX_VALUE;
        private Integer gameDateMaxInt = 0;

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

}
