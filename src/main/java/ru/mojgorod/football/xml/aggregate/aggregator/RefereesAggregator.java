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
import ru.mojgorod.football.xml.config.Config;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class RefereesAggregator implements Aggregator {

    private final HashMap<String, TournamentStat> referees = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String referee = xmlReport.getReferee();
        String refereeKey = xmlReport.getRefereeKey();

        TournamentStat stat = TournamentStat.get(referees, refereeKey);
        stat.name = referee;
        stat.city = xmlReport.getRefereeCity();
        stat.games++;

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case RED_CARD:
                    stat.redCards++;
                    break;
                case YELLOW_CARD:
                    stat.yellowCards++;
                    break;
                case RED_AND_YELLOW_CARD:
                    stat.redAndYellowCards++;
                    break;
                case PENALTY_GOAL:
                case PENALTY_MISSED:
                    stat.penalties++;
                    break;
            }
        }
    }

    @Override
    public void print(final Config config, final PrintStream out, final String title) {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(referees));
        sortedMap.putAll(referees);
        out.println("<h2 id='RefereesAggregator'>Судейская статистика</h2>");
        out.println("<pre>");
        out.println("================================================================================================================");
        out.println("| Судья                | Город                | Игр        | Удалений   | Два преду- | Предупре-  | Пенальти   |");
        out.println("|                      |                      |            |            | преждения  | ждений     |            |");
        out.println("================================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = referees.get(s);
            out.printf("| %-20s | %-20s | %-10d | %-10d | %-10d | %-10d | %-10d |%n",
                    stat.name, stat.city, stat.games, stat.redCards, stat.redAndYellowCards, stat.yellowCards, stat.penalties);
        }
        out.println("================================================================================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private int games = 0;
        private int redCards = 0;
        private int redAndYellowCards = 0;
        private int yellowCards = 0;
        private int penalties = 0;
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
        private HashMap<String, TournamentStat> map;

        public StatComparator(final HashMap<String, TournamentStat> map) {
            this.map = map;
        }

        @Override
        public int compare(String key1, String key2) {
            TournamentStat stat1 = map.get(key1);
            TournamentStat stat2 = map.get(key2);
            String value1 = stat1.name;
            String value2 = stat2.name;
            String comp1 = value1 + "|" + key1;
            String comp2 = value2 + "|" + key2;
            return collator.compare(comp1, comp2);
        }
        
    }

    @Override
    public void drawCharts(String title) {
    }

}
