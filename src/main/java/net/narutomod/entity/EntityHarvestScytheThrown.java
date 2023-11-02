
package net.narutomod.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;
import net.narutomod.item.ItemHarvestScythe;
import net.narutomod.item.ItemKunai;
import net.narutomod.item.ItemThrownHarvestScythe;

import java.util.Iterator;
import java.util.ArrayList;

@ElementsNarutomodMod.ModElement.Tag
public class EntityHarvestScytheThrown extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 436;
	public static final int ENTITYID_RANGED = 437;
	public EntityHarvestScytheThrown(ElementsNarutomodMod instance) {
		super(instance, 874);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
				.id(new ResourceLocation("narutomod", "harvest_scythe_thrown"), ENTITYID).name("harvest_scythe_thrown").tracker(64, 1, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class EntityArrowCustom extends EntityArrow {
		public EntityArrowCustom(World a) {
			super(a);
		}

		public EntityArrowCustom(World worldIn, double x, double y, double z) {
			super(worldIn, x, y, z);
		}

		public EntityArrowCustom(World worldIn, EntityLivingBase shooter) {
			super(worldIn, shooter);
		}

		@Override
		protected void arrowHit(EntityLivingBase entity) {
			super.arrowHit(entity);
			entity.setArrowCountInEntity(entity.getArrowCountInEntity() - 1);
		}

		@Override
		protected ItemStack getArrowStack() {
			return new ItemStack(ItemThrownHarvestScythe.block);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			NBTTagCompound data = getEntityData();
			data.setBoolean("InGround", this.inGround);

			if (this.ticksExisted >= 800) {
				this.setDead();
			}
		}

		@Override
		public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
			super.shoot(x, y, z, velocity, inaccuracy);
			this.setNoGravity(true);
		}

		/*@Override
		public void onUpdate() {
			super.onUpdate();
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			World world = this.world;
			Entity entity = (Entity) shootingEntity;
			if (this.inGround) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("x", x);
					$_dependencies.put("y", y);
					$_dependencies.put("z", z);
					$_dependencies.put("world", world);
					ProcedureKunaiBulletHitsBlock.executeProcedure($_dependencies);
				}
				this.world.removeEntity(this);
			}
		}*/
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> {
				return new Renderer.RenderCustom(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityArrowCustom> {
			protected final Item item;
			private final RenderItem itemRenderer;

			private float rotAngle = 0.0F;

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.item = ItemThrownHarvestScythe.block;
				this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
			}

			@Override
			public void doRender(EntityArrowCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				NBTTagCompound data = entity.getEntityData();

				GlStateManager.pushMatrix();
				GlStateManager.translate((float)x, (float)y, (float)z);
				GlStateManager.enableRescaleNormal();

				if (data.hasKey("InGround")) {
					if (!data.getBoolean("InGround")) {
						rotAngle += 10.0F * partialTicks;
						GlStateManager.rotate(rotAngle, 0.0F, 1.0F, 0.0F);
					} else {
						GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
					}
				}
				//GlStateManager.rotate((entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks), 0.0F, 0.0F, 1.0F);

				this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				if (this.renderOutlines) {
					GlStateManager.enableColorMaterial();
					GlStateManager.enableOutlineMode(this.getTeamColor(entity));
				}
				this.itemRenderer.renderItem(this.getStackToRender(entity), ItemCameraTransforms.TransformType.GROUND);
				if (this.renderOutlines) {
					GlStateManager.disableOutlineMode();
					GlStateManager.disableColorMaterial();
				}
				GlStateManager.disableRescaleNormal();
				GlStateManager.popMatrix();
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			public ItemStack getStackToRender(EntityArrowCustom entityIn) {
				return new ItemStack(this.item);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityArrowCustom entity) {
				return TextureMap.LOCATION_BLOCKS_TEXTURE;
			}
		}
	}
}
