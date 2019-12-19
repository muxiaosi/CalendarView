package com.mxs.calendarview.binding;


import com.mxs.calendarview.R;

/**
 * @author lijie on 2019/4/16
 */
public class ResourceId {
    private int backgroundResourceId;

    public int getUnboxed() {
        return backgroundResourceId;
    }

    private ResourceId(int id) {
        backgroundResourceId = id;
    }

    public static ResourceId wrap(String type) {
        int resourceId;
        switch (type) {
            case "white":
                resourceId = R.color.white;
                break;
            default:
                resourceId = R.color.white;
                break;
        }
        return new ResourceId(resourceId);
    }

    public static ResourceId wrap(int resourceId) {
        return new ResourceId(resourceId);
    }

    /**
     * 设置文字的大小
     *
     * @param str       文本
     * @param maxLength 设置长度
     * @return
     */
    public static boolean textSize(String str, int maxLength) {
        if (str == null) {
            return false;
        }
        return str.length() > maxLength;
    }
}
