package com.github.topikachu.jmeter.milvus.search;

import javax.swing.*;

public class MilvusSamplerGuiTest {
    public static void main(String[] args) {
        // Set the look and feel to match the system's native look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a frame to hold the GUI
        JFrame frame = new JFrame("Milvus Sampler GUI Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create an instance of your GUI component
        MilvusSearchSamplerGui gui = new MilvusSearchSamplerGui();

        // Add the GUI component to the frame
        frame.getContentPane().add(gui);

        // Set the frame size and make it visible
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}