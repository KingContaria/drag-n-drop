package me.contaria.dragndrop.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.contaria.dragndrop.Draggable;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin <E extends EntryListWidget.Entry<E>> {

    @Shadow protected abstract E getEntry(int par1);

    @ModifyExpressionValue(method = "renderList", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;top:I"))
    private int dragndrop_alwaysRenderDraggingEntries1(int top, @Local(ordinal = 6) int entryIndex) {
        return this.getEntry(entryIndex) instanceof Draggable draggable && draggable.isPickedUp() ? Integer.MIN_VALUE : top;
    }

    @ModifyExpressionValue(method = "renderList", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;bottom:I"))
    private int dragndrop_alwaysRenderDraggingEntries2(int bottom, @Local(ordinal = 6) int entryIndex) {
        return this.getEntry(entryIndex) instanceof Draggable draggable && draggable.isPickedUp() ? Integer.MAX_VALUE : bottom;
    }
}