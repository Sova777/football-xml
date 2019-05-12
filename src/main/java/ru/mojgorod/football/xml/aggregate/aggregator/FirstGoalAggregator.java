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
import ru.mojgorod.football.xml.aggregate.SeasonParameters;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class FirstGoalAggregator implements Aggregator {

    private final HashMap<String, TournamentStat> teams = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (!xmlReport.isValidScore()) {
            return;
        }

        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();

        TournamentStat statFirstGoal = null;
        TournamentStat statNotFirstGoal = null;
        boolean isTeam1 = true;
        int g1 = 0;
        int g2 = 0;

        boolean isFirstGoal = true;
        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            if (eventType.isAnyGoal()) {
                String team = event.getTeam();
                if (team.equals(team1)) {
                    if (eventType.isGoal() || eventType.isPenaltyGoal()) {
                        if (isFirstGoal) {
                            statFirstGoal = TournamentStat.get(teams, teamKey1);
                            statFirstGoal.team = team1;
                            statNotFirstGoal = TournamentStat.get(teams, teamKey2);
                            statNotFirstGoal.team = team2;
                            isTeam1 = true;
                        }
                        g1++;
                    } else if (eventType.isAutoGoal()) {
                        if (isFirstGoal) {
                            statNotFirstGoal = TournamentStat.get(teams, teamKey1);
                            statNotFirstGoal.team = team1;
                            statFirstGoal = TournamentStat.get(teams, teamKey2);
                            statFirstGoal.team = team2;
                            isTeam1 = false;
                        }
                        g2++;
                    }
                } else {
                    if (eventType.isGoal() || eventType.isPenaltyGoal()) {
                        if (isFirstGoal) {
                            statFirstGoal = TournamentStat.get(teams, teamKey2);
                            statFirstGoal.team = team2;
                            statNotFirstGoal = TournamentStat.get(teams, teamKey1);
                            statNotFirstGoal.team = team1;
                            isTeam1 = false;
                        }
                        g2++;
                    } else if (eventType.isAutoGoal()) {
                        if (isFirstGoal) {
                            statNotFirstGoal = TournamentStat.get(teams, teamKey2);
                            statNotFirstGoal.team = team2;
                            statFirstGoal = TournamentStat.get(teams, teamKey1);
                            statFirstGoal.team = team1;
                            isTeam1 = true;
                        }
                        g1++;
                    }
                }
                isFirstGoal = false;
            }
        }

        if (statFirstGoal != null && statNotFirstGoal != null) {
            if (isTeam1) {
                if (g1 > g2) {
                    statFirstGoal.winFirst++;
                    statNotFirstGoal.loseNotFirst++;
                } else if (g1 < g2) {
                    statFirstGoal.loseFirst++;
                    statNotFirstGoal.winNotFirst++;
                } else {
                    statFirstGoal.drawFirst++;
                    statNotFirstGoal.drawNotFirst++;
                }
                if (g2 == 0) {
                    statFirstGoal.noGoalsFirst++;
                    statNotFirstGoal.noGoalsNotFirst++;
                }
            } else {
                if (g1 < g2) {
                    statFirstGoal.winFirst++;
                    statNotFirstGoal.loseNotFirst++;
                } else if (g1 > g2) {
                    statFirstGoal.loseFirst++;
                    statNotFirstGoal.winNotFirst++;
                } else {
                    statFirstGoal.drawFirst++;
                    statNotFirstGoal.drawNotFirst++;
                }
                if (g1 == 0) {
                    statFirstGoal.noGoalsFirst++;
                    statNotFirstGoal.noGoalsNotFirst++;
                }
            }
        }
    }

    @Override
    public void print(final SeasonParameters parameters) {
        PrintStream out = parameters.getOutput();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        out.println("<h2 id='FirstGoalAggregator'>Влияние первого гола на итоговый результат</h2>");
        out.println("<pre>");
        out.println("==============================================================================================================");
        out.println("| Команда              |                Забили первыми            |            Пропустили первыми            |");
        out.println("|                      |-------------------------------------------------------------------------------------|");
        out.println("|                      | побед  | ничьих | пораж. | не пропустили | побед  | ничьих | пораж. | не забили     |");
        out.println("==============================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            out.printf("| %-20s | %-6d | %-6d | %-6d | %-13d | %-6d | %-6d | %-6d | %-13d |%n",
                    stat.team, stat.winFirst, stat.drawFirst, stat.loseFirst, stat.noGoalsFirst,
                    stat.winNotFirst, stat.drawNotFirst, stat.loseNotFirst, stat.noGoalsNotFirst);
        }
        out.println("==============================================================================================================");
        out.println("</pre>");
    }

    static private class TournamentStat {

        private String team = "";
        private int winFirst = 0;
        private int drawFirst = 0;
        private int loseFirst = 0;
        private int noGoalsFirst = 0;
        private int winNotFirst = 0;
        private int drawNotFirst = 0;
        private int loseNotFirst = 0;
        private int noGoalsNotFirst = 0;

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
            String value1 = stat1.team;
            String value2 = stat2.team;
            return collator.compare(value1, value2);
        }

    }

    @Override
    public void drawCharts(final SeasonParameters parameters) {
    }

}
