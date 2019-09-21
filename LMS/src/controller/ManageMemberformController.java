package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import util.MemberTM;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ManageMemberformController {
    public JFXTextField txtFieldMemberID;
    public JFXTextField txtFieldName;
    public JFXTextField txtFieldAddress;
    public TableView<MemberTM> tblViewMembers;
    public TableColumn<MemberTM, String> clmnMemberID;
    public TableColumn<MemberTM, String> clmnName;
    public TableColumn<MemberTM, String> clmnAddress;
    public AnchorPane anchrpane_ManageMems;
    public JFXButton btnNewMember;
    public JFXButton btnDelete;
    public JFXButton btnSave;
    public TextField txtField_Search;
    public JFXButton viewReport;
    private Connection connection;
    private PreparedStatement psForSelect;
    private PreparedStatement psForInsert;
    private PreparedStatement psForUpdate;
    private PreparedStatement psForDelete;

    public void initialize() throws ClassNotFoundException {

        txtFieldMemberID.setDisable(true);
        txtFieldAddress.setDisable(true);
        txtFieldName.setDisable(true);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);

//        ObservableList<MemberTM> members = FXCollections.observableList(DB.memberlist);
//        tblViewMembers.setItems(members);
        tblViewMembers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblViewMembers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblViewMembers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        try {
            connection = DBConnection.getInstance().getConnection();
            psForSelect = connection.prepareStatement("SELECT * FROM library_member WHERE memberid LIKE ? OR membername LIKE ? OR " +
                    "memberaddress LIKE ?");
            psForInsert = connection.prepareStatement("INSERT INTO library_member VALUES (?,?,?)");
            psForUpdate = connection.prepareStatement("UPDATE library_member SET membername=?,memberaddress=? WHERE memberid=?");
            psForDelete = connection.prepareStatement("DELETE FROM library_member WHERE memberid=?");
            loadAllMembers();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        tblViewMembers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MemberTM>() {
            @Override
            public void changed(ObservableValue<? extends MemberTM> observable, MemberTM oldValue, MemberTM newValue) {
                MemberTM selecteditem = tblViewMembers.getSelectionModel().getSelectedItem();
                if (selecteditem == null) {
                    btnSave.setText("Save");
                    btnDelete.setDisable(true);
                    return;

                }

                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
                txtFieldMemberID.setText(selecteditem.getId());
                txtFieldAddress.setText(selecteditem.getAddress());
                txtFieldName.setText(selecteditem.getName());
                txtFieldMemberID.setDisable(false);
                txtFieldName.setDisable(false);
                txtFieldAddress.setDisable(false);


            }
        });

        txtField_Search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    loadAllMembers();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void loadAllMembers() throws SQLException {
        tblViewMembers.getItems().clear();
        psForSelect.setString(1, "%" + txtField_Search.getText() + "%");
        psForSelect.setString(2, "%" + txtField_Search.getText() + "%");
        psForSelect.setString(3, "%" + txtField_Search.getText() + "%");

        ResultSet resultSet = psForSelect.executeQuery();
        ObservableList<MemberTM> members = tblViewMembers.getItems();
        while (resultSet.next()) {
            String memberid = resultSet.getString("memberid");
            String membername = resultSet.getString("membername");
            String memberaddress = resultSet.getString("memberaddress");
            MemberTM memberTM = new MemberTM(memberid, membername, memberaddress);
            members.add(memberTM);


        }


    }


    public void btnNewMember_OnAction(ActionEvent actionEvent) throws SQLException {
        txtFieldName.clear();
        txtFieldAddress.clear();
        txtFieldMemberID.clear();
        tblViewMembers.getSelectionModel().clearSelection();
        txtFieldName.setDisable(false);
        txtFieldAddress.setDisable(false);
        btnDelete.setDisable(false);
        btnSave.setDisable(false);
        int maxId = 0;
        ObservableList<String> MemberId = FXCollections.observableArrayList();
        ResultSet resultSet = psForSelect.executeQuery();
        while (resultSet.next()) {
            String mid = resultSet.getString("memberid");
            MemberId.add(mid);
            for (String s : MemberId) {
                int id = Integer.parseInt(s.replace("M", ""));
                if (id > maxId) {
                    maxId = id;
                }
            }
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "M00" + maxId;
            } else if (maxId < 100) {
                id = "M0" + maxId;
            } else {
                id = "M" + maxId;
            }
            txtFieldMemberID.setText(id);
        }

//        int maxID = 0;
//        for (MemberTM member : DB.memberlist) {
//
//            int id = Integer.parseInt(member.getId().replace("M", ""));
//            if (id > maxID) {
//                maxID = id;
//            }
//        }
//
//        maxID = maxID + 1;
//        String id = "";
//        if (maxID < 10) {
//            id = "M00" + maxID;
//        } else if (maxID < 100) {
//            id = "M0" + maxID;
//        } else {
//            id = "M" + maxID;
//        }
//        txtFieldMemberID.setText(id);


    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {

        MemberTM selectedItem = tblViewMembers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            new Alert(Alert.AlertType.WARNING, "Please select one member.", ButtonType.OK).show();
            return;
        }


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this member?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {

            tblViewMembers.getItems().remove(selectedItem);
            try {

                psForDelete.setString(1, selectedItem.getId());

                boolean b = psForDelete.executeUpdate() > 0;
                if (b) {
                    new Alert(Alert.AlertType.INFORMATION, "Member Has Deleted..", ButtonType.OK).show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again", ButtonType.OK).show();
                }

            } catch (Exception e) {
                System.out.println(e);
            }


        }


    }

    public void btnSave_OnAction(ActionEvent actionEvent) throws SQLException {

        String name = txtFieldName.getText();
        String address = txtFieldAddress.getText();
        if (name.matches("^[A-Za-z\\s]{3,10}$")) {

            if (address.matches("\\b[A-Za-z,.\\s]+\\b")) {
                if (btnSave.getText().equals("Save")) {

                    psForInsert.setString(1, txtFieldMemberID.getText());
                    psForInsert.setString(2, txtFieldName.getText());
                    psForInsert.setString(3, txtFieldAddress.getText());

                    if (psForInsert.executeLargeUpdate() > 0) {
                        new Alert(Alert.AlertType.INFORMATION, "Member added!").showAndWait();
                        loadAllMembers();
                        btnNewMember_OnAction(actionEvent);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to add member!").showAndWait();
                    }


//                    ObservableList<MemberTM> memberTMS = tblViewMembers.getItems();
//                    memberTMS.add(new MemberTM(txtFieldMemberID.getText(),
//                            txtFieldName.getText(), txtFieldAddress.getText()));
//                    btnNewMember_OnAction(actionEvent);

                } else {
                    psForUpdate.setString(1, txtFieldName.getText());
                    psForUpdate.setString(2, txtFieldAddress.getText());
                    psForUpdate.setString(3, txtFieldMemberID.getText());


                    boolean updated = psForUpdate.executeUpdate() > 0;
                    if (updated) {
                        new Alert(Alert.AlertType.INFORMATION, "Updated successfully").show();
                        loadAllMembers();
                        tblViewMembers.refresh();
                        btnNewMember_OnAction(actionEvent);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update").show();
                    }
//                    MemberTM selectedmember = tblViewMembers.getSelectionModel().getSelectedItem();
//                    selectedmember.setName(txtFieldName.getText());
//                    selectedmember.setAddress(txtFieldAddress.getText());
//                    tblViewMembers.refresh();
//                    btnNewMember_OnAction(actionEvent);


                }

            } else {

                txtFieldAddress.requestFocus();
                new Alert(Alert.AlertType.ERROR, "Please enter a valid address.").showAndWait();
            }


        } else {
            txtFieldName.requestFocus();
            new Alert(Alert.AlertType.ERROR, "Please enter a valid name.").showAndWait();

        }

    }

    public void btnBacktoHome_OnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/Mainform.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.anchrpane_ManageMems.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void viewReport_OnAction(ActionEvent actionEvent) throws JRException {
        ObservableList<MemberTM> members = tblViewMembers.getItems();
        System.out.println(members);
        JasperDesign jasperDesign = JRXmlLoader.
                load(ManageMemberformController.class.
                        getResourceAsStream("/report/MemberJasper.jrxml"));


        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String, Object> params = new HashMap<>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                params, new JRBeanCollectionDataSource(members));

        JasperViewer.viewReport(jasperPrint, false);
    }
}
