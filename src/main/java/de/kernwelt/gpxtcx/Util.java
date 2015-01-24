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

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * 
 * @author Thomas Kern
 * 
 */
public class Util
{
    private static File       currentDir;

    private Util() {
    }

    public static Document parse(File input) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setCoalescing(true); // Convert CDATA to Text nodes
        factory.setNamespaceAware(false); // No namespaces: this is default
        factory.setValidating(false); // Don't validate DTD: also default

        DocumentBuilder parser = factory.newDocumentBuilder();

        return parser.parse(input);
    }

    public static Node getChildByName(Node node, String name) {
        NodeList children = node.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);
            if (child.getNodeName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public static void fixSchema(Document gpxDoc) {

        // creator="GPS-Track-Analyse.NET 5.0.1.0" version="1.1"
        // xmlns="http://www.topografix.com/GPX/1/1"
        // xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        // xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd"
        // xmlns:gpxx="http://www.gps-freeware.de/xmlschemas/TrackPointExtension/v1">

        Element gpxNode = (Element) gpxDoc.getElementsByTagName("gpx").item(0);

        Attr attr = gpxDoc.createAttribute("xmlns");
        attr.setNodeValue("http://www.topografix.com/GPX/1/1");
        gpxNode.setAttributeNode(attr);

        attr = gpxDoc.createAttribute("xmlns:xsi");
        attr.setNodeValue("http://www.w3.org/2001/XMLSchema-instance");
        gpxNode.setAttributeNode(attr);

        attr = gpxDoc.createAttribute("creator");
        attr.setNodeValue("gpxtcx 1.0");
        gpxNode.setAttributeNode(attr);

        attr = gpxDoc.createAttribute("version");
        attr.setNodeValue("1.0");
        gpxNode.setAttributeNode(attr);

        attr = gpxDoc.createAttribute("xsi:schemaLocation");
        attr.setNodeValue("http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
        gpxNode.setAttributeNode(attr);

        attr = gpxDoc.createAttribute("xmlns:gpxx");
        attr.setNodeValue("http://www.gps-freeware.de/xmlschemas/TrackPointExtension/v1");
        gpxNode.setAttributeNode(attr);

    }

    public static long toTime(String st) throws ParseException {
        return DatatypeConverter.parseDateTime(st).getTimeInMillis();
    }

    public static void showInfoMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showYesNoMessage(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Warnung", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static File getFile(Component parent, boolean save, String suffix) {
        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileHidingEnabled(false);
        fc.setFileFilter(new Filter(suffix));

        if (currentDir != null) {
            fc.setCurrentDirectory(currentDir);
        }

        int returnVal = 0;
        if (save) {
            returnVal = fc.showSaveDialog(parent);
        } else {
            returnVal = fc.showOpenDialog(parent);
        }

        File selectedFile = fc.getSelectedFile();
        if (selectedFile != null)
            currentDir = selectedFile.getParentFile();

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fs = fc.getSelectedFile();
            if (save && !fs.getName().endsWith(suffix)) {
                return new File(fs.getAbsoluteFile() + suffix);
            }
            return fs;
        } else {
            return null;
        }
    }

    static class Filter extends FileFilter
    {

        private String suffix;

        public Filter(String suffix) {
            this.suffix = suffix;
        }

        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            if (extension != null) {
                if (extension.equals(suffix)) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        // The description of this filter
        public String getDescription() {
            return suffix;
        }

        public String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i).toLowerCase();
            }
            return ext;
        }
    }
}
