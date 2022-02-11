package app.nekogram.tcp2ws;

import org.tcp2ws.ProxyHandler;
import org.tcp2ws.tcp2wsServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Tcp2Ws extends tcp2wsServer {
    protected String address;

    public synchronized void start(int listenPort, String address) {
        this.stopping = false;
        this.port = listenPort;
        this.address = address;
        new Thread(new ServerProcess()).start();
    }

    private class ServerProcess implements Runnable {
        private ServerProcess() {
        }

        public void run() {
            try {
                (new Timer()).schedule(new TimerTask() {
                    public void run() {
                        if (tcp2wsServer.ifDoH) {
                            tcp2wsServer.DoHCache = new HashMap<>();
                        }

                    }
                }, 0L, 300000L);
                this.handleClients(Tcp2Ws.this.port);
            } catch (IOException var2) {
                Thread.currentThread().interrupt();
            }

        }

        protected void handleClients(int port) throws IOException {
            ServerSocket listenSocket = new ServerSocket(port, 50, InetAddress.getByName(address));
            listenSocket.setSoTimeout(200);
            Tcp2Ws.this.port = listenSocket.getLocalPort();

            while (true) {
                synchronized (Tcp2Ws.this) {
                    if (Tcp2Ws.this.stopping) {
                        break;
                    }
                }

                this.handleNextClient(listenSocket);
            }

            try {
                listenSocket.close();
            } catch (IOException ignored) {
            }

        }

        private void handleNextClient(ServerSocket listenSocket) {
            try {
                Socket clientSocket = listenSocket.accept();
                clientSocket.setSoTimeout(200);
                (new Thread(new ProxyHandler(clientSocket))).start();
            } catch (Exception ignored) {
            }

        }
    }
}
