package com.robertx22.ancient_obelisks.database;

import com.robertx22.library_of_exile.dimension.structure.SimplePrebuiltMapData;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;

public class Obelisk implements JsonExileRegistry<Obelisk>, IAutoGson<Obelisk> {
    public static Obelisk SERIALIZER = new Obelisk(new SimplePrebuiltMapData(1, ""), 0, "empty");

    public SimplePrebuiltMapData simple_prebuilt_map = null;

    public int weight = 1000;
    public String id = "";

    public Obelisk(SimplePrebuiltMapData simple_prebuilt_map, int weight, String id) {
        this.simple_prebuilt_map = simple_prebuilt_map;
        this.weight = weight;
        this.id = id;
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return ObeliskDatabase.OBELISK_TYPE;
    }

    @Override
    public Class<Obelisk> getClassForSerialization() {
        return Obelisk.class;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return weight;
    }
}
