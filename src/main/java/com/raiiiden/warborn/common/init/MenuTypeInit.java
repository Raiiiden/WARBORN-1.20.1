package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.menu.WorkstationMenu;
import com.raiiiden.warborn.common.object.BackpackMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuTypeInit {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, WARBORN.MODID);

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }

    public static final RegistryObject<MenuType<BackpackMenu>> BACKPACK_MENU =
            MENU_TYPES.register("backpack", () -> IForgeMenuType.create(BackpackMenu::new));

    // Both workstations share one menu type; the station kind travels in the open packet.
    public static final RegistryObject<MenuType<WorkstationMenu>> WORKSTATION_MENU =
            MENU_TYPES.register("workstation", () -> IForgeMenuType.create(WorkstationMenu::new));
}
