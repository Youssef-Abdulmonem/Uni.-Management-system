package org.example;

import javax.swing.*;

public abstract class Frame {
    public static JFrame basicFrame(String title, int width, int height, boolean closeAction) {
        JFrame frame;

        frame = new JFrame(title);
        frame.setSize(width, height);
        if (closeAction) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
        frame.setLayout(null);

        return frame;
    }
}
