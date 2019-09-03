importPackage(java.lang);
importPackage(Packages.tools);
importPackage(Packages.client);
var jobName;
 var job;
 var text11,text30,text00;
 var choose;
 var initial,virtue;
 var camp1,camp2,camp3;

 var showjob1 = ["战士","魂骑士","战神"];
 var showjob2 = ["法师","炎术士"];
 var showjob3 = ["射手","风灵使"];
 var showjob4 = ["飞侠","夜行者"];
 var showjob5 = ["海盗","奇袭者"];
 var showname;
 var selectjob;
 var campis;

var camp = 1; // 1-3

function start() {
     status = -1;
     action(1, 0, 0);
     campis = Integer.valueOf(cm.getPlayer().getJob() / 1000);
     campis2 = cm.getPlayer().getJob() / 1000;
 }

function action(mode, type, selection) {
     if (mode == -1) {
         cm.dispose();
     } else {
         if (mode == 1)
             status++;
         else
             status--;
         if (status == 0) {
             cm.sendNext("你好, 我是#b转职#k与#b重生#k指导员.");
         } else if (status == 1) {
             text11 = "祝贺你达到了#b"+ cm.getLevel() +"级#k. 那么你想选择的 #r第一职业#k 是?#b\r\n";
             text30 = "祝贺你达到了30级. 你想转职为: #b#k\r\n";
             text00 = "祝贺你达到了" + cm.getLevel() + "级. 你是否想转职为 #r";
             if (cm.getLevel() < 200 && (campis2 == 0.0 || campis2 == 1.0 || campis2 == 2.0)) {
                 if (cm.getLevel() < 8) {
                     cm.sendOk("对不起, 你至少要达到#b8级#k我才能为你服务.");
                     cm.dispose();
                 } else {
					 if(cm.getLevel() < 10){
						 text11 += "#L" + 200 + "#" + showjob2[0] + "#l \t\t"
					 }else{
					   for(var j = 1;j <= 5; j++){
                         for(var i = 0;i < camp; i++){
                             selectjob = i * 1000 + 100 * j;
                             if (j == 1) 
                                 showname = showjob1[i];
                             if (j == 2) 
                                 showname = showjob2[i];
                             if (j == 3) 
                                 showname = showjob3[i];
                             if (j == 4) 
                                 showname = showjob4[i];    
                             if (j == 5) 
                                 showname = showjob5[i];       
                             if (showname == null)
                                 break;          
                             text11 += "#L" + selectjob + "#" + showname + "#l \t\t"
                         }
							text11 +="\r\n";
						}
					 }
                     cm.sendSimple(text11);
                     initial = 11;
                 }
             } else if (cm.getLevel() < 30){
                 cm.sendOk("对不起, 你必须达到#b30级#k才能进行#r第二次转职#k.");
                 cm.dispose();
             } else if (cm.getPlayer().getJob() == 100) {
                 camp1 = 30.110;
                 text30 += "#L110#剑客#l\r\n#L120#准骑士#l\r\n#L130#枪战士#l";
                 cm.sendSimple(text30);
             } else if (cm.getPlayer().getJob() == 200) {
                 camp1 = 30.210;
                 text30 += "#L210#火毒法师#l\r\n#L220#冰雷法师#l\r\n#L230#牧师#l";
                 cm.sendSimple(text30);
             } else if (cm.getPlayer().getJob() == 300) {
                 camp1 = 30.310;
                 text30 += "#L310#猎人#l\r\n#L320#弩手#l";
                 cm.sendSimple(text30);
             } else if (cm.getPlayer().getJob() == 400) {
                 camp1 = 30.410;
                 text30 += "#L410#刺客#l\r\n#L420#侠客#l";
                 cm.sendSimple(text30);
             } else if (cm.getPlayer().getJob() == 500) {
                 camp1 = 30.510;
                 text30 += "#L510#拳手#l\r\n#L520#火枪手#l";
                 cm.sendSimple(text30);
 /*
 } else if (cm.getPlayer().getJob().equals(MapleJob.DAWNWARRIOR1)) { // 无骑士团的屏蔽掉这里
                 camp1 = 31.1110;
                 jobName = "魂骑士 Ⅱ";
                 job = MapleJob.DAWNWARRIOR2;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob().equals(MapleJob.BLAZEWIZARD1)) {
                 camp1 = 31.1210;
                 jobName = "炎术士 Ⅱ";
                 job = MapleJob.BLAZEWIZARD2;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob().equals(MapleJob.WINDARCHER1)) {
                 camp1 = 31.1310;
                 jobName = "风灵使 Ⅱ";
                 job = MapleJob.WINDARCHER2;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob().equals(MapleJob.NIGHTWALKER1)) {
                 camp1 = 31.1410;
                 jobName = "夜行者 Ⅱ";
                 job = MapleJob.NIGHTWALKER2;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob().equals(MapleJob.THUNDERBREAKER1)) {
                 camp1 = 31.1510;
                 jobName = "奇袭者 Ⅱ";
                 job = MapleJob.THUNDERBREAKER2;
                 cm.sendYesNo(text00 + jobName);

} else if (cm.getPlayer().getJob().equals(MapleJob.ARAN2)) { // 无战神的屏蔽掉这里
                 camp3 = 32.2110;
                 jobName = "战神 Ⅱ";
                 job = MapleJob.ARAN3;
                 cm.sendYesNo(text00 + jobName);
 */
             
             } else if (cm.getLevel() < 70) {
                 cm.sendOk("对不起, 你必须达到#b70级#k才能进行#r第三次转职#k.");
                 cm.dispose();
             } else if (cm.getPlayer().getJob() == 110) {
                 camp1 = 70.111;
                 jobName = "勇士";
                 job = 111;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 120) {
                 camp1 = 70.121;
                 jobName = "骑士";
                 job = 121;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 130) {
                 camp1 = 70.131;
                 jobName = "龙骑士";
                 job = 131;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 210) {
                 camp1 = 70.211;
                 jobName = "火毒巫师";
                 job = 211;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 220) {
                 camp1 = 70.221;
                 jobName = "冰雷巫师";
                 job = 221;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 230) {
                 camp1 = 70.231;
                 jobName = "祭司";
                 job = 231;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 310) {
                 camp1 = 70.311;
                 jobName = "猎人";
                 job = 311;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 320) {
                 camp1 = 70.321;
                 jobName = "游侠";
                 job = 321;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 410) {
                 camp1 = 70.411;
                 jobName = "无影人";
                 job = 411;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 420) {
                 camp1 = 70.421;
                 jobName = "独行客";
                 job = 421;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 510) {
                 camp1 = 70.511;
                 jobName = "斗士";
                 job = 511;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 520) {
                 camp1 = 70.521;
                 jobName = "大副";
                 job = 521;
                 cm.sendYesNo(text00 + jobName);
 /*
 } else if (cm.getPlayer().getJob().equals(MapleJob.DAWNWARRIOR2)) { // 无骑士团的屏蔽掉这里
                 camp1 = 71.1111;
                 jobName = "魂骑士 Ⅲ";
                 job = MapleJob.DAWNWARRIOR3;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob().equals(MapleJob.BLAZEWIZARD2)) {
                 camp1 = 71.1211;
                 jobName = "炎术士 Ⅲ";
                 job = MapleJob.BLAZEWIZARD3;
                     cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob().equals(MapleJob.WINDARCHER2)) {
                 camp1 = 71.1311;
                 jobName = "风灵使 Ⅲ";
                 job = MapleJob.WINDARCHER3;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob().equals(MapleJob.NIGHTWALKER2)) {
                 camp1 = 71.1411;
                 jobName = "夜行者 Ⅲ";
                 job = MapleJob.NIGHTWALKER3;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob().equals(MapleJob.THUNDERBREAKER2)) {
                 camp1 = 71.1511;
                 jobName = "奇袭者 Ⅲ";
                 job = MapleJob.THUNDERBREAKER3;
                 cm.sendYesNo(text00 + jobName);
   
 } else if (cm.getPlayer().getJob().equals(MapleJob.ARAN3)) { // 无战神的屏蔽掉这里
                 camp3 = 72.2111;
                 jobName = "战神 Ⅲ";
                 job = MapleJob.ARAN4;
                 cm.sendYesNo(text00 + jobName);
 */
             } else if (cm.getLevel() < 120) {
                 cm.sendOk("对不起, 你必须达到#b120级#k才能进行#r第四次转职#k.");
                 cm.dispose();
             } else if (cm.getPlayer().getJob() == 111) {
                 camp1 = 120.112;
                 jobName = "英雄";
                 job = 112;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 121) {
                 camp1 = 120.122;
                 jobName = "圣骑士";
                 job = 122;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 131) {
                 camp1 = 120.132;
                 jobName = "黑骑士";
                 job = 132;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 211) {
                 camp1 = 120.212;
                 jobName = "火毒魔导士";
                 job = 212;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 221) {
                 camp1 = 120.222;
                 jobName = "冰雷魔导士";
                 job = 222;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 231) {
                 camp1 = 120.232;
                 jobName = "主教";
                 job = 232;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 311) {
                 camp1 = 120.312;
                 jobName = "箭神";
                 job = 312;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 321) {
                 camp1 = 120.322;
                 jobName = "弩神";
                 job = 322;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 411) {
                 camp1 = 120.412;
                 jobName = "隐士";
                 job = 412;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 421) {
                 camp1 = 120.422;
                 jobName = "侠盗";
                 job = 422;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 511) {
                 camp1 = 120.512;
                 jobName = "冲锋队长";
                 job = 512;
                 cm.sendYesNo(text00 + jobName);
             } else if (cm.getPlayer().getJob() == 521) {
                 camp1 = 120.522;
                 jobName = "船长";
                 job = 522;
                 cm.sendYesNo(text00 + jobName);
 /*
 } else if (cm.getPlayer().getJob().equals(MapleJob.ARAN4)) { // 无战神的屏蔽掉这里
                 camp3 = 122.2112;
                 jobName = "战神 Ⅳ";
                 job = MapleJob.ARAN5;
                 cm.sendYesNo(text00 + jobName);
 */
             } else if (cm.getLevel() >= 200 || (cm.getLevel() >= 120 && campis == 1)) {//cm.getJobId() == 1111
                 getCostAp();
				 cm.sendYesNo("啊哈... 伟大的#b#h ##k。你已经通过一个漫长而充满挑战的道路,终于成为了风起云涌的人物。 \r\n如果您能给我#b圣杯#k #v4031454#， 我可以用我的乾坤大挪移心法，助你进行投胎转世！ 您将成为1级的#b新手#k, 并且清空你的#b技能#k，然后给予#r"+ "转身次数 * 100" +"#k个属性点。\r\n#k你当前转生次数为：#r" + cm.getPlayer().getPrizeLog("转身") + "#k次，你是否想#r转生#k呢?");

            //     cm.sendYesNo("啊哈... 伟大的#b#h ##k。你已经通过一个漫长而充满挑战的道路,终于成为了风起云涌的人物。 \r\n如果您能给我#b冒险岛纪念币#k #v4001129#(新建角色时送1个)， 我可以用我的乾坤大挪移心法，助你进行投胎转世！ 您将成为1级的#b新手#k, 并且清空你的#b技能#k，然后扣除#r"+costAp+"#k个属性点。\r\n#k你当前转生次数为：#r" + cm.getChar().getReborns() + "#k次，你是否想#r转生#k呢?");
                 initial = 1;
             //} else if (cm.getLevel() < 200) {
             //    cm.sendOk("对不起, 你已经完成了所有的转职. \r\n\r\n然而在这个世界阴暗的深处, 被#baexr#k封印的魔王正蠢蠢欲动, 它的残忍无人能及,你需要修炼的更加强大才能拯救所有的人. \r\n当你#r200级#k的时候再来找我吧.");
             //    cm.dispose();    
             } else {
                 cm.dispose();
             }
         } else if (status == 2) {
             choose = selection;
             setJob();
             if (initial == 11) {
                 cm.sendYesNo("你想成为 #r" + jobName + "#k 吗?");
             } else if (camp1 > 30 && camp1 < 31)  {
                 cm.sendYesNo("你想成为 #r" + jobName + "#k 吗?");
             } else if (camp2 > 31 && camp2 < 32) {
                 cm.changeJob(job);
                 cm.sendOk("你去吧. 向着最后的胜利冲刺吧 :)");
                 cm.dispose();
             } else if (camp3 > 32 && camp3 < 33) {
                 cm.changeJob(job);
                 cm.sendOk("你去吧. 也许不久的将来还能见到你 :)");
                 cm.dispose();    
             } else if (camp1 > 70 && camp1 < 71) {
                 cm.changeJob(job);
                 cm.sendOk("你去吧. 向着最后的胜利冲刺吧 :)");
                 cm.dispose();
             } else if (camp2 > 71 && camp2 < 72)  {
                 cm.changeJob(job);
                 cm.sendOk("你去吧. 我已经没什么可以教你的了 :)");
                 cm.dispose();
             } else if (camp3 > 72 && camp3 < 73)  {
                 cm.changeJob(job);
                 cm.sendOk("你去吧. 向着最后的胜利冲刺吧 :)");
                 cm.dispose();    
             } else if (camp1 > 120 && camp1 < 121)  {
                 cm.changeJob(job);
                 setSkill();
                 cm.sendOk("你去吧. 我已经没什么可以教你的了 :)");
                 cm.dispose();
             } else if (camp3 > 122 && camp3 < 123)  {
                 cm.changeJob(job);
                 cm.sendOk("你去吧. 我已经没什么可以教你的了 :)");
                 setSkill();
                 cm.dispose();    
             } else if (initial == 1)  {
                 doReborn();
             }
         } else if (status == 3) {
             if (initial == 11) {
                   cm.changeJob(job);
                   cm.sendOk("你去吧. 未来是属于你们的 :)");
                   cm.dispose();
             } else if (camp1 > 30 && camp1 < 31)  {
                 cm.changeJob(job);
                 cm.sendOk("你去吧. 也许不久的将来还能见到你 :)");
                 cm.dispose();
             }
         }
     }
 }


function setJob() {
     if (choose == 100) {
         jobName = "战士";
         job = 100;
         virtue = 11.1;
     } else if (choose == 200) {
         jobName = "法师";
         job = 200;
         virtue = 11.2;
     } else if (choose == 300) {
         jobName = "射手";
         job = 300;
         virtue = 11.3;
     } else if (choose == 400) {
         jobName = "飞侠";
         job = 400;
         virtue = 11.4;
     } else if (choose == 500) {
         jobName = "海盗";
         job = 500;
         virtue = 11.5;
     } else if (choose == 1100) {
         jobName = "魂骑士";
         job = 1100;
         virtue = 11.1;
     } else if (choose == 1200) {
         jobName = "炎术士";
         job = 1200;
         virtue = 11.2;
     } else if (choose == 1300) {
         jobName = "风灵使者";
         job = 1300;
         virtue = 11.3;
     } else if (choose == 1400) {
         jobName = "夜行者";
         job = 1400;
         virtue = 11.4;
     } else if (choose == 1500) {
         jobName = "奇袭者";
         job = 1500;
         virtue = 11.5;
     } else if (choose == 2100) {
         jobName = "战神";
         job = 2100;
         virtue = 11.1;    
     } else if (choose == 110) {
         jobName = "剑客";
         job = 110;
     } else if (choose == 120) {
         jobName = "准骑士";
         job = 120;
     } else if (choose == 130) {
         jobName = "枪战士";
         job = 130;
     } else if (choose == 210) {
         jobName = "火毒法师";
         job = 210;
     } else if (choose == 220) {
         jobName = "冰雷法师";
         job = 220;
     } else if (choose == 230) {
         jobName = "牧师";
         job = 230;
     } else if (choose == 310) {
         jobName = "猎人";
         job = 310;
     } else if (choose == 320) {
         jobName = "弩手";
         job = 320;
     } else if (choose == 410) {
         jobName = "刺客";
         job = 410;
     } else if (choose == 420) {
         jobName = "侠客";
         job = 420;
     } else if (choose == 510) {
         jobName = "拳手";
         job = 510;
     } else if (choose == 520) {
         jobName = "火枪手";
         job = 520;
     }
 }

function setSkill() {
     if (cm.getPlayer().getJob() == 112) {
         cm.teachSkill(1120003,0,10);
         cm.teachSkill(1120004,0,10);
         cm.teachSkill(1120005,0,10);
         cm.teachSkill(1121001,0,10);
         cm.teachSkill(1121002,0,10);
         cm.teachSkill(1121006,0,10);
         cm.teachSkill(1121008,0,10);
         cm.teachSkill(1121010,0,10); // 葵花宝典[无效]
         cm.teachSkill(1121000,0,10); // 冒险岛勇士
         cm.teachSkill(1121011,0,5);  // 勇士的意志
     } else if (cm.getPlayer().getJob() == 122) {
         cm.teachSkill(1220005,0,10);
         cm.teachSkill(1220006,0,10);
         cm.teachSkill(1220010,0,10);
         cm.teachSkill(1221001,0,10);
         cm.teachSkill(1221002,0,10);
         cm.teachSkill(1221003,0,10);
         cm.teachSkill(1221004,0,10);
         cm.teachSkill(1221007,0,10);
         cm.teachSkill(1221009,0,10);
         cm.teachSkill(1221011,0,10);
         cm.teachSkill(1221000,0,10);
         cm.teachSkill(1221012,0,5);
     } else if (cm.getPlayer().getJob() == 132) {
         cm.teachSkill(1320005,0,10);
         cm.teachSkill(1320006,0,10);
         cm.teachSkill(1321007,0,10);
         cm.teachSkill(1320008,0,10);
         cm.teachSkill(1320009,0,10);
         cm.teachSkill(1321001,0,10);
         cm.teachSkill(1321002,0,10);
         cm.teachSkill(1321003,0,10);
         cm.teachSkill(1321000,0,10);
         cm.teachSkill(1321010,0,5);
     } else if (cm.getPlayer().getJob() == 212) {
         cm.teachSkill(2121001,0,10);
         cm.teachSkill(2121002,0,10);
         cm.teachSkill(2121003,0,10);
         cm.teachSkill(2121004,0,10);
         cm.teachSkill(2121005,0,10);
         cm.teachSkill(2121006,0,10);
         cm.teachSkill(2121007,0,10);
         cm.teachSkill(2121000,0,10);
         cm.teachSkill(2121008,0,5);
     } else if (cm.getPlayer().getJob() == 222) {
         cm.teachSkill(2221001,0,10);
         cm.teachSkill(2221002,0,10);
         cm.teachSkill(2221003,0,10);
         cm.teachSkill(2221004,0,10);
         cm.teachSkill(2221005,0,10);
         cm.teachSkill(2221006,0,10);
         cm.teachSkill(2221007,0,10);
         cm.teachSkill(2221000,0,10);
         cm.teachSkill(2221008,0,5);
     } else if (cm.getPlayer().getJob() == 232) {
         cm.teachSkill(2321000,0,10);
         cm.teachSkill(2321001,0,10);
         cm.teachSkill(2321002,0,10);
         cm.teachSkill(2321003,0,10);
         cm.teachSkill(2321004,0,10);
         cm.teachSkill(2321005,0,10);
         cm.teachSkill(2321006,0,10);
         cm.teachSkill(2321007,0,10);
         cm.teachSkill(2321008,0,10);
         cm.teachSkill(2321009,0,5);
     } else if (cm.getPlayer().getJob() == 312) {
         cm.teachSkill(3120005,0,10);
         cm.teachSkill(3121002,0,10);
         cm.teachSkill(3121003,0,10);
         cm.teachSkill(3121004,0,10);
         cm.teachSkill(3121006,0,10);
         cm.teachSkill(3121007,0,10);
         cm.teachSkill(3121008,0,10);
         cm.teachSkill(3121000,0,10);
         cm.teachSkill(3121009,0,5);
     } else if (cm.getPlayer().getJob() == 322) {
         cm.teachSkill(3220004,0,10);
         cm.teachSkill(3221001,0,10);
         cm.teachSkill(3221002,0,10);
         cm.teachSkill(3221003,0,10);
         cm.teachSkill(3221005,0,10);
         cm.teachSkill(3221006,0,10);
         cm.teachSkill(3221007,0,10);
         cm.teachSkill(3221000,0,10);
         cm.teachSkill(3221008,0,5);
     } else if (cm.getPlayer().getJob() == 412) {
         cm.teachSkill(4120002,0,10);
         cm.teachSkill(4121003,0,10);
         cm.teachSkill(4121006,0,10);
         cm.teachSkill(4121007,0,10);
         cm.teachSkill(4121008,0,10);
         cm.teachSkill(4120005,0,10); // 武器用毒液
         cm.teachSkill(4121004,0,10); // 忍者伏击
         cm.teachSkill(4121000,0,10);
         cm.teachSkill(4121009,0,5);
     } else if (cm.getPlayer().getJob() == 422) {
         cm.teachSkill(4220002,0,10);
         cm.teachSkill(4220005,0,10);
         cm.teachSkill(4221001,0,10);
         cm.teachSkill(4221007,0,10);
         cm.teachSkill(4221004,0,10); // 忍者伏击
         cm.teachSkill(4221006,0,10); // 烟幕弹
         cm.teachSkill(4221003,0,10); // 挑衅
         cm.teachSkill(4221000,0,10);
         cm.teachSkill(4221008,0,5);
     } else if (cm.getPlayer().getJob() == 512) {
         cm.teachSkill(5121001,0,10);
         cm.teachSkill(5121002,0,10);
         cm.teachSkill(5121003,0,10);
         cm.teachSkill(5121004,0,10);
         cm.teachSkill(5121005,0,10);
         cm.teachSkill(5121007,0,10);
         cm.teachSkill(5121009,0,10);
         cm.teachSkill(5121010,0,10);
         cm.teachSkill(5121000,0,10);
         cm.teachSkill(5121008,0,5);
     } else if (cm.getPlayer().getJob() == 522) {
         cm.teachSkill(5220001,0,10);
         cm.teachSkill(5220002,0,10);
         cm.teachSkill(5220011,0,10);
         cm.teachSkill(5221003,0,10);
         cm.teachSkill(5221004,0,10);
         cm.teachSkill(5221006,0,10);
         cm.teachSkill(5221007,0,10);
         cm.teachSkill(5221008,0,10);
         cm.teachSkill(5221009,0,10);
         cm.teachSkill(5221000,0,10);
         cm.teachSkill(5221010,0,5);
 /*
 } else if (cm.getPlayer().getJob() == 2112) { // 无战神的屏蔽掉这里
         cm.teachSkill(21120001,0,10);
         cm.teachSkill(21120002,0,10);
         cm.teachSkill(21120004,0,10);
         cm.teachSkill(21120005,0,10);
         cm.teachSkill(21120006,0,10);
         cm.teachSkill(21120007,0,10);
         cm.teachSkill(21120009,0,10);
         cm.teachSkill(21120010,0,10);
         cm.teachSkill(21121000,0,10);
         cm.teachSkill(21121003,0,10);
         //cm.teachSkill(21121008,0,5);
 */
     }
 }

var newAp,newStr,newDex,newInt,newLuk;
 var costAp;
 var maxReborns = 5;

function getCostAp() {
     if (campis == 1) {
         costAp = 1000;
     } else {
         costAp = 600;
     }
 }

function doReborn() {
     var p = cm.getPlayer();
     newStr =  p.getStr();
     newDex =  p.getDex();
     newInt =  p.getInt();
     newLuk =  p.getLuk();
     var totStat = newStr + newDex + newInt + newLuk - 16;
     
     if (!cm.haveItem(4031454)) { 
         cm.sendOk("你没有带来#b勇气之心#k!");
         cm.dispose();
     } else {
		 newStat();
      //   if(p.getRemainingAp() >= costAp){
       //      newAp = p.getRemainingAp() - costAp;
     //        newStat();
      //   } else {
      //       newAp = 0;
      //       costAp = costAp - p.getRemainingAp();
      //       if (totStat >= costAp) {
         //        for (var i = 0; i <= costAp; i++) {
      //               if (newStr > 4) {
        //                 newStr -=1;
       //                  costAp -=1;
        //             }
     //                if (newDex > 4) {
            //             newDex -=1;
        //                 costAp -=1;
           //          }
        //             if (newInt > 4) {
           //              newInt -=1;
            //             costAp -=1;
           //          }
            //         if (newLuk > 4) {
          //               newLuk -=1;
                         costAp -=1;
           //          }
         //        }
           //      newStat();
         //    } else {
            //     getCostAp();
         //        cm.sendOk("属性点不够,无法转生!请确保你的AP点数或属性总和-16后达到#b"+costAp+"#k.");
          //       cm.dispose();
          //   }
         //}
         
     }
 }

function newStat() {
	 cm.clearSkills();
	 var ch = cm.getChar();
	 ch.setLevel(2);
     cm.gainItem(4031454,-1);
     cm.getPlayer().setPrizeLog("转身");
	 cm.changeJob(0);
 
     var statup = new java.util.ArrayList();
     var p = cm.getPlayer();
     cm.getPlayer().getStat().setStr(4);
     cm.getPlayer().getStat().setDex(4);
     cm.getPlayer().getStat().setInt(4);
     cm.getPlayer().getStat().setLuk(4);
     cm.getPlayer().setRemainingAp(cm.getPlayer().getPrizeLog("转身") * 100);
     statup.add(new Pair(MapleStat.STR, Integer.valueOf(4)));
     statup.add(new Pair(MapleStat.DEX, Integer.valueOf(4)));
     statup.add(new Pair(MapleStat.INT, Integer.valueOf(4)));
     statup.add(new Pair(MapleStat.LUK, Integer.valueOf(4)));
     statup.add(new Pair(MapleStat.AVAILABLEAP, Integer.valueOf(cm.getPlayer().getRemainingAp())));
     statup.add(new Pair(MapleStat.EXP, Integer.valueOf(0)));
     statup.add(new Pair(MapleStat.LEVEL, Integer.valueOf(1)));
     statup.add(new Pair(MapleStat.JOB, Integer.valueOf(campis * 1000)));
     p.getClient().getSession().write(MaplePacketCreator.updatePlayerStats(statup,0));
     //cm.unequipEverything();

     cm.sendOk("#b您做得非常好#k, 为你成功#e投胎转世#n高兴吧！");
	 cm.serverNotice("[转世重生]:恭喜"+ cm.getChar().getName() +"，完成第" + cm.getPlayer().getPrizeLog("转身") +"次转身! 又变回一个渣渣了!");
     cm.dispose();
 }
