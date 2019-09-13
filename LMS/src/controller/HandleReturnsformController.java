package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.ReturnTM;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HandleReturnsformController {
    public JFXComboBox<String> cmbIssuedID;
    public JFXTextField txtFieldMemberName;
    public JFXTextField txtFieldIssuedDate;
    public JFXTextField txtFieldCurrentDate;
    public JFXTextField txtFieldFee;
    public JFXButton btnCalculateFee;
    public TableView<ReturnTM> tblViewHandleBooks;
    public TableColumn clmnIssueID;
    public TableColumn clmnMemID;
    public TableColumn clmnName;
    public TableColumn clmnIssuedDate;
    public AnchorPane anchrPaneHandleReturns;
    public TextField txtFieldSearch;
    public JFXButton btnDone;
    public TextField txtOverdue;
    private Connection connection;
    private PreparedStatement pstmForInsert;
    private PreparedStatement psForSelect;
    private PreparedStatement psForUpdate;
    private PreparedStatement psForLoadBook;
    private PreparedStatement psForLoadIssue;
    private PreparedStatement psForIssueID;


    public void initialize() throws ClassNotFoundException, SQLException {

        tblViewHandleBooks.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("issueid"));
        tblViewHandleBooks.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("memberid"));
        tblViewHandleBooks.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("membername"));
        tblViewHandleBooks.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("date"));

        Class.forName("com.mysql.jdbc.Driver");
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");
//
            psForSelect = connection.prepareStatement("SELECT issue.issueid,issue.member_id,library_member.membername,issue.date\n" +
                    "FROM issue\n" +
                    "LEFT JOIN library_member ON library_member.memberid = issue.member_id\n" +
                    "WHERE issue.issueid LIKE ? OR issue.member_id LIKE ? OR library_member.membername LIKE ? OR issue.date LIKE ?\n" +
                    "ORDER BY issue.issueid");
            pstmForInsert = connection.prepareStatement("INSERT INTO book_return VALUES (?,?,?)");
            psForUpdate = connection.prepareStatement("UPDATE book SET status='Available' WHERE bookid=?");
            psForLoadBook = connection.prepareStatement("SELECT * FROM book WHERE status='Not Available'");
            psForLoadIssue = connection.prepareStatement("SELECT * FROM issue");
            psForIssueID = connection.prepareStatement("SELECT issue.issueid,issue.book_id,book.status\n" +
                    "FROM issue\n" +
                    "INNER JOIN book ON book.bookid = issue.book_id\n" +
                    "WHERE book.status='Not Available'\n" +
                    "ORDER BY issue.issueid;\n" +
                    " \n");

            loadAllReturnedBooks();

        } catch (SQLException e) {
            System.out.println("SECOND: " + e);
        }

//        ObservableList<IssueDetail> issueDetails = FXCollections.observableList(DB.issuedBookList);
//        ObservableList<IBookCartTM> iBookCartTMS = FXCollections.observableList(DB.tblCart);
//        tblViewHandleBooks.setItems(issueDetails);
//
//        ObservableList<String> issuedids = cmbIssuedID.getItems();
//        for (IssueDetail issueDetail : DB.issuedBookList) {
//            issuedids.add(issueDetail.getIssueId());
//        }

        ObservableList<String> cmbObservableList = cmbIssuedID.getItems();
        ResultSet resultSet = psForIssueID.executeQuery();
        while (resultSet.next()){
            String iid = resultSet.getString("issueid");
            cmbObservableList.add(iid);

        }


//        while(resultSet1.next()){
//            while (resultSet2.next()){
//                System.out.println("--Testing Testing--");
//                if(resultSet2.getString("bookid").equals( resultSet1.getString("book_id"))){
//                    System.out.println("-TEST-");
//                    String cmbid = resultSet1.getString("issueid");
//                    cmbObservableList.add(cmbid);
//                }
//            }
//        }



//        while ((resultSet.next())) {
//
//            String cmbid = resultSet.getString("issueid");
//            cmbObservableList.add(cmbid);
//        }


        cmbIssuedID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String selectedIssueID = cmbIssuedID.getSelectionModel().getSelectedItem();
                ResultSet resultSt = null;
                try {
                    resultSt = psForSelect.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(selectedIssueID);
                if (selectedIssueID == null) {
                    return;
                }

                try {
                    while (resultSt.next()) {
                        //System.out.println("Test");
                        if (resultSt.getString("issueid").equals(selectedIssueID)) {
                            txtFieldMemberName.setText(resultSt.getString(3));
                            txtFieldIssuedDate.setText(resultSt.getString(4));
                        }

                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }


//                for (IssueDetail issueDetail : DB.issuedBookList) {
//                    if (issueDetail.getIssueId().equals(selectedIssueID)) {
//                        txtFieldMemberName.setText(issueDetail.getMembername());
//                        txtFieldIssuedDate.setText(String.valueOf(issueDetail.getIssueDate()));
//
//                    }
//                }
            }
        });


        txtFieldSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    loadAllReturnedBooks();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    private void loadAllReturnedBooks() throws SQLException {

        tblViewHandleBooks.getItems().clear();
        psForSelect.setString(1, "%" + txtFieldSearch.getText() + "%");
        psForSelect.setString(2, "%" + txtFieldSearch.getText() + "%");
        psForSelect.setString(3, "%" + txtFieldSearch.getText() + "%");
        psForSelect.setString(4, "%" + txtFieldSearch.getText() + "%");

        long t1 = System.currentTimeMillis();
        ResultSet rst = psForSelect.executeQuery();
        long t2 = System.currentTimeMillis();
        System.out.println("Time diff : " + (t2 - t1));

        ObservableList<ReturnTM> returnedbooks = tblViewHandleBooks.getItems();

        while (rst.next()) {
            String issueid = rst.getString(1);
            String memberid = rst.getString(2);
            String membername = rst.getString(3);
            Date date = rst.getDate(4);


            ReturnTM returnTM = new ReturnTM(issueid, memberid, membername, date);
            returnedbooks.add(returnTM);
        }


    }


    public void btnBackToHome_OnClicked(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/Mainform.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.anchrPaneHandleReturns.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }


    public void btnCalculateFee_OnAction(ActionEvent actionEvent) {
        try {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
            String issueDate = txtFieldIssuedDate.getText();
            String currentDate = txtFieldCurrentDate.getText();
            int validDate = 14;
            double fee = 0;
            try {
                Date dateBefore = myFormat.parse(issueDate);
                Date dateAfter = myFormat.parse(currentDate);
                long difference = dateAfter.getTime() - dateBefore.getTime();
                float daysBetween = (difference / (1000 * 60 * 60 * 24));
                System.out.println("Dates between " + daysBetween);
                if (daysBetween > validDate) {
                    int extentndceDays = (int) daysBetween - validDate;
                    txtOverdue.setText(String.valueOf(extentndceDays));
                    fee = extentndceDays * 10;
                    String finalfee = String.format("%.2f", fee);
                    txtFieldFee.setText(finalfee);
                } else {
                    txtOverdue.setText(String.valueOf(0));
                    txtFieldFee.setText(String.valueOf(0));
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (ParseException ex) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Alert");
            // Header Text: null
            alert.setHeaderText(null);
            alert.setContentText("Please fill out all required fields.");
            alert.showAndWait();


//            Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill all required fields.");
//            alert.show();
        }

    }

    public void btnDone_OnAction(ActionEvent actionEvent) throws SQLException {

        ResultSet rst = psForLoadIssue.executeQuery();
        String bookid = null;
        while (rst.next()) {
            if (rst.getString("issueid").equals(cmbIssuedID.getSelectionModel().getSelectedItem())) {
                bookid = rst.getString("book_id");
            }
        }
        pstmForInsert.setString(1, cmbIssuedID.getSelectionModel().getSelectedItem());
        pstmForInsert.setString(2, txtFieldFee.getText());
        pstmForInsert.setString(3, txtFieldCurrentDate.getText());
        int i = pstmForInsert.executeUpdate();

        if (i > 0) {
            new Alert(Alert.AlertType.CONFIRMATION, "Success", ButtonType.OK).show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK).show();
        }
        System.out.println(bookid);
        psForUpdate.setString(1, bookid);
        int i1 = psForUpdate.executeUpdate();
        if (i1 > 0) {
            System.out.println("Update Done");
        }

        ObservableList<String> cmbObservableList = cmbIssuedID.getItems();
        cmbObservableList.clear();
        ResultSet resultSet = psForIssueID.executeQuery();
        while (resultSet.next()){
            String iid = resultSet.getString("issueid");
            cmbObservableList.add(iid);

        }

//        for (IssueDetail issueDetail : DB.issuedBookList) {
//            if (issueDetail.getIssueId().equals(cmbIssuedID.getSelectionModel().getSelectedItem())) {
//                for (BookTM bookTM : DB.booklist) {
//                    if (issueDetail.getBookid().equals(bookTM.getBookid())) {
//                        bookTM.setStatus("Available");
//                    }
//                }
//            }
//        }


        //tblViewHandleBooks.getItems().clear();
        cmbIssuedID.getSelectionModel().clearSelection();
        txtFieldMemberName.clear();
        txtFieldCurrentDate.clear();
        txtFieldIssuedDate.clear();
        txtFieldFee.clear();
        txtOverdue.clear();
    }
}
