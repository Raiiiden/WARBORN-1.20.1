package com.raiiiden.warborn.client.shader;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * Commands for testing and controlling shaders
 */
public class ShaderCommand {
    private static final String TEMP_SHADER_PREFIX = "temp_";

    private static final SuggestionProvider<CommandSourceStack> SHADER_ID_SUGGESTIONS = 
        (context, builder) -> {
            Set<String> shaderIds = ShaderRegistry.getInstance().getRegisteredShaderIds();
            return SharedSuggestionProvider.suggest(shaderIds, builder);
        };
    
    /**
     * Registers all shader-related commands
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> shaderCommand = 
            Commands.literal("shader")
                .requires(source -> source.hasPermission(2))

                .then(Commands.literal("list")
                    .executes(ShaderCommand::listShaders))

                .then(Commands.literal("enable")
                    .then(Commands.argument("shaderId", StringArgumentType.word())
                        .suggests(SHADER_ID_SUGGESTIONS)
                        .executes(ShaderCommand::enableShader)))

                .then(Commands.literal("disable")
                    .then(Commands.argument("shaderId", StringArgumentType.word())
                        .suggests(SHADER_ID_SUGGESTIONS)
                        .executes(ShaderCommand::disableShader)))

                .then(Commands.literal("test")
                    .then(Commands.argument("shaderLocation", ResourceLocationArgument.id())
                        .executes(ShaderCommand::testShader)))

                .then(Commands.literal("cleartemp")
                    .executes(ShaderCommand::clearTempShaders));
        
        dispatcher.register(shaderCommand);
    }
    
    /**
     * Lists all registered shaders
     */
    private static int listShaders(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();

        ShaderRegistry registry = ShaderRegistry.getInstance();
        Set<String> shaderIds = registry.getRegisteredShaderIds();

        if (shaderIds.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No shaders are currently registered").withStyle(ChatFormatting.YELLOW), false);
        } else {
            source.sendSuccess(() -> Component.literal("Registered shaders:").withStyle(ChatFormatting.GREEN), false);
            for (String id : shaderIds) {
                boolean isActive = registry.isShaderActive(id);
                ChatFormatting color = isActive ? ChatFormatting.GREEN : ChatFormatting.GRAY;
                source.sendSuccess(() -> Component.literal("- " + id + (isActive ? " (ACTIVE)" : "")).withStyle(color), false);
            }
        }
        
        return shaderIds.size();
    }

    /**
     * Enables a specific shader
     */
    private static int enableShader(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String shaderId = StringArgumentType.getString(ctx, "shaderId");
        CommandSourceStack source = ctx.getSource();

        if (ShaderRegistry.getInstance().setShaderEnabled(shaderId, true)) {
            source.sendSuccess(() -> Component.literal("Enabled shader: " + shaderId).withStyle(ChatFormatting.GREEN), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Shader not found: " + shaderId));
            return 0;
        }
    }
    
    /**
     * Disables a specific shader
     */
    private static int disableShader(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String shaderId = StringArgumentType.getString(ctx, "shaderId");
        CommandSourceStack source = ctx.getSource();

        if (ShaderRegistry.getInstance().setShaderEnabled(shaderId, false)) {
            source.sendSuccess(() -> Component.literal("Disabled shader: " + shaderId).withStyle(ChatFormatting.YELLOW), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Shader not found: " + shaderId));
            return 0;
        }
    }
    
    /**
     * Tests a night vision shader with specified parameters
     */
    private static int testNightVision(CommandContext<CommandSourceStack> ctx, String preset) throws CommandSyntaxException {
        float intensity = FloatArgumentType.getFloat(ctx, "intensity");
        CommandSourceStack source = ctx.getSource();

        String tempId = TEMP_SHADER_PREFIX + "nvg_" + preset + "_" + System.currentTimeMillis();

        switch (preset) {
            case "green":
                registerTempShader(tempId, ShaderPresets.NIGHT_VISION, ShaderPresets.greenNightVision(intensity));
                break;
            case "blue":
                registerTempShader(tempId, ShaderPresets.NIGHT_VISION, ShaderPresets.blueNightVision(intensity));
                break;
            case "white":
                registerTempShader(tempId, ShaderPresets.NIGHT_VISION, ShaderPresets.whiteNightVision(intensity));
                break;
            default:
                source.sendFailure(Component.literal("Unknown preset: " + preset));
                return 0;
        }

        source.sendSuccess(() -> 
            Component.literal("Testing " + preset + " night vision (intensity: " + intensity + ")").withStyle(ChatFormatting.GREEN), true);
        source.sendSuccess(() -> 
            Component.literal("Use '/shader toggle " + tempId + "' to turn it off").withStyle(ChatFormatting.YELLOW), false);
        
        return 1;
    }
    
    /**
     * Tests an arbitrary shader file
     */
    private static int testShader(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation shaderLocation = ResourceLocationArgument.getId(ctx, "shaderLocation");
        CommandSourceStack source = ctx.getSource();

        String tempId = TEMP_SHADER_PREFIX + "custom_" + System.currentTimeMillis();

        registerTempShader(tempId, shaderLocation, postChain -> {});
        
        source.sendSuccess(() -> 
            Component.literal("Testing shader: " + shaderLocation).withStyle(ChatFormatting.GREEN), true);
        source.sendSuccess(() -> 
            Component.literal("Use '/shader toggle " + tempId + "' to turn it off").withStyle(ChatFormatting.YELLOW), false);
        
        return 1;
    }
    
    /**
     * Clears all temporary test shaders
     */
    private static int clearTempShaders(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        int count = ShaderRegistry.getInstance().removeShadersByPrefix(TEMP_SHADER_PREFIX);
        
        if (count > 0) {
            source.sendSuccess(() -> 
                Component.literal("Removed " + count + " temporary shader" + (count == 1 ? "" : "s")).withStyle(ChatFormatting.GREEN), true);
        } else {
            source.sendSuccess(() -> 
                Component.literal("No temporary shaders to remove").withStyle(ChatFormatting.YELLOW), false);
        }
        
        return count;
    }
    
    /**
     * Registers a temporary test shader
     */
    private static void registerTempShader(String id, ResourceLocation location, java.util.function.Consumer<net.minecraft.client.renderer.PostChain> configurer) {

        ShaderRegistry.getInstance().unregisterShader(id);

        ShaderRegistry.getInstance().registerShader(
            id,
            location,
            mc -> true,
            configurer
        );
    }
} 