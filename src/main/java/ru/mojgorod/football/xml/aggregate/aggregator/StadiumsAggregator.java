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
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class StadiumsAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> stadiums = new HashMap<>();
    private int games = 0;
    private int attendance = 0;
    private int minAttendance = Integer.MAX_VALUE;
    private int maxAttendance = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        String stadiumKey = xmlReport.getStadiumKey();

        int attendanceValue;
        if (xmlReport.isBehindClosedDoors()) {
            attendanceValue = 0;
        } else {
            Integer attendanceInteger = xmlReport.getStadiumAttendanceInt();
            attendanceValue = (attendanceInteger == null) ? 0 : attendanceInteger;
        }

        TournamentStat stat = TournamentStat.get(stadiums, stadiumKey);
        stat.attendance += attendanceValue;
        if (attendanceValue < stat.minAttendance) {
            stat.minAttendance = attendanceValue;
        }
        if (attendanceValue > stat.maxAttendance) {
            stat.maxAttendance = attendanceValue;
        }
        stat.games++;
        stat.city = xmlReport.getStadiumCity();
        stat.name = xmlReport.getStadium();

        games++;
        attendance += attendanceValue;
        if (attendanceValue < minAttendance) {
            minAttendance = attendanceValue;
        }
        if (attendanceValue > maxAttendance) {
            maxAttendance = attendanceValue;
        }
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(stadiums));
        sortedMap.putAll(stadiums);
        out.println("<h2 id='StadiumsAggregator'>Средняя посещаемость по стадиону</h2>");
        out.println("<pre>");
        out.println("===========================================================================================================================");
        out.println("| Стадион                                      | Город                | Матчей     |               Зрителей               |");
        out.println("|                                              |                      |            |------------|------------|------------|");
        out.println("|                                              |                      |            | В среднем  | Минимум    | Максимум   |");
        out.println("===========================================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = stadiums.get(s);
            out.printf("| %-44s | %-20s | %-10d | %-10d | %-10d | %-10d |%n",
                    fixStadiumName(stat.name), stat.city, stat.games, stat.attendance / stat.games, stat.minAttendance, stat.maxAttendance);
        }
        int average = (games > 0) ? attendance / games : 0;
        if (minAttendance == Integer.MAX_VALUE) {
            minAttendance = 0;
        }
        out.println("---------------------------------------------------------------------------------------------------------------------------");
        out.format("| Итого                                        |                      | %-10d | %-10d | %-10d | %-10d |%n",
                games, average, minAttendance, maxAttendance);
        out.println("===========================================================================================================================");
        out.println("</pre>");
        out.println("<img src='image/stat_attendance_" + getFileNamePrefix() + getSeason().getId() + ".png' alt='График'><br>");
    }

    private String fixStadiumName(String name) {
         name = name.replaceFirst("^Стадион Стадион ", "Стадион ");
         name = name.replaceFirst("^Стадион &quot;Стадион (.+)&quot;$", "Стадион $1");
         name = name.replaceFirst("^Стадион \"Стадион (.+)\"$", "Стадион $1");
         name = name.replaceFirst("^Стадион Футбольный манеж (.+)$", "Футбольный манеж $1");
         name = name.replaceFirst("^Стадион &quot;Арена (.+)&quot;$", "Арена $1");
         name = name.replaceFirst("^Стадион &quot;(.+) Арена&quot;$", "$1 Арена");
         name = name.replaceFirst("^Стадион \"Арена (.+)\"$", "Арена $1");
         name = name.replaceFirst("^Стадион \"(.+) Арена\"$", "$1 Арена");
         name = name.replaceFirst("^Стадион Центральный стадион (.+)$", "Центральный стадион $1");
         return name;
    }

    static private class TournamentStat {

        private int attendance = 0;
        private int minAttendance = Integer.MAX_VALUE;
        private int maxAttendance = 0;
        private int games = 0;
        private String name = "";
        private String city = "";

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
            int value1 = stat1.attendance / stat1.games;
            int value2 = stat2.attendance / stat2.games;
            if (value1 < value2) {
                return 1;
            } else if (value1 > value2) {
                return -1;
            }
            return collator.compare(stat1.name, stat2.name);
        }
        
    }

    @Override
    public void drawCharts() {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(stadiums));
        sortedMap.putAll(stadiums);
        int items = stadiums.size();
        int height = 400;
        if (items > 16) {
            height += 16 * (items - 16);
        }

        String title = getSeason().getTitle();
        String id = getSeason().getId();

        BarChart chart = new HorizontalBarChart(800, height);
        chart.setCopyright("(c) football.mojgorod.ru");
        chart.setFontSize(14);
        chart.setFontSizeTitle(20);
        chart.setTitle("Средняя посещаемость по стадиону (" + title + ")");
        String outputFolder = getConfigFile().getOutputFolder();
        chart.setOutputFile(outputFolder + "/image/stat_attendance_" + getFileNamePrefix() + id + ".png");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = stadiums.get(s);
            chart.addPoint(fixStadiumName(stat.name), stat.attendance / stat.games);
        }
        chart.draw();
    }

}
