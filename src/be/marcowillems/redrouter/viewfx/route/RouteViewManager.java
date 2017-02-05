/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.route.RouteEntry;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 *
 * @author Marco Willems
 */
public class RouteViewManager implements Callback<TreeView, RouteEntryTreeCell> {

    private final Map<RouteEntry, RouteEntryTreeView> entryContainers = new HashMap<>();

    @Override
    public RouteEntryTreeCell call(TreeView param) {
        return new RouteEntryTreeCell(this, param);
    }

    public RouteEntryTreeView getContainer(RouteEntry routeEntry, TreeItem treeItem) {
        if (routeEntry != null) {
            if (!entryContainers.containsKey(routeEntry)) {
                entryContainers.put(routeEntry, new RouteEntryTreeView(routeEntry, treeItem));
            }
            return entryContainers.get(routeEntry);
        } else {
            return null;
        }
    }

}
