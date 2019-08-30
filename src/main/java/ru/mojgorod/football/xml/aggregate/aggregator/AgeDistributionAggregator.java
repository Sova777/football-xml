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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import ru.mojgorod.football.chart.BarChart;
import ru.mojgorod.football.chart.HorizontalBarChart;
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
public class AgeDistributionAggregator extends Aggregator {

    private HashSet<String> players = new HashSet<>();
    TreeMap<Integer, Integer> ages = new TreeMap<>();
    private int maxDate = 0;

    @Override
    public void add(FootballXmlReport xmlReport) {
        Integer date = xmlReport.getDateInt();
        if (maxDate < date) {
            maxDate = date;
        }

        List<FootballXmlPlayer> players1 = xmlReport.getPlayers1();
        for (FootballXmlPlayer player : players1) {
            addPlayer(player.getKey());
        }

        List<FootballXmlPlayer> players2 = xmlReport.getPlayers2();
        for (FootballXmlPlayer player : players2) {
            addPlayer(player.getKey());
        }

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            FootballEventType eventType = event.getEventType();
            switch (eventType) {
                case SUBSTITUTION:
                    addPlayer(event.getPlayerKey2());
                    break;
            }
        }
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        if (isPlayerInfo()) {
            for (String key : players) {
                Age ageValue = getPlayerInfo(key).getAge(maxDate);
                Double playerAge = (ageValue == null) ? null : ageValue.getDoubleValue();
                if (playerAge != null) {
                    int age = playerAge.intValue();
                    Integer value = ages.get(age);
                    if (value == null) {
                        ages.put(age, 1);
                    } else {
                        ages.put(age, value + 1);
                    }
                }
            }
        }

        out.println("<h2 id='AgeDistributionAggregator'>Возраст игроков</h2>");
        out.println("<p class=\"text\">Возраст игроков на момент последней игры сезона.</p>");
        out.println("<pre>");
        out.println("===========================");
        out.println("| Возраст    | Игроков    |");
        out.println("===========================");
        for (Map.Entry<Integer, Integer> entry : ages.entrySet()) {
            out.printf(Locale.US, "| %-10d | %-10d |%n",
                    entry.getKey(), entry.getValue());
        }
        out.println("===========================");
        out.println( "</pre>");
        out.println("<img src='image/stat_age_v" + getSeason().getId() + ".png'><br>");
    }

    public void addPlayer(final String keyPlayer) {
        if (!players.contains(keyPlayer)) {
            players.add(keyPlayer);
        }
    }

    @Override
    public void drawCharts() {
        int items = ages.size();
        int height = 400;
        if (items > 16) {
            height += 16 * (items - 16);
        }

        String title = getSeason().getTitle();
        String id = getSeason().getId();

        BarChart chart = new HorizontalBarChart(500, height);
        chart.setCopyright("(c) football.mojgorod.ru");
        chart.setFontSize(14);
        chart.setFontSizeTitle(20);
        chart.setTitle("Возраст игроков (" + title + ")");
        chart.setOutputFile(getConfigFile().getOutputFolder() + "/image/stat_age_v" + id + ".png");
        for (Map.Entry<Integer, Integer> entry : ages.entrySet()) {
            chart.addPoint(Utils.getLocalizedYearsMessage(entry.getKey()), Double.valueOf(entry.getValue()));
        }
        chart.draw();
    }

}
