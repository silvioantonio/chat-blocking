package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SILVIO
 */
public class Servidor {
    
    public static final int PORT = 8000;    
    private final List<EscutaChat> listaDeClientesConectados;
    private ServerSocket serverSocket;
    
    public Servidor(){
        listaDeClientesConectados = new ArrayList<>();
    }
    
    private void iniciar() throws IOException{
        this.serverSocket = new ServerSocket(PORT);
        loopDeConexaoDoCliente();
    }
    
    private void loopDeConexaoDoCliente(){
        System.out.println("Aguardando nova Conexao");
        
        try{
            while(true){
                final EscutaChat socketDoCliente;
                
                try{
                //aguarda e aceita uma conexao de um cliente (serverSocket.accept())
                socketDoCliente = new EscutaChat(serverSocket.accept());
                System.out.println("Nova conexao: "+socketDoCliente.getSocket().getRemoteSocketAddress());
                } catch(IOException e){
                    System.err.println("Erro ao aceitar a conexao do cliente, ERRO: "+e.getMessage());
                    continue;
                }
                
                try{
                    //Cria uma nova Thread para permitir que o servidor nao fique bloqueado enquanto
                    //atende as requisicoes de um unico cliente.
                    new Thread(() -> loopDeMensagensDoCliente(socketDoCliente)).start();
                }catch (OutOfMemoryError ex) {
                    System.err.println("Nao foi possivel criar thread para o novo cliente. O servidor possivelmente est� sobrecarregdo. Conexao sera fechada: ");
                    System.err.println(ex.getMessage());
                    socketDoCliente.close();
                }

                listaDeClientesConectados.add(socketDoCliente);
            }
        }finally{
            try {
                stop();
            } catch (IOException ex) {
                System.err.println("Houve um erro, ERRO AO FINALIZAR SERVIDOR!!!");
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void loopDeMensagensDoCliente(EscutaChat socketDoCliente){
        String mensagemDoCliente;
        
        //Loop que aguarda mensagens do cliente recebido por parametro
        //Se a mensagem for nula, entao o cliente desconectou.
        while((mensagemDoCliente = socketDoCliente.getMensagem()) != null){
            
            if(mensagemDoCliente.endsWith("sair")){
                socketDoCliente.enviaMensagem("Desconectando :"+socketDoCliente.getLogin());
                break;
            }
                        
            //A mensagem começara com 'login' apenas uma vez por cliente
            if(mensagemDoCliente.startsWith("Login")){
                socketDoCliente.setLogin(getConteudoDaMensagem(mensagemDoCliente));
                socketDoCliente.enviaMensagem("Bem Vindo "+getConteudoDaMensagem(mensagemDoCliente));
            }else {
                enviaMensagemParaTodos(socketDoCliente, mensagemDoCliente);
            }
            
            System.out.println("Mensagem do cliente "+socketDoCliente.getLogin()+": "+mensagemDoCliente);
            
        }
        socketDoCliente.close();
    }
    
    private String getConteudoDaMensagem(String mensagem){
        //descobre a posicao do primeiro espaco
        int i = mensagem.indexOf(" ");
        
        //se encontrou espaco, retornu tudo apos ele
        if(i > -1){
            return mensagem.substring(i + 1);
        }
        //se nao encontrar espacos, returna toda a mensagem
        return mensagem;
    }
    
    private void enviaMensagemParaTodos(EscutaChat socketDoClienteOrigem, String mensagemDoCliente){    
        //usa um iterator para percorrer a lista de clientes conectados
        final Iterator<EscutaChat> iterator = this.listaDeClientesConectados.iterator();
        int numeroDeClientesQueReceberamAMensagem = 0;
        int i;
        String loginDaMensagemPrivada;
        
        /*Percorre a lista usando o iterator enquanto existir um proximo elemento (hasNext)
        * para processar, ou seja, enquanto nao percorrer a lista inteira.
        */
        while(iterator.hasNext()){
            //pega o elemento autal da lista
            EscutaChat clienteDestino = iterator.next();
            
            //verifica se o cliente atual da lista nao foi o mesmo que enviou a mensagem
            //se nao for, entao a mensagem e enviada para esse cliente
            if(!clienteDestino.equals(socketDoClienteOrigem)){
                if(mensagemDoCliente.startsWith("@")){
                    i = mensagemDoCliente.indexOf(" ");//pega o indice do primeiro espaco
                    loginDaMensagemPrivada = mensagemDoCliente.substring(1, i);//pega o login do destinatario
                    if(clienteDestino.getLogin().equalsIgnoreCase(loginDaMensagemPrivada)){
                        clienteDestino.enviaMensagem(socketDoClienteOrigem.getLogin()+": "+getConteudoDaMensagem(mensagemDoCliente));
                        break;
                    }
                }else{
                    if(clienteDestino.enviaMensagem(mensagemDoCliente)){ 
                        numeroDeClientesQueReceberamAMensagem ++;
                    } else {
                        //Caso a mensagem nao tenha sido enviada e porque o cliente desconectou
                        //remove o cliente da lista
                        iterator.remove();
                    }
                }
            }
        }
        System.out.println("Mensagem publica enviada para "+numeroDeClientesQueReceberamAMensagem+" clientes.");
    }
    
    private void stop() throws IOException {
        System.out.println("Houve algum erro, Finalizando servidor...");
        serverSocket.close();
    }
    
    public static void main(String[] args) throws IOException {
        Servidor servidor = new Servidor();
        try {
            servidor.iniciar();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }
    
}
