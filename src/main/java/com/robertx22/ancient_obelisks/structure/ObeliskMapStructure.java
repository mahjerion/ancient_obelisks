package com.robertx22.ancient_obelisks.structure;

import com.robertx22.ancient_obelisks.database.ObeliskDatabase;
import com.robertx22.ancient_obelisks.main.ObelisksMain;
import com.robertx22.library_of_exile.dimension.MapGenerationUTIL;
import com.robertx22.library_of_exile.dimension.structure.SimplePrebuiltMapData;
import com.robertx22.library_of_exile.dimension.structure.SimplePrebuiltMapStructure;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.world.level.ChunkPos;

public class ObeliskMapStructure extends SimplePrebuiltMapStructure {
    @Override
    public SimplePrebuiltMapData getMap(ChunkPos start) {
        var random = MapGenerationUTIL.createRandom(start);
        var list = ObeliskDatabase.getObelisks().getList();
        var ob = RandomUtils.weightedRandom(list, random.nextDouble());
        return ob.simple_prebuilt_map;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    public ChunkPos getStartFromCounter(int x, int z) {
        var start = new ChunkPos(x * ObeliskMapStructure.DUNGEON_LENGTH, z * ObeliskMapStructure.DUNGEON_LENGTH);
        start = ObelisksMain.OBELISK_MAP_STRUCTURE.getStartChunkPos(start);
        return start;
    }

    public static int DUNGEON_LENGTH = 10;

    @Override
    protected ChunkPos INTERNALgetStartChunkPos(ChunkPos cp) {
        var newcp = (new ChunkPos(cp.x / DUNGEON_LENGTH * DUNGEON_LENGTH, cp.z / DUNGEON_LENGTH * DUNGEON_LENGTH));
        return newcp;

        /*
        int chunkX = cp.x;
        int chunkZ = cp.z;
        int distToEntranceX = 11 - (chunkX % DUNGEON_LENGTH);
        int distToEntranceZ = 11 - (chunkZ % DUNGEON_LENGTH);
        chunkX += distToEntranceX;
        chunkZ += distToEntranceZ;
        return new ChunkPos(chunkX, chunkZ);

         */
    }

}
