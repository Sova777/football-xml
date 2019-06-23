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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class TopScoresBySeasonAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> playersMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case GOAL:
                case PENALTY_GOAL:
                    TournamentStat stat = TournamentStat.get(playersMap, event.getPlayerKey1());
                    stat.name = event.getPlayer1();
                    stat.addTeam(event.getTeam());
                    stat.id = event.getPlayerKey1();
                    stat.goals++;
                    if (eventType.equals(FootballEventType.PENALTY_GOAL)) {
                        stat.penalty++;
                    }
                    break;
            }
        }
    }

    @Override
    public void afterSeason() {
        int maxGoals = 0;
        StringBuilder players = new StringBuilder("");

        for (TournamentStat pl : playersMap.values()) {
            int goalsValue = pl.goals;
            if (goalsValue >= maxGoals) {
                StringBuilder teams = new StringBuilder("");
                int counter = 0;
                for (String item : pl.teams) {
                    if (counter != 0) {
                        teams.append(", ");
                    }
                    teams.append(item);
                    counter++;
                }
                String playerValue = pl.name + "(" + teams.toString()
                        + ", " + pl.penalty + ")";
                if (isPlayerInfo()) {
                    String fixedName = getPlayerInfo(pl.id).getName();
                    if (fixedName != null) {
                        pl.name = getPlayerInfo(pl.id).getName();
                    }
                }
                if (goalsValue == maxGoals) {
                    players.append(", ").append(playerValue);
                } else {
                    maxGoals = goalsValue;
                    players = new StringBuilder(playerValue);
                }
            }
        }

        SeasonStat stat = new SeasonStat();
        stat.goals = maxGoals;
        stat.players = players.toString();
        stat.title = getSeason().getTitle();
        playersMap.clear();
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='TopScoresBySeasonAggregator'>Список лучших бомбардиров за сезон</h2>");
        out.println("<p class=\"text\">У игроков в скобках указаны команды за которые он забивал и количество мячей забитых с пенальти.</p>");
        out.println("<pre>");
        out.println("====================================================================================================");
        out.println("| Сезон           | Мячей      | Лучшие бомбардиры                                                 |");
        out.println("====================================================================================================");
        for (SeasonStat stat : seasons) {
            out.printf("| %-15s | %-10d | %-65s |%n", stat.title, stat.goals, stat.players);
        }
        out.println("====================================================================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private int goals = 0;
        private String players;
        private String title = "";

    }

    static private class TournamentStat {

        Collator collator = Collator.getInstance(new Locale("ru", "RU"));
        private int goals = 0;
        private int penalty = 0;
        private String name = "";
        private Set<String> teams = new TreeSet<>(collator);
        private String id = "";

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

        public void addTeam(final String team) {
            if (!teams.contains(team)) {
                teams.add(team);
            }
        }

    }

}
