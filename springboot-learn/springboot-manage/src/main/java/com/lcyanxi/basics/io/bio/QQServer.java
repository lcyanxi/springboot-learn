package com.lcyanxi.basics.io.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lichang
 * @date 2021/1/7
 */
public class QQServer {
    static byte[] bytes = new byte[1024];

    public static void main(String[] args) throws IOException, InterruptedException {
        // io
        // ioService();
        // nio
        nioService();
    }

    /**
     * bio
     * @throws IOException
     */
    private static void ioService() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8080));
        // 套接字列表，用于存储
        List<Socket> socketList = new ArrayList<>();

        // 设置非阻塞
//        serverSocket.setConfig();
        while(true) {
            System.out.println("服务器等待连接....");
            // 获取连接
            Socket socket = serverSocket.accept();
            System.out.println("服务器连接....");
            // 如果没人连接
            if(socket == null) {
                System.out.println("没人连接");
                // 遍历循环socketList，套接字list
                printMessage(socketList,socket);
            } else {
                // 如果有人连接，把套接字放入到列表中
                socketList.add(socket);
                // 设置非阻塞
//                serverSocket.setConfig();
                // 遍历循环socketList，套接字list
                printMessage(socketList,socket);
            }
        }
    }

    /**
     * nio
     * @throws IOException
     */
    private static void nioService() throws IOException, InterruptedException {
        // 创建一个通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8089));
        // 定义list用于存储SocketChannel，也就是非阻塞的连接
        List<SocketChannel> socketChannelList = new ArrayList<>();
        // 缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 设置非阻塞
        serverSocketChannel.configureBlocking(false);

        while(true) {
            System.out.println("服务器等待连接....");
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("服务器连接....");
            // 但无人连接的时候
            if(socketChannel == null) {
                // 睡眠一秒
                TimeUnit.SECONDS.sleep(1);
                System.out.println("无人连接");
                printMessageByNio(socketChannelList,byteBuffer);
            } else {
                // 设置成非阻塞
                socketChannel.configureBlocking(false);
                // 将该通道存入到List中
                socketChannelList.add(socketChannel);
                printMessageByNio(socketChannelList,byteBuffer);
            }
        }
    }

    private static void printMessageByNio(List<SocketChannel> socketChannelList,ByteBuffer byteBuffer) throws IOException {
        for(SocketChannel item: socketChannelList) {
            int len = item.read(byteBuffer);
            if(len > 0) {
                // 切换成读模式
                byteBuffer.flip();
                // 打印出结果
                System.out.println("读取到的数据" + new String(byteBuffer.array(), 0, len));
            }
            byteBuffer.clear();
        }
    }
    private static void printMessage(List<Socket> socketList,Socket socket) throws IOException {
        for(Socket item : socketList) {
            int read = socket.getInputStream().read(bytes);
            // 表示有人发送东西
            if(read != 0) {
                // 打印出内容
                System.out.println(new String(bytes));
            }
        }
    }
}
