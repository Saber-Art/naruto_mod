
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityRegistry;

import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemBijuCloak;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.*;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;

@ElementsNarutomodMod.ModElement.Tag
public abstract class EntityBijuManager<T extends EntityTailedBeast.Base> {
	private static final Map<Class<? extends EntityTailedBeast.Base>, EntityBijuManager> mapByClass = Maps.newHashMap();
	private static final Map<Integer, EntityBijuManager> mapByTailnum = Maps.newHashMap();
	private static final int[] ZERO = {0, 0, 0};
	private UUID vesselUuid;
	private EntityPlayer jinchurikiPlayer;
	private String vesselName = "";
	private T entity;
	private final Class<T> entityClass;
	private final int tails;
	private int cloakLevel;
	private long cloakCD;
	private final int[] cloakXp = new int[3];
	private BlockPos spawnPos;
	private int ticksSinceDeath;
	private boolean hasLived;
	private static final Random rand = new Random();

	private static final List<List<Biome>> spawns = Lists.newArrayList(
			Arrays.asList(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MESA),											  		   // Shukaku
			Arrays.asList(Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.SWAMPLAND), 						   // Matatabi
			Arrays.asList(Biomes.OCEAN, Biomes.BEACH),																		   // Isobu
			Arrays.asList(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS), 		   // Son Goku
			Arrays.asList(Biomes.PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU),											   // Kokuo
			Arrays.asList(Biomes.RIVER, Biomes.SWAMPLAND),																	   // Saiken
			Arrays.asList(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_FOREST), // Chomei
			Arrays.asList(Biomes.OCEAN, Biomes.STONE_BEACH),																   // Gyuki
			Arrays.asList(Biomes.FOREST, Biomes.FOREST_HILLS, Biomes.ROOFED_FOREST)									  		   // Kurama
	);

	public static Collection<EntityBijuManager> getBMList() {
		return ImmutableList.copyOf(mapByClass.values());
	}

	@Nullable
	protected static EntityBijuManager getBijuManagerFrom(EntityPlayer player) {
		for (EntityBijuManager bm : mapByClass.values()) {
			if (player.equals(bm.getJinchurikiPlayer())) {
				return bm;
			}
		}
		return null;
	}

	public static boolean isJinchuriki(EntityPlayer player) {
		return getBijuManagerFrom(player) != null;
	}

	public static boolean isJinchurikiOf(EntityPlayer player, Class<? extends EntityTailedBeast.Base> clazz) {
		EntityBijuManager tb = mapByClass.get(clazz);
		return tb != null && player.equals(tb.getJinchurikiPlayer());
	}

	@Nullable
	public static EntityTailedBeast.Base getBijuOfPlayerInWorld(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getEntityInWorld(player.world) : null;
	}

	public static int availableBijus() {
		int i = 0;
		//for (EntityBijuManager bm : mapByClass.values()) {
		//	if (!bm.isSealed()) {
		//		++i;
		//	}
		//}
		for (int j = 1; j <= 9; j++) {
			if (mapByTailnum.containsKey(j) && !mapByTailnum.get(j).isSealed()) {
				++i;
			}
		}
		return i;
	}

	public static int getRandomAvailableBiju() {
		int i = availableBijus();
		if (i > 0) {
			i = rand.nextInt(i);
			int j = 0;
			//for (EntityBijuManager bm : mapByClass.values()) {
			for (int k = 1; k <= 9; k++) {
				EntityBijuManager bm = mapByTailnum.get(k);
				//if (!bm.isSealed()) {
				if (bm != null && !bm.isSealed()) {
					if (j == i) {
						return bm.getTails();
					}
					++j;
				}
			}
		}
		return 0;
	}

	public static boolean anyBijuAddedToWorld() {
		for (EntityBijuManager bm : mapByClass.values()) {
			if (bm.isAddedToWorld()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBijuAddedToWorld(int tails) {
		EntityBijuManager bm = mapByTailnum.get(tails);
		return bm != null && bm.isAddedToWorld();
	}

	public static void unsetPlayerAsJinchuriki(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		if (bm != null) {
			bm.setVesselEntity(null);
		}
	}

	public static boolean setPlayerAsJinchurikiByTails(EntityPlayer player, int tailnum) {
		EntityBijuManager bm = mapByTailnum.get(tailnum);
		if (bm != null && !bm.isSealed()) {
			bm.setVesselEntity(player);
			return true;
		}
		return false;
	}

	public static void revokeJinchurikiByTails(int tailnum) {
		EntityBijuManager bm = mapByTailnum.get(tailnum);
		if (bm != null) {
			bm.setVesselEntity(null);
		}
	}

	public static void revokeAllJinchuriki() {
		for (EntityBijuManager bm : mapByClass.values()) {
			bm.setVesselEntity(null);
		}
	}

	@Nullable
	public static EntityTailedBeast.Base getEntityByTails(int tailnum) {
		EntityBijuManager bm = mapByTailnum.get(tailnum);
		return bm != null ? bm.getEntity() : null;
	}

	@Nullable
	public static BlockPos getPositionByTails(int tailnum) {
		EntityBijuManager bm = mapByTailnum.get(tailnum);
		return bm != null ? bm.getPosition() : null;
	}

	public static int getTails(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getTails() : 0;
	}

	public static String getNameOfJinchurikisBiju(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getEntityLocalizedName() : null;
	}

	public static void toggleBijuCloak(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		if (bm != null) {
			bm.toggleBijuCloak();
		}
	}

	public static int increaseCloakLevel(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.increaseCloakLevel() : 0;
	}

	public static int cloakLevel(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getCloakLevel() : 0;
	}

	public static void addCloakXp(EntityPlayer player, int xp) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		if (bm != null) {
			bm.addCloakXp(xp);
		}
	}

	public static int getCloakXp(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getCloakXp() : 0;
	}

	public static List<String> listJinchuriki() {
		List<String> list = Lists.newArrayList();
		for (EntityBijuManager bm : mapByClass.values()) {
			list.add(bm.toString());
		}
		return list;
	}

	public static EntityBijuManager getClosestBiju(EntityPlayer player) {
		EntityBijuManager closest = null;

		for (EntityBijuManager bm : mapByClass.values()) {
			if (!bm.hasSpawnPos() || bm.isSealed()) {
				continue;
			}

			double distance = bm.distanceToPlayer(player);

			if (closest == null || distance < closest.distanceToPlayer(player)) {
				closest = bm;
			}
		}
		return closest;
	}

	public static void resetAllSpawnPos() {
		for (EntityBijuManager bm : mapByClass.values()) {
			bm.setSpawnPos(null);
		}
	}

	public EntityBijuManager(Class<T> clazz, int tailnum) {
		this.entityClass = clazz;
		this.tails = tailnum;
		this.setCloakXPs(ZERO);
		mapByClass.put(clazz, this);
		mapByTailnum.put(tailnum, this);
	}

	public void reset() {
		this.setVesselEntity(null, false);
		this.cloakCD = 0;
		this.spawnPos = null;
		this.entity = null;
		this.hasLived = false;
		this.ticksSinceDeath = 0;
	}

	public int getTicksSinceDeath() {
		return this.ticksSinceDeath;
	}

	public void setTicksSinceDeath(int ticks) {
		this.setTicksSinceDeath(ticks, true);
	}
	
	public void setTicksSinceDeath(int ticksSinceDeath, boolean dirty) {
		this.ticksSinceDeath = ticksSinceDeath;
		if (dirty) {
			this.markDirty();
		}
	}

	public boolean getHasLived() {
		return this.hasLived;
	}

	public void setHasLived(boolean lived) {
		this.setHasLived(lived, true);
	}
	
	public void setHasLived(boolean hasLived, boolean dirty) {
		this.hasLived = hasLived;
		if (dirty) {
			this.markDirty();
		}
	}

	public BlockPos getPosition() {
		return this.isAddedToWorld() ? this.locateEntity() : this.spawnPos;
	}

	public double distanceToPlayer(EntityPlayer player) {
		return player.getDistanceSq(this.getPosition());
	}

	public BlockPos getSpawnPos() {
		return this.spawnPos;
	}

	public void setSpawnPos(BlockPos pos) {
		this.setSpawnPos(pos, true);
	}
	
	public void setSpawnPos(BlockPos spawnPos, boolean dirty) {
		this.spawnPos = spawnPos;
		if (dirty) {
			this.markDirty();
		}
	}

	public boolean hasSpawnPos() {
		return this.spawnPos != null;
	}

	public boolean canSpawnInBiome(Biome biome) {
		return this.spawns.get(this.tails - 1).contains(biome);
	}

	public void onAddedToWorld(T entityIn) {
		this.onAddedToWorld(entityIn, true);
	}

	public void onAddedToWorld(T entityIn, boolean dirty) {
		this.hasLived = true;
		this.ticksSinceDeath = 0;
		this.entity = entityIn;
		if (dirty) {
			this.markDirty();
		}
	}

	public void onRemovedFromWorld(T entityIn) {
		this.onRemovedFromWorld(entityIn, true);
	}

	public void onRemovedFromWorld(T entityIn, boolean dirty) {
		this.entity = null;
		if (dirty) {
			this.markDirty();
		}
	}
	
	public boolean isAddedToWorld() {
		return this.entity != null;
	}

	public boolean isAddedToWorld(World world) {
		return this.entity != null && this.entity.world == world;
	}

	public void loadEntityFromNBT(NBTTagCompound compound) {
		this.entity.readFromNBT(compound);
	}

	public boolean isSealed() {
		return this.vesselUuid != null;
	}

	public boolean hasJinchuriki() {
		return this.vesselUuid != null && !this.vesselUuid.equals(EntityGedoStatue.GEDOMAZO_UUID);
	}

	@Nullable
	public UUID getVesselUuid() {
		return this.vesselUuid;
	}
	
	public void setVesselUuid(@Nullable UUID uuid) {
		this.vesselUuid = uuid;
		EntityGedoStatue.setBijuSealed(this.tails - 1, EntityGedoStatue.GEDOMAZO_UUID.equals(uuid));
	}

	public String getVesselName() {
		return this.vesselName;
	}
	
	public void setVesselName(String name) {
		this.vesselName = name;
	}

	@Nullable
	public EntityPlayer getJinchurikiPlayer() {
		return this.jinchurikiPlayer;
	}

	public void setVesselEntity(@Nullable Entity entityIn) {
		this.setVesselEntity(entityIn, true);
	}

	public void setVesselEntity(@Nullable Entity entityIn, boolean dirty) {
		if (entityIn == null) {
			this.setVesselUuid(null);
			if (this.getCloakLevel() != 0) {
				this.toggleBijuCloak();
			}
			this.setCloakXPs(ZERO);
			this.vesselName = "";
		} else {
			this.setVesselUuid(entityIn.getUniqueID());
			this.vesselName = entityIn.getName();
			if (this.entity != null) {
				this.entity.setDead();
			}
		}
		this.jinchurikiPlayer = entityIn instanceof EntityPlayer ? (EntityPlayer)entityIn : null;
		if (dirty) {
			this.markDirty();
		}
	}

	public void verifyVesselEntity(Entity entityIn) {
		if (entityIn.getUniqueID().equals(this.vesselUuid)) {
			this.setVesselEntity(entityIn, true);
			System.out.println(this.toString());
		}
	}

	public int[] getCloakXPs() {
		return this.cloakXp;
	}

	public void setCloakXPs(int[] xps) {
		this.cloakXp[0] = xps[0];
		this.cloakXp[1] = xps[1];
		this.cloakXp[2] = xps[2];
	}

	private void saveAndResetWearingTicks(int level) {
		if (level < 3 || this.isAddedToWorld()) {
			int i = level == 3 ? this.entity.getAge() : ItemBijuCloak.getWearingTicks(this.jinchurikiPlayer);
			this.cloakXp[level-1] += i / 20;
			if (level < 3) {
				ItemBijuCloak.setWearingTicks(this.jinchurikiPlayer, 0);
			}
			this.cloakCD += i + (int)((float)level * 2f * i / Math.max(MathHelper.sqrt(MathHelper.sqrt((float)this.cloakXp[level-1])) - 3f, 1f));
			this.markDirty();
		}
	}

	public void addCloakXp(int xp) {
		if (this.cloakLevel >= 1 && this.cloakLevel <= 3) {
			this.cloakXp[this.cloakLevel-1] += xp;
			this.markDirty();
		}
	}

	public int getCloakXp() {
		return this.cloakLevel >= 1 && this.cloakLevel <= 3 ? this.cloakXp[this.cloakLevel-1] : 0;
	}

	public long getCloakCD() {
		return this.cloakCD;
	}

	public void setCloakCD(long time) {
		this.cloakCD = time;
	}

	public void toggleBijuCloak() {
		if (this.jinchurikiPlayer != null) {
			Chakra.Pathway cp = Chakra.pathway(this.jinchurikiPlayer);
			int i = this.cloakLevel <= 0 ? 1 : 0;
			if (i == 1) {
				long l = this.jinchurikiPlayer.world.getTotalWorldTime();
				if (l < this.cloakCD && !this.jinchurikiPlayer.isCreative()) {
					if (!this.jinchurikiPlayer.world.isRemote) {
						this.jinchurikiPlayer.sendStatusMessage(
						 new TextComponentTranslation("chattext.cooldown.formatted", (this.cloakCD - l) / 20L), true);
					}
					return;
				}
				this.cloakCD = l;
				cp.consume(-5000d - this.getCloakXp(), true);
				if (this.jinchurikiPlayer.inventory.armorInventory.get(3).getItem() != ItemBijuCloak.helmet) {
					ItemStack stack = new ItemStack(ItemBijuCloak.helmet);
					stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setInteger("Tails", this.tails);
					ProcedureUtils.swapItemToSlot(this.jinchurikiPlayer, EntityEquipmentSlot.HEAD, stack);
				}
				if (this.jinchurikiPlayer.inventory.armorInventory.get(2).getItem() != ItemBijuCloak.body) {
					ItemStack stack = new ItemStack(ItemBijuCloak.body, 1, this.tails);
					stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setInteger("Tails", this.tails);
					ProcedureUtils.swapItemToSlot(this.jinchurikiPlayer, EntityEquipmentSlot.CHEST, stack);
				}
				if (this.jinchurikiPlayer.inventory.armorInventory.get(1).getItem() != ItemBijuCloak.legs) {
					ItemStack stack = new ItemStack(ItemBijuCloak.legs);
					stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setInteger("Tails", this.tails);
					ProcedureUtils.swapItemToSlot(this.jinchurikiPlayer, EntityEquipmentSlot.LEGS, stack);
				}
			} else {
				this.saveAndResetWearingTicks(this.cloakLevel);
				this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.helmet, -1, -1, null);
				this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.body, -1, -1, null);
				this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.legs, -1, -1, null);
				T biju = this.getEntityInWorld(this.jinchurikiPlayer.world);
				if (biju != null && !biju.isDead) {
					biju.setDead();
				}
				if (cp.isFull()) {
					cp.consume(cp.getAmount() - cp.getMax());
				}
			}
			this.cloakLevel = i;
		}
	}

	public int increaseCloakLevel() {
		if (this.cloakLevel < 3) {
			if ((this.cloakLevel == 1 && this.cloakXp[0] > 3600) || (this.cloakLevel == 2 && this.cloakXp[1] > 4800)) {
				Chakra.pathway(this.jinchurikiPlayer).consume(-10000d, true);
				this.saveAndResetWearingTicks(this.cloakLevel++);
			}
		} else {
			this.cloakLevel = 3;
		}
		if (this.cloakLevel == 3 && this.jinchurikiPlayer != null) {
			this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.helmet, -1, -1, null);
			this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.body, -1, -1, null);
			this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.legs, -1, -1, null);
			T biju = this.spawnEntity(this.jinchurikiPlayer);
			if (biju != null) {
				biju.setLifeSpan(this.cloakXp[2] * 5 + 200);
			}
		}
		return this.cloakLevel;
	}

	public int getCloakLevel() {
		return this.cloakLevel;
	}

	@Nullable
	public BlockPos locateEntity() {
		return this.entity != null ? this.entity.getPosition() : null;
	}

	public int getTails() {
		return this.tails;
	}

	public T spawnEntity(World world, double x, double y, double z, float yaw) {
		try {
			T biju = this.entityClass.getConstructor(World.class).newInstance(world);
			biju.forceSpawn = true;
			biju.rotationYawHead = yaw;
			biju.setLocationAndAngles(x, y, z, yaw, 0f);
			world.spawnEntity(biju);
			biju.forceSpawn = false;
			return biju;
		} catch (Exception exception) {
			throw new Error(exception);
		}
	}

	private T spawnEntity(EntityPlayer jinchuriki) {
		try {
			T biju = this.entityClass.getConstructor(EntityPlayer.class).newInstance(jinchuriki);
			biju.forceSpawn = true;
			jinchuriki.world.spawnEntity(biju);
			biju.forceSpawn = false;
			return biju;
		} catch (Exception exception) {
			throw new Error(exception);
		}
	}

	@Nullable
	public T getEntity() {
		return this.entity;
	}
	
	@Nullable
	public T getEntityInWorld(World world) {
		if (this.entity != null) {
			Entity entity1 = world.getEntityByID(this.entity.getEntityId());
			return entity1 != null && entity1.getClass() == this.entityClass ? (T)entity1 : null;
		}
		return null;
	}

	public abstract void markDirty();

	public String getEntityLocalizedName() {
		return I18n.translateToLocal("entity." + EntityRegistry.instance().getEntry(this.entityClass).getName() + ".name");
	}

	public String toString() {
		EntityPlayer jinchuriki = this.getJinchurikiPlayer();
		return " >>>> " + (jinchuriki != null ? jinchuriki.getName() : this.vesselName) + " is the " + this.getEntityLocalizedName() + " jinchuriki.";
	}

	public interface ITailBeast {
		void fuuinIntoVessel(Entity vessel, int fuuinTime);
		boolean isFuuinInProgress();
		void cancelFuuin();
		void incFuuinProgress(int i);
		float getFuuinProgress();
	}
}
