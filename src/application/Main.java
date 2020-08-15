package application;

import javafx.application.Application;
import javafx.stage.Stage;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    //쓰레드 수 제한
    public static ExecutorService threadpool;
    public static Vector<Client> clients = new Vector<Client>();

    ServerSocket serverSocket;

    //서버를 구동시켜 클라이언트의 연결 기다림
    public void startServer(String IP, int port){
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(IP, port));
        } catch (Exception e){
            e.printStackTrace();
            if(!serverSocket.isClosed()) {
                stopServer();
            }
            return;
        }
        //클라이언트가 접속할 때까지 기다린다.
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Socket socket = serverSocket.accept();
                        clients.add(new Client(socket));
                        System.out.println("클라이언트 접속"
                            +socket.getRemoteSocketAddress()
                            +": " + Thread.currentThread().getName());
                    } catch (Exception e) {
                        if(!serverSocket.isClosed());
                        stopServer();
                    }
                    break;
                }
            }
        };
        threadpool = Executors.newCachedThreadPool();
        threadpool.submit(thread);
    }
    //서버의 작동을 중지시키는 메소드
    public void stopServer() {
    try {
        //현재 작동중인 소켓 닫기
        Iterator<Client> iterator = clients.iterator();
        while(iterator.hasNext()) {
        Client client = iterator.next();
        client.socket.close();
        iterator.remove();
        }
        //서버 소켓 닫고
        if(serverSocket != null && !serverSocket.isClosed()){
            serverSocket.close();
        }
        //쓰레드 풀 종료
        if(threadpool != null & !threadpool.isShutdown()) {
            threadpool.shutdown();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    //UI 생성 및 프로그램 동작
    @Override
    public void start(Stage primaryStage) throws Exception{

    }


    public static void main(String[] args) {
        launch(args);
    }
}
