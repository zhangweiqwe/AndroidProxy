package cn.wsgwz.gravity.nativeGuard;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public interface NativeStatusListenner {
    public void onChange(StatusEnum statusEnum,StringBuilder sbMessage);
    enum  StatusEnum{
        START_OK,STOP_OK,START_ERROR,STOP_ERROR
    }
}
