package cn.wsgwz.gravity.util.aboutShell;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RootShell {
    public static final int GET_ROOT = 0; // 获取到ROOT权限
    public static final int USER_DENIED = 1001; // 用户拒绝授予ROOT权限
    public static final int GRANT_ROOT_TIMEOUT = 1002; // 获取ROOT权限超时

    private static final int MAX_WAIT_TIME = 30; // s
    private static final int MAX_COMMAND_COUNT = 10;

    private static final String TAG = "RootShell";

    private volatile static RootShell sInstance;
    private static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private int mStatusCode = GET_ROOT;
    private AtomicBoolean mStarted = new AtomicBoolean(false);

    private Process mProcess;
    private BufferedReader mCommandReader;
    private BufferedWriter mCommandWriter;

    private ArrayBlockingQueue<Command> mCommandBlockingQueue;
    private CommandExecutorThread mCommandExecutorThread;

    public static RootShell getInstance() {
        if (sInstance == null) {
            synchronized (RootShell.class) {
                if (sInstance == null) {
                    sInstance = new RootShell();
                }
            }
        }
        return sInstance;
    }

    private RootShell() {
        mCommandBlockingQueue = new ArrayBlockingQueue<>(MAX_COMMAND_COUNT);
        startRoot();
    }

    // 获取root权限
    private boolean startRoot() {
        try {
            mProcess = new ProcessBuilder("su").redirectErrorStream(true).start();
        } catch (IOException e) {
            mProcess = null;
            Log.w(TAG, "Start root process failed!");
        }

        if (mProcess != null) {
            mCommandReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            mCommandWriter = new BufferedWriter(new OutputStreamWriter(mProcess.getOutputStream()));

            // 验证是否有root权限
            RootValidationTask task = RootValidationTask.newTask(mCommandReader, mCommandWriter);
            Future<Integer> future = THREAD_POOL.submit(task);
            try {
                Integer result = future.get(MAX_WAIT_TIME, TimeUnit.SECONDS);
                if (result != null && result == RootValidationTask.RESULT_OK) {
                    Log.d(TAG, "has access ROOT permission");
                    mStarted.getAndSet(true);
                    // 有oot权限则开启读写命令线程
                    startCommandExecutionThreads();
                    return true;
                }
                mStatusCode = USER_DENIED;
                Log.w(TAG, "User denied!");
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                mProcess.destroy();
                if (e instanceof TimeoutException) {
                    Log.w(TAG, "Validate ROOT permission timeout!");
                    mStatusCode = GRANT_ROOT_TIMEOUT;
                } else {
                    mStatusCode = USER_DENIED;
                    Log.w(TAG, "User denied!");
                }
            }
        }
        return false;
    }

    /**
     * 终止root
     */
    public void terminal() {
        if (sInstance != null && mStarted.getAndSet(false)) {
            new Command("exit 0").execute(); // 保证命令队列中的命令都执行完成
            sInstance.mCommandExecutorThread.interrupt();
            try {
                sInstance.mCommandReader.close();
                sInstance.mCommandWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sInstance.mProcess.destroy();
            sInstance = null;
        }
    }

    /**
     * 开启命令执行线程
     */
    private void startCommandExecutionThreads() {
        mCommandExecutorThread = new CommandExecutorThread();
        mCommandExecutorThread.start();
    }

    /**
     * 入队命令
     * @param commands
     */
    public int enqueueCommands(Command... commands) {
        if (mStarted.get()) {
            for (Command command : commands) {
                if (command != null) {
                    try {
                        boolean success = mCommandBlockingQueue.offer(command);
                        Log.d(TAG, "enqueue: " + command.getCommand().replace("\n", " ") + ", " + success);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mStatusCode;
    }

    /**
     * 命令执行线程
     */
    private class CommandExecutorThread extends Thread {

        @Override
        public void run() {
            while (mStarted.get()) {
                try {
                    // 1.取出命令
                    Command command = mCommandBlockingQueue.take();
                    // 2.执行命令
                    Log.d(TAG, "Input:" + command.getCommand().replace("\n", " "));
                    mCommandWriter.write(command.getCommand());
                    mCommandWriter.flush();
                    // 3.获取结果
                    ArrayList<String> messageList = new ArrayList<>();
                    String line;
                    while ((line = mCommandReader.readLine()) != null) {
                        Log.d(TAG, "Output: " + line);
                        int tokenIndex = line.indexOf(Command.CMD_TOKEN);
                        if (tokenIndex >= 0) {
                            line = line.substring(tokenIndex);
                            String fields[] = line.split(" ");
                            if (fields.length == 3 && !TextUtils.isEmpty(fields[1]) && !TextUtils.isEmpty(fields[2])) {
                                int commandId = Integer.parseInt(fields[1]);
                                int exitCode = Integer.parseInt(fields[2]);
                                if (command.getId() == commandId) {
                                    command.setResult(exitCode, messageList);
                                    break;
                                } else {
                                    throw new IllegalArgumentException("Current command id don't match output id");
                                }
                            }
                        } else {
                            messageList.add(line);
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 验证是否有ROOT权限
     */
    private static class RootValidationTask implements Callable<Integer> {
        public static final int RESULT_DENIED = -1;
        public static final int RESULT_OK = 1;

        private BufferedReader mReader;
        private BufferedWriter mWriter;

        public static RootValidationTask newTask(BufferedReader reader, BufferedWriter writer) {
            return new RootValidationTask(reader, writer);
        }

        private RootValidationTask(BufferedReader reader, BufferedWriter writer) {
            if (reader == null) {
                throw new NullPointerException("reader is null.");
            }
            if (writer == null) {
                throw new NullPointerException("writer is null.");
            }
            mReader = reader;
            mWriter = writer;
        }

        @Override
        public Integer call() {
            try {
                mWriter.write("echo root\n");
                mWriter.flush();
                final String emptyLine = "";
                while (true) {
                    String line = mReader.readLine();
                    if (line == null) {
                        throw new EOFException(); // IOException的子类
                    }
                    if (emptyLine.equals(line)) {
                        continue;
                    }
                    if (line.equals("root")) {
                        break;
                    }
                }
            } catch (Exception e) {
                return RESULT_DENIED;
            }
            return RESULT_OK;
        }
    }

}
