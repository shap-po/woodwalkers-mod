package tocraft.walkers.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.PlayerUnlocks;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.WalkersType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class WalkersCommand {

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, ctx, b) -> {
            LiteralCommandNode<ServerCommandSource> rootNode = CommandManager
                    .literal("walkers")
                    .requires(source -> source.hasPermissionLevel(2))
                    .build();

            /*
            Used to give the specified Walkers to the specified Player.
             */
            LiteralCommandNode<ServerCommandSource> grantNode = CommandManager
                    .literal("grant")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.literal("everything")
                                    .executes(context -> {
                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        for (WalkersType<?> type : WalkersType.getAllTypes(player.world)) {
                                            if(!PlayerUnlocks.has(player, type)) {
                                                PlayerUnlocks.unlock(player, type);
                                            }
                                        }

                                        return 1;
                                    })
                            )
                            .then(CommandManager.argument("walkers", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        grant(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntitySummonArgumentType.getEntitySummon(context, "walkers"),
                                                null
                                        );
                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                grant(
                                                        context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        EntitySummonArgumentType.getEntitySummon(context, "walkers"),
                                                        nbt
                                                );

                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> revokeNode = CommandManager
                    .literal("revoke")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.literal("everything")
                                    .executes(context -> {
                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        for (WalkersType<?> type : WalkersType.getAllTypes(player.world)) {
                                            if(PlayerUnlocks.has(player, type)) {
                                                PlayerUnlocks.revoke(player, type);
                                            }
                                        }

                                        return 1;
                                    })
                            )
                            .then(CommandManager.argument("walkers", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        revoke(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntitySummonArgumentType.getEntitySummon(context, "walkers"),
                                                null
                                        );
                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                revoke(
                                                        context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        EntitySummonArgumentType.getEntitySummon(context, "walkers"),
                                                        nbt
                                                );

                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> equip = CommandManager
                    .literal("equip")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.argument("walkers", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        equip(context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntitySummonArgumentType.getEntitySummon(context, "walkers"),
                                                null);

                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                equip(context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        EntitySummonArgumentType.getEntitySummon(context, "walkers"),
                                                        nbt);

                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> unequip = CommandManager
                    .literal("unequip")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .executes(context -> {
                                unequip(
                                        context.getSource().getPlayer(),
                                        EntityArgumentType.getPlayer(context, "player")
                                );
                                return 1;
                            })
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> test = CommandManager
                    .literal("test")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                            .then(CommandManager.literal("not")
                                    .then(CommandManager.argument("walkers", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                            .executes(context -> {
                                                return testNot(
                                                        context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        EntitySummonArgumentType.getEntitySummon(context, "walkers")
                                                );
                                            })
                                    )
                            )
                            .then(CommandManager.argument("walkers", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        return test(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntitySummonArgumentType.getEntitySummon(context, "walkers")
                                        );
                                    })
                            )
                    )
                    .build();

            rootNode.addChild(grantNode);
            rootNode.addChild(revokeNode);
            rootNode.addChild(equip);
            rootNode.addChild(unequip);
            rootNode.addChild(test);

            dispatcher.getRoot().addChild(rootNode);
        });
    }

    private static int test(ServerPlayerEntity source, ServerPlayerEntity player, Identifier walkers) {
        EntityType<?> type = Registry.ENTITY_TYPE.get(walkers);

        if(PlayerWalkers.getWalkers(player) != null && PlayerWalkers.getWalkers(player).getType().equals(type)) {
            if(WalkersConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("walkers.test_positive", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
            }

            return 1;
        }

        if(WalkersConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("walkers.test_failed", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
        }

        return 0;
    }

    private static int testNot(ServerPlayerEntity source, ServerPlayerEntity player, Identifier walkers) {
        EntityType<?> type = Registry.ENTITY_TYPE.get(walkers);

        if(PlayerWalkers.getWalkers(player) != null && !PlayerWalkers.getWalkers(player).getType().equals(type)) {
            if(WalkersConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("walkers.test_failed", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
            }

            return 1;
        }

        if(WalkersConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("walkers.test_positive", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
        }

        return 0;
    }

    private static void grant(ServerPlayerEntity source, ServerPlayerEntity player, Identifier id, @Nullable NbtCompound nbt) {
        WalkersType<LivingEntity> type = new WalkersType(Registry.ENTITY_TYPE.get(id));
        Text name = Text.translatable(type.getEntityType().getTranslationKey());

        // If the specified granting NBT is not null, change the WalkersType to reflect potential variants.
        if(nbt != null) {
            NbtCompound copy = nbt.copy();
            copy.putString("id", id.toString());
            ServerWorld serverWorld = source.getWorld();
            Entity loaded = EntityType.loadEntityWithPassengers(copy, serverWorld, it -> it);
            if(loaded instanceof LivingEntity living) {
                type = new WalkersType<>(living);
                name = type.createTooltipText(living);
            }
        }

        if(!PlayerUnlocks.has(player, type)) {
            boolean result = PlayerUnlocks.unlock(player, type);

            if(result && WalkersConfig.getInstance().logCommands()) {
                player.sendMessage(Text.translatable("walkers.unlock_entity", name), true);
                source.sendMessage(Text.translatable("walkers.grant_success", name, player.getDisplayName()), true);
            }
        } else {
            if(WalkersConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("walkers.already_has", player.getDisplayName(), name), true);
            }
        }
    }

    private static void revoke(ServerPlayerEntity source, ServerPlayerEntity player, Identifier id, @Nullable NbtCompound nbt) {
        WalkersType<LivingEntity> type = new WalkersType(Registry.ENTITY_TYPE.get(id));
        Text name = Text.translatable(type.getEntityType().getTranslationKey());

        // If the specified granting NBT is not null, change the WalkersType to reflect potential variants.
        if(nbt != null) {
            NbtCompound copy = nbt.copy();
            copy.putString("id", id.toString());
            ServerWorld serverWorld = source.getWorld();
            Entity loaded = EntityType.loadEntityWithPassengers(copy, serverWorld, it -> it);
            if(loaded instanceof LivingEntity living) {
                type = new WalkersType<>(living);
                name = type.createTooltipText(living);
            }
        }

        if(PlayerUnlocks.has(player, type)) {
            PlayerUnlocks.revoke(player, type);

            if(WalkersConfig.getInstance().logCommands()) {
                player.sendMessage(Text.translatable("walkers.revoke_entity", name), true);
                source.sendMessage(Text.translatable("walkers.revoke_success", name, player.getDisplayName()), true);
            }
        } else {
            if(WalkersConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("walkers.does_not_have", player.getDisplayName(), name), true);
            }
        }
    }

    private static void equip(ServerPlayerEntity source, ServerPlayerEntity player, Identifier walkers, @Nullable NbtCompound nbt) {
        Entity created;

        if(nbt != null) {
            NbtCompound copy = nbt.copy();
            copy.putString("id", walkers.toString());
            ServerWorld serverWorld = source.getWorld();
            created = EntityType.loadEntityWithPassengers(copy, serverWorld, it -> it);
        } else {
            EntityType<?> entity = Registry.ENTITY_TYPE.get(walkers);
            created = entity.create(player.world);
        }

        if(created instanceof LivingEntity living) {
            @Nullable WalkersType<?> defaultType = WalkersType.from(living);

            if(defaultType != null) {
                boolean result = PlayerWalkers.updateWalkers(player, defaultType, (LivingEntity) created);
                if(result && WalkersConfig.getInstance().logCommands()) {
                    source.sendMessage(Text.translatable("walkers.equip_success", Text.translatable(created.getType().getTranslationKey()), player.getDisplayName()), true);
                }
            }
        }
    }

    private static void unequip(ServerPlayerEntity source, ServerPlayerEntity player) {
        boolean result = PlayerWalkers.updateWalkers(player, null, null);

        if(result && WalkersConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("walkers.unequip_success", player.getDisplayName()), false);
        }
    }
}