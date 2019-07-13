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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
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

    protected int width = -1;
    protected int height = -1;
    protected double scaleX = -1;
    protected double scaleY = -1;
    private int stepY = 1;
    protected int offsetX = -1;
    protected int offsetY = -1;
    protected int offsetYBottom = -1;
    protected double min = -1;
    protected double max = -1;
    protected double minDraw = 0;
    protected double maxDraw = -1;
    protected int maxTitleLength = 0;
    protected int maxYLength = 0;
    protected int columns = 0;
    protected int fontSize = 10;
    protected int fontSizeTitle = 10;
    protected String copyright = null;
    protected String title = null;
    protected String outputFile = null;
    protected int maxNumbersAfterDot = 0;
    protected boolean isDisplayValueOnTop = true;
    protected int copyrightHeight = 0;
    protected final List<List<BarChartPoint>> data = new ArrayList<>();

    public static final Color COLOR_WHITE = new Color(255, 255, 255);
    public static final Color COLOR_RED = new Color(220, 57, 18);
    public static final Color COLOR_BLUE = new Color(51, 102, 204);
    public static final Color COLOR_BLACK = new Color(0, 0, 0);
    public static final Color COLOR_GRAY = new Color(160, 160, 160);
    public static final Color COLOR_GREEN = new Color(51, 204, 102);
    public static final Color COLOR_LIGHT_BLACK = new Color(49, 49, 100);

    private static final float DASH_ARRAY[] = {1.0f};
    protected static BasicStroke dashed =
            new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, DASH_ARRAY, 0.0f);

    public BarChart(int width, int height) {
        this.width = width;
        this.height = height;
        this.data.add(new ArrayList<>());
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

    public void setMinValue(double min) {
        this.minDraw = min;
    }

    public void setMaxNumbersAfterDot(int maxNumbersAfterDot) {
        this.maxNumbersAfterDot = maxNumbersAfterDot;
    }

    public void setDisplayValueOnTop(boolean isDisplayValueOnTop) {
        this.isDisplayValueOnTop = isDisplayValueOnTop;
    }

    public void draw() {
        if (data.size() == 1 && data.get(0).isEmpty()) {
            data.get(0).add(new BarChartPoint("             ", 1.0, COLOR_WHITE));
        }
        BufferedImage bi
                = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, fontSizeTitle);

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-90), 0, 0);
        Font verticalFont = DEFAULT_FONT.deriveFont(affineTransform);
        calculateConstants(g, DEFAULT_FONT, verticalFont);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        for (List<BarChartPoint> setData : data) {
            int x0 = -1;
            int y0 = -1;
            int i = 0;
            for (BarChartPoint point : setData) {
                Double value = point.getValue();
                Color color = point.getColor();
                if (color == null) {
                    g.setColor(COLOR_BLUE);
                } else {
                    g.setColor(color);
                }
                int x = getLocalX(i) + 1;
                int y = getLocalY(value);
                if (value < minDraw) {
                    y = -1;
                }
                drawPoint(g, x0, y0, x, y, (int)(scaleX - 1), (int)(scaleY * (value - minDraw)));
                x0 = x;
                y0 = y;
                i++;
            }
        }

        g.setColor(COLOR_BLACK);
        g.drawLine(getLocalX(0), getLocalY(minDraw), getLocalX(columns), getLocalY(minDraw));
        g.drawLine(getLocalX(0), getLocalY(minDraw), getLocalX(0), getLocalY(maxDraw));
        g.drawLine(getLocalX(columns), getLocalY(minDraw), getLocalX(columns), getLocalY(maxDraw));

        g.setColor(COLOR_GRAY);
        for (double j = minDraw + stepY; j <= maxDraw; j += stepY) {
            g.drawLine(getLocalX(0), getLocalY(j), getLocalX(columns), getLocalY(j));
        }

        g.setFont(verticalFont);
        int i = 0;
        for (BarChartPoint point : data.get(0)) {
            Double value = point.getValue();
            g.setColor(COLOR_BLACK);
            Rectangle2D bounds = g.getFontMetrics(verticalFont).getStringBounds(point.getTitle(), g);
            if (isDisplayValueOnTop) {
                g.drawString(point.getTitle(), getLocalX(i + 1) - ((int)scaleX - fontSize) / 2, getLocalY(value) - 1);
            } else {
                g.drawString(point.getTitle(), getLocalX(i + 1) - ((int)scaleX - fontSize) / 2, getLocalY(minDraw) + (int)bounds.getWidth() + 5);
            }
            i++;
        }

        g.setColor(COLOR_BLACK);
        g.setFont(DEFAULT_FONT);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(maxNumbersAfterDot);
        for (double j = minDraw; j <= maxDraw; j += stepY) {
            g.drawString(nf.format(j), 1, getLocalY(j));
            g.drawString(nf.format(j), getLocalX(columns) + 3, getLocalY(j));
        }

        Rectangle2D bounds = g.getFontMetrics(DEFAULT_FONT).getStringBounds(copyright, g);
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

    protected void drawPoint(Graphics2D g, int x0, int y0, int x, int y, int width, int height) {
        if (y > -1) {
            g.fillRect(x, y, width, height);
        }
    }

    protected int getLocalX(int x) {
        return (int) (x * scaleX + offsetX + maxTitleLength);
    }

    protected int getLocalX(double x) {
        return (int) (x * scaleX + offsetX + maxTitleLength);
    }

    protected int getLocalY(double y) {
        if (isDisplayValueOnTop) {
            return (int) (height - offsetYBottom - scaleY * (y - minDraw));
        }
        return (int) (height - offsetYBottom - copyrightHeight - scaleY * (y - minDraw));
    }

    public void addPoint(BarChartPoint point) {
        this.data.get(0).add(point);
    }

    public void addPoint(int setIndex, BarChartPoint point) {
        if (this.data.size() < setIndex + 1) {
            for (int i = this.data.size(); i <= setIndex; i++) {
                this.data.add(new ArrayList<>());
            }
        }
        this.data.get(setIndex).add(point);
    }

    public void addPoint(String title, Integer value, Color color) {
        addPoint(new BarChartPoint(title, value == null ? null : value.doubleValue(), color));
    }

    public void addPoint(String title, Double value, Color color) {
        addPoint(new BarChartPoint(title, value, color));
    }

    public void addPoint(int setIndex, String title, Double value, Color color) {
        addPoint(setIndex, new BarChartPoint(title, value, color));
    }

    public void addPoint(String title, Integer value) {
        addPoint(title, value == null ? null : value.doubleValue(), null);
    }

    public void addPoint(String title, Double value) {
        addPoint(title, value, null);
    }

    public void addPoint(int setIndex, String title, Double value) {
        addPoint(setIndex, title, value, null);
    }

    public void addPoint(Integer value) {
        addPoint(null, value == null ? null : value.doubleValue(), null);
    }

    public void addPoint(Double value) {
        addPoint(null, value, null);
    }

    public void addPoint(int setIndex, Double value) {
        addPoint(setIndex, null, value, null);
    }

    public void addPoint(Integer value, Color color) {
        addPoint(null, value == null ? null : value.doubleValue(), color);
    }

    public void addPoint(Double value, Color color) {
        addPoint(null, value, color);
    }

    public void addPoint(int setIndex, Double value, Color color) {
        addPoint(setIndex, null, value, color);
    }

    protected void reseteConstants() {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        maxDraw = Integer.MIN_VALUE;
        columns = 0;
        offsetX = 20;
        offsetY = fontSizeTitle * 5 / 2;
        maxTitleLength = 0;
    }

    protected void calculateConstants(Graphics2D g, Font font, Font verticalFont) {
        reseteConstants();

        Rectangle2D boundsCopyright = g.getFontMetrics(font).getStringBounds(copyright, g);
        copyrightHeight = (int)boundsCopyright.getHeight() + 10;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(maxNumbersAfterDot);
        for (List<BarChartPoint> setData : data) {
            for (BarChartPoint point : setData) {
                Double value = point.getValue();
                if (value != null) {
                    Rectangle2D bounds = g.getFontMetrics(font).getStringBounds(nf.format(point.getValue()), g);
                    Rectangle2D boundsVertical = g.getFontMetrics(verticalFont).getStringBounds(point.getTitle(), g);
                    double titleValueWidth = bounds.getWidth();
                    double titleVerticalWidth = boundsVertical.getWidth();
                    if (titleValueWidth > maxYLength) {
                        maxYLength = (int) titleValueWidth;
                        offsetYBottom = offsetY;
                    }
                    if (titleVerticalWidth > offsetYBottom) {
                        offsetYBottom = (int) titleVerticalWidth;
                    }
                    if (value < min) {
                        min = value;
                    }
                    if (value > max) {
                        max = value;
                    }
                }
            }
        }
        columns = data.get(0).size();
        if (isDisplayValueOnTop) {
            maxDraw = max + 2;
        } else {
            maxDraw = max + 1;
        }
        if (minDraw > 0 && minDraw > min) {
            min = minDraw;
        } else {
            min = 0;
        }
        offsetX = maxYLength;
        scaleX = (width - 2 * offsetX) / columns;
        if (isDisplayValueOnTop) {
            scaleY = (height - 2 * offsetY) / (maxDraw - min);
            offsetYBottom = offsetY;
        } else {
            scaleY = (height - offsetY - offsetYBottom - copyrightHeight) / (maxDraw - min);
        }
        if (scaleY < 10) {
            stepY = 10;
        }
    }

}
