package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
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
import util.IBookCartTM;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

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
        Class.forName("com.mysql.jdbc.Driver");
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");
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
//
//                if (selectedBookCode == null) {
//
//                    txtFieldAuthor.clear();
//                    txtFieldTitle.clear();
//
//                    btnAdd.setDisable(true);
//                    return;
//                }
//
//
//                btnAdd.setDisable(false);
//                for (BookTM bookTM : tempbooks) {
//                    if (bookTM.getBookid().equals(selectedBookCode)) {
//                        txtFieldTitle.setText((bookTM.getTitle()));
//                        txtFieldAuthor.setText(bookTM.getAuthor());
//                        txtFieldAuthor.setEditable(false);
//                        txtFieldTitle.setEditable(false);
//
//                    }
//                }
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
//        ObservableList<String> books = cmbBookID.getItems();
//        books.clear();
//        for (BookTM book : DB.booklist) {
//            if (book.getStatus().equals("Available")) {
//                books.add(book.getBookid());
//            }
//        }
//
//
//
//        reset();
//        tempbooks = new ArrayList<>();
//        for (BookTM item : DB.booklist) {
//            tempbooks.add(item.clone());
//        }

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
                new Alert(Alert.AlertType.WARNING,"You can borrow only one book at a time.").show();

            }
        }
//        String selectedItemCode = cmbBookID.getSelectionModel().getSelectedItem();
//
//        ObservableList<IBookCartTM> details = tblViewIssued.getItems();
//        Button btnDelete = new Button("Delete");
//
//
//        IBookCartTM detailTM = new IBookCartTM(cmbBookID.getSelectionModel().getSelectedItem(),
//                txtFieldTitle.getText(), txtFieldAuthor.getText(), btnDelete);
//        for (BookTM bookTM : tempbooks) {
//            if (bookTM.getBookid().equals(cmbBookID.getSelectionModel().getSelectedItem())) {
//                bookTM.setStatus("NA");
//            }
//        }
//
//        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                for (BookTM tempbook : tempbooks) {
//                    if (tempbook.getBookid().equals(detailTM.getBookid())) {
//
//                        tempbook.setStatus("Available");
//                        break;
//                    }
//                }
//                tblViewIssued.getItems().remove(detailTM);
//
//
//                enableIssueBookButton();
//                if (tblViewIssued.getItems().size() == 1) {
//                    btnAdd.setDisable(true);
//                }
//                cmbBookID.requestFocus();
//                cmbBookID.getSelectionModel().clearSelection();
//                tblViewIssued.getSelectionModel().clearSelection();
//                btnAdd.setDisable(false);
//            }
//
//        });
//
//        details.add(detailTM);
//        enableIssueBookButton();
//        cmbBookID.requestFocus();
//        //cmbBookID.getSelectionModel().clearSelection();
//        tblViewIssued.getSelectionModel().clearSelection();
//
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Borrowing Limits");
//
//        // Header Text: null
//        alert.setHeaderText(null);
//        alert.setContentText(" You have reached your borrowing limit ! ");
//
//        alert.showAndWait();

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


//        private void reset() {
//
//        txtFieldDate.setText(String.valueOf(LocalDate.now()));
//        btnIssueBook.setDisable(true);
//        btnAdd.setDisable(true);
//        txtFieldTitle.setEditable(false);
//        txtFieldName.setEditable(false);
//        txtFieldAuthor.setEditable(false);
//        cmbBookID.getSelectionModel().clearSelection();
//        cmbMemberID.getSelectionModel().clearSelection();
//        tblViewIssued.getItems().clear();
//        txtFieldName.clear();
//
//        int maxOrderId = 0;
//        for (IssueDetail issueDetail : DB.issuedBookList) {
//            int issueId = Integer.parseInt(issueDetail.getIssueId().replace("I", ""));
//            if (issueId > maxOrderId) {
//                maxOrderId = issueId;
//            }
//        }
//        maxOrderId++;
//        if (maxOrderId < 10) {
//            txtFieldIssueID.setText("I00" + maxOrderId);
//        } else if (maxOrderId < 100) {
//            txtFieldIssueID.setText("I0" + maxOrderId);
//        } else {
//            txtFieldIssueID.setText("I" + maxOrderId);
//        }
//
//
//    }
//


//    private void enableIssueBookButton() {
//
//        String selectedMember = cmbMemberID.getSelectionModel().getSelectedItem();
//        int size = tblViewIssued.getItems().size();
//        if (selectedMember == null || size == 0) {
//            btnIssueBook.setDisable(true);
//        } else {
//            btnIssueBook.setDisable(false);
//        }
//
//
//    }


//
//
//
//    }
    }

    public void btnIssueBook_OnAction(ActionEvent actionEvent) throws SQLException {
        String selectedItem = (String) cmbBookID.getSelectionModel().getSelectedItem();

        psForInsert.setString(1, txtFieldIssueID.getText());
        psForInsert.setString(2, txtFieldDate.getText());
        psForInsert.setString(3, cmbBookID.getSelectionModel().getSelectedItem());
        psForInsert.setString(4, cmbMemberID.getSelectionModel().getSelectedItem());

        System.out.println(psForInsert);

        int i = psForInsert.executeUpdate();

        if (i > 0) {
            new Alert(Alert.AlertType.CONFIRMATION, "Success", ButtonType.OK).show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK).show();
        }


        psForUpdate.setString(1, selectedItem);

        int i1 = psForUpdate.executeUpdate();
        if (i1 > 0) {
            System.out.println("done");
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
//        IssueDetail issueDetail = new IssueDetail(txtFieldIssueID.getText(),
//                LocalDate.now(),cmbBookID.getSelectionModel().getSelectedItem(),
//                cmbMemberID.getSelectionModel().getSelectedItem(), txtFieldName.getText());
//        DB.issuedBookList.add(issueDetail);
//        DB.booklist = tempbooks;
//        System.out.println(issueDetail);
//        reset();
       // generateIssueId();
    }
}