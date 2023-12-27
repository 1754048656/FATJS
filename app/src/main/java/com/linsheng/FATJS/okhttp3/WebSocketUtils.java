package com.linsheng.FATJS.okhttp3;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketUtils {
    private static boolean isClosed = true;
    private static WebSocket webSocket;
    private static final OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    public abstract static class JsWebSocketListener extends WebSocketListener {
        // WebSocket连接成功时的处理
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response){
            isClosed = false;
            startHeartbeat();
        };

        // 接收到WebSocket消息时的处理
        @Override
        public abstract void onMessage(@NonNull WebSocket webSocket, @NonNull String text);

        // WebSocket关闭时的处理
        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            isClosed = true;
        };

        // WebSocket连接失败时的处理
        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
            isClosed = true;
        }
    }

    public static WebSocket build(String websocket_url, JsWebSocketListener jsWebSocketListener) {
        Request request = new Request.Builder()
                .url(websocket_url)
                .build();

        webSocket = client.newWebSocket(request, jsWebSocketListener);
        return webSocket;
    }

    public static void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public static void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Goodbye, WebSocket!");
        }
    }

    public static boolean isAlive() {
        return webSocket != null && !isClosed;
    }

    private static void startHeartbeat() {
        // 启动心跳定时器，每隔30秒发送一条心跳消息
        Timer heartbeatTimer = new Timer();
        heartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isClosed) {
                    // 发送心跳消息
                    sendMessage("1");
                }
            }
        }, 30000, 30000);
    }
}
