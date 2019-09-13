package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import util.BookTM;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;

public class ManageBookformController {
    public AnchorPane anchrPaneManageBooks;
    public JFXTextField txtFieldBookID;
    public JFXTextField txtFieldTitle;
    public JFXTextField txtFieldAuthor;
    public JFXTextField txtFieldPrice;
    public TableView<BookTM> tblViewBooks;
    public TableColumn<BookTM, String> clmnBookID;
    public TableColumn<BookTM, String> clmnTitle;
    public TableColumn<BookTM, String> clmnAuthor;
    public TableColumn<BookTM, Double> clmnPrice;
    public TableColumn<BookTM, String> clmnStatus;
    public JFXButton btnAdd;
    public JFXButton btnNewBook;
    public TextField txtFieldSearch;
    public JFXTextField txtFieldStatus;
    public JFXButton btnDeleteBook;
    private Connection connection;
    private PreparedStatement pstmForQuery;
    private PreparedStatement pstmForInsert;
    private PreparedStatement psForSelect;
    private PreparedStatement psForUpdate;
    private PreparedStatement psForDelete;


    public void initialize() throws ClassNotFoundException {

        txtFieldBookID.setDisable(true);
        txtFieldAuthor.setDisable(true);
        txtFieldTitle.setDisable(true);
        txtFieldPrice.setDisable(true);
        txtFieldStatus.setDisable(true);
        //txtFieldSearch.setDisable(true);
        btnDeleteBook.setDisable(true);

        btnAdd.setDisable(true);


//        ObservableList<BookTM> books = FXCollections.observableList(DB.booklist);
////        tblViewBooks.setItems(books);

        tblViewBooks.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("bookid"));
        tblViewBooks.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblViewBooks.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tblViewBooks.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));
        tblViewBooks.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("status"));


        Class.forName("com.mysql.jdbc.Driver");
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");
//            psForSelect = connection.prepareStatement("SELECT * FROM book WHERE bookid LIKE ? OR title LIKE ? OR " +
//                    "author LIKE ? OR  price LIKE ? OR status LIKE ?");
            pstmForQuery = connection.prepareStatement("SELECT * FROM book WHERE bookid LIKE ? OR title LIKE ? OR author LIKE ? OR price LIKE ? OR status LIKE ?");
            pstmForInsert = connection.prepareStatement("INSERT INTO book VALUES (?,?,?,?,?)");
            psForUpdate = connection.prepareStatement("UPDATE Book SET bookid=?,title=?,author=?,price=?,status=? WHERE bookid=?");
            psForDelete = connection.prepareStatement("DELETE FROM book WHERE bookid=?");
            loadAllBooks();
        } catch (SQLException e) {
            System.out.println("SECOND: " + e);
        }

        tblViewBooks.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookTM>() {
            @Override
            public void changed(ObservableValue<? extends BookTM> observable, BookTM oldValue, BookTM newValue) {
                BookTM selectedbookdetail = tblViewBooks.getSelectionModel().getSelectedItem();
                if (selectedbookdetail == null) {
                    btnAdd.setText("Add");
                    btnAdd.setDisable(true);
                    return;
                } else {

                    btnAdd.setText("Update");
                    txtFieldAuthor.setDisable(false);
                    txtFieldPrice.setDisable(false);
                    txtFieldSearch.setDisable(false);
                    txtFieldStatus.setDisable(false);
                    txtFieldTitle.setDisable(false);
                    btnAdd.setDisable(false);
                    btnDeleteBook.setDisable(false);
                    txtFieldBookID.setText(selectedbookdetail.getBookid());
                    txtFieldTitle.setText(selectedbookdetail.getTitle());
                    txtFieldAuthor.setText(selectedbookdetail.getAuthor());
                    txtFieldStatus.setText(selectedbookdetail.getStatus());
                    txtFieldPrice.setText(String.valueOf(selectedbookdetail.getPrice()));


                }


            }
        });

        txtFieldSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    loadAllBooks();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


//        txtFieldSearch.textProperty().addListener(new ChangeListener<String>() {
////            @Override
////            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
////                ObservableList<BookTM> selectbook = FXCollections.observableArrayList();
////
////
////                for (BookTM regbook : books) {
////                    if (regbook.getBookid().toLowerCase().contains(newValue.toLowerCase()) ||
////                            regbook.getTitle().toLowerCase().contains(newValue.toLowerCase()) ||
////                            regbook.getAuthor().toLowerCase().contains(newValue.toLowerCase())) {
////                        selectbook.add(regbook);
////
////                    }
////
////
////                }
////
////                tblViewBooks.setItems(selectbook);
////            }
////        });

//        txtFieldSearch.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                try {
//                    loadAllBooks();
//                } catch (SQLException e) {
//                    System.out.println("THIRD: "+e);
//                }
//            }
//        });

    }


    private void loadAllBooks() throws SQLException {
        tblViewBooks.getItems().clear();
        pstmForQuery.setString(1, "%" + txtFieldSearch.getText() + "%");
        pstmForQuery.setString(2, "%" + txtFieldSearch.getText() + "%");
        pstmForQuery.setString(3, "%" + txtFieldSearch.getText() + "%");
        pstmForQuery.setString(4, "%" + txtFieldSearch.getText() + "%");
        pstmForQuery.setString(5, "%" + txtFieldSearch.getText() + "%");

        // pstmForQuery.setDouble(4, Double.parseDouble("%" + txtFieldSearch.getText() + "%"));
        long t1 = System.currentTimeMillis();
        ResultSet rst = pstmForQuery.executeQuery();
        long t2 = System.currentTimeMillis();
        System.out.println("Time diff : " + (t2 - t1));

        ObservableList<BookTM> books = tblViewBooks.getItems();

        while (rst.next()) {
            String bookid = rst.getString(1);
            String title = rst.getString(2);
            String author = rst.getString(3);
            Double price = rst.getDouble(4);
            String status = rst.getString(5);


            BookTM book = new BookTM(bookid, title, author, price, status);
            books.add(book);
        }
        //tblViewBooks.setItems(books);
    }


    public void btnAdd_OnAction(ActionEvent actionEvent) throws SQLException {

        try {
            txtFieldSearch.setDisable(false);
            String title = txtFieldTitle.getText();
            String author = txtFieldAuthor.getText();
            String price = txtFieldPrice.getText();

            if (title.matches("^[A-Za-z\\s]{3,20}$")) {

                if (author.matches("^[A-Za-z\\s]{3,15}$")) {

                    if (price.matches("^[^0][0-9.]{1,8}$")) {


                        if (btnAdd.getText().equals("Add")) {
                            pstmForInsert.setString(1, txtFieldBookID.getText());
                            pstmForInsert.setString(2, txtFieldTitle.getText());
                            pstmForInsert.setString(3, txtFieldAuthor.getText());
                            pstmForInsert.setString(4, txtFieldPrice.getText());
                            pstmForInsert.setString(5, txtFieldStatus.getText());

                            if (pstmForInsert.executeUpdate() > 0) {

                                ObservableList obBookTable = tblViewBooks.getItems();
                                BookTM book = new BookTM(txtFieldBookID.getText(), txtFieldTitle.getText(), txtFieldAuthor.getText(), Double.parseDouble(txtFieldPrice.getText()), txtFieldStatus.getText());
                                obBookTable.add(book);
                                tblViewBooks.setItems(obBookTable);
                                tblViewBooks.refresh();

                                System.out.println("Loaded.");
                                txtFieldTitle.clear();
                                txtFieldAuthor.clear();
                                txtFieldPrice.clear();
                                txtFieldStatus.clear();
                                txtFieldBookID.clear();
                                txtFieldBookID.requestFocus();
                                txtFieldBookID.selectAll();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Failed to save").show();
                                txtFieldBookID.requestFocus();
                                txtFieldBookID.selectAll();
                            }

//                            ObservableList<BookTM> books = tblViewBooks.getItems();
//                            books.add(new BookTM(txtFieldBookID.getText(), txtFieldTitle.getText(),
//                                    txtFieldAuthor.getText(), Double.parseDouble(txtFieldPrice.getText()),
//                                    txtFieldStatus.getText()));
//                            btnNewBook_OnAction(actionEvent);
                        } else {

                            BookTM selectedbook = tblViewBooks.getSelectionModel().getSelectedItem();
                            selectedbook.setAuthor(txtFieldAuthor.getText());
                            selectedbook.setTitle(txtFieldTitle.getText());
                            selectedbook.setPrice(Double.parseDouble(txtFieldPrice.getText()));
                            selectedbook.setStatus(txtFieldStatus.getText());
                            tblViewBooks.refresh();

                           // String sql = "UPDATE book set bookid = ?, title = ? , author = ?,price =?, status=? where bookid = ?";
                            //pstmForQuery = connection.prepareStatement(sql);
                            psForUpdate.setString(1, txtFieldBookID.getText());
                            psForUpdate.setString(2, txtFieldTitle.getText());
                            psForUpdate.setString(3, txtFieldAuthor.getText());
                            psForUpdate.setDouble(4, Double.parseDouble(txtFieldPrice.getText()));
                            psForUpdate.setString(5, txtFieldStatus.getText());
                            psForUpdate.setString(6,txtFieldBookID.getText());


                            psForUpdate.executeUpdate();
                            System.out.println("Update Done.");


//                            BookTM selectedbook = tblViewBooks.getSelectionModel().getSelectedItem();
//                            selectedbook.setAuthor(txtFieldAuthor.getText());
//                            selectedbook.setTitle(txtFieldTitle.getText());
//                            selectedbook.setPrice(Double.parseDouble(txtFieldPrice.getText()));
//                            selectedbook.setStatus(txtFieldStatus.getText());
//                            tblViewBooks.refresh();
//                            btnNewBook_OnAction(actionEvent);


                        }


                    } else {


                        txtFieldPrice.requestFocus();
                        System.out.println("Please enter a valid price.");
                    }


                } else {
                    txtFieldAuthor.requestFocus();
                    System.out.println("Please enter a valid author.");
                }


            } else {

                txtFieldTitle.requestFocus();
                System.out.println("Please enter a valid title.");
            }


        } catch (NumberFormatException e) {
            System.out.println("You must set values for the text fields.");
        }

    }


    public void generateID() throws SQLException {

        try {
            int maxId = 0;
            ObservableList<String> bookId = FXCollections.observableArrayList();
            ResultSet resultSet = pstmForQuery.executeQuery();
            while (resultSet.next()) {
                String bid = resultSet.getString("bookid");
                bookId.add(bid);
                for (String s : bookId) {
                    int id = Integer.parseInt(s.replace("B", ""));
                    if (id > maxId) {
                        maxId = id;
                    }
                }
                maxId = maxId + 1;
                String id = "";
                if (maxId < 10) {
                    id = "B00" + maxId;
                } else if (maxId < 100) {
                    id = "B0" + maxId;
                } else {
                    id = "B" + maxId;
                }
                txtFieldBookID.setText(id);
            }
        } catch (SQLException e) {
            System.out.println("ONE: " + e);
        }


    }


    public void btnNewBook_OnAction(ActionEvent actionEvent) throws SQLException {

        //txtFieldSearch.setDisable(true);
        txtFieldSearch.clear();
        txtFieldStatus.clear();
        txtFieldPrice.clear();
        txtFieldAuthor.clear();
        txtFieldTitle.clear();
        txtFieldStatus.setText("Available");
        // txtFieldStatus.setText("Available");
        txtFieldAuthor.setDisable(false);
        txtFieldPrice.setDisable(false);
        //txtFieldSearch.setDisable(false);
        txtFieldStatus.setDisable(false);
        txtFieldTitle.setDisable(false);
        btnAdd.setDisable(false);
        generateID();

//        int maxId = 0;
//        for (BookTM books : DB.booklist) {
//            int id = Integer.parseInt(books.getBookid().replace("B", ""));
//            if (id > maxId) {
//                maxId = id;
//            }
//        }
//
//        maxId = maxId + 1;
//        String id = "";
//        if (maxId < 10) {
//            id = "B00" + maxId;
//        } else if (maxId < 100) {
//            id = "B0" + maxId;
//        } else {
//            id = "B" + maxId;
//        }
//        txtFieldBookID.setText(id);


    }

    public void btnDeleteBook_OnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this book?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            BookTM selectedItem = tblViewBooks.getSelectionModel().getSelectedItem();
            tblViewBooks.getItems().remove(selectedItem);
            try {
                //loadAllCustomers();
                //pstmForQuery = connection.prepareStatement("DELETE FROM customer WHERE id = ? ");
                psForDelete.setString(1, selectedItem.getBookid());
//                pstmForQuery.setString(2, selectedItem.getName());
//                pstmForQuery.setString(3, selectedItem.getAddress());
                boolean b = psForDelete.executeUpdate() > 0;
                if (b){
                    new Alert(Alert.AlertType.INFORMATION,"Customer Has Deleted..",ButtonType.OK).show();
                }else{
                    new Alert(Alert.AlertType.WARNING,"Try Again",ButtonType.OK).show();
                }

            } catch (Exception e) {
                System.out.println(e);
            }





        }
    }

    public void btnBacktoHome_OnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/Mainform.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.anchrPaneManageBooks.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
}
