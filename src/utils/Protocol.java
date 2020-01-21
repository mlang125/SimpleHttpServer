package utils;

public enum Protocol {
    HTTP_0_9 (0, 9),
    HTTP_1_0 (1, 0),
    HTTP_1_1 (1, 1),
    HTTP_2_0 (2, 0);


    private int major;
    private int minor;

    Protocol(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static Protocol valueOf(int major, int minor) {
        if (0 == major && 9 == minor) {
            return Protocol.HTTP_0_9;
        } else if (1 == major && 0 == minor) {
            return Protocol.HTTP_1_0;
        } else if (1 == major && 1 == minor) {
            return Protocol.HTTP_1_1;
        } else if (2 == major && 0 == minor) {
            return Protocol.HTTP_2_0;
        }
        throw new IllegalArgumentException("No Protocol Version " + major + "." + minor);
    }

    /* Returns a protocol from a string in the format "HTTP/*.*" */
    public static Protocol fromHeader(String httpVersion) {
        if (null == httpVersion || !httpVersion.startsWith("HTTP/")) {
            throw new IllegalArgumentException("Incorrect format for httpVersion");
        }
        int startIndex = httpVersion.indexOf("/") + 1;
        String version = httpVersion.substring(startIndex, httpVersion.length());

        boolean hasMinor = version.length() == 3;
        int major = Integer.parseInt(version.charAt(0) + "");
        int minor = hasMinor ? Integer.parseInt(version.charAt(2) + "") : 0;

        return Protocol.valueOf(major, minor);
    }

    public boolean equals(Protocol other) {
        return this.major == other.major && this.minor == other.minor;
    }
}
