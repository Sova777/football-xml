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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.library.FootballXmlPlayersInfo;

/**
 *
 * @author sova
 */
public class PlayersManager {
    
    private final ArrayList<FootballXmlPlayersInfo> players = new ArrayList<>();
    private final HashMap<String, FootballXmlPlayersInfo> playersHash = new HashMap<>();
    private static final FootballXmlPlayersInfo EMPTY_PLAYER  = new FootballXmlPlayersInfo();

    public void addPlayer(FootballXmlPlayersInfo player) {
        players.add(player);
        playersHash.put(player.getId(), player);
    }

    public FootballXmlPlayersInfo getPlayerInfo(final String key) {
        if (key == null) {
            Logger.getLogger(PlayersManager.class.getName()).log(Level.SEVERE, "Key is null");
            return EMPTY_PLAYER;
        }
        FootballXmlPlayersInfo playerInfo = playersHash.get(key);
        if (playerInfo == null) {
            Logger.getLogger(PlayersManager.class.getName()).log(Level.SEVERE, "Unknown key: {0}", key);
            return EMPTY_PLAYER;
        }
        return playerInfo;
    }

    public String getName(final String key) {
        return getPlayerInfo(key).getName();
    }

    public String getBirthday(final String key) {
        return getPlayerInfo(key).getBirthday();
    }

}
