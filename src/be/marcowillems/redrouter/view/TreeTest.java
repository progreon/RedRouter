/*
 * Copyright (C) 2016 Marco Willems
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package be.marcowillems.redrouter.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author Marco Willems
 */
public class TreeTest extends JFrame {

    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;

    private JTree tree;

    public TreeTest() throws HeadlessException {        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        //create the child nodes
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");

        vegetableNode.add(new DefaultMutableTreeNode("Capsicum"));
        vegetableNode.add(new DefaultMutableTreeNode("Carrot"));
        vegetableNode.add(new DefaultMutableTreeNode("Tomato"));
        vegetableNode.add(new DefaultMutableTreeNode("Potato"));

        fruitNode.add(new DefaultMutableTreeNode("Banana"));
        fruitNode.add(new DefaultMutableTreeNode("Mango"));
        fruitNode.add(new DefaultMutableTreeNode("Apple"));
        fruitNode.add(new DefaultMutableTreeNode("Grapes"));
        fruitNode.add(new DefaultMutableTreeNode("Orange"));

        //add the child nodes to the root node
        root.add(vegetableNode);
        root.add(fruitNode);

        //create the tree by passing in the root node
        tree = new JTree(root);
//        tree.setShowsRootHandles(true);
//        tree.setRootVisible(false);
        tree.setCellRenderer(new MyRenderer());
//        tree.setEditable(true);
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler());
        add(new JScrollPane(tree));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JTree Example");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private class TreeTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferSupport support) {
            if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)
                    || !support.isDrop()) {
                return false;
            }

            JTree.DropLocation dropLocation
                    = (JTree.DropLocation) support.getDropLocation();
            return dropLocation.getPath() != null;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                // Make up a node array of copies for transfer and
                // another for/of the nodes that will be removed in
                // exportDone after a successful drop.
                List<DefaultMutableTreeNode> copies = new ArrayList<>();
                List<DefaultMutableTreeNode> toRemove = new ArrayList<>();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
                DefaultMutableTreeNode copy = new DefaultMutableTreeNode(node);
                copies.add(copy);
                toRemove.add(node);
                for (int i = 1; i < paths.length; i++) {
                    DefaultMutableTreeNode next = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                    // Do not allow higher level nodes to be added to list.
                    if (next.getLevel() < node.getLevel()) {
                        break;
                    } else if (next.getLevel() > node.getLevel()) {  // child node
                        copy.add(new DefaultMutableTreeNode(next));
                        // node already contains child
                    } else {                                        // sibling
                        copies.add(new DefaultMutableTreeNode(next));
                        toRemove.add(next);
                    }
                }
                DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
                nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
                return new NodesTransferable(nodes);
            }
            return null;
        }

        @Override
        public boolean importData(TransferSupport support) {
            return true;
        }

        public TreeTransferHandler() {
        }
    }

    public class NodesTransferable implements Transferable {

        DefaultMutableTreeNode[] nodes;

        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return nodes;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }

    private class MyRenderer extends JPanel implements TreeCellRenderer {

        public MyRenderer() {
            this.setBackground(Color.LIGHT_GRAY);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            this.removeAll();
            add(new JLabel(value.toString() + " [" + userObject.getClass().getName() + "]"));
            if (selected) {
                this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            } else {
                this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
            return this;
        }

    }

    public static void main(String[] args) {
        new TreeTest();
    }

}
