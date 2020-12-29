package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.item.ItemStack;

public interface IUpgradeWrapper {
	ItemStack getUpgradeStack();

	default boolean hideSettingsTab() {
		return false;
	}

	default void onNbtChange(IBackpackWrapper backpackWrapper) {
		//noop
	}
}
