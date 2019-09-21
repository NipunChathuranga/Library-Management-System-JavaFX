package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import util.IBookCartTM;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class IssueBookformController {
    public JFXTextField txtFieldIssueID;
    public JFXTextField txtFieldDate;
    public JFXComboBox<String> cmbMemberID;
    public JFXTextField txtFieldName;
    public JFXComboBox<String> cmbBookID;
    public JFXTextField txtFieldTitle;
    public JFXTextField txtFieldAuthor;
    public JFXButton btnNewIssue;
    public JFXButton btnIssueBook;
    public TableView<IBookCartTM> tblViewIssued;
    public JFXButton btnAdd;
    public AnchorPane anchrpaneIssueBook;
    public TableColumn clmnBookID;
    public TableColumn clmnTitle;
    public TableColumn clmnAuthor;
    public TableColumn clmnBtnDel;
    // private ArrayList<BookTM> tempbooks = new ArrayList<>();
    private Connection connection;
    private PreparedStatement psForSelect;
    private PreparedStatement psForInsert;
    private PreparedStatement psForUpdate;
    //private PreparedStatement psForDelete;
    private PreparedStatement psForLoadMem;
    private PreparedStatement psForLoadBook;

    public void initialize() throws ClassNotFoundException, SQLException {
        tblViewIssued.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("bookid"));
        tblViewIssued.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblViewIssued.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tblViewIssued.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("delete"));

        try {
            connection = DBConnection.getInstance().getConnection();
            psForSelect = connection.prepareStatement("SELECT * FROM issue");
            psForInsert = connection.prepareStatement("INSERT INTO issue VALUES (?,?,?,?)");
            psForUpdate = connection.prepareStatement("UPDATE book SET status='Not Available' WHERE bookid=?");
            psForLoadBook = connection.prepareStatement("SELECT * FROM book WHERE status='Available'");
            psForLoadMem = connection.prepareStatement("SELECT * FROM library_member");
            //psForDelete = connection.prepareStatement("DELETE FROM library_member WHERE memberid=?");
//            loadAllMembers();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        txtFieldDate.setText(String.valueOf(LocalDate.now()));
//        ObservableList<String> books = cmbBookID.getItems();
//        for (BookTM book : DB.booklist) {
//            if(book.getStatus().equals("Available")){
//                books.add(book.getBookid());
//            }
//        }

//        ObservableList<String> members = cmbMemberID.getItems();
//        for (MemberTM member : DB.memberlist) {
//            members.add(member.getId());
//        }
//
//        tempbooks = new ArrayList<>();
//        for (BookTM book : DB.booklist) {
//            tempbooks.add(book.clone());
//        }


        ResultSet resultSet = psForLoadMem.executeQuery();
        System.out.println(resultSet);
        ObservableList<String> memberID = cmbMemberID.getItems();
        while (resultSet.next()) {
            String id = resultSet.getString("memberid");
            memberID.add(id);
        }

        ResultSet resultSt = psForLoadBook.executeQuery();

        ObservableList<String> bookIDs = cmbBookID.getItems();
        while (resultSt.next()) {
            String id = resultSt.getString("bookid");
            bookIDs.add(id);
        }

        cmbMemberID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String selectedmemberID = cmbMemberID.getSelectionModel().getSelectedItem();
                if (selectedmemberID == null) {
                    return;
                }
                ResultSet resultSet1 = null;
                try {
                    resultSet1 = psForLoadMem.executeQuery();
                    while (resultSet1.next()) {
                        if (resultSet1.getString("memberiD").equals(selectedmemberID)) {
                            txtFieldName.setText(resultSet1.getString("membername"));
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }


//                for (IssueDetail issueDetail:DB.issuedBookList){
//                    if(issueDetail.getMemberId().equals(selectedmemberID)){
//                        Alert alert = new Alert(Alert.AlertType.WARNING);
//                        alert.setTitle("Warning Alert");
//
//                        // Header Text: null
//                        alert.setHeaderText(null);
//                        alert.setContentText("A member can borrow only one book at a time! ");
//
//                        alert.showAndWait();
//                    }
//                }
//
//                enableIssueBookButton();
////                for(BookTM bookTM:DB.booklist){
////                    if(bookTM.getStatus()=="NA"){
////                        if(bookTM.)
////                    }
////                }
//
//                for (MemberTM member : DB.memberlist) {
//                    if (member.getId().equals(selectedmemberID)) {
//                        txtFieldName.setText(member.getName());
//                        txtFieldName.setEditable(false);
//                        break;
//                    }
//                }
            }
        });


        cmbBookID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String selectedBookCode = cmbBookID.getSelectionModel().getSelectedItem();
                if (selectedBookCode == null) {
                    return;
                }
                ResultSet resultSet = null;

                try {
                    resultSet = psForLoadBook.executeQuery();
                    while (resultSet.next()) {
                        if (resultSet.getString("bookid").equals(selectedBookCode)) {
                            txtFieldTitle.setText(resultSet.getString("title"));
                            txtFieldAuthor.setText(resultSet.getString("author"));
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    public void btnBackToHome_OnClicked(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/Mainform.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.anchrpaneIssueBook.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void btnNewIssue_OnAction(ActionEvent actionEvent) {

        txtFieldAuthor.setDisable(false);
        txtFieldTitle.setDisable(false);
        txtFieldName.setDisable(false);
        // txtNumberOfBook.setDisable(false);
        cmbBookID.setDisable(false);
        cmbMemberID.setDisable(false);
        txtFieldIssueID.setDisable(false);
        btnAdd.setDisable(false);
        generateIssueId();


    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        ObservableList<IBookCartTM> items = tblViewIssued.getItems();
        String selectedMember = (String) cmbMemberID.getSelectionModel().getSelectedItem();

        if (selectedMember != "null") {
            String seleItem = (String) cmbBookID.getSelectionModel().getSelectedItem();
            boolean isExsits = false;
            for (IBookCartTM item : tblViewIssued.getItems()) {
                if (item.getBookid().equals(seleItem)) {
                    new Alert(Alert.AlertType.ERROR, "Can not add same Book!", ButtonType.OK).show();
                    isExsits = true;
                }
            }
            if (!isExsits) {
                JFXButton button = new JFXButton("Delete");
                IBookCartTM addBookTM = new IBookCartTM((String) cmbBookID.getSelectionModel().getSelectedItem(), txtFieldTitle.getText(), txtFieldAuthor.getText(), button);
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //   count--;
                        tblViewIssued.getItems().remove(addBookTM);
                        tblViewIssued.refresh();
                        btnAdd.setDisable(false);
                    }
                });
                // count++;
                items.add(addBookTM);
                tblViewIssued.refresh();
//                cmbBookID.getSelectionModel().clearSelection();
                txtFieldTitle.clear();
                txtFieldAuthor.clear();
                //cmbBookID.getSelectionModel().clearSelection();
                //txtNumberOfBook.setText(Integer.toString(count));
                txtFieldIssueID.setDisable(false);
                btnAdd.setDisable(true);
                new Alert(Alert.AlertType.WARNING, "You can borrow only one book at a time.").show();

            }
        }


    }


    public void generateIssueId() {

        int maxid = 0;

        try {
            ObservableList<String> issueBook = FXCollections.observableArrayList();
            ResultSet resultSet = psForSelect.executeQuery();
            while (resultSet.next()) {
                String issueID = resultSet.getString("issueid");
                issueBook.add(issueID);

                for (String s : issueBook) {
                    int id = Integer.parseInt(s.replace("I", ""));
                    if (id > maxid) {
                        maxid = id;
                    }

                }
            }


            maxid = maxid + 1;

            if (maxid < 10) {
                txtFieldIssueID.setText("I00" + maxid);
            } else if (maxid < 100) {
                txtFieldIssueID.setText("I0" + maxid);
            } else {
                txtFieldIssueID.setText("I" + maxid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void btnIssueBook_OnAction(ActionEvent actionEvent) throws SQLException {
        String selectedItem = (String) cmbBookID.getSelectionModel().getSelectedItem();

        try {

            connection.setAutoCommit(false);
            psForInsert.setString(1, txtFieldIssueID.getText());
            psForInsert.setString(2, txtFieldDate.getText());
            psForInsert.setString(3, cmbBookID.getSelectionModel().getSelectedItem());
            psForInsert.setString(4, cmbMemberID.getSelectionModel().getSelectedItem());

            System.out.println(psForInsert);

            int i = psForInsert.executeUpdate();

            if (i == 0) {
                connection.rollback();
                throw new RuntimeException("Error!! Something went wrong");
            }




            psForUpdate.setString(1, selectedItem);

            int i1 = psForUpdate.executeUpdate();
            if (i1 == 0) {
                connection.rollback();
                throw new RuntimeException("Something, something went wrong");
            }

            connection.commit();
            new Alert(Alert.AlertType.INFORMATION, "--Book Issue Process Completed--").showAndWait();
            JasperReport mainJasperReport = (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream("/report/IssueMainReport.jasper"));


            Map<String, Object> params = new HashMap<>();
            params.put("issueid", txtFieldIssueID.getText());

            JasperPrint jasperPrint = JasperFillManager.fillReport(mainJasperReport,
                    params, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (Throwable e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        cmbBookID.getItems().clear();

        ResultSet resultSet = psForLoadBook.executeQuery();
        ObservableList<String> bookIDs = cmbBookID.getItems();
        while (resultSet.next()) {
            String id = resultSet.getString("bookid");
            bookIDs.add(id);
        }

        txtFieldIssueID.clear();
        cmbMemberID.getSelectionModel().clearSelection();
        txtFieldName.clear();
        tblViewIssued.getItems().clear();

    }
}