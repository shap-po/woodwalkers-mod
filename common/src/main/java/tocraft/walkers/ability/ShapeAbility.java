package tocraft.walkers.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.CEntity;
import tocraft.walkers.Walkers;

@SuppressWarnings("resource")
public abstract class ShapeAbility<E extends LivingEntity> {
    abstract public ResourceLocation getId();

    /**
     * Defines the use action of this ability. Implementers can assume the ability checks, such as cool-downs, have successfully passed.
     *
     * @param player player using the ability
     * @param shape  current shape of the player
     * @param world  world the player is residing in
     */
    abstract public void onUse(Player player, E shape, Level world);

    /**
     * @return cooldown of this ability, in ticks, after it is used.
     */
    public int getCooldown(E entity) {
        String id = EntityType.getKey(entity.getType()).toString();

        // put default cool-down into config if it's not already present
        if (!CEntity.level(entity).isClientSide() && !Walkers.CONFIG.abilityCooldownMap.containsKey(id)) {
            Walkers.CONFIG.abilityCooldownMap.put(id, this.getDefaultCooldown());
            Walkers.CONFIG.save();
        }

        return Walkers.CONFIG.abilityCooldownMap.getOrDefault(id, getDefaultCooldown());
    }

    public int getDefaultCooldown() {
        return 20;
    }

    abstract public Item getIcon();
}
