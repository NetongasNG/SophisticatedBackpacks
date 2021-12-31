package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.UpgradeWrapperBase;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class CookingUpgradeWrapper<W extends CookingUpgradeWrapper<W, U, R>, U extends UpgradeItemBase<W>, R extends AbstractCookingRecipe>
		extends UpgradeWrapperBase<W, U> implements ITickableUpgrade, ICookingUpgrade<R> {
	private static final int NOTHING_TO_DO_COOLDOWN = 10;
	protected final CookingLogic<R> cookingLogic;

	protected CookingUpgradeWrapper(IBackpackWrapper backpackWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler, Config.Common.CookingUpgradeConfig cookingUpgradeConfig, RecipeType<R> recipeType, float burnTimeModifier) {
		super(backpackWrapper, upgrade, upgradeSaveHandler);
		cookingLogic = new CookingLogic<>(upgrade, upgradeSaveHandler, cookingUpgradeConfig, recipeType, burnTimeModifier);
	}

	public void tick(@Nullable LivingEntity entity, Level world, BlockPos pos) {
		if (isInCooldown(world)) {
			return;
		}

		if (!cookingLogic.tick(world)) {
			setCooldown(world, NOTHING_TO_DO_COOLDOWN);
		}

		boolean isBurning = cookingLogic.isBurning(world);
		BackpackRenderInfo renderInfo = backpackWrapper.getRenderInfo();
		if (renderInfo.getUpgradeRenderData(CookingUpgradeRenderData.TYPE).map(CookingUpgradeRenderData::isBurning).orElse(false) != isBurning) {
			if (isBurning) {
				renderInfo.setUpgradeRenderData(CookingUpgradeRenderData.TYPE, new CookingUpgradeRenderData(true));
			} else {
				renderInfo.removeUpgradeRenderData(CookingUpgradeRenderData.TYPE);
			}
		}
	}

	public CookingLogic<R> getCookingLogic() {
		return cookingLogic;
	}

	public static class SmeltingUpgradeWrapper extends CookingUpgradeWrapper<SmeltingUpgradeWrapper, SmeltingUpgradeItem, SmeltingRecipe> {
		public SmeltingUpgradeWrapper(IBackpackWrapper backpackWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
			super(backpackWrapper, upgrade, upgradeSaveHandler, Config.COMMON.smeltingUpgrade, RecipeType.SMELTING, 1);
		}
	}

	public static class SmokingUpgradeWrapper extends CookingUpgradeWrapper<SmokingUpgradeWrapper, SmokingUpgradeItem, SmokingRecipe> {
		public SmokingUpgradeWrapper(IBackpackWrapper backpackWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
			super(backpackWrapper, upgrade, upgradeSaveHandler, Config.COMMON.smokingUpgrade, RecipeType.SMOKING, 0.5f);
		}
	}

	public static class BlastingUpgradeWrapper extends CookingUpgradeWrapper<BlastingUpgradeWrapper, BlastingUpgradeItem, BlastingRecipe> {
		public BlastingUpgradeWrapper(IBackpackWrapper backpackWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
			super(backpackWrapper, upgrade, upgradeSaveHandler, Config.COMMON.blastingUpgrade, RecipeType.BLASTING, 0.5f);
		}
	}
}
