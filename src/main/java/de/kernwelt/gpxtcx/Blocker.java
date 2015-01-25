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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Blocker extends JPanel implements AWTEventListener {
    private Blocker() {
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        if (event instanceof KeyEvent && event.getSource() instanceof Component) {
            ((KeyEvent) event).consume();
        }
    }

    public static void setBusy(JFrame frame, boolean busy) {
        if (!(frame.getRootPane().getGlassPane() instanceof Blocker)) {
            Blocker glassPane = new Blocker();
            glassPane.setOpaque(false);
            glassPane.setLayout(null);
            glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            glassPane.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    e.consume();
                }
            });
            frame.getRootPane().setGlassPane(glassPane);
        }

        Blocker gp = (Blocker)frame.getRootPane().getGlassPane();
        if (busy) {
            Toolkit.getDefaultToolkit().addAWTEventListener(gp, AWTEvent.KEY_EVENT_MASK);
        } else {
            Toolkit.getDefaultToolkit().removeAWTEventListener(gp);
        }

        gp.setVisible(busy);
    }
}
