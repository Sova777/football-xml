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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import ru.mojgorod.football.xml.config.ConfigFile;
import ru.mojgorod.football.xml.library.FootballXmlParser;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class SeasonsManager {

    private final ArrayList<SeasonParameters> seasons;
    private final ConfigFile configFile;
    private PlayersManager playersManager;

    public SeasonsManager(ConfigFile configFile) {
        this.seasons = new ArrayList<>();
        this.configFile = configFile;
    }

    public void add(final Season season, final String filePath, final Aggregator... aggregators) {
        seasons.add(new SeasonParameters(configFile, playersManager, season, filePath, aggregators));
    }

    public void aggregate() {
        for (SeasonParameters currentSeason : seasons) {
            Aggregator.setSeasonParameters(currentSeason);
            for (Aggregator aggregator : currentSeason.getAggregators()) {
                aggregator.beforeSeason();
            }

            for (String folder : currentSeason.getSeason().getFolders()) {
                try {
                    Files.newDirectoryStream(Paths.get(folder)).forEach(path -> {
                        if (Files.isRegularFile(path)) {
                            try {
                                FootballXmlReport xmlReport = FootballXmlParser.parseFile(path.toFile().getAbsolutePath());
                                for (Aggregator aggregator : currentSeason.getAggregators()) {
                                    aggregator.add(xmlReport);
                                }
                            } catch (ParserConfigurationException | SAXException | IOException ex) {
                                Logger.getLogger(SeasonsManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                } catch (IOException ex) {
                    Logger.getLogger(SeasonsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            for (Aggregator aggregator : currentSeason.getAggregators()) {
                aggregator.afterSeason();
            }
            Aggregator.setSeasonParameters(null);
        }
    }

    public void print() {
        for (SeasonParameters currentSeason : seasons) {
            Aggregator.setSeasonParameters(currentSeason);
            PrintStream out = currentSeason.getOutput();
            if (out != null) {
                try {
//                    String title = parameters.getSeason().getTitle();
//                    String id = parameters.getSeason().getId();
//                    out.println("============= " + title + " =============");
                    printHeader(out, currentSeason);
                    for (Aggregator aggregator : currentSeason.getAggregators()) {
//                        out.println("--- " + aggregator.getClass().getSimpleName() + " ---");
                        aggregator.print();
                        aggregator.drawCharts();
                    }
                    printFooter(out, currentSeason);
                } finally {
                    currentSeason.closeOutput();
                }
            }
            Aggregator.setSeasonParameters(null);
        }
    }

    private void printSection(PrintStream out, final String file, final SeasonParameters currentSeason) {
        if (file == null) {
            return;
        }
        StringBuilder seasonsLine = new StringBuilder("");
        for (SeasonParameters seasonParameters : seasons) {
            Season season = seasonParameters.getSeason();
            if (seasonsLine.length() != 0) {
                seasonsLine.append("\n");
            }
            String value = seasonParameters.equals(currentSeason) ? configFile.getCurrentSeason() : configFile.getOtherSeason();
            seasonsLine.append(value
                    .replaceAll("##title##", season.getTitle())
                    .replaceAll("##linktitle##", season.getLinkTitle())
                    .replaceAll("##id##", season.getId())
            );
        }
        String title = currentSeason.getSeason().getTitle();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line.replaceAll("##title##", title).replaceAll("##seasons##", seasonsLine.toString()));
            }
        } catch (IOException ex) {
            Logger.getLogger(SeasonsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printHeader(PrintStream out, final SeasonParameters currentSeason) {
        printSection(out, configFile.getHeader(), currentSeason);
    }

    private void printFooter(PrintStream out, final SeasonParameters currentSeason) {
        printSection(out, configFile.getFooter(), currentSeason);
    }

    public ConfigFile getConfig() {
        return configFile;
    }

    public void addPlayersInfo(PlayersManager playersManager) {
        this.playersManager = playersManager;
    }

}
