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
package ru.mojgorod.football.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author sova
 */
public class BarChart {

    private int width = -1;
    private int height = -1;
    private int scaleX = -1;
    private int scaleY = -1;
    private int stepY = 1;
    private int offsetX = -1;
    private int offsetY = -1;
    private int min = -1;
    private int max = -1;
    private int maxDraw = -1;
    private int columns = 0;
    private int fontSize = 10;
    private int fontSizeTitle = 10;
    private String copyright = null;
    private String title = null;
    private String outputFile = null;
    private final List<BarChartPoint> data = new ArrayList<>();

    public static final Color COLOR_WHITE = new Color(255, 255, 255);
    public static final Color COLOR_RED = new Color(220, 57, 18);
    public static final Color COLOR_BLUE = new Color(51, 102, 204);
    public static final Color COLOR_BLACK = new Color(0, 0, 0);
    public static final Color COLOR_GRAY = new Color(160, 160, 160);

    public BarChart(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setFontSizeTitle(int fontSizeTitle) {
        this.fontSizeTitle = fontSizeTitle;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void draw() {
        BufferedImage bi
                = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, fontSizeTitle);

        calculateConstants();
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-90), 0, 0);
        Font verticalFont = DEFAULT_FONT.deriveFont(affineTransform);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        int i = 0;
        for (BarChartPoint point : data) {
            Integer value = point.getValue();
            Color color = point.getColor();
            if (color == null) {
                g.setColor(COLOR_BLUE);
            } else {
                g.setColor(color);
            }
            g.fillRect(getLocalX(i) + 1, getLocalY(value), scaleX - 1, scaleY * value);
            i++;
        }

        g.setColor(COLOR_BLACK);
        g.drawLine(getLocalX(0), getLocalY(0), getLocalX(columns), getLocalY(0));
        g.drawLine(getLocalX(0), getLocalY(0), getLocalX(0), getLocalY(maxDraw));
        g.drawLine(getLocalX(columns), getLocalY(0), getLocalX(columns), getLocalY(maxDraw));

        g.setColor(COLOR_GRAY);
        for (int j = stepY; j <= maxDraw; j += stepY) {
            g.drawLine(getLocalX(0), getLocalY(j), getLocalX(columns), getLocalY(j));
        }

        g.setFont(verticalFont);
        i = 0;
        for (BarChartPoint point : data) {
            Integer value = point.getValue();
            g.setColor(COLOR_BLACK);
            g.drawString(point.getTitle(), getLocalX(i + 1) - (scaleX - fontSize) / 2, getLocalY(value) - 1);
            i++;
        }

        g.setColor(COLOR_BLACK);
        g.setFont(DEFAULT_FONT);
        for (int j = 0; j <= maxDraw; j += stepY) {
            g.drawString(String.valueOf(j), 1, getLocalY(j));
            g.drawString(String.valueOf(j), getLocalX(columns) + 3, getLocalY(j));
        }

        Rectangle2D bounds = g.getFontMetrics().getStringBounds(copyright, g);
        g.drawString(copyright, (int) (getLocalX(i) - bounds.getWidth()), height - 5);

        g.setFont(TITLE_FONT);
        bounds = g.getFontMetrics().getStringBounds(title, g);
        g.drawString(title, (int) ((width - bounds.getWidth()) /2), (int) (bounds.getHeight() + 2));

        File outputfile = new File(outputFile);
        try {
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(BarChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getLocalX(int x) {
        return x * scaleX + offsetX;
    }

    private int getLocalY(int y) {
        return height - offsetY - scaleY * y;
    }

    public void addPoint(BarChartPoint point) {
        this.data.add(point);
    }

    public void addPoint(String title, Integer value, Color color) {
        addPoint(new BarChartPoint(title, value, color));
    }

    public void addPoint(String title, Integer value) {
        addPoint(title, value, null);
    }

    public void addPoint(Integer value) {
        addPoint(null, value, null);
    }

    public void addPoint(Integer value, Color color) {
        addPoint(null, value, color);
    }

    private void reseteConstants() {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        maxDraw = Integer.MIN_VALUE;
        columns = 0;
        offsetX = 20;
        offsetY = fontSizeTitle * 3 / 2;
    }

    private void calculateConstants() {
        reseteConstants();
        for (BarChartPoint point : data) {
            Integer value = point.getValue();
            if (value != null) {
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
            columns++;
        }
        maxDraw = max + 2;
        min = 0;
        scaleX = (width - 2 * offsetX) / columns;
        scaleY = (height - 2 * offsetY) / (maxDraw - min);
        if (scaleY < 10) {
            stepY = 10;
        }
    }

}
