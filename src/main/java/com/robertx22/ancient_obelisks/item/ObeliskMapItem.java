package com.robertx22.ancient_obelisks.item;

import com.robertx22.ancient_obelisks.main.ObeliskEntries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ObeliskMapItem extends Item {
    public ObeliskMapItem() {
        super(new Properties().stacksTo(1));
    }

    public static ItemStack blankMap() {

        ItemStack stack = new ItemStack(ObeliskEntries.OBELISK_MAP_ITEM.get());

        var data = new ObeliskItemMapData();

        ObeliskItemNbt.OBELISK_MAP.saveTo(stack, data);

        return stack;

    }
}
