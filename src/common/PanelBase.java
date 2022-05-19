/*
 * Copyright (C) 2022 The Java Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package common;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * The {@PanelBase} is base class for panels used in the application.
 * 
 * @author olegshchepilov
 *
 */

public class PanelBase extends JPanel {
    public class ComponentListenerImpl implements ComponentListener {
        public ComponentListenerImpl(PanelBase panel) {
            this.panel = panel;
        }

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            panel.onResize();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }

        PanelBase panel = null;
    }

    public PanelBase() {
        addComponentListener(new ComponentListenerImpl(this));
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        for (PanelBar bar : bars) {
            bar.paint(graphics);
        }
    }

    // If a child class wants to process key events, it has to return non-null
    // object
    public KeyListener keyListener() {
        return null;
    }

    // If a child class wants to process mouse events, it has to return non-null
    // object
    public MouseListener mouseListener() {
        return null;
    }

    // If a child class wants to process mouse motion, it has to return non-null
    // object
    public MouseMotionListener mouseMotionListener() {
        return null;
    }

    // Calls when the component has been resized
    public void onResize() {
        updateBarPositions();
    }

    // Adds bar to array of bars
    protected void addBar(PanelBar bar) {
        if (bar != null) {
            bars.add(bar);
        }
        updateBarPositions();
    }

    // Returns rectangle of bar. This method has to be defined by an inheritor
    protected Rectangle calcBarArea(PanelBar bar) {
        return null;
    }

    // Updates all bars
    protected void updateBarPositions() {
        for (PanelBar bar : bars) {
            bar.updatePostition(calcBarArea(bar));
        }

    }

    protected List<PanelBar> bars = new ArrayList<PanelBar>();

    private static final long serialVersionUID = 1L;
}
