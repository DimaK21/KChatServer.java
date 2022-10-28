package ru.kryu.kchat.kchatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    private final Vector<ClientHandler> clients = new Vector<>();
    private AuthService authService;

    Server(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started");
            authService = new AuthService();
            authService.connect();
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected " + socket.getInetAddress() + " " + socket.getPort());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Сервис авторизации не запущен");
            e.printStackTrace();
        } finally {
            try {
                authService.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void broadcastMessage(String message) {
        for (ClientHandler o : clients) {
            o.sendMessage(message);
        }
        System.out.println("Сервер делает рассылку всем " + message);
    }

    public void privateMessage(ClientHandler from, String toNick, String message) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(toNick)) {
                o.sendMessage(from.getNick() + " to " + toNick + ": " + message);
                from.sendMessage(from.getNick() + " to " + toNick + ": " + message);
                break;
            }
        }
    }

    public void clientsListMessage() {
        StringBuilder stringBuilder = new StringBuilder("/clientslist ");
        for (ClientHandler o : clients) {
            stringBuilder.append(o.getNick() + " ");
        }
        String out = stringBuilder.toString();
        for (ClientHandler o : clients) {
            o.sendMessage(out);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        System.out.println("Клиент в рассылке сервера");
        clientsListMessage();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Клиент вышел из рассылки сервера");
        clientsListMessage();
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) return true;
        }
        return false;
    }
}
