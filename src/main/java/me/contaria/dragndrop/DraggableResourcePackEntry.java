package me.contaria.dragndrop;

import me.contaria.dragndrop.interfaces.IPackScreen;
import me.contaria.dragndrop.mixin.EntryListWidgetAccessor;
import me.contaria.dragndrop.mixin.ResourcePackEntryAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.Text;

public class DraggableResourcePackEntry extends PackListWidget.ResourcePackEntry implements Draggable {

    private boolean pickedUp;
    private double pickedUpFromX;
    private double pickedUpFromY;
    private double scrollAmountWhenPickedUp;

    private DelayedRenderCall delayedRenderCall;

    public DraggableResourcePackEntry(MinecraftClient client, PackListWidget widget, Screen screen, ResourcePackOrganizer.Pack pack) {
        super(client, widget, screen, pack);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // pick up the element
        if (this.isMouseOver(mouseX, mouseY)) {
            this.pickUp(mouseX, mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // drop the element
        if (this.pickedUp) {
            if (this.drop(mouseX, mouseY)) {
                return true;
            }
            super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (this.pickedUp) {
            this.delayedRenderCall = () -> {
                int newY = y + mouseY - (int) this.pickedUpFromY + (int) (((ResourcePackEntryAccessor) this).getWidget().getScrollAmount() - this.scrollAmountWhenPickedUp);
                int newX = x + mouseX - (int) this.pickedUpFromX;

                DrawableHelper.fill(matrices, newX - 1, newY - 1, newX + entryWidth - 9, newY + entryHeight + 1, -1873784752);
                super.render(matrices, index, newY, newX, entryWidth, entryHeight, mouseX, mouseY, false, tickDelta);
            };
            return;
        }
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
    }

    public void render() {
        if (this.delayedRenderCall != null) {
            this.delayedRenderCall.render();
            this.delayedRenderCall = null;
        }
    }

    private void pickUp(double x, double y) {
        ((IPackScreen) this.screen).pickedUpEntries().add(this);
        this.pickedUp = true;
        this.pickedUpFromX = x;
        this.pickedUpFromY = y;
        this.scrollAmountWhenPickedUp = ((ResourcePackEntryAccessor) this).getWidget().getScrollAmount();
    }

    private boolean drop(double x, double y) {
        ((IPackScreen) this.screen).pickedUpEntries().remove(this);
        this.pickedUp = false;
        if (hasMoved(x, y)) {
            for (Element e : this.screen.children()) {
                if (e instanceof PackListWidget listWidget && (tryInsert(listWidget, x, y) || tryInsert(listWidget, x, y))) {
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    private boolean hasMoved(double x, double y) {
        return this.pickedUpFromX - x != 0 || this.pickedUpFromY - y != 0;
    }

    private boolean tryInsert(PackListWidget listWidget, double x, double y) {
        ResourcePackOrganizer.Pack pack = ((ResourcePackEntryAccessor) this).getPack();
        boolean switchesList = !listWidget.equals(((ResourcePackEntryAccessor) this).getWidget());
        if (listWidget.isMouseOver(x, y) && !(pack.isAlwaysEnabled() && switchesList)) {
            int index = listWidget.children().indexOf(((EntryListWidgetAccessor<PackListWidget.ResourcePackEntry>) listWidget).callGetEntryAtPosition(x, y + ((EntryListWidgetAccessor<?>) listWidget).getItemHeight() / 2.0));

            if (!switchesList && index > listWidget.children().indexOf(this)) {
                index--;
            }

            if (switchesList) {
                if (((IPackScreen) this.screen).isSelectedPackList(listWidget)) {
                    ResourcePackCompatibility resourcePackCompatibility = pack.getCompatibility();
                    if (resourcePackCompatibility.isCompatible()) {
                        pack.enable();
                    } else {
                        ResourcePackOrganizer.Pack finalPack = pack;
                        int finalIndex = index;
                        this.client.setScreen(new ConfirmScreen(confirmed -> {
                            if (confirmed) {
                                finalPack.enable();
                                this.movePackToIndex(((ResourcePackEntryAccessor) listWidget.children().get(0)).getPack(), finalIndex);
                            }
                            this.client.setScreen(this.screen);
                        }, Text.translatable("pack.incompatible.confirm.title"), resourcePackCompatibility.getConfirmMessage()));
                        return true;
                    }
                } else {
                    pack.disable();
                }
                pack = ((ResourcePackEntryAccessor) listWidget.children().get(0)).getPack();
            }

            this.movePackToIndex(pack, index);
            return true;
        }
        return false;
    }

    private void movePackToIndex(ResourcePackOrganizer.Pack pack, int index) {
        while (pack.canMoveTowardStart()) {
            pack.moveTowardStart();
        }
        for (int i = 0; i != index && pack.canMoveTowardEnd(); i++) {
            pack.moveTowardEnd();
        }
    }

    @Override
    public boolean isPickedUp() {
        return pickedUp;
    }

    @FunctionalInterface
    private interface DelayedRenderCall {
        void render();
    }
}
