import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(1234);
			
			System.out.println("Esperando conexao...");
			Socket conexaoCliente = server.accept();
			System.out.println("CHEGOU CONEXAO !");
			
			InputStream is = conexaoCliente.getInputStream();
			OutputStream os = conexaoCliente.getOutputStream();
			
			DataOutputStream dos = new DataOutputStream(os); 
			
			String resp = "HTTP/1.1 404 Not Found\nContent-Type:text/html\n\n" +
					"<html><title> Aula ISI - </title><h3> Aula ISI</h3></html>";
			
			dos.write(resp.getBytes());
			
			
			Thread.sleep(2000);
			int x = 0;
			while((x = is.read())!=-1) {
				System.out.print((char)x);
				
				//os.write(x+1);
			}
			
			dos.close();
			is.close();
			conexaoCliente.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}