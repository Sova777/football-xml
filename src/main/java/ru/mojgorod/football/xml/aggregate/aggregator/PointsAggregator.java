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

import java.awt.Color;
import java.io.PrintStream;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;
import ru.mojgorod.football.chart.BarChart;
import ru.mojgorod.football.chart.BlockChart;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.Utils;

/**
 *
 * @author sova
 */
public class PointsAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> teams = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();
        int goals1 = xmlReport.getGoalsInt1();
        int goals2 = xmlReport.getGoalsInt2();

        TournamentStat stat1 = TournamentStat.get(teams, teamKey1);
        stat1.team = team1;
        stat1.addStat(teamKey2, goals1, goals2);
        TournamentStat stat2 = TournamentStat.get(teams, teamKey2);
        stat2.team = team2;
        stat2.addStat(teamKey1, goals2, goals1);
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        out.println("<h2 id='PointsAggregator'>Распределение заработанных очков</h2>");
        out.println("<p class=\"text\">Данная таблица не совсем соответствует турнирной. Здесь сначала учитываются очки, затем победы, разница мячей и название команды по алфавиту.</p>");
        out.println("<pre>");

        out.println(Utils.repeatText("=", 29 + 6 * teams.size()));
        out.print("|  № | Команда              |");
        for (int i = 0; i < teams.size(); i++) {
            out.printf(" %-3d |", i + 1);
        }
        out.println();
        out.println(Utils.repeatText("=", 29 + 6 * teams.size()));
        int counter = 0;
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            out.printf("| %2d | %-20s |", counter + 1, stat.team);
            for (String team : sortedMap.keySet()) {
                String points = "";
                TeamStat teamStat = stat.teamStat.get(team);
                if (teamStat != null) {
                    points = String.valueOf(3 * teamStat.wins + teamStat.draws);
                }
                out.printf(" %-3s |", points);
            }
            out.println();
            counter++;
        }
        out.println(Utils.repeatText("=", 29 + 6 * teams.size()));
        out.println("</pre>");
        out.println("<img src='image/stat_points_v" + getSeason().getId() + ".png'>");
        out.println("<pre>");
        out.println("зелёный цвет - только победы");
        out.println("синий цвет   - побед больше чем поражений");
        out.println("серый цвет   - равное число побед и поражений");
        out.println("жёлтый цвет  - поражений больше чем побед");
        out.println("красный цвет - только поражения.");
        out.println("</pre>");
    }

    static private class TournamentStat {

        private String team = "";
        private int wins = 0;
        private int draws = 0;
        private int loses = 0;
        private int goals1 = 0;
        private int goals2 = 0;
        private int points = 0;
        private HashMap<String, TeamStat> teamStat = new HashMap<>();

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

        public void addStat(final String keyTeam, int goals1, int goals2) {
            TeamStat stat = teamStat.get(keyTeam);
            if (stat == null) {
                stat = new TeamStat();
                this.teamStat.put(keyTeam, stat);
            }

            if (goals1 == goals2) {
                this.draws++;
                this.points++;
                stat.draws++;
            } else if (goals1 > goals2) {
                this.wins++;
                this.points += 3;
                stat.wins++;
            } else {//  if (goals1 < goals2) {
                this.loses++;
                stat.loses++;
            }
            this.goals1 += goals1;
            this.goals2 += goals2;
        }

    }

    static private class TeamStat {

        private int wins = 0;
        private int draws = 0;
        private int loses = 0;

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

            int points1 = stat1.points;
            int points2 = stat2.points;
            if (points1 < points2) {
                return 1;
            } else if (points1 > points2) {
                return -1;
            }

            int wins1 = stat1.wins;
            int wins2 = stat2.wins;
            if (wins1 < wins2) {
                return 1;
            } else if (wins1 > wins2) {
                return -1;
            }

            int diff1 = stat1.goals1 - stat1.goals2;
            int diff2 = stat2.goals1 - stat2.goals2;
            if (diff1 < diff2) {
                return 1;
            } else if (diff1 > diff2) {
                return -1;
            }
            return collator.compare(stat1.team, stat2.team);
        }
        
    }

    @Override
    public void drawCharts() {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        int items = teams.size();
        int height = 400;
        if (items > 16) {
            height += 16 * (items - 16);
        }

        String title = getSeason().getTitle();
        String id = getSeason().getId();

        BarChart chart = new BlockChart(800, height);
        chart.setCopyright("(c) football.mojgorod.ru");
        chart.setFontSize(14);
        chart.setFontSizeTitle(20);
        chart.setTitle("Распределение заработанных очков (" + title + ")");
        String outputFolder = getConfigFile().getOutputFolder();
        chart.setOutputFile(outputFolder + "/image/stat_points_v" + id + ".png");

        int counter = 0;
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            int teamIndex = 0;
            for (String team : sortedMap.keySet()) {
                Color color = BarChart.COLOR_WHITE;
                TeamStat teamStat = stat.teamStat.get(team);
                int points = 0;
                if (teamStat != null) {
                    points = 3 * teamStat.wins + teamStat.draws;
                    if (teamStat.wins > 0 && teamStat.draws == 0 && teamStat.loses == 0) {
                        color = BarChart.COLOR_GREEN;
                    } else if (teamStat.wins > teamStat.loses) {
                        color = BarChart.COLOR_BLUE;
                    } else if (teamStat.wins == teamStat.loses) {
                        color = BarChart.COLOR_GRAY;
                    } else if (teamStat.loses > 0 && teamStat.draws == 0 && teamStat.wins == 0) {
                        color = BarChart.COLOR_RED;
                    } else if (teamStat.wins < teamStat.loses) {
                        color = BarChart.COLOR_YELLOW;
                    }
                }
                chart.addPoint(teamIndex, stat.team, points, color);
                teamIndex++;
            }
            counter++;
        }
        chart.draw();
    }

}
