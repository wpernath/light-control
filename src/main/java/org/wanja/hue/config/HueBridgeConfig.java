package org.wanja.hue.config;

import java.util.Set;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix="hue")
public interface HueBridgeConfig {
    Set<HueBridge> bridges();
    String paul();

    public interface HueBridge {
        String name();
        String baseURL();
        String authToken();
    }
}
