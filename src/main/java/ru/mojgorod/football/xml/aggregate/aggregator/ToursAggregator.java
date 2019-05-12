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
import java.util.Locale;
import java.util.TreeMap;
import ru.mojgorod.football.chart.BarChart;
import ru.mojgorod.football.chart.HorizontalBarChart;
import ru.mojgorod.football.xml.aggregate.SeasonParameters;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class ToursAggregator implements Aggregator {
    private final HashMap<String, TournamentStat> tours = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String round = xmlReport.getRound();
        Integer attendanceInteger = xmlReport.getStadiumAttendanceInt();
        int attendanceValue = (attendanceInteger == null) ? 0 : attendanceInteger;
        TournamentStat stat = TournamentStat.get(tours, round);
        stat.attendance += attendanceValue;
        stat.games++;
        stat.goals += xmlReport.getGoalsInt1() + xmlReport.getGoalsInt2();
        stat.tour = round;
    }

    @Override
    public void print(final SeasonParameters parameters) {
        PrintStream out = parameters.getOutput();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(tours));
        sortedMap.putAll(tours);
        out.println("<h2 id='ToursAggregator'>Статистика за тур</h2>");
        out.println("<pre>");
        out.println("===============================================");
        out.println("| Тур | Матчей |    Мячей    |   Зрителей     |");
        out.println("|     |        | (в среднем) |  (в среднем)   |");
        out.println("===============================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = tours.get(s);
            float goalsAverage = (stat.games == 0) ? 0.0f : (float)stat.goals / stat.games;
            int attendanceAverage = (stat.games == 0) ? 0 : stat.attendance / stat.games;
            out.printf("| %-3s | %-6d | %4d(%-5.2f) | %6d(%-6d) |%n",
                    stat.tour, stat.games, stat.goals, goalsAverage, stat.attendance, attendanceAverage);
        }
        out.println("===============================================");
        out.println("</pre>");
        out.println("<img src='image/stat_tours_v" + parameters.getSeason().getId() + ".png'><br>");
    }

    static private class TournamentStat {

        private String tour = "";
        private int attendance = 0;
        private int games = 0;
        private int goals = 0;

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
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
            String values1 = String.format("%4s", key1);
            String values2 = String.format("%4s", key2);
            return values1.compareTo(values2);
        }
        
    }

    @Override
    public void drawCharts(final SeasonParameters parameters) {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(tours));
        sortedMap.putAll(tours);
        int items = tours.size();
        int height = 400;
        if (items > 16) {
            height += 16 * (items - 16);
        }

        String title = parameters.getSeason().getTitle();
        String id = parameters.getSeason().getId();

        BarChart chart = new HorizontalBarChart(800, height);
        chart.setCopyright("(c) football.mojgorod.ru");
        chart.setFontSize(14);
        chart.setFontSizeTitle(20);
        chart.setTitle("Средняя посещаемость за тур (" + title + ")");
        String outputFolder = parameters.getConfigFile().getOutputFolder();
        chart.setOutputFile(outputFolder + "/image/stat_tours_v" + id + ".png");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = tours.get(s);
            chart.addPoint(stat.tour, stat.attendance / stat.games);
        }
        chart.draw();
    }

}
