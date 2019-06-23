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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class HatTricsBySeasonAggregator extends Aggregator {

    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();
    private static int two = 0;
    private static int hattrics = 0;
    private static int pokers = 0;
    private static int fiveOrMore = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {
        HashMap<String, MatchStat> matchStat = new HashMap<>();
        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case GOAL:
                case PENALTY_GOAL:
                    MatchStat.add(matchStat, event);
                    break;
            }
        }
        for (String key : matchStat.keySet()) {
            MatchStat itemStat = matchStat.get(key);
            if (itemStat.goals == 2) {
                two++;
            } else if (itemStat.goals == 3) {
                hattrics++;
            } else if (itemStat.goals == 4) {
                pokers++;
            } else if (itemStat.goals >= 5) {
                fiveOrMore++;
            }
        }
    }

    @Override
    public void afterSeason() {
        SeasonStat stat = new SeasonStat();
        stat.two = two;
        stat.hattrics = hattrics;
        stat.pokers = pokers;
        stat.fiveOrMore = fiveOrMore;
        stat.title = getSeason().getTitle();
        two = 0;
        hattrics = 0;
        pokers = 0;
        fiveOrMore = 0;
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='HatTricsBySeasonAggregator'>2 и более мяча за матч (дубль, хет-трик, покер)</h2>");
        out.println("<pre>");
        out.println("=======================================================================");
        out.println("| Сезон           | Дублей     | Хет-триков | Покеров    | 5 и больше |");
        out.println("=======================================================================");
        for (SeasonStat stat : seasons) {
            out.printf("| %-15s | %-10d | %-10d | %-10d | %-10d |%n",
                    stat.title, stat.two, stat.hattrics, stat.pokers, stat.fiveOrMore);
        }
        out.println("=======================================================================");
        out.println( "</pre>");
    }

    static private class MatchStat {

        private int goals = 0;
        
        public static void add(final HashMap<String, MatchStat> hashMatch, final FootballXmlEvent event) {
            String keyStat = event.getPlayerKey1();
            if (!hashMatch.containsKey(keyStat)) {
                hashMatch.put(keyStat, new MatchStat());
            }
            MatchStat matchStat = hashMatch.get(keyStat);
            matchStat.goals++;
        }

    }

    static private class SeasonStat {

        private int two = 0;
        private int hattrics = 0;
        private int pokers = 0;
        private int fiveOrMore = 0;
        private String title = "";

    }

}
