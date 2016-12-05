package com.wire.bots.propeller;

/**
 * Created with IntelliJ IDEA.
 * User: dejankovacevic
 * Date: 22/10/16
 * Time: 14:56
 */
public class HelloConfig extends com.wire.wbotz.Configuration {
    public String name;
    public int accent;
    public String[] profiles;

    public String getName() {
        return name;
    }

    public int getAccent() {
        return accent;
    }

    public String[] getProfiles() {
        return profiles;
    }
}
