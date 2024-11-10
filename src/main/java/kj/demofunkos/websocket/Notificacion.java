package kj.demofunkos.websocket;

public record Notificacion<T>(
        String entidad,
        Tipo tipo,
        T data
) {

    public enum Tipo {CREATE, UPDATE, DELETE}
}
