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
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author sova
 */
public class BlockChart extends BarChart {

    public BlockChart(int width, int height) {
        super(width, height);
    }

    @Override
    public void draw() {
        if (data.size() == 1 && data.get(0).isEmpty()) {
            data.get(0).add(new BarChartPoint("             ", 1.0, COLOR_WHITE));
        }
        BufferedImage bi
                = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        final Font ARIAL_BLACK_FONT = new Font("Arial"/*Font.SANS_SERIF*/, Font.PLAIN, fontSize);
        final Font LABEL_FONT = ARIAL_BLACK_FONT.deriveFont(
                Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD)
        );
        final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, fontSizeTitle);
        Stroke defaultStroke = g.getStroke();

        calculateConstants(g, LABEL_FONT, null);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        int columnIndex = 0;
        for (List<BarChartPoint> setData : data) {
            int i = 0;
            for (BarChartPoint point : setData) {
                Double value = point.getValue();
                Color color = point.getColor();
                if (color == null) {
                    g.setColor(COLOR_BLUE);
                } else {
                    g.setColor(color);
                }
                g.fillRect(getLocalX(columnIndex) + 2, getLocalY(columns - i) + 2, (int)(scaleX) - 2, (int)(scaleY) - 3);
                g.setColor(COLOR_WHITE);
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(maxNumbersAfterDot);
                String labelValue = nf.format(value);
                Rectangle2D bounds = g.getFontMetrics().getStringBounds(labelValue, g);
                g.drawString(labelValue, (int)((getLocalX(columnIndex) + getLocalX(columnIndex + 1) - bounds.getWidth()) / 2), (int) (getLocalY(columns - i - 1) - (scaleY - fontSize) / 2) - 1);
                i++;
            }
            columnIndex++;
        }

        int i = 0;
        for (BarChartPoint point : data.get(0)) {
            if (i != 0 && i != columns) {
                if (i % 3 == 0) {
                    g.setColor(COLOR_GRAY);
                    g.setStroke(dashed);
                    g.drawLine(offsetX, getLocalY(columns - i), getLocalX(maxDraw), getLocalY(columns - i));
                    g.setStroke(defaultStroke);
                }
            }
            i++;            
        }

        g.setColor(COLOR_PURE_BLACK);
        g.drawLine(getLocalX(0), getLocalY(0), getLocalX(0), getLocalY(columns));
        g.drawLine(offsetX, getLocalY(0), offsetX, getLocalY(columns));
        g.drawLine(getLocalX(maxDraw), getLocalY(0), getLocalX(maxDraw), getLocalY(columns));
        g.drawLine(offsetX, getLocalY(0), getLocalX(maxDraw), getLocalY(0));
        g.drawLine(offsetX, getLocalY(columns), getLocalX(maxDraw), getLocalY(columns));

        g.setColor(COLOR_GRAY);
        for (int j = 0; j <= maxDraw; j++) {
            if (j % 3 == 0) {
                g.setStroke(defaultStroke);
            } else {
                g.setStroke(dashed);
            }
            g.drawLine(getLocalX(j), getLocalY(0), getLocalX(j), getLocalY(columns));
        }

        g.setColor(COLOR_LIGHT_BLACK);
        g.setFont(LABEL_FONT);
        i = 0;
        for (BarChartPoint point : data.get(0)) {
            g.drawString(point.getTitle(), offsetX + 4, (int)(getLocalY(columns - i - 1) - (scaleY - fontSize) / 2 - 2));
            i++;
        }

        g.setColor(COLOR_PURE_BLACK);
        g.setFont(DEFAULT_FONT);
        for (int j = 1; j <= maxDraw; j++) {
            String title = data.get(0).get(j - 1).getTitle();
            if (this.title.length() > 3) {
                title = title.substring(0, 3);
            }
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(title, g);
            g.drawString(title, (getLocalX(j - 1) + getLocalX(j) - (int) bounds.getWidth()) / 2, getLocalY(columns) - 4);
            g.drawString(title, (getLocalX(j - 1) + getLocalX(j) - (int) bounds.getWidth()) / 2, getLocalY(0) + (int) bounds.getHeight());
        }

        Rectangle2D bounds = g.getFontMetrics().getStringBounds(copyright, g);
        g.drawString(copyright, (int) (getLocalX(maxDraw) - bounds.getWidth()), height - 5);

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

    @Override
    protected void calculateConstants(Graphics2D g, Font font, Font verticalFont) {
        reseteConstants();
        g.setFont(font);
        for (List<BarChartPoint> setData : data) {
            for (BarChartPoint point : setData) {
                String titlePoint = point.getTitle();
                if (titlePoint != null) {
                    Rectangle2D bounds = g.getFontMetrics().getStringBounds(point.getTitle(), g);
                    double titlePointWidth = bounds.getWidth();
                    if (titlePointWidth > maxTitleLength) {
                        maxTitleLength = (int) titlePointWidth;
                    }
                }
//                Double value = point.getValue();
//                if (value != null) {
//                    if (value < min) {
//                        min = value;
//                    }
//                    if (value > max) {
//                        max = value;
//                    }
//                }
            }
        }
        columns = data.get(0).size();
        max = columns;
        maxTitleLength += 8;
        maxDraw = max;
        min = 0;
        offsetYBottom = offsetY;
        scaleY = (float)(height - 2 * offsetY) / columns;
        scaleX = (float)(width - 2 * offsetX - maxTitleLength) / (maxDraw - min);
    }

}
