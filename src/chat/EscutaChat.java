package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author SILVIO
 */
public class EscutaChat implements Runnable {  
    
    public EscutaChat(Socket socket) throws IOException{
        String pessoa = socket.getRemoteSocketAddress().toString();
        Scanner leitor = new Scanner(socket.getInputStream());
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