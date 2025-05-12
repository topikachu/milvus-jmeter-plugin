package com.github.topikachu.jmeter.milvus.search;

import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.StringProperty;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

public class MilvusSearchSamplerGui extends AbstractSamplerGui {

    private JTextField endpointField;
    private JTextField tokenField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField collectionNameField;
    private JTextField filterField;
    private JTextField outputFieldsField;
    private JSpinner topKSpinner;
    private JTextArea queryVectorArea;

    public MilvusSearchSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel parameterPanel = new JPanel(new GridBagLayout());
        parameterPanel.setBorder(BorderFactory.createTitledBorder("Milvus Sampler Parameters"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        endpointField = new JTextField();
        addField(parameterPanel, gbc, "Endpoint:", endpointField, 0);

        tokenField = new JTextField();
        addField(parameterPanel, gbc, "Token:", tokenField, 1);

        usernameField = new JTextField();
        addField(parameterPanel, gbc, "Username:", usernameField, 2);

        passwordField = new JTextField();
        addField(parameterPanel, gbc, "Password:", passwordField, 3);

        collectionNameField = new JTextField();
        addField(parameterPanel, gbc, "Collection Name:", collectionNameField, 4);

        filterField = new JTextField();
        addField(parameterPanel, gbc, "Filter:", filterField, 5);

        outputFieldsField = new JTextField();
        addField(parameterPanel, gbc, "Output Fields:", outputFieldsField, 6);

        topKSpinner = new JSpinner(new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1));
        addField(parameterPanel, gbc, "Top K:", topKSpinner, 7);

        queryVectorArea = new JTextArea(5, 20);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        parameterPanel.add(new JLabel("Query Vector:"), gbc);
        gbc.gridy = 9;
        parameterPanel.add(new JScrollPane(queryVectorArea), gbc);

        mainPanel.add(parameterPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.NORTH);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure label does not expand vertically
        gbc.insets = new Insets(5, 5, 5, 2);
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1; // Allow field to expand horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure field expands horizontally
        gbc.insets = new Insets(5, 2, 5, 5);
        panel.add(field, gbc);
    }

    @Override
    public String getStaticLabel() {
        return "Milvus Search";
    }

    @Override
    public String getLabelResource() {
        return  getClass().getCanonicalName();
    }

    @Override
    public TestElement createTestElement() {
        MilvusSearchSampler sampler = new MilvusSearchSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement sampler) {
        super.modifyTestElement(sampler);
        sampler.setProperty(new StringProperty(MilvusSearchSampler.ENDPOINT, endpointField.getText()));
        sampler.setProperty(new StringProperty(MilvusSearchSampler.TOKEN, tokenField.getText()));
        sampler.setProperty(new StringProperty(MilvusSearchSampler.USERNAME, usernameField.getText()));
        sampler.setProperty(new StringProperty(MilvusSearchSampler.PASSWORD, passwordField.getText()));
        sampler.setProperty(new StringProperty(MilvusSearchSampler.COLLECTION_NAME, collectionNameField.getText()));
        sampler.setProperty(new StringProperty(MilvusSearchSampler.FILTER, filterField.getText()));
        sampler.setProperty(new StringProperty(MilvusSearchSampler.OUTPUT_FIELDS, outputFieldsField.getText()));
        sampler.setProperty(new StringProperty(MilvusSearchSampler.TOP_K, topKSpinner.getValue().toString()));
        sampler.setProperty(new StringProperty(MilvusSearchSampler.QUERY_VECTOR, queryVectorArea.getText()));
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        endpointField.setText(element.getPropertyAsString(MilvusSearchSampler.ENDPOINT));
        tokenField.setText(element.getPropertyAsString(MilvusSearchSampler.TOKEN));
        usernameField.setText(element.getPropertyAsString(MilvusSearchSampler.USERNAME));
        passwordField.setText(element.getPropertyAsString(MilvusSearchSampler.PASSWORD));
        collectionNameField.setText(element.getPropertyAsString("collectionName"));
        filterField.setText(element.getPropertyAsString("filter"));
        outputFieldsField.setText(element.getPropertyAsString("outputFields"));
        topKSpinner.setValue(Integer.parseInt(element.getPropertyAsString("topK")));
        queryVectorArea.setText(element.getPropertyAsString("queryVector"));
    }

    @Override
    public Collection<String> getMenuCategories() {
        return Arrays.asList(MenuFactory.SAMPLERS);
    }


}