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
import java.util.HashMap;
import java.util.List;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.Age;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlPlayer;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.Utils;

/**
 *
 * @author sova
 */
public class MedianBySeasonAggregator extends Aggregator {

    private final HashMap<String, TournamentStat> playersMap = new HashMap<>();
    private final static ArrayList<SeasonStat> seasons = new ArrayList<>();
    private final static ArrayList<Integer> attendanceArray = new ArrayList<>();
    private final static ArrayList<Integer> yellowCardsArray = new ArrayList<>();
    private final static ArrayList<Integer> goalsArray = new ArrayList<>();
    private final static ArrayList<Integer> goals1Array = new ArrayList<>();
    private final static ArrayList<Integer> goals2Array = new ArrayList<>();
    private int maxDate = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        int attendance;
        if (xmlReport.isBehindClosedDoors()) {
            attendance = 0;
        } else {
            Integer attendanceInteger = xmlReport.getStadiumAttendanceInt();
            attendance = (attendanceInteger == null) ? 0 : attendanceInteger;
        }

        attendanceArray.add(attendance);
        Integer date = xmlReport.getDateInt();
        if (maxDate < date) {
            maxDate = date;
        }
        int goals1 = xmlReport.getGoalsInt1();
        goals1Array.add(goals1);
        int goals2 = xmlReport.getGoalsInt2();
        goals2Array.add(goals2);
        goalsArray.add(goals1 + goals2);

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            TournamentStat stat = TournamentStat.get(playersMap, player.getKey());
            stat.games++;
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            TournamentStat stat = TournamentStat.get(playersMap, player.getKey());
            stat.games++;
        }

        int yellowCards = 0;
        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case YELLOW_CARD:
                    yellowCards++;
                    break;
                case SUBSTITUTION:
                    TournamentStat stat = TournamentStat.get(playersMap, event.getPlayerKey2());
                    stat.games++;
                    break;
            }
        }
        yellowCardsArray.add(yellowCards);
    }

    @Override
    public void afterSeason() {
        ArrayList<Double> ageArray = new ArrayList<>();
        if (isPlayerInfo()) {
            for (String key : playersMap.keySet()) {
                Age ageValue = getPlayerInfo(key).getAge(maxDate);
                Double playerAge = (ageValue == null) ? null : ageValue.getDoubleValue();
                if (playerAge != null) {
                    ageArray.add(playerAge);
                }
            }
        }
        SeasonStat stat = new SeasonStat();
        stat.attendance = Utils.medianInteger(attendanceArray);
        stat.goals = Utils.medianInteger(goalsArray);
        stat.goals1 = Utils.medianInteger(goals1Array);
        stat.goals2 = Utils.medianInteger(goals2Array);
        stat.yellowCards = Utils.medianInteger(yellowCardsArray);
        stat.age = Utils.medianDouble(ageArray);
        stat.title = getSeason().getTitle();
        attendanceArray.clear();
        goalsArray.clear();
        goals1Array.clear();
        goals2Array.clear();
        yellowCardsArray.clear();
        playersMap.clear();
        maxDate = 0;
        seasons.add(stat);
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='MedianBySeasonAggregator'>Медиана</h2>");
        out.println("<p class=\"text\">Иногда медиана интереснее чем среднее арифметическое значение. Отбрасываются все крайние значения и остаётся только самое типичное значение.</p>");
        out.println("<pre>");
        out.println("===========================================================================================");
        out.println("| Сезон           | Зрителей |            Мячей            | Предупре- | Возраст          |");
        out.println("|                 |          |---------|---------|---------| ждений    |                  |");
        out.println("|                 |          | Хозяева |  Гости  |  Всего  |           |                  |");
        out.println("===========================================================================================");
        for (SeasonStat stat : seasons) {
            out.printf("| %-15s | %-8.1f | %-7.1f | %-7.1f | %-7.1f | %-9.1f | %-16s |%n",
                    stat.title, stat.attendance, stat.goals1, stat.goals2, stat.goals, stat.yellowCards, Utils.getLocalizedMessage(stat.age));
        }
        out.println("===========================================================================================");
        out.println("</pre>");
    }

    static private class SeasonStat {

        private double attendance = 0;
        private double age = 0;
        private double goals = 0;
        private double goals1 = 0;
        private double goals2 = 0;
        private double yellowCards = 0;
        private String title = "";

    }

    static private class TournamentStat {

        private int games = 0;

        public static TournamentStat get(final HashMap<String, TournamentStat> hashStat, final String keyStat) {
            if (!hashStat.containsKey(keyStat)) {
                hashStat.put(keyStat, new TournamentStat());
            }
            return hashStat.get(keyStat);
        }

    }

}
