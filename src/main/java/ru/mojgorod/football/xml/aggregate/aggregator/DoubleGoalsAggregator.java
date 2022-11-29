/*
Copyright (c) 2022, Valeriy Soldatov
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
import java.util.Collections;

/**
 *
 * @author sova
 */
public class DoubleGoalsAggregator extends HatTricsAggregator {

    public DoubleGoalsAggregator() {
        min = 2;
    }

    @Override
    public void print() {
        PrintStream out = getOutput();
        if (isPlayerInfo()) {
            for (TournamentStat pl : matches) {
                pl.name = getPlayerInfo(pl.key).getName();
            }
        }
        Collections.sort(matches, new StatArrayComparator());
        out.println("<h2 id='DoubleGoalsAggregator'>2 и более мяча за матч (дубль, хет-трик, покер)</h2>");
        out.println("<pre>");
        out.println("=================================================================================================================");
        out.println("| Игрок                     | Команда              | Матч                                               | Мячей |");
        out.println("=================================================================================================================");
        for (TournamentStat stat : matches) {
            out.printf("| %-25s | %-20s | %-50s | %-5s |%n",
                    stat.name, stat.team, stat.match, stat.goals);
        }
        out.println("=================================================================================================================");
        out.println( "</pre>");
    }

}
