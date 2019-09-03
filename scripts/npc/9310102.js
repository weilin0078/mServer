/*
*   班森奶奶的珠宝公司
*   By: Razoer·瞌睡鱼               如果需要改动，请自行修改，请保留本人创意提名~
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple ("你好， #b#h ##k,我可以让你成为世上最有钱的人。\r\n#k#n只要有#e#b贵金属#k#n.#e#b珠宝#k#n.#e#b水晶#k#n.#e#b陨石#k#n,我就能帮你兑换成金钱。\r\n你目前有： #e#r" + cm.getMeso() + "金币#k#n \r\n#r#L0##e#v4011000#青铜      报酬:金币200000    经验50000#n#l\r\n#k#L1##e#v4011001#钢铁      报酬:金币300000    经验100000#n#l#k\r\n#b#L2##e#v4011002#锂矿石    报酬:金币300000    经验100000#n#l#k\r\n#g#L3##e#v4011003#朱矿石    报酬:金币300000    经验100000#n#l#k\r\n#d#L4##e#v4011004#银        报酬:金币300000    经验100000#n#l#k\r\n#k#L5##e#v4011005#紫矿石    报酬:金币300000    经验100000#n#l#k\r\n#r#L6##e#v4011006#黄金      报酬:金币500000    经验200000#n#l#k\r\n#b#L7##e#v4021000#石榴石    报酬:金币300000    经验100000#n#l#k\r\n#g#L8##e#v4021001#紫水晶    报酬:金币300000    经验100000#n#l#k\r\n#d#L9##e#v4021002#海蓝宝石  报酬:金币300000    经验100000#n#l#k\r\n#r#L10##e#v4021004#蛋白石   报酬:金币300000    经验100000#n#l#k\r\n#d#L11##e#v4021006#黄晶     报酬:金币500000    经验200000#n#l#k\r\n#k#L12##e#v4021003#祖母绿   报酬:金币300000    经验100000#n#l#k\r\n#b#L13##e#v4021005#蓝宝石   报酬:金币300000    经验100000#n#l#k\r\n#g#L14##e#v4021007#钻石     报酬:金币1000000   经验200000#n#l\r\n#k#L15##e#v4021008#黑水晶   报酬:金币1000000   经验200000\r\n#r#L16##e#v4005000#力量水晶 报酬:金币1000000   经验200000#n#l\r\n#d#L17##e#v4005001#智慧水晶 报酬:金币1000000   经验200000#n#l\r\n#g#L18##e#v4005002#敏捷水晶 报酬:金币1000000   经验200000#n#l\r\n#d#L19##e#v4005003#幸运水晶 报酬:金币1000000   经验200000#n#l\r\n#k#L20##e#v4005004#暗黑水晶 报酬:金币1500000   经验300000#n#l\r\n#b#L21##e#v4011007#月石     报酬:金币3000000   经验500000#n#l\r\n#r#L22##e#v4021009#星石     报酬:金币3000000   经验500000#n#l")
        } else if (status == 1) {
            switch(selection) {
        case 0:
            if (cm.haveItem(4011000,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经小有名气大家都称你为#v4011000#青铜大王，并且通过努力获得了200000金币和50000点经验值哦! 继续加油！.");
                cm.gainItem(4011000,-1);
                cm.gainMeso(200000);
                cm.gainExp(50000);
                cm.dispose();
            }else{
                cm.sendOk("最近你的#v4011000#青铜产量很少哦！加油！~.");
                cm.dispose();
            }
            break;
        case 1: 
            if (cm.haveItem(4011001,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经小有名气大家都称你为#v4011001#钢铁大王，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4011001,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("最近你的#v4011001#钢铁产量很少哦！加油！~.");
                cm.dispose();
            }
            break;
         case 2: 
            if (cm.haveItem(4011002,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经小有名气大家都称你为#v4011002#锂矿大王，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4011002,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("最近你的#v4011002#锂矿产量很少哦！加油！~.");
                cm.dispose();
            }
            break;
         case 3: 
            if (cm.haveItem(4011003,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经小有名气大家都称你为#v4011003#朱矿石大王，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4011003,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("最近你的#v4011003#朱矿产量很少哦！加油！~.");
                cm.dispose();
            }
            break;
         case 4: 
            if (cm.haveItem(4011004,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经小有名气大家都称你为#v4011004#白银石大王，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4011004,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("最近你的#v4011004#白银产量很少哦！加油！~.");
                cm.dispose();
            }
            break;
         case 5: 
            if (cm.haveItem(4011005,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经小有名气大家都称你为#v4011005#紫矿石大王，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4011005,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("最近你的#v4011005#紫矿石产量很少哦！加油！~.");
                cm.dispose();
            }
            break;
         case 6: 
            if (cm.haveItem(4011006,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4011006#黄金大亨，并且通过努力获得了500000金币和200000点经验值哦! 继续加油！.");
                cm.gainItem(4011006,-1);
                cm.gainMeso(500000);
                cm.gainExp(200000);
                cm.dispose();
            }else{
                cm.sendOk("#v4011006#黄金可是很贵重的！丝毫都不能缺少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 7: 
            if (cm.haveItem(4021000,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021000#珠宝大亨，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4021000,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021000#石榴石可是很贵重的！丝毫都不能缺少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 8: 
            if (cm.haveItem(4021001,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021001#珠宝大亨，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4021001,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021001#紫水晶可是很贵重的！丝毫都不能缺少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 9: 
            if (cm.haveItem(4021002,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021002#珠宝大亨，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4021002,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021002#海蓝宝石可是很贵重的！丝毫都不能缺少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 10: 
            if (cm.haveItem(4021004,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021004#珠宝大亨，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4021004,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021004#蛋白石可是很贵重的！丝毫都不能缺少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 11: 
            if (cm.haveItem(4021006,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021006#珠宝大亨，并且通过努力获得了500000金币和200000点经验值哦! 继续加油！.");
                cm.gainItem(4021006,-1);
                cm.gainMeso(500000);
                cm.gainExp(200000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021006#黄晶可是很贵重的！丝毫都不能缺少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 12: 
            if (cm.haveItem(4021003,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021003#珠宝大亨，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4021003,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021003#祖母绿可是很贵重的！丝毫都不能缺少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 13: 
            if (cm.haveItem(4021005,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021005#珠宝大亨，并且通过努力获得了300000金币和100000点经验值哦! 继续加油！.");
                cm.gainItem(4021005,-1);
                cm.gainMeso(300000);
                cm.gainExp(100000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021005#蓝宝石可是很贵重的！丝毫都不能缺少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 14: 
            if (cm.haveItem(4021007,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021007#珠宝大亨，并且通过努力获得了1000000金币和200000点经验值哦! 继续加油！.");
                cm.gainItem(4021007,-1);
                cm.gainMeso(1000000);
                cm.gainExp(200000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021007#钻石非常贵重的！一点瑕疵都不能有,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 15: 
            if (cm.haveItem(4021008,1)){
                cm.sendOk("非常的高兴能和你合作，在珠宝业界你已经久负盛名大家都称你为#v4021008#珠宝大亨，并且通过努力获得了1000000金币和200000点经验值哦! 继续加油！.");
                cm.gainItem(4021008,-1);
                cm.gainMeso(1000000);
                cm.gainExp(200000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021008#黑水晶非常贵重的！一点重量都不能少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 16: 
            if (cm.haveItem(4005000,1)){
                cm.sendOk("非常的高兴能和你合作，在水晶业界你已经久负盛名大家都称你为#v4005000#水晶大亨，并且通过努力获得了1000000金币和200000点经验值哦! 继续加油！.");
                cm.gainItem(4005000,-1);
                cm.gainMeso(1000000);
                cm.gainExp(200000);
                cm.dispose();
            }else{
                cm.sendOk("#v4005000#力量水晶非常贵重的！一点重量都不能少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 17: 
            if (cm.haveItem(4005001,1)){
                cm.sendOk("非常的高兴能和你合作，在水晶业界你已经久负盛名大家都称你为#v4005001#水晶大亨，并且通过努力获得了1000000金币和200000点经验值哦! 继续加油！.");
                cm.gainItem(4005001,-1);
                cm.gainMeso(1000000);
                cm.gainExp(200000);
                cm.dispose();
            }else{
                cm.sendOk("#v4005001#智慧水晶非常贵重的！一点重量都不能少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 18: 
            if (cm.haveItem(4005002,1)){
                cm.sendOk("非常的高兴能和你合作，在水晶业界你已经久负盛名大家都称你为#v4005002#水晶大亨，并且通过努力获得了1000000金币和200000点经验值哦! 继续加油！.");
                cm.gainItem(4005002,-1);
                cm.gainMeso(1000000);
                cm.gainExp(200000);
                cm.dispose();
            }else{
                cm.sendOk("#v4005002#敏捷水晶非常贵重的！一点重量都不能少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 19: 
            if (cm.haveItem(4005003,1)){
                cm.sendOk("非常的高兴能和你合作，在水晶业界你已经久负盛名大家都称你为#v4005003#水晶大亨，并且通过努力获得了1000000金币和200000点经验值哦! 继续加油！.");
                cm.gainItem(4005003,-1);
                cm.gainMeso(1000000);
                cm.gainExp(200000);
                cm.dispose();
            }else{
                cm.sendOk("#v4005003#幸运水晶非常贵重的！一点重量都不能少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 20: 
            if (cm.haveItem(4005004,1)){
                cm.sendOk("非常的高兴能和你合作，在水晶业界你已经久负盛名大家都称你为#v4005004#水晶大亨，并且通过努力获得了1500000金币和300000点经验值哦! 继续加油！.");
                cm.gainItem(4005004,-1);
                cm.gainMeso(1500000);
                cm.gainExp(300000);
                cm.dispose();
            }else{
                cm.sendOk("#v4005004#黑暗水晶非常贵重的！一点重量都不能少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 21: 
            if (cm.haveItem(4011007,1)){
                cm.sendOk("非常的高兴能和你合作，在月石业界你已经久负盛名大家都称你为#v4011007#月石大亨，并且通过努力获得了3000000金币和500000点经验值哦! 继续加油！.");
                cm.gainItem(4011007,-1);
                cm.gainMeso(3000000);
                cm.gainExp(500000);
                cm.dispose();
            }else{
                cm.sendOk("#v4011007#月石非常贵重的！一点重量都不能少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
         case 22: 
            if (cm.haveItem(4021009,1)){
                cm.sendOk("非常的高兴能和你合作，在星石业界你已经久负盛名大家都称你为#v4021009#星石大亨，并且通过努力获得了3000000金币和500000点经验值哦! 继续加油！.");
                cm.gainItem(4021009,-1);
                cm.gainMeso(3000000);
                cm.gainExp(500000);
                cm.dispose();
            }else{
                cm.sendOk("#v4021009#星石极其珍贵！一点重量都不能少,继续加油哦小朋友！~.");
                cm.dispose();
            }
            break;
            }
        }
    }
}