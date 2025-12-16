import { Injectable } from '@angular/core';
import { RxStomp } from '@stomp/rx-stomp';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root',
})
export class WebSocketService extends RxStomp {
    constructor() {
        super();
    }

    public connect(token: string) {
        this.configure({
            brokerURL: `ws://localhost:8082/ws?token=${token}`,
            connectHeaders: {
                Authorization: `Bearer ${token}`
            },
            heartbeatIncoming: 0,
            heartbeatOutgoing: 20000,
            reconnectDelay: 200,
            // Debug mode for dev
            debug: (msg: string) => {
                // console.log(new Date(), msg);
            }
        });
        this.activate();
    }
}
