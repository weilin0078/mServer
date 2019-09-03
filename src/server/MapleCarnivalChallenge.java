package server;

import java.lang.ref.WeakReference;

import client.MapleCharacter;
import handling.world.MaplePartyCharacter;

public class MapleCarnivalChallenge
{
    WeakReference<MapleCharacter> challenger;
    String challengeinfo;
    
    public MapleCarnivalChallenge(final MapleCharacter challenger) {
        this.challengeinfo = "";
        this.challenger = new WeakReference<MapleCharacter>(challenger);
        this.challengeinfo += "#b";
        for (final MaplePartyCharacter pc : challenger.getParty().getMembers()) {
            final MapleCharacter c = challenger.getMap().getCharacterById(pc.getId());
            if (c != null) {
                this.challengeinfo = this.challengeinfo + c.getName() + " / \u7b49\u7d1a" + c.getLevel() + " / " + getJobNameById(c.getJob()) + "\r\n";
            }
        }
        this.challengeinfo += "#k";
    }
    
    public MapleCharacter getChallenger() {
        return this.challenger.get();
    }
    
    public String getChallengeInfo() {
        return this.challengeinfo;
    }
    
    public static final String getJobNameById(final int job) {
        switch (job) {
            case 0: {
                return "\u521d\u5fc3\u8005";
            }
            case 1000: {
                return "Nobless";
            }
            case 2000: {
                return "Legend";
            }
            case 2001: {
                return "Evan";
            }
            case 3000: {
                return "Citizen";
            }
            case 100: {
                return "\u528d\u58eb";
            }
            case 110: {
                return "\u72c2\u6230\u58eb";
            }
            case 111: {
                return "\u72c2\u6230\u58eb";
            }
            case 112: {
                return "\u82f1\u96c4";
            }
            case 120: {
                return "\u898b\u7fd2\u9a0e\u58eb";
            }
            case 121: {
                return "\u9a0e\u58eb";
            }
            case 122: {
                return "\u8056\u6230\u58eb";
            }
            case 130: {
                return "\u69cd\u9a0e\u5175";
            }
            case 131: {
                return "\u9f8d\u9a0e\u58eb";
            }
            case 132: {
                return "\u9ed1\u9a0e\u58eb";
            }
            case 200: {
                return "\u6cd5\u5e2b";
            }
            case 210: {
                return "\u5deb\u5e2b(\u706b,\u6bd2)";
            }
            case 211: {
                return "\u9b54\u5c0e\u58eb(\u706b,\u6bd2)";
            }
            case 212: {
                return "\u5927\u9b54\u5c0e\u58eb(\u706b,\u6bd2)";
            }
            case 220: {
                return "\u5deb\u5e2b(\u51b0,\u96f7)";
            }
            case 221: {
                return "\u9b54\u5c0e\u58eb(\u51b0,\u96f7)";
            }
            case 222: {
                return "\u5927\u9b54\u5c0e\u58eb(\u51b0,\u96f7)";
            }
            case 230: {
                return "\u50e7\u4fb6";
            }
            case 231: {
                return "\u796d\u53f8";
            }
            case 232: {
                return "\u4e3b\u6559";
            }
            case 300: {
                return "\u5f13\u7bad\u624b";
            }
            case 310: {
                return "\u7375\u4eba";
            }
            case 311: {
                return "\u904a\u4fe0";
            }
            case 312: {
                return "\u7bad\u795e";
            }
            case 320: {
                return "\u5f29\u5f13\u624b";
            }
            case 321: {
                return "\u72d9\u64ca\u624b";
            }
            case 322: {
                return "\u795e\u5c04\u624b";
            }
            case 400: {
                return "\u76dc\u8cca";
            }
            case 410: {
                return "\u523a\u5ba2";
            }
            case 411: {
                return "\u6697\u6bba\u8005";
            }
            case 412: {
                return "\u591c\u4f7f\u8005";
            }
            case 420: {
                return "\u4fe0\u76dc";
            }
            case 421: {
                return "\u795e\u5077";
            }
            case 422: {
                return "\u6697\u5f71\u795e\u5077";
            }
            case 430: {
                return "Blade Recruit";
            }
            case 431: {
                return "Blade Acolyte";
            }
            case 432: {
                return "Blade Specialist";
            }
            case 433: {
                return "Blade Lord";
            }
            case 434: {
                return "Blade Master";
            }
            case 500: {
                return "\u6d77\u76dc";
            }
            case 510: {
                return "\u6253\u624b";
            }
            case 511: {
                return "\u683c\u9b25\u5bb6";
            }
            case 512: {
                return "\u62f3\u9738";
            }
            case 520: {
                return "\u69cd\u624b";
            }
            case 521: {
                return "\u795e\u69cd\u624b";
            }
            case 522: {
                return "\u69cd\u795e";
            }
            case 1100:
            case 1110:
            case 1111:
            case 1112: {
                return "Soul Master";
            }
            case 1200:
            case 1210:
            case 1211:
            case 1212: {
                return "Flame Wizard";
            }
            case 1300:
            case 1310:
            case 1311:
            case 1312: {
                return "Wind Breaker";
            }
            case 1400:
            case 1410:
            case 1411:
            case 1412: {
                return "Night Walker";
            }
            case 1500:
            case 1510:
            case 1511:
            case 1512: {
                return "Striker";
            }
            case 2100:
            case 2110:
            case 2111:
            case 2112: {
                return "Aran";
            }
            case 2200:
            case 2210:
            case 2211:
            case 2212:
            case 2213:
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218: {
                return "Evan";
            }
            case 3200:
            case 3210:
            case 3211:
            case 3212: {
                return "Battle Mage";
            }
            case 3300:
            case 3310:
            case 3311:
            case 3312: {
                return "Wild Hunter";
            }
            case 3500:
            case 3510:
            case 3511:
            case 3512: {
                return "Mechanic";
            }
            default: {
                return "Unknown Job";
            }
        }
    }
    
    public static final String getJobBasicNameById(final int job) {
        switch (job) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 3000: {
                return "\u521d\u5fc3\u8005";
            }
            case 100:
            case 110:
            case 111:
            case 112:
            case 120:
            case 121:
            case 122:
            case 130:
            case 131:
            case 132:
            case 1100:
            case 1110:
            case 1111:
            case 1112:
            case 2100:
            case 2110:
            case 2111:
            case 2112: {
                return "\u528d\u58eb";
            }
            case 200:
            case 210:
            case 211:
            case 212:
            case 220:
            case 221:
            case 222:
            case 230:
            case 231:
            case 232:
            case 1200:
            case 1210:
            case 1211:
            case 1212:
            case 2200:
            case 2210:
            case 2211:
            case 2212:
            case 2213:
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218:
            case 3200:
            case 3210:
            case 3211:
            case 3212: {
                return "\u6cd5\u5e2b";
            }
            case 300:
            case 310:
            case 311:
            case 312:
            case 320:
            case 321:
            case 322:
            case 1300:
            case 1310:
            case 1311:
            case 1312:
            case 3300:
            case 3310:
            case 3311:
            case 3312: {
                return "\u5f13\u7bad\u624b";
            }
            case 400:
            case 410:
            case 411:
            case 412:
            case 420:
            case 421:
            case 422:
            case 430:
            case 431:
            case 432:
            case 433:
            case 434:
            case 1400:
            case 1410:
            case 1411:
            case 1412: {
                return "\u76dc\u8cca";
            }
            case 500:
            case 510:
            case 511:
            case 512:
            case 520:
            case 521:
            case 522:
            case 1500:
            case 1510:
            case 1511:
            case 1512:
            case 3500:
            case 3510:
            case 3511:
            case 3512: {
                return "\u6d77\u76dc";
            }
            default: {
                return "Unknown Job";
            }
        }
    }
}
