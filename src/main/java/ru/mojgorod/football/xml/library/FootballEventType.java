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

/**
 *
 * @author sova
 */
public enum FootballEventType {

    GOAL, PENALTY_GOAL, AUTOGOAL, PENALTY_MISSED, RED_CARD, RED_CARD_STAFF,
    YELLOW_CARD, YELLOW_CARD_STAFF, RED_AND_YELLOW_CARD, RED_AND_YELLOW_CARD_STAFF,
    SUBSTITUTION, SUBSTITUTION_GOALKEEPER, UNKNOWN;

    public boolean isAnyGoal() {
        return this.equals(FootballEventType.GOAL)
                || this.equals(FootballEventType.PENALTY_GOAL)
                || this.equals(FootballEventType.AUTOGOAL);
    }

    public boolean isAutoGoal() {
        return this.equals(FootballEventType.AUTOGOAL);
    }

    public boolean isGoal() {
        return this.equals(FootballEventType.GOAL);
    }

    public boolean isPenaltyGoal() {
        return this.equals(FootballEventType.PENALTY_GOAL);
    }

    public boolean isPenaltyMissed() {
        return this.equals(FootballEventType.PENALTY_MISSED);
    }

    public boolean isRedCard() {
        return this.equals(FootballEventType.RED_CARD);
    }

    public boolean isRedCardStaff() {
        return this.equals(FootballEventType.RED_CARD_STAFF);
    }

    public boolean isRedAndYellowCard() {
        return this.equals(FootballEventType.RED_AND_YELLOW_CARD);
    }

    public boolean isRedAndYellowCardStaff() {
        return this.equals(FootballEventType.RED_AND_YELLOW_CARD_STAFF);
    }

    public boolean isYellowCard() {
        return this.equals(FootballEventType.YELLOW_CARD);
    }

    public boolean isYellowCardStaff() {
        return this.equals(FootballEventType.YELLOW_CARD_STAFF);
    }

    public boolean isSubstitution() {
        return this.equals(FootballEventType.SUBSTITUTION);
    }

    public boolean isSubstitutionGoalkeeper() {
        return this.equals(FootballEventType.SUBSTITUTION_GOALKEEPER);
    }

}
