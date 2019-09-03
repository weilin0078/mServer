package tools.wztosql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

public class WzStringDumper
{
    public static void main(final String[] args) throws FileNotFoundException, IOException {
        final File stringFile = MapleDataProviderFactory.fileInWZPath("string.wz");
        final MapleDataProvider stringProvider = MapleDataProviderFactory.getDataProvider(stringFile);
        final MapleData cash = stringProvider.getData("Cash.img");
        final MapleData consume = stringProvider.getData("Consume.img");
        final MapleData eqp = stringProvider.getData("Eqp.img").getChildByPath("Eqp");
        final MapleData etc = stringProvider.getData("Etc.img").getChildByPath("Etc");
        final MapleData ins = stringProvider.getData("Ins.img");
        final MapleData pet = stringProvider.getData("Pet.img");
        final MapleData map = stringProvider.getData("Map.img");
        final MapleData mob = stringProvider.getData("Mob.img");
        final MapleData skill = stringProvider.getData("Skill.img");
        final MapleData npc = stringProvider.getData("Npc.img");
        final String output = args[0];
        final File outputDir = new File(output);
        final File cashTxt = new File(output + "\\Cash.txt");
        final File useTxt = new File(output + "\\Use.txt");
        final File eqpDir = new File(output + "\\Equip");
        final File etcTxt = new File(output + "\\Etc.txt");
        final File insTxt = new File(output + "\\Setup.txt");
        final File petTxt = new File(output + "\\Pet.txt");
        final File mapTxt = new File(output + "\\Map.txt");
        final File mobTxt = new File(output + "\\Mob.txt");
        final File skillTxt = new File(output + "\\Skill.txt");
        final File npcTxt = new File(output + "\\NPC.txt");
        outputDir.mkdir();
        cashTxt.createNewFile();
        useTxt.createNewFile();
        eqpDir.mkdir();
        etcTxt.createNewFile();
        insTxt.createNewFile();
        petTxt.createNewFile();
        mapTxt.createNewFile();
        mobTxt.createNewFile();
        skillTxt.createNewFile();
        npcTxt.createNewFile();
        System.out.println("\u63d0\u53d6 Cash.img \u6578\u64da...");
        PrintWriter writer = new PrintWriter(new FileOutputStream(cashTxt));
        for (final MapleData child : cash.getChildren()) {
            final MapleData nameData = child.getChildByPath("name");
            final MapleData descData = child.getChildByPath("desc");
            String name = "";
            String desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + name + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Cash.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Consume.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(useTxt));
        for (final MapleData child : consume.getChildren()) {
            final MapleData nameData = child.getChildByPath("name");
            final MapleData descData = child.getChildByPath("desc");
            String name = "";
            String desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + name + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Consume.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Eqp.img \u6578\u64da...");
        for (final MapleData child : eqp.getChildren()) {
            System.out.println("\u63d0\u53d6 " + child.getName() + " \u6578\u64da...");
            final File eqpFile = new File(output + "\\Equip\\" + child.getName() + ".txt");
            eqpFile.createNewFile();
            final PrintWriter eqpWriter = new PrintWriter(new FileOutputStream(eqpFile));
            for (final MapleData child2 : child.getChildren()) {
                final MapleData nameData2 = child2.getChildByPath("name");
                final MapleData descData2 = child2.getChildByPath("desc");
                String name2 = "";
                String desc2 = "(\u7121\u63cf\u8ff0)";
                if (nameData2 != null) {
                    name2 = (String)nameData2.getData();
                }
                if (descData2 != null) {
                    desc2 = (String)descData2.getData();
                }
                eqpWriter.println(child2.getName() + " - " + name2 + " - " + desc2);
            }
            eqpWriter.flush();
            eqpWriter.close();
            System.out.println(child.getName() + " \u63d0\u53d6\u5b8c\u6210.");
        }
        System.out.println("Eqp.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Etc.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(etcTxt));
        for (final MapleData child : etc.getChildren()) {
            final MapleData nameData = child.getChildByPath("name");
            final MapleData descData = child.getChildByPath("desc");
            String name = "";
            String desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + name + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Etc.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Ins.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(insTxt));
        for (final MapleData child : ins.getChildren()) {
            final MapleData nameData = child.getChildByPath("name");
            final MapleData descData = child.getChildByPath("desc");
            String name = "";
            String desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + name + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Ins.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Pet.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(petTxt));
        for (final MapleData child : pet.getChildren()) {
            final MapleData nameData = child.getChildByPath("name");
            final MapleData descData = child.getChildByPath("desc");
            String name = "";
            String desc = "(\u7121\u63cf\u8ff0)";
            if (nameData != null) {
                name = (String)nameData.getData();
            }
            if (descData != null) {
                desc = (String)descData.getData();
            }
            writer.println(child.getName() + " - " + name + " - " + desc);
        }
        writer.flush();
        writer.close();
        System.out.println("Pet.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Map.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(mapTxt));
        for (final MapleData child : map.getChildren()) {
            writer.println(child.getName());
            writer.println();
            for (final MapleData child3 : child.getChildren()) {
                final MapleData streetData = child3.getChildByPath("streetName");
                final MapleData mapData = child3.getChildByPath("mapName");
                String streetName = "(\u7121\u6578\u64da\u540d)";
                String mapName = "(\u65e0\u5730\u56fe\u540d)";
                if (streetData != null) {
                    streetName = (String)streetData.getData();
                }
                if (mapData != null) {
                    mapName = (String)mapData.getData();
                }
                writer.println(child3.getName() + " - " + streetName + " - " + mapName);
            }
            writer.println();
        }
        writer.flush();
        writer.close();
        System.out.println("Map.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Mob.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(mobTxt));
        for (final MapleData child : mob.getChildren()) {
            final MapleData nameData = child.getChildByPath("name");
            String name3 = "";
            if (nameData != null) {
                name3 = (String)nameData.getData();
            }
            writer.println(child.getName() + " - " + name3);
        }
        writer.flush();
        writer.close();
        System.out.println("Mob.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Skill.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(skillTxt));
        for (final MapleData child : skill.getChildren()) {
            final MapleData nameData = child.getChildByPath("name");
            final MapleData descData = child.getChildByPath("desc");
            final MapleData bookData = child.getChildByPath("bookName");
            String name4 = "";
            String desc3 = "";
            if (nameData != null) {
                name4 = (String)nameData.getData();
            }
            if (descData != null) {
                desc3 = (String)descData.getData();
            }
            if (bookData == null) {
                writer.println(child.getName() + " - " + name4 + " - " + desc3);
            }
        }
        writer.flush();
        writer.close();
        System.out.println("Skill.img \u63d0\u53d6\u5b8c\u6210.");
        System.out.println("\u63d0\u53d6 Npc.img \u6578\u64da...");
        writer = new PrintWriter(new FileOutputStream(npcTxt));
        for (final MapleData child : npc.getChildren()) {
            final MapleData nameData = child.getChildByPath("name");
            String name3 = "";
            if (nameData != null) {
                name3 = (String)nameData.getData();
            }
            writer.println(child.getName() + " - " + name3);
        }
        writer.flush();
        writer.close();
        System.out.println("Npc.img \u63d0\u53d6\u5b8c\u6210.");
    }
}
