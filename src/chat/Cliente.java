package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author SILVIO
 */
public class Cliente {

    private Socket socket;
    private Scanner scanner, sc;
    private PrintWriter escritor;
    
    public Cliente() throws IOException{
        scanner = new Scanner(System.in);
    }
    
    private void configurarRede() throws IOException{
        Servidor servidor = new Servidor();
        try {
            this.socket = new Socket(servidor.getIP_SERVIDOR_LOCAL(),servidor.getPORT());
            this.escritor = new PrintWriter(this.socket.getOutputStream(),true);
            this.sc = new Scanner(this.socket.getInputStream());//
            new Thread(new EscutarServidor()).start();
            
            loopDeMensagem();
            
        } catch (IOException ex) {
            System.out.println("Erro ao obter informaçoes do servidor!!!"+ex);
        }
    }
    
    private class EscutarServidor implements Runnable{

        @Override
        public void run() {
            String texto;
            while((texto=sc.nextLine())!=null){
                System.out.println("Texto recebido: "+texto+"\n");
            }
        }
    
    }
    
    private void loopDeMensagem() {
        String mensagem = "";
        while(!mensagem.equalsIgnoreCase("sair")){
            System.out.println("Eu: ");
            mensagem = this.scanner.nextLine();//Escreve a mensagem
            this.escritor.println(mensagem);//Manda a mensagem pro servidor
        }
    }
    
    public static void main(String[] args) throws IOException {
       Cliente cliente = new Cliente();
       cliente.configurarRede();
    }

}
