package com.girlkun.services.func;

import com.girlkun.consts.ConstItem;
import com.girlkun.models.Template;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.models.shop.ItemShop;
import com.girlkun.server.Manager;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemMapService;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RedeemPointService {
    public static RedeemPointService i;


    public static RedeemPointService gI() {
        if (i == null) {
            i = new RedeemPointService();
        }
        return i;
    }


    public void doiHopQuaHuyDiet(Player player) {
        List<Item.ItemOption> options = new ArrayList<>();
        Item.ItemOption itemOption = new Item.ItemOption(ItemService.gI().getItemOptionTemplate(30), 0);
        Item.ItemOption itemOption1 = new Item.ItemOption(ItemService.gI().getItemOptionTemplate(212), 0);
        options.add(itemOption1);
        options.add(itemOption);
        Service.gI().sendItemToPlayer(player, 2045, 1, options);
        // tru diem
        player.getSession().vip_point -= ConstItem.PRICE_OF_HOP_QUA_HUY_DIET;
    }

}
