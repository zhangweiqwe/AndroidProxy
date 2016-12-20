package cn.wsgwz.gravity.helper;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/12/20.
 */

public class DemoGetInstance {
    private static final DemoGetInstance demoGetInstance = new DemoGetInstance();

    private DemoGetInstance() {
    }
    public static final DemoGetInstance getInstance(){
        return demoGetInstance;
    }
}
