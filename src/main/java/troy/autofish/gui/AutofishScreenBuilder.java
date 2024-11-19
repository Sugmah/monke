package troy.autofish.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import troy.autofish.FabricModAutofish;
import troy.autofish.config.Config;

import java.util.function.Function;

public class AutofishScreenBuilder {

    private static final Function<Boolean, Text> yesNoTextSupplier = bool -> {
        if (bool) return Text.translatable("options.autofish.toggle.on");
        else return Text.translatable("options.autofish.toggle.off");
    };

    public static Screen buildScreen(FabricModAutofish modAutofish, Screen parentScreen) {

        Config config = modAutofish.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setTitle(Text.translatable("options.autofish.title"))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> {
                    // Enforce constraints on the in-memory config object
                    modAutofish.getConfig().enforceConstraints();
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory configCat = builder.getOrCreateCategory(Text.translatable("options.autofish.config"));

        // Basic Settings
        AbstractConfigListEntry toggleAutofish = entryBuilder.startBooleanToggle(
                        Text.translatable("options.autofish.enable.title"),
                        config.isAutofishEnabled())
                .setTooltip(Text.translatable("options.autofish.enable.tooltip"))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setAutofishEnabled(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        AbstractConfigListEntry toggleMultiRod = entryBuilder.startBooleanToggle(
                        Text.translatable("options.autofish.multirod.title"),
                        config.isMultiRod())
                .setTooltip(
                        Text.translatable("options.autofish.multirod.tooltip_0"),
                        Text.translatable("options.autofish.multirod.tooltip_1"),
                        Text.translatable("options.autofish.multirod.tooltip_2"))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setMultiRod(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        // (Add other entries following the same pattern)

        SubCategoryBuilder subCatBuilderBasic = entryBuilder.startSubCategory(Text.translatable("options.autofish.basic.title"));
        subCatBuilderBasic.add(toggleAutofish);
        subCatBuilderBasic.add(toggleMultiRod);
        subCatBuilderBasic.setExpanded(true);

        configCat.addEntry(subCatBuilderBasic.build());

        return builder.build();
    }
}
