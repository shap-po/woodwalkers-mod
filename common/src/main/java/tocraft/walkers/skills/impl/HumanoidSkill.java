package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class HumanoidSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("humanoid");
    public static final Codec<HumanoidSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.stable(new HumanoidSkill<>()));

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
