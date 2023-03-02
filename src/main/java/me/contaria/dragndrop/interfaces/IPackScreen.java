package me.contaria.dragndrop.interfaces;

import me.contaria.dragndrop.DraggableResourcePackEntry;
import net.minecraft.client.gui.screen.pack.PackListWidget;

import java.util.List;

public interface IPackScreen {

    List<DraggableResourcePackEntry> pickedUpEntries();

    boolean isSelectedPackList(PackListWidget packListWidget);
}
