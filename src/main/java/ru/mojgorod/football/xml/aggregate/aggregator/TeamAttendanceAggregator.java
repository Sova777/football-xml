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
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class TeamAttendanceAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> teams = new HashMap<>();

    @Override
    public void add(FootballXmlReport xmlReport) {
        String teamKey1 = xmlReport.getTeamKey1();
        String teamKey2 = xmlReport.getTeamKey2();
        Integer attendanceInteger = xmlReport.getStadiumAttendanceInt();
        int attendanceValue = (attendanceInteger == null) ? 0 : attendanceInteger;

        TournamentStat stat1 = TournamentStat.get(teams, teamKey1);
        stat1.team = xmlReport.getTeam1();
        stat1.homeGames++;
        stat1.homeAttendance += attendanceValue;

        TournamentStat stat2 = TournamentStat.get(teams, teamKey2);
        stat2.team = xmlReport.getTeam2();
        stat2.awayGames++;
        stat2.awayAttendance += attendanceValue;
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(teams));
        sortedMap.putAll(teams);
        out.println("<h2 id='TeamAttendanceAggregator'>Средняя командная посещаемость</h2>");
        out.println( "<pre>");
        out.println("===============================================================");
        out.println("| Команда              | Дома       | В гостях   | Общая      |");
        out.println("===============================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = teams.get(s);
            int home = (stat.homeGames == 0) ? 0 : stat.homeAttendance / stat.homeGames;
            int away = (stat.awayGames == 0) ? 0 : stat.awayAttendance / stat.awayGames;
            int total = ((stat.homeGames + stat.awayGames) == 0) ? 0
                    : (stat.homeAttendance + stat.awayAttendance) / (stat.homeGames + stat.awayGames);
            out.printf("| %-20s | %-10d | %-10d | %-10d |%n",
                    stat.team, home, away, total);
        }
        out.println("===============================================================");
        out.println( "</pre>");
    }

    static private class TournamentStat {

        private int homeAttendance = 0;
        private int awayAttendance = 0;
        private int homeGames = 0;
        private int awayGames = 0;
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
        private final HashMap<String, TournamentStat> map;

        public StatComparator(final HashMap<String, TournamentStat> map) {
            this.map = map;
        }

        @Override
        public int compare(String key1, String key2) {
            TournamentStat stat1 = map.get(key1);
            TournamentStat stat2 = map.get(key2);
            int value1 = (stat1.homeAttendance + stat1.awayAttendance) / (stat1.homeGames + stat1.awayGames);
            int value2 = (stat2.homeAttendance + stat2.awayAttendance) / (stat2.homeGames + stat2.awayGames);
            if (value1 < value2) {
                return 1;
            } else if (value1 > value2) {
                return -1;
            }
            return collator.compare(stat1.team, stat2.team);
        }
        
    }

}
