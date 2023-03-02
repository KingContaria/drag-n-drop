package me.contaria.dragndrop.mixin;

import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PackListWidget.ResourcePackEntry.class)
public interface ResourcePackEntryAccessor {

    @Accessor
    PackListWidget getWidget();

    @Accessor
    ResourcePackOrganizer.Pack getPack();
}
