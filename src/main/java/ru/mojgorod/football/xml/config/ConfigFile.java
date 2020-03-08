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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
import ru.mojgorod.football.xml.aggregate.Aggregator;
import ru.mojgorod.football.xml.aggregate.Season;
import ru.mojgorod.football.xml.aggregate.SeasonsManager;
import ru.mojgorod.football.xml.library.FootballXmlPlayersParser;

/**
 *
 * @author sova
 */
public class ConfigFile {

    private String playersPath = null;
    private String outputFolder = null;
    private String header = null;
    private String footer = null;
    private String headerFinal = null;
    private String footerFinal = null;
    private String filePostfixFinal = null;
    private String currentSeason = null;
    private String otherSeason = null;
    private boolean seasonReports = true;
    private boolean finalReport = true;
    private final List<String> aggregators = new ArrayList<>();
    private final List<Season> seasons = new ArrayList<>();
    private final HashMap<String, ArrayList<String>> parameters = new HashMap<>();

    public static SeasonsManager readConfig(String configPath) {
        ConfigFile newInstance = new ConfigFile();
        Aggregator.setConfigFile(newInstance);
        Node node;
        String root;
        try {
            File inputFile = new File(configPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);
            document.getDocumentElement().normalize();
            root = document.getElementsByTagName("root").item(0).getTextContent();
            newInstance.outputFolder = document.getElementsByTagName("output").item(0).getTextContent();
            node = document.getElementsByTagName("players").item(0);
            newInstance.playersPath = (node == null) ? null : node.getTextContent();
            node = document.getElementsByTagName("header").item(0);
            newInstance.header = (node == null) ? null : node.getTextContent();
            node = document.getElementsByTagName("footer").item(0);
            newInstance.footer = (node == null) ? null : node.getTextContent();
            node = document.getElementsByTagName("headerFinal").item(0);
            newInstance.headerFinal = (node == null) ? null : node.getTextContent();
            node = document.getElementsByTagName("footerFinal").item(0);
            newInstance.footerFinal = (node == null) ? null : node.getTextContent();
            node = document.getElementsByTagName("filePostfixFinal").item(0);
            newInstance.filePostfixFinal = (node == null) ? null : node.getTextContent();
            node = document.getElementsByTagName("current").item(0);
            newInstance.currentSeason = (node == null) ? null : node.getTextContent();
            node = document.getElementsByTagName("other").item(0);
            newInstance.otherSeason = (node == null) ? null : node.getTextContent();

            node = document.getElementsByTagName("seasonReports").item(0);
            newInstance.seasonReports = (node == null) ? true : Boolean.getBoolean(node.getTextContent());
            node = document.getElementsByTagName("finalReport").item(0);
            newInstance.finalReport = (node == null) ? true : Boolean.getBoolean(node.getTextContent());

            if (System.getProperty("output") != null) {
                newInstance.outputFolder = System.getProperty("output");
            }
            if (System.getProperty("filePostfixFinal") != null) {
                newInstance.filePostfixFinal = System.getProperty("filePostfixFinal");
            }

            NodeList parameterNodes = document.getElementsByTagName("parameter");
            int sizeParameterNodes = parameterNodes.getLength();
            for (int i = 0; i < sizeParameterNodes; i++) {
                Node parameterNode = parameterNodes.item(i);
                if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) parameterNode;
                    String parameterName = element.getAttribute("name");
                    String parameterValue = parameterNode.getTextContent();
                    ArrayList<String> parameterArray = newInstance.parameters.get(parameterName);
                    if (parameterArray == null) {
                        parameterArray = new ArrayList<>();
                        newInstance.parameters.put(parameterName, parameterArray);
                    }
                    parameterArray.add(parameterValue);
                }
            }

            NodeList aggregatorNodes = document.getElementsByTagName("aggregator");
            int sizeAggregatorNodes = aggregatorNodes.getLength();
            for (int i = 0; i < sizeAggregatorNodes; i++) {
                Node aggregatorNode = aggregatorNodes.item(i);
                newInstance.aggregators.add(aggregatorNode.getTextContent());
            }

            NodeList seasonNodes = document.getElementsByTagName("season");
            int size = seasonNodes.getLength();
            for (int i = 0; i < size; i++) {
                Node seasonNode = seasonNodes.item(i);
                if (seasonNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) seasonNode;
                    String id = element.getAttribute("id");
                    String title = element.getAttribute("title");
                    String linktitle = element.getAttribute("linktitle");
                    String sort = element.getAttribute("sort");
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
                    newInstance.seasons.add(new Season(id, title, linktitle, sort, folders));
                }
            }
        } catch (IOException | ParserConfigurationException | DOMException | SAXException ex) {
            Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        SeasonsManager seasonsManager = new SeasonsManager(newInstance);
        String path = newInstance.playersPath;
        if (path != null) {
            if (!new File(path).exists()) {
                throw new RuntimeException("'" + path + "' файл не найден");
            }
            seasonsManager.addPlayersInfo(FootballXmlPlayersParser.parseFile(path));
        }

        for (Season season : newInstance.seasons) {
            seasonsManager.add(season,
                    newInstance.outputFolder + "stat_v" + season.getId() + ".html",
                    initAggregators(newInstance.aggregators.toArray(new String[0]))
            );
        }
        return seasonsManager;

    }

    private static Aggregator[] initAggregators(String... aggregatorNames) {
        if (aggregatorNames == null) {
            return null;
        }
        List<Aggregator> aggregatorsList = new ArrayList<>();
        for (String aggregatorName : aggregatorNames) {
            aggregatorsList.add(initAggregator(aggregatorName));
        }
        return aggregatorsList.toArray(new Aggregator[0]);
    }

    private static Aggregator initAggregator(String aggregatorName) {
        try {
            Class clazz = Class.forName(Aggregator.class.getPackageName() + ".aggregator." + aggregatorName + "Aggregator");
            return (Aggregator) clazz.newInstance();
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Неизвестное имя агрегатора: " + aggregatorName);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Невалидное имя агрегатора: " + aggregatorName);
        }
    }

    public void callBeforeAll() {
        callMethod("beforeAll");
    }

    public void callAfterAll() {
        callMethod("afterAll");
    }

    public void printFinalReport() {
        callMethod("printFinalReport");
    }

    public void drawFinalCharts() {
        callMethod("drawFinalCharts");
    }

    public void callMethod(String methodName) {
        if (aggregators == null) {
            return;
        }
        for (String aggregatorName : aggregators) {
            try {
                Class clazz = Class.forName(Aggregator.class.getPackageName() + ".aggregator." + aggregatorName + "Aggregator");
                Method method = clazz.getDeclaredMethod(methodName);
                method.invoke(null);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Неизвестное имя агрегатора: " + aggregatorName);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Невалидное имя агрегатора: " + aggregatorName);
            } catch (IllegalArgumentException | InvocationTargetException | SecurityException ex) {
                throw new RuntimeException("Невалидный агрегатор: " + aggregatorName);
            } catch (NoSuchMethodException ex) {
            }
        }
    }

    public String getPlayersPath() {
        return playersPath;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public String getHeaderFinal() {
        return headerFinal;
    }

    public String getFooterFinal() {
        return footerFinal;
    }

    public String getFilePostfixFinal() {
        return filePostfixFinal;
    }

    public String getCurrentSeason() {
        return currentSeason;
    }

    public String getOtherSeason() {
        return otherSeason;
    }

    public boolean isPrintSeasonReports() {
        return seasonReports;
    }

    public boolean isPrintFinalReport() {
        return finalReport;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public List<String> getAggregators() {
        return aggregators;
    }

    public boolean isParameterExist(String parameterName) {
        return getParameterValues(parameterName) != null;
    }

    public ArrayList<String> getParameterValues(String parameterName) {
        return parameters.get(parameterName);
    }

    public String getParameterValue(String parameterName) {
        ArrayList<String> parameterValues = getParameterValues(parameterName);
        return parameterValues == null ? null : parameterValues.get(0);
    }

}
