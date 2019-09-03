var 聊天 = "#fUI/StatusBar/BtChat/normal/0#";
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
			text += "\t\t\t  #e欢迎来到#r回归冒险岛 #k!#n\r\n"
            text += "#b特别注意：萌新请看群文件萌新必看说明，这个很重要\r\n\r\n"//3
            text += "#r1.本服经验-金币-爆率：5倍#l\r\n\r\n"//3
            text += "#r2.本服不私下出售任何物品礼包#l\r\n\r\n"//3
            text += "#r3.本服充值比例：1:200#l\r\n\r\n"//3
            text += "#r4.目前只开放职业：战士.魔法师.弓箭手.飞侠.海盗.战神\r\n(目前骑士团不开放)#l\r\n\r\n"//3
            text += "#r5.修复所有副本+任务+部分游戏功能#l\r\n\r\n"//3
            text += "#r6.十人活跃成员以上家族 族长可向管理员申请特别优惠 家族成员充值享有 充值再送百分之二十 另外族长 每周可领取价值30元 的点卷大礼包一个 根据家族规模 待遇可以上升调整 更多优惠 请咨询管理员(活跃成员指每天在线两小时以上)#l\r\n\r\n"//3
            text += "#r7.出现假死情况请点击拍卖边上的"+ 聊天 +"按钮即可解除假死#l\r\n\r\n"//3
			text += "#r8.1-10级为1倍经验之后就会变回5倍#l\r\n\r\n"//3
			text += "#r9.8级即可点拍卖领取新手奖励#l\r\n\r\n"//3
			text += "#r10.如果不能穿装备请输入@ea进行解卡,卡地图请输入@自由回到自由市场#l\r\n\r\n"//3
			text += "#r11.本服账户采用捆绑机器码 一旦非法操作查封 将无法解封 请珍惜Gogo账户 远离非法外挂辅助#l\r\n\r\n"//3
			text += "#r12.每周更新游戏副本 和各种好玩的游戏项目#l\r\n\r\n"//3
			text += "#r13.本服即将更新加入PK系统 分为 单挑PK 和组队PK 丰富玩家游戏娱乐性#l\r\n\r\n"//3
            cm.sendOk(text);
		    cm.dispose();
		}
    }
}


