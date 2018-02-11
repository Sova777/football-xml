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
package ru.mojgorod.football.xml.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mojgorod.football.xml.aggregate.Season;

/**
 *
 * @author sova
 */
public class Config {

    private static String playersPath = null;
    private static String header = null;
    private static String footer = null;

    public static List<Season> readConfig() {
        final String configPath = System.getProperty("config.properties", "config.properties");
        String root = null;
        List<Season> seasons = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(configPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int pos = line.indexOf('=');
                if (pos != -1) {
                    String key = line.substring(0, pos);
                    String value = line.substring(pos + 1);
                    if ("root".equals(key)) {
                        root = value;
                    } else if ("players".equals(key)) {
                        playersPath = value;
                    } else if ("header".equals(key)) {
                        header = value;
                    } else if ("footer".equals(key)) {
                        footer = value;
                    } else if (key.startsWith("#")) { // комментарий
                    } else if (key.startsWith("season.")) {
                        String[] keyArray = key.split("\\.");
                        if (keyArray.length == 2) {
                            String[] folders = value.split(",");
                            int size = folders.length;
                            for (int i = 0; i < size; i++) {
                                folders[i] = root + folders[i];
                            }
                            seasons.add(new Season(keyArray[1], folders));
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
        return seasons;
    }

    public static String getPlayersPath() {
        return playersPath;
    }

    public static String getHeader() {
        return header;
    }

    public static String getFooter() {
        return footer;
    }

}
