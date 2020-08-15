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
import java.util.ArrayList;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class AttendanceBySeasonAggregator extends Aggregator {

    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();
    private static int attendance = 0;
    private static int games = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        Integer attendanceInteger = xmlReport.getStadiumAttendanceInt();
        int attendanceValue = (attendanceInteger == null) ? 0 : attendanceInteger;
        attendance += attendanceValue;
        games++;
    }

    @Override
    public void afterSeason() {
        SeasonStat stat = new SeasonStat();
        stat.games = games;
        stat.attendance = attendance;
        stat.title = getSeason().getTitle();
        games = 0;
        attendance = 0;
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='AttendanceBySeasonAggregator'>Средняя посещаемость за сезон</h2>");
        out.println( "<pre>");
        out.println("==========================================================");
        out.println("| Сезон           | Матчей     | Зрителей   | В среднем  |");
        out.println("==========================================================");
        for (SeasonStat stat : seasons) {
            int average = (stat.games == 0) ? 0 : (stat.attendance / stat.games);
            out.printf("| %-15s | %-10d | %-10d | %-10d |%n",
                    stat.title, stat.games, stat.attendance, average);
        }
        out.println("==========================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private int attendance = 0;
        private int games = 0;
        private String title = "";

    }

}
