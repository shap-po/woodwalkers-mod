package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
//#if MC>=1205
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.Holder;
//#else
//$$ import net.minecraft.world.item.alchemy.PotionUtils;
//#endif
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tocraft.craftedcore.patched.CRegistries;
import tocraft.craftedcore.patched.Identifier;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

import java.util.Arrays;
import java.util.List;

public class ThrowPotionsAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    //#if MC>=1205
    public static final List<Holder<Potion>> VALID_POTIONS = Arrays.asList(Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS);
    private final List<Holder<Potion>> validPotions;
    
    public ThrowPotionsAbility(List<Holder<Potion>> validPotions) {
       this.validPotions = validPotions;
    }
    //#else
    //$$ public static final List<Potion> VALID_POTIONS = Arrays.asList(Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS);
    //$$ private final List<Potion> validPotions;
    //$$
    //$$ public ThrowPotionsAbility(List<Potion> validPotions) {
    //$$     this.validPotions = validPotions;
    //$$ }
    //#endif

    public static final ResourceLocation ID = Walkers.id("throw_potion");
    //#if MC>=1205
    public static final MapCodec<ThrowPotionsAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("potions", VALID_POTIONS.stream().map(potion -> BuiltInRegistries.POTION.getKey(potion.value())).toList()).forGetter(o -> o.validPotions.stream().map(potion -> BuiltInRegistries.POTION.getKey(potion.value())).toList())
    ).apply(instance, instance.stable(validPotions -> new ThrowPotionsAbility<>(validPotions.stream().map(potionId -> (Holder<Potion>) BuiltInRegistries.POTION.getHolder(potionId).orElseThrow()).toList()))));
    //#else
    //$$ @SuppressWarnings("unchecked")
    //$$ public static final MapCodec<ThrowPotionsAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
    //$$         Codec.list(ResourceLocation.CODEC).optionalFieldOf("potions", VALID_POTIONS.stream().map(o -> ((Registry<Potion>) CRegistries.getRegistry(Identifier.parse("potion"))).getKey(o)).toList()).forGetter(o -> o.validPotions.stream().map(o1 -> ((Registry<Potion>) CRegistries.getRegistry(Identifier.parse("potion"))).getKey(o1)).toList())
    //$$ ).apply(instance, instance.stable(validPotions -> new ThrowPotionsAbility<>(validPotions.stream().map(o -> ((Registry<Potion>) CRegistries.getRegistry(Identifier.parse("potion"))).get(o)).toList()))));
    //#endif

    public ThrowPotionsAbility() {
        this(VALID_POTIONS);
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        ThrownPotion potionEntity = new ThrownPotion(world, player);
        //#if MC>=1205
         potionEntity.setItem(PotionContents.createItemStack(Items.SPLASH_POTION, validPotions.get(world.random.nextInt(validPotions.size()))));
        //#else
        //$$ potionEntity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), validPotions.get(world.random.nextInt(validPotions.size()))));
        //#endif
        potionEntity.setXRot(-20.0F);
        Vec3 rotation = player.getLookAngle();
        potionEntity.shoot(rotation.x(), rotation.y(), rotation.z(), 0.75F, 8.0F);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITCH_THROW, player.getSoundSource(), 1.0F, 0.8F + world.random.nextFloat() * 0.4F);

        world.addFreshEntity(potionEntity);
    }

    @Override
    public Item getIcon() {
        return Items.POTION;
    }

    @Override
    public int getDefaultCooldown() {
        return 200;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }
}
