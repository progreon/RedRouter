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
package be.marcowillems.redrouter.view.route;

import be.marcowillems.redrouter.route.RouteOr;
import be.marcowillems.redrouter.route.RouteBattle;
import be.marcowillems.redrouter.route.RouteDirections;
import be.marcowillems.redrouter.route.Route;
import be.marcowillems.redrouter.route.RouteEncounter;
import be.marcowillems.redrouter.route.RouteShopping;
import be.marcowillems.redrouter.route.RouteSwapPokemon;
import be.marcowillems.redrouter.route.RouteEntry;
import be.marcowillems.redrouter.route.RouteSection;
import be.marcowillems.redrouter.route.RouteGetPokemon;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Marco Willems
 */
public class RouteTree extends JTree {

    public final Route route;

    private boolean isEditMode = false;
    public final Color backgroundColor = new Color(225, 225, 175);
    public final Color nodeBackgroundColor = new Color(200, 150, 75);
    public final Color nodeSelectedColor = new Color(170, 125, 65);
    public final Color nodeBorderColor = new Color(115, 55, 45);
    // TODO: icons

    public RouteTree(Route route) {
        super();
        this.route = route;
//        this.setModel(new RouteTreeModel(route));
        this.setBackground(backgroundColor);
        this.setUI(new RouteTreeUI());
        RouteTreeNodeRenderer renderer = new RouteTreeNodeRenderer();
        this.setCellRenderer(renderer);
        this.setEditable(true);
        MyDefaultCellEditor editor = new MyDefaultCellEditor(this, renderer);
        this.setCellEditor(editor);
        this.addTreeSelectionListener(editor);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        initTree();
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }

    public void refresh() {
        stopEditing();
        update((TreeNode) getModel().getRoot());
    }

    public boolean isEditMode() {
        return this.isEditMode;
    }

    public void setEditMode(boolean isEditMode) {
        if (this.isEditMode != isEditMode) {
            this.isEditMode = isEditMode;
            refresh();
        }
    }

    private void initTree() {
        RouteEntryTreeNode root = newRouteEntryTreeNode(this, route);
        if (route.hasChildren()) {
            for (RouteEntry re : route.getChildren()) {
                addTreeNode(root, re);
            }
        }
        ((DefaultTreeModel) this.getModel()).setRoot(root);
    }

    private void addTreeNode(RouteEntryTreeNode parent, RouteEntry entry) {
        RouteEntryTreeNode newNode = newRouteEntryTreeNode(this, entry);
        if (entry.hasChildren()) {
            for (RouteEntry re : entry.getChildren()) {
                addTreeNode(newNode, re);
            }
        }
        parent.add(newNode);
    }

    private RouteEntryTreeNode newRouteEntryTreeNode(RouteTree tree, RouteEntry entry) {
        if (entry instanceof RouteBattle) {
            return new RouteBattleTreeNode(tree, (RouteBattle) entry);
        } else if (entry instanceof RouteDirections) {
            return new RouteDirectionsTreeNode(tree, (RouteDirections) entry);
        } else if (entry instanceof RouteEncounter) {
            return new RouteEncounterTreeNode(tree, (RouteEncounter) entry);
        } else if (entry instanceof RouteGetPokemon) {
            return new RouteGetPokemonTreeNode(tree, (RouteGetPokemon) entry);
        } else if (entry instanceof RouteOr) {
            return new RouteOrTreeNode(tree, (RouteOr) entry);
        } else if (entry instanceof RouteSection) {
            return new RouteSectionTreeNode(tree, (RouteSection) entry);
        } else if (entry instanceof RouteShopping) {
            return new RouteShoppingTreeNode(tree, (RouteShopping) entry);
        } else if (entry instanceof RouteSwapPokemon) {
            return new RouteSwapPokemonTreeNode(tree, (RouteSwapPokemon) entry);
        } else {
            return null;
        }
    }

    private void update(TreeNode node) {
        ((DefaultTreeModel) getModel()).nodeChanged(node);
        for (int i = 0; i < node.getChildCount(); i++) {
            update(node.getChildAt(i));
        }
    }

    private class RouteTreeUI extends BasicTreeUI {

        public RouteTreeUI() {
            this.setHashColor(nodeBorderColor);
        }

    }

    private class RouteTreeNodeRenderer extends DefaultTreeCellRenderer {

        public RouteTreeNodeRenderer() {
            super();
//            this.setBackgroundSelectionColor(nodeSelectedColor);
//            this.setBackgroundNonSelectionColor(nodeBackgroundColor);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (value instanceof RouteEntryTreeNode) {
                RouteEntryTreeNode node = (RouteEntryTreeNode) value;
                int preferredWidth = 400;
                if (tree.getParent() != null) {
                    int delta = this.getIcon().getIconWidth() + this.getIconTextGap();
                    // parent = jviewport, parent.parent = jscrollpane
                    preferredWidth = tree.getParent().getParent().getWidth() - (delta * node.getLevel()) - 20;
                }
                return node.getRender(new RenderSettings(preferredWidth, selected, expanded, leaf, row, hasFocus));
            } else {
                return c;
            }
        }

    }

    private class MyDefaultCellEditor extends DefaultTreeCellEditor {

        public MyDefaultCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
        }

        @Override
        public boolean isCellEditable(EventObject event) {
            boolean editable = false;
            if (event != null && event.getSource() instanceof RouteTree && event instanceof MouseEvent) {
                setTree((RouteTree) event.getSource());
                int row = tree.getRowForLocation(
                        ((MouseEvent) event).getX(),
                        ((MouseEvent) event).getY());
                if (!tree.getSelectionModel().isRowSelected(row)) {
                    tree.setSelectionRow(row);
                }
                lastRow = tree.getLeadSelectionRow();
                if (tree.getSelectionModel().isRowSelected(row)) {
                    editable = true;
                }
            }
            if (event == null) {
                editable = true;
            }
            if (!realEditor.isCellEditable(event)) {
                return false;
            }
            if (editable) {
                prepareForEditing();
            }
            return editable;
        }

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            return this.renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
        }

    }

//    private class RouteTreeModel implements TreeModel {
//
//        private Route route;
//        private final List<TreeModelListener> listeners;
//
//        public RouteTreeModel(Route route) {
//            this.route = route;
//            listeners = new ArrayList<>();
//        }
//
//        public void setRoute(Route route) {
//            Route oldRoute = this.route;
//            this.route = route;
//            for (TreeModelListener l : listeners) {
//                l.treeStructureChanged(new TreeModelEvent(this, new Object[]{oldRoute}));
//            }
//        }
//
//        public int getLevel(Object node) {
//            RouteEntry re = (RouteEntry) node;
//            int level = 0;
//            while (re.parent != null) {
//                level++;
//                re = re.parent;
//            }
//            return level;
//        }
//
//        public void refresh() {
//            for (TreeModelListener l : listeners) {
//                l.treeStructureChanged(new TreeModelEvent(this, new Object[]{route}));
////                l.notifyAll();
//            }
//        }
//
//        @Override
//        public Object getRoot() {
//            return route;
//        }
//
//        @Override
//        public Object getChild(Object parent, int index) {
//            return ((RouteEntry) parent).children.get(index);
//        }
//
//        @Override
//        public int getChildCount(Object parent) {
//            return ((RouteEntry) parent).children.size();
//        }
//
//        @Override
//        public boolean isLeaf(Object node) {
//            return ((RouteEntry) node).children == null || ((RouteEntry) node).children.isEmpty();
//        }
//
//        @Override
//        public void valueForPathChanged(TreePath path, Object newValue) {
//            System.out.println(path + ": " + newValue.getClass() + " - " + newValue);
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public int getIndexOfChild(Object parent, Object child) {
//            return ((RouteEntry) parent).children.indexOf(child);
//        }
//
//        @Override
//        public void addTreeModelListener(TreeModelListener l) {
//            listeners.add(l);
//        }
//
//        @Override
//        public void removeTreeModelListener(TreeModelListener l) {
//            listeners.remove(l);
//        }
//
//    }
}
