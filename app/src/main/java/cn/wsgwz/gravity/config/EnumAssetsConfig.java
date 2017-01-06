package cn.wsgwz.gravity.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremy Wang on 2016/12/20.
 */

public enum EnumAssetsConfig {
    ChongQing_YiDong_1("/config/ChongQing_YiDong_1.xml","重庆移动1号线"),ChongQing_YiDong_1_S("/config/ChongQing_YiDong_1_S.xml","重庆移动1号线视频"),
    ChongQing_YiDong_2("/config/ChongQing_YiDong_2.xml","重庆移动2号线"),ChongQing_YiDong_3("/config/ChongQing_YiDong_3.xml","重庆移动3号线"),
    ChongQing_LianTong_1("/config/ChongQing_LianTong_1.xml","重庆联通1号线"),
    SiChuan_YiDong_1("/config/SiChuan_YiDong_1.xml","四川移动1号线");
    private String key;
    private String values;

    EnumAssetsConfig(String key, String values) {
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public String getValues() {
        return values;
    }
}
