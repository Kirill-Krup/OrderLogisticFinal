package com.courcach.corsewww.Models;
import com.courcach.Server.Services.ClassesForRequests.Users;
import com.courcach.corsewww.Views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final ConnectionToServer connectionToServer;
    private Users user;

    private Model(){
        this.connectionToServer = new ConnectionToServer();
        this.viewFactory = new ViewFactory();
    }

    public synchronized static Model getInstance(){
        if(model == null){
            model = new Model();
        }
        return model;
    }

    public ConnectionToServer getConnectionToServer() {return connectionToServer;}

    public ViewFactory getViewFactory(){
        return viewFactory;
    }

    public Users getCurrentUser() {
        return user;
    }

    public void setCurrentUser(Users currentUser) {
        this.user = currentUser;
    }
}