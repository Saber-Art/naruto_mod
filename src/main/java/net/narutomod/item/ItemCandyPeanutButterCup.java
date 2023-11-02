
package net.narutomod.item;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemFood;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

@ElementsNarutomodMod.ModElement.Tag
public class ItemCandyPeanutButterCup extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:candy_peanut_butter_cup")
	public static final Item block = null;
	public ItemCandyPeanutButterCup(ElementsNarutomodMod instance) {
		super(instance, 867);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:candy_peanutbuttercup", "inventory"));
	}
	public static class ItemFoodCustom extends ItemFood {
		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("candy_peanut_butter_cup");
			setRegistryName("candy_peanut_butter_cup");
			setAlwaysEdible();
			setCreativeTab(CreativeTabs.FOOD);
			setMaxStackSize(64);
		}

		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
			super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}
	}
}
