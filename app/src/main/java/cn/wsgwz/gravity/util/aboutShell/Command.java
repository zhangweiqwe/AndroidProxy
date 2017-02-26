package cn.wsgwz.gravity.util.aboutShell;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Command {
    public static final String CMD_TOKEN = "^@#*end*#@^";

    private static final int MAX_WAIT_TIME = 30; // s

    /**
     * 命名的退出状态:
     * -2, 不能执行, 没有root权限
     * -1, 超时(自定义,非shell的状态)
     * 0, 成功
     * 1-125, 命令不成功地退出. 特殊的退出值的含义有单独的命令定义.
     * 126, 命令找到了, 但文件无法执行
     * 127, 命令不存在
     * >128, 命令因收到信号而死亡
     */
    private int mExitCode;
    private ArrayList<String> mMessageList;
    private String mErrorMsg;

    private final int mId;
    private final String mCommand;

    private boolean mFinished;

    public Command(String command) {
        mId = CommandIdGenerator.getId();
        mCommand = command + "\necho " + CMD_TOKEN + " " + mId + " $?\n"; // ls -l\n echo cmd_id $?
    }

    /**
     * 执行命令, 该操作是耗时操作, 请确保在非UI线程上执行
     */
    public void execute() {
        execute(RootShell.getInstance(), MAX_WAIT_TIME);
    }

    /**
     * 执行命令, 该操作是耗时操作, 请确保在非UI线程上执行
     * @param shell
     * @param timeout 命令超时时间
     */
    public void execute(RootShell shell, int timeout) {
        int rootStatus = shell.enqueueCommands(this);
        if (rootStatus == RootShell.GET_ROOT) {
            synchronized (this) {
                if (!mFinished) {
                    try {
                        wait(timeout * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!mFinished) {
                        ArrayList<String> errorMsgList = new ArrayList<>();
                        errorMsgList.add("Execute command timeout");
                        setResult(-1, errorMsgList);
                    }
                }
            }
        } else {
            ArrayList<String> errorMsgList = new ArrayList<>();
            errorMsgList.add("Can't execute command without ROOT permission");
            setResult(-2, errorMsgList);
        }
    }

    /**
     * 设置命令执行结果
     * @param exitCode
     * @param messageList
     */
    public void setResult(int exitCode, ArrayList<String> messageList) {
        mExitCode = exitCode;
        if (mExitCode == 0) {
            mMessageList = messageList;
        } else {
            if (messageList != null && !messageList.isEmpty()) {
                mErrorMsg = messageList.get(0);
            }
        }
        synchronized (this) {
            mFinished = true;
            notify();
        }
    }

    public int getId() {
        return mId;
    }

    public String getCommand() {
        return mCommand;
    }

    public int getExitCode() {
        return mExitCode;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public ArrayList<String> getMessageList() {
        return mMessageList;
    }

    private static class CommandIdGenerator {

        public static AtomicInteger sId = new AtomicInteger(0);

        public static int getId() {
            return sId.addAndGet(1);
        }
    }
}