package me.contaria.dragndrop.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntryListWidget.class)
public interface EntryListWidgetAccessor<E extends EntryListWidget.Entry<E>> {

    @Accessor
    int getItemHeight();

    @Invoker
    E callGetEntryAtPosition(double x, double y);
}
