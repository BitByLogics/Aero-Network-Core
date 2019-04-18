package net.aeronetwork.core.map.impl;

import lombok.Getter;
import net.aeronetwork.core.map.Map;

import java.util.List;

@Getter
public class DefaultMap implements Map {

    private String name;
    private List<String> builders;
}
