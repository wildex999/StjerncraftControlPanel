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
                if (_this.socket != null)
                    _this.socket.send(JSON.stringify({ messageId: "testId", messageData: { data: "lol" } }));
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
            //Start session with the Module Manager, and get active modules from server(Which we have permission to load)
            //Wait for user settings from server(In response to connecting)
            //Load all active modules
        };
        return Main;
    }());
    return Main;
});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTW9kdWxlQ29yZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uL3NyYy90cy9jb25maWcudHMiLCIuLi9zcmMvdHMvbWVzc2FnZXNjbGllbnQudHMiLCIuLi9zcmMvdHMvY29yZS50cyIsIi4uL3NyYy90cy9tYWluLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQSxDQUNJO0lBQ0ksU0FBUyxDQUFDLE1BQU0sQ0FBQztRQUNiLFNBQVMsRUFBRSxNQUFNO1FBQ2pCLE9BQU8sRUFBRTtZQUNMLFFBQVEsRUFBRSxZQUFZO1NBQ3pCO0tBQ0osQ0FBQyxDQUFDO0lBRUgsU0FBUyxDQUFDLENBQUMsTUFBTSxDQUFDLEVBQUUsVUFBVSxJQUFJO1FBQzlCLElBQUksSUFBSSxHQUFHLElBQUksSUFBSSxFQUFFLENBQUM7UUFDdEIsSUFBSSxDQUFDLEdBQUcsRUFBRSxDQUFDO0lBQ2YsQ0FBQyxDQUFDLENBQUM7QUFDUCxDQUFDLENBQUMsRUFBRSxDQUFDOzs7O0lDTlQ7UUFXSSx3QkFBWSxNQUFjLEVBQUUsSUFBWTtZQUNwQyxJQUFJLENBQUMsTUFBTSxHQUFHLE1BQU0sQ0FBQztZQUNyQixJQUFJLENBQUMsSUFBSSxHQUFHLElBQUksQ0FBQztRQUNyQixDQUFDO1FBRU0sZ0NBQU8sR0FBZDtZQUFBLGlCQWlCQztZQWhCRyxFQUFFLENBQUEsQ0FBQyxJQUFJLENBQUMsTUFBTSxJQUFJLElBQUksQ0FBQztnQkFDbkIsSUFBSSxDQUFDLFVBQVUsQ0FBQyxLQUFLLEVBQUUsYUFBYSxDQUFDLENBQUM7WUFFMUMsSUFBSSxDQUFDLE1BQU0sR0FBRyxJQUFJLFNBQVMsQ0FBQyxPQUFPLEdBQUcsSUFBSSxDQUFDLE1BQU0sR0FBRyxHQUFHLEdBQUcsSUFBSSxDQUFDLElBQUksR0FBRyxLQUFLLENBQUMsQ0FBQztZQUU3RSxJQUFJLENBQUMsTUFBTSxDQUFDLE1BQU0sR0FBRztnQkFDakIsS0FBSSxDQUFDLFdBQVcsRUFBRSxDQUFDO2dCQUNuQixFQUFFLENBQUEsQ0FBQyxLQUFJLENBQUMsTUFBTSxJQUFJLElBQUksQ0FBQztvQkFDbkIsS0FBSSxDQUFDLE1BQU0sQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLFNBQVMsQ0FBQyxFQUFDLFNBQVMsRUFBRSxRQUFRLEVBQUUsV0FBVyxFQUFFLEVBQUMsSUFBSSxFQUFFLEtBQUssRUFBQyxFQUFDLENBQUMsQ0FBQyxDQUFDO1lBQzVGLENBQUMsQ0FBQTtZQUVELElBQUksQ0FBQyxNQUFNLENBQUMsT0FBTyxHQUFHLFVBQUMsS0FBaUI7Z0JBQ3BDLEtBQUksQ0FBQyxjQUFjLENBQUMsS0FBSyxDQUFDLENBQUM7WUFDL0IsQ0FBQyxDQUFBO1lBRUQsSUFBSSxDQUFDLE1BQU0sQ0FBQyxTQUFTLEdBQUcsY0FBTSxPQUFBLEtBQUksQ0FBQyxTQUFTLEVBQWQsQ0FBYyxDQUFDO1FBQ2pELENBQUM7UUFFTSxtQ0FBVSxHQUFqQixVQUFrQixVQUFtQixFQUFFLE1BQWM7WUFDakQsRUFBRSxDQUFBLENBQUMsSUFBSSxDQUFDLE1BQU0sSUFBSSxJQUFJLENBQUM7Z0JBQ25CLE1BQU0sQ0FBQztZQUVYLElBQUksQ0FBQyxrQkFBa0IsQ0FBQyxVQUFVLEdBQUcsSUFBSSxHQUFHLElBQUksRUFBRSxNQUFNLENBQUMsQ0FBQztRQUM5RCxDQUFDO1FBRU8sMkNBQWtCLEdBQTFCLFVBQTJCLElBQVksRUFBRSxNQUFjO1lBQ25ELEVBQUUsQ0FBQSxDQUFDLElBQUksQ0FBQyxNQUFNLElBQUksSUFBSSxDQUFDLENBQUMsQ0FBQztnQkFDckIsT0FBTyxDQUFDLEdBQUcsQ0FBQyxvQ0FBb0MsR0FBRyxJQUFJLEdBQUcsS0FBSyxHQUFHLE1BQU0sQ0FBQyxDQUFDO2dCQUMxRSxNQUFNLENBQUM7WUFDWCxDQUFDO1lBRUQsSUFBSSxDQUFDLE1BQU0sQ0FBQyxLQUFLLENBQUMsSUFBSSxFQUFFLE1BQU0sQ0FBQyxDQUFDO1lBQ2hDLElBQUksQ0FBQyxNQUFNLEdBQUcsSUFBSSxDQUFDO1FBQ3ZCLENBQUM7UUFFRDs7O1dBR0c7UUFDSyxrQ0FBUyxHQUFqQixVQUFrQixPQUFxQjtZQUMvQixFQUFFLENBQUEsQ0FBQyxDQUFDLE9BQU8sQ0FBQyxJQUFJLENBQUMsQ0FBQyxDQUFDO2dCQUNmLE9BQU8sQ0FBQyxHQUFHLENBQUMsMEJBQTBCLEdBQUcsT0FBTyxDQUFDLENBQUM7Z0JBQ2xELE1BQU0sQ0FBQztZQUNYLENBQUM7WUFFRCxpREFBaUQ7WUFDakQsSUFBSSxDQUFDO2dCQUNELElBQUksR0FBRyxHQUFZLElBQUksQ0FBQyxLQUFLLENBQUMsT0FBTyxDQUFDLElBQUksQ0FBQyxDQUFDO2dCQUM1QyxFQUFFLENBQUEsQ0FBQyxDQUFDLEdBQUcsQ0FBQyxXQUFXLENBQUM7b0JBQ2hCLE1BQU0sSUFBSSxLQUFLLENBQUMsb0NBQW9DLEdBQUcsR0FBRyxDQUFDLENBQUM7Z0JBRWhFLElBQUksT0FBTyxHQUFHLElBQUksQ0FBQyxlQUFlLENBQUMsR0FBRyxDQUFDLFdBQVcsQ0FBQyxDQUFDO2dCQUNwRCxFQUFFLENBQUEsQ0FBQyxPQUFPLENBQUM7b0JBQ1AsT0FBTyxDQUFDLEdBQUcsQ0FBQyxDQUFDO2dCQUNqQixJQUFJO29CQUNBLElBQUksQ0FBQyx1QkFBdUIsQ0FBQyxHQUFHLENBQUMsV0FBVyxDQUFDLENBQUM7WUFDdEQsQ0FBQztZQUFDLEtBQUssQ0FBQSxDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUM7Z0JBQ1IsSUFBSSxDQUFDLG9CQUFvQixDQUFDLENBQUMsQ0FBQyxDQUFDO1lBQ2pDLENBQUM7UUFDVCxDQUFDO1FBRUQ7OztXQUdHO1FBQ0gsNkNBQW9CLEdBQXBCLFVBQXFCLEtBQVU7WUFDM0IsT0FBTyxDQUFDLEdBQUcsQ0FBQywwREFBMEQsR0FBRyxLQUFLLENBQUMsQ0FBQztZQUNoRixJQUFJLENBQUMsa0JBQWtCLENBQUMsSUFBSSxFQUFFLEtBQUssQ0FBQyxDQUFDO1FBQ3pDLENBQUM7UUFFRDs7O1dBR0c7UUFDSCxnREFBdUIsR0FBdkIsVUFBd0IsV0FBbUI7WUFDdkMsSUFBSSxLQUFLLEdBQUcsK0JBQStCLEdBQUcsV0FBVyxDQUFDO1lBQzFELE9BQU8sQ0FBQyxHQUFHLENBQUMsS0FBSyxDQUFDLENBQUM7WUFDbkIsSUFBSSxDQUFDLGtCQUFrQixDQUFDLElBQUksRUFBRSxLQUFLLENBQUMsQ0FBQztRQUN6QyxDQUFDO1FBRUQ7Ozs7V0FJRztRQUNILG1DQUFVLEdBQVYsVUFBVyxXQUFtQixFQUFFLE9BQXVCO1lBQ25ELElBQUksQ0FBQyxlQUFlLENBQUMsV0FBVyxDQUFDLEdBQUcsT0FBTyxDQUFDO1FBQ2hELENBQUM7UUFDTCxxQkFBQztJQUFELENBQUMsQUF6R0QsSUF5R0M7Ozs7OztJQ3hHRDtRQUdJO1lBQ0ksT0FBTyxDQUFDLEdBQUcsQ0FBQyxtQkFBbUIsQ0FBQyxDQUFDO1FBQ3JDLENBQUM7UUFFRCxzQkFBTyxHQUFQLFVBQVEsTUFBYyxFQUFFLElBQVksRUFBRSxpQkFBb0M7WUFDdEUsK0hBQStIO1lBQy9ILEVBQUUsQ0FBQSxDQUFDLElBQUksQ0FBQyxNQUFNLENBQUMsQ0FBQyxDQUFDO2dCQUNiLElBQUksQ0FBQyxNQUFNLENBQUMsVUFBVSxDQUFDLEtBQUssRUFBRSxnQkFBZ0IsQ0FBQyxDQUFDO2dCQUNoRCxJQUFJLENBQUMsTUFBTSxHQUFHLElBQUksQ0FBQztZQUN2QixDQUFDO1lBRUQsT0FBTyxDQUFDLEdBQUcsQ0FBQyxnQkFBZ0IsR0FBRyxNQUFNLEdBQUcsR0FBRyxHQUFHLElBQUksQ0FBQyxDQUFDO1lBQ3BELElBQUksQ0FBQyxNQUFNLEdBQUcsSUFBSSx3QkFBYyxDQUFDLE1BQU0sRUFBRSxJQUFJLENBQUMsQ0FBQztZQUMvQyxJQUFJLENBQUMsTUFBTSxDQUFDLFdBQVcsR0FBRztnQkFDdEIsaUJBQWlCLENBQUMsV0FBVyxFQUFFLENBQUM7WUFDcEMsQ0FBQyxDQUFDO1lBQ0YsSUFBSSxDQUFDLE1BQU0sQ0FBQyxjQUFjLEdBQUcsVUFBVSxLQUFpQjtnQkFDcEQsaUJBQWlCLENBQUMsY0FBYyxDQUFDLEtBQUssQ0FBQyxJQUFJLEVBQUUsS0FBSyxDQUFDLE1BQU0sRUFBRSxLQUFLLENBQUMsUUFBUSxDQUFDLENBQUM7WUFDL0UsQ0FBQyxDQUFDO1lBRUYsSUFBSSxDQUFDO2dCQUNELElBQUksQ0FBQyxNQUFNLENBQUMsT0FBTyxFQUFFLENBQUM7WUFDMUIsQ0FBQztZQUFDLEtBQUssQ0FBQSxDQUFDLFNBQVMsQ0FBQyxDQUFDLENBQUM7Z0JBQ2hCLElBQUksS0FBSyxHQUFHLHFDQUFxQyxHQUFHLFNBQVMsQ0FBQztnQkFDOUQsRUFBRSxDQUFBLENBQUMsSUFBSSxDQUFDLE1BQU0sQ0FBQyxDQUFDLENBQUM7b0JBQ2IsSUFBSSxDQUFDLE1BQU0sQ0FBQyxVQUFVLENBQUMsSUFBSSxFQUFFLEtBQUssQ0FBQyxDQUFDO29CQUNwQyxJQUFJLENBQUMsTUFBTSxHQUFHLElBQUksQ0FBQztnQkFDdkIsQ0FBQztZQUNMLENBQUM7UUFDTCxDQUFDO1FBRUQsb0JBQUssR0FBTCxjQUFTLENBQUM7UUFDVixxQkFBTSxHQUFOLGNBQVUsQ0FBQztRQUNYLHlCQUFVLEdBQVYsY0FBYyxDQUFDO1FBQ2YseUJBQVUsR0FBVixjQUFjLENBQUM7UUFHbkIsV0FBQztJQUFELENBQUMsQUF4Q0QsSUF3Q0M7Ozs7O0lDOUNEO1FBQUE7UUF1QkEsQ0FBQztRQXRCVSxrQkFBRyxHQUFWO1lBQ0ksSUFBSSxJQUFJLEdBQUcsSUFBSSxjQUFJLEVBQUUsQ0FBQztZQUV0QixtQkFBbUI7WUFDbkIsSUFBSSxDQUFDLE9BQU8sQ0FBQyxXQUFXLEVBQUUsSUFBSSxFQUFFO2dCQUFJO2dCQVVwQyxDQUFDO2dCQVRHLDZCQUFXLEdBQVg7b0JBQ0ksT0FBTyxDQUFDLEdBQUcsQ0FBQyxXQUFXLENBQUMsQ0FBQztnQkFDN0IsQ0FBQztnQkFDRCxnQ0FBYyxHQUFkLFVBQWUsSUFBWSxFQUFFLE1BQWMsRUFBRSxRQUFpQjtvQkFDMUQsT0FBTyxDQUFDLEdBQUcsQ0FBQyxnQkFBZ0IsR0FBRyxJQUFJLEdBQUcsS0FBSyxHQUFHLE1BQU0sR0FBRyxLQUFLLEdBQUcsUUFBUSxDQUFDLENBQUM7Z0JBQzdFLENBQUM7Z0JBQ0QseUJBQU8sR0FBUCxVQUFRLFlBQW9CO29CQUN4QixPQUFPLENBQUMsR0FBRyxDQUFDLG9CQUFvQixHQUFHLFlBQVksQ0FBQyxDQUFDO2dCQUNyRCxDQUFDO2dCQUNMLGNBQUM7WUFBRCxDQUFDLEFBVm1DLEdBVW5DLENBQUMsQ0FBQztZQUVILDZHQUE2RztZQUU3RywrREFBK0Q7WUFFL0QseUJBQXlCO1FBQzdCLENBQUM7UUFDTCxXQUFDO0lBQUQsQ0FBQyxBQXZCRCxJQXVCQztJQUVELE9BQVMsSUFBSSxDQUFDIiwic291cmNlc0NvbnRlbnQiOlsiKFxyXG4gICAgZnVuY3Rpb24gKCkge1xyXG4gICAgICAgIHJlcXVpcmVqcy5jb25maWcoe1xyXG4gICAgICAgICAgICBcImJhc2VVcmxcIjogXCJsaWJzXCIsXHJcbiAgICAgICAgICAgIFwicGF0aHNcIjoge1xyXG4gICAgICAgICAgICAgICAgXCJqcXVlcnlcIjogXCJqcXVlcnkubWluXCJcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH0pO1xyXG5cclxuICAgICAgICByZXF1aXJlanMoW1wibWFpblwiXSwgZnVuY3Rpb24gKE1haW4pIHtcclxuICAgICAgICAgICAgbGV0IG1haW4gPSBuZXcgTWFpbigpO1xyXG4gICAgICAgICAgICBtYWluLnJ1bigpO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfSkoKTsiLCJpbnRlcmZhY2UgTWVzc2FnZSB7XHJcbiAgICBtZXNzYWdlVHlwZTogc3RyaW5nO1xyXG4gICAgbWVzc2FnZTogYW55O1xyXG59XHJcblxyXG50eXBlIE1lc3NhZ2VIYW5kbGVyID0gKG1lc3NhZ2U6IE1lc3NhZ2UpID0+IGFueTtcclxuXHJcbmV4cG9ydCBkZWZhdWx0IGNsYXNzIE1lc3NhZ2VzQ2xpZW50IHtcclxuICAgIG1lc3NhZ2VIYW5kbGVyczogeyBba2V5OiBzdHJpbmddOiBNZXNzYWdlSGFuZGxlciB9O1xyXG5cclxuICAgIHNvY2tldDogV2ViU29ja2V0IHwgbnVsbDtcclxuICAgIHNlcnZlcjogc3RyaW5nO1xyXG4gICAgcG9ydDogbnVtYmVyO1xyXG5cclxuICAgIG9uQ29ubmVjdGVkOiAoKSA9PiBhbnk7XHJcbiAgICBvbkRpc2Nvbm5lY3RlZDogKGNsb3NlOiBDbG9zZUV2ZW50KSA9PiBhbnk7XHJcblxyXG5cclxuICAgIGNvbnN0cnVjdG9yKHNlcnZlcjogc3RyaW5nLCBwb3J0OiBudW1iZXIpIHtcclxuICAgICAgICB0aGlzLnNlcnZlciA9IHNlcnZlcjtcclxuICAgICAgICB0aGlzLnBvcnQgPSBwb3J0O1xyXG4gICAgfVxyXG5cclxuICAgIHB1YmxpYyBjb25uZWN0KCkge1xyXG4gICAgICAgIGlmKHRoaXMuc29ja2V0ICE9IG51bGwpXHJcbiAgICAgICAgICAgIHRoaXMuZGlzY29ubmVjdChmYWxzZSwgXCJOZXcgY29ubmVjdFwiKTtcclxuXHJcbiAgICAgICAgdGhpcy5zb2NrZXQgPSBuZXcgV2ViU29ja2V0KFwid3M6Ly9cIiArIHRoaXMuc2VydmVyICsgXCI6XCIgKyB0aGlzLnBvcnQgKyBcIi93c1wiKTtcclxuXHJcbiAgICAgICAgdGhpcy5zb2NrZXQub25vcGVuID0gKCkgPT4ge1xyXG4gICAgICAgICAgICB0aGlzLm9uQ29ubmVjdGVkKCk7XHJcbiAgICAgICAgICAgIGlmKHRoaXMuc29ja2V0ICE9IG51bGwpXHJcbiAgICAgICAgICAgICAgICB0aGlzLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KHttZXNzYWdlSWQ6IFwidGVzdElkXCIsIG1lc3NhZ2VEYXRhOiB7ZGF0YTogXCJsb2xcIn19KSk7XHJcbiAgICAgICAgfVxyXG5cclxuICAgICAgICB0aGlzLnNvY2tldC5vbmNsb3NlID0gKGNsb3NlOiBDbG9zZUV2ZW50KSA9PiB7XHJcbiAgICAgICAgICAgIHRoaXMub25EaXNjb25uZWN0ZWQoY2xvc2UpO1xyXG4gICAgICAgIH1cclxuXHJcbiAgICAgICAgdGhpcy5zb2NrZXQub25tZXNzYWdlID0gKCkgPT4gdGhpcy5vbk1lc3NhZ2U7XHJcbiAgICB9XHJcblxyXG4gICAgcHVibGljIGRpc2Nvbm5lY3QoZHVlVG9FcnJvcjogYm9vbGVhbiwgcmVhc29uOiBzdHJpbmcpIHtcclxuICAgICAgICBpZih0aGlzLnNvY2tldCA9PSBudWxsKVxyXG4gICAgICAgICAgICByZXR1cm47XHJcblxyXG4gICAgICAgIHRoaXMuZGlzY29ubmVjdEludGVybmFsKGR1ZVRvRXJyb3IgPyAxMDAxIDogMTAwMCwgcmVhc29uKTtcclxuICAgIH1cclxuXHJcbiAgICBwcml2YXRlIGRpc2Nvbm5lY3RJbnRlcm5hbChjb2RlOiBudW1iZXIsIHJlYXNvbjogc3RyaW5nKSB7XHJcbiAgICAgICAgaWYodGhpcy5zb2NrZXQgPT0gbnVsbCkge1xyXG4gICAgICAgICAgICBjb25zb2xlLmxvZyhcIlRyeWluZyB0byBkaXNjb25uZWN0IG51bGwgc29ja2V0OiBcIiArIGNvZGUgKyBcIiB8IFwiICsgcmVhc29uKTtcclxuICAgICAgICAgICAgcmV0dXJuO1xyXG4gICAgICAgIH1cclxuXHJcbiAgICAgICAgdGhpcy5zb2NrZXQuY2xvc2UoY29kZSwgcmVhc29uKTtcclxuICAgICAgICB0aGlzLnNvY2tldCA9IG51bGw7XHJcbiAgICB9XHJcblxyXG4gICAgLyoqXHJcbiAgICAgKiBIYW5kbGUgbWVzc2FnZXMgZnJvbSB0aGUgV2ViU29ja2V0IHNlcnZlci5cclxuICAgICAqIEBwYXJhbSBtZXNzYWdlIFxyXG4gICAgICovXHJcbiAgICBwcml2YXRlIG9uTWVzc2FnZShtZXNzYWdlOiBNZXNzYWdlRXZlbnQpIHtcclxuICAgICAgICAgICAgaWYoIW1lc3NhZ2UuZGF0YSkge1xyXG4gICAgICAgICAgICAgICAgY29uc29sZS5sb2coXCJHb3Qgbm8gZGF0YSBpbiBtZXNzYWdlOiBcIiArIG1lc3NhZ2UpO1xyXG4gICAgICAgICAgICAgICAgcmV0dXJuO1xyXG4gICAgICAgICAgICB9XHJcblxyXG4gICAgICAgICAgICAvL1BhcnNlIHRoZSBtZXNzYWdlIGFuZCBjYWxsIHRoZSByZWxldmFudCBoYW5kbGVyXHJcbiAgICAgICAgICAgIHRyeSB7XHJcbiAgICAgICAgICAgICAgICBsZXQgbXNnOiBNZXNzYWdlID0gSlNPTi5wYXJzZShtZXNzYWdlLmRhdGEpO1xyXG4gICAgICAgICAgICAgICAgaWYoIW1zZy5tZXNzYWdlVHlwZSlcclxuICAgICAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoXCJNZXNzYWdlIGlzIG1pc3NpbmcgZXhwZWN0ZWQgZGF0YTogXCIgKyBtc2cpO1xyXG5cclxuICAgICAgICAgICAgICAgIGxldCBoYW5kbGVyID0gdGhpcy5tZXNzYWdlSGFuZGxlcnNbbXNnLm1lc3NhZ2VUeXBlXTtcclxuICAgICAgICAgICAgICAgIGlmKGhhbmRsZXIpXHJcbiAgICAgICAgICAgICAgICAgICAgaGFuZGxlcihtc2cpO1xyXG4gICAgICAgICAgICAgICAgZWxzZVxyXG4gICAgICAgICAgICAgICAgICAgIHRoaXMub25NZXNzYWdlTWlzc2luZ0hhbmRsZXIobXNnLm1lc3NhZ2VUeXBlKTtcclxuICAgICAgICAgICAgfSBjYXRjaChlKSB7XHJcbiAgICAgICAgICAgICAgICB0aGlzLm9uUGFyc2VNZXNzYWdlRmFpbGVkKGUpO1xyXG4gICAgICAgICAgICB9XHJcbiAgICB9XHJcblxyXG4gICAgLyoqXHJcbiAgICAgKiBDYWxsZWQgd2hlbiBwYXJzaW5nIGEgbWVzc2FnZSBmYWlscywgZWl0aGVyIGR1ZSB0byBjb3JydXB0ZWQgZGF0YSwgb3IgdW5leHBlY3RlZCBwcm90b2NvbC5cclxuICAgICAqIEBwYXJhbSBlcnJvciBUaGUgZXJyb3IgbWlnaHQgYmUgYW4gZXhjZXB0aW9uLCBvciBhbiBlcnJvciBtZXNzYWdlLlxyXG4gICAgICovXHJcbiAgICBvblBhcnNlTWVzc2FnZUZhaWxlZChlcnJvcjogYW55KSB7XHJcbiAgICAgICAgY29uc29sZS5sb2coXCJGYWlsZWQgdG8gcGFyc2UgbWVzc2FnZSwgY2xvc2luZyBjb25uZWN0aW9uLiBFeGNlcHRpb246IFwiICsgZXJyb3IpO1xyXG4gICAgICAgIHRoaXMuZGlzY29ubmVjdEludGVybmFsKDEwMDIsIGVycm9yKTtcclxuICAgIH1cclxuXHJcbiAgICAvKipcclxuICAgICAqIENhbGxlZCB3aGVuIHJlY2VpdmluZyBhIG1lc3NhZ2Ugd2l0aCBhIG1lc3NhZ2UgdHlwZSB3aGljaCB3ZSBoYXZlIG5vIGhhbmRsZXIgZm9yLlxyXG4gICAgICogQHBhcmFtIG1lc3NhZ2VUeXBlXHJcbiAgICAgKi9cclxuICAgIG9uTWVzc2FnZU1pc3NpbmdIYW5kbGVyKG1lc3NhZ2VUeXBlOiBzdHJpbmcpIHtcclxuICAgICAgICBsZXQgZXJyb3IgPSBcIkdvdCBtZXNzYWdlIHdpdGhvdXQgaGFuZGxlcjogXCIgKyBtZXNzYWdlVHlwZTtcclxuICAgICAgICBjb25zb2xlLmxvZyhlcnJvcik7XHJcbiAgICAgICAgdGhpcy5kaXNjb25uZWN0SW50ZXJuYWwoMTAwMywgZXJyb3IpO1xyXG4gICAgfVxyXG5cclxuICAgIC8qKlxyXG4gICAgICogU2V0IHRoZSBoYW5kbGVyIGZvciBhIGdpdmVuIG1lc3NhZ2UgdHlwZS5cclxuICAgICAqIEBwYXJhbSBtZXNzYWdlVHlwZSBcclxuICAgICAqIEBwYXJhbSBoYW5kbGVyIFxyXG4gICAgICovXHJcbiAgICBzZXRIYW5kbGVyKG1lc3NhZ2VUeXBlOiBzdHJpbmcsIGhhbmRsZXI6IE1lc3NhZ2VIYW5kbGVyKSB7XHJcbiAgICAgICAgdGhpcy5tZXNzYWdlSGFuZGxlcnNbbWVzc2FnZVR5cGVdID0gaGFuZGxlcjtcclxuICAgIH1cclxufSIsImltcG9ydCBNZXNzYWdlc0NsaWVudCBmcm9tIFwiLi9tZXNzYWdlc2NsaWVudFwiO1xyXG5cclxuaW50ZXJmYWNlIENvbm5lY3Rpb25IYW5kbGVyIHtcclxuICAgIG9uQ29ubmVjdGVkKCk7XHJcbiAgICBvbkRpc2Nvbm5lY3RlZChjb2RlOiBudW1iZXIsIHJlYXNvbjogc3RyaW5nLCB3YXNDbGVhbjogYm9vbGVhbik7XHJcbiAgICBvbkVycm9yKGVycm9yTWVzc2FnZTogc3RyaW5nKTtcclxufVxyXG5cclxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgQ29yZSB7XHJcbiAgICBjbGllbnQ6IE1lc3NhZ2VzQ2xpZW50IHwgbnVsbDtcclxuXHJcbiAgICBjb25zdHJ1Y3RvcigpIHtcclxuICAgICAgICBjb25zb2xlLmxvZyhcIkxvYWRlZCBNb2R1bGVDb3JlXCIpO1xyXG4gICAgfVxyXG5cclxuICAgIGNvbm5lY3Qoc2VydmVyOiBzdHJpbmcsIHBvcnQ6IG51bWJlciwgY29ubmVjdGlvbkhhbmRsZXI6IENvbm5lY3Rpb25IYW5kbGVyKSB7XHJcbiAgICAgICAgLy9UT0RPOiBJZiBhbHJlYWR5IGNvbm5lY3RlZCwgZGlzY29ubmVjdCBhbmQgdW5sb2FkIGFsbCBsb2FkZWQgTW9kdWxlcyhPciByZXRhaW4gdGhlbSB1bnRpbCByZWNvbm5lY3QgYW5kIGRvIHBlcm1pc3Npb24gY2hlY2s/KVxyXG4gICAgICAgIGlmKHRoaXMuY2xpZW50KSB7XHJcbiAgICAgICAgICAgIHRoaXMuY2xpZW50LmRpc2Nvbm5lY3QoZmFsc2UsIFwiTmV3IGNvbm5lY3Rpb25cIik7XHJcbiAgICAgICAgICAgIHRoaXMuY2xpZW50ID0gbnVsbDtcclxuICAgICAgICB9XHJcblxyXG4gICAgICAgIGNvbnNvbGUubG9nKFwiQ29ubmVjdGluZyB0byBcIiArIHNlcnZlciArIFwiOlwiICsgcG9ydCk7XHJcbiAgICAgICAgdGhpcy5jbGllbnQgPSBuZXcgTWVzc2FnZXNDbGllbnQoc2VydmVyLCBwb3J0KTtcclxuICAgICAgICB0aGlzLmNsaWVudC5vbkNvbm5lY3RlZCA9IGZ1bmN0aW9uICgpIHtcclxuICAgICAgICAgICAgY29ubmVjdGlvbkhhbmRsZXIub25Db25uZWN0ZWQoKTtcclxuICAgICAgICB9O1xyXG4gICAgICAgIHRoaXMuY2xpZW50Lm9uRGlzY29ubmVjdGVkID0gZnVuY3Rpb24gKGNsb3NlOiBDbG9zZUV2ZW50KSB7XHJcbiAgICAgICAgICAgIGNvbm5lY3Rpb25IYW5kbGVyLm9uRGlzY29ubmVjdGVkKGNsb3NlLmNvZGUsIGNsb3NlLnJlYXNvbiwgY2xvc2Uud2FzQ2xlYW4pO1xyXG4gICAgICAgIH07XHJcblxyXG4gICAgICAgIHRyeSB7XHJcbiAgICAgICAgICAgIHRoaXMuY2xpZW50LmNvbm5lY3QoKTtcclxuICAgICAgICB9IGNhdGNoKGV4Y2VwdGlvbikge1xyXG4gICAgICAgICAgICBsZXQgZXJyb3IgPSBcIkVycm9yIHdoaWxlIHNldHRpbmcgdXAgY29ubmVjdGlvbjogXCIgKyBleGNlcHRpb247XHJcbiAgICAgICAgICAgIGlmKHRoaXMuY2xpZW50KSB7XHJcbiAgICAgICAgICAgICAgICB0aGlzLmNsaWVudC5kaXNjb25uZWN0KHRydWUsIGVycm9yKTtcclxuICAgICAgICAgICAgICAgIHRoaXMuY2xpZW50ID0gbnVsbDtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH1cclxuICAgIH1cclxuXHJcbiAgICBsb2dpbigpIHt9XHJcbiAgICBsb2dvdXQoKSB7fVxyXG4gICAgZ2V0TW9kdWxlcygpIHt9XHJcbiAgICBsb2FkTW9kdWxlKCkge31cclxuXHJcblxyXG59IiwiaW1wb3J0IENvcmUgZnJvbSBcIi4vY29yZVwiO1xyXG5cclxuY2xhc3MgTWFpbiB7XHJcbiAgICBwdWJsaWMgcnVuKCkge1xyXG4gICAgICAgIGxldCBjb3JlID0gbmV3IENvcmUoKTtcclxuXHJcbiAgICAgICAgLy9Db25uZWN0IHRvIHNlcnZlclxyXG4gICAgICAgIGNvcmUuY29ubmVjdChcImxvY2FsaG9zdFwiLCA4MDgwLCBuZXcgY2xhc3Mge1xyXG4gICAgICAgICAgICBvbkNvbm5lY3RlZCgpIHtcclxuICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiQ29ubmVjdGVkXCIpO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIG9uRGlzY29ubmVjdGVkKGNvZGU6IG51bWJlciwgcmVhc29uOiBzdHJpbmcsIHdhc0NsZWFuOiBib29sZWFuKSB7XHJcbiAgICAgICAgICAgICAgICBjb25zb2xlLmxvZyhcIkRpc2Nvbm5lY3RlZDogXCIgKyBjb2RlICsgXCIgfCBcIiArIHJlYXNvbiArIFwiIHwgXCIgKyB3YXNDbGVhbik7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgb25FcnJvcihlcnJvck1lc3NhZ2U6IHN0cmluZykge1xyXG4gICAgICAgICAgICAgICAgY29uc29sZS5sb2coXCJDb25uZWN0aW9uIEVycm9yOiBcIiArIGVycm9yTWVzc2FnZSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9KTtcclxuICAgICAgICBcclxuICAgICAgICAvL1N0YXJ0IHNlc3Npb24gd2l0aCB0aGUgTW9kdWxlIE1hbmFnZXIsIGFuZCBnZXQgYWN0aXZlIG1vZHVsZXMgZnJvbSBzZXJ2ZXIoV2hpY2ggd2UgaGF2ZSBwZXJtaXNzaW9uIHRvIGxvYWQpXHJcblxyXG4gICAgICAgIC8vV2FpdCBmb3IgdXNlciBzZXR0aW5ncyBmcm9tIHNlcnZlcihJbiByZXNwb25zZSB0byBjb25uZWN0aW5nKVxyXG5cclxuICAgICAgICAvL0xvYWQgYWxsIGFjdGl2ZSBtb2R1bGVzXHJcbiAgICB9XHJcbn1cclxuXHJcbmV4cG9ydCA9IE1haW47Il19