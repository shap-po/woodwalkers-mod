package tocraft.walkers.ability.impl.specific;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrassEaterAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("eat_grass");
    public static final MapCodec<GrassEaterAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new GrassEaterAbility<>()));

    public final Map<UUID, Integer> eatTick = new HashMap<>();

    @Override
    public void onUse(Player player, T shape, Level world) {
        eatGrass(player);
    }

    public void eatGrass(Player player) {
        eatTick.put(player.getUUID(), Mth.positiveCeilDiv(40, 2));
    }

    @Override
    public Item getIcon() {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse("short_grass"));
    }
}