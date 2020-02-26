package org.xam.gh;

import java.io.FileInputStream;
import java.util.Properties;

public final class GithubToken {
    public static String get() {
        try {
            Properties prop = new Properties();

            final String home = System.getProperty("user.home");
            final String githubProp = home + "/.m2/github.properties";
            prop.load(new FileInputStream(githubProp));
            return prop.get("token").toString();
        } catch (Exception e) {
            return null;
        }
    }
}
