package com.robertx22.ancient_obelisks.main;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public enum ObeliskWords implements ITranslated {
    WAVE_X_STARTING("Wave %1$s Starting!"),
    LAST_WAVE("Last Wave INCOMING!");
    public String name;

    ObeliskWords(String name) {
        this.name = name;
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return new TranslationBuilder(ObelisksMain.MODID).name(ExileTranslation.of(ObelisksMain.MODID + ".words." + this.name().toLowerCase(Locale.ROOT), name));
    }

    public MutableComponent get(Object... obj) {
        return getTranslation(TranslationType.NAME).getTranslatedName(obj);
    }
}
