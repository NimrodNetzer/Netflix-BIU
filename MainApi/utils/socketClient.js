const net = require('net');


class SocketClient {
    constructor(serverIp, serverPort) {
      this.serverIp = serverIp;
      this.serverPort = serverPort;
      this.client = null;
      this.isConnected = false;
      this.connectPromise = null;
    }
  
    async connect() {
      if (this.isConnected) {
        return;
      }
  
      if (this.connectPromise) {
        return this.connectPromise;
      }
  
      this.connectPromise = new Promise((resolve, reject) => {
        this.client = new net.Socket();
  
        this.client.on('connect', () => {
          this.isConnected = true;
          resolve();
        });
  
        this.client.on('error', (err) => {
          console.error('Socket error:', err);
          this.isConnected = false;
          reject(err);
        });
  
        this.client.on('close', () => {
          this.isConnected = false;
          this.connectPromise = null;
        });
        this.client.connect(this.serverPort, this.serverIp);
      });
  
      return this.connectPromise;
    }
  
    async send(message) {
      try {
        if (!this.isConnected) {
          await this.connect();
        }
  
        return new Promise((resolve, reject) => {
          const messageHandler = (data) => {
            this.client.removeListener('data', messageHandler);
            resolve(data.toString());
          };
  
          this.client.on('data', messageHandler);
          this.client.write(message);
        });
      } catch (error) {
        console.error('Error sending message:', error);
        throw error;
      }
    }
  
    disconnect() {
      if (this.client && this.isConnected) {
        this.client.destroy();
        this.isConnected = false;
        this.connectPromise = null;
      }
    }
}
  

module.exports = SocketClient;