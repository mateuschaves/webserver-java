import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HttpMirror {

    static ArrayList<String> users = new ArrayList();

    public static int createUser(String name) {
        if (name.equals("ADM")) {
            return 403;
        }

        HttpMirror.users.add(name);

        return 201;
    }

    public static int deleteUser(String name) {
        int index = HttpMirror.users.indexOf(name);

        if (index == -1) {
            return 400;
        }

        if (HttpMirror.users.get(index).equals("ADM")) {
            return 401;
        }

        if (index != -1) {
            HttpMirror.users.remove(name);
            return 200;
        }

        return 400;
    }

    public static String listUsers() {
        return HttpMirror.parseUserResponse(HttpMirror.users);
    }

    public static String parseUserResponse(ArrayList<String> users) {
        String response = "{\r\n";

        for (String user : users) {
            response += "\"name\": \"" + user + "\"\r\n,";
        }

        response += "}";

        return response;
    }

    public static void initializeServer() {

        HttpMirror.users.add("ADM");

        try {
            int port = 1234;

            ServerSocket ss = new ServerSocket(port);

            for (;;) {
                Socket client = ss.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream());

                String line;
                String list = "";
                int httpCodeResponse = 400;
                while ((line = in.readLine()) != null) {
                    if (line.length() == 0)
                        break;

                    if (line.contains("OPERATION-TYPE")) {

                        String userName = line.substring(line.lastIndexOf(":") + 1);

                        if (line.contains("CREATE")) {
                            httpCodeResponse = HttpMirror.createUser(userName);
                        }

                        if (line.contains("DELETE")) {
                            httpCodeResponse = HttpMirror.deleteUser(userName);
                        }

                        if (line.contains("LIST")) {
                            list = HttpMirror.listUsers();
                            httpCodeResponse = 200;
                        }
                    }
                }

                out.print("HTTP/1.1 " + httpCodeResponse + "\r\n");
                out.print("Content-Type: application/json\r\n");
                out.print("Connection: close\r\n");
                out.print("\r\n");

                if (!list.isEmpty()) {
                    out.print(list);
                }

                out.close();
                in.close();
                client.close();
            }
        }

        catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void main(String args[]) {
        HttpMirror.initializeServer();
    }
}