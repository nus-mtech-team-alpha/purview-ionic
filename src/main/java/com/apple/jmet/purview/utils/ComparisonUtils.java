package com.apple.jmet.purview.utils;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class ComparisonUtils {

    private ComparisonUtils() { }

    private static class SingletonHelper {
        private static final ComparisonUtils INSTANCE = new ComparisonUtils();
    }

    public static ComparisonUtils getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public int singleValueCompare(String val1, String val2){
        ComparableVersion cv1 = new ComparableVersion(val1);
        ComparableVersion cv2 = new ComparableVersion(val2);
        return cv1.compareTo(cv2);
    }

}
