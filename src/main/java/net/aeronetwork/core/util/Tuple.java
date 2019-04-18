package net.aeronetwork.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tuple<A, B> {

    private A objectA;
    private B objectB;
}
