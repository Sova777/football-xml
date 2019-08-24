package ru.mojgorod.football.xml.aggregate;

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


import java.io.PrintStream;
import ru.mojgorod.football.xml.config.ConfigFile;
import ru.mojgorod.football.xml.library.FootballXmlPlayersInfo;
import ru.mojgorod.football.xml.library.FootballXmlReport;

/**
 *
 * @author sova
 */
public abstract class Aggregator {

    private static ConfigFile configFile;
    private static SeasonParameters seeasonParameters;
    private static PrintStream outFinalReport;

    public Aggregator() {
    }

    static void setSeasonParameters(SeasonParameters seeasonParameters) {
        Aggregator.seeasonParameters = seeasonParameters;
    }

    public static void setConfigFile(ConfigFile configFile) {
        Aggregator.configFile = configFile;
    }

    public static void setOutputFinalReport(PrintStream outFinalReport) {
        Aggregator.outFinalReport = outFinalReport;
    }

    public abstract void add(FootballXmlReport xmlReport);

    public void beforeSeason() {
    }

    public void afterSeason() {
    }

    public static void beforeAll() {
    }

    public static void afterAll() {
    }

    public static void printFinalReport() {
    }

    public static void drawFinalCharts() {
    }

    public void print() {
    }

    public void drawCharts() {
    }

    protected PrintStream getOutput() {
        return seeasonParameters.getOutput();
    }

    protected static PrintStream getOutputFinalReport() {
        return outFinalReport;
    }

    protected boolean isPlayerInfo() {
        return seeasonParameters.isPlayerInfo();
    }

    protected FootballXmlPlayersInfo getPlayerInfo(String key) {
        return seeasonParameters.getPlayerInfo(key);
    }

    protected static ConfigFile getConfigFile() {
        return configFile;
    }

    protected Season getSeason() {
        return seeasonParameters.getSeason();
    }

    protected static String getParameterValue(String parameterName) {
        String property = System.getProperty(parameterName);
        if (property != null) {
            return property;
        }
        return configFile.getParameterValue(parameterName);
    }

}
