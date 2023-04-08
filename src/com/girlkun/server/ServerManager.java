package com.girlkun.server;

import com.arriety.MaQuaTang.MaQuaTangManager;
import com.girlkun.database.GirlkunDB;

import java.awt.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import com.girlkun.jdbc.daos.HistoryTransactionDAO;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.item.Item;
import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
import com.girlkun.models.player.Player;
import com.girlkun.network.session.ISession;
import com.girlkun.network.example.MessageSendCollect;
import com.girlkun.network.server.GirlkunServer;
import com.girlkun.network.server.IServerClose;
import com.girlkun.network.server.ISessionAcceptHandler;
import com.girlkun.network.session.Session;
import com.girlkun.server.io.MyKeyHandler;
import com.girlkun.server.io.MySession;
import com.girlkun.services.ClanService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NgocRongNamecService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChonAiDay;
import com.girlkun.services.func.TopService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;


import java.net.Socket;
import java.util.*;
import java.util.logging.Level;

public class ServerManager {

    public static String timeStart;

    public static final Map CLIENTS = new HashMap();

    public static String NAME = "Girlkun75";
    public static int PORT = 14445;

    private static ServerManager instance;

    public static ServerSocket listenSocket;
    public static boolean isRunning;

    public void init() {
        Manager.gI();
        try {
            if (Manager.LOCAL) return;
            GirlkunDB.executeUpdate("update account set last_time_login = '2000-01-01', "
                    + "last_time_logout = '2001-01-01'");
        } catch (Exception e) {
        }
        HistoryTransactionDAO.deleteHistory();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) throws Exception {
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
        ServerManager.gI().run();
    }

    public void run() throws Exception {
        isRunning = true;
        activeCommandLine();
        activeGame();
        activeServerSocket();
        Logger.log(Logger.PURPLE_BOLD_BRIGHT, "     ▄█████ ]▄▄▄▄▄▄▃\n▂▄▅███████▅▄▃▂\nI█████████████]\n◥⊙▲⊙▲⊙▲⊙▲⊙▲⊙▲⊙◤");
        MaQuaTangManager.gI().init();
        new Thread(DaiHoiVoThuat.gI(), "Thread DHVT").start();

        ChonAiDay.gI().lastTimeEnd = System.currentTimeMillis() + 300000;
        new Thread(ChonAiDay.gI(), "Thread CAD").start();

        NgocRongNamecService.gI().initNgocRongNamec((byte) 0);

        new Thread(NgocRongNamecService.gI(), "Thread NRNM").start();

        new Thread(TopService.gI(), "Thread TOP").start();
        try {
            Thread.sleep(1000);
            BossManager.gI().loadBoss();
            Manager.MAPS.forEach(com.girlkun.models.map.Map::initBoss);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(BossManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void act() throws Exception {
        GirlkunServer.gI().init().setAcceptHandler(new ISessionAcceptHandler() {
                    @Override
                    public void sessionInit(ISession is) {
                        if (!canConnectWithIp(is.getIP())) {
                            is.disconnect();
                            return;
                        }

                        is = is.setMessageHandler(Controller.getInstance())
                                .setSendCollect(new MessageSendCollect())
                                .setKeyHandler(new MyKeyHandler())
                                .startCollect();
                    }

                    @Override
                    public void sessionDisconnect(ISession session) {
                        Client.gI().kickSession((MySession) session);
                    }
                }).setTypeSessioClone(MySession.class)
                .setDoSomeThingWhenClose(new IServerClose() {
                    @Override
                    public void serverClose() {
                        System.out.println("server close");
                        System.exit(0);
                    }
                })
                .start(PORT);

    }

    private void activeServerSocket() throws Exception {
        this.act();
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void disconnect(MySession session) {
        Object o = CLIENTS.get(session.getIP());
        if (o != null) {
            int n = Integer.parseInt(String.valueOf(o));
            n--;
            if (n < 0) {
                n = 0;
            }
            CLIENTS.put(session.getIP(), n);
        }
    }

    private void activeCommandLine() {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String line = sc.nextLine();
                if (line.equals("nplayer")) {
                    System.out.println("player onlline : " + Client.gI().getPlayers().size());

                } else if (line.equals("rt")) {
                    // restart server
                    // kick all
                    for (int i = 0; i < Client.gI().getPlayers().size(); i++) {
                        Logger.success("Kick success player" + Client.gI().getPlayers().get(i).name);
                        Client.gI().kickSession(Client.gI().getPlayers().get(i).getSession());
                    }
                    // off server
                    ServerManager.gI().close(0);
                }

            }
        }, "Active line").start();
    }

    private void activeGame() {
    }

    public void close(long delay) {
        GirlkunServer.gI().stopConnect();
        isRunning = false;
        try {
            ClanService.gI().close();
        } catch (Exception e) {
            Logger.error("Lỗi save clan!...................................\n");
        }
        Client.gI().close();

        Logger.success("SUCCESSFULLY MAINTENANCE!...................................\n");
        System.exit(0);
    }
}
