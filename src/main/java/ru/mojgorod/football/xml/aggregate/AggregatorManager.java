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
package ru.mojgorod.football.xml.aggregate;

import java.util.ArrayList;
import java.util.Arrays;
import ru.mojgorod.football.xml.aggregate.aggregator.Aggregator;
import ru.mojgorod.football.xml.aggregate.aggregator.GoalsAgainstAggregetor;
import ru.mojgorod.football.xml.aggregate.aggregator.GoalsForAggregetor;
import ru.mojgorod.football.xml.aggregate.aggregator.MatchesAggregator;
import ru.mojgorod.football.xml.aggregate.aggregator.StadiumsAggregator;
import ru.mojgorod.football.xml.aggregate.aggregator.StrongLoseAggregator;
import ru.mojgorod.football.xml.aggregate.aggregator.StrongWinAggregator;
import ru.mojgorod.football.xml.aggregate.aggregator.TeamAttendanceAggregator;
import ru.mojgorod.football.xml.aggregate.aggregator.TeamsAggregator;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class AggregatorManager {

    private final ArrayList<Aggregator> aggregators = new ArrayList<>();

    public static Aggregator createStadiumsAggregator() {
        return new StadiumsAggregator();
    }

    public static Aggregator createMatchesAggregator() {
        return new MatchesAggregator();
    }

    public static Aggregator createTeamAttendanceAggregator() {
        return new TeamAttendanceAggregator();
    }

    public static Aggregator createGoalsForAggregetor() {
        return new GoalsForAggregetor();
    }

    public static Aggregator createGoalsAgainstAggregetor() {
        return new GoalsAgainstAggregetor();
    }

    public static Aggregator createStrongWinAggregator() {
        return new StrongWinAggregator();
    }

    public static Aggregator createStrongLoseAggregator() {
        return new StrongLoseAggregator();
    }

    public static Aggregator createTeamsAggregator() {
        return new TeamsAggregator();
    }

    public AggregatorManager(Aggregator... aggregators) {
        this.aggregators.addAll(Arrays.asList(aggregators));
    }

    public void add(Aggregator aggregator) {
        aggregators.add(aggregator);
    }

    public void add(FootballXmlReport xmlReport) {
        for (Aggregator aggregator : aggregators) {
            aggregator.add(xmlReport);
        }
    }

}
