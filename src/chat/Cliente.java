package chat;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author SILVIO
 */
public class Cliente implements Runnable {

    public static final String SERVER_ADDRESS = "127.0.0.1";
    private EscutaChat escutaChat;
    private Scanner scanner;
    
    public Cliente() throws IOException{
        scanner = new Scanner(System.in);
    }
    
    private void iniciar() throws IOException{
        try {
            this.escutaChat = new EscutaChat(new Socket(SERVER_ADDRESS,Servidor.PORT));
            
            loopDeMensagem();
            
        } catch (IOException ex) {
            System.err.println("Erro ao obter informaçoes do servidor!!!"+ex);
        }
    }
    
    @Override
    public void run() {
        String mensagemDeTexto;
        while(!escutaChat.getSocket().isClosed() && ((mensagemDeTexto = escutaChat.getMensagem())!= null)){
            System.out.println(mensagemDeTexto+"\n");
        }
    }
    
    private void loopDeMensagem() {
        String mensagemDeTexto;
        do{
            System.out.println("Login: ");
            mensagemDeTexto = scanner.nextLine();
        }while(mensagemDeTexto == null);
        
        System.out.println("Servidor diz: "+ escutaChat.enviaMensagemEPegaResposta("Login: "+mensagemDeTexto));
        
        new Thread(this).start();
        
        System.out.println("Digite SAIR a qualquer monento para fechar o chat!!!");
        
        while(!"sair".equalsIgnoreCase(mensagemDeTexto)){
            System.out.println("Eu: ");
            mensagemDeTexto = this.scanner.nextLine();//Escreve a mensagem
            if(mensagemDeTexto != null)
                escutaChat.enviaMensagem(mensagemDeTexto);//Manda a mensagem pro servidor
        }
        escutaChat.close();
    }
    
    public static void main(String[] args) throws IOException {
       Cliente cliente = new Cliente();
       cliente.iniciar();
    }

}
