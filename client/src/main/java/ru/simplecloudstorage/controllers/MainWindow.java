package ru.simplecloudstorage.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.client.ClientSender;
import ru.simplecloudstorage.utils.InformationDialog;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {

    private String serverPath;
    private String clientPath;
    private double pr = 0;
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button uploadButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button newFolderButton;
    @FXML
    private Button downloadButton;
    @FXML
    private ComboBox diskBox;
    @FXML
    private BorderPane mainPane;
    @FXML
    private TreeView leftView;
    @FXML
    private TreeView rightView;
    @FXML
    private HBox leftPane;
    @FXML
    private HBox rightPane;

    private ClientApp application;
    private ClientSender clientSender;
    private  List<String> excludeList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        diskBoxInit();
        excludeFoldersAdd();
        setImageToButtons();
    }

    private void setImageToButtons() {
        deleteButton.graphicProperty().setValue(new ImageView(new Image("delete.png")));
        newFolderButton.graphicProperty().setValue(new ImageView(new Image("add.png")));
        uploadButton.graphicProperty().setValue(new ImageView(new Image("p-c.png")));
        downloadButton.graphicProperty().setValue(new ImageView(new Image("c-p.png")));
    }

    public void rightViewInit() {
        Platform.runLater(() -> createRightViewTree(diskBox.getValue().toString()));
    }


    private void diskBoxInit() {
        diskBox.setItems(diskListCreate());
        diskBox.getSelectionModel().select(0);
    }

    private ObservableList diskListCreate() {
        ObservableList<String> disksList = FXCollections.observableArrayList();
        for (File path : File.listRoots()) {
            disksList.add(path.toString().substring(0,2));
        }
        return disksList;
    }

    @FXML
    private void paneZoom(ZoomEvent zoomEvent) {
        leftPane.setPrefWidth((mainPane.getWidth()/2) - 40);
        rightPane.setPrefWidth((mainPane.getWidth()/2) - 40);
    }

    @FXML
    private void uploadAction(ActionEvent actionEvent) {
        if (!clientPath.isEmpty() && !serverPath.isEmpty()) {
            if(Files.isRegularFile(Path.of(clientPath))) {
                clientSender.fileUploadToServer(clientPath, serverPath +
                        clientPath.substring(clientPath.lastIndexOf(File.separator),
                                clientPath.length()));
            }
        } else {
            Platform.runLater(() -> new InformationDialog("Не выполнено действие",
                    "Не выбраны точки копирования",
                    "Выберите место откуда и куда вы хотите произвести копирование " +
                            "и попробуйте еще раз!"));
        }
    }

    public void setApplication(ClientApp clientApp) {
        this.application = clientApp;
    }

    public void setDownloader(ClientSender clientSender) {
        this.clientSender = clientSender;
        clientSender.setMainWindow(this);
    }

    private void createRightViewTree(String disk) {
        File rootDisk = new File(disk + File.separator);
        TreeItem<String> rootPCItem = new TreeItem<>(rootDisk.getPath().toString());
        rightView.setShowRoot(false);
        try {
            fillFilesTree(rootDisk, rootPCItem);
        } catch (IOException e) {
           logger.error(e.getMessage());
        }
        rightView.setRoot(rootPCItem);
    }

    private void fillFilesTree(File target, TreeItem<String> item) throws IOException {

        if (target.isDirectory()) {
            String itemName;
            if (target.getName().length() == 0) itemName = diskBox.getValue().toString();
            else itemName = target.getName();
            TreeItem<String> treeItem = new TreeItem<>(itemName);
            treeItem.setGraphic(new ImageView(new Image("folder.png")));
            item.getChildren().add(treeItem);

            for (File element : target.listFiles()) {
                String tempName = element.getPath().toString().substring(3);
                boolean canAdd = false;
                for (String s : excludeList) {
                    if(tempName.equals(s) || tempName.endsWith(".sys") ||
                    tempName.endsWith(".tmp")) {
                        canAdd = false;
                        break;
                    } else canAdd = true;
                }
                if(canAdd) fillFilesTree(element,treeItem);
                else continue;
            }
        } else {
            TreeItem<String> treeItem = new TreeItem<>(target.getName());
            treeItem.setGraphic(new ImageView(new Image("file.png")));
            item.getChildren().add(treeItem);
        }
    }

    private void excludeFoldersAdd() {
        excludeList = new ArrayList<>();
        excludeList.add("$Recycle.Bin");
        excludeList.add("$WinREAgent");
        excludeList.add("Documents and Settings");
        excludeList.add("Program Files");
        excludeList.add("Program Files (x86)");
        excludeList.add("ProgramData");
        excludeList.add("MSOCache");
        excludeList.add("System Volume Information");
        excludeList.add("Users");
        excludeList.add("Windows");
    }

    public void setLeftView(TreeItem<String> tree) {
        leftView.setShowRoot(false);
        Platform.runLater(() -> leftView.setRoot(tree));
    }
    public void setRightView() {
        rightViewInit();
    }

    public void diskBoxAction(ActionEvent actionEvent) {
        rightViewInit();
    }

    public void diskBoxUpdate(MouseEvent mouseEvent) {
        diskBox.setItems(diskListCreate());
    }

    @FXML
    public void rightViewClicked(MouseEvent mouseEvent) {
        try {
            clientPath = getViewItemPath(rightView);
        } catch (Exception e) {
        }
    }

    @FXML
    public void leftViewClicked(MouseEvent mouseEvent) {
        try {
            serverPath = getViewItemPath(leftView);
        } catch (Exception e) {
        }
    }

    private String getViewItemPath(TreeView tree) {
        String tempPathToFile = "";
        String value = "";
            MultipleSelectionModel<TreeItem<String>> items = tree.getSelectionModel();
            TreeItem<String> treeItem = items.getSelectedItem();
            while (treeItem != tree.getRoot()) {
                value = treeItem.getValue();
                tempPathToFile = value + File.separator + tempPathToFile;
                treeItem = treeItem.getParent();
            }
        return tempPathToFile.substring(0, tempPathToFile.length()-1);
    }

    @FXML
    public void downloadAction(ActionEvent actionEvent) {
        clientSender.fileDownloadFromServer(serverPath, clientPath);
    }

    @FXML
    public void deleteAction(ActionEvent actionEvent) {
        if (leftView.getSelectionModel().getSelectedIndex() > 0) {
          Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
          confirmationDialog.setTitle("Удаление");
          confirmationDialog.setHeaderText("Вы действительно хотите удалить элемент?");
          confirmationDialog.setContentText(serverPath);
            Optional<ButtonType> option = confirmationDialog.showAndWait();
            if (option.get() == ButtonType.OK) {
                clientSender.deleteOnServerProcess(serverPath);
            }
        }
    }

    public void sendNewFolderCommand(String folderName) {
        clientSender.newDirectoryCreateOnServer(serverPath + File.separator + folderName);
    }

    public void createNewFolder(ActionEvent actionEvent) {
        application.getNewFolderDialogStage().showAndWait();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Button getUploadButton() {
        return uploadButton;
    }

    public Button getNewFolderButton() {
        return newFolderButton;
    }

    public Button getDownloadButton() {
        return downloadButton;
    }
}
