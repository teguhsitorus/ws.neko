package app.nekogram.tcp2ws;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.tcp2ws.tcp2wsServer;

import java.net.ServerSocket;

public class Main {
    public Tcp2Ws tcp2wsServer;
    private boolean tcp2wsStarted = false;

    @Parameter(names = {"--help", "-h"}, help = true, description = "Show help")
    private boolean help;
    @Parameter(names = {"--port", "-p"}, description = "Listening port")
    private int socksPort = 6356;
    @Parameter(names = {"--address", "-a"}, description = "Listening address")
    private String socksAddress = "127.0.0.1";
    @Parameter(names = {"--tls", "-t"}, description = "Enable TLS")
    private boolean enableTLS = true;
    @Parameter(names = {"--doh", "-d"}, description = "Enable DoH")
    private boolean useDoH = true;

    public static void main(String... argv) {
        Main main = new Main();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(main)
                .build();
        jCommander.parse(argv);
        if (main.help) {
            jCommander.usage();
            return;
        }
        main.startServer();
    }

    public void startServer() {
        if (tcp2wsStarted) {
            return;
        }
        try {
            if (socksPort == -1) {
                ServerSocket socket = new ServerSocket(0);
                socksPort = socket.getLocalPort();
                socket.close();
            }
            if (!tcp2wsStarted) {
                System.out.print("Starting tcp2ws...\n");
                tcp2wsServer = (Tcp2Ws) new Tcp2Ws()
                        .setTgaMode(false)
                        .setTls(enableTLS)
                        .setIfMTP(true)
                        .setIfDoH(useDoH);
                tcp2wsServer.start(socksPort, socksAddress);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    tcp2wsServer.stop();
                    System.out.print("Exiting...\n");
                }));
                tcp2wsStarted = true;
                System.out.printf("Connect with link https://t.me/proxy?server=%s&port=%d&secret=00000000000000000000000000000000\n", socksAddress, socksPort);
            }
        } catch (Exception e) {
            if (socksPort == 6356) {
                socksPort = -1;
                startServer();
            }
        }
    }
}
