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
package ru.mojgorod.football.xml.library;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sova
 */
public class FootballXmlEvent {

    private String time;
    private String type;
    private String comment;
    private String team;
    private String playerId;
    private String player1;
    private String playerId2;
    private String player2;

    public FootballXmlEvent() {
    }

    public void setTime(final String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public Integer getTimeInt() {
        String timeString = getTime();;
        int pos = timeString.indexOf('+');
        if (pos != -1) {
            timeString = timeString.substring(0, pos);
        }
        int time;
        try {
            time = Integer.parseInt(timeString);
        } catch (NumberFormatException ex) {
            Logger.getLogger(FootballXmlEvent.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return time;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    void setComment(final String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    void setTeam(final String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    void setPlayerId(final String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    void setPlayer(final String player1) {
        this.player1 = player1;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayerKey1() {
        String key = playerId;
        if ("".equals(key)) {
            key = player1;
        }
        return (key == null) ? "" : key;
    }

    public String getPlayerWithTeamKey1(final String teamId) {
        String player = getPlayerKey1();
        String teamKey = teamId;
        if ("".equals(teamKey)) {
            teamKey = team;
        }
        if (teamKey == null) {
            teamKey = "";
        }
        return String.format("%s|%s", player, teamKey);
    }

    void setPlayerId2(final String playerId2) {
        this.playerId2 = playerId2;
    }

    public String getPlayerId2() {
        return playerId2;
    }

    void setPlayer2(final String player2) {
        this.player2 = player2;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getPlayerKey2() {
        String key = playerId2;
        if ("".equals(key)) {
            key = player2;
        }
        return (key == null) ? "" : key;
    }

    public String getPlayerWithTeamKey2(final String teamId) {
        String player = getPlayerKey2();
        String teamKey = teamId;
        if ("".equals(teamKey)) {
            teamKey = team;
        }
        if (teamKey == null) {
            teamKey = "";
        }
        return String.format("%s|%s", player, teamKey);
    }

    public boolean isAnyGoal() {
        return ("Гол".equals(type) || "Гол с пенальти".equals(type) || "Гол в свои ворота".equals(type));
    }

    public boolean isAutoGoal() {
        return "Гол в свои ворота".equals(type);
    }

    public FootballEventType getEventType() {
        switch(type) {
            case "Гол":
                return FootballEventType.GOAL;
            case "Гол с пенальти":
                return FootballEventType.PENALTY_GOAL;
            case "Незабитый пенальти":
                return FootballEventType.PENALTY_MISSED;
            case "Гол в свои ворота":
                return FootballEventType.AUTOGOAL;
            case "Замена":
                return FootballEventType.SUBSTITUTION;
            case "Замена вратаря":
                return FootballEventType.SUBSTITUTION_GOALKEEPER;
            case "Предупреждение":
                return FootballEventType.YELLOW_CARD;
            case "Удаление":
                return FootballEventType.RED_CARD;
            case "Удаление и предупреждение":
                return FootballEventType.RED_AND_YELLOW_CARD;
            default:
                Logger.getLogger(FootballXmlEvent.class.getName()).log(Level.SEVERE, "Unknown event type: {0}", type);
                return FootballEventType.UNKNOWN;
        }
    }

}
