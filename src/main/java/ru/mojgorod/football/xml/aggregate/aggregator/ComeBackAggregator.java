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
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class ComeBackAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> teams = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        if (!xmlReport.isValidScore()) {
            return;
        }
        int g1Before60 = 0;
        int g1Before65 = 0;
        int g1Before70 = 0;
        int g1Before75 = 0;
        int g1Before80 = 0;
        int g1Before85 = 0;
        int g1Before90 = 0;
        int g1Final = 0;
        int g2Before60 = 0;
        int g2Before65 = 0;
        int g2Before70 = 0;
        int g2Before75 = 0;
        int g2Before80 = 0;
        int g2Before85 = 0;
        int g2Before90 = 0;
        int g2Final = 0;

        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            if (eventType.isAnyGoal()) {
                String team = event.getTeam();
                int time = event.getTimeInt();
                boolean isFirstTeam = false;
                if (team.equals(team1)) {
                    if (eventType.isGoal() || eventType.isPenaltyGoal()) {
                        isFirstTeam = true;
                    }
                } else {
                    if (eventType.isAutoGoal()) {
                        isFirstTeam = true;
                    }
                }

                if (isFirstTeam) { g1Final++; } else { g2Final++; }
                if (time < 90) {
                    if (isFirstTeam) { g1Before90++; } else { g2Before90++; }
                    if (time < 85) {
                        if (isFirstTeam) { g1Before85++; } else { g2Before85++; }
                        if (time < 80) {
                            if (isFirstTeam) { g1Before80++; } else { g2Before80++; }
                            if (time < 75) {
                                if (isFirstTeam) { g1Before75++; } else { g2Before75++; }
                                if (time < 70) {
                                    if (isFirstTeam) { g1Before70++; } else { g2Before70++; }
                                    if (time < 65) {
                                        if (isFirstTeam) { g1Before65++; } else { g2Before65++; }
                                        if (time < 60) {
                                            if (isFirstTeam) { g1Before60++; } else { g2Before60++; }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        TournamentStat stat1 = TournamentStat.get(teams, teamKey1);
        stat1.team = team1;
        TournamentStat stat2 = TournamentStat.get(teams, teamKey2);
        stat2.team = team2;

        if ((g1Final > g2Final) && (g1Before60 <= g2Before60)) {
            stat1.games60++;
        } else if ((g1Final < g2Final) && (g1Before60 >= g2Before60)) {
            stat2.games60++;
        } else if ((g1Final == g2Final) && (g1Before60 < g2Before60)) {
            stat1.games60++;
        } else if ((g1Final == g2Final) && (g1Before60 > g2Before60)) {
            stat2.games60++;
        }

        if ((g1Final > g2Final) && (g1Before65 <= g2Before65)) {
            stat1.games65++;
        } else if ((g1Final < g2Final) && (g1Before65 >= g2Before65)) {
            stat2.games65++;
        } else if ((g1Final == g2Final) && (g1Before65 < g2Before65)) {
            stat1.games65++;
        } else if ((g1Final == g2Final) && (g1Before65 > g2Before65)) {
            stat2.games65++;
        }

        if ((g1Final > g2Final) && (g1Before70 <= g2Before70)) {
            stat1.games70++;
        } else if ((g1Final < g2Final) && (g1Before70 >= g2Before70)) {
            stat2.games70++;
        } else if ((g1Final == g2Final) && (g1Before70 < g2Before70)) {
            stat1.games70++;
        } else if ((g1Final == g2Final) && (g1Before70 > g2Before70)) {
            stat2.games70++;
        }

        if ((g1Final > g2Final) && (g1Before75 <= g2Before75)) {
            stat1.games75++;
        } else if ((g1Final < g2Final) && (g1Before75 >= g2Before75)) {
            stat2.games75++;
        } else if ((g1Final == g2Final) && (g1Before75 < g2Before75)) {
            stat1.games75++;
        } else if ((g1Final == g2Final) && (g1Before75 > g2Before75)) {
            stat2.games75++;
        }

        if ((g1Final > g2Final) && (g1Before80 <= g2Before80)) {
            stat1.games80++;
        } else if ((g1Final < g2Final) && (g1Before80 >= g2Before80)) {
            stat2.games80++;
        } else if ((g1Final == g2Final) && (g1Before80 < g2Before80)) {
            stat1.games80++;
        } else if ((g1Final == g2Final) && (g1Before80 > g2Before80)) {
            stat2.games80++;
        }

        if ((g1Final > g2Final) && (g1Before85 <= g2Before85)) {
            stat1.games85++;
        } else if ((g1Final < g2Final) && (g1Before85 >= g2Before85)) {
            stat2.games85++;
        } else if ((g1Final == g2Final) && (g1Before85 < g2Before85)) {
            stat1.games85++;
        } else if ((g1Final == g2Final) && (g1Before85 > g2Before85)) {
            stat2.games85++;
        }

        if ((g1Final > g2Final) && (g1Before90 <= g2Before90)) {
            stat1.games90++;
        } else if ((g1Final < g2Final) && (g1Before90 >= g2Before90)) {
            stat2.games90++;
        } else if ((g1Final == g2Final) && (g1Before90 < g2Before90)) {
            stat1.games90++;
        } else if ((g1Final == g2Final) && (g1Before90 > g2Before90)) {
            stat2.games90++;
        }
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        out.println("<h2 id='ComeBackAggregator'>Изменили исход игры в конце матча</h2>");
        out.println("<p class=\"text\">Проигрывая к 60 (65, 70, 75, 80, 85, 90) минуте, выиграли или сделали ничью. Или сделали ничью, проигрывая к 60 (65, 70, 75, 80, 85, 90) минуте.</p>");
        out.println("<pre>");
        out.println("=========================================================================");
        out.println("| Команда              |                     Матчей                     |");
        out.println("|                      |------------------------------------------------|");
        out.println("|                      | 60'  | 65'  | 70'  | 75'  | 80'  | 85'  | 90'  |");
        out.println("=========================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            if (stat.games60 != 0 || stat.games65 != 0 || stat.games70 != 0 || stat.games75 != 0
                    || stat.games80 != 0 || stat.games85 != 0 || stat.games90 != 0) {
                out.printf("| %-20s | %-4d | %-4d | %-4d | %-4d | %-4d | %-4d | %-4d |%n",
                        stat.team, stat.games60, stat.games65, stat.games70, stat.games75,
                        stat.games80, stat.games85, stat.games90);
            }
        }
        out.println("=========================================================================");
        out.println("</pre>");
    }

    static private class TournamentStat {

        private String team = "";
        private int games60 = 0;
        private int games65 = 0;
        private int games70 = 0;
        private int games75 = 0;
        private int games80 = 0;
        private int games85 = 0;
        private int games90 = 0;

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
            String value1 = stat1.team;
            String value2 = stat2.team;
            if (stat1.games60 > stat2.games60) {
                return -1;
            } else if (stat1.games60 < stat2.games60) {
                return 1;
            }
            return collator.compare(value1, value2);
        }
        
    }

}
