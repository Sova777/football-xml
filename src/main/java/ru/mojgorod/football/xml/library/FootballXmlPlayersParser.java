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
import ru.mojgorod.football.xml.aggregate.PlayersManager;

/**
 *
 * @author sova
 */
public class FootballXmlPlayersParser {
    
    public static PlayersManager parseFile(String file) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            PlayersManager playersManager = new PlayersManager();
            Node root = document.getDocumentElement();
            NodeList subNodes = root.getChildNodes();
            int size = subNodes.getLength();
            for (int i = 0; i < size; i++) {
                Node node = subNodes.item(i);
                switch (node.getNodeType()) {
                    case Node.ELEMENT_NODE:
                        String nodeName = node.getNodeName();
                        switch (nodeName) {
                            case "player":
                                tagPlayer(playersManager, node);
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
            return playersManager;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(FootballXmlPlayersParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void tagPlayer(PlayersManager playersManager, Node node) {
        FootballXmlPlayersInfo player = new FootballXmlPlayersInfo();
        NamedNodeMap attributes = node.getAttributes();
        int attributesSize = attributes.getLength();
        for (int j = 0; j < attributesSize; j++) {
            Node attributeNode = attributes.item(j);
            String attributeValue = attributeNode.getNodeValue();
            switch (attributeNode.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    switch (attributeNode.getNodeName()) {
                        case "id":
                            player.setId(attributeValue);
                            break;
                        case "name":
                            player.setName(attributeValue);
                            break;
                        case "fullname":
                            player.setFullname(attributeValue);
                            break;
                        case "birthday":
                            player.setBirthday(attributeValue);
                            break;
                        case "country":
                            player.setCountry(attributeValue);
                            break;
                        case "position":
                            player.setPosition(attributeValue);
                            break;
                        case "height":
                            player.setHeight(attributeValue);
                            break;
                        case "weight":
                            player.setWeight(attributeValue);
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
        playersManager.addPlayer(player);
    }

    private static void logUnknownAttributeNode(Node attributeNode) {
        Logger.getLogger(FootballXmlPlayersParser.class.getName()).log(
                Level.FINEST, "{0} = {1}, ", new Object[]{attributeNode.getNodeName(), attributeNode.getNodeValue()}
        );
    }

    private static void logUnknownNode(Node node) {
        Logger.getLogger(FootballXmlPlayersParser.class.getName()).log(
                Level.FINEST, "{0} = {1}, ", new Object[]{node.getNodeName(), node.getNodeValue()}
        );
    }

}
