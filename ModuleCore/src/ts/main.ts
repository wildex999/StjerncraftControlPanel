import Core from "./core";

class Main {
    public run() {
        let core = new Core();

        //Connect to server
        core.connect("localhost", 8080, new class {
            onConnected() {
                console.log("Connected");
            }
            onDisconnected(code: number, reason: string, wasClean: boolean) {
                console.log("Disconnected: " + code + " | " + reason + " | " + wasClean);
            }
            onError(errorMessage: string) {
                console.log("Connection Error: " + errorMessage);
            }
        });
        
        //Get active modules from server(Which we have permission to load)

        //Wait for user settings from server(In response to connecting)

        //Load all active modules
    }
}

export = Main;