package com.lcyanxi.io.bio;

import java.io.IOException;
import java.net.Socket;

/**
 * @author lichang
 * @date 2021/1/7
 */
public class QQClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 8089);
        int index = 0;
        while (true){
            Thread.sleep(1000);
            String message =  "test request " + index ;
            System.out.println("send message : " + message);
            socket.getOutputStream().write(message.getBytes());
            index ++;
        }
    }
}
