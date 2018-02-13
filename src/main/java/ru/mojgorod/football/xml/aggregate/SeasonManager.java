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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import ru.mojgorod.football.xml.aggregate.aggregator.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlParser;
import ru.mojgorod.football.xml.library.FootballXmlPlayersInfo;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class SeasonManager {

    private final ArrayList<Item> items = new ArrayList<>();
    private PlayersManager playersManager;
    private final Config config = new Config();

    public SeasonManager() {
    }

    public void add(final Season season, final String filePath, final Aggregator... aggregator) {
        items.add(new Item(season, filePath, aggregator));
    }

    public void aggregate() {
        for (Item item : items) {
            for (String folder : item.getSeason().getFolders()) {
                try {
                    Files.newDirectoryStream(Paths.get(folder)).forEach(path -> {
                        if (Files.isRegularFile(path)) {
                            try {
                                FootballXmlReport xmlReport = FootballXmlParser.parseFile(path.toFile().getAbsolutePath());
                                for (Aggregator aggregator : item.getAggregators()) {
                                    aggregator.add(xmlReport);
                                }
                            } catch (ParserConfigurationException | SAXException | IOException ex) {
                                Logger.getLogger(SeasonManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                } catch (IOException ex) {
                    Logger.getLogger(SeasonManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void addPlayersInfo(PlayersManager playersManager) {
        this.playersManager = playersManager;
    }

    public void print() {
        for (Item item : items) {
            PrintStream out = item.getOutput();
            if (out != null) {
                try {
                    String title = item.getSeason().getTitle();
//                    out.println("============= " + title + " =============");
                    printHeader(out, title);
                    for (Aggregator aggregator : item.getAggregators()) {
//                        out.println("--- " + aggregator.getClass().getSimpleName() + " ---");
                        aggregator.print(config, out, title);
                    }
                    printFooter(out, title);
                } finally {
                    item.closeOutput();
                }
            }
        }
    }

    public void addHeader(String header) {
        config.header = header;
    }

    public void addFooter(String footer) {
        config.footer = footer;
    }

    private void printHeader(PrintStream out, final String title) {
        if (config.header == null) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(config.header))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line.replaceAll("##title##", title));
            }
        } catch (IOException ex) {
            Logger.getLogger(SeasonManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printFooter(PrintStream out, final String title) {
        if (config.footer == null) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(config.footer))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line.replaceAll("##title##", title));
            }
        } catch (IOException ex) {
            Logger.getLogger(SeasonManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class Item {

        private final Season season;
        private final String filePath;
        private final Aggregator[] aggregators;
        private PrintStream out = null;

        public Item(final Season season, final String filePath, final Aggregator[] aggregator) {
            this.season = season;
            this.filePath = filePath;
            this.aggregators = aggregator;
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

        private PrintStream getOutput() {
            if (out == null) {
                if (filePath == null) {
                    out = System.out;
                } else {
                    try {
                        out = new PrintStream(new FileOutputStream(filePath), true, "Windows-1251");
                    } catch (IOException ex) {
                        if (out != null) {
                            out.close();
                        }
                        out = null;
                        Logger.getLogger(SeasonManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return out;
        }

        private void closeOutput() {
            if (out == null || System.out.equals(out)) {
                return;
            }
            out.close();
            out = null;
        }
    }

    public class Config {

        public boolean isFixNames = true;
        public String header = null;
        public String footer = null;

        public FootballXmlPlayersInfo getPlayerInfo(String key) {
            return playersManager.getPlayerInfo(key);
        }

    }

}
