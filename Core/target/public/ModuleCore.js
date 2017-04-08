"use strict";
(function () {
    requirejs.config({
        "baseUrl": "libs",
        "paths": {
            "jquery": "jquery.min"
        }
    });
    requirejs(["main"], function (Main) {
        var main = new Main();
        main.run();
    });
})();
define("messagesclient", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var MessagesClient = (function () {
        function MessagesClient(server, port) {
            this.server = server;
            this.port = port;
        }
        MessagesClient.prototype.connect = function () {
            var _this = this;
            if (this.socket != null)
                this.disconnect(false, "New connect");
            this.socket = new WebSocket("ws://" + this.server + ":" + this.port + "/ws");
            this.socket.onopen = function () {
                _this.onConnected();
            };
            this.socket.onclose = function (close) {
                _this.onDisconnected(close);
            };
            this.socket.onmessage = function () { return _this.onMessage; };
        };
        MessagesClient.prototype.disconnect = function (dueToError, reason) {
            if (this.socket == null)
                return;
            this.disconnectInternal(dueToError ? 1001 : 1000, reason);
        };
        MessagesClient.prototype.disconnectInternal = function (code, reason) {
            if (this.socket == null) {
                console.log("Trying to disconnect null socket: " + code + " | " + reason);
                return;
            }
            this.socket.close(code, reason);
            this.socket = null;
        };
        /**
         * Handle messages from the WebSocket server.
         * @param message
         */
        MessagesClient.prototype.onMessage = function (message) {
            if (!message.data) {
                console.log("Got no data in message: " + message);
                return;
            }
            //Parse the message and call the relevant handler
            try {
                var msg = JSON.parse(message.data);
                if (!msg.messageType)
                    throw new Error("Message is missing expected data: " + msg);
                var handler = this.messageHandlers[msg.messageType];
                if (handler)
                    handler(msg);
                else
                    this.onMessageMissingHandler(msg.messageType);
            }
            catch (e) {
                this.onParseMessageFailed(e);
            }
        };
        /**
         * Called when parsing a message fails, either due to corrupted data, or unexpected protocol.
         * @param error The error might be an exception, or an error message.
         */
        MessagesClient.prototype.onParseMessageFailed = function (error) {
            console.log("Failed to parse message, closing connection. Exception: " + error);
            this.disconnectInternal(1002, error);
        };
        /**
         * Called when receiving a message with a message type which we have no handler for.
         * @param messageType
         */
        MessagesClient.prototype.onMessageMissingHandler = function (messageType) {
            var error = "Got message without handler: " + messageType;
            console.log(error);
            this.disconnectInternal(1003, error);
        };
        /**
         * Set the handler for a given message type.
         * @param messageType
         * @param handler
         */
        MessagesClient.prototype.setHandler = function (messageType, handler) {
            this.messageHandlers[messageType] = handler;
        };
        return MessagesClient;
    }());
    exports.default = MessagesClient;
});
define("core", ["require", "exports", "messagesclient"], function (require, exports, messagesclient_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var Core = (function () {
        function Core() {
            console.log("Loaded ModuleCore");
        }
        Core.prototype.connect = function (server, port, connectionHandler) {
            //TODO: If already connected, disconnect and unload all loaded Modules(Or retain them until reconnect and do permission check?)
            if (this.client) {
                this.client.disconnect(false, "New connection");
                this.client = null;
            }
            console.log("Connecting to " + server + ":" + port);
            this.client = new messagesclient_1.default(server, port);
            this.client.onConnected = function () {
                connectionHandler.onConnected();
            };
            this.client.onDisconnected = function (close) {
                connectionHandler.onDisconnected(close.code, close.reason, close.wasClean);
            };
            try {
                this.client.connect();
            }
            catch (exception) {
                var error = "Error while setting up connection: " + exception;
                if (this.client) {
                    this.client.disconnect(true, error);
                    this.client = null;
                }
            }
        };
        Core.prototype.login = function () { };
        Core.prototype.logout = function () { };
        Core.prototype.getModules = function () { };
        Core.prototype.loadModule = function () { };
        return Core;
    }());
    exports.default = Core;
});
define("main", ["require", "exports", "core"], function (require, exports, core_1) {
    "use strict";
    var Main = (function () {
        function Main() {
        }
        Main.prototype.run = function () {
            var core = new core_1.default();
            //Connect to server
            core.connect("localhost", 8080, new (function () {
                function class_1() {
                }
                class_1.prototype.onConnected = function () {
                    console.log("Connected");
                };
                class_1.prototype.onDisconnected = function (code, reason, wasClean) {
                    console.log("Disconnected: " + code + " | " + reason + " | " + wasClean);
                };
                class_1.prototype.onError = function (errorMessage) {
                    console.log("Connection Error: " + errorMessage);
                };
                return class_1;
            }()));
            //Get active modules from server(Which we have permission to load)
            //Wait for user settings from server(In response to connecting)
            //Load all active modules
        };
        return Main;
    }());
    return Main;
});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTW9kdWxlQ29yZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uL3NyYy90cy9jb25maWcudHMiLCIuLi9zcmMvdHMvbWVzc2FnZXNjbGllbnQudHMiLCIuLi9zcmMvdHMvY29yZS50cyIsIi4uL3NyYy90cy9tYWluLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQSxDQUNJO0lBQ0ksU0FBUyxDQUFDLE1BQU0sQ0FBQztRQUNiLFNBQVMsRUFBRSxNQUFNO1FBQ2pCLE9BQU8sRUFBRTtZQUNMLFFBQVEsRUFBRSxZQUFZO1NBQ3pCO0tBQ0osQ0FBQyxDQUFDO0lBRUgsU0FBUyxDQUFDLENBQUMsTUFBTSxDQUFDLEVBQUUsVUFBVSxJQUFJO1FBQzlCLElBQUksSUFBSSxHQUFHLElBQUksSUFBSSxFQUFFLENBQUM7UUFDdEIsSUFBSSxDQUFDLEdBQUcsRUFBRSxDQUFDO0lBQ2YsQ0FBQyxDQUFDLENBQUM7QUFDUCxDQUFDLENBQUMsRUFBRSxDQUFDOzs7O0lDTlQ7UUFXSSx3QkFBWSxNQUFjLEVBQUUsSUFBWTtZQUNwQyxJQUFJLENBQUMsTUFBTSxHQUFHLE1BQU0sQ0FBQztZQUNyQixJQUFJLENBQUMsSUFBSSxHQUFHLElBQUksQ0FBQztRQUNyQixDQUFDO1FBRU0sZ0NBQU8sR0FBZDtZQUFBLGlCQWVDO1lBZEcsRUFBRSxDQUFBLENBQUMsSUFBSSxDQUFDLE1BQU0sSUFBSSxJQUFJLENBQUM7Z0JBQ25CLElBQUksQ0FBQyxVQUFVLENBQUMsS0FBSyxFQUFFLGFBQWEsQ0FBQyxDQUFDO1lBRTFDLElBQUksQ0FBQyxNQUFNLEdBQUcsSUFBSSxTQUFTLENBQUMsT0FBTyxHQUFHLElBQUksQ0FBQyxNQUFNLEdBQUcsR0FBRyxHQUFHLElBQUksQ0FBQyxJQUFJLEdBQUcsS0FBSyxDQUFDLENBQUM7WUFFN0UsSUFBSSxDQUFDLE1BQU0sQ0FBQyxNQUFNLEdBQUc7Z0JBQ2pCLEtBQUksQ0FBQyxXQUFXLEVBQUUsQ0FBQztZQUN2QixDQUFDLENBQUE7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QkQsSUF1QkM7SUFFRCxPQUFTLElBQUksQ0FBQyIsInNvdXJjZXNDb250ZW50IjpbIihcclxuICAgIGZ1bmN0aW9uICgpIHtcclxuICAgICAgICByZXF1aXJlanMuY29uZmlnKHtcclxuICAgICAgICAgICAgXCJiYXNlVXJsXCI6IFwibGlic1wiLFxyXG4gICAgICAgICAgICBcInBhdGhzXCI6IHtcclxuICAgICAgICAgICAgICAgIFwianF1ZXJ5XCI6IFwianF1ZXJ5Lm1pblwiXHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9KTtcclxuXHJcbiAgICAgICAgcmVxdWlyZWpzKFtcIm1haW5cIl0sIGZ1bmN0aW9uIChNYWluKSB7XHJcbiAgICAgICAgICAgIGxldCBtYWluID0gbmV3IE1haW4oKTtcclxuICAgICAgICAgICAgbWFpbi5ydW4oKTtcclxuICAgICAgICB9KTtcclxuICAgIH0pKCk7IiwiaW50ZXJmYWNlIE1lc3NhZ2Uge1xyXG4gICAgbWVzc2FnZVR5cGU6IHN0cmluZztcclxuICAgIG1lc3NhZ2U6IGFueTtcclxufVxyXG5cclxudHlwZSBNZXNzYWdlSGFuZGxlciA9IChtZXNzYWdlOiBNZXNzYWdlKSA9PiBhbnk7XHJcblxyXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBNZXNzYWdlc0NsaWVudCB7XHJcbiAgICBtZXNzYWdlSGFuZGxlcnM6IHsgW2tleTogc3RyaW5nXTogTWVzc2FnZUhhbmRsZXIgfTtcclxuXHJcbiAgICBzb2NrZXQ6IFdlYlNvY2tldCB8IG51bGw7XHJcbiAgICBzZXJ2ZXI6IHN0cmluZztcclxuICAgIHBvcnQ6IG51bWJlcjtcclxuXHJcbiAgICBvbkNvbm5lY3RlZDogKCkgPT4gYW55O1xyXG4gICAgb25EaXNjb25uZWN0ZWQ6IChjbG9zZTogQ2xvc2VFdmVudCkgPT4gYW55O1xyXG5cclxuXHJcbiAgICBjb25zdHJ1Y3RvcihzZXJ2ZXI6IHN0cmluZywgcG9ydDogbnVtYmVyKSB7XHJcbiAgICAgICAgdGhpcy5zZXJ2ZXIgPSBzZXJ2ZXI7XHJcbiAgICAgICAgdGhpcy5wb3J0ID0gcG9ydDtcclxuICAgIH1cclxuXHJcbiAgICBwdWJsaWMgY29ubmVjdCgpIHtcclxuICAgICAgICBpZih0aGlzLnNvY2tldCAhPSBudWxsKVxyXG4gICAgICAgICAgICB0aGlzLmRpc2Nvbm5lY3QoZmFsc2UsIFwiTmV3IGNvbm5lY3RcIik7XHJcblxyXG4gICAgICAgIHRoaXMuc29ja2V0ID0gbmV3IFdlYlNvY2tldChcIndzOi8vXCIgKyB0aGlzLnNlcnZlciArIFwiOlwiICsgdGhpcy5wb3J0ICsgXCIvd3NcIik7XHJcblxyXG4gICAgICAgIHRoaXMuc29ja2V0Lm9ub3BlbiA9ICgpID0+IHtcclxuICAgICAgICAgICAgdGhpcy5vbkNvbm5lY3RlZCgpO1xyXG4gICAgICAgIH1cclxuXHJcbiAgICAgICAgdGhpcy5zb2NrZXQub25jbG9zZSA9IChjbG9zZTogQ2xvc2VFdmVudCkgPT4ge1xyXG4gICAgICAgICAgICB0aGlzLm9uRGlzY29ubmVjdGVkKGNsb3NlKTtcclxuICAgICAgICB9XHJcblxyXG4gICAgICAgIHRoaXMuc29ja2V0Lm9ubWVzc2FnZSA9ICgpID0+IHRoaXMub25NZXNzYWdlO1xyXG4gICAgfVxyXG5cclxuICAgIHB1YmxpYyBkaXNjb25uZWN0KGR1ZVRvRXJyb3I6IGJvb2xlYW4sIHJlYXNvbjogc3RyaW5nKSB7XHJcbiAgICAgICAgaWYodGhpcy5zb2NrZXQgPT0gbnVsbClcclxuICAgICAgICAgICAgcmV0dXJuO1xyXG5cclxuICAgICAgICB0aGlzLmRpc2Nvbm5lY3RJbnRlcm5hbChkdWVUb0Vycm9yID8gMTAwMSA6IDEwMDAsIHJlYXNvbik7XHJcbiAgICB9XHJcblxyXG4gICAgcHJpdmF0ZSBkaXNjb25uZWN0SW50ZXJuYWwoY29kZTogbnVtYmVyLCByZWFzb246IHN0cmluZykge1xyXG4gICAgICAgIGlmKHRoaXMuc29ja2V0ID09IG51bGwpIHtcclxuICAgICAgICAgICAgY29uc29sZS5sb2coXCJUcnlpbmcgdG8gZGlzY29ubmVjdCBudWxsIHNvY2tldDogXCIgKyBjb2RlICsgXCIgfCBcIiArIHJlYXNvbik7XHJcbiAgICAgICAgICAgIHJldHVybjtcclxuICAgICAgICB9XHJcblxyXG4gICAgICAgIHRoaXMuc29ja2V0LmNsb3NlKGNvZGUsIHJlYXNvbik7XHJcbiAgICAgICAgdGhpcy5zb2NrZXQgPSBudWxsO1xyXG4gICAgfVxyXG5cclxuICAgIC8qKlxyXG4gICAgICogSGFuZGxlIG1lc3NhZ2VzIGZyb20gdGhlIFdlYlNvY2tldCBzZXJ2ZXIuXHJcbiAgICAgKiBAcGFyYW0gbWVzc2FnZSBcclxuICAgICAqL1xyXG4gICAgcHJpdmF0ZSBvbk1lc3NhZ2UobWVzc2FnZTogTWVzc2FnZUV2ZW50KSB7XHJcbiAgICAgICAgICAgIGlmKCFtZXNzYWdlLmRhdGEpIHtcclxuICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiR290IG5vIGRhdGEgaW4gbWVzc2FnZTogXCIgKyBtZXNzYWdlKTtcclxuICAgICAgICAgICAgICAgIHJldHVybjtcclxuICAgICAgICAgICAgfVxyXG5cclxuICAgICAgICAgICAgLy9QYXJzZSB0aGUgbWVzc2FnZSBhbmQgY2FsbCB0aGUgcmVsZXZhbnQgaGFuZGxlclxyXG4gICAgICAgICAgICB0cnkge1xyXG4gICAgICAgICAgICAgICAgbGV0IG1zZzogTWVzc2FnZSA9IEpTT04ucGFyc2UobWVzc2FnZS5kYXRhKTtcclxuICAgICAgICAgICAgICAgIGlmKCFtc2cubWVzc2FnZVR5cGUpXHJcbiAgICAgICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKFwiTWVzc2FnZSBpcyBtaXNzaW5nIGV4cGVjdGVkIGRhdGE6IFwiICsgbXNnKTtcclxuXHJcbiAgICAgICAgICAgICAgICBsZXQgaGFuZGxlciA9IHRoaXMubWVzc2FnZUhhbmRsZXJzW21zZy5tZXNzYWdlVHlwZV07XHJcbiAgICAgICAgICAgICAgICBpZihoYW5kbGVyKVxyXG4gICAgICAgICAgICAgICAgICAgIGhhbmRsZXIobXNnKTtcclxuICAgICAgICAgICAgICAgIGVsc2VcclxuICAgICAgICAgICAgICAgICAgICB0aGlzLm9uTWVzc2FnZU1pc3NpbmdIYW5kbGVyKG1zZy5tZXNzYWdlVHlwZSk7XHJcbiAgICAgICAgICAgIH0gY2F0Y2goZSkge1xyXG4gICAgICAgICAgICAgICAgdGhpcy5vblBhcnNlTWVzc2FnZUZhaWxlZChlKTtcclxuICAgICAgICAgICAgfVxyXG4gICAgfVxyXG5cclxuICAgIC8qKlxyXG4gICAgICogQ2FsbGVkIHdoZW4gcGFyc2luZyBhIG1lc3NhZ2UgZmFpbHMsIGVpdGhlciBkdWUgdG8gY29ycnVwdGVkIGRhdGEsIG9yIHVuZXhwZWN0ZWQgcHJvdG9jb2wuXHJcbiAgICAgKiBAcGFyYW0gZXJyb3IgVGhlIGVycm9yIG1pZ2h0IGJlIGFuIGV4Y2VwdGlvbiwgb3IgYW4gZXJyb3IgbWVzc2FnZS5cclxuICAgICAqL1xyXG4gICAgb25QYXJzZU1lc3NhZ2VGYWlsZWQoZXJyb3I6IGFueSkge1xyXG4gICAgICAgIGNvbnNvbGUubG9nKFwiRmFpbGVkIHRvIHBhcnNlIG1lc3NhZ2UsIGNsb3NpbmcgY29ubmVjdGlvbi4gRXhjZXB0aW9uOiBcIiArIGVycm9yKTtcclxuICAgICAgICB0aGlzLmRpc2Nvbm5lY3RJbnRlcm5hbCgxMDAyLCBlcnJvcik7XHJcbiAgICB9XHJcblxyXG4gICAgLyoqXHJcbiAgICAgKiBDYWxsZWQgd2hlbiByZWNlaXZpbmcgYSBtZXNzYWdlIHdpdGggYSBtZXNzYWdlIHR5cGUgd2hpY2ggd2UgaGF2ZSBubyBoYW5kbGVyIGZvci5cclxuICAgICAqIEBwYXJhbSBtZXNzYWdlVHlwZVxyXG4gICAgICovXHJcbiAgICBvbk1lc3NhZ2VNaXNzaW5nSGFuZGxlcihtZXNzYWdlVHlwZTogc3RyaW5nKSB7XHJcbiAgICAgICAgbGV0IGVycm9yID0gXCJHb3QgbWVzc2FnZSB3aXRob3V0IGhhbmRsZXI6IFwiICsgbWVzc2FnZVR5cGU7XHJcbiAgICAgICAgY29uc29sZS5sb2coZXJyb3IpO1xyXG4gICAgICAgIHRoaXMuZGlzY29ubmVjdEludGVybmFsKDEwMDMsIGVycm9yKTtcclxuICAgIH1cclxuXHJcbiAgICAvKipcclxuICAgICAqIFNldCB0aGUgaGFuZGxlciBmb3IgYSBnaXZlbiBtZXNzYWdlIHR5cGUuXHJcbiAgICAgKiBAcGFyYW0gbWVzc2FnZVR5cGUgXHJcbiAgICAgKiBAcGFyYW0gaGFuZGxlciBcclxuICAgICAqL1xyXG4gICAgc2V0SGFuZGxlcihtZXNzYWdlVHlwZTogc3RyaW5nLCBoYW5kbGVyOiBNZXNzYWdlSGFuZGxlcikge1xyXG4gICAgICAgIHRoaXMubWVzc2FnZUhhbmRsZXJzW21lc3NhZ2VUeXBlXSA9IGhhbmRsZXI7XHJcbiAgICB9XHJcbn0iLCJpbXBvcnQgTWVzc2FnZXNDbGllbnQgZnJvbSBcIi4vbWVzc2FnZXNjbGllbnRcIjtcclxuXHJcbmludGVyZmFjZSBDb25uZWN0aW9uSGFuZGxlciB7XHJcbiAgICBvbkNvbm5lY3RlZCgpO1xyXG4gICAgb25EaXNjb25uZWN0ZWQoY29kZTogbnVtYmVyLCByZWFzb246IHN0cmluZywgd2FzQ2xlYW46IGJvb2xlYW4pO1xyXG4gICAgb25FcnJvcihlcnJvck1lc3NhZ2U6IHN0cmluZyk7XHJcbn1cclxuXHJcbmV4cG9ydCBkZWZhdWx0IGNsYXNzIENvcmUge1xyXG4gICAgY2xpZW50OiBNZXNzYWdlc0NsaWVudCB8IG51bGw7XHJcblxyXG4gICAgY29uc3RydWN0b3IoKSB7XHJcbiAgICAgICAgY29uc29sZS5sb2coXCJMb2FkZWQgTW9kdWxlQ29yZVwiKTtcclxuICAgIH1cclxuXHJcbiAgICBjb25uZWN0KHNlcnZlcjogc3RyaW5nLCBwb3J0OiBudW1iZXIsIGNvbm5lY3Rpb25IYW5kbGVyOiBDb25uZWN0aW9uSGFuZGxlcikge1xyXG4gICAgICAgIC8vVE9ETzogSWYgYWxyZWFkeSBjb25uZWN0ZWQsIGRpc2Nvbm5lY3QgYW5kIHVubG9hZCBhbGwgbG9hZGVkIE1vZHVsZXMoT3IgcmV0YWluIHRoZW0gdW50aWwgcmVjb25uZWN0IGFuZCBkbyBwZXJtaXNzaW9uIGNoZWNrPylcclxuICAgICAgICBpZih0aGlzLmNsaWVudCkge1xyXG4gICAgICAgICAgICB0aGlzLmNsaWVudC5kaXNjb25uZWN0KGZhbHNlLCBcIk5ldyBjb25uZWN0aW9uXCIpO1xyXG4gICAgICAgICAgICB0aGlzLmNsaWVudCA9IG51bGw7XHJcbiAgICAgICAgfVxyXG5cclxuICAgICAgICBjb25zb2xlLmxvZyhcIkNvbm5lY3RpbmcgdG8gXCIgKyBzZXJ2ZXIgKyBcIjpcIiArIHBvcnQpO1xyXG4gICAgICAgIHRoaXMuY2xpZW50ID0gbmV3IE1lc3NhZ2VzQ2xpZW50KHNlcnZlciwgcG9ydCk7XHJcbiAgICAgICAgdGhpcy5jbGllbnQub25Db25uZWN0ZWQgPSBmdW5jdGlvbiAoKSB7XHJcbiAgICAgICAgICAgIGNvbm5lY3Rpb25IYW5kbGVyLm9uQ29ubmVjdGVkKCk7XHJcbiAgICAgICAgfTtcclxuICAgICAgICB0aGlzLmNsaWVudC5vbkRpc2Nvbm5lY3RlZCA9IGZ1bmN0aW9uIChjbG9zZTogQ2xvc2VFdmVudCkge1xyXG4gICAgICAgICAgICBjb25uZWN0aW9uSGFuZGxlci5vbkRpc2Nvbm5lY3RlZChjbG9zZS5jb2RlLCBjbG9zZS5yZWFzb24sIGNsb3NlLndhc0NsZWFuKTtcclxuICAgICAgICB9O1xyXG5cclxuICAgICAgICB0cnkge1xyXG4gICAgICAgICAgICB0aGlzLmNsaWVudC5jb25uZWN0KCk7XHJcbiAgICAgICAgfSBjYXRjaChleGNlcHRpb24pIHtcclxuICAgICAgICAgICAgbGV0IGVycm9yID0gXCJFcnJvciB3aGlsZSBzZXR0aW5nIHVwIGNvbm5lY3Rpb246IFwiICsgZXhjZXB0aW9uO1xyXG4gICAgICAgICAgICBpZih0aGlzLmNsaWVudCkge1xyXG4gICAgICAgICAgICAgICAgdGhpcy5jbGllbnQuZGlzY29ubmVjdCh0cnVlLCBlcnJvcik7XHJcbiAgICAgICAgICAgICAgICB0aGlzLmNsaWVudCA9IG51bGw7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9XHJcbiAgICB9XHJcblxyXG4gICAgbG9naW4oKSB7fVxyXG4gICAgbG9nb3V0KCkge31cclxuICAgIGdldE1vZHVsZXMoKSB7fVxyXG4gICAgbG9hZE1vZHVsZSgpIHt9XHJcblxyXG5cclxufSIsImltcG9ydCBDb3JlIGZyb20gXCIuL2NvcmVcIjtcclxuXHJcbmNsYXNzIE1haW4ge1xyXG4gICAgcHVibGljIHJ1bigpIHtcclxuICAgICAgICBsZXQgY29yZSA9IG5ldyBDb3JlKCk7XHJcblxyXG4gICAgICAgIC8vQ29ubmVjdCB0byBzZXJ2ZXJcclxuICAgICAgICBjb3JlLmNvbm5lY3QoXCJsb2NhbGhvc3RcIiwgODA4MCwgbmV3IGNsYXNzIHtcclxuICAgICAgICAgICAgb25Db25uZWN0ZWQoKSB7XHJcbiAgICAgICAgICAgICAgICBjb25zb2xlLmxvZyhcIkNvbm5lY3RlZFwiKTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBvbkRpc2Nvbm5lY3RlZChjb2RlOiBudW1iZXIsIHJlYXNvbjogc3RyaW5nLCB3YXNDbGVhbjogYm9vbGVhbikge1xyXG4gICAgICAgICAgICAgICAgY29uc29sZS5sb2coXCJEaXNjb25uZWN0ZWQ6IFwiICsgY29kZSArIFwiIHwgXCIgKyByZWFzb24gKyBcIiB8IFwiICsgd2FzQ2xlYW4pO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIG9uRXJyb3IoZXJyb3JNZXNzYWdlOiBzdHJpbmcpIHtcclxuICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiQ29ubmVjdGlvbiBFcnJvcjogXCIgKyBlcnJvck1lc3NhZ2UpO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfSk7XHJcbiAgICAgICAgXHJcbiAgICAgICAgLy9HZXQgYWN0aXZlIG1vZHVsZXMgZnJvbSBzZXJ2ZXIoV2hpY2ggd2UgaGF2ZSBwZXJtaXNzaW9uIHRvIGxvYWQpXHJcblxyXG4gICAgICAgIC8vV2FpdCBmb3IgdXNlciBzZXR0aW5ncyBmcm9tIHNlcnZlcihJbiByZXNwb25zZSB0byBjb25uZWN0aW5nKVxyXG5cclxuICAgICAgICAvL0xvYWQgYWxsIGFjdGl2ZSBtb2R1bGVzXHJcbiAgICB9XHJcbn1cclxuXHJcbmV4cG9ydCA9IE1haW47Il19