package ru.simplecloudstorage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.InformationDialog;
import ru.simplecloudstorage.utils.SceneName;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ClientHandler extends SimpleChannelInboundHandler<BaseCommand> {

    private final String ERROR = "Ошибка";
    private final String ERROR_TITLE = "Ошибка выполнения действия";

    private final String REGISTER = "Регистрация";
    private final String REGISTER_TITLE = "Регистрация прошла успешно!";
    private final String REGISTER_TEXT = "Попробуйте авторизоваться используя форму авторизации";

    private String downloadingClientPath;
    private String downloadingFileName;
    private ClientApp application;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseCommand command) throws Exception {
        checkCommands(command, channelHandlerContext);
    }

    private void checkCommands(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        checkAuthOkCommand(command, channelHandlerContext);
        checkRegisterOkCommand(command, channelHandlerContext);
        checkDownloadFileCommand(command, channelHandlerContext);
    }

    private void checkRegisterOkCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.REGISTER_OK)) {
            Platform.runLater(() -> {
            new InformationDialog(REGISTER, REGISTER_TITLE, REGISTER_TEXT);
            });
            ClientApp.authDialogSetRoot(SceneName.AUTH_DIALOG.getValue(), SceneName.REGISTER_WINDOW.getValue());
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
