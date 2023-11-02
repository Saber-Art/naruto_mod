
package net.narutomod.item;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkSystem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.internal.NetworkModHolder;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.narutomod.NarutomodMod;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.narutomod.gui.GuiBucketStorage;
import net.narutomod.gui.GuiScrollSwampPitGui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemHalloweenBucket extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:halloween_bucket")
	public static final Item block = null;
	public ItemHalloweenBucket(ElementsNarutomodMod instance) {
		super(instance, 863);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onItemDropped(ItemTossEvent event) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiBucketStorage.GuiWindow) {
			if (event.getEntityItem().getItem().getItem() == new ItemStack(ItemHalloweenBucket.block, 1).getItem()) {
				event.setCanceled(true);
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		registerCapabilities();
	}

	@Override
	public void initElements() {
		//MinecraftForge.EVENT_BUS.register(this);
		elements.items.add(() -> new ItemCustom());
	}

	private void registerCapabilities() {

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:halloween_bucket", "inventory"));
	}

	public static class ItemCustom extends Item implements ItemOnBody.Interface {

		private ItemStackHandler inv;
		public ItemCustom() {
			setMaxDamage(0);
			maxStackSize = 1;
			setUnlocalizedName("halloween_bucket");
			setRegistryName("halloween_bucket");
			setCreativeTab(TabModTab.tab);
			this.inv = new ItemStackHandler(18);
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
			return 0F;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
			ActionResult<ItemStack> ar = super.onItemRightClick(worldIn, playerIn, handIn);
			ItemStack itemstack = ar.getResult();
			int x = (int) playerIn.posX;
			int y = (int) playerIn.posY;
			int z = (int) playerIn.posZ;
			if (playerIn instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP) playerIn;
				playerMP.openGui(NarutomodMod.instance, GuiBucketStorage.GUIID, worldIn, x,y,z);
			}

			return ar;
		}

		public ItemStackHandler getInv(ItemStack stack) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}

			ItemStackHandler inv = new ItemStackHandler(18);

			NBTTagCompound tag = stack.getTagCompound();
			if (!tag.hasKey("Inventory")) {
				tag.setTag("Inventory", inv.serializeNBT());
			}

			inv.deserializeNBT(tag.getCompoundTag("Inventory"));

			return inv;
		}

		public void setInv(ItemStack stack, ItemStackHandler inv) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}

			NBTTagCompound tag = stack.getTagCompound();
			assert tag != null;
			tag.setTag("Inventory", inv.serializeNBT());
		}

		@Override
		public boolean showSkinLayer() { return true; }
		@Override
		public ItemOnBody.BodyPart showOnBody() {
			return ItemOnBody.BodyPart.RIGHT_LEG;
		}

		@Override
		public ItemStack getRenderStack() { return null; }

		@Override
		public boolean canRender() { return true; }
	}
}
