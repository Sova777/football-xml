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
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.config.Config;
import ru.mojgorod.football.xml.config.ConfigFile;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class CoachesAggregator implements Aggregator {


    private final HashMap<String, TournamentStat> coaches = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String team1 = xmlReport.getTeam1();
        String team2 = xmlReport.getTeam2();

        String coach1 = xmlReport.getCoach1();
        String coachKey1 = xmlReport.getCoachKeyWithTeam1();
        String coach2 = xmlReport.getCoach2();
        String coachKey2 = xmlReport.getCoachKeyWithTeam2();

        TournamentStat stat1 = TournamentStat.get(coaches, coachKey1);
        stat1.coach = coach1;
        stat1.team = xmlReport.getTeam1();
        stat1.games++;
        if (xmlReport.isWinTeam1()) {
            stat1.wins++;
        }
        if (xmlReport.isDrawTeam1()) {
            stat1.draw++;
        }
        if (xmlReport.isLoseTeam1()) {
            stat1.loses++;
        }
        stat1.goals1 += xmlReport.getGoalsInt1();
        stat1.goals2 += xmlReport.getGoalsInt2();

        TournamentStat stat2 = TournamentStat.get(coaches, coachKey2);
        stat2.coach = coach2;
        stat2.team = xmlReport.getTeam2();
        stat2.games++;
        if (xmlReport.isWinTeam2()) {
            stat2.wins++;
        }
        if (xmlReport.isDrawTeam2()) {
            stat2.draw++;
        }
        if (xmlReport.isLoseTeam2()) {
            stat2.loses++;
        }
        stat2.goals1 += xmlReport.getGoalsInt2();
        stat2.goals2 += xmlReport.getGoalsInt1();

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            String team = event.getTeam();
            TournamentStat statCurrent;
            if (team.equals(team1)) {
                statCurrent = stat1;
            } else if (team.equals(team2)) {
                statCurrent = stat2;
            } else {
                Logger.getLogger(GoalsForAggregetor.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                continue;
            }

            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case RED_CARD:
                    statCurrent.redCards++;
                    break;
                case YELLOW_CARD:
                    statCurrent.yellowCards++;
                    break;
                case RED_AND_YELLOW_CARD:
                    statCurrent.redAndYellowCards++;
                    break;
                case PENALTY_GOAL:
                case PENALTY_MISSED:
                    statCurrent.penalty++;
                    break;
            }
        }
    }

    @Override
    public void print(final ConfigFile configFile, final Config config, final PrintStream out, final String title, final String id) {
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(coaches));
        sortedMap.putAll(coaches);
        out.println("<h2 id='CoachesAggregator'>Тренерская статистика</h2>");
        out.println("<p class=\"text\">Статистика по тренерам не всегда верна, но она очень важна. Мне будет удобней видеть и править ошибки если она будет видна на этой странице.</p>");
        out.println("<pre>");
        out.println("====================================================================================================================================");
        out.println("| Тренер                 | Команда              | Игр   | Статистика           | Удалений   | Два преду- | Предупре-  | Пенальти   |");
        out.println("|                        |                      |       |                      |            | преждения  | ждений     |            |");
        out.println("====================================================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = coaches.get(s);
            String matchStat = String.format("+%2d =%2d -%2d %2d:%2d", stat.wins, stat.draw, stat.loses, stat.goals1, stat.goals2);
            out.printf("| %-22s | %-20s | %-5d | %-20s | %-10d | %-10d | %-10d | %-10d |%n",
                    stat.coach, stat.team, stat.games, matchStat, stat.redCards, stat.redAndYellowCards, stat.yellowCards, stat.penalty);
        }
        out.println("====================================================================================================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private int games = 0;
        private int wins = 0;
        private int draw = 0;
        private int loses = 0;
        private int goals1 = 0;
        private int goals2 = 0;
        private int redCards = 0;
        private int redAndYellowCards = 0;
        private int yellowCards = 0;
        private int penalty = 0;
        private String coach = "";
        private String team = "";

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
            int compare;
            TournamentStat stat1 = map.get(key1);
            TournamentStat stat2 = map.get(key2);

            String value1 = stat1.coach.replaceAll(" ", "_");
            String value2 = stat2.coach.replaceAll(" ", "_");
            compare = collator.compare(value1, value2);
            if (compare != 0) {
                return compare;
            }

            String team1 = stat1.team.replaceAll(" ", "_");
            String team2 = stat2.team.replaceAll(" ", "_");
            compare = collator.compare(team1, team2);
            if (compare != 0) {
                return compare;
            }

            return collator.compare(key1, key2);
        }
        
    }

    @Override
    public void drawCharts(final ConfigFile configFile, String title, String id) {
    }

}
