/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mojgorod.football.xml.aggregate.aggregator;

import java.io.PrintStream;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import ru.mojgorod.football.chart.BarChart;
import ru.mojgorod.football.chart.HorizontalBarChart;
import ru.mojgorod.football.xml.aggregate.SeasonParameters;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.Utils;

/**
 *
 * @author sova
 */
public class MonthsAggregator implements Aggregator {

    private final HashMap<Integer, TournamentStat> months = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        Integer date = xmlReport.getDateInt();
        int day = date % 100;
        int yearAndMonth = (date - day) / 100;
        Integer attendanceInteger = xmlReport.getStadiumAttendanceInt();
        int attendanceValue = (attendanceInteger == null) ? 0 : attendanceInteger;
        TournamentStat stat = TournamentStat.get(months, yearAndMonth);
        stat.attendance += attendanceValue;
        stat.games++;
        stat.month = yearAndMonth;

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            if (eventType.isAnyGoal()) {
                stat.goals++;
            }
            if (eventType.isRedCard()) {
                stat.redCards++;
            } else if (eventType.isRedAndYellowCard()) {
                stat.redAndYellowCards++;
            } else if (eventType.isYellowCard()) {
                stat.yellowCards++;
            } else if (eventType.isPenaltyGoal() || eventType.isPenaltyMissed()) {
                stat.penalties++;
            }
        }
    }

    @Override
    public void print(final SeasonParameters parameters) {
        PrintStream out = parameters.getOutput();
        TreeMap<Integer, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(months));
        sortedMap.putAll(months);
        out.println("<h2 id='MonthsAggregator'>Статистика за месяц</h2>");
        out.println("<pre>");
        out.println("============================================================================================================");
        out.println("| Месяц    | Матчей |    Мячей    |  Удалений   |  Два преду- |  Предупре-  |  Пенальти   |   Зрителей     |");
        out.println("|          |        | (в среднем) | (в среднем) |  преждения  |  ждений     | (в среднем) |  (в среднем)   |");
        out.println("|          |        |             |             | (в среднем) | (в среднем) |             |                |");
        out.println("============================================================================================================");
        for (Integer s : sortedMap.keySet()) {
            TournamentStat stat = months.get(s);
            float goalsAverage = (stat.games == 0) ? 0.0f : (float)stat.goals / stat.games;
            int attendanceAverage = (stat.games == 0) ? 0 : stat.attendance / stat.games;
            float redCardsAverage = (stat.games == 0) ? 0 : (float)stat.redCards / stat.games;
            float redAndYellowCardsAverage = (stat.games == 0) ? 0 : (float)stat.redAndYellowCards / stat.games;
            float yellowCardsAverage = (stat.games == 0) ? 0 : (float)stat.yellowCards / stat.games;
            float penaltiesAverage = (stat.games == 0) ? 0 : (float)stat.penalties / stat.games;
            out.printf("| %-8s | %-6d | %4d(%-5.2f) | %4d(%-5.2f) | %4d(%-5.2f) | %4d(%-5.2f) | %4d(%-5.2f) | %6d(%-6d) |%n",
                    Utils.getMonthName(stat.month % 100), stat.games, stat.goals, goalsAverage,
                    stat.redCards, redCardsAverage, stat.redAndYellowCards, redAndYellowCardsAverage,
                    stat.yellowCards, yellowCardsAverage, stat.penalties, penaltiesAverage,
                    stat.attendance, attendanceAverage);
        }
        out.println("============================================================================================================");
        out.println("</pre>");
        out.println("<img src='image/stat_months_v" + parameters.getSeason().getId() + ".png'><br>");
    }

    static private class TournamentStat {

        private Integer month = 0;
        private int attendance = 0;
        private int games = 0;
        private int goals = 0;
        private int redCards = 0;
        private int redAndYellowCards = 0;
        private int yellowCards = 0;
        private int penalties = 0;        

        public static TournamentStat get(final HashMap<Integer, TournamentStat> hashStat, final Integer keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

    static private class StatComparator implements Comparator<Integer> {

        Collator collator = Collator.getInstance(new Locale("ru", "RU"));
        private HashMap<Integer, TournamentStat> map;

        public StatComparator(final HashMap<Integer, TournamentStat> map) {
            this.map = map;
        }

        @Override
        public int compare(Integer key1, Integer key2) {
            String values1 = String.format("%4s", key1);
            String values2 = String.format("%4s", key2);
            return values1.compareTo(values2);
        }
        
    }

    @Override
    public void drawCharts(final SeasonParameters parameters) {
        TreeMap<Integer, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(months));
        sortedMap.putAll(months);
        int items = months.size();
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
        chart.setTitle("Средняя посещаемость за месяц (" + title + ")");
        String outputFolder = parameters.getConfigFile().getOutputFolder();
        chart.setOutputFile(outputFolder + "/image/stat_months_v" + id + ".png");
        for (Integer s : sortedMap.keySet()) {
            TournamentStat stat = months.get(s);
            chart.addPoint(Utils.getMonthName(stat.month % 100), stat.attendance / stat.games);
        }
        chart.draw();
    }

}
