package test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test
{
    public static void main(final String[] args) {
        final Date date = new Date();
        final SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        System.out.println(dateFm.format(date));
    }
}
