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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.config.ConfigFile;
import ru.mojgorod.football.xml.library.FootballXmlPlayersInfo;

/**
 *
 * @author sova
 */
public class SeasonParameters {

    private final Season season;
    private final String filePath;
    private final ConfigFile configFile;
    private final PlayersManager playersManager;
    private final Aggregator[] aggregators;
    private PrintStream out = null;

    public SeasonParameters(final ConfigFile configFile, final PlayersManager playersManager,
            final Season season, final String filePath, final Aggregator[] aggregators) {
        this.configFile = configFile;
        this.playersManager = playersManager;
        this.season = season;
        this.filePath = filePath;
        this.aggregators = aggregators;
    }

    public Season getSeason() {
        return season;
    }

    public String getFilePath() {
        return filePath;
    }

    public Aggregator[] getAggregators() {
        return aggregators;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public PlayersManager getPlayersManager() {
        return playersManager;
    }

    public boolean isPlayerInfo() {
        return playersManager != null;
    }

    public FootballXmlPlayersInfo getPlayerInfo(String key) {
        return playersManager.getPlayerInfo(key);
    }

    public PrintStream getOutput() {
        if (out == null) {
            if (filePath == null) {
                out = System.out;
            } else {
                try {
                    out = new PrintStream(new FileOutputStream(filePath), true, "UTF-8");
                } catch (IOException ex) {
                    if (out != null) {
                        out.close();
                    }
                    out = null;
                    Logger.getLogger(SeasonsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return out;
    }

    void closeOutput() {
        if (out == null || System.out.equals(out)) {
            return;
        }
        out.close();
        out = null;
    }

}
