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

/**
 *
 * @author sova
 */
public class FootballXmlGooalkeeperStat {

    private int games = 0;
    private int goals = 0;
    private int autogoals = 0;
    private int dry = 0;
    private int redCards = 0;
    private int yellowCards = 0;
    private int penaltySuccess = 0;
    private int penaltyMissed = 0;
    private String from = "";
    private String till = "";
    private String name = "";
    private String team = "";
    private String teamKey = "";
    private String teamOpponent = "";
    private String teamOpponentKey = "";
    private String key = "";
    private String longkey = "";

    public static FootballXmlGooalkeeperStat get(final HashMap<String,
            FootballXmlGooalkeeperStat> hashStat, final String keyStat) {
        if (!hashStat.containsKey(keyStat)) {
            hashStat.put(keyStat, new FootballXmlGooalkeeperStat());
        }
        return hashStat.get(keyStat);
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAutogoals() {
        return autogoals;
    }

    public void setAutogoals(int autogoals) {
        this.autogoals = autogoals;
    }

    public int getDry() {
        return dry;
    }

    public void setDry(int dry) {
        this.dry = dry;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int yellowCards) {
        this.yellowCards = yellowCards;
    }

    public int getPenaltySuccess() {
        return penaltySuccess;
    }

    public void setPenaltySuccess(int penaltySuccess) {
        this.penaltySuccess = penaltySuccess;
    }

    public int getPenaltyMissed() {
        return penaltyMissed;
    }

    public void setPenaltyMissed(int penaltyMissed) {
        this.penaltyMissed = penaltyMissed;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTill() {
        return till;
    }

    public void setTill(String till) {
        this.till = till;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getTeamOpponent() {
        return teamOpponent;
    }

    public void setTeamOpponent(String teamOpponent) {
        this.teamOpponent = teamOpponent;
    }

    public String getTeamOpponentKey() {
        return teamOpponentKey;
    }

    public void setTeamOpponentKey(String teamOpponentKey) {
        this.teamOpponentKey = teamOpponentKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLongkey() {
        return longkey;
    }

    public void setLongkey(String longkey) {
        this.longkey = longkey;
    }

}
