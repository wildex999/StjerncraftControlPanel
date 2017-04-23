import MessagesClient from "./messagesclient";

interface ConnectionHandler {
    onConnected();
    onDisconnected(code: number, reason: string, wasClean: boolean);
    onError(errorMessage: string);
}

export default class Core {
    client: MessagesClient | null;

    constructor() {
        console.log("Loaded ModuleCore");
    }

    connect(server: string, port: number, connectionHandler: ConnectionHandler) {
        //TODO: If already connected, disconnect and unload all loaded Modules(Or retain them until reconnect and do permission check?)
        if(this.client) {
            this.client.disconnect(false, "New connection");
            this.client = null;
        }

        console.log("Connecting to " + server + ":" + port);
        this.client = new MessagesClient(server, port);
        this.client.onConnected = function () {
            connectionHandler.onConnected();
        };
        this.client.onDisconnected = function (close: CloseEvent) {
            connectionHandler.onDisconnected(close.code, close.reason, close.wasClean);
        };

        try {
            this.client.connect();
        } catch(exception) {
            let error = "Error while setting up connection: " + exception;
            if(this.client) {
                this.client.disconnect(true, error);
                this.client = null;
            }
        }
    }

    login() {}
    logout() {}
    getModules() {}
    loadModule() {}


}