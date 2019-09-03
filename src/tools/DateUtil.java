package tools;

import java.util.Date;
import java.util.TimeZone;

public class DateUtil
{
    private static final long FT_UT_OFFSET = 116444520000000000L;
    
    public static boolean isDST() {
        return TimeZone.getDefault().inDaylightTime(new Date());
    }
    
    public static long getFileTimestamp(final long timeStampinMillis) {
        return getFileTimestamp(timeStampinMillis, false);
    }
    
    public static long getFileTimestamp(long timeStampinMillis, final boolean roundToMinutes) {
        if (isDST()) {
            timeStampinMillis -= 3600000L;
        }
        timeStampinMillis += 50400000L;
        long time;
        if (roundToMinutes) {
            time = timeStampinMillis / 1000L / 60L * 600000000L;
        }
        else {
            time = timeStampinMillis * 10000L;
        }
        return time + 116444520000000000L;
    }
}
