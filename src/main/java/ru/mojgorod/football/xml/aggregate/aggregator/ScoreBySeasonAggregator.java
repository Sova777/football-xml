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
public class ScoreBySeasonAggregator extends Aggregator {

    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();
    private int winsHome = 0;
    private int winsVisitor = 0;
    private int winsStrong = 0;
    private int draws = 0;
    private int zero = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {
        int goals1 = xmlReport.getGoalsInt1();
        int goals2 = xmlReport.getGoalsInt2();
        if (goals1 == goals2) {
            draws++;
        }
        if (goals1 > goals2) {
            winsHome++;
        }
        if (goals1 < goals2) {
            winsVisitor++;
        }
        if (goals1 == 0 && goals2 == 0) {
            zero++;
        }
        int diff = goals1 - goals2;
        if (diff >= 3 || diff <= -3) {
            winsStrong++;
        }
    }

    @Override
    public void afterSeason() {
        SeasonStat stat = new SeasonStat();
        stat.winsHome = winsHome;
        stat.winsVisitor = winsVisitor;
        stat.winsStrong = winsStrong;
        stat.draws = draws;
        stat.zero = zero;
        stat.title = getSeason().getTitle();
        winsHome = 0;
        winsVisitor = 0;
        winsStrong = 0;
        draws = 0;
        zero = 0;
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='ScoreBySeasonAggregator'>Счёт</h2>");
        out.println("<pre>");
        out.println("=================================================================================================");
        out.println("| Сезон           |               Побед                  | Побед с    | Ничьих     | Счёт 0:0   |");
        out.println("|                 |--------------------------------------| крупным    |            |            |");
        out.println("|                 |   Всего    |  Хозяев    | Гостей     | счётом     |            |            |");
        out.println("=================================================================================================");
        for (SeasonStat stat : seasons) {
            out.printf("| %-15s | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d |%n",
                    stat.title, stat.winsHome + stat.winsVisitor, stat.winsHome, stat.winsVisitor, stat.winsStrong,
                    stat.draws, stat.zero);
        }
        out.println("=================================================================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private int winsHome = 0;
        private int winsVisitor = 0;
        private int winsStrong = 0;
        private int draws = 0;
        private int zero = 0;
        private String title = "";

    }

}
