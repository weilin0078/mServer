/*
@	Name: GMS-like Gachapon
	Ellinia
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	if (cm.haveItem(5220000, 1)) {
	    cm.sendYesNo("你持有#b转蛋券#k你要试试手气吗?");
	} else {
	    cm.sendOk("你没有转蛋券，请收集完成再来转蛋哦.");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	var item;
	if (Math.floor(Math.random() * 900) == 0) {
	    var rareList = new Array(1402013, 1382047, 1372037, 1382045, 1432018, 1372035, 1382048, 2040924, 2040927, 2040931);

	    item = cm.gainGachaponItem(rareList[Math.floor(Math.random() * rareList.length)], 1);
	} else {
	    var itemList = new Array(1402013, 1382047, 1372037, 1382045, 1432018, 1372035, 1382048, 2040924, 2040927, 2040931, 2040914, 2040919, 2000005, 1050124, 2001002, 2022136, 1022029, 1112500, 2022136, 2022136, 2022179, 2022245, 2100008, 2290049, 2290037, 2290043, 2290055, 2290079, 2290085, 2290098, 2002012, 2002099, 2022139, 2022189, 2022190, 2022255, 1302029, 1332005, 1372010, 1372039, 1372040, 1372041, 1372042, 1402009, 1402010, 1002416, 1002397, 1002435, 1022050, 1012056, 1032013, 1022048, 2001000, 2010000, 2050004, 2100009, 2383006, 2040010, 2040029, 2022565, 5120000, 5120001, 5120002, 5120003, 5120004, 5120005, 5120006, 5120007, 5120008, 5120009, 5151006, 5151007, 5160001, 5160002, 5160003, 5160004, 5160005, 5160006, 5160007, 5160008, 5160009, 5160010, 5160011, 5160012, 5160013, 4000017, 2010002, 2012000, 2012001, 2012002, 2012003, 2012004, 2012005, 2012006, 2022016, 9000030, 5152000, 5152001, 5130000, 5121003, 2022031, 2022032, 2022033, 2040406, 2040425, 2040712, 2040811, 2040814, 2043101, 2043301, 2043804, 2044001, 2044204, 4280000, 4280001, 5151008, 4031019, 4021000, 4021001, 4021002, 4021003, 4021004, 4021005, 4021006, 4021007, 4021008, 4021009, 4021010, 4001104, 4001083, 4001084, 4001085, 4031456, 4000116, 1000027, 1002019, 1002186, 1002309, 1002413, 1002414, 1001023, 1002012);
	    item = cm.gainGachaponItem(itemList[Math.floor(Math.random() * itemList.length)], 1);
	}
	if (item != -1) {
	    cm.gainItem(5220000, -1);
	    cm.sendOk("你得到了 #b#t" + item + "##k.");
	} else {
	    cm.sendOk("请确认背包是否已经满了以及是否有转蛋卷唷。");
	}
	cm.safeDispose();
    }
}