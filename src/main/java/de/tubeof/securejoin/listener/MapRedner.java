package de.tubeof.securejoin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;

public class MapRedner implements Listener {

    @EventHandler
    public void onMapRender(MapInitializeEvent event) {
        MapView mapView = event.getMap();
        mapView.getId();
    }
}
