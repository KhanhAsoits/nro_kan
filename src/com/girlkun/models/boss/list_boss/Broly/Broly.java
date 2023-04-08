package com.girlkun.models.boss.list_boss.Broly;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.MapService;
import com.girlkun.services.PetService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;
import com.middlewares.CombineMiddleware;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.Random;


public class Broly extends Boss {

    public Broly() throws Exception {
        super(BossID.BROLY, BossesData.BROLY_1, BossesData.BROLY_2, BossesData.BROLY_3, BossesData.BROLY_4);
        BossesData.BROLY_1.setName(String.format("Broly %d", Util.nextInt(1, 999)));
    }

    @Override
    public void die(Player plKill) {
        if (this.currentLevel == 0) {
            // cho de
            if (plKill.pet == null) {
                Service.gI().sendThongBao(plKill, "Bạn nhân được đệ tử.");
                PetService.gI().createNormalPet(plKill, plKill.gender);
                ChangeMapService.gI().backHome(plKill);
            }
        }
        super.die(plKill);
    }

    @Override
    public void reward(Player plKill) {
        if (this.currentLevel > 0) {
            // chi tinh tu broly 2
            // huy die
            super.getVipPoint(plKill);
            int[] itemDos = new int[]{555, 556, 557, 558, 559, 560, 561, 562};
            int[] NRs = new int[]{17, 18};
            int randomDo = new Random().nextInt(itemDos.length);
            int randomNR = new Random().nextInt(NRs.length);
            if (Util.isTrue(5, 100)) {
                if (CombineMiddleware.gI().canDrop1s(this.zone, this.location.x, this.location.y, plKill.id)) {
                    return;
                }
                if (Util.isTrue(1, 50)) {
                    // ty le roi do than la 2/10
                    Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 15, 1, this.location.x, this.location.y, plKill.id));
                    return;
                }
                Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
            } else {
                Service.gI().dropItemMap(this.zone, new ItemMap(zone, NRs[randomNR], 1, this.location.x, this.location.y, plKill.id));
            }
        }

    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 2);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
}





















