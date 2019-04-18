package net.aeronetwork.core.map.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@AllArgsConstructor
@Getter
public class MapData<T> {

    private File mapFolder;
    private T data;
}
