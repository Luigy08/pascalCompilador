/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unitec.compiladores;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.tree.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author elco45
 */
public class XMLTreeView {

    private SAXTreeBuilder saxTree = null;

    public XMLTreeView(JFrame frame, String file) {
        frame.getContentPane().setLayout(new BorderLayout());
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(file);

        saxTree = new SAXTreeBuilder(top);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new InputSource(new FileInputStream(file)), saxTree);
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(saxTree.getTree());
        expandAllNodes(tree, 0, tree.getRowCount());
        JScrollPane scrollPane = new JScrollPane(tree);

        frame.getContentPane().add("Center", scrollPane);
        frame.setVisible(true);

    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
}

class SAXTreeBuilder extends DefaultHandler {

    private DefaultMutableTreeNode currentNode = null;
    private DefaultMutableTreeNode previousNode = null;
    private DefaultMutableTreeNode rootNode = null;

    public SAXTreeBuilder(DefaultMutableTreeNode root) {
        rootNode = root;
    }

    public void startDocument() {
        currentNode = rootNode;
    }

    public void endDocument() {
    }

    public void characters(char[] data, int start, int end) {
        String str = new String(data, start, end);
        if (!str.equals("") && Character.isLetter(str.charAt(0))) {
            currentNode.add(new DefaultMutableTreeNode(str));
        }
    }

    public void startElement(String uri, String qName, String lName, Attributes atts) {
        previousNode = currentNode;
        currentNode = new DefaultMutableTreeNode(lName);
        // Add attributes as child nodes //
        attachAttributeList(currentNode, atts);
        previousNode.add(currentNode);
    }

    public void endElement(String uri, String qName, String lName) {
        if (currentNode.getUserObject().equals(lName)) {
            currentNode = (DefaultMutableTreeNode) currentNode.getParent();
        }
    }

    public DefaultMutableTreeNode getTree() {
        return rootNode;
    }

    private void attachAttributeList(DefaultMutableTreeNode node, Attributes atts) {
        for (int i = 0; i < atts.getLength(); i++) {
            String name = atts.getLocalName(i);
            String value = atts.getValue(name);
            node.add(new DefaultMutableTreeNode(name + " = " + value));
        }
    }

}
