package ru.simplecloudstorage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.InformationDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.regex.Pattern;

public class ClientHandler extends SimpleChannelInboundHandler<BaseCommand> {

    private final String ERROR = "Ошибка";
    private final String ERROR_TITLE = "Ошибка выполнения действия";

    private final String REGISTER = "Регистрация";
    private final String REGISTER_TITLE = "Регистрация прошла успешно!";
    private final String REGISTER_TEXT = "Попробуйте авторизоваться используя форму авторизации";

    private String downloadingClientPath;
    private String downloadingFileName;
    private ClientApp application;
    private TreeItem<String> leftViewItems;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseCommand command) throws Exception {
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
        }
        if (command.getType().equals(CommandType.REGISTER_FALIED)) {
            RegisterFailedCommand registerFailedCommand = (RegisterFailedCommand) command;
            Platform.runLater(() -> {
            new ErrorDialog(ERROR, ERROR_TITLE, registerFailedCommand.getMessage());
            });
        }
    }

    private void checkAuthOkCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.AUTH_OK)) {
            application.authDialogClose();
        }
        if (command.getType().equals(CommandType.AUTH_FAILED)) {
            AuthFailedCommand authFailedCommand = (AuthFailedCommand) command;
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
        }
    }

    private void getTreeItemFromList(List<String> pathsList, String login) {
        leftViewItems = new TreeItem<>(login);
        boolean isDir = false;
        treeItemsAdd(pathsList);
    }

    private void treeItemsAdd(List<String> pathsList) {
        boolean isDir;
        for (String branch : pathsList) {
            if (branch.substring(0, 2).equals("D:")) isDir = true;
            else isDir = false;
            String[] folders = branch.substring(2).split(Pattern.quote("\\"));
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
        if (command.getType().equals(CommandType.DOWNLOAD_FILE)) {
            DownloadFileCommand downloadFileCommand = new DownloadFileCommand();
            try(RandomAccessFile downloadedFile = new RandomAccessFile(downloadingClientPath + downloadingFileName, "rw")) {
                downloadedFile.seek(downloadFileCommand.getStartPosition());
                downloadedFile.write(downloadFileCommand.getContent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (downloadFileCommand.isEndOfFile()) channelHandlerContext.close();
        }
    }


    public void setApplication(ClientApp application) {
        this.application = application;
    }

    public String getDownloadingClientPath() {
        return downloadingClientPath;
    }

    public String getDownloadingFileName() {
        return downloadingFileName;
    }

    public void setDownloadingClientPath(String downloadingClientPath) {
        this.downloadingClientPath = downloadingClientPath;
    }

    public void setDownloadingFileName(String downloadingFileName) {
        this.downloadingFileName = downloadingFileName;
    }
}
