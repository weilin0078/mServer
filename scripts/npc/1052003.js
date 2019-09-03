
/* Chris
	Victoria Road : Kerning City Repair Shop (103000006)
	
	Refining NPC: 
	* Minerals
	* Jewels
	* Special - Iron Hog's Metal Hoof x 100 into Steel Plate
	* Claws
*/

var status = 0;
var selectedType = -1;
var selectedItem = -1;
var item;
var mats;
var matQty;
var cost;
var qty;
var equip;
var last_use; //last item is a use item

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        cm.dispose();
    if (status == 0) {
        var selStr = "是的，我退休的盜賊。如果你願意付出，我可以給你我的一些服務.#b"
        var options = new Array("製作礦石", "提煉寶石礦", "鋼鐵肥肥的腿兌換中心...", "升級拳套");
        for (var i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i] + "#l";
        }
        cm.sendSimple(selStr);
    } else if (status == 1) {
        selectedType = selection;
        if (selectedType == 0) { //mineral refine
            var selStr = "所以你想要製作什麼礦石??#b";
            var minerals = new Array("青銅", "鋼鐵", "鋰礦石", "朱礦石", "銀", "紫礦石", "黃金");
            for (var i = 0; i < minerals.length; i++) {
                selStr += "\r\n#L" + i + "# " + minerals[i] + "#l";
            }
            equip = false;
            cm.sendSimple(selStr);
        } else if (selectedType == 1) { //jewel refine
            var selStr = "所以你要我提煉哪種寶石??#b";
            var jewels = new Array("石榴石", "紫水晶", "海藍寶石", "祖母綠", "蛋白石", "藍寶石", "黃晶", "鑽石", "黑暗水晶");
            for (var i = 0; i < jewels.length; i++) {
                selStr += "\r\n#L" + i + "# " + jewels[i] + "#l";
            }
            equip = false;
            cm.sendSimple(selStr);
        } else if (selectedType == 2) { //foot refine
            var selStr = "你知道嗎？很多人不知道，在鐵豬的蹄金屬的潛力......我可以使它成為一些特別的東西，如果你要我做的話...";
            equip = false;
            cm.sendYesNo(selStr);
        } else if (selectedType == 3) { //claw refine
            var selStr = "啊你想要升級拳套，告訴我要升級哪一個?\r\n#e#r(這邊有小BUG例如你選錯你要升級的拳套)\r\n(請勿停止對話OR取消因為會斷線這點請特別注意!)\r\n(所以請先選好您要升級的拳套)#k#n#b";
            var claws = new Array("赤紅戰神拳套#k - 盜賊 等級. 60#b", "藍寶戰神拳套#k - 盜賊 等級. 60#b", "黑戰神拳套#k - 盜賊 等級. 60#b");
            for (var i = 0; i < claws.length; i++) {
                selStr += "\r\n#L" + i + "# " + claws[i] + "#l";
            }
            equip = true;
            cm.sendSimple(selStr);
        }
        if (equip)
            status++;
    } else if (status == 2 && mode == 1) {
        selectedItem = selection;
        if (selectedType == 0) { //mineral refine
            var itemSet = new Array(4011000, 4011001, 4011002, 4011003, 4011004, 4011005, 4011006);
            var matSet = new Array(4010000, 4010001, 4010002, 4010003, 4010004, 4010005, 4010006);
            var matQtySet = new Array(10, 10, 10, 10, 10, 10, 10);
            var costSet = new Array(300, 300, 300, 500, 500, 500, 800);
            item = itemSet[selectedItem];
            mats = matSet[selectedItem];
            matQty = matQtySet[selectedItem];
            cost = costSet[selectedItem];
        } else if (selectedType == 1) { //jewel refine
            var itemSet = new Array(4021000, 4021001, 4021002, 4021003, 4021004, 4021005, 4021006, 4021007, 4021008);
            var matSet = new Array(4020000, 4020001, 4020002, 4020003, 4020004, 4020005, 4020006, 4020007, 4020008);
            var matQtySet = new Array(10, 10, 10, 10, 10, 10, 10, 10, 10);
            var costSet = new Array(500, 500, 500, 500, 500, 500, 500, 1000, 3000);
            item = itemSet[selectedItem];
            mats = matSet[selectedItem];
            matQty = matQtySet[selectedItem];
            cost = costSet[selectedItem];
        } else if (selectedType == 2) { //special refine
            var itemSet = new Array(4011001, 1);
            var matSet = new Array(4000039, 1);
            var matQtySet = new Array(100, 1);
            var costSet = new Array(1000, 1)
            item = itemSet[0];
            mats = matSet[0];
            matQty = matQtySet[0];
            cost = costSet[0];
        }
        var prompt = "所以，你要我做一些 #t" + item + "#? 在這種情況下，有多少你要我做多少個??";
        cm.sendGetNumber(prompt, 1, 1, 100);

    } else if (status == 3) {
        if (equip) {
            selectedItem = selection;
            qty = 1;
        } else {
            qty = selection;
        }
        last_use = false;

        if (selectedType == 3) { //claw refine
            var itemSet = new Array(1472023, 1472024, 1472025);
            var matSet = new Array(new Array(1472022, 4011007, 4021000, 2012000), new Array(1472022, 4011007, 4021005, 2012002), new Array(1472022, 4011007, 4021008, 4000046));
            var matQtySet = new Array(new Array(1, 1, 8, 10), new Array(1, 1, 8, 10), new Array(1, 1, 3, 5));
            var costSet = new Array(80000, 80000, 100000)
            item = itemSet[selectedItem];
            mats = matSet[selectedItem];
            matQty = matQtySet[selectedItem];
            cost = costSet[selectedItem];
            if (selectedItem != 2)
                last_use = true;
        }

        var prompt = "你要我做 ";
        if (qty == 1)
            prompt += "1個 #t" + item + "#?";
        else
            prompt += qty + " #t" + item + "#?";
        prompt += " 在這種情況下，我要為了做出好的品質。請確保您是否有這麼多空間可以放!#b";

        if (mats instanceof Array) {
            for (var i = 0; i < mats.length; i++) {
                prompt += "\r\n#i" + mats[i] + "# " + matQty[i] * qty + " #t" + mats[i] + "#";
            }
        } else {
            prompt += "\r\n#i" + mats + "# " + matQty * qty + " #t" + mats + "#";
        }

        if (cost > 0) {
            prompt += "\r\n#i4031138# " + cost * qty + " 楓幣";
        }
        cm.sendYesNo(prompt);
    } else if (status == 4) {
        var complete = true;

        if (cm.getMeso() < cost * qty) {
            complete = false;
            cm.sendOk("我只需要現金謝謝。");
			cm.dispose();
			return;
        } else {
            if (mats instanceof Array) {
			for (var i = 0; i < mats.length; i++) {
				complete = cm.haveItem(mats[i], matQty[i] * qty);
		    if (!complete) {
				break;
		    }
			}
            } else {
                complete = cm.haveItem(mats, matQty * qty);
            }
        }

        if (!complete) {
            cm.sendOk("我不能接受的替代品。如果你沒有什麼我需要的話，我將無法幫你!!");
			cm.dispose();
			return;
	} else if (!cm.canHold()) {
            cm.sendOk("請確認道具欄是否有空間");
			cm.dispose();
			return;
        } else {
            if (mats instanceof Array) {
                for (var i = 0; i < mats.length; i++) {
                    cm.gainItem(mats[i], -matQty[i] * qty);
                }
            } else {
                cm.gainItem(mats, -matQty * qty);
            }
            cm.gainMeso(-cost * qty);
            cm.gainItem(item, qty);
            cm.sendNext("呼......我差點沒想到，將工作的第二個......好吧，反正我希望你喜歡它。");
        }
        cm.dispose();
    }
}
