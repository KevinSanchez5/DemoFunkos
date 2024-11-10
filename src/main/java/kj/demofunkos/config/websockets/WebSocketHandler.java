package kj.demofunkos.config.websockets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender {
    private final String entity;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public WebSocketHandler(String entity) {
        this.entity = entity;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        log.info("Conexion establecida");
        log.info("Session :" + session);
        sessions.add(session);
        TextMessage message = new TextMessage("Updates Web socket:" + entity +
                " - Funkos API");
        log.info("Servidor envia: {}", message);
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Conexion cerrada");
        sessions.remove(session);
    }


    @Override
    public void sendMessage(String message) throws IOException {
        for(WebSocketSession session : sessions) {
            if(session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }

    }

    @Scheduled(fixedRate = 1000)
    @Override
    public void sendPeriodicMessage() throws IOException {
        for(WebSocketSession session : sessions){
            if(session.isOpen()){
                String broadcast = "Mensaje periodico del servidor" + LocalTime.now().toString();
                session.sendMessage(new TextMessage(broadcast));
            }
        }

    }

    @Override
    public List<String> getSubProtocols() {
        return List.of("subprotocol.demo.websocket");
    }


    public void handleTransporError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error de transporte con el servidor", exception);
    }
}
