package tocraft.walkers.api;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
//#if MC>1182
import org.jetbrains.annotations.ApiStatus;
import tocraft.walkers.impl.tick.shapes.FrogTickHandler;
import tocraft.walkers.impl.tick.shapes.WardenTickHandler;
//#endif
import tocraft.walkers.impl.tick.shapes.JumpBoostTickHandler;
import tocraft.walkers.impl.tick.shapes.SnowGolemTickHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class WalkersTickHandlers {

    private static final Map<EntityType<?>, WalkersTickHandler<?>> HANDLERS = new LinkedHashMap<>();

    @ApiStatus.Internal
    public static void initialize() {
        //#if MC>1182
        register(EntityType.WARDEN, new WardenTickHandler());
        register(EntityType.FROG, new FrogTickHandler());
        register(EntityType.CAMEL, new JumpBoostTickHandler<>(0));
        //#endif
        register(EntityType.SNOW_GOLEM, new SnowGolemTickHandler());
        register(EntityType.RABBIT, new JumpBoostTickHandler<>(1));
        register(EntityType.GOAT, new JumpBoostTickHandler<>(2));
        register(EntityType.MAGMA_CUBE, new JumpBoostTickHandler<>(2));
        register(EntityType.SLIME, new JumpBoostTickHandler<>(2));
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, WalkersTickHandler<T> handler) {
        HANDLERS.put(type, handler);
    }

    @ApiStatus.Internal
    public static Map<EntityType<?>, WalkersTickHandler<?>> getHandlers() {
        return HANDLERS;
    }
}
