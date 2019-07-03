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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class PointsBySeasonAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> teamsMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String teamKey1 = xmlReport.getTeamKey1();
        String teamKey2 = xmlReport.getTeamKey2();
        int goals1 = xmlReport.getGoalsInt1();
        int goals2 = xmlReport.getGoalsInt2();
        TournamentStat stat1 = TournamentStat.get(teamsMap, teamKey1);
        TournamentStat stat2 = TournamentStat.get(teamsMap, teamKey2);
        if (goals1 == goals2) {
            stat1.points++;
            stat2.points++;
        } else if (goals1 > goals2) {
            stat1.points += 3;
        } else {//  if (goals1 < goals2) {
            stat2.points += 3;
        }
    }

    @Override
    public void afterSeason() {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teamsMap));
        sortedMap.putAll(teamsMap);
        SeasonStat stat = new SeasonStat();
        String[] keys = sortedMap.keySet().toArray(new String[0]);
        int last = sortedMap.size();
        if (last > 7) {
            stat.pointsLast1 = sortedMap.get(keys[last - 1]).points;
        }
        if (last > 6) {
            stat.pointsLast2 = sortedMap.get(keys[last - 2]).points;
        }
        if (last > 5) {
            stat.pointsLast3 = sortedMap.get(keys[last - 3]).points;
        }
        if (last > 4) {
            stat.points5 = sortedMap.get(keys[4]).points;
        }
        if (last > 3) {
            stat.points4 = sortedMap.get(keys[3]).points;
        }
        if (last > 2) {
            stat.points3 = sortedMap.get(keys[2]).points;
        }
        if (last > 1) {
            stat.points2 = sortedMap.get(keys[1]).points;
        }
        if (last > 0) {
            stat.points1 = sortedMap.get(keys[0]).points;
        }
        stat.title = getSeason().getTitle();
        teamsMap.clear();
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='PointsBySeasonAggregator'>Занятое место и количество набранных очков</h2>");
        out.println("<p class=\"text\">Без учёта снятых очков. Учитываются только результаты матчей.</p>");
        out.println("<pre>");
        out.println("=======================================================================================");
        out.println("| Сезон           |                              Занятое место                        |");
        out.println("|                 |-------|-------|-------|-------|-------|-------|-------|-----------|");
        out.println("|                 | 1-ое  | 2-ое  | 3-ье  | 4-ое  | 5-ое  | 3-е с | 2-е с | Последнее |");
        out.println("|                 |       |       |       |       |       | конца | конца |           |");
        out.println("=======================================================================================");
        for (SeasonStat stat : seasons) {
            out.printf("| %-15s | %-5d | %-5d | %-5d | %-5d | %-5d | %-5d | %-5d | %-9d |%n",
                    stat.title, stat.points1, stat.points2, stat.points3, stat.points4, stat.points5,
                    stat.pointsLast3, stat.pointsLast2, stat.pointsLast1);
        }
        out.println("=======================================================================================");
        out.println("</pre>");
    }

    static private class SeasonStat {

        private int points1 = 0;
        private int points2 = 0;
        private int points3 = 0;
        private int points4 = 0;
        private int points5 = 0;
        private int pointsLast3 = 0;
        private int pointsLast2 = 0;
        private int pointsLast1 = 0;
        private String title = "";

    }

    static private class TournamentStat {

        private int points = 0;

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
            int value1 = stat1.points;
            int value2 = stat2.points;
            if (value1 < value2) {
                return 1;
            } else if (value1 > value2) {
                return -1;
            }
            return collator.compare(key1, key2);
        }

    }

}
