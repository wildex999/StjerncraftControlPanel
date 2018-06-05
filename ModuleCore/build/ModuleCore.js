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
define("message", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
});
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
                if (!msg.messageId)
                    throw new Error("Message is missing messageId: " + msg);
                var handler = this.messageHandlers[msg.messageId];
                if (handler)
                    handler(msg);
                else
                    this.onMessageMissingHandler(msg.messageId);
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
         * Set the handler for a given message.
         * @param messageId The string identifier for the message type
         * @param handler
         */
        MessagesClient.prototype.setHandler = function (messageId, handler) {
            this.messageHandlers[messageId] = handler;
        };
        MessagesClient.prototype.sendMessage = function (message) {
            if (this.socket == null)
                return;
            this.socket.send(JSON.stringify(message));
        };
        return MessagesClient;
    }());
    exports.default = MessagesClient;
});
define("messages/MessageVersion", ["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var MessageVersion = (function () {
        function MessageVersion(version) {
            this.messageId = "Version";
            this.messageData = {
                version: version
            };
        }
        return MessageVersion;
    }());
    exports.default = MessageVersion;
});
define("core", ["require", "exports", "messagesclient", "messages/MessageVersion"], function (require, exports, messagesclient_1, MessageVersion_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var CORE_VERSION = 1; //Should match the Server Core version(TODO: Auto generate?)
    var Core = (function () {
        function Core() {
            console.log("Loaded ModuleCore");
        }
        Core.prototype.connect = function (server, port, connectionHandler) {
            var _this = this;
            //TODO: If already connected, disconnect and unload all loaded Modules(Or retain them until reconnect and do permission check?)
            if (this.client) {
                this.client.disconnect(false, "New connection");
                this.client = null;
            }
            console.log("Connecting to " + server + ":" + port);
            this.client = new messagesclient_1.default(server, port);
            this.client.onConnected = function () {
                if (_this.client == null)
                    return;
                //Send our Core Client version to the server, allowing it to verify we are compatible
                _this.client.sendMessage(new MessageVersion_1.default(CORE_VERSION));
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
                    //Get for list of Service APIs
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTW9kdWxlQ29yZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uL3NyYy90cy9jb25maWcudHMiLCIuLi9zcmMvdHMvbWVzc2FnZS50cyIsIi4uL3NyYy90cy9tZXNzYWdlc2NsaWVudC50cyIsIi4uL3NyYy90cy9tZXNzYWdlcy9NZXNzYWdlVmVyc2lvbi50cyIsIi4uL3NyYy90cy9jb3JlLnRzIiwiLi4vc3JjL3RzL21haW4udHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBLENBQ0k7SUFDSSxTQUFTLENBQUMsTUFBTSxDQUFDO1FBQ2IsU0FBUyxFQUFFLE1BQU07UUFDakIsT0FBTyxFQUFFO1lBQ0wsUUFBUSxFQUFFLFlBQVk7U0FDekI7S0FDSixDQUFDLENBQUM7SUFFSCxTQUFTLENBQUMsQ0FBQyxNQUFNLENBQUMsRUFBRSxVQUFVLElBQUk7UUFDOUIsSUFBSSxJQUFJLEdBQUcsSUFBSSxJQUFJLEVBQUUsQ0FBQztRQUN0QixJQUFJLENBQUMsR0FBRyxFQUFFLENBQUM7SUFDZixDQUFDLENBQUMsQ0FBQztBQUNQLENBQUMsQ0FBQyxFQUFFLENBQUM7Ozs7Ozs7O0lFVFQ7UUFXSSx3QkFBWSxNQUFjLEVBQUUsSUFBWTtZQUNwQyxJQUFJLENBQUMsTUFBTSxHQUFHLE1BQU0sQ0FBQztZQUNyQixJQUFJLENBQUMsSUFBSSxHQUFHLElBQUksQ0FBQztRQUNyQixDQUFDO1FBRU0sZ0NBQU8sR0FBZDtZQUFBLGlCQWVDO1lBZEcsRUFBRSxDQUFBLENBQUMsSUFBSSxDQUFDLE1BQU0sSUFBSSxJQUFJLENBQUM7Z0JBQ25CLElBQUksQ0FBQyxVQUFVLENBQUMsS0FBSyxFQUFFLGFBQWEsQ0FBQyxDQUFDO1lBRTFDLElBQUksQ0FBQyxNQUFNLEdBQUcsSUFBSSxTQUFTLENBQUMsT0FBTyxHQUFHLElBQUksQ0FBQyxNQUFNLEdBQUcsR0FBRyxHQUFHLElBQUksQ0FBQyxJQUFJLEdBQUcsS0FBSyxDQUFDLENBQUM7WUFFN0UsSUFBSSxDQUFDLE1BQU0sQ0FBQyxNQUFNLEdBQUc7Z0JBQ2pCLEtBQUksQ0FBQyxXQUFXLEVBQUUsQ0FBQztZQUN2QixDQUFDLENBQUM7WUFFRixJQUFJLENBQUMsTUFBTSxDQUFDLE9BQU8sR0FBRyxVQUFDLEtBQWlCO2dCQUNwQyxLQUFJLENBQUMsY0FBYyxDQUFDLEtBQUssQ0FBQyxDQUFDO1lBQy9CLENBQUMsQ0FBQztZQUVGLElBQUksQ0FBQyxNQUFNLENBQUMsU0FBUyxHQUFHLGNBQU0sT0FBQSxLQUFJLENBQUMsU0FBUyxFQUFkLENBQWMsQ0FBQztRQUNqRCxDQUFDO1FBRU0sbUNBQVUsR0FBakIsVUFBa0IsVUFBbUIsRUFBRSxNQUFjO1lBQ2pELEVBQUUsQ0FBQSxDQUFDLElBQUksQ0FBQyxNQUFNLElBQUksSUFBSSxDQUFDO2dCQUNuQixNQUFNLENBQUM7WUFFWCxJQUFJLENBQUMsa0JBQWtCLENBQUMsVUFBVSxHQUFHLElBQUksR0FBRyxJQUFJLEVBQUUsTUFBTSxDQUFDLENBQUM7UUFDOUQsQ0FBQztRQUVPLDJDQUFrQixHQUExQixVQUEyQixJQUFZLEVBQUUsTUFBYztZQUNuRCxFQUFFLENBQUEsQ0FBQyxJQUFJLENBQUMsTUFBTSxJQUFJLElBQUksQ0FBQyxDQUFDLENBQUM7Z0JBQ3JCLE9BQU8sQ0FBQyxHQUFHLENBQUMsb0NBQW9DLEdBQUcsSUFBSSxHQUFHLEtBQUssR0FBRyxNQUFNLENBQUMsQ0FBQztnQkFDMUUsTUFBTSxDQUFDO1lBQ1gsQ0FBQztZQUVELElBQUksQ0FBQyxNQUFNLENBQUMsS0FBSyxDQUFDLElBQUksRUFBRSxNQUFNLENBQUMsQ0FBQztZQUNoQyxJQUFJLENBQUMsTUFBTSxHQUFHLElBQUksQ0FBQztRQUN2QixDQUFDO1FBRUQ7OztXQUdHO1FBQ0ssa0NBQVMsR0FBakIsVUFBa0IsT0FBcUI7WUFDL0IsRUFBRSxDQUFBLENBQUMsQ0FBQyxPQUFPLENBQUMsSUFBSSxDQUFDLENBQUMsQ0FBQztnQkFDZixPQUFPLENBQUMsR0FBRyxDQUFDLDBCQUEwQixHQUFHLE9BQU8sQ0FBQyxDQUFDO2dCQUNsRCxNQUFNLENBQUM7WUFDWCxDQUFDO1lBRUQsaURBQWlEO1lBQ2pELElBQUksQ0FBQztnQkFDRCxJQUFJLEdBQUcsR0FBWSxJQUFJLENBQUMsS0FBSyxDQUFDLE9BQU8sQ0FBQyxJQUFJLENBQUMsQ0FBQztnQkFDNUMsRUFBRSxDQUFBLENBQUMsQ0FBQyxHQUFHLENBQUMsU0FBUyxDQUFDO29CQUNkLE1BQU0sSUFBSSxLQUFLLENBQUMsZ0NBQWdDLEdBQUcsR0FBRyxDQUFDLENBQUM7Z0JBRTVELElBQUksT0FBTyxHQUFHLElBQUksQ0FBQyxlQUFlLENBQUMsR0FBRyxDQUFDLFNBQVMsQ0FBQyxDQUFDO2dCQUNsRCxFQUFFLENBQUEsQ0FBQyxPQUFPLENBQUM7b0JBQ1AsT0FBTyxDQUFDLEdBQUcsQ0FBQyxDQUFDO2dCQUNqQixJQUFJO29CQUNBLElBQUksQ0FBQyx1QkFBdUIsQ0FBQyxHQUFHLENBQUMsU0FBUyxDQUFDLENBQUM7WUFDcEQsQ0FBQztZQUFDLEtBQUssQ0FBQSxDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUM7Z0JBQ1IsSUFBSSxDQUFDLG9CQUFvQixDQUFDLENBQUMsQ0FBQyxDQUFDO1lBQ2pDLENBQUM7UUFDVCxDQUFDO1FBRUQ7OztXQUdHO1FBQ0gsNkNBQW9CLEdBQXBCLFVBQXFCLEtBQVU7WUFDM0IsT0FBTyxDQUFDLEdBQUcsQ0FBQywwREFBMEQsR0FBRyxLQUFLLENBQUMsQ0FBQztZQUNoRixJQUFJLENBQUMsa0JBQWtCLENBQUMsSUFBSSxFQUFFLEtBQUssQ0FBQyxDQUFDO1FBQ3pDLENBQUM7UUFFRDs7O1dBR0c7UUFDSCxnREFBdUIsR0FBdkIsVUFBd0IsV0FBbUI7WUFDdkMsSUFBSSxLQUFLLEdBQUcsK0JBQStCLEdBQUcsV0FBVyxDQUFDO1lBQzFELE9BQU8sQ0FBQyxHQUFHLENBQUMsS0FBSyxDQUFDLENBQUM7WUFDbkIsSUFBSSxDQUFDLGtCQUFrQixDQUFDLElBQUksRUFBRSxLQUFLLENBQUMsQ0FBQztRQUN6QyxDQUFDO1FBRUQ7Ozs7V0FJRztRQUNILG1DQUFVLEdBQVYsVUFBVyxTQUFpQixFQUFFLE9BQXVCO1lBQ2pELElBQUksQ0FBQyxlQUFlLENBQUMsU0FBUyxDQUFDLEdBQUcsT0FBTyxDQUFDO1FBQzlDLENBQUM7UUFFRCxvQ0FBVyxHQUFYLFVBQVksT0FBZ0I7WUFDeEIsRUFBRSxDQUFBLENBQUMsSUFBSSxDQUFDLE1BQU0sSUFBSSxJQUFJLENBQUM7Z0JBQ25CLE1BQU0sQ0FBQztZQUVYLElBQUksQ0FBQyxNQUFNLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxTQUFTLENBQUMsT0FBTyxDQUFDLENBQUMsQ0FBQztRQUM5QyxDQUFDO1FBQ0wscUJBQUM7SUFBRCxDQUFDLEFBOUdELElBOEdDOzs7Ozs7SUNoSEQ7UUFNSSx3QkFBWSxPQUFlO1lBTDNCLGNBQVMsR0FBVyxTQUFTLENBQUM7WUFNMUIsSUFBSSxDQUFDLFdBQVcsR0FBRztnQkFDZixPQUFPLEVBQUUsT0FBTzthQUNuQixDQUFBO1FBQ0wsQ0FBQztRQUNMLHFCQUFDO0lBQUQsQ0FBQyxBQVhELElBV0M7Ozs7OztJQ1ZELElBQUksWUFBWSxHQUFHLENBQUMsQ0FBQyxDQUFDLDREQUE0RDtJQVFsRjtRQUdJO1lBQ0ksT0FBTyxDQUFDLEdBQUcsQ0FBQyxtQkFBbUIsQ0FBQyxDQUFDO1FBQ3JDLENBQUM7UUFFRCxzQkFBTyxHQUFQLFVBQVEsTUFBYyxFQUFFLElBQVksRUFBRSxpQkFBb0M7WUFBMUUsaUJBOEJDO1lBN0JHLCtIQUErSDtZQUMvSCxFQUFFLENBQUEsQ0FBQyxJQUFJLENBQUMsTUFBTSxDQUFDLENBQUMsQ0FBQztnQkFDYixJQUFJLENBQUMsTUFBTSxDQUFDLFVBQVUsQ0FBQyxLQUFLLEVBQUUsZ0JBQWdCLENBQUMsQ0FBQztnQkFDaEQsSUFBSSxDQUFDLE1BQU0sR0FBRyxJQUFJLENBQUM7WUFDdkIsQ0FBQztZQUVELE9BQU8sQ0FBQyxHQUFHLENBQUMsZ0JBQWdCLEdBQUcsTUFBTSxHQUFHLEdBQUcsR0FBRyxJQUFJLENBQUMsQ0FBQztZQUNwRCxJQUFJLENBQUMsTUFBTSxHQUFHLElBQUksd0JBQWMsQ0FBQyxNQUFNLEVBQUUsSUFBSSxDQUFDLENBQUM7WUFDL0MsSUFBSSxDQUFDLE1BQU0sQ0FBQyxXQUFXLEdBQUc7Z0JBQ3RCLEVBQUUsQ0FBQSxDQUFDLEtBQUksQ0FBQyxNQUFNLElBQUksSUFBSSxDQUFDO29CQUNuQixNQUFNLENBQUM7Z0JBRVgscUZBQXFGO2dCQUNyRixLQUFJLENBQUMsTUFBTSxDQUFDLFdBQVcsQ0FBQyxJQUFJLHdCQUFjLENBQUMsWUFBWSxDQUFDLENBQUMsQ0FBQztnQkFDMUQsaUJBQWlCLENBQUMsV0FBVyxFQUFFLENBQUM7WUFDcEMsQ0FBQyxDQUFDO1lBQ0YsSUFBSSxDQUFDLE1BQU0sQ0FBQyxjQUFjLEdBQUcsVUFBVSxLQUFpQjtnQkFDcEQsaUJBQWlCLENBQUMsY0FBYyxDQUFDLEtBQUssQ0FBQyxJQUFJLEVBQUUsS0FBSyxDQUFDLE1BQU0sRUFBRSxLQUFLLENBQUMsUUFBUSxDQUFDLENBQUM7WUFDL0UsQ0FBQyxDQUFDO1lBRUYsSUFBSSxDQUFDO2dCQUNELElBQUksQ0FBQyxNQUFNLENBQUMsT0FBTyxFQUFFLENBQUM7WUFDMUIsQ0FBQztZQUFDLEtBQUssQ0FBQSxDQUFDLFNBQVMsQ0FBQyxDQUFDLENBQUM7Z0JBQ2hCLElBQUksS0FBSyxHQUFHLHFDQUFxQyxHQUFHLFNBQVMsQ0FBQztnQkFDOUQsRUFBRSxDQUFBLENBQUMsSUFBSSxDQUFDLE1BQU0sQ0FBQyxDQUFDLENBQUM7b0JBQ2IsSUFBSSxDQUFDLE1BQU0sQ0FBQyxVQUFVLENBQUMsSUFBSSxFQUFFLEtBQUssQ0FBQyxDQUFDO29CQUNwQyxJQUFJLENBQUMsTUFBTSxHQUFHLElBQUksQ0FBQztnQkFDdkIsQ0FBQztZQUNMLENBQUM7UUFDTCxDQUFDO1FBRUQsb0JBQUssR0FBTCxjQUFTLENBQUM7UUFDVixxQkFBTSxHQUFOLGNBQVUsQ0FBQztRQUNYLHlCQUFVLEdBQVYsY0FBYyxDQUFDO1FBQ2YseUJBQVUsR0FBVixjQUFjLENBQUM7UUFHbkIsV0FBQztJQUFELENBQUMsQUE3Q0QsSUE2Q0M7Ozs7O0lDdEREO1FBQUE7UUEwQkEsQ0FBQztRQXpCVSxrQkFBRyxHQUFWO1lBQ0ksSUFBSSxJQUFJLEdBQUcsSUFBSSxjQUFJLEVBQUUsQ0FBQztZQUV0QixtQkFBbUI7WUFDbkIsSUFBSSxDQUFDLE9BQU8sQ0FBQyxXQUFXLEVBQUUsSUFBSSxFQUFFO2dCQUFJO2dCQWFwQyxDQUFDO2dCQVpHLDZCQUFXLEdBQVg7b0JBQ0ksT0FBTyxDQUFDLEdBQUcsQ0FBQyxXQUFXLENBQUMsQ0FBQztvQkFFekIsOEJBQThCO2dCQUVsQyxDQUFDO2dCQUNELGdDQUFjLEdBQWQsVUFBZSxJQUFZLEVBQUUsTUFBYyxFQUFFLFFBQWlCO29CQUMxRCxPQUFPLENBQUMsR0FBRyxDQUFDLGdCQUFnQixHQUFHLElBQUksR0FBRyxLQUFLLEdBQUcsTUFBTSxHQUFHLEtBQUssR0FBRyxRQUFRLENBQUMsQ0FBQztnQkFDN0UsQ0FBQztnQkFDRCx5QkFBTyxHQUFQLFVBQVEsWUFBb0I7b0JBQ3hCLE9BQU8sQ0FBQyxHQUFHLENBQUMsb0JBQW9CLEdBQUcsWUFBWSxDQUFDLENBQUM7Z0JBQ3JELENBQUM7Z0JBQ0wsY0FBQztZQUFELENBQUMsQUFibUMsR0FhbkMsQ0FBQyxDQUFDO1lBRUgsNkdBQTZHO1lBRTdHLCtEQUErRDtZQUUvRCx5QkFBeUI7UUFDN0IsQ0FBQztRQUNMLFdBQUM7SUFBRCxDQUFDLEFBMUJELElBMEJDO0lBRUQsT0FBUyxJQUFJLENBQUMiLCJzb3VyY2VzQ29udGVudCI6WyIoXHJcbiAgICBmdW5jdGlvbiAoKSB7XHJcbiAgICAgICAgcmVxdWlyZWpzLmNvbmZpZyh7XHJcbiAgICAgICAgICAgIFwiYmFzZVVybFwiOiBcImxpYnNcIixcclxuICAgICAgICAgICAgXCJwYXRoc1wiOiB7XHJcbiAgICAgICAgICAgICAgICBcImpxdWVyeVwiOiBcImpxdWVyeS5taW5cIlxyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfSk7XHJcblxyXG4gICAgICAgIHJlcXVpcmVqcyhbXCJtYWluXCJdLCBmdW5jdGlvbiAoTWFpbikge1xyXG4gICAgICAgICAgICBsZXQgbWFpbiA9IG5ldyBNYWluKCk7XHJcbiAgICAgICAgICAgIG1haW4ucnVuKCk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9KSgpOyIsImV4cG9ydCBpbnRlcmZhY2UgTWVzc2FnZSB7XHJcbiAgICBtZXNzYWdlSWQ6IHN0cmluZztcclxuICAgIG1lc3NhZ2VEYXRhOiBhbnk7XHJcbn0iLCJpbXBvcnQgeyBNZXNzYWdlIH0gZnJvbSBcIi4vbWVzc2FnZVwiXHJcblxyXG50eXBlIE1lc3NhZ2VIYW5kbGVyID0gKG1lc3NhZ2U6IE1lc3NhZ2UpID0+IGFueTtcclxuXHJcbmV4cG9ydCBkZWZhdWx0IGNsYXNzIE1lc3NhZ2VzQ2xpZW50IHtcclxuICAgIG1lc3NhZ2VIYW5kbGVyczogeyBba2V5OiBzdHJpbmddOiBNZXNzYWdlSGFuZGxlciB9O1xyXG5cclxuICAgIHNvY2tldDogV2ViU29ja2V0IHwgbnVsbDtcclxuICAgIHNlcnZlcjogc3RyaW5nO1xyXG4gICAgcG9ydDogbnVtYmVyO1xyXG5cclxuICAgIG9uQ29ubmVjdGVkOiAoKSA9PiBhbnk7XHJcbiAgICBvbkRpc2Nvbm5lY3RlZDogKGNsb3NlOiBDbG9zZUV2ZW50KSA9PiBhbnk7XHJcblxyXG5cclxuICAgIGNvbnN0cnVjdG9yKHNlcnZlcjogc3RyaW5nLCBwb3J0OiBudW1iZXIpIHtcclxuICAgICAgICB0aGlzLnNlcnZlciA9IHNlcnZlcjtcclxuICAgICAgICB0aGlzLnBvcnQgPSBwb3J0O1xyXG4gICAgfVxyXG5cclxuICAgIHB1YmxpYyBjb25uZWN0KCkge1xyXG4gICAgICAgIGlmKHRoaXMuc29ja2V0ICE9IG51bGwpXHJcbiAgICAgICAgICAgIHRoaXMuZGlzY29ubmVjdChmYWxzZSwgXCJOZXcgY29ubmVjdFwiKTtcclxuXHJcbiAgICAgICAgdGhpcy5zb2NrZXQgPSBuZXcgV2ViU29ja2V0KFwid3M6Ly9cIiArIHRoaXMuc2VydmVyICsgXCI6XCIgKyB0aGlzLnBvcnQgKyBcIi93c1wiKTtcclxuXHJcbiAgICAgICAgdGhpcy5zb2NrZXQub25vcGVuID0gKCkgPT4ge1xyXG4gICAgICAgICAgICB0aGlzLm9uQ29ubmVjdGVkKCk7XHJcbiAgICAgICAgfTtcclxuXHJcbiAgICAgICAgdGhpcy5zb2NrZXQub25jbG9zZSA9IChjbG9zZTogQ2xvc2VFdmVudCkgPT4ge1xyXG4gICAgICAgICAgICB0aGlzLm9uRGlzY29ubmVjdGVkKGNsb3NlKTtcclxuICAgICAgICB9O1xyXG5cclxuICAgICAgICB0aGlzLnNvY2tldC5vbm1lc3NhZ2UgPSAoKSA9PiB0aGlzLm9uTWVzc2FnZTtcclxuICAgIH1cclxuXHJcbiAgICBwdWJsaWMgZGlzY29ubmVjdChkdWVUb0Vycm9yOiBib29sZWFuLCByZWFzb246IHN0cmluZykge1xyXG4gICAgICAgIGlmKHRoaXMuc29ja2V0ID09IG51bGwpXHJcbiAgICAgICAgICAgIHJldHVybjtcclxuXHJcbiAgICAgICAgdGhpcy5kaXNjb25uZWN0SW50ZXJuYWwoZHVlVG9FcnJvciA/IDEwMDEgOiAxMDAwLCByZWFzb24pO1xyXG4gICAgfVxyXG5cclxuICAgIHByaXZhdGUgZGlzY29ubmVjdEludGVybmFsKGNvZGU6IG51bWJlciwgcmVhc29uOiBzdHJpbmcpIHtcclxuICAgICAgICBpZih0aGlzLnNvY2tldCA9PSBudWxsKSB7XHJcbiAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiVHJ5aW5nIHRvIGRpc2Nvbm5lY3QgbnVsbCBzb2NrZXQ6IFwiICsgY29kZSArIFwiIHwgXCIgKyByZWFzb24pO1xyXG4gICAgICAgICAgICByZXR1cm47XHJcbiAgICAgICAgfVxyXG5cclxuICAgICAgICB0aGlzLnNvY2tldC5jbG9zZShjb2RlLCByZWFzb24pO1xyXG4gICAgICAgIHRoaXMuc29ja2V0ID0gbnVsbDtcclxuICAgIH1cclxuXHJcbiAgICAvKipcclxuICAgICAqIEhhbmRsZSBtZXNzYWdlcyBmcm9tIHRoZSBXZWJTb2NrZXQgc2VydmVyLlxyXG4gICAgICogQHBhcmFtIG1lc3NhZ2UgXHJcbiAgICAgKi9cclxuICAgIHByaXZhdGUgb25NZXNzYWdlKG1lc3NhZ2U6IE1lc3NhZ2VFdmVudCkge1xyXG4gICAgICAgICAgICBpZighbWVzc2FnZS5kYXRhKSB7XHJcbiAgICAgICAgICAgICAgICBjb25zb2xlLmxvZyhcIkdvdCBubyBkYXRhIGluIG1lc3NhZ2U6IFwiICsgbWVzc2FnZSk7XHJcbiAgICAgICAgICAgICAgICByZXR1cm47XHJcbiAgICAgICAgICAgIH1cclxuXHJcbiAgICAgICAgICAgIC8vUGFyc2UgdGhlIG1lc3NhZ2UgYW5kIGNhbGwgdGhlIHJlbGV2YW50IGhhbmRsZXJcclxuICAgICAgICAgICAgdHJ5IHtcclxuICAgICAgICAgICAgICAgIGxldCBtc2c6IE1lc3NhZ2UgPSBKU09OLnBhcnNlKG1lc3NhZ2UuZGF0YSk7XHJcbiAgICAgICAgICAgICAgICBpZighbXNnLm1lc3NhZ2VJZClcclxuICAgICAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoXCJNZXNzYWdlIGlzIG1pc3NpbmcgbWVzc2FnZUlkOiBcIiArIG1zZyk7XHJcblxyXG4gICAgICAgICAgICAgICAgbGV0IGhhbmRsZXIgPSB0aGlzLm1lc3NhZ2VIYW5kbGVyc1ttc2cubWVzc2FnZUlkXTtcclxuICAgICAgICAgICAgICAgIGlmKGhhbmRsZXIpXHJcbiAgICAgICAgICAgICAgICAgICAgaGFuZGxlcihtc2cpO1xyXG4gICAgICAgICAgICAgICAgZWxzZVxyXG4gICAgICAgICAgICAgICAgICAgIHRoaXMub25NZXNzYWdlTWlzc2luZ0hhbmRsZXIobXNnLm1lc3NhZ2VJZCk7XHJcbiAgICAgICAgICAgIH0gY2F0Y2goZSkge1xyXG4gICAgICAgICAgICAgICAgdGhpcy5vblBhcnNlTWVzc2FnZUZhaWxlZChlKTtcclxuICAgICAgICAgICAgfVxyXG4gICAgfVxyXG5cclxuICAgIC8qKlxyXG4gICAgICogQ2FsbGVkIHdoZW4gcGFyc2luZyBhIG1lc3NhZ2UgZmFpbHMsIGVpdGhlciBkdWUgdG8gY29ycnVwdGVkIGRhdGEsIG9yIHVuZXhwZWN0ZWQgcHJvdG9jb2wuXHJcbiAgICAgKiBAcGFyYW0gZXJyb3IgVGhlIGVycm9yIG1pZ2h0IGJlIGFuIGV4Y2VwdGlvbiwgb3IgYW4gZXJyb3IgbWVzc2FnZS5cclxuICAgICAqL1xyXG4gICAgb25QYXJzZU1lc3NhZ2VGYWlsZWQoZXJyb3I6IGFueSkge1xyXG4gICAgICAgIGNvbnNvbGUubG9nKFwiRmFpbGVkIHRvIHBhcnNlIG1lc3NhZ2UsIGNsb3NpbmcgY29ubmVjdGlvbi4gRXhjZXB0aW9uOiBcIiArIGVycm9yKTtcclxuICAgICAgICB0aGlzLmRpc2Nvbm5lY3RJbnRlcm5hbCgxMDAyLCBlcnJvcik7XHJcbiAgICB9XHJcblxyXG4gICAgLyoqXHJcbiAgICAgKiBDYWxsZWQgd2hlbiByZWNlaXZpbmcgYSBtZXNzYWdlIHdpdGggYSBtZXNzYWdlIHR5cGUgd2hpY2ggd2UgaGF2ZSBubyBoYW5kbGVyIGZvci5cclxuICAgICAqIEBwYXJhbSBtZXNzYWdlVHlwZVxyXG4gICAgICovXHJcbiAgICBvbk1lc3NhZ2VNaXNzaW5nSGFuZGxlcihtZXNzYWdlVHlwZTogc3RyaW5nKSB7XHJcbiAgICAgICAgbGV0IGVycm9yID0gXCJHb3QgbWVzc2FnZSB3aXRob3V0IGhhbmRsZXI6IFwiICsgbWVzc2FnZVR5cGU7XHJcbiAgICAgICAgY29uc29sZS5sb2coZXJyb3IpO1xyXG4gICAgICAgIHRoaXMuZGlzY29ubmVjdEludGVybmFsKDEwMDMsIGVycm9yKTtcclxuICAgIH1cclxuXHJcbiAgICAvKipcclxuICAgICAqIFNldCB0aGUgaGFuZGxlciBmb3IgYSBnaXZlbiBtZXNzYWdlLlxyXG4gICAgICogQHBhcmFtIG1lc3NhZ2VJZCBUaGUgc3RyaW5nIGlkZW50aWZpZXIgZm9yIHRoZSBtZXNzYWdlIHR5cGVcclxuICAgICAqIEBwYXJhbSBoYW5kbGVyIFxyXG4gICAgICovXHJcbiAgICBzZXRIYW5kbGVyKG1lc3NhZ2VJZDogc3RyaW5nLCBoYW5kbGVyOiBNZXNzYWdlSGFuZGxlcikge1xyXG4gICAgICAgIHRoaXMubWVzc2FnZUhhbmRsZXJzW21lc3NhZ2VJZF0gPSBoYW5kbGVyO1xyXG4gICAgfVxyXG5cclxuICAgIHNlbmRNZXNzYWdlKG1lc3NhZ2U6IE1lc3NhZ2UpIHtcclxuICAgICAgICBpZih0aGlzLnNvY2tldCA9PSBudWxsKVxyXG4gICAgICAgICAgICByZXR1cm47XHJcblxyXG4gICAgICAgIHRoaXMuc29ja2V0LnNlbmQoSlNPTi5zdHJpbmdpZnkobWVzc2FnZSkpO1xyXG4gICAgfVxyXG59IiwiaW1wb3J0IHsgTWVzc2FnZSB9IGZyb20gXCIuLi9tZXNzYWdlXCJcclxuXHJcbmV4cG9ydCBkZWZhdWx0IGNsYXNzIE1lc3NhZ2VWZXJzaW9uIGltcGxlbWVudHMgTWVzc2FnZSB7XHJcbiAgICBtZXNzYWdlSWQ6IHN0cmluZyA9IFwiVmVyc2lvblwiO1xyXG4gICAgbWVzc2FnZURhdGE6IHtcclxuICAgICAgICB2ZXJzaW9uOiBudW1iZXI7XHJcbiAgICB9O1xyXG5cclxuICAgIGNvbnN0cnVjdG9yKHZlcnNpb246IG51bWJlcikge1xyXG4gICAgICAgIHRoaXMubWVzc2FnZURhdGEgPSB7XHJcbiAgICAgICAgICAgIHZlcnNpb246IHZlcnNpb25cclxuICAgICAgICB9XHJcbiAgICB9XHJcbn0iLCJpbXBvcnQgTWVzc2FnZXNDbGllbnQgZnJvbSBcIi4vbWVzc2FnZXNjbGllbnRcIjtcclxuaW1wb3J0IE1lc3NhZ2VWZXJzaW9uIGZyb20gXCIuL21lc3NhZ2VzL01lc3NhZ2VWZXJzaW9uXCI7XHJcblxyXG5sZXQgQ09SRV9WRVJTSU9OID0gMTsgLy9TaG91bGQgbWF0Y2ggdGhlIFNlcnZlciBDb3JlIHZlcnNpb24oVE9ETzogQXV0byBnZW5lcmF0ZT8pXHJcblxyXG5pbnRlcmZhY2UgQ29ubmVjdGlvbkhhbmRsZXIge1xyXG4gICAgb25Db25uZWN0ZWQoKTtcclxuICAgIG9uRGlzY29ubmVjdGVkKGNvZGU6IG51bWJlciwgcmVhc29uOiBzdHJpbmcsIHdhc0NsZWFuOiBib29sZWFuKTtcclxuICAgIG9uRXJyb3IoZXJyb3JNZXNzYWdlOiBzdHJpbmcpO1xyXG59XHJcblxyXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBDb3JlIHtcclxuICAgIGNsaWVudDogTWVzc2FnZXNDbGllbnQgfCBudWxsO1xyXG5cclxuICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgIGNvbnNvbGUubG9nKFwiTG9hZGVkIE1vZHVsZUNvcmVcIik7XHJcbiAgICB9XHJcblxyXG4gICAgY29ubmVjdChzZXJ2ZXI6IHN0cmluZywgcG9ydDogbnVtYmVyLCBjb25uZWN0aW9uSGFuZGxlcjogQ29ubmVjdGlvbkhhbmRsZXIpIHtcclxuICAgICAgICAvL1RPRE86IElmIGFscmVhZHkgY29ubmVjdGVkLCBkaXNjb25uZWN0IGFuZCB1bmxvYWQgYWxsIGxvYWRlZCBNb2R1bGVzKE9yIHJldGFpbiB0aGVtIHVudGlsIHJlY29ubmVjdCBhbmQgZG8gcGVybWlzc2lvbiBjaGVjaz8pXHJcbiAgICAgICAgaWYodGhpcy5jbGllbnQpIHtcclxuICAgICAgICAgICAgdGhpcy5jbGllbnQuZGlzY29ubmVjdChmYWxzZSwgXCJOZXcgY29ubmVjdGlvblwiKTtcclxuICAgICAgICAgICAgdGhpcy5jbGllbnQgPSBudWxsO1xyXG4gICAgICAgIH1cclxuXHJcbiAgICAgICAgY29uc29sZS5sb2coXCJDb25uZWN0aW5nIHRvIFwiICsgc2VydmVyICsgXCI6XCIgKyBwb3J0KTtcclxuICAgICAgICB0aGlzLmNsaWVudCA9IG5ldyBNZXNzYWdlc0NsaWVudChzZXJ2ZXIsIHBvcnQpO1xyXG4gICAgICAgIHRoaXMuY2xpZW50Lm9uQ29ubmVjdGVkID0gKCkgPT4ge1xyXG4gICAgICAgICAgICBpZih0aGlzLmNsaWVudCA9PSBudWxsKVxyXG4gICAgICAgICAgICAgICAgcmV0dXJuO1xyXG5cclxuICAgICAgICAgICAgLy9TZW5kIG91ciBDb3JlIENsaWVudCB2ZXJzaW9uIHRvIHRoZSBzZXJ2ZXIsIGFsbG93aW5nIGl0IHRvIHZlcmlmeSB3ZSBhcmUgY29tcGF0aWJsZVxyXG4gICAgICAgICAgICB0aGlzLmNsaWVudC5zZW5kTWVzc2FnZShuZXcgTWVzc2FnZVZlcnNpb24oQ09SRV9WRVJTSU9OKSk7XHJcbiAgICAgICAgICAgIGNvbm5lY3Rpb25IYW5kbGVyLm9uQ29ubmVjdGVkKCk7XHJcbiAgICAgICAgfTtcclxuICAgICAgICB0aGlzLmNsaWVudC5vbkRpc2Nvbm5lY3RlZCA9IGZ1bmN0aW9uIChjbG9zZTogQ2xvc2VFdmVudCkge1xyXG4gICAgICAgICAgICBjb25uZWN0aW9uSGFuZGxlci5vbkRpc2Nvbm5lY3RlZChjbG9zZS5jb2RlLCBjbG9zZS5yZWFzb24sIGNsb3NlLndhc0NsZWFuKTtcclxuICAgICAgICB9O1xyXG5cclxuICAgICAgICB0cnkge1xyXG4gICAgICAgICAgICB0aGlzLmNsaWVudC5jb25uZWN0KCk7XHJcbiAgICAgICAgfSBjYXRjaChleGNlcHRpb24pIHtcclxuICAgICAgICAgICAgbGV0IGVycm9yID0gXCJFcnJvciB3aGlsZSBzZXR0aW5nIHVwIGNvbm5lY3Rpb246IFwiICsgZXhjZXB0aW9uO1xyXG4gICAgICAgICAgICBpZih0aGlzLmNsaWVudCkge1xyXG4gICAgICAgICAgICAgICAgdGhpcy5jbGllbnQuZGlzY29ubmVjdCh0cnVlLCBlcnJvcik7XHJcbiAgICAgICAgICAgICAgICB0aGlzLmNsaWVudCA9IG51bGw7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9XHJcbiAgICB9XHJcblxyXG4gICAgbG9naW4oKSB7fVxyXG4gICAgbG9nb3V0KCkge31cclxuICAgIGdldE1vZHVsZXMoKSB7fVxyXG4gICAgbG9hZE1vZHVsZSgpIHt9XHJcblxyXG5cclxufSIsImltcG9ydCBDb3JlIGZyb20gXCIuL2NvcmVcIjtcclxuXHJcbmNsYXNzIE1haW4ge1xyXG4gICAgcHVibGljIHJ1bigpIHtcclxuICAgICAgICBsZXQgY29yZSA9IG5ldyBDb3JlKCk7XHJcblxyXG4gICAgICAgIC8vQ29ubmVjdCB0byBzZXJ2ZXJcclxuICAgICAgICBjb3JlLmNvbm5lY3QoXCJsb2NhbGhvc3RcIiwgODA4MCwgbmV3IGNsYXNzIHtcclxuICAgICAgICAgICAgb25Db25uZWN0ZWQoKSB7XHJcbiAgICAgICAgICAgICAgICBjb25zb2xlLmxvZyhcIkNvbm5lY3RlZFwiKTtcclxuXHJcbiAgICAgICAgICAgICAgICAvL0dldCBmb3IgbGlzdCBvZiBTZXJ2aWNlIEFQSXNcclxuICAgICAgICAgICAgICAgIFxyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIG9uRGlzY29ubmVjdGVkKGNvZGU6IG51bWJlciwgcmVhc29uOiBzdHJpbmcsIHdhc0NsZWFuOiBib29sZWFuKSB7XHJcbiAgICAgICAgICAgICAgICBjb25zb2xlLmxvZyhcIkRpc2Nvbm5lY3RlZDogXCIgKyBjb2RlICsgXCIgfCBcIiArIHJlYXNvbiArIFwiIHwgXCIgKyB3YXNDbGVhbik7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgb25FcnJvcihlcnJvck1lc3NhZ2U6IHN0cmluZykge1xyXG4gICAgICAgICAgICAgICAgY29uc29sZS5sb2coXCJDb25uZWN0aW9uIEVycm9yOiBcIiArIGVycm9yTWVzc2FnZSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9KTtcclxuICAgICAgICBcclxuICAgICAgICAvL1N0YXJ0IHNlc3Npb24gd2l0aCB0aGUgTW9kdWxlIE1hbmFnZXIsIGFuZCBnZXQgYWN0aXZlIG1vZHVsZXMgZnJvbSBzZXJ2ZXIoV2hpY2ggd2UgaGF2ZSBwZXJtaXNzaW9uIHRvIGxvYWQpXHJcblxyXG4gICAgICAgIC8vV2FpdCBmb3IgdXNlciBzZXR0aW5ncyBmcm9tIHNlcnZlcihJbiByZXNwb25zZSB0byBjb25uZWN0aW5nKVxyXG5cclxuICAgICAgICAvL0xvYWQgYWxsIGFjdGl2ZSBtb2R1bGVzXHJcbiAgICB9XHJcbn1cclxuXHJcbmV4cG9ydCA9IE1haW47Il19