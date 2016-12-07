package com.wire.bots.hello;

/**
 * Created with IntelliJ IDEA.
 * User: dejankovacevic
 * Date: 22/10/16
 * Time: 14:56
 */
public class HelloConfig extends com.wire.wbotz.Configuration {
    public String name;
    public int accent;
    private String smallProfile;
    private String bigProfile;

    public String getName() {
        return name;
    }

    public int getAccent() {
        return accent;
    }


    public String getSmallProfile() {
        return smallProfile;
    }

    public String getBigProfile() {
        return bigProfile;
    }
}
