package handling.login;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

public class LoginInformationProvider
{
    private static final LoginInformationProvider instance;
    protected final List<String> ForbiddenName;
    
    public static LoginInformationProvider getInstance() {
        return LoginInformationProvider.instance;
    }
    
    protected LoginInformationProvider() {
        this.ForbiddenName = new ArrayList<String>();
        System.out.println("\u52a0\u8f7d \u5546\u57ce\u5b8c\u6210 :::");
        final String WZpath = System.getProperty("net.sf.odinms.wzpath");
        final MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz")).getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            this.ForbiddenName.add(MapleDataTool.getString(data));
        }
    }
    
    public final boolean isForbiddenName(final String in) {
        for (final String name : this.ForbiddenName) {
            if (in.contains(name)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        instance = new LoginInformationProvider();
    }
}
