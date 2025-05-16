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

import java.awt.Color;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import ru.mojgorod.football.chart.BarChart;
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
public class AgeBySeasonAggregator extends Aggregator {

    private HashMap<String, TournamentStat> playersMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();
    private static int maxDate = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        Integer date = xmlReport.getDateInt();
        if (maxDate < date) {
            maxDate = date;
        }

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            TournamentStat stat = TournamentStat.get(playersMap, player.getKey());
            stat.games++;
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            TournamentStat stat = TournamentStat.get(playersMap, player.getKey());
            stat.games++;
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    TournamentStat stat = TournamentStat.get(playersMap, event.getPlayerKey2());
                    stat.games++;
                    break;
            }
        }
    }

    @Override
    public void afterSeason() {
        int players = 0;
        int players2 = 0;
        int players3 = 0;
        double age = 0;
        double age2 = 0;
        double age3 = 0;
        int agePlayers = 0;
        int agePlayers2 = 0;
        int agePlayers3 = 0;
        boolean isAgeValid1 = true;
        boolean isAgeValid2 = true;
        boolean isAgeValid3 = true;
        if (isPlayerInfo()) {
            players += playersMap.size();

            double column2 = 10;
            double column3 = 20;
            for (String key : playersMap.keySet()) {
                TournamentStat stat = playersMap.get(key);
                Age ageValue = getPlayerInfo(key).getAge(maxDate);
                Double playerAge = (ageValue == null) ? null : ageValue.getDoubleValue();
                if (playerAge == null) {
                    isAgeValid1 = false;
                } else {
                    age += playerAge;
                    agePlayers++;
                }
                int playerGames = stat.games;
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
        }

        SeasonStat stat = new SeasonStat();
        stat.players = players;
        stat.players2 = players2;
        stat.players3 = players3;
        stat.isAgeValid1 = isAgeValid1;
        stat.isAgeValid2 = isAgeValid2;
        stat.isAgeValid3 = isAgeValid3;
        stat.age = age;
        stat.age2 = age2;
        stat.age3 = age3;
        stat.agePlayers = agePlayers;
        stat.agePlayers2 = agePlayers2;
        stat.agePlayers3 = agePlayers3;
        stat.title = getSeason().getTitle();
        playersMap.clear();
        maxDate = 0;
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='AgeBySeasonAggregator'>Средний возраст игроков</h2>");
        out.println("<pre>");
        out.println("============================================================================================================================");
        out.println("| Сезон           | Играл хоть 1 раз                 | Играл не менее чем в 10 матчах   | Играл не менее чем в 20 матчах   |");
        out.println("|                 |-------------|--------------------|-------------|--------------------|-------------|--------------------|");
        out.println("|                 | Игроков     | Возраст            | Игроков     | Возраст            | Игроков     | Возраст            |");
        out.println("============================================================================================================================");

        for (SeasonStat stat : seasons) {
            char valid1 = stat.isAgeValid1 ? ' ' : '*';
            char valid2 = stat.isAgeValid2 ? ' ' : '*';
            char valid3 = stat.isAgeValid3 ? ' ' : '*';
            out.printf(Locale.US, "| %-15s | %-11d | %-16s %c | %-11d | %-16s %c | %-11d | %-16s %c |%n",
                    stat.title, stat.players, Utils.getLocalizedMessage(stat.age / stat.agePlayers), valid1,
                    stat.players2, Utils.getLocalizedMessage(stat.age2 / stat.agePlayers2), valid2,
                    stat.players3, Utils.getLocalizedMessage(stat.age3 / stat.agePlayers3), valid3);
        }
        out.println("============================================================================================================================");
        out.println( "* - означает, что на данный момент нет данных по возрасту одного или более игроков");
        out.println( "</pre>");
        out.println("<img src='image/stat_age.png' alt='График'>");
    }

    static private class SeasonStat {

        private int players = 0;
        private int players2 = 0;
        private int players3 = 0;
        private double age = 0;
        private double age2 = 0;
        private double age3 = 0;
        private int agePlayers = 0;
        private int agePlayers2 = 0;
        private int agePlayers3 = 0;
        private boolean isAgeValid1 = true;
        private boolean isAgeValid2 = true;
        private boolean isAgeValid3 = true;
        private String title = "";

    }

    static private class TournamentStat {

        private int games = 0;

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

    public static void drawFinalCharts() {
        BarChart chart = new BarChart(800, 400/*height*/);
        chart.setCopyright("(c) football.mojgorod.ru");
        chart.setFontSize(14);
        chart.setFontSizeTitle(20);
        chart.setTitle("Средний возраст игроков в чемпионате России");
        chart.setMinValue(23);
        chart.setMaxNumbersAfterDot(1);
        String outputFolder = getConfigFile().getOutputFolder();
        chart.setOutputFile(outputFolder + "/image/stat_age.png");
        int counter = 0;
        for (SeasonStat stat : seasons) {
            Color color = (counter % 2 == 0) ? BarChart.COLOR_BLUE : BarChart.COLOR_GREEN;
            chart.addPoint(stat.title, stat.players == 0 ? 0.0 : (stat.age / stat.agePlayers), color);
            counter++;
        }
        chart.draw();
    }

}
