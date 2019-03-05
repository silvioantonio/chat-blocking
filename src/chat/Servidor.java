package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author SILVIO
 */
public class Servidor {
    
    private final int PORT = 8000;
    private final String IP_SERVIDOR_LOCAL = "127.0.0.1";
    private List<PrintWriter> escritores = new ArrayList<>();
    private Scanner leitor;
    private String pessoa;
    
    private void iniciar() throws IOException{
        ServerSocket serverSocket = new ServerSocket(PORT);
        while(true){
            Socket socket = serverSocket.accept();
            System.out.println("Nova conexao: "+socket.getRemoteSocketAddress());
            new Thread(new EscutaChat(socket)).start();
            PrintWriter print = new PrintWriter(socket.getOutputStream());
            escritores.add(print);
        }
    }
    public class EscutaChat implements Runnable {
    
        public EscutaChat(Socket socket) throws IOException{
            pessoa = socket.getRemoteSocketAddress().toString();
            leitor = new Scanner(socket.getInputStream());
        }

        private void encaminhaMsg(String texto){
            for(PrintWriter x : escritores){
                x.println(texto);
                x.flush();
            }
        }

        @Override
        public void run() {
            String mensagem;
            while(true){
                mensagem = leitor.nextLine();
                if(mensagem != null){
                    if(!mensagem.equalsIgnoreCase("sair")){
                        System.out.println(mensagem);
                        encaminhaMsg(mensagem);
                    } else {
                        System.out.println("Cliente "+pessoa+" desconectou!");
                        break;
                    }
                } else {
                    System.out.println("MENSAGENS NAO PODEM SER NULAS");
                }
            }
        }

    }
    
    public int getPORT(){
        return this.PORT;
    }
    
    public String getIP_SERVIDOR_LOCAL(){
        return this.IP_SERVIDOR_LOCAL;
    }
    
    public static void main(String[] args) throws IOException {
        Servidor servidor = new Servidor();
        servidor.iniciar();
    }
    
}
