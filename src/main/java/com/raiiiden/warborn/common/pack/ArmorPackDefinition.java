package com.raiiiden.warborn.common.pack;

import java.util.List;

// A validated armor pack and the creative tab generated for it.
public record ArmorPackDefinition(
        String id,
        String tabId,
        String translationKey,
        String icon,
        boolean includeGeneralItems,
        List<ArmorItemDefinition> items
) {
    public ArmorPackDefinition {
        items = List.copyOf(items);
    }
}
