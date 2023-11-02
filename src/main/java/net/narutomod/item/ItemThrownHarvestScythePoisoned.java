
package net.narutomod.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.narutomod.procedure.ProcedureThrownHarvestScytheRightClickedInAir;

import java.util.HashMap;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ItemThrownHarvestScythePoisoned extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:thrown_harvest_scythe_poisoned")
	public static final Item block = null;
	public ItemThrownHarvestScythePoisoned(ElementsNarutomodMod instance) {
		super(instance, 876);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:thrown_harvest_scythe_poisoned", "inventory"));
	}
	public static class ItemCustom extends Item {
		public ItemCustom() {
			setMaxDamage(0);
			maxStackSize = 1;
			setUnlocalizedName("thrown_harvest_scythe_poisoned");
			setRegistryName("thrown_harvest_scythe_poisoned");
		}

		@Override
		public int getItemEnchantability() {
			return 0;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
			return 1F;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ActionResult<ItemStack> ar = super.onItemRightClick(world, entity, hand);
			ItemStack itemstack = ar.getResult();
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				ProcedureThrownHarvestScytheRightClickedInAir.executeProcedure($_dependencies);
			}
			return ar;
		}
	}
}
