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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import ru.mojgorod.football.xml.aggregate.SeasonManager;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class HatTricsAggregator implements Aggregator {

    private final ArrayList<TournamentStat> matches = new ArrayList<>();
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
            if (itemStat.goals >= 3) {
                TournamentStat tournamentStat = new TournamentStat();
                tournamentStat.name = itemStat.name;
                tournamentStat.team = itemStat.team;
                tournamentStat.match = String.format("%s %s - %s %s:%s",
                        xmlReport.getDateString(), xmlReport.getTeam1(), xmlReport.getTeam2(), xmlReport.getGoals1(), xmlReport.getGoals2());
                tournamentStat.goals = itemStat.goals;
                tournamentStat.key = itemStat.key;
                tournamentStat.date = xmlReport.getDate();
                matches.add(tournamentStat);
            }
        }
    }

    @Override
    public void print(final SeasonManager.Config config, final PrintStream out, final String title) {
        if (config.isPlayerInfo()) {
            for (TournamentStat pl : matches) {
                pl.name = config.getPlayerInfo(pl.key).getName();
            }
        }
        Collections.sort(matches, new StatArrayComparator());
        out.println("<h2>3 и более мяча за матч (хет-трик, покер)</h2>");
        out.println("<pre>");
        out.println("=================================================================================================================");
        out.println("| Игрок                     | Команда              | Матч                                               | Мячей |");
        out.println("=================================================================================================================");
        for (TournamentStat stat : matches) {
            out.printf("| %-25s | %-20s | %-50s | %-5s |%n",
                    stat.name, stat.team, stat.match, stat.goals);
        }
        out.println("=================================================================================================================");
        out.println( "</pre>");
    }

    static private class MatchStat {

        private String name = "";
        private String team = "";
        private String key = "";
        private int goals = 0;
        
        public static void add(final HashMap<String, MatchStat> hashMatch, final FootballXmlEvent event) {
            String keyStat = event.getPlayerKey1();
            if (!hashMatch.containsKey(keyStat)) {
                hashMatch.put(keyStat, new MatchStat());
            }
            MatchStat matchStat = hashMatch.get(keyStat);
            matchStat.goals++;
            matchStat.name = event.getPlayer1();
            matchStat.team = event.getTeam();
            matchStat.key = event.getPlayerKey1();
        }

    }

    static private class TournamentStat {

        private String name = "";
        private String team = "";
        private String match = "";
        private int goals = 0;
        private String key = "";
        private String date = "";

    }

    static private class StatArrayComparator implements Comparator<TournamentStat> {

        Collator collator = Collator.getInstance(new Locale("ru", "RU"));

        @Override
        public int compare(TournamentStat stat1, TournamentStat stat2) {
            String value1 = stat1.date + "|" + stat1.name + "|" + stat1.key;
            String value2 = stat2.date + "|" + stat2.name + "|" + stat2.key;
            return collator.compare(value1, value2);
        }
        
    }

}
