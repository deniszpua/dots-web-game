package org.example.net;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;
import java.util.HashSet;
import java.util.Set;


public class EndpointConfig implements ServerApplicationConfig {

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(
            Set<Class<? extends Endpoint>> scanned) {
        return null;
    }


    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set) {
        // Deploy WebSocket endpoint
        Set<Class<?>> results = new HashSet<>();
        results.add(WebsocketEndpoint.class);
        return results;
    }
}