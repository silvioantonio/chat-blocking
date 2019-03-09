package chat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author SILVIO
 */
public class EscutaChat implements Closeable {
    
    private final Socket socket;
    private final BufferedReader leitor;
    private final PrintWriter escritor;
    private String login;
    
    public EscutaChat(Socket socket) throws IOException{
        this.socket = socket;
        this.leitor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.escritor = new PrintWriter(socket.getOutputStream(), true);
    }
    
    public String enviaMensagemEPegaResposta(String texto){
        enviaMensagem(texto);
        return getMensagem();
    }
    
    public boolean enviaMensagem(String mensagem){
        escritor.println(mensagem);
        
        //retorna true se nao houver erros ao enviar a mensagem
        return !escritor.checkError();
    }
    
    public String getMensagem(){
        try {
            return leitor.readLine();
        } catch (IOException ex) {
            Logger.getLogger(EscutaChat.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }    
    
    @Override
    public void close(){
        mensagemFinalizarCliente();
        escritor.close();
        try {
            leitor.close();
        } catch (IOException ex) {
            System.err.println("Erro ao finalizar LEITOR!!!");
            Logger.getLogger(EscutaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("Erro ao finalizar SOCKET!!!");
            Logger.getLogger(EscutaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void mensagemFinalizarCliente(){
        System.out.println("Cliente Desconectando "+getLogin());
    }
    
    public Socket getSocket(){
        return this.socket;
    }
    
    public String getLogin(){
        return this.login;
    }
    
    public void setLogin(String login){
        this.login = login;
    }
    
}