var status = 0;
var itemList = 
Array(     
			Array(2022216,900,1,1), //头盔命中率卷轴60%
			Array(2022217,900,1,1), //脸部装饰回避率卷轴10%
			Array(2022218,900,1,1), //脸部装饰回避率卷轴60%
			Array(2022219,900,1,1), //眼部装饰命中率卷轴10%
			Array(2022220,900,1,1), //眼部装饰命中率卷轴60%
			Array(2022221,900,1,1), //眼部装饰智力卷轴10%
			Array(2022222,900,1,1), //枪
			Array(2022223,900,1,1) //1克拉鸳鸯戒指.
);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendOk("只要你有#v5310000#，我就可以帮你预测并送给你非常给力的1小时增益BUFF哦~怎么样要试试吗？");
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
        if (cm.haveItem(5310000, 1)) {
            cm.sendYesNo("只要你有#v5310000#，我就可以帮你预测并送给你非常给力的1小时增益BUFF哦~怎么样要试试吗？");
        } else {
            cm.sendOk("只要你有#v5310000#，我就可以帮你预测并送给你非常给力的1小时增益BUFF哦~怎么样要试试吗？#k吗?");
            cm.safeDispose();
        }
    } else if (status == 1) {
        var chance = Math.floor(Math.random() * 900);
        var finalitem = Array();
        for (var i = 0; i < itemList.length; i++) {
            if (itemList[i][1] >= chance) {
                finalitem.push(itemList[i]);
            }
        }
        if (finalitem.length != 0) {
            var item;
            var random = new java.util.Random();
            var finalchance = random.nextInt(finalitem.length);
            var itemId = finalitem[finalchance][0];
            var quantity = finalitem[finalchance][2];
            var notice = finalitem[finalchance][3];
            item = cm.gainGachaponItem(itemId, quantity, "薇薇安占卜", notice);
            if (item != -1) {
                cm.gainItem(5310000, -1);
                cm.sendOk("你获得了 #b#t" + item + "##k " + quantity + "个。");
            } else {
                cm.sendOk("你确实有#b#t5310000##k吗？如果是，请你确认在背包的装备，消耗，其他窗口中是否有一格以上的空间。");
            }
            cm.safeDispose();
        } else {
            cm.sendOk("今天的运气可真差，什么都没有拿到。");
            cm.gainItem(5310000, -1);
            cm.safeDispose();
        }
    }
}