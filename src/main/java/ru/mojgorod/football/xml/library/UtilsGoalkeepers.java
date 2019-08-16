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
package ru.mojgorod.football.xml.library;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sova
 */
public class UtilsGoalkeepers {

    public static HashMap<String, FootballXmlGooalkeeperStat> getMatchStat(FootballXmlReport xmlReport) {
        final HashMap<String, FootballXmlGooalkeeperStat> matchKeepers = new HashMap<>();
        String team1 = xmlReport.getTeam1();
        String teamKey1 = xmlReport.getTeamKey1();
        String teamId1 = xmlReport.getTeamId1();
        String team2 = xmlReport.getTeam2();
        String teamKey2 = xmlReport.getTeamKey2();
        String teamId2 = xmlReport.getTeamId2();

        String goalkeeperKey1 = xmlReport.getGoalkeeperKey1();
        String goalkeeperKey2 = xmlReport.getGoalkeeperKey2();

        FootballXmlGooalkeeperStat statKeeper1 = getMathStat(matchKeepers, xmlReport.getGoalkeeperKey1());
        setMatchStat(statKeeper1, xmlReport.getGoalkeeper1(),
                xmlReport.getTeam1(), xmlReport.getTeamKey1(),
                xmlReport.getTeam2(), xmlReport.getTeamKey2(),
                xmlReport.getGoalkeeperKey1(), xmlReport.getGoalkeeperKeyWithTeam1());
        statKeeper1.setDry(1);

        FootballXmlGooalkeeperStat statKeeper2 = getMathStat(matchKeepers, xmlReport.getGoalkeeperKey2());
        setMatchStat(statKeeper2, xmlReport.getGoalkeeper2(),
                xmlReport.getTeam2(), xmlReport.getTeamKey2(),
                xmlReport.getTeam1(), xmlReport.getTeamKey1(),
                xmlReport.getGoalkeeperKey2(), xmlReport.getGoalkeeperKeyWithTeam2());
        statKeeper2.setDry(1);

        List<FootballXmlEvent> events = xmlReport.getEvents();
        for (FootballXmlEvent event : events) {
            String team = event.getTeam();
            FootballXmlGooalkeeperStat statCurrent;
            FootballXmlGooalkeeperStat statOpponent;
            if (team.equals(team1)) {
                statCurrent = statKeeper1;
                statOpponent = statKeeper2;
            } else if (team.equals(team2)) {
                statCurrent = statKeeper2;
                statOpponent = statKeeper1;
            } else {
                Logger.getLogger(UtilsGoalkeepers.class.getName()).log(Level.SEVERE, "Unknown team: {0}", team);
                continue;
            }

            FootballEventType eventType = event.getEventType();
            String time = event.getTime();
            switch (eventType) {
                case SUBSTITUTION:
                    if (goalkeeperKey1.equals(event.getPlayerKey1())) {
                        statKeeper1.setTill(event.getTime());
                    }
                    if (goalkeeperKey2.equals(event.getPlayerKey1())) {
                        statKeeper2.setTill(event.getTime());
                    }
                    if (goalkeeperKey1.equals(event.getPlayerKey1())) {
                        statCurrent.setDry(0);
                        statKeeper1 = getMathStat(matchKeepers, event.getPlayerKey2());
                        setMatchStat(statKeeper1, event.getPlayer2(),
                                team1, teamKey1, team2, teamKey2,
                                event.getPlayerId2(), event.getPlayerWithTeamKey2(teamId1));
                        goalkeeperKey1 = event.getPlayerKey2();
                    }
                    if (goalkeeperKey2.equals(event.getPlayerKey1())) {
                        statCurrent.setDry(0);
                        statKeeper2 = getMathStat(matchKeepers, event.getPlayerKey2());
                        setMatchStat(statKeeper2, event.getPlayer2(),
                                team2, teamKey2, team1, teamKey1,
                                event.getPlayerId2(), event.getPlayerWithTeamKey2(teamId2));
                        goalkeeperKey2 = event.getPlayerKey2();
                    }
                    if (goalkeeperKey1.equals(event.getPlayerKey2())) {
                        statKeeper1.setFrom(event.getTime());
                    }
                    if (goalkeeperKey2.equals(event.getPlayerKey2())) {
                        statKeeper2.setFrom(event.getTime());
                    }
                    break;
                case SUBSTITUTION_GOALKEEPER:
                    if ("".equals(statCurrent.getTill())) {
                        statCurrent.setTill(time);
                    }
                    statCurrent.setDry(0);
                    if (statKeeper1.equals(statCurrent)) {
                        statKeeper1 = getMathStat(matchKeepers, event.getPlayerKey1());
                        setMatchStat(statKeeper1, event.getPlayer1(),
                                team1, teamKey1, team2, teamKey2,
                                event.getPlayerId(), event.getPlayerWithTeamKey1(teamId1));
                        statKeeper1.setFrom(time);
                        goalkeeperKey1 = event.getPlayerKey1();
                    } else {
                        statKeeper2 = getMathStat(matchKeepers, event.getPlayerKey1());
                        setMatchStat(statKeeper2, event.getPlayer1(),
                                team2, teamKey2, team1, teamKey1,
                                event.getPlayerId(), event.getPlayerWithTeamKey1(teamId2));
                        statKeeper2.setFrom(time);
                        goalkeeperKey2 = event.getPlayerKey1();
                    }
                    break;
                case YELLOW_CARD:
                    if (goalkeeperKey1.equals(event.getPlayerKey1())) {
                        statKeeper1.setYellowCards(statKeeper1.getYellowCards() + 1);
                    }
                    if (goalkeeperKey2.equals(event.getPlayerKey1())) {
                        statKeeper2.setYellowCards(statKeeper2.getYellowCards() + 1);
                    }
                    break;
                case RED_CARD:
                case RED_AND_YELLOW_CARD:
                    if (goalkeeperKey1.equals(event.getPlayerKey1())) {
                        statKeeper1.setRedCards(statKeeper1.getRedCards() + 1);
                        statKeeper1.setTill(time);
                        statKeeper1.setDry(0);
                        goalkeeperKey1 = "???";
                    }
                    if (goalkeeperKey2.equals(event.getPlayerKey1())) {
                        statKeeper2.setRedCards(statKeeper2.getRedCards() + 1);
                        statKeeper2.setTill(time);
                        statKeeper2.setDry(0);
                        goalkeeperKey2 = "???";
                    }
                    break;
                case AUTOGOAL:
                    statCurrent.setGoals(statCurrent.getGoals() + 1);
                    statCurrent.setDry(0);
                    if (goalkeeperKey1.equals(event.getPlayerKey1())) {
                        statKeeper1.setAutogoals(statKeeper1.getAutogoals() + 1);
                    }
                    if (goalkeeperKey2.equals(event.getPlayerKey1())) {
                        statKeeper2.setAutogoals(statKeeper2.getAutogoals() + 1);
                    }
                    break;
                case GOAL:
                    statOpponent.setGoals(statOpponent.getGoals() + 1);
                    statOpponent.setDry(0);
                    break;
                case PENALTY_GOAL:
                    statOpponent.setPenaltySuccess(statOpponent.getPenaltySuccess() + 1);
                    statOpponent.setGoals(statOpponent.getGoals() + 1);
                    statOpponent.setDry(0);
                    break;
                case PENALTY_MISSED:
                    statOpponent.setPenaltyMissed(statOpponent.getPenaltyMissed() + 1);
                    break;
            }
        }
        return matchKeepers;
    }

    private static FootballXmlGooalkeeperStat getMathStat(
            final HashMap<String, FootballXmlGooalkeeperStat> hashStat, final String keyStat) {
        if (!hashStat.containsKey(keyStat)) {
            hashStat.put(keyStat, new FootballXmlGooalkeeperStat());
        }
        return hashStat.get(keyStat);
    }

    private static void setMatchStat(final FootballXmlGooalkeeperStat statKeeper,
            final String name, final String team, final String teamKey,
            final String teamOpponent, final String teamOpponentKey,
            final String key, final String longkey){
        statKeeper.setName(name);
        statKeeper.setTeam(team);
        statKeeper.setTeamKey(teamKey);
        statKeeper.setTeamOpponent(teamOpponent);
        statKeeper.setTeamOpponentKey(teamOpponentKey);
        statKeeper.setGames(1);
        statKeeper.setKey(key);
        statKeeper.setLongkey(longkey);
    }

}
