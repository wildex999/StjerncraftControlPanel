interface Message {
    messageType: string;
    message: any;
}

type MessageHandler = (message: Message) => any;

export default class MessagesClient {
    messageHandlers: { [key: string]: MessageHandler };

    socket: WebSocket | null;
    server: string;
    port: number;

    onConnected: () => any;
    onDisconnected: (close: CloseEvent) => any;


    constructor(server: string, port: number) {
        this.server = server;
        this.port = port;
    }

    public connect() {
        if(this.socket != null)
            this.disconnect(false, "New connect");

        this.socket = new WebSocket("ws://" + this.server + ":" + this.port + "/ws");

        this.socket.onopen = () => {
            this.onConnected();
        }

        this.socket.onclose = (close: CloseEvent) => {
            this.onDisconnected(close);
        }

        this.socket.onmessage = () => this.onMessage;
    }

    public disconnect(dueToError: boolean, reason: string) {
        if(this.socket == null)
            return;

        this.disconnectInternal(dueToError ? 1001 : 1000, reason);
    }

    private disconnectInternal(code: number, reason: string) {
        if(this.socket == null) {
            console.log("Trying to disconnect null socket: " + code + " | " + reason);
            return;
        }

        this.socket.close(code, reason);
        this.socket = null;
    }

    /**
     * Handle messages from the WebSocket server.
     * @param message 
     */
    private onMessage(message: MessageEvent) {
            if(!message.data) {
                console.log("Got no data in message: " + message);
                return;
            }

            //Parse the message and call the relevant handler
            try {
                let msg: Message = JSON.parse(message.data);
                if(!msg.messageType)
                    throw new Error("Message is missing expected data: " + msg);

                let handler = this.messageHandlers[msg.messageType];
                if(handler)
                    handler(msg);
                else
                    this.onMessageMissingHandler(msg.messageType);
            } catch(e) {
                this.onParseMessageFailed(e);
            }
    }

    /**
     * Called when parsing a message fails, either due to corrupted data, or unexpected protocol.
     * @param error The error might be an exception, or an error message.
     */
    onParseMessageFailed(error: any) {
        console.log("Failed to parse message, closing connection. Exception: " + error);
        this.disconnectInternal(1002, error);
    }

    /**
     * Called when receiving a message with a message type which we have no handler for.
     * @param messageType
     */
    onMessageMissingHandler(messageType: string) {
        let error = "Got message without handler: " + messageType;
        console.log(error);
        this.disconnectInternal(1003, error);
    }

    /**
     * Set the handler for a given message type.
     * @param messageType 
     * @param handler 
     */
    setHandler(messageType: string, handler: MessageHandler) {
        this.messageHandlers[messageType] = handler;
    }
}