package tocraft.walkers.skills;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.skills.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SkillRegistry {
    private static final Map<Predicate<LivingEntity>, ShapeSkill<?>> skills = new HashMap<>();
    private static final Map<ResourceLocation, Codec<? extends ShapeSkill<?>>> skillCodecs = new HashMap<>();


    @SuppressWarnings("unchecked")
    public static void init() {
        // register skill codecs
        registerCodec(MobEffectSkill.ID, MobEffectSkill.CODEC);
        registerCodec(BurnInDaylightSkill.ID, BurnInDaylightSkill.CODEC);
        registerCodec(FlyingSkill.ID, FlyingSkill.CODEC);
        registerCodec(PreySkill.ID, PreySkill.CODEC);
        registerCodec(TemperatureSkill.ID, TemperatureSkill.CODEC);
        registerCodec(RiderSkill.ID, RiderSkill.CODEC);
        registerCodec(StandOnFluidSkill.ID, StandOnFluidSkill.CODEC);
        registerCodec(NoPhysicsSkill.ID, NoPhysicsSkill.CODEC);
        registerCodec(CantSwimSkill.ID, CantSwimSkill.CODEC);
        registerCodec(UndrownableSkill.ID, UndrownableSkill.CODEC);
        registerCodec(SlowFallingSkill.ID, SlowFallingSkill.CODEC);
        registerCodec(HunterSkill.ID, HunterSkill.CODEC);
        registerCodec(ClimbBlocksSkill.ID, ClimbBlocksSkill.CODEC);
        registerCodec(ReinforcementsSkill.ID, ReinforcementsSkill.CODEC);
        // register skills
        // mob effects
        registerByClass(Bat.class, new MobEffectSkill<>(new MobEffectInstance(MobEffects.NIGHT_VISION, 100000, 0, false, false)));
        // burn in daylight
        registerByClass(Zombie.class, new BurnInDaylightSkill<>());
        registerByClass(Skeleton.class, new BurnInDaylightSkill<>());
        registerByClass(Stray.class, new BurnInDaylightSkill<>());
        registerByClass(Phantom.class, new BurnInDaylightSkill<>());
        // flying
        registerByClass(Allay.class, new FlyingSkill<>());
        registerByClass(Bat.class, new FlyingSkill<>());
        registerByClass(Bee.class, new FlyingSkill<>());
        registerByClass(Blaze.class, new FlyingSkill<>());
        registerByClass(EnderDragon.class, new FlyingSkill<>());
        registerByClass(FlyingMob.class, new FlyingSkill<>());
        registerByClass(Parrot.class, new FlyingSkill<>());
        registerByClass(Vex.class, new FlyingSkill<>());
        registerByClass(WitherBoss.class, new FlyingSkill<>());
        // wolf prey
        registerByClass(Bat.class, (PreySkill<Bat>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Fox.class, (PreySkill<Fox>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Sheep.class, (PreySkill<Sheep>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Skeleton.class, (PreySkill<Skeleton>) PreySkill.ofHunterClass(Wolf.class));
        registerByClass(Parrot.class, (PreySkill<Parrot>) PreySkill.ofHunterClass(Wolf.class));
        // fox prey
        registerByClass(Chicken.class, (PreySkill<Chicken>) PreySkill.ofHunterClass(Fox.class));
        registerByClass(Rabbit.class, (PreySkill<Rabbit>) PreySkill.ofHunterClass(Fox.class));
        registerByPredicate(entity -> entity instanceof Turtle && entity.isBaby(), PreySkill.ofHunterClass(Fox.class));
        // hostile attacked by iron golem
        registerByPredicate(entity -> entity instanceof Enemy && !(entity instanceof Creeper), PreySkill.ofHunterClass(IronGolem.class));
        // hurt by high temperature
        registerByClass(SnowGolem.class, new TemperatureSkill<>());
        // ravager riding
        registerByClass(Evoker.class, (RiderSkill<Evoker>) RiderSkill.ofRideableClass(Ravager.class));
        registerByClass(Pillager.class, (RiderSkill<Pillager>) RiderSkill.ofRideableClass(Ravager.class));
        registerByClass(Vindicator.class, (RiderSkill<Vindicator>) RiderSkill.ofRideableClass(Ravager.class));
        // Zombie Horse and Skeleton Horse riding
        registerByPredicate(entity -> entity instanceof Enemy, new RiderSkill<>(List.of(rideable -> rideable instanceof AbstractHorse && rideable instanceof Enemy)));
        // lava walking
        registerByClass(Strider.class, new StandOnFluidSkill<>(FluidTags.LAVA));
        // fall through blocks
        registerByClass(Vex.class, new NoPhysicsSkill<>());
        // can't swim
        registerByClass(IronGolem.class, new CantSwimSkill<>());
        // undrownable
        registerByClass(IronGolem.class, new UndrownableSkill<>());
        // hunter
        registerByClass(Wolf.class, (HunterSkill<Wolf>) HunterSkill.ofPreyClass(AbstractSkeleton.class));
        registerByPredicate(entity -> entity instanceof Ocelot || entity instanceof Cat, HunterSkill.ofPreyClass(Creeper.class));
        // climb blocks
        registerByClass(Spider.class, new ClimbBlocksSkill<>());
        registerByClass(Spider.class, new ClimbBlocksSkill<>(List.of(Blocks.COBWEB), new ArrayList<>()));
        // reinforcements
        registerByClass(Wolf.class, new ReinforcementsSkill<>());
    }

    /**
     * @return a list of every available skill for the specified entity
     */
    @SuppressWarnings("unchecked")
    public static <L extends LivingEntity> List<ShapeSkill<L>> getAll(@NotNull L shape) {
        List<ShapeSkill<L>> skillList = new ArrayList<>();
        List<ShapeSkill<?>> unformulatedSkills = new ArrayList<>(skills.entrySet().stream().filter(entry -> entry.getKey().test(shape)).map(Map.Entry::getValue).toList());
        for (ShapeSkill<?> unformatedSkill : unformulatedSkills) {
            skillList.add((ShapeSkill<L>) unformatedSkill);
        }
        return skillList;
    }

    /**
     * @return a list of every available skill for the specified entity
     */
    public static <L extends LivingEntity> List<ShapeSkill<L>> get(@NotNull L shape, ResourceLocation skillId) {
        List<ShapeSkill<L>> skillList = new ArrayList<>();
        for (ShapeSkill<L> skill : getAll(shape)) {
            if (skill.getId() == skillId) {
                skillList.add(skill);
            }
        }
        return skillList;
    }


    public static <A extends LivingEntity> void registerByType(EntityType<A> type, ShapeSkill<A> skill) {
        registerByPredicate(livingEntity -> type.equals(livingEntity.getType()), skill);
    }

    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, ShapeSkill<A> skill) {
        registerByPredicate(entityClass::isInstance, skill);
    }

    /**
     * Register a skill for a predicate
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param skill           your {@link ShapeAbility}
     */
    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, ShapeSkill<?> skill) {
        skills.put(entityPredicate, skill);
    }

    public static void registerCodec(ResourceLocation skillId, Codec<? extends ShapeSkill<?>> skillCodec) {
        skillCodecs.put(skillId, skillCodec);
    }

    @Nullable
    public static Codec<? extends ShapeSkill<?>> getSkillCodec(ResourceLocation skillId) {
        return skillCodecs.get(skillId);
    }

    @Nullable
    public static ResourceLocation getSkillId(ShapeSkill<?> skill) {
        for (Map.Entry<ResourceLocation, Codec<? extends ShapeSkill<?>>> resourceLocationCodecEntry : skillCodecs.entrySet()) {
            if (resourceLocationCodecEntry.getValue() == skill.codec()) {
                return resourceLocationCodecEntry.getKey();
            }
        }
        return null;
    }

    public static <L extends LivingEntity> boolean has(@NotNull L shape, ResourceLocation skillId) {
        for (ShapeSkill<L> skill : getAll(shape)) {
            if (skill.getId() == skillId) {
                return true;
            }
        }
        return false;
    }
}