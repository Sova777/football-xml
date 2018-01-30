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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import ru.mojgorod.football.xml.PrintXML;
import ru.mojgorod.football.xml.aggregate.aggregator.Aggregator;
import ru.mojgorod.football.xml.library.FootballXmlParser;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public class SeasonManager {

    private final ArrayList<Item> items = new ArrayList<>();

    public SeasonManager() {
    }

    public void add(final Season season, final Aggregator... aggregator) {
        items.add(new Item(season, aggregator));
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
                                Logger.getLogger(PrintXML.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                } catch (IOException ex) {
                    Logger.getLogger(PrintXML.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void print() {
        for (Item item : items) {
            String title = item.getSeason().getTitle();
            System.out.println("============= " + title + " =============");
            for (Aggregator aggregator : item.getAggregators()) {
                System.out.println("--- " + aggregator.getClass().getSimpleName() + " ---");
                aggregator.print(title);
            }
        }
    }

    private static class Item {

        private final Season season;
        private final Aggregator[] aggregators;

        public Item(final Season season, final Aggregator[] aggregator) {
            this.season = season;
            this.aggregators = aggregator;
        }

        public Season getSeason() {
            return season;
        }

        public Aggregator[] getAggregators() {
            return aggregators;
        }

    }

}
