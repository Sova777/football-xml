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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author sova
 */
public class FootballXmlParser {

    public static FootballXmlReport parseFile(String file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        FootballXmlReport xmlReport = new FootballXmlReport();
        xmlReport.setCanceled(false);
        Node root = document.getDocumentElement();
        NodeList subNodes = root.getChildNodes();
        int size = subNodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = subNodes.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String nodeName = node.getNodeName();
                    switch (nodeName) {
                        case "match":
                            tagMatch(xmlReport, node);
                            break;
                        case "team1":
                            tagTeam1(xmlReport, node);
                            break;
                        case "team2":
                            tagTeam2(xmlReport, node);
                            break;
                        case "score":
                            tagScore(xmlReport, node);
                            break;
                        case "date":
                            tagDate(xmlReport, node);
                            break;
                        case "stadium":
                            tagStadium(xmlReport, node);
                            break;
                        case "player1":
                            tagPlayer1(xmlReport, node);
                            break;
                        case "player2":
                            tagPlayer2(xmlReport, node);
                            break;
                        case "event":
                            tagEvent(xmlReport, node);
                            break;
                        case "coach1":
                            tagCoach1(xmlReport, node);
                            break;
                        case "coach2":
                            tagCoach2(xmlReport, node);
                            break;
                        case "referee":
                            tagReferee(xmlReport, node);
                            break;
                        case "text":
                            tagText(xmlReport, node);
                            break;
                        case "canceled":
                            tagCanceled(xmlReport, node);
                            break;
                        default:
                            logUnknownNode(node);
                            break;
                    }
                    break;
                default:
                    logUnknownNode(node);
                    break;
            }
        }
        return xmlReport;
    }

    private static void tagMatch(FootballXmlReport xmlReport, Node node) {
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            xmlReport.setMatchid(attributeValue);
                            break;
                        case "tournament":
                            xmlReport.setTournament(attributeValue);
                            break;
                        case "round":
                            xmlReport.setRound(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagTeam1(FootballXmlReport xmlReport, Node node) {
        xmlReport.setTeam1(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            xmlReport.setTeamId1(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagTeam2(FootballXmlReport xmlReport, Node node) {
        xmlReport.setTeam2(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            xmlReport.setTeamId2(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagScore(FootballXmlReport xmlReport, Node node) {
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "goals1":
                            xmlReport.setGoals1(attributeValue);
                            break;
                        case "goals2":
                            xmlReport.setGoals2(attributeValue);
                            break;
                        case "extra1":
                            xmlReport.setExtra1(attributeValue);
                            break;
                        case "extra2":
                            xmlReport.setExtra2(attributeValue);
                            break;
                        case "penalties1":
                            xmlReport.setPenalties1(attributeValue);
                            break;
                        case "penalties2":
                            xmlReport.setPenalties2(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagDate(FootballXmlReport xmlReport, Node node) {
        xmlReport.setDate(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "time":
                            xmlReport.setTime(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagStadium(FootballXmlReport xmlReport, Node node) {
        xmlReport.setStadium(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            xmlReport.setStadiumId(attributeValue);
                            break;
                        case "city":
                            xmlReport.setStadiumCity(attributeValue);
                            break;
                        case "attendance":
                            xmlReport.setStadiumAttendance(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagPlayer1(FootballXmlReport xmlReport, Node node) {
        FootballXmlPlayer player1 = new FootballXmlPlayer(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            player1.setId(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
        xmlReport.addPlayer1(player1);
    }

    private static void tagPlayer2(FootballXmlReport xmlReport, Node node) {
        FootballXmlPlayer player2 = new FootballXmlPlayer(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            player2.setId(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
        xmlReport.addPlayer2(player2);
    }

    private static void tagEvent(FootballXmlReport xmlReport, Node node) {
        FootballXmlEvent event = new FootballXmlEvent();
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "time":
                            event.setTime(attributeValue);
                            break;
                        case "type":
                            event.setType(attributeValue);
                            break;
                        case "comment":
                            event.setComment(attributeValue);
                            break;
                        case "team":
                            event.setTeam(attributeValue);
                            break;
                        case "playerid":
                            event.setPlayerId(attributeValue);
                            break;
                        case "player":
                            event.setPlayer(attributeValue);
                            break;
                        case "playerid2":
                            event.setPlayerId2(attributeValue);
                            break;
                        case "player2":
                            event.setPlayer2(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
        xmlReport.addEvent(event);
    }

    private static void tagCoach1(FootballXmlReport xmlReport, Node node) {
        xmlReport.setCoach1(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            xmlReport.setCoachId1(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagCoach2(FootballXmlReport xmlReport, Node node) {
        xmlReport.setCoach2(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            xmlReport.setCoachId2(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagReferee(FootballXmlReport xmlReport, Node node) {
        xmlReport.setReferee(node.getTextContent());
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            xmlReport.setRefereeId(attributeValue);
                            break;
                        case "city":
                            xmlReport.setRefereeCity(attributeValue);
                            break;
                        case "country":
                            xmlReport.setRefereeCountry(attributeValue);
                            break;
                        default:
                            logUnknownAttributeNode(attributeNode);
                            break;
                    }
                    break;
                default:
                    logUnknownAttributeNode(attributeNode);
                    break;
            }
        }
    }

    private static void tagText(FootballXmlReport xmlReport, Node node) {
        xmlReport.setText(node.getTextContent());
    }

    private static void tagCanceled(FootballXmlReport xmlReport, Node node) {
        xmlReport.setCanceled(true);
    }

    private static void logUnknownAttributeNode(Node attributeNode) {
        Logger.getLogger(FootballXmlParser.class.getName()).log(
                Level.FINEST, "{0} = {1}, ", new Object[]{attributeNode.getNodeName(), attributeNode.getNodeValue()}
        );
    }

    private static void logUnknownNode(Node node) {
        Logger.getLogger(FootballXmlParser.class.getName()).log(
                Level.FINEST, "{0} = {1}, ", new Object[]{node.getNodeName(), node.getNodeValue()}
        );
    }

}
