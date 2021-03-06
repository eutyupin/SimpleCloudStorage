package ru.simplecloudstorage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.controllers.MainWindow;
import ru.simplecloudstorage.utils.ClientUserUtils;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.InformationDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

public class ClientHandler extends SimpleChannelInboundHandler<BaseCommand> {

    private final String ERROR = "Ошибка";
    private final String ERROR_TITLE = "Ошибка выполнения действия";

    private final String REGISTER = "Регистрация";
    private final String REGISTER_TITLE = "Регистрация прошла успешно!";
    private final String REGISTER_TEXT = "Попробуйте авторизоваться используя форму авторизации";
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private ClientApp application;
    private MainWindow mainWindow;
    private TreeItem<String> leftViewItems;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseCommand command) {
        checkCommands(command, channelHandlerContext);
    }

    private void checkCommands(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        checkAuthOkCommand(command, channelHandlerContext);
        checkServerFileListCommand(command, channelHandlerContext);
        checkRegisterOkCommand(command, channelHandlerContext);
        checkDownloadFileCommand(command, channelHandlerContext);
    }

    private void checkRegisterOkCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.REGISTER_OK)) {
            Platform.runLater(() -> {
            new InformationDialog(REGISTER, REGISTER_TITLE, REGISTER_TEXT);
            ClientApp.authDialogSetRoot(SceneName.AUTH_DIALOG.getValue(), SceneName.REGISTER_WINDOW.getValue());
            });
            logger.info(String.format("Command %s received. Registration OK.", command.getClass().getSimpleName()));
        }
        if (command.getType().equals(CommandType.REGISTER_FALIED)) {
            RegisterFailedCommand registerFailedCommand = (RegisterFailedCommand) command;
            logger.info(String.format("Command %s received. Registration failed with message %s",
                    registerFailedCommand.getClass().getSimpleName(), registerFailedCommand.getMessage()));
            Platform.runLater(() -> {
            new ErrorDialog(ERROR, ERROR_TITLE, registerFailedCommand.getMessage());
            });
        }
    }

    private void checkAuthOkCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.AUTH_OK)) {
            application.authDialogClose();
            logger.info(String.format("Command %s received. Authorization OK.", command.getClass().getSimpleName()));
        }
        if (command.getType().equals(CommandType.AUTH_FAILED)) {
            AuthFailedCommand authFailedCommand = (AuthFailedCommand) command;
            logger.info(String.format("Command %s received. Authorization failed with message %s",
                    authFailedCommand.getClass().getSimpleName(), authFailedCommand.getMessage()));
            Platform.runLater(() -> {
                new ErrorDialog(ERROR, ERROR_TITLE, authFailedCommand.getMessage());
                application.getAuthDialog().prepareFieldsForLogin();
            });
        }
    }

    private void checkServerFileListCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if(command.getType().equals(CommandType.SERVER_FILE_LIST)) {
            ServerFileListCommand serverFileListCommand = (ServerFileListCommand) command;
            getTreeItemFromList(serverFileListCommand.getFileList(), serverFileListCommand.getRootDirectory());
            application.getMainWindow().setLeftView(leftViewItems);
            logger.info(String.format("Command %s received. Server file list received.", serverFileListCommand.getClass().getSimpleName()));
        }
    }

    private void getTreeItemFromList(List<String> pathsList, String login) {
        leftViewItems = new TreeItem<>(login);
        leftViewItems.setGraphic(new ImageView(new Image("folder.png")));
        if (pathsList.size() == 0) pathsList.add("D:" + login);
        treeItemsAdd(pathsList);
    }

    private void treeItemsAdd(List<String> pathsList) {
        boolean isDir;
        for (String branch : pathsList) {
            if (branch.substring(0, 2).equals("D:")) isDir = true;
            else isDir = false;
            String[] folders = branch.substring(2).split(Pattern.quote(File.separator));
            TreeItem<String> parent = leftViewItems;
            for (int i = 0; i < folders.length; i++) {
                    TreeItem<String> found = null;
                for (TreeItem<String> child : parent.getChildren()) {
                    if (child.getValue().equals(folders[i])) {
                        found = child;
                    }
                }
                if (found == null) {
                    found = new TreeItem<>(folders[i]);
                    if(!isDir && i == folders.length - 1) found.setGraphic(new ImageView(new Image("file.png")));
                    else found.setGraphic(new ImageView(new Image("folder.png")));
                    parent.getChildren().add(found);
                }
                parent = found;
            }
        }
        leftViewItems.setExpanded(true);
    }

    private void checkDownloadFileCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        double percentage = 0.0;
        if (command.getType().equals(CommandType.DOWNLOAD_FILE)) {
            DownloadFileCommand downloadFileCommand = (DownloadFileCommand) command;
            String path = ClientUserUtils.checkDirectory(downloadFileCommand.getPath());
            path += downloadFileCommand.getFileName();
            try(RandomAccessFile downloadedFile = new RandomAccessFile(path, "rw")) {
                downloadedFile.seek(downloadFileCommand.getStartPosition());
                downloadedFile.write(downloadFileCommand.getContent());
                mainWindow.getDownloadButton().setDisable(true);
                mainWindow.getUploadButton().setDisable(true);
                percentage = (double) downloadFileCommand.getStartPosition() / (double) downloadFileCommand.getTotalFileLength();
                mainWindow.getProgressBar().setProgress(percentage);
            } catch (IOException e) {
                logger.error(e.getMessage());
                Platform.runLater(() -> {
                    new ErrorDialog("Ошибка", "Ошибка скачивания файла", e.getMessage());
                });
            }
            if (downloadFileCommand.isEndOfFile()) {
                mainWindow.getDownloadButton().setDisable(false);
                mainWindow.getUploadButton().setDisable(false);
                mainWindow.getProgressBar().setProgress(0);
                application.getMainWindow().setRightView();
                logger.info(String.format("Command %s received. Total %d bytes received",
                        downloadFileCommand.getClass().getSimpleName(), downloadFileCommand.getTotalFileLength()));
            }
        }
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void setApplication(ClientApp application) {
        this.application = application;
    }

}
