package com.artek;

import java.util.HashMap;

public class CachedInfo {
    public static volatile HashMap<String, String> marks;

    static {
        marks = new HashMap<>();
        marks.put("zaichikauartsiom", "assadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsadassadsadsad");
    }

    public static HashMap<String, String> getMap() {
        return marks;
    }
}
