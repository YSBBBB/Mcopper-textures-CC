package inventorypreviewpatch.mixin.renders.InventoryPreview;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.entity.IMixinAbstractHorseEntity;
import fi.dy.masa.malilib.mixin.entity.IMixinMerchantEntity;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.InventoryOverlayScreen;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.game.BlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtEntityUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import inventorypreviewpatch.helper.MethodExecuteHelper;
import inventorypreviewpatch.render.WuTongUIOverlay;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fi.dy.masa.malilib.render.InventoryOverlay.Context;
import static inventorypreviewpatch.configs.Configs.Fixes.*;
import static inventorypreviewpatch.configs.Configs.Generic.DISPLAY_BREWING_STAND_PROGRESS;
import static inventorypreviewpatch.configs.Configs.Generic.DISPLAY_FURNACE_PROGRESS;
import static inventorypreviewpatch.render.WuTongUIOverlay.PreviewOverlay;
import static inventorypreviewpatch.render.WuTongUIOverlayHandler.renderFrame;

@Mixin(InventoryOverlayScreen.class)
public class ExtraRenderMixin {

    @Unique
    private static final Identifier MINECART = Identifier.ofVanilla("textures/font/b118.png");
    @Unique
    private static final Identifier BOAT = Identifier.ofVanilla("textures/font/b117.png");
    @Unique
    private static final ItemStack CHEST = Items.CHEST.getDefaultStack();
    @Unique
    private static final ItemStack SADDLE = Items.SADDLE.getDefaultStack();
    @Unique
    private static final ItemStack AIR = Items.AIR.getDefaultStack();
    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    @Final
    @Shadow(remap = false)
    private boolean shulkerBGColors;
    @Final
    @Shadow(remap = false)
    private boolean villagerBGColors;
    @Shadow(remap = false)
    private Context previewData;
    @Shadow(remap = false)
    private int ticks;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void RenderAtHead(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        if (PREVENT_PREVIEWING_OWN_BACKPACK.getBooleanValue()
                && (previewData.entity() != null && mc.player != null)
                && previewData.entity().getUuid().equals(mc.player.getUuid())
        ) return;

        this.ticks++;
        MinecraftClient mc = MinecraftClient.getInstance();
        World world = WorldUtils.getBestWorld(mc);
        boolean useWuTong = INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("wutong");
        if (previewData == null || world == null) return;
        MethodExecuteHelper.startExecute("inventory_preview", -1);  //判断该方法是否正在执行
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        int x = xCenter - 52 / 2;
        int y = yCenter - 92;
        int startSlot = 0;
        int totalSlots = previewData.inv() == null ? 0 : previewData.inv().size();
        NbtCompound nbt = previewData.nbt() == null ? new NbtCompound() : previewData.nbt();
        List<ItemStack> armourItems = new ArrayList<>();
        if (previewData.entity() instanceof AbstractHorseEntity horse) {
            if (HORSE_FIXES.getBooleanValue()) {
                Inventory inv = ((IMixinAbstractHorseEntity) horse).malilib_getHorseInventory();
                this.previewData = new Context(previewData.type(), inv, previewData.be(), previewData.entity(), nbt, previewData.handler());
                if (previewData.inv() == null) return;
                armourItems.add(horse.getEquippedStack(EquipmentSlot.BODY));
                armourItems.add(horse instanceof LlamaEntity ? AIR : previewData.inv().getStack(0));
                if (horse instanceof AbstractDonkeyEntity donkey && donkey.hasChest()) {
                    armourItems.set(horse instanceof LlamaEntity ? 1 : 0, CHEST);
                }
            } else {
                if (previewData.inv() == null) {
                    MaLiLib.LOGGER.warn("InventoryOverlayScreen(): Horse inv() = null");
                    return;
                }
                armourItems.add(previewData.entity().getEquippedStack(EquipmentSlot.BODY));
                armourItems.add(previewData.inv().getStack(0));
            }
            startSlot = 1;
            totalSlots = previewData.inv().size() - 1;

        } else if (previewData.entity() instanceof WolfEntity) {
            armourItems.add(previewData.entity().getEquippedStack(EquipmentSlot.BODY));
        }

        final InventoryOverlay.InventoryRenderType type = (previewData.entity() instanceof VillagerEntity) ? InventoryOverlay.InventoryRenderType.VILLAGER : InventoryOverlay.getBestInventoryType(previewData.inv(), nbt, previewData);
        final InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, totalSlots);
        final int rows = (int) Math.ceil((double) totalSlots / props.slotsPerRow);
        Set<Integer> lockedSlots = new HashSet<>();
        int xInv = xCenter - (props.width / 2);
        int yInv = yCenter - props.height - 6;

        if (rows > 6) {
            yInv -= (rows - 6) * 18;
            y -= (rows - 6) * 18;
        }

        if (MaLiLibReference.DEBUG_MODE) {
            MaLiLib.LOGGER.warn("render():0: type [{}], previewData.type [{}], previewData.inv [{}], previewData.be [{}], previewData.ent [{}], previewData.nbt [{}]", type.toString(), previewData.type().toString(),
                    previewData.inv() != null, previewData.be() != null, previewData.entity() != null, nbt.getString("id"));
            MaLiLib.LOGGER.error("0: -> inv.type [{}] // nbt.type [{}]", previewData.inv() != null ? InventoryOverlay.getInventoryType(previewData.inv()) : null, InventoryOverlay.getInventoryType(nbt));
            MaLiLib.LOGGER.error("1: -> inv.size [{}] // inv.isEmpty [{}]", previewData.inv() != null ? previewData.inv().size() : -1, previewData.inv() != null ? previewData.inv().isEmpty() : -1);
        }

        if (previewData.entity() != null) {
            x = xCenter - 55;
            xInv = xCenter + 2;
            yInv = Math.min(yInv, yCenter - 92);
        }
        if (previewData.be() instanceof CrafterBlockEntity cbe) {
            lockedSlots = BlockUtils.getDisabledSlots(cbe);
        } else if (nbt.contains(NbtKeys.DISABLED_SLOTS)) {
            lockedSlots = NbtBlockUtils.getDisabledSlotsFromNbt(nbt);
        }

        if (!armourItems.isEmpty()) {
            Inventory horseInv = new SimpleInventory(armourItems.toArray(new ItemStack[0]));
            PreviewOverlay.renderInventoryBackground(type, xInv, yInv, 1, horseInv.size(), previewData, mc, drawContext);
            InventoryOverlay.renderInventoryBackgroundSlots(type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, drawContext);
            InventoryOverlay.renderInventoryStacks(type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, horseInv.size(), mc, drawContext, mouseX, mouseY);
            xInv += 32 + 4;
        }

        if (previewData.be() != null && previewData.be().getCachedState().getBlock() instanceof ShulkerBoxBlock sbb) {
            WuTongUIOverlay.PreviewOverlay.setShulkerboxBackgroundTintColor(sbb, this.shulkerBGColors);
        }

        // Inv Display
        if (totalSlots > 0 && previewData.inv() != null) {
            PreviewOverlay.renderInventoryBackground(type, xInv, yInv, props.slotsPerRow, totalSlots, previewData, mc, drawContext);
            // TODO 1.21.4+
            if (type == InventoryOverlay.InventoryRenderType.BREWING_STAND) {
                InventoryOverlay.renderBrewerBackgroundSlots(previewData.inv(), xInv, yInv, drawContext);
            }
            InventoryOverlay.renderInventoryStacks(type, previewData.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, startSlot, totalSlots, lockedSlots, mc, drawContext, mouseX, mouseY);
        }

        // EnderItems Display
        if (previewData.type() == InventoryOverlay.InventoryRenderType.PLAYER && nbt.contains(NbtKeys.ENDER_ITEMS)) {
            EnderChestInventory enderItems = InventoryUtils.getPlayerEnderItemsFromNbt(nbt, world.getRegistryManager());

            if (enderItems == null) {
                enderItems = new EnderChestInventory();
            }

            yInv = yCenter + 6;
            PreviewOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, xInv, yInv, 9, 27, previewData, mc, drawContext);
            InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.GENERIC, enderItems, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mc, drawContext, mouseX, mouseY);
        } else if (previewData.entity() instanceof PlayerEntity player) {
            yInv = yCenter + 6;
            PreviewOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, xInv, yInv, 9, 27, previewData, mc, drawContext);
            InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.GENERIC, player.getEnderChestInventory(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mc, drawContext, mouseX, mouseY);
        }

        // Villager Trades Display
        if (type == InventoryOverlay.InventoryRenderType.VILLAGER && nbt.contains(NbtKeys.OFFERS)) {
            DefaultedList<ItemStack> offers = InventoryUtils.getSellingItemsFromNbt(nbt, world.getRegistryManager());
            Inventory tradeOffers = InventoryUtils.getAsInventory(offers);

            if (tradeOffers != null && !tradeOffers.isEmpty()) {
                int xInvOffset = (xCenter - 55) - (props.width / 2);
                int offerSlotCount = 9;

                yInv = yCenter + 6;

                // Realistically, this should never go above 9; but because Minecraft doesn't have these guard rails, be prepared for it.
                if (offers.size() > 9) {
                    offerSlotCount = 18;
                }

                RenderUtils.setVillagerBackgroundTintColor(NbtEntityUtils.getVillagerDataFromNbt(nbt), this.villagerBGColors);
                PreviewOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, previewData, mc, drawContext);
                InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mc, drawContext, mouseX, mouseY);
            }
        } else if (previewData.entity() instanceof MerchantEntity merchant) {
            TradeOfferList trades = ((IMixinMerchantEntity) merchant).malilib_offers();
            DefaultedList<ItemStack> offers = trades != null ? InventoryUtils.getSellingItems(trades) : DefaultedList.of();
            Inventory tradeOffers = InventoryUtils.getAsInventory(offers);

            if (tradeOffers != null && !tradeOffers.isEmpty()) {
                int xInvOffset = (xCenter - 55) - (props.width / 2);
                int offerSlotCount = 9;

                yInv = yCenter + 6;

                // Realistically, this should never go above 9; but because Minecraft doesn't have these guard rails, be prepared for it.
                if (offers.size() > 9) {
                    offerSlotCount = 18;
                }

                if (merchant instanceof VillagerEntity villager) {
                    RenderUtils.setVillagerBackgroundTintColor(villager.getVillagerData(), this.villagerBGColors);
                }
                PreviewOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, previewData, mc, drawContext);
                InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mc, drawContext, mouseX, mouseY);
            }
        }
        // Entity Display
        if (previewData.entity() != null) {
            InventoryOverlay.renderEquipmentOverlayBackground(x, y, previewData.entity(), drawContext);
            InventoryOverlay.renderEquipmentStacks(previewData.entity(), x, y, mc, drawContext, mouseX, mouseY);
        }

        switch (previewData.be()) {
            case null -> {
                if (!useWuTong) break;
                if (nbt.getString("id").equals("minecraft:chest_minecart")) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, MINECART, xCenter - 8, yCenter - 83, 0.0F, 0.0F, 16, 16, 16, 16);
                } else if (nbt.getString("id").equals("minecraft:hopper_minecart")) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, MINECART, xCenter - 8, yCenter - 49, 0.0F, 0.0F, 16, 16, 16, 16);
                } else if (nbt.getString("id").equals("minecraft:bamboo_chest_raft") || nbt.getString("id").equals("minecraft:oak_chest_boat")) {
                    drawContext.drawTexture(RenderLayer::getGuiTextured, BOAT, xCenter - 8, yCenter - 83, 0.0F, 0.0F, 16, 16, 16, 16);
                }
            }
            case AbstractFurnaceBlockEntity ignore when DISPLAY_FURNACE_PROGRESS.getBooleanValue() ->
                    renderFrame(null, previewData, xInv, yInv, 0, 0, 3);
            case BrewingStandBlockEntity ignore when DISPLAY_BREWING_STAND_PROGRESS.getBooleanValue() ->
                    renderFrame(null, previewData, xInv, yInv, 0, 0, 4);
            default -> {
            }
        }
        // Refresh
        if (ticks % 4 == 0) {
            previewData = previewData.handler().onContextRefresh(previewData, world);
        }
    }
}
