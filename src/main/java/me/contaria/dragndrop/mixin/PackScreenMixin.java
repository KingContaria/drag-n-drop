package me.contaria.dragndrop.mixin;

import me.contaria.dragndrop.interfaces.IPackScreen;
import me.contaria.dragndrop.DraggableResourcePackEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(PackScreen.class)
public abstract class PackScreenMixin extends Screen implements IPackScreen {

    @Shadow private PackListWidget selectedPackList;

    @Unique
    private final List<DraggableResourcePackEntry> pickedUpEntries = new CopyOnWriteArrayList<>();

    protected PackScreenMixin(Text title) {
        super(title);
    }

    @Override
    public List<DraggableResourcePackEntry> pickedUpEntries() {
        return pickedUpEntries;
    }

    @Override
    public boolean isSelectedPackList(PackListWidget packListWidget) {
        return this.selectedPackList == packListWidget;
    }

    @Redirect(method = "method_29672", at = @At(value = "NEW", target = "(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/pack/PackListWidget;Lnet/minecraft/client/gui/screen/pack/ResourcePackOrganizer$Pack;)Lnet/minecraft/client/gui/screen/pack/PackListWidget$ResourcePackEntry;"))
    private PackListWidget.ResourcePackEntry dragndrop_replaceWithDraggableResourcePackEntries(MinecraftClient client, PackListWidget widget, ResourcePackOrganizer.Pack pack) {
        return new DraggableResourcePackEntry(client, widget, this, pack);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void dragndrop_renderDraggedResourcePackEntriesDelayed(CallbackInfo ci) {
        this.pickedUpEntries.forEach(DraggableResourcePackEntry::render);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (DraggableResourcePackEntry entry : this.pickedUpEntries) {
            entry.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
