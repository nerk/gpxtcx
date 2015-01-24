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

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            private Document gpxDoc;
            private Tcx trackPoints;
            private MainFrame frame;
            private Date gpxStartTime;
            private Date tcxStartTime;
            private long tcxTimeOffset = 0;

            public void run() {
                try {

                    try {
                        UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
                    } catch (Exception e) {
                    }

                    frame = new MainFrame();
                    frame.getSaveBtn().setEnabled(false);
                    frame.getBtnAlignTime().setEnabled(false);

                    frame.getReadGpxBtn().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            File gpxFile = Util.getFile(frame, false, ".gpx");
                            if (gpxFile != null) {
                                try {

                                    gpxDoc = Util.parse(gpxFile);

                                    gpxStartTime = getStartTime();
                                    if (gpxStartTime == null) {
                                        Util.showErrorMessage(frame, "Error reading file " + gpxFile.getAbsolutePath());
                                    } else {
                                        frame.getGpxStartTime().setText(gpxStartTime.toString());
                                        Util.fixSchema(gpxDoc);
                                        frame.getGpxFileField().setText(gpxFile.getAbsolutePath());
                                    }
                                } catch (Exception e1) {
                                    Util.showErrorMessage(frame, "Error reading file " + gpxFile.getAbsolutePath());
                                }
                            }
                            checkEnabledButtons();
                        }
                    });

                    frame.getReadTcxBtn().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            File tcxFile = Util.getFile(frame, false, ".tcx");
                            if (tcxFile != null) {
                                try {
                                    trackPoints = new Tcx();
                                    trackPoints.createTrackPoints(tcxFile);
                                    tcxStartTime = trackPoints.getStartTime();
                                    frame.getTcxStartTime().setText(tcxStartTime.toString());
                                    frame.getTcxFileField().setText(tcxFile.getAbsolutePath());
                                } catch (Exception e2) {
                                    Util.showErrorMessage(frame, "Error reading file " + tcxFile.getAbsolutePath());
                                }
                                checkEnabledButtons();
                            }
                        }
                    });

                    frame.getSaveBtn().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final File mergedFile = Util.getFile(frame, true, ".gpx");
                            if (mergedFile != null) {

                                boolean doCreate = true;
                                if (mergedFile.exists()) {
                                    doCreate = Util.showYesNoMessage(frame, "File already exists. Overwrite?");
                                }
                                if (doCreate) {
                                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                    SwingWorker worker = new SwingWorker<Boolean, Void>() {
                                        @Override
                                        public Boolean doInBackground() throws Exception {
                                            create(mergedFile);
                                            return true;
                                        }

                                        @Override
                                        public void done() {
                                            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                                            try {
                                                get();
                                                Util.showInfoMessage(frame, "Merged file '" + mergedFile.getAbsolutePath() + "' saved successfully!");
                                            } catch (InterruptedException ignore) {
                                                // ignored
                                            } catch (java.util.concurrent.ExecutionException e) {
                                                Throwable cause = e.getCause();
                                                String err = null;
                                                if (cause != null) {
                                                    err = cause.getMessage();
                                                } else {
                                                    err = e.getMessage();
                                                }
                                                Util.showErrorMessage(frame, err);
                                            }
                                        }
                                    };

                                    worker.execute();

                                }
                            }
                        }
                    });

                    frame.getBtnAlignTime().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            try {
                                tcxTimeOffset = gpxStartTime.getTime() - tcxStartTime.getTime();
                                frame.getTcxStartTime().setText(frame.getGpxStartTime().getText());
                            } catch (Exception e3) {
                                Util.showErrorMessage(frame, "Can't align timebase.");
                            }
                        }
                    });

                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * Enable/disable buttons
             */
            private void checkEnabledButtons() {
                boolean enabled = frame.getGpxFileField().getText().length() > 0 && frame.getTcxFileField().getText().length() > 0;
                frame.getSaveBtn().setEnabled(enabled);
                frame.getBtnAlignTime().setEnabled(enabled);
            }

            /**
             * Get time of the first track point frm GPX file
             *
             * @return time as a string
             */
            private Date getStartTime() {
                NodeList sections = gpxDoc.getElementsByTagName("trkpt");
                int nTr = sections.getLength();
                for (int i = 0; i < nTr; i++) {

                    Element trackPt = (Element) sections.item(i);
                    Node timeNode = Util.getChildByName(trackPt, "time");
                    if (timeNode != null) {
                        return DatatypeConverter.parseDateTime(timeNode.getFirstChild().getNodeValue()).getTime();
                    }
                }
                return null;
            }

            /**
             * Merge extension data into GPX
             *
             * @param outputFile
             * @throws TransformerConfigurationException
             * @throws TransformerException
             * @throws ParseException
             * @throws FileNotFoundException
             */
            private void create(File outputFile) throws TransformerConfigurationException, TransformerException, ParseException, FileNotFoundException {
                Document mergedDoc = (Document) gpxDoc.cloneNode(true);
                NodeList sections = mergedDoc.getElementsByTagName("trkpt");
                int nTr = sections.getLength();
                for (int i = 0; i < nTr; i++) {

                    Element trackPt = (Element) sections.item(i);
                    Node timeNode = Util.getChildByName(trackPt, "time");
                    if (timeNode != null) {
                        String st = timeNode.getFirstChild().getNodeValue();
                        long time = Util.toTime(st);
                        int heartRate = trackPoints.getHeartRateForTime(time + tcxTimeOffset);
                        if (heartRate > 0) {
                            Element extension = mergedDoc.createElement("gpxx:TrackPointExtension");
                            Element hr = mergedDoc.createElement("gpxx:hr");
                            hr.appendChild(mergedDoc.createTextNode(String.valueOf(heartRate)));
                            extension.appendChild(hr);
                            trackPt.appendChild(extension);
                        }
                    }
                }

                TransformerFactory tf = TransformerFactory.newInstance();

                Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                // we want to pretty format the XML output
                // transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                // "2");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));

                DOMSource source = new DOMSource(mergedDoc);
                StreamResult result = new StreamResult(out);
                transformer.transform(source, result);
                try {
                    out.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        });
    }
}