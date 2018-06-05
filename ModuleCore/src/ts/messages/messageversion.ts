import { Message } from "../message"

export default class MessageVersion implements Message {
    messageId: string = "Version";
    messageData: {
        version: number;
    };

    constructor(version: number) {
        this.messageData = {
            version: version
        }
    }
}