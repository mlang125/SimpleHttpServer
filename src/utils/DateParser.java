package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateParser {
    private static final String RFC2616 = "EEE, dd MMM yyyy HH:mm:ss zzz";	// Sun, 06 Nov 1994 08:49:37 GMT
    private static final String RFC850 = "EEE, dd-MMM-yy HH:mm:ss zzz"; 	// Sunday, 06-Nov-94 08:49:37 GMT
    private static final String ANSI = "EEE MMM d HH:mm:ss yyyy"; 	// Sun Nov 6 08:49:37 1994

    private static final ThreadLocal<List<SimpleDateFormat>> THREAD_LOCAL_SIMPLE_DATE_FORMATS = new ThreadLocal<List<SimpleDateFormat>>() {
        @Override
        protected List<SimpleDateFormat> initialValue() {
            List<SimpleDateFormat> simpleDateFormats = new ArrayList<SimpleDateFormat>();
            simpleDateFormats.add(new SimpleDateFormat(RFC2616));
            simpleDateFormats.add(new SimpleDateFormat(RFC850));
            simpleDateFormats.add(new SimpleDateFormat(ANSI));
            return simpleDateFormats;
        }
    };

    /**
     * Parses a String representing a date that is formatted for HTTP/1.1, and
     * returns its value as a long.
     *
     * @param dateString a String representing a date that is formatted for
     *                   HTTP/1.1
     * @return the long value of the date, or -1 if no matching was found.
     *
     *
     */
    public static long parseDateHeader(String dateString) {
        for (SimpleDateFormat sdf : THREAD_LOCAL_SIMPLE_DATE_FORMATS.get()) {
            try {
                return sdf.parse(dateString).getTime();
            } catch (ParseException e) {}
        }
        return -1;
    }


    /**
     * Returns a String representation of the current time in RFC 1123 format.
     *
     * @return the date format String.
     */
    public static String getDateHeader() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }
}
