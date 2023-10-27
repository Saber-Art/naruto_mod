
package net.narutomod.item;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkSystem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.NetworkModHolder;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.items.CapabilityItemHandler;
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
		if (event.getEntityItem().getItem().getItem() == block) {
			if (Minecraft.getMinecraft().currentScreen instanceof GuiBucketStorage.GuiWindow) {
				Minecraft.getMinecraft().player.closeScreen();
			}
		}
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:halloween_bucket", "inventory"));
	}
	public static class ItemCustom extends Item {
		public ItemCustom() {
			setMaxDamage(0);
			maxStackSize = 1;
			setUnlocalizedName("halloween_bucket");
			setRegistryName("halloween_bucket");
			setCreativeTab(TabModTab.tab);
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

		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound compound) {
			return new InventoryCapability();
		}

		@Override
		public NBTTagCompound getNBTShareTag(ItemStack stack) {
			NBTTagCompound nbt = super.getNBTShareTag(stack);
			if (nbt == null) {
				nbt = new NBTTagCompound();
			}

			if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
				ItemStackHandler capability = (ItemStackHandler) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				nbt.setTag("Inventory", capability.serializeNBT());
			}

			return nbt;
		}

		@Override
		public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
			super.readNBTShareTag(stack, nbt);

			if (nbt != null && stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
				ItemStackHandler capability = (ItemStackHandler) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				capability.deserializeNBT(nbt.getCompoundTag("Inventory"));
			}
		}
	}

	private static class InventoryCapability implements ICapabilitySerializable<NBTTagCompound> {
		@CapabilityInject(ItemStackHandler.class)
		public static final Capability<ItemStackHandler> INVENTORY_CAPABILITY = null;

		private final ItemStackHandler inventory = createItemHandler();

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == INVENTORY_CAPABILITY;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			if (capability == INVENTORY_CAPABILITY) {
				return INVENTORY_CAPABILITY.cast(this.inventory);
			}
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return this.inventory.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			this.inventory.deserializeNBT(nbt);
		}

		private ItemStackHandler createItemHandler() {
			return new ItemStackHandler(18) {
				@Override
				public int getSlotLimit(int slot) {
					return 64;
				}

				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					// Replace 'block' with the actual block you are checking against
					return stack.getItem() != Item.getItemFromBlock(Blocks.STONE);
				}
			};
		}

		// Register the capability
		public static void register() {
			CapabilityManager.INSTANCE.register(ItemStackHandler.class, new Capability.IStorage<ItemStackHandler>() {
				@Nullable
				@Override
				public NBTBase writeNBT(Capability<ItemStackHandler> capability, ItemStackHandler instance, EnumFacing side) {
					return instance.serializeNBT();
				}

				@Override
				public void readNBT(Capability<ItemStackHandler> capability, ItemStackHandler instance, EnumFacing side, NBTBase nbt) {
					instance.deserializeNBT((NBTTagCompound) nbt);
				}
			}, () -> new ItemStackHandler(18));
		}
	}
}
