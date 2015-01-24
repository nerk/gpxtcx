/*
 * Copyright (c) 2011-2015 Thomas Kern
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package de.kernwelt.gpxtcx;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tcx
{

    private List<TrackPoint> trackPoints;
    private Date dateTime;

    public Tcx() {
    }

    public void createTrackPoints(File input) throws IOException, ParserConfigurationException, org.xml.sax.SAXException, ParseException {

        Document document = Util.parse(input);

        trackPoints = new ArrayList<TrackPoint>();
        NodeList trackPointNodes = document.getElementsByTagName("Trackpoint");
        for (int i = 0; i < trackPointNodes.getLength(); i++) {
            Node trackPointNode = trackPointNodes.item(i);
            NodeList children = trackPointNode.getChildNodes();
            int heartRate = 0;
            long time = 0;
            for (int j = 0; j < children.getLength(); j++) {
                Node subTrackPt = children.item(j);
                if (subTrackPt.getNodeName().equals("HeartRateBpm")) {
                    Node child = Util.getChildByName(subTrackPt, "Value");
                    if (child != null) {
                        heartRate = Integer.valueOf(child.getFirstChild().getNodeValue());
                    }
                } else if (subTrackPt.getNodeName().equals("Time")) {
                    Node node = subTrackPt.getFirstChild();
                    time = Util.toTime(node.getNodeValue());
                }
            }

            if (heartRate > 0 && time > 0) {
                trackPoints.add(new TrackPoint(time, heartRate));
            }
        }

        // <Lap StartTime="2011-03-06T12:40:09Z">
        NodeList lap = document.getElementsByTagName("Lap");
        String startTime = lap.item(0).getAttributes().getNamedItem("StartTime").getNodeValue();
        dateTime = DatatypeConverter.parseDateTime(startTime).getTime();
    }

    public Date getStartTime() {
        return dateTime;
    }

    public int getHeartRateForTime(long time) {

        for (TrackPoint tp : trackPoints) {
            if (time >= tp.getTime() - 5000 && time < tp.getTime() + 5000) {
                return tp.getHeartRate();
            }
        }
        return 0;
    }

    public static class TrackPoint
    {
        private long time;
        private int  heartRate;

        public TrackPoint(long time, int heartRate) {
            this.time = time;
            this.heartRate = heartRate;
        }

        public long getTime() {
            return time;
        }

        public int getHeartRate() {
            return heartRate;
        }
    }
}
