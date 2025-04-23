package com.courcach.Server.Handlers;

import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.Server.Services.Client.ClientResponse.ClientOrderResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends RoleHandler {
    public ClientHandler(Socket userSocket, ObjectInputStream in, ObjectOutputStream out) {
        super(userSocket, in, out);
    }

    @Override
    public void handle() throws IOException, ClassNotFoundException {
        while (!exit) {
            ClientRequest request = (ClientRequest) in.readObject();
            if(request instanceof ClientRequest) {
                ClientOrderResponse orderResponse = new ClientOrderResponse();
                switch (request.getRequest()){
                    case "giveMeAllMaterials"->{
                        List<Places> allPlaces =orderResponse.getPlaces();
                        out.writeObject(allPlaces);
                        out.flush();
                    }
                    case "addNewOrder"->{

                    }

                    case "replenishmentWallet"->{

                    }

                    case "exit"->{
                        exit = true;
                    }
                }
            }
        }
    }
}
