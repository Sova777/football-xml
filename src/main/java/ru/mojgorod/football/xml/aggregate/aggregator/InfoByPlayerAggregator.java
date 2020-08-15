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
import java.util.List;
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.library.FootballEventType;
import ru.mojgorod.football.xml.library.FootballXmlEvent;
import ru.mojgorod.football.xml.library.FootballXmlPlayer;
import ru.mojgorod.football.xml.library.FootballXmlPlayersInfo;
import ru.mojgorod.football.xml.library.FootballXmlReport;
import ru.mojgorod.football.xml.library.Utils;

/**
 *
 * @author sova
 */
public class InfoByPlayerAggregator extends Aggregator {

    private static String playerId = getParameterValue("playerId");
    private static String fullname = "";
    private static String age = "";
    private static String ageFirst = "";
    private static String ageLast = "";
    private static String ageFirstGoal = "";
    private static String ageLastGoal = "";
    private static String birthday = "";
    private static String birthdayIso8601 = "";
    private static String country  = "";
    private static String position = "";
    private static String height = "";
    private static String weight = "";
    private static int firstMatchDate = Integer.MAX_VALUE;
    private static String firstMatch = "";
    private static int lastMatchDate = Integer.MIN_VALUE;
    private static String lastMatch = "";
    private static int firstGoalDate = Integer.MAX_VALUE;
    private static String firstGoal = "";
    private static int lastGoalDate = Integer.MIN_VALUE;
    private static String lastGoal = "";

    @Override
    public void add(FootballXmlReport xmlReport) {
        if (xmlReport.isCanceled()) {
            return;
        }

        Integer date = xmlReport.getDateInt();
        boolean isPlayed = false;
        boolean isGoal = false;

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            if (playerId.equals(player.getKey())) {
                isPlayed = true;
                break;
            }
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            if (playerId.equals(player.getKey())) {
                isPlayed = true;
                break;
            }
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    if (playerId.equals(event.getPlayerKey2())) {
                        isPlayed = true;
                    }
                    break;
                case GOAL:
                case PENALTY_GOAL:
                    if (playerId.equals(event.getPlayerKey1())) {
                        isGoal = true;
                        break;
                    }
                    break;
            }
        }

        if (isPlayed) {
            if (date != null) {
                String match = String.format("%s %s - %s %s:%s",
                        xmlReport.getDateString(), xmlReport.getTeam1(), xmlReport.getTeam2(), xmlReport.getGoals1(), xmlReport.getGoals2());
                if (firstMatchDate > date) {
                    firstMatchDate = date;
                    firstMatch = match;
                }
                if (lastMatchDate < date) {
                    lastMatchDate = date;
                    lastMatch = match;
                }
                if (isGoal) {
                    if (firstGoalDate > date) {
                        firstGoalDate = date;
                        firstGoal = match;
                    }
                    if (lastGoalDate < date) {
                        lastGoalDate = date;
                        lastGoal = match;
                    }
                }
            }
        }
    }

    @Override
    public void afterSeason() {
        if (isPlayerInfo()) {
            FootballXmlPlayersInfo info = getPlayerInfo(playerId);
            fullname = info.getFullname();
            birthday = Utils.convertDateToString(info.getBirthdayInt());
            birthdayIso8601 = Utils.convertDateToIso8601(info.getBirthdayInt());
            country = info.getCountry();
            position = info.getPosition();
            height = info.getHeight();
            weight = info.getWeight();
            if (info.getAge() != null) {
                age = Utils.getLocalizedMessage(info.getAge());
                if (firstMatchDate != Integer.MAX_VALUE) {
                    ageFirst = Utils.getLocalizedMessage(info.getAge(firstMatchDate));
                }
                if (lastMatchDate != Integer.MIN_VALUE) {
                    ageLast = Utils.getLocalizedMessage(info.getAge(lastMatchDate));
                }
                if (firstGoalDate != Integer.MAX_VALUE) {
                    ageFirstGoal = Utils.getLocalizedMessage(info.getAge(firstGoalDate));
                }
                if (lastGoalDate != Integer.MIN_VALUE) {
                    ageLastGoal = Utils.getLocalizedMessage(info.getAge(lastGoalDate));
                }
            }
        }
    }

    public static void printFinalReport() {
        PrintStream out = getOutputFinalReport();
        out.println("<h2 id='InfoByPlayerAggregator'>Информация об игроке</h2>");
        out.println("<h3>" + fullname + "</h3>");
        out.println("<pre>");
        out.println(birthday + " (<span id=\"age\">?? лет</span>)");
//        out.println(birthday + " (" + age + ")");
        out.println("Страна: " + country);
        out.println("Амплуа: " + position);
        out.println("Рост: " + height + " см.");
        out.println("Вес: " + weight + "кг.");
        out.println("Первый матч: " + firstMatch + " (" + ageFirst + ")");
        out.println("Последний матч: " + lastMatch + " (" + ageLast + ")");
        if (!"".equals(firstGoal)) {
            out.println("Первый гол: " + firstGoal + " (" + ageFirstGoal + ")");
        }
        if (!"".equals(lastGoal)) {
            out.println("Последний гол: " + lastGoal + " (" + ageLastGoal + ")");
        }
        out.println("</pre>");
        out.println("<script language=\"javascript\">age(\"" + birthdayIso8601 + "\");</script>");
    }

}
