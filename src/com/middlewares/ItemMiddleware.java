package com.middlewares;

import com.girlkun.consts.ConstItem;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import sun.security.mscapi.CPublicKey;

import java.util.ArrayList;
import java.util.List;

public class ItemMiddleware {

    public static ItemMiddleware i;

    public static ItemMiddleware gI() {
        if (i == null) {
            i = new ItemMiddleware();
        }
        return i;
    }

    public void sendItemHuyDiet(int type, Player player) {
        int gender = player.gender;
        Item item = null;
        int tempId = -1;
        int itemParam = 0;
        List<Item.ItemOption> itemOptions = new ArrayList<>();
        switch (type) {
            case ConstItem.WEAPON_TYPE_GANG:
                do {
                    itemParam = Util.nextInt(ConstItem.GANG_HUY_DIET_MIN_MAX[0], ConstItem.GANG_HUY_DIET_MIN_MAX[1]);
                }
                while (!Util.isTrueMin(itemParam, ConstItem.GANG_HUY_DIET_MIN_MAX[1]));
                itemOptions.add(new Item.ItemOption(ItemService.gI().getItemOptionTemplate(ConstItem.OPTION_GANG[0]), itemParam));
                switch (gender) {
                    case 0:
                        tempId = 657;
                        break;
                    case 1:
                        tempId = 659;
                        break;
                    case 2:
                        tempId = 661;
                        break;
                }
                break;
            case ConstItem.WEAPON_TYPE_QUAN:
                itemParam = Util.nextInt(ConstItem.QUAN_HUY_DIET_MIN_MAX[0], ConstItem.QUAN_HUY_DIET_MIN_MAX[1]);
                itemOptions.add(new Item.ItemOption(ItemService.gI().getItemOptionTemplate(ConstItem.OPTION_QUAN[0]), itemParam));
                switch (gender) {
                    case 0:
                        tempId = 651;
                        break;
                    case 1:
                        tempId = 653;
                        break;
                    case 2:
                        tempId = 655;
                        break;
                }
                break;
            case ConstItem.WEAPON_TYPE_AO:
                itemParam = Util.nextInt(ConstItem.AO_HUY_DIET_MIN_MAX[0], ConstItem.AO_HUY_DIET_MIN_MAX[1]);
                itemOptions.add(new Item.ItemOption(ItemService.gI().getItemOptionTemplate(ConstItem.OPTION_AO[0]), itemParam));
                switch (gender) {
                    case 0:
                        tempId = 650;
                        break;
                    case 1:
                        tempId = 652;
                        break;
                    case 2:
                        tempId = 654;
                        break;
                }
                break;
            case ConstItem.WEAPON_TYPE_GIAY:
                itemParam = Util.nextInt(ConstItem.GIAY_HUY_DIET_MIN_MAX[0], ConstItem.GIAY_HUY_DIET_MIN_MAX[1]);
                itemOptions.add(new Item.ItemOption(ItemService.gI().getItemOptionTemplate(ConstItem.OPTION_GIAY[0]), itemParam));
                switch (gender) {
                    case 0:
                        tempId = 658;
                        break;
                    case 1:
                        tempId = 660;
                        break;
                    case 2:
                        tempId = 662;
                        break;
                }
                break;
            default:
                itemParam = Util.nextInt(ConstItem.NHAN_HUY_DIET_MIN_MAX[0], ConstItem.NHAN_HUY_DIET_MIN_MAX[1]);
                itemOptions.add(new Item.ItemOption(ItemService.gI().getItemOptionTemplate(ConstItem.OPTION_NHAN[0]), itemParam));
                tempId = 656;
        }

        if (tempId != -1) {
            item = ItemService.gI().createItemDoHuyDiet(tempId, 1);
        }
        // gen item option data
        itemOptions.add(new Item.ItemOption(ItemService.gI().getItemOptionTemplate(ConstItem.ITEM_REQURED_POWER), 20));

        itemOptions.add(new Item.ItemOption(ItemService.gI().getItemOptionTemplate(ConstItem.ITEM_NOT_TRANSACTION), 0));
        item.itemOptions = itemOptions;
        InventoryServiceNew.gI().addItemBag(player, item);
        Item hop_qua_huy_diet = InventoryServiceNew.gI().findItemBag(player, 2045);
        if (hop_qua_huy_diet != null) {
            InventoryServiceNew.gI().subQuantityItemsBag(player, hop_qua_huy_diet, 1);
        }
        InventoryServiceNew.gI().sendItemBags(player);

        Service.gI().sendThongBao(player, String.format("Bạn vừa nhận được %s ", item.template.name));
    }
}
