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

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame
{

    private JPanel     contentPane;
    private JTextField gpxFileField;
    private JTextField tcxFileField;
    private JButton    readGpxBtn;
    private JButton    readTcxBtn;
    private JSeparator separator;
    private JLabel     lblNewLabel;
    private JLabel     lblNewLabel_1;
    private JSeparator separator_1;
    private JButton    saveBtn;
    private JTextField gpxStartTime;
    private JTextField tcxStartTime;
    private JButton    btnAlignTime;

    /**
     * Show the frame. Testing only!
     * 
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable()
        {
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.pack();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    */

    /**
     * Create the frame.
     */
    public MainFrame() {
        setTitle("GPX/TCX Merge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setBounds(100, 100, 490, 353);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.PREF_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.PREF_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("left:default:grow"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.PREF_COLSPEC, }, new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC, FormSpecs.MIN_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.MIN_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.PREF_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.PREF_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.PREF_ROWSPEC, }));

        JLabel lblGpxFile = new JLabel("GPX File:");
        contentPane.add(lblGpxFile, "1, 3, right, default");

        gpxFileField = new JTextField();
        gpxFileField.setEditable(false);
        contentPane.add(gpxFileField, "3, 3, 4, 1, fill, default");
        gpxFileField.setColumns(10);

        readGpxBtn = new JButton("Read...");
        contentPane.add(readGpxBtn, "10, 3");

        JLabel lblTcxFile = new JLabel("TCX File:");
        contentPane.add(lblTcxFile, "1, 6, right, default");

        tcxFileField = new JTextField();
        tcxFileField.setEditable(false);
        contentPane.add(tcxFileField, "3, 6, 4, 1, fill, default");
        tcxFileField.setColumns(10);

        readTcxBtn = new JButton("Read...");
        contentPane.add(readTcxBtn, "10, 6");

        separator = new JSeparator();
        contentPane.add(separator, "1, 10, 10, 1, fill, default");

        lblNewLabel = new JLabel("GPX Start Time:");
        contentPane.add(lblNewLabel, "1, 12, right, default");

        gpxStartTime = new JTextField();
        gpxStartTime.setEditable(false);
        contentPane.add(gpxStartTime, "3, 12, left, default");
        gpxStartTime.setColumns(20);

        lblNewLabel_1 = new JLabel("TCX Start Time:");
        contentPane.add(lblNewLabel_1, "1, 16, right, default");

        tcxStartTime = new JTextField();
        tcxStartTime.setEditable(false);
        contentPane.add(tcxStartTime, "3, 16, left, default");
        tcxStartTime.setColumns(20);

        btnAlignTime = new JButton("Align Timebase");
        contentPane.add(btnAlignTime, "5, 16");

        separator_1 = new JSeparator();
        contentPane.add(separator_1, "1, 18, 10, 1");

        saveBtn = new JButton("Merge & Save");
        contentPane.add(saveBtn, "10, 20");
    }

    public JTextField getGpxFileField() {
        return gpxFileField;
    }

    public JTextField getTcxFileField() {
        return tcxFileField;
    }

    public JButton getReadGpxBtn() {
        return readGpxBtn;
    }

    public JButton getReadTcxBtn() {
        return readTcxBtn;
    }

    public JTextField getTcxStartTime() {
        return tcxStartTime;
    }

    public JTextField getGpxStartTime() {
        return gpxStartTime;
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }

    public JButton getBtnAlignTime() {
        return btnAlignTime;
    }
}
