package com.courcach.corsewww.Controllers.Employee;

import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import com.courcach.Server.Services.Employee.EmployeeRequest;
import com.courcach.corsewww.Controllers.Client.HistoryRowController;
import com.courcach.corsewww.Models.ConnectionToServer;
import com.courcach.corsewww.Models.Model;
import com.courcach.corsewww.Views.NotificationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReportsWorkController {
    private ConnectionToServer connection = Model.getInstance().getConnectionToServer();
    private List<ReportModel> allReports;
    private int rating = 0;

    @FXML
    private Button allReportsBut;

    @FXML
    private TextArea answerField;

    @FXML
    private Button backBut;

    @FXML
    private ListView<AnchorPane> listForReports;

    @FXML
    private Button noAnswerReportsBut;

    @FXML
    private Text noMessages;

    @FXML
    private StackPane notificationPane;

    @FXML
    private Label ratingLabel;

    @FXML
    private ImageView sadImage;

    @FXML
    private Button sendAnswerBut;


    public void initialize() {
        connection.sendObject(new EmployeeRequest("giveMeAllReports"));
        allReports = (List<ReportModel>) connection.receiveObject();
        noAnswerReportsBut.setStyle(noAnswerReportsBut.getStyle() + "-fx-background-color: green;-fx-text-fill: white;");
        updateRating();
        refreshNotAnswered();

        allReportsBut.setOnAction(event -> {
            allReportsBut.setStyle(allReportsBut.getStyle() + "-fx-background-color: green; -fx-text-fill: white;");
            noAnswerReportsBut.setStyle(noAnswerReportsBut.getStyle() + "-fx-background-color: transparent;-fx-text-fill: #d4ffb2;");
            answerField.setText("");
            answerField.setPromptText("Выберите отзыв и вы увидите ответ сотрудика, если такой есть");
            sendAnswerBut.setVisible(false);
            refreshAllReports();
        });

        noAnswerReportsBut.setOnAction(event -> {
            allReportsBut.setStyle(allReportsBut.getStyle() + "-fx-background-color: transparent; -fx-text-fill: #d4ffb2;");
            noAnswerReportsBut.setStyle(noAnswerReportsBut.getStyle() + "-fx-background-color: green;-fx-text-fill: white;");
            answerField.setText("");
            answerField.setPromptText("Введите ответ пользователю");
            sendAnswerBut.setVisible(true);
            refreshNotAnswered();
        });


        sendAnswerBut.setOnAction(event -> {
            int selectedIndex = listForReports.getSelectionModel().getSelectedIndex();
            if(selectedIndex >= 0){
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                AnchorPane selectedPane = listForReports.getSelectionModel().getSelectedItem();
                ReportRowForEmployeeController controller = (ReportRowForEmployeeController) selectedPane.getProperties().get("controller");
                ReportModel report = new ReportModel(controller.getReport().getOrderNumber(),answerField.getText(),currentTime);
                connection.sendObject(new EmployeeRequest("answerForUser",report,Model.getInstance().getCurrentUser().getLogin()));
                String request =(String) connection.receiveObject();
                if(request.contains("успешно")){
                    NotificationUtil.showNotification(notificationPane,request);
                    for(ReportModel rep : allReports){
                        if(rep.getOrderNumber() == controller.getReport().getOrderNumber()){
                            rep.setReportAnswer(answerField.getText());
                            rep.setReportAnswerTime(currentTime);
                            break;
                        }
                    }
                    refreshNotAnswered();
                }else{
                    NotificationUtil.showErrorNotification(notificationPane, request);
                }
            }else{
                NotificationUtil.showErrorNotification(notificationPane,"Вы не выбрали отзыв, на который хотите ответить");
            }
        });

        backBut.setOnAction(event -> {
            Stage stage = (Stage) backBut.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showEmployeeWindow();
        });
    }


    private void refreshNotAnswered(){
        listForReports.getItems().clear();
        boolean check = false;
        noMessages.setVisible(false);
        sadImage.setVisible(false);
        for(ReportModel report : allReports){
            if(report.getReportAnswer() == null){
                check = true;
                addItem(report);
            }
        }
        if(!check) {
            noMessages.setVisible(true);
            sadImage.setVisible(true);
        }
    }

    private void refreshAllReports(){
        listForReports.getItems().clear();
        noMessages.setVisible(false);
        sadImage.setVisible(false);
        listForReports.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                ReportRowForEmployeeController controller =
                        (ReportRowForEmployeeController) newSelection.getProperties().get("controller");
                if (controller.getReport().getReportAnswer() != null) {
                    answerField.setText(controller.getReport().getReportAnswer());
                } else {
                    answerField.setText("На этот отзыв ещё нет ответа");
                }
            }
        });
        for(ReportModel report : allReports){
            addItem(report);
        }
    }

    private void addItem(ReportModel report){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Employee/ReportRowForEmployee.fxml"));
        AnchorPane cellForOrder;
        try {
            cellForOrder = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ReportRowForEmployeeController controller = fxmlLoader.getController();
        controller.setTexts(report);
        controller.setItemPane(cellForOrder);
        cellForOrder.getProperties().put("controller", controller);
        listForReports.getItems().add(cellForOrder);
    }



    private void updateRating() {
        for (ReportModel reportModel : allReports) {
            rating = rating + reportModel.getStars();
        }
        if(!allReports.isEmpty()){
            rating = rating / allReports.size();
            ratingLabel.setText(rating + " ★");
        }

    }
}
