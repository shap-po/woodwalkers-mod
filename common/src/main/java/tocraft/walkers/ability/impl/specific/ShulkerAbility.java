package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.CEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.mixin.accessor.ShulkerAccessor;

@SuppressWarnings("resource")
public class ShulkerAbility<T extends Shulker> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("shulker");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        LivingEntity target = CEntity.level(player).getNearestEntity(CEntity.level(player).getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20, 4.0, 20), livingEntity -> true), TargetingConditions.forCombat().range(20).selector((livingEntity) -> !livingEntity.is(player)), player, player.getX(), player.getEyeY(), player.getZ());

        if (target != null) {
            CEntity.level(player).addFreshEntity(new ShulkerBullet(CEntity.level(player), player, target, player.getDirection().getAxis()));
        }
        player.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);

        ((ShulkerAccessor) shape).callSetRawPeekAmount(100);
    }

    @Override
    public Item getIcon() {
        return Items.SHULKER_SHELL;
    }

    @Override
    public int getDefaultCooldown() {
        return 80;
    }
}
