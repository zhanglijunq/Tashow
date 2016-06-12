package com.showjoy.tashow.person;

/**
 * Created by mac on 16/5/18.
 */
public class SettingEvent {

    private String tag = SettingEvent.class.getSimpleName();

    private boolean isChange = false;

    public SettingEvent(boolean b) {
        this.isChange = b;
    }

    public boolean isChange() {
        return isChange;
    }

    public String getTag() {
        return tag;
    }
}
