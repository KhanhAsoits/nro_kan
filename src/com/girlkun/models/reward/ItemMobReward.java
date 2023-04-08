package com.girlkun.models.reward;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.Template;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class ItemMobReward {

    private Template.ItemTemplate temp;
    private int[] mapDrop;
    private int[] quantity;
    private int[] ratio;
    private int gender;

    private List<ItemOptionMobReward> option;

    public ItemMobReward(int tempId, int[] mapDrop, int[] quantity, int[] ratio, int gender) {
        this.temp = Manager.ITEM_TEMPLATES.get(tempId);
        this.mapDrop = mapDrop;
        this.quantity = quantity;
        if (this.quantity[0] < 0) {
            this.quantity[0] = -this.quantity[0];
        } else if (this.quantity[0] == 0) {
            this.quantity[0] = 1;
        }
        if (this.quantity[1] < 0) {
            this.quantity[1] = -this.quantity[1];
        } else if (this.quantity[1] == 0) {
            this.quantity[1] = 1;
        }
        if (this.quantity[0] > this.quantity[1]) {
            int tempSwap = this.quantity[0];
            this.quantity[0] = this.quantity[1];
            this.quantity[1] = tempSwap;
        }
        this.ratio = ratio;
        this.gender = gender;
        this.option = new ArrayList<>();
    }

    public ItemMap getItemMap(Zone zone, Player player, int x, int y) {
        for (int mapId : this.mapDrop) {
            if (mapId != -1 && mapId != zone.map.mapId) {
                continue;
            }
            // check power
            if (this.gender != -1 && this.gender != player.gender) {
                // check gender ne
                // nêu do roi la do nm ma player la td thi se ko set ty le roi
                break;
            }

            if (player.nPoint.power > ConstPlayer.POWER_LIMIT_DROP_SKH) {
                // neu player hon 1 toi la se ko roi nua
                break;
            }
            if (Util.isTrue(this.ratio[0], this.ratio[1])) {
                ItemMap itemMap = new ItemMap(zone, this.temp, Util.nextInt(this.quantity[0], this.quantity[1]),
                        x, y, player.id);
                for (ItemOptionMobReward opt : this.option) {
                    if (!Util.isTrue(opt.getRatio()[0], opt.getRatio()[1])) {
                        continue;
                    }
                    if (player.isPet && (opt.getTemp().id >= 127 && opt.getTemp().id <= 135)) {
                        // neu la de thi se ko roi
                        return null;
                    }
                    itemMap.options.add(new Item.ItemOption(opt.getTemp(), Util.nextInt(opt.getParam()[0], opt.getParam()[1])));
                }
                return itemMap;
            }
        }
        return null;
    }

}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - TiMi :)))
 */
