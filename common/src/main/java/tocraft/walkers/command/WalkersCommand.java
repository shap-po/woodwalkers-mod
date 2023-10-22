package tocraft.walkers.command;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.events.common.CommandEvents;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;

public class WalkersCommand {

	public static void register() {
		CommandEvents.REGISTRATION.register((dispatcher, ctx, b) -> {
			LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("walkers")
					.requires(source -> source.hasPermission(2)).build();

			/*
			 * Used to remove the second shape of the specified Player.
			 */
			LiteralCommandNode<CommandSourceStack> remove2ndShape = Commands.literal("remove2ndShape")
					.then(Commands.argument("player", EntityArgument.players()).executes(context -> {
						remove2ndShape(context.getSource(), EntityArgument.getPlayer(context, "player"));
						return 1;
					})).executes(context -> {
						remove2ndShape(context.getSource(), context.getSource().getPlayer());
						return 1;
					}).build();

			/*
			 * Used to give the specified shape to the specified Player.
			 */
			LiteralCommandNode<CommandSourceStack> change2ndShape = Commands.literal("change2ndShape")
					.then(Commands.argument("shape", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE))
							.suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
								change2ndShape(context.getSource(), context.getSource().getPlayer(),
										EntityType.getKey(
												ResourceArgument.getSummonableEntityType(context, "shape").value()),
										null);
								return 1;
							}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(context -> {
								CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");

								change2ndShape(context.getSource(), context.getSource().getPlayer(),
										EntityType.getKey(
												ResourceArgument.getSummonableEntityType(context, "shape").value()),
										nbt);

								return 1;
							})))
					.then(Commands.argument("player", EntityArgument.players())
							.then(Commands.argument("shape", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE))
									.suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
										change2ndShape(context.getSource(), EntityArgument.getPlayer(context, "player"),
												EntityType.getKey(ResourceArgument
														.getSummonableEntityType(context, "shape").value()),
												null);
										return 1;
									}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
											.executes(context -> {
												CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");

												change2ndShape(context.getSource(),
														EntityArgument.getPlayer(context, "player"),
														EntityType.getKey(ResourceArgument
																.getSummonableEntityType(context, "shape").value()),
														nbt);

												return 1;
											}))))
					.build();

			LiteralCommandNode<CommandSourceStack> switchShape = Commands.literal("switchShape")
					.then(Commands.argument("shape", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE))
							.suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
								switchShape(context.getSource(), context.getSource().getPlayer(),
										EntityType.getKey(
												ResourceArgument.getSummonableEntityType(context, "shape").value()),
										null);

								return 1;
							}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(context -> {
								CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");

								switchShape(context.getSource(), context.getSource().getPlayer(),
										EntityType.getKey(
												ResourceArgument.getSummonableEntityType(context, "shape").value()),
										nbt);

								return 1;
							})))
					.then(Commands.argument("player", EntityArgument.players()).executes(context -> {
						switchShape(context.getSource(), EntityArgument.getPlayer(context, "player"));
						return 1;
					}).then(Commands.argument("shape", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE))
							.suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
								switchShape(context.getSource(), EntityArgument.getPlayer(context, "player"),
										EntityType.getKey(
												ResourceArgument.getSummonableEntityType(context, "shape").value()),
										null);

								return 1;
							}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(context -> {
								CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");

								switchShape(context.getSource(), EntityArgument.getPlayer(context, "player"),
										EntityType.getKey(
												ResourceArgument.getSummonableEntityType(context, "shape").value()),
										nbt);

								return 1;
							}))))
					.build();

			LiteralCommandNode<CommandSourceStack> show2ndShape = Commands.literal("show2ndShape").executes(context -> {
				return show2ndShape(context.getSource(), context.getSource().getPlayer());
			}).then(Commands.argument("player", EntityArgument.player()).executes(context -> {
				return show2ndShape(context.getSource(), EntityArgument.getPlayer(context, "player"));
			})).build();

			rootNode.addChild(remove2ndShape);
			rootNode.addChild(change2ndShape);
			rootNode.addChild(switchShape);
			rootNode.addChild(show2ndShape);

			dispatcher.getRoot().addChild(rootNode);
		});
	}

	private static int show2ndShape(CommandSourceStack source, ServerPlayer player) {

		if (((PlayerDataProvider) player).get2ndShape() != null) {
			if (Walkers.CONFIG.logCommands) {
				source.sendSystemMessage(Component.translatable("walkers.show2ndShapeNot_positive",
						player.getDisplayName(), Component.translatable(
								((PlayerDataProvider) player).get2ndShape().getEntityType().getDescriptionId())));
			}

			return 1;
		} else if (Walkers.CONFIG.logCommands) {
			source.sendSystemMessage(Component.translatable("walkers.show2ndShapeNot_failed", player.getDisplayName()));
		}

		return 0;
	}

	private static void remove2ndShape(CommandSourceStack source, ServerPlayer player) {

		boolean result = PlayerShapeChanger.change2ndShape(player, null);

		if (result && Walkers.CONFIG.logCommands) {
			player.displayClientMessage(Component.translatable("walkers.remove_entity"), true);
			source.sendSystemMessage(Component.translatable("walkers.deletion_success", player.getDisplayName()));
		}
	};

	private static void change2ndShape(CommandSourceStack source, ServerPlayer player, ResourceLocation id,
			@Nullable CompoundTag nbt) {
		ShapeType<LivingEntity> type = new ShapeType(BuiltInRegistries.ENTITY_TYPE.get(id));
		Component name = Component.translatable(type.getEntityType().getDescriptionId());

		// If the specified granting NBT is not null, change the ShapeType to reflect
		// potential variants.
		if (nbt != null) {
			CompoundTag copy = nbt.copy();
			copy.putString("id", id.toString());
			ServerLevel serverWorld = source.getLevel();
			Entity loaded = EntityType.loadEntityRecursive(copy, serverWorld, it -> it);
			if (loaded instanceof LivingEntity living) {
				type = new ShapeType<>(living);
				name = type.createTooltipText(living);
			}
		}

		if (((PlayerDataProvider) player).get2ndShape() != type) {
			boolean result = PlayerShapeChanger.change2ndShape(player, type);

			if (result && Walkers.CONFIG.logCommands) {
				player.sendSystemMessage(Component.translatable("walkers.unlock_entity", name));
				source.sendSystemMessage(
						Component.translatable("walkers.grant_success", name, player.getDisplayName()));
			}
		} else {
			if (Walkers.CONFIG.logCommands) {
				source.sendSystemMessage(Component.translatable("walkers.already_has", player.getDisplayName(), name));
			}
		}
	};

	private static void switchShape(CommandSourceStack source, ServerPlayer player, ResourceLocation shape,
			@Nullable CompoundTag nbt) {
		Entity created;

		if (nbt != null) {
			CompoundTag copy = nbt.copy();
			copy.putString("id", shape.toString());
			ServerLevel serverWorld = source.getLevel();
			created = EntityType.loadEntityRecursive(copy, serverWorld, it -> it);
		} else {
			EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.get(shape);
			created = entity.create(player.level());
		}

		if (created instanceof LivingEntity living) {
			@Nullable
			ShapeType<?> defaultType = ShapeType.from(living);

			if (defaultType != null) {
				boolean result = PlayerShape.updateShapes(player, defaultType, (LivingEntity) created);
				if (result && Walkers.CONFIG.logCommands) {
					source.sendSystemMessage(Component.translatable("walkers.switchShape_success",
							player.getDisplayName(), Component.translatable(created.getType().getDescriptionId())));
				}
			}
		}
	}

	private static void switchShape(CommandSourceStack source, ServerPlayer player) {
		boolean result = PlayerShape.updateShapes(player, null, null);

		if (result && Walkers.CONFIG.logCommands) {
			source.sendSystemMessage(
					Component.translatable("walkers.switchShape_human_success", player.getDisplayName()));
		}
	}
}
