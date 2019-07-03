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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class EventsBySeasonAggregator extends Aggregator {

    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();
    private int redCards = 0;
    private int redAndYellowCards = 0;
    private int yellowCards = 0;
    private int goals = 0;
    private int goals1 = 0;
    private int goals2 = 0;
    private int autoGoals = 0;
    private int penaltySuccess = 0;
    private int penaltyMissed = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {

        String team1 = xmlReport.getTeam1();
        String team2 = xmlReport.getTeam2();

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {

            boolean isTeam1 = false;
            boolean isTeam2 = false;
            FootballEventType eventType = event.getEventType();
            String team = event.getTeam();
            if (team.equals(team1)) {
                isTeam1 = true;
            } else if (team.equals(team2)) {
                isTeam2 = true;
            } else {
                Logger.getLogger(EventsBySeasonAggregator.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
            }
            switch (eventType) {
                case RED_CARD:
                    redCards++;
                    break;
                case YELLOW_CARD:
                    yellowCards++;
                    break;
                case RED_AND_YELLOW_CARD:
                    redAndYellowCards++;
                    break;
                case GOAL:
                    goals++;
                    if (isTeam1) {
                        goals1++;
                    }
                    if (isTeam2) {
                        goals2++;
                    }
                    break;
                case PENALTY_GOAL:
                    goals++;
                    if (isTeam1) {
                        goals1++;
                    }
                    if (isTeam2) {
                        goals2++;
                    }
                    penaltySuccess++;
                    break;
                case PENALTY_MISSED:
                    penaltyMissed++;
                    break;
                case AUTOGOAL:
                    goals++;
                    if (isTeam1) {
                        goals2++;
                    }
                    if (isTeam2) {
                        goals1++;
                    }
                    autoGoals++;
                    break;
            }
        }
    }

    @Override
    public void afterSeason() {
        SeasonStat stat = new SeasonStat();
        stat.redCards = redCards;
        stat.redAndYellowCards = redAndYellowCards;
        stat.yellowCards = yellowCards;
        stat.goals = goals;
        stat.goals1 = goals1;
        stat.goals2 = goals2;
        stat.autoGoals = autoGoals;
        stat.penaltySuccess = penaltySuccess;
        stat.penaltyMissed = penaltyMissed;
        stat.title = getSeason().getTitle();
        redCards = 0;
        redAndYellowCards = 0;
        yellowCards = 0;
        goals = 0;
        goals1 = 0;
        goals2 = 0;
        autoGoals = 0;
        penaltySuccess = 0;
        penaltyMissed = 0;
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='EventsBySeasonAggregator'>Событий за сезон</h2>");
        out.println("<pre>");
        out.println("===========================================================================================================================");
        out.println("| Сезон           | Удалений   | Два преду- | Предупре-  |             Забито мячей             | Пенальти   | Забито в   |");
        out.println("|                 |            | преждения  | ждений     |------------|------------|------------| (забито)   | свои       |");
        out.println("|                 |            |            |            | Хозяева    | Гости      | Всего      |            | ворота     |");
        out.println("===========================================================================================================================");
        for (SeasonStat stat : seasons) {
            String penaltiesFor = String.format("%d(%d)", stat.penaltySuccess + stat.penaltyMissed, stat.penaltySuccess);
            out.printf("| %-15s | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d | %-10s | %-10d |%n",
                    stat.title, stat.redCards, stat.redAndYellowCards, stat.yellowCards, stat.goals1, stat.goals2, stat.goals,
                    penaltiesFor, stat.autoGoals);
        }
        out.println("===========================================================================================================================");
        out.println( "</pre>");
    }

    static private class SeasonStat {

        private int redCards = 0;
        private int redAndYellowCards = 0;
        private int yellowCards = 0;
        private int goals = 0;
        private int goals1 = 0;
        private int goals2 = 0;
        private int autoGoals = 0;
        private int penaltySuccess = 0;
        private int penaltyMissed = 0;
        private String title = "";

    }

}
