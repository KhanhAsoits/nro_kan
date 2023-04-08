package com.middlewares;

import com.girlkun.consts.ConstItem;
import com.girlkun.models.Template;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.Zone;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.PetService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class CombineMiddleware {

    public static CombineMiddleware i;


    public static CombineMiddleware gI() {
        if (i == null) {
            i = new CombineMiddleware();
        }
        return i;
    }


    public boolean canCallNamecDragon(Player player) {
        try {

            // get clan player
            List<Player> clanMember = player.clan.membersInGame;
            int memberOfClanCount = 1;
            int countOfDragon = 1;

            List<Player> playerInZone = player.zone.getPlayers();
            for (int i = 0; i < playerInZone.size(); i++) {
                for (int j = 0; j < clanMember.size(); j++) {
                    //c
                    if (clanMember.get(j).getSession().userId == playerInZone.get(i).getSession().userId) {
                        memberOfClanCount++;
                        if (clanMember.get(j).idNRNM == ConstItem.ID_NRNM_1S ||
                                clanMember.get(j).idNRNM == ConstItem.ID_NRNM_2S
                                || clanMember.get(j).idNRNM == ConstItem.ID_NRNM_3S
                                || clanMember.get(j).idNRNM == ConstItem.ID_NRNM_4S
                                || clanMember.get(j).idNRNM == ConstItem.ID_NRNM_5S
                                || clanMember.get(j).idNRNM == ConstItem.ID_NRNM_6S
                                || clanMember.get(j).idNRNM == ConstItem.ID_NRNM_7S
                        ) {
                            countOfDragon++;
                        }
                    }
                }
            }

            if (memberOfClanCount < 7) {
                Service.gI().sendThongBao(player, "Cần ít nhất 7 thành viên bang trong map.");
                return false;
            } else {
                if (countOfDragon < 7) {
                    Service.gI().sendThongBao(player, "Cần 7 viên ngọc rồng để ước.");
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return false;
        }
    }

    public boolean canDrop1s(Zone zone, int x, int y, long pKillId) {
        if (Util.isTrue(1, 100)) {
            Service.gI().dropItemMap(zone, Util.ratiItem(zone, 14, 1, x, y, pKillId));
            return true;
        }
        return false;
    }

    public void ChangeBeerusPet(Player player, Npc npc) {
        // check has item
        if (player.getSession().diem_huy_diet - 10 >= 0) {
            int[] quans = {0, 0, 0};
            List<Item> items = player.inventory.itemsBag;
            for (Item item : items) {
                if (item.isNotNullItem()) {
                    if (item.template.id == 2046) {
                        quans[0] = item.quantity;
                    }
                    if (item.template.id == 2047) {
                        quans[1] = item.quantity;
                    }
                    if (item.template.id == 2048) {
                        quans[2] = item.quantity;
                    }
                }

            }

            if (quans[0] < 99) {
                Service.gI().sendThongBao(player, String.format("Bạn còn thiếu %d bánh quế", 99 - quans[0]));
                return;
            }
            if (quans[1] < 99) {
                Service.gI().sendThongBao(player, String.format("Bạn còn thiếu %d đùi gà", 99 - quans[1]));
                return;
            }
            if (quans[2] < 99) {
                Service.gI().sendThongBao(player, String.format("Bạn còn thiếu %d bánh bao", 99 - quans[2]));
                return;
            }

            // nếu đủ
            InventoryServiceNew.gI().subQuantityItemsBag(player, InventoryServiceNew.gI().findItemBag(player, 2046), 99);
            InventoryServiceNew.gI().subQuantityItemsBag(player, InventoryServiceNew.gI().findItemBag(player, 2047), 99);
            InventoryServiceNew.gI().subQuantityItemsBag(player, InventoryServiceNew.gI().findItemBag(player, 2048), 99);
            InventoryServiceNew.gI().sendItemBags(player);
            if (player.pet != null) {
                PetService.gI().changeBerusPet(player);
            } else {
                PetService.gI().createBerusPet(player);
            }
            player.getSession().diem_huy_diet -= 10;
            Service.gI().sendThongBao(player, "Đổi đệ thành công!");
            npc.npcChat(player, "Ta đi ngủ đây.");
        } else {
            Service.gI().sendThongBao(player, "Bạn thiếu điểm hủy diệt rồi.");
        }

    }

}
