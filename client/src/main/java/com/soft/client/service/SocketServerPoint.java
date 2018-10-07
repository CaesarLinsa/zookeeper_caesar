package com.soft.client.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public class SocketServerPoint {
        private String ip;
        private Integer port;

        public SocketServerPoint(String ip, Integer port) {
            this.ip = ip;
            this.port = port;
        }

        public  void clientToServer(String str){
            try {
                Socket client = new Socket(this.ip,this.port);
                Writer writer = new OutputStreamWriter(client.getOutputStream());
                writer.write(str);
                writer.flush();
                writer.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

}
