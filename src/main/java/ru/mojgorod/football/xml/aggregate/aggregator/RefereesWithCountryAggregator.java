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
import java.util.TreeMap;

/**
 *
 * @author sova
 */
public class RefereesWithCountryAggregator extends RefereesAggregator {

    @Override
    public void print() {
        PrintStream out = getOutput();
        TreeMap<String, TournamentStat> sortedMap = new TreeMap<>(new StatComparator(referees));
        sortedMap.putAll(referees);
        out.println("<h2 id='RefereesWithCountryAggregator'>Судейская статистика</h2>");
        out.println("<pre>");
        out.println("================================================================================================================");
        out.println("| Судья                | Страна               | Игр        | Удалений   | Два преду- | Предупре-  | Пенальти   |");
        out.println("|                      |                      |            |            | преждения  | ждений     |            |");
        out.println("================================================================================================================");
        for (String s : sortedMap.keySet()) {
            TournamentStat stat = referees.get(s);
            out.printf("| %-20s | %-20s | %-10d | %-10d | %-10d | %-10d | %-10d |%n",
                    stat.name, stat.country, stat.games, stat.redCards, stat.redAndYellowCards, stat.yellowCards, stat.penalties);
        }
        out.println("================================================================================================================");
        out.println( "</pre>");
    }

}
