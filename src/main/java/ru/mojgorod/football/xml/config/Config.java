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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.mojgorod.football.xml.aggregate.Season;

/**
 *
 * @author sova
 */
public class Config {

    private static String playersPath = null;
    private static String outputFolder = null;
    private static String header = null;
    private static String footer = null;
    private static String currentSeason = null;
    private static String otherSeason = null;

    public static List<Season> readConfig() {
        final String configPath = System.getProperty("config.xml", "config.xml");
        List<Season> seasons = new ArrayList<>();
        Node node;
        String root;
        try {
            File inputFile = new File(configPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);
            document.getDocumentElement().normalize();
            root = document.getElementsByTagName("root").item(0).getTextContent();
            outputFolder = document.getElementsByTagName("output").item(0).getTextContent();
            node = document.getElementsByTagName("players").item(0);
            playersPath = (node == null) ? null : node.getTextContent();
            header = document.getElementsByTagName("header").item(0).getTextContent();
            footer = document.getElementsByTagName("footer").item(0).getTextContent();
            currentSeason = document.getElementsByTagName("current").item(0).getTextContent();
            otherSeason = document.getElementsByTagName("other").item(0).getTextContent();

            NodeList seasonNodes = document.getElementsByTagName("season");
            int size = seasonNodes.getLength();
            for (int i = 0; i < size; i++) {
                Node seasonNode = seasonNodes.item(i);
                if (seasonNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) seasonNode;
                    String id = element.getAttribute("id");
                    String title = element.getAttribute("title");
                    String linktitle = element.getAttribute("linktitle");
                    NodeList folderNodes = element.getElementsByTagName("folder");
                    int sizeFolders = folderNodes.getLength();
                    String[] folders = new String[sizeFolders];
                    for (int j = 0; j < sizeFolders; j++) {
                        Node folderNode = folderNodes.item(j);
                        if (seasonNode.getNodeType() == Node.ELEMENT_NODE) {
                            String folder = root + folderNode.getTextContent();
                            folders[j] = folder;
                        }
                    }
                    seasons.add(new Season(id, title, linktitle, folders));
                }
            }
        } catch (IOException | ParserConfigurationException | DOMException | SAXException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
        return seasons;
    }

    public static String getPlayersPath() {
        return playersPath;
    }

    public static String getOutputFolder() {
        return outputFolder;
    }

    public static String getHeader() {
        return header;
    }

    public static String getFooter() {
        return footer;
    }

    public static String getCurrentSeason() {
        return currentSeason;
    }

    public static String getOtherSeason() {
        return otherSeason;
    }

}
