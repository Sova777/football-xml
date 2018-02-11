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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.PrintXML;

/**
 *
 * @author sova
 */
public class FootballXmlReport {

    private String tournament;
    private String round;
    private String matchId;
    private String team1;
    private String teamId1;
    private String team2;
    private String teamId2;
    private String date;
    private String time;
    private String goals1;
    private String goals2;
    private String extra1;
    private String extra2;
    private String penalties1;
    private String penalties2;
    private String stadium;
    private String stadiumId;
    private String stadiumCity;
    private String stadiumAttendance;
    private String referee;
    private String refereeId;
    private String refereeCity;
    private String coach1;
    private String coachId1;
    private String coach2;
    private String coachId2;
    private String text;
    private ArrayList<FootballXmlPlayer> players1 = new ArrayList<>();
    private ArrayList<FootballXmlPlayer> players2 = new ArrayList<>();
    private ArrayList<FootballXmlEvent> events = new ArrayList<>();
    private final String CODE_WIN = "+";
    private final String CODE_LOSE = "-";

    public void setTournament(final String tournament) {
        this.tournament = tournament;
    }

    public String getTournament() {
        return tournament;
    }

    public void setRound(final String round) {
        this.round = round;
    }

    public String getRound() {
        return round;
    }

    public void setMatchid(final String matchId) {
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setTeam1(final String team1) {
        this.team1 = team1;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeamId1(final String teamId1) {
        this.teamId1 = teamId1;
    }

    public String getTeamId1() {
        return teamId1;
    }

    public String getTeamKey1() {
        String key = teamId1;
        if ("".equals(key)) {
            key = team1;
        }
        return (key == null) ? "" : key;
    }

    public void setTeam2(final String team2) {
        this.team2 = team2;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeamId2(final String teamId2) {
        this.teamId2 = teamId2;
    }

    public String getTeamId2() {
        return teamId2;
    }

    public String getTeamKey2() {
        String key = teamId2;
        if ("".equals(key)) {
            key = team2;
        }
        return (key == null) ? "" : key;
    }

    public void setGoals1(final String goals1) {
        this.goals1 = goals1;
    }

    public String getGoals1() {
        return goals1;
    }

    public Integer getGoalsInt1() {
        int goals1;
        try {
            goals1 = Integer.parseInt(getGoals1());
        } catch (NumberFormatException ex) {
            Logger.getLogger(PrintXML.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return goals1;
    }

    public void setGoals2(final String goals2) {
        this.goals2 = goals2;
    }

    public String getGoals2() {
        return goals2;
    }

    public Integer getGoalsInt2() {
        int goals2;
        try {
            goals2 = Integer.parseInt(getGoals2());
        } catch (NumberFormatException ex) {
            Logger.getLogger(PrintXML.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return goals2;
    }

    public void setExtra1(final String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra2(final String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setPenalties1(final String penalties1) {
        this.penalties1 = penalties1;
    }

    public String getPenalties1() {
        return penalties1;
    }

    public void setPenalties2(final String penalties2) {
        this.penalties2 = penalties2;
    }

    public String getPenalties2() {
        return penalties2;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public Integer getDateInt() {
        if (date == null) {
            return null;
        }
        if (date.length() != 8) {
            Logger.getLogger(FootballXmlReport.class.getName()).log(Level.SEVERE, "Wrong date format: '{0}'", date);
            return null;
        }
        return Integer.valueOf(date.substring(6, 8)) + 100 * Integer.valueOf(date.substring(4, 6)) + 10000 * Integer.valueOf(date.substring(0, 4));
    }

    public String getDateString() {
        if (date == null) {
            return "";
        }
        if (date.length() != 8) {
            Logger.getLogger(FootballXmlReport.class.getName()).log(Level.SEVERE, "Wrong date format: {0}", date);
            return "";
        }
        return date.substring(6, 8) + "/" + date.substring(4, 6) + "/" + date.substring(0, 4);
    }

    public void setTime(final String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setStadium(final String stadium) {
        this.stadium = stadium;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadiumId(final String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public String getStadiumKey() {
        String key = stadiumId;
        if ("".equals(key)) {
            key = stadium;
        }
        return (key == null) ? "" : key;
    }

    public void setStadiumCity(final String stadiumCity) {
        this.stadiumCity = stadiumCity;
    }

    public String getStadiumCity() {
        return stadiumCity;
    }

    public void setStadiumAttendance(final String stadiumAttendance) {
        this.stadiumAttendance = stadiumAttendance;
    }

    public String getStadiumAttendance() {
        return stadiumAttendance;
    }

    public Integer getStadiumAttendanceInt() {
        int matchAttendance;
        try {
            matchAttendance = Integer.parseInt(getStadiumAttendance());
        } catch (NumberFormatException ex) {
            Logger.getLogger(FootballXmlReport.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return matchAttendance;
    }

    public void setReferee(final String referee) {
        this.referee = referee;
    }

    public String getReferee() {
        return referee;
    }

    public String getRefereeKey() {
        String key = refereeId;
        if ("".equals(key) || key == null) {
            key = referee;
        }
        return (key == null) ? "" : key;
    }

    public void setRefereeId(final String refereeId) {
        this.refereeId = refereeId;
    }

    public String getRefereeId() {
        return refereeId;
    }

    public void setRefereeCity(final String refereeCity) {
        this.refereeCity = refereeCity;
    }

    public String getRefereeCity() {
        return refereeCity;
    }


    public void setCoach1(final String coach1) {
        this.coach1 = coach1;
    }

    public String getCoach1() {
        return coach1;
    }

    public String getCoachKey1() {
        String key = coachId1;
        if ("".equals(key) || key == null) {
            key = coach1;
        }
        return (key == null) ? "" : key;
    }

    public String getCoachKeyWithTeam1() {
        String coach = getCoachKey1();
        String team = getTeamKey1();
        return String.format("%s|%s", coach, team);
    }

    public void setCoachId1(final String coachId1) {
        this.coachId1 = coachId1;
    }

    public String getCoachId1() {
        return coachId1;
    }

    public void setCoach2(final String coach2) {
        this.coach2 = coach2;
    }

    public String getCoach2() {
        return coach2;
    }

    public String getCoachKey2() {
        String key = coachId2;
        if ("".equals(key) || key == null) {
            key = coach2;
        }
        return (key == null) ? "" : key;
    }

    public String getCoachKeyWithTeam2() {
        String coach = getCoachKey2();
        String team = getTeamKey2();
        return String.format("%s|%s", coach, team);
    }

    public void setCoachId2(final String coachId2) {
        this.coachId2 = coachId2;
    }

    public String getCoachId2() {
        return coachId2;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    void addPlayer1(FootballXmlPlayer player1) {
        players1.add(player1);
    }

    public FootballXmlPlayer getPlayer1(int index) {
        return players1.get(index);
    }

    public List<FootballXmlPlayer> getPlayers1() {
        return players1;
    }

    void addPlayer2(FootballXmlPlayer player2) {
        players2.add(player2);
    }

    public FootballXmlPlayer getPlayer2(int index) {
        return players2.get(index);
    }

    public List<FootballXmlPlayer> getPlayers2() {
        return players2;
    }

    void addEvent(FootballXmlEvent event) {
        events.add(event);
    }

    public FootballXmlEvent getEvent(int index) {
        return events.get(index);
    }

    public List<FootballXmlEvent> getEvents() {
        return events;
    }

    public String getGoalkeeper1() {
        if (players1 == null || players1.isEmpty()) {
            return null;
        }
        return players1.get(0).getName();
    }

    public String getGoalkeeperKey1() {
        if (players1 == null || players1.isEmpty()) {
            return null;
        }
        return players1.get(0).getKey();
    }

    public String getGoalkeeperKeyWithTeam1() {
        String goalkeeper = getGoalkeeperKey1();
        String team = getTeamKey1();
        return String.format("%s|%s", goalkeeper, team);
    }

    public String getGoalkeeper2() {
        if (players2 == null || players2.isEmpty()) {
            return null;
        }
        return players2.get(0).getName();
    }

    public String getGoalkeeperKey2() {
        if (players2 == null || players2.isEmpty()) {
            return null;
        }
        return players2.get(0).getKey();
    }

    public String getGoalkeeperKeyWithTeam2() {
        String goalkeeper = getGoalkeeperKey2();
        String team = getTeamKey2();
        return String.format("%s|%s", goalkeeper, team);
    }

    public boolean isValidScore() {
        if (goals1 == null || goals2 == null) {
            return false;
        }
        return goals1.matches("^[0-9]+$") && goals2.matches("^[0-9]+$");
    }

    public boolean isWinTeam1() {
        if (!isValidScore()) {
            return CODE_WIN.equals(goals1);
        }
        int g1 = getGoalsInt1();
        int g2 = getGoalsInt2();
        return (g1 > g2);
    }

    public boolean isDrawTeam1() {
        if (!isValidScore()) {
            return false;
        }
        int g1 = getGoalsInt1();
        int g2 = getGoalsInt2();
        return (g1 == g2);
    }

    public boolean isLoseTeam1() {
        if (!isValidScore()) {
            return CODE_LOSE.equals(goals1);
        }
        int g1 = getGoalsInt1();
        int g2 = getGoalsInt2();
        return (g1 < g2);
    }

    public boolean isWinTeam2() {
        if (!isValidScore()) {
            return CODE_WIN.equals(goals2);
        }
        int g1 = getGoalsInt1();
        int g2 = getGoalsInt2();
        return (g1 < g2);
    }

    public boolean isDrawTeam2() {
        if (!isValidScore()) {
            return false;
        }
        int g1 = getGoalsInt1();
        int g2 = getGoalsInt2();
        return (g1 == g2);
    }

    public boolean isLoseTeam2() {
        if (!isValidScore()) {
            return CODE_LOSE.equals(goals2);
        }
        int g1 = getGoalsInt1();
        int g2 = getGoalsInt2();
        return (g1 > g2);
    }

    public void print() {
        System.out.print(getRound());
        System.out.println(getMatchId()
                + " " + getTournament()
                + " " + getRound());
        System.out.println(getTeam1() + " - " + getTeam2());
        System.out.println(getTeamId1() + " - " + getTeamId2());
        System.out.println(getGoals1() + " - " + getGoals2());
        System.out.println(getDate() + " " + getTime());
        System.out.println(getStadiumId()
                + " " + getStadium()
                + " " + getStadiumCity());
        System.out.println(getRefereeId()
                + " " + getReferee()
                + " " + getRefereeCity());
        System.out.println(getCoachId1()
                + " " + getCoach1());
        System.out.println(getCoachId2()
                + " " + getCoach2());
        for (FootballXmlPlayer player : getPlayers1()) {
            System.out.println(player.getId() + " " + player.getName());
        }

        for (FootballXmlPlayer player : getPlayers2()) {
            System.out.println(player.getId() + " " + player.getName());
        }

        for (FootballXmlEvent event : getEvents()) {
            System.out.println(event.getTime() + " " + event.getType()
                    + " " + event.getPlayerId() + " " + event.getPlayer1()
                    + " " + event.getPlayerId2() + " " + event.getPlayer2()
            );
        }
    }

}
