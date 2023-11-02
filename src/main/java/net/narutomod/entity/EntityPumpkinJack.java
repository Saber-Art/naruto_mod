
package net.narutomod.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;
import net.narutomod.ModConfig;
import net.narutomod.NarutomodMod;
import net.narutomod.item.*;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.potion.PotionChakraEnhancedStrength;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.procedure.ProcedureBasicNinjaSkills;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import software.bernie.example.client.renderer.entity.layer.GeoExampleLayer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.RenderHurtColor;
import software.bernie.geckolib3.util.MatrixStack;
import software.bernie.shadowed.eliotlash.mclib.utils.Interpolations;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.*;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPumpkinJack extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 434;
	public static final int ENTITYID_RANGED = 435;
	public EntityPumpkinJack(ElementsNarutomodMod instance) {
		super(instance, 872);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "pumpkin_jack"), ENTITYID)
						.name("pumpkin_jack").tracker(64, 1, true).egg(-26368, -13421773).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		int i = MathHelper.clamp(ModConfig.SPAWN_WEIGHT_JACK, 0, 200);
		if (i > 0) {
			EntityRegistry.addSpawn(EntityCustom.class, i, 1, 1, EnumCreatureType.MONSTER,
					Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMPLAND, Biomes.RIVER, Biomes.FOREST_HILLS,
					Biomes.TAIGA_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.BIRCH_FOREST,
					Biomes.BIRCH_FOREST_HILLS, Biomes.ROOFED_FOREST, Biomes.SAVANNA, Biomes.EXTREME_HILLS,
					Biomes.MUTATED_FOREST, Biomes.MUTATED_TAIGA, Biomes.MUTATED_SWAMPLAND, Biomes.MUTATED_JUNGLE,
					Biomes.MUTATED_JUNGLE_EDGE, Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST_HILLS,
					Biomes.MUTATED_ROOFED_FOREST, Biomes.MUTATED_SAVANNA, Biomes.MUTATED_EXTREME_HILLS,
					Biomes.MUTATED_EXTREME_HILLS_WITH_TREES, Biomes.EXTREME_HILLS_WITH_TREES);
		}
	}

	@SubscribeEvent
	public void onLivingDamaged(LivingDamageEvent event) {
		if ( !(event.getEntityLiving() instanceof EntityCustom) ) { return; }
		DamageSource dmg = event.getSource();
		Entity attacker = dmg.getTrueSource();
		if (attacker instanceof EntityPlayer || attacker instanceof EntityNinjaMob.Base) {
			if (((EntityLivingBase) attacker).isPotionActive(PotionChakraEnhancedStrength.potion)) {
				if (event.getAmount() >= 20) {
					event.setAmount(20);
				}
			}
		}
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private final double SCYTHE_THROW = 100d;
		private final double PUMPKIN_ATTACK = 50d;
		private final double WOOD_DRAGON = 200d;
		private final double WOOD_BINDING = 700d;
		private final double FIREBALL_CHAKRA = 50d;
		private final double INVIS_CHAKRA = 100000d;
		private static final int GENJUTSU_COOLDOWN = 100; // 5 seconds
		private boolean isReal;
		private int stage;
		private boolean canDrop = false;
		private Item drop;
		private Item msStack;
		private Item tomoeStack;
		private boolean dropEyes;
		private int lookedAtTime;
		private final int genjutsuDuration = 200;
		private int lastGenjutsuTime;
		private int lastInvisTime;
		private int lastSusanooTime = -600;
		private int woodburialTicks = 0;
		private int woodburialDur = 600;

		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);

		public EntityCustom(World world) {
			super(world, 120, 70000d);
			//this.setItemToInventory(kunaiStack);
			this.isImmuneToFire = true;
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			//this.setItemStackToSlot(EntityEquipmentSlot.HEAD, tomoe);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemHarvestScythe.block, 1));

			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double) 500);
			this.setHealth(this.getMaxHealth());

			boolean canDrop = false;
			if (Math.random() > 0.7) {
				canDrop = true;
			}
			this.canDrop = true;
			this.setStage(1);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		public void setStage(int newStage) {
			this.stage = newStage;
			this.getEntityData().setInteger("stage", this.stage);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 1.0D, 50, 16.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.getAttackTarget().getDistance(EntityCustom.this) > 4d;
				}
			});
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 1.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !EntityCustom.this.isRiding()
							&& EntityCustom.this.getAttackTarget().posY - EntityCustom.this.posY > 5d;
				}
			});
			this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0d, true) {

				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !EntityCustom.this.isRiding()
							&& EntityCustom.this.getAttackTarget().getDistance(EntityCustom.this)
							<= 6d;
				}

				@Override
				public void checkAndPerformAttack(EntityLivingBase target, double distance) {
					double d0 = this.getAttackReachSqr(target);
					if (distance <= d0 && this.attackTick <= 0) {
						if (new Random().nextFloat() * 100 < 2F) {
							this.attackTick = 60;

							float min = 1;
							float max = 10;

							Random r = new Random();
							float randomNum = min + (max - min) * r.nextFloat();
							target.world.spawnEntity(new EntityWoodBurial.EC(target));
						} else {
							this.attackTick = 20;
							this.attacker.swingArm(EnumHand.MAIN_HAND);
							this.attacker.attackEntityAsMob(target);
						}
					}
				}



				/*@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					return EntityCustom.this.isSusanooActive() ? ProcedureUtils.getReachDistanceSq(EntityCustom.this.susanooEntity)
							: super.getAttackReachSqr(attackTarget);
				}
				@Override
				protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_) {
					if (EntityCustom.this.isSusanooActive()) {
						if (p_190102_2_ <= this.getAttackReachSqr(p_190102_1_) && this.attackTick <= 0) {
							this.attackTick = 20;
							EntityCustom.this.susanooEntity.swingArm(EnumHand.MAIN_HAND);
							EntityCustom.this.susanooEntity.attackEntityAsMob(p_190102_1_);
						}
					} else {
						super.checkAndPerformAttack(p_190102_1_, p_190102_2_);
					}
				}*/
			});
			this.tasks.addTask(4, new EntityAIWatchClosest2(this, EntityPlayer.class, 40.0F, 1.0F));
			this.tasks.addTask(5, new EntityAIWander(this, 0.3));
			this.tasks.addTask(6, new EntityAILookIdle(this));
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false,
					new Predicate<EntityPlayer>() {
						public boolean apply(@Nullable EntityPlayer p_apply_1_) {
							return p_apply_1_ != null
									&& (ItemSharingan.wearingAny(p_apply_1_) || EntityBijuManager.isJinchuriki(p_apply_1_));
						}
					}));
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
			if (this.stage == 2) {
				if (this.canDrop && new Random().nextFloat() < 0.1F) {
					ItemStack _stack = new ItemStack(ItemHarvestScythe.block, 1);
					this.entityDropItem(_stack, 0.0f);
				} else {
					this.entityDropItem(new ItemStack(Item.getItemById(91), 1), 0.0f);
				}
			}
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvents.ENTITY_SKELETON_DEATH;
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamJack.contains(entityIn.getClass());
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL) {
				return false;
			}
			if (!this.world.isRemote) {
				boolean ret = true;
				Entity entity1 = source.getTrueSource();
				if (this.rand.nextInt(3) <= 1) {
					this.setPositionAndUpdate(this.posX + (this.rand.nextDouble() - 0.5) * 2, this.posY, this.posZ + (this.rand.nextDouble() - 0.5) * 2);
					ret = false;
				} else if (this.ticksExisted > this.lastInvisTime + 200 && this.getChakra() >= INVIS_CHAKRA) {
					this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 200, 1, false, false));
					for (int i = 0; i < 100; i++) {
						Entity entityToSpawn = new EntityCrow.EntityCustom(this.world);
						entityToSpawn.setLocationAndAngles(this.posX, this.posY + 1.4, this.posZ, this.rand.nextFloat() * 360F, 0.0F);
						this.world.spawnEntity(entityToSpawn);
					}
					this.setPositionAndUpdate(this.posX + (this.rand.nextDouble() - 0.5) * 6, this.posY + 1,
							this.posZ + (this.rand.nextDouble() - 0.5) * 6);
					this.consumeChakra(INVIS_CHAKRA);
					this.lastInvisTime = this.ticksExisted;
					ret = false;
				}
				if (!ret) {
					if (entity1 instanceof EntityLivingBase) {
						this.setRevengeTarget((EntityLivingBase)entity1);
					}
					return false;
				}
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		protected void updateAITasks() {
			//super.updateAITasks();
			EntityLivingBase target = this.getAttackTarget();
			if (target != null && target.isEntityAlive()) {
				/*
				if (this.isSusanooActive()) {
					this.susanooEntity.setAttackTarget(target);
				}
				if (this.lookedAtTime >= 5 && this.ticksExisted > this.lastGenjutsuTime + this.genjutsuDuration + GENJUTSU_COOLDOWN
						&& this.consumeChakra(GENJUTSU_CHAKRA)) {
					if (target instanceof EntityPlayerMP) {
						ProcedureSync.MobAppearanceParticle.send((EntityPlayerMP)target, ENTITYID_RANGED);
					}
					this.world.playSound(null, target.posX, target.posY, target.posZ,
							SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:genjutsu")), SoundCategory.NEUTRAL, 1f, 1f);
					if (!this.world.isRemote) {
						target.addPotionEffect(new PotionEffect(PotionParalysis.potion, this.genjutsuDuration, 1, false, false));
						target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, this.genjutsuDuration+40, 0, false, true));
						target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 0, false, true));
					}
					this.lastGenjutsuTime = this.ticksExisted;
					this.lookedAtTime = 0;
				}

				 */
				if (this.equals(ProcedureUtils.objectEntityLookingAt(target, 24d).entityHit)) {
					++this.lookedAtTime;
				} else {
					this.lookedAtTime = 0;
				}
			} else if (this.peacefulTicks > 200) {
				this.setAttackTarget(target = null);
			}
		}

		@Override
		public void onEntityUpdate() {
			super.onEntityUpdate();
			{
				java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
				$_dependencies.put("entity", this);
				$_dependencies.put("world", this.world);
				ProcedureBasicNinjaSkills.executeProcedure($_dependencies);
			}
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere()
					&& this.world.getEntitiesWithinAABB(EntityCustom.class, this.getEntityBoundingBox().grow(128.0D)).isEmpty();
			//&& this.rand.nextInt(5) == 0;
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			int chance = this.rand.nextInt(12);
			if (this.getRidingEntity() instanceof EntitySusanooClothed.EntityCustom) {
				((EntitySusanooClothed.EntityCustom)this.getRidingEntity()).attackEntityWithRangedAttack(target, distanceFactor);
			} else {

				float rN = new Random().nextFloat();

				//Amatarasu isnt needed
				if (chance == 0 && this.stage > 1 && distanceFactor > 0.3333f && this.consumeChakra(900000d)) {
					this.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvent.REGISTRY
							.getObject(new ResourceLocation("narutomod:sharingansfx")), SoundCategory.NEUTRAL, 1f, 1f);
					target.addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, 1200, 3, false, false));
				} else if (chance <= 2 && distanceFactor >= 0.5333f && rN < 0.6f && this.consumeChakra(FIREBALL_CHAKRA)) {
					double d0 = target.posX - this.posX;
					double d1 = target.posY - (this.posY + this.getEyeHeight());
					double d2 = target.posZ - this.posZ;

					float min = 1;
					float max = 10;

					Random r = new Random();
					float randomNum = min + (max - min) * r.nextFloat();

					new ItemYooton.EntityMagmaBall.Jutsu().createJutsu(this, d0, d1, d2, randomNum);
				} else if (chance <= 3 && distanceFactor >= 0.5333f && rN > 0.5f && this.consumeChakra(WOOD_DRAGON)) {
					double d0 = target.posX - this.posX;
					double d1 = target.posY - (this.posY + this.getEyeHeight());
					double d2 = target.posZ - this.posZ;

					float min = 1;
					float max = 5;

					Random r = new Random();
					float randomNum = min + (max - min) * r.nextFloat();

					new EntityWoodDragon.EC.Jutsu().createJutsu(this, randomNum);
				} else if (!this.isRiding()) {
					EntityHarvestScytheThrown.EntityArrowCustom projectile = new EntityHarvestScytheThrown.EntityArrowCustom(this.world, this);
					Vec3d vec = target.getPositionEyes(1f).subtract(projectile.getPositionVector());
					projectile.shoot(vec.x, vec.y-1, vec.z, 2f, 0);
					projectile.setDamage(21);
					projectile.setKnockbackStrength(2);
					this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1, 1f / (this.rand.nextFloat() * 0.5f + 1f) + 0.25f);
					this.world.spawnEntity(projectile);
				}
			}
		}

		@Override
		public void addTrackingPlayer(EntityPlayerMP player) {
			super.addTrackingPlayer(player);

			if (ModConfig.AGGRESSIVE_BOSSES) {
				this.setAttackTarget(player);
			}
		}

		@Override
		public void removeTrackingPlayer(EntityPlayerMP player) {
			super.removeTrackingPlayer(player);

			if (this.bossInfo.getPlayers().contains(player)) {
				this.bossInfo.removePlayer(player);
			}
		}

		private void trackAttackedPlayers() {
			Entity entity = this.getAttackingEntity();

			if (entity instanceof EntityPlayerMP || (entity = (ModConfig.AGGRESSIVE_BOSSES ? this.getLastAttackedEntity() : this.getAttackTarget())) instanceof EntityPlayerMP) {
				this.bossInfo.addPlayer((EntityPlayerMP) entity);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.trackAttackedPlayers();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			ItemStack eyes = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

			if (this.world.isRemote) { return; }
			if (this.stage == 1) {
				if ( (this.getHealth() / this.getMaxHealth()) <= 0.50) {
					this.setStage(2);
					this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double) this.getMaxHealth() * 3);
					this.setHealth(this.getMaxHealth());

					Vec3d pos = this.getPositionVector();
					this.world.playSound((EntityPlayer) null, pos.x, pos.y, pos.z, (net.minecraft.util.SoundEvent) SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:pumpkin-roar")), SoundCategory.NEUTRAL, (float) 5, (float) 1);

					//this.setEye(this.msStack);
				}
			}
		}

		@Override
		protected boolean canSeeInvisible(Entity entityIn) {
			return !entityIn.isInvisible() || this.getDistanceSq(entityIn) <= 400d;
		}

		//Entity Data Saving and Loading from variables
		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("stage", this.stage);
			compound.setBoolean("canDrop", this.canDrop);
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setStage(compound.getInteger("stage"));
			this.canDrop = compound.getBoolean("canDrop");
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager ->
					new Renderer.RenderCustom(renderManager, new ModelBiped()));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/pumpkin_jack_flat.png");

			private ModelBiped bipedModel;

			public RenderCustom(RenderManager renderManagerIn, ModelBiped modelIn) {
				super(renderManagerIn, modelIn);

				this.bipedModel = modelIn;

				//this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerCustomHead(modelIn.bipedHead));
				//this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerHeldItem(this));
				//this.addLayer(new EntityClone.ClientRLM().new BipedArmorLayer(this));
			}

			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				super.doRender(entity, x, y, z, entityYaw, partialTicks);

				NBTTagCompound data = entity.getEntityData();

				if (this.bipedModel != null) {
					ModelBiped model = this.bipedModel;
					if (data.getInteger("stage") < 2) {
						model.bipedHead.showModel = false;
						model.bipedHeadwear.showModel = false;
					} else {
						model.bipedHead.showModel = true;
						model.bipedHeadwear.showModel = true;
					}
				}
			}



			@Override
			protected void renderLayers(EntityCustom entity, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
				if (!entity.isInvisible()) {
					super.renderLayers(entity, f0, f1, f2, f3, f4, f5, f6);
				}
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
			}

			@Override
			public void transformHeldFull3DItemLayer() {
				GlStateManager.translate(0.0F, 0.1875F, 0.0F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}
	}
}
