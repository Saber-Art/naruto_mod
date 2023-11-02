
package net.narutomod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import net.narutomod.procedure.ProcedureWhiteZetsuFleshFoodEaten;

import java.util.HashMap;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ItemCandyGhost extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:candy_ghost")
	public static final Item block = null;
	public ItemCandyGhost(ElementsNarutomodMod instance) {
		super(instance, 868);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:candy_ghost", "inventory"));
	}
	public static class ItemFoodCustom extends ItemFood {
		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("candy_ghost");
			setRegistryName("candy_ghost");
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

		@Override
		protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entity) {
			super.onFoodEaten(itemStack, world, entity);
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				ProcedureWhiteZetsuFleshFoodEaten.executeProcedure($_dependencies);
			}
		}
	}
}
