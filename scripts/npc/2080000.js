/* Mos
 Leafre : Leafre (240000000)
 
 Refining NPC: 
 * Level 110 weapons - Stimulator allowed
 */

        var status = 0;
var selectedType = -1;
var selectedItem = -1;
var stimulator = false;
var bustedDagger = false;
var item;
var mats;
var matQty;
var cost;
var stimID;

function start() {
    status = -2;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == -1) {
        cm.sendSimple("你好~如果您有興趣升級或修理你的武器，你肯定會來對地方了！我在這個偉大的神木村最好武器製造商。好了，你怎麼想的武器，是充滿了龍的不可思議的力量？你有興趣嗎？\r\n#L0# 做一個龍武器。\r\n#L100# 修理裝備。#l");
    } else if (status == 0) {
        if (selection == 0) {
            if (cm.haveItem(4001079)) {
                bustedDagger = true;
                cm.sendNext("這是什麼？在破獲匕首你似乎老了，我需要 #i" + 4011001 + "# 和 #i" + 4011002 + "#.");
            } else {
                var selStr = "龍的力量是不可低估的。如果你願意，我可以添加自己的權力你的武器之一。但是，武器必須足夠強大，以保持其潛在的...#b";
                var options = new Array("什麼是催化劑?", "做一個劍士武器", "做一個弓箭手武器", "做一個法師武器", "做一個盜賊武器", "做一個海盜武器",
                        "做一個劍士武器使用催化劑", "做一個弓箭手武器使用催化劑", "做一個法師武器使用催化劑", "做一個盜賊武器使用催化劑", "做一個海盜武器使用催化劑");
                for (var i = 0; i < options.length; i++) {
                    selStr += "\r\n#L" + i + "# " + options[i] + "#l";
                }
                cm.sendSimple(selStr);
            }
        } else {
            cm.sendYesNo("太好了！我會告訴你我的能力的。大家都知道，這取決於耐久性的物品的等級和數量的物品的丟失，所以服務費各不相同？您想立即修復你的裝備？");
            status = 99;
        }

    } else if (status == 1) {
        if (bustedDagger) {
            if (cm.haveItem(4011001) && cm.haveItem(4011002) && cm.haveItem(4001079)) {
                cm.gainItem(4011001, -1);
                cm.gainItem(4011002, -1);
                cm.gainItem(4001079, -1);
                cm.gainItem(4001078, 1);
            } else {
                cm.sendOk("你沒有足夠的材料。");
            }
            cm.dispose();
        } else {
            selectedType = selection;
            if (selectedType > 5) {
                stimulator = true;
                selectedType -= 5;
            } else
                stimulator = false;
            if (selectedType == 0) { //What's a stim?
                cm.sendNext("催化劑是一種特殊的藥水，我可以加入到創建某些項目的進程。它給它統計中，就好像從一個怪物下降。然而，它可能有沒有變化，而且也有可能為項低於平均水平。還有沒有得到任何項目使用刺激的時候，所以請明智的選擇有10％的機會。")
                cm.dispose();
            } else if (selectedType == 1) { //warrior weapon
                var selStr = "很好，那麼你想做哪一個？？#b";
                var weapon = new Array("龍泉劍#k - 等級. 110 單手劍#b", "戰龍斧#k - 等級. 110 單手斧#b", "龍頭錘#k - 等級. 110 單手錘#b", "狂龍劍#k - 等級. 110 雙手劍#b", "龍王之斧#k - 等級. 110 雙手斧#b", "龍之焰#k - 等級. 110 雙手錘#b",
                        "幻龍長槍#k - 等級. 110 火槍#b", "赤龍長矛#k - 等級. 110 矛#b");
                for (var i = 0; i < weapon.length; i++) {
                    selStr += "\r\n#L" + i + "# " + weapon[i] + "#l";
                }
                cm.sendSimple(selStr);
            } else if (selectedType == 2) { //bowman weapon
                var selStr = "很好，那麼你想做哪一個？？#b";
                var weapon = new Array("龍形之弓#k - 等級. 110 弓#b", "飛龍弩#k - 等級. 110 弩#b");
                for (var i = 0; i < weapon.length; i++) {
                    selStr += "\r\n#L" + i + "# " + weapon[i] + "#l";
                }
                cm.sendSimple(selStr);
            } else if (selectedType == 3) { //magician weapon
                var selStr = "很好，那麼你想做哪一個？？#b";
                var weapon = new Array("聖龍短杖#k - 等級. 108 短杖#b", "龍骨長杖#k - 等級. 110 長杖#b");
                for (var i = 0; i < weapon.length; i++) {
                    selStr += "\r\n#L" + i + "# " + weapon[i] + "#l";
                }
                cm.sendSimple(selStr);
            } else if (selectedType == 4) { //thief weapon
                var selStr = "很好，那麼你想做哪一個？？#b";
                var weapon = new Array("阿拉伯彎刀#k - 等級. 110 力量短刀#b", "烈風短刃#k - 等級. 110 幸運短刀#b", "綠色龍牙拳刃#k - 等級. 110 拳套#b");
                for (var i = 0; i < weapon.length; i++) {
                    selStr += "\r\n#L" + i + "# " + weapon[i] + "#l";
                }
                cm.sendSimple(selStr);
            } else if (selectedType == 5) { //pirate weapon
                var selStr = "很好，那麼你想做哪一個？？#b";
                var weapon = new Array("龍王之爪#k - 等級. 110 指虎#b", "聖龍金槍#k - 等級. 110 槍#b");
                for (var i = 0; i < weapon.length; i++) {
                    selStr += "\r\n#L" + i + "# " + weapon[i] + "#l";
                }
                cm.sendSimple(selStr);
            }
        }
    } else if (status == 2) {
        selectedItem = selection;
        if (selectedType == 1) { //warrior weapon
            var itemSet = new Array(1302059, 1312031, 1322052, 1402036, 1412026, 1422028, 1432038, 1442045);
            var matSet = new Array(new Array(1302056, 4000244, 4000245, 4005000), new Array(1312030, 4000244, 4000245, 4005000), new Array(1322045, 4000244, 4000245, 4005000), new Array(1402035, 4000244, 4000245, 4005000),
                    new Array(1412021, 4000244, 4000245, 4005000), new Array(1422027, 4000244, 4000245, 4005000), new Array(1432030, 4000244, 4000245, 4005000), new Array(1442044, 4000244, 4000245, 4005000));
            var matQtySet = new Array(new Array(1, 20, 25, 8), new Array(1, 20, 25, 8), new Array(1, 20, 25, 8), new Array(1, 20, 25, 8), new Array(1, 20, 25, 8), new Array(1, 20, 25, 8), new Array(1, 20, 25, 8), new Array(1, 20, 25, 8));
            var costSet = new Array(120000, 120000, 120000, 120000, 120000, 120000, 120000, 120000);
            item = itemSet[selectedItem];
            mats = matSet[selectedItem];
            matQty = matQtySet[selectedItem];
            cost = costSet[selectedItem];
        } else if (selectedType == 2) { //bowman weapon
            var itemSet = new Array(1452044, 1462039);
            var matSet = new Array(new Array(1452019, 4000244, 4000245, 4005000, 4005002), new Array(1462015, 4000244, 4000245, 4005000, 4005002));
            var matQtySet = new Array(new Array(1, 20, 25, 3, 5), new Array(1, 20, 25, 5, 3));
            var costSet = new Array(120000, 120000);
            item = itemSet[selectedItem];
            mats = matSet[selectedItem];
            matQty = matQtySet[selectedItem];
            cost = costSet[selectedItem];
        } else if (selectedType == 3) { //magician weapon
            var itemSet = new Array(1372032, 1382036);
            var matSet = new Array(new Array(1372010, 4000244, 4000245, 4005001, 4005003), new Array(1382035, 4000244, 4000245, 4005001, 4005003));
            var matQtySet = new Array(new Array(1, 20, 25, 6, 2), new Array(1, 20, 25, 6, 2));
            var costSet = new Array(120000, 120000);
            item = itemSet[selectedItem];
            mats = matSet[selectedItem];
            matQty = matQtySet[selectedItem];
            cost = costSet[selectedItem];
        } else if (selectedType == 4) { //thief weapon
            var itemSet = new Array(1332049, 1332050, 1472051);
            var matSet = new Array(new Array(1332051, 4000244, 4000245, 4005000, 4005002), new Array(1332052, 4000244, 4000245, 4005002, 4005003), new Array(1472053, 4000244, 4000245, 4005002, 4005003));
            var matQtySet = new Array(new Array(1, 20, 25, 5, 3), new Array(1, 20, 25, 3, 5), new Array(1, 20, 25, 2, 6));
            var costSet = new Array(120000, 120000, 120000);
            item = itemSet[selectedItem];
            mats = matSet[selectedItem];
            matQty = matQtySet[selectedItem];
            cost = costSet[selectedItem];
        } else if (selectedType == 5) { //pirate weapon
            var itemSet = new Array(1482013, 1492013);
            var matSet = new Array(new Array(1482012, 4000244, 4000245, 4005000, 4005002), new Array(1492012, 4000244, 4000245, 4005000, 4005002));
            var matQtySet = new Array(new Array(1, 20, 25, 5, 3), new Array(1, 20, 25, 3, 5));
            var costSet = new Array(120000, 120000);
            item = itemSet[selectedItem];
            mats = matSet[selectedItem];
            matQty = matQtySet[selectedItem];
            cost = costSet[selectedItem];
        }

        var prompt = "你想要做一個 #t" + item + "#? 在這種情況下，為了要做出好品質的裝備。請確保您有空間在您的裝備欄！#b";

        if (stimulator) {
            stimID = getStimID(item);
            prompt += "\r\n#i" + stimID + "# 1 #t" + stimID + "#";
        }

        if (mats instanceof Array) {
            for (var i = 0; i < mats.length; i++) {
                prompt += "\r\n#i" + mats[i] + "# " + matQty[i] + " #t" + mats[i] + "#";
            }
        } else {
            prompt += "\r\n#i" + mats + "# " + matQty + " #t" + mats + "#";
        }

        if (cost > 0)
            prompt += "\r\n#i4031138# " + cost + " 楓幣";

        cm.sendYesNo(prompt);
    } else if (status == 3 && mode == 1) {
        var complete = true;

        if (cm.getMeso() < cost) {
            cm.sendOk("糟糕...你的錢好像不夠哦...")
            cm.dispose();
            return;
        } else {
            if (mats instanceof Array) {

                for (var i = 0; complete && i < mats.length; i++)
                {
                    if (!cm.haveItem(mats[i], matQty[i]))
                    {
                        complete = false;
                    }
                }
            } else {
                if (!cm.haveItem(mats, matQty))
                {
                    complete = false;
                }
            }
        }

        if (stimulator) { //check for stimulator
            if (!cm.haveItem(stimID)) {
                complete = false;
            }
        }

        if (!complete)
            cm.sendOk("由於你沒有足夠的材料，所以我不幫忙做了。");
        else {
            if (mats instanceof Array) {
                for (var i = 0; i < mats.length; i++) {
                    cm.gainItem(mats[i], -matQty[i]);
                }
            } else
                cm.gainItem(mats, -matQty);

            cm.gainMeso(-cost);
            if (stimulator) { //check for stimulator
                cm.gainItem(stimID, -1);
                var deleted = Math.floor(Math.random() * 10);
                if (deleted != 0) {
                    cm.gainItem(item, 1, true)
                    cm.sendOk("完成。善待好你的武器，免得你使龍的憤怒.");
                } else {
                    cm.sendOk("不幸的是，龍的精髓...抵觸你的武器。我很抱歉是我的疏失.....");
                }
            } else { //just give basic item
                cm.gainItem(item, 1);
                cm.sendOk("完成。善待好你的武器，免得你使龍的憤怒.");
            }
        }
        cm.dispose();
    } else if (status == 100) {
        cm.sendRepairWindow();
        cm.dispose();
    }
}

function getStimID(equipID) {
    var cat = Math.floor(equipID / 10000);
    var stimBase = 4130002; //stim for 1h sword

    switch (cat) {
        case 130: //1h sword, do nothing
            break;
        case 131: //1h axe
            stimBase++;
            break;
        case 132: //1h bw
            stimBase += 2;
            break;
        case 140: //2h sword
            stimBase += 3;
            break;
        case 141: //2h axe
            stimBase += 4;
            break;
        case 142: //2h bw
            stimBase += 5;
            break;
        case 143: //spear
            stimBase += 6;
            break;
        case 144: //polearm
            stimBase += 7;
            break;
        case 137: //wand
            stimBase += 8;
            break;
        case 138: //staff
            stimBase += 9;
            break;
        case 145: //bow
            stimBase += 10;
            break;
        case 146: //xbow
            stimBase += 11;
            break;
        case 133: //dagger
            stimBase += 12;
            break;
        case 147: //claw
            stimBase += 13;
            break;
        case 148: //knuckle
            stimBase += 14;
            break;
        case 149: //gun
            stimBase += 15;
            break;
    }

    return stimBase;
}