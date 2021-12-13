package ru.simplecloudstorage.client;

import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.simplecloudstorage.commands.DeleteCommand;
import ru.simplecloudstorage.commands.DownloadRequestCommand;
import ru.simplecloudstorage.commands.NewDirectoryCommand;
import ru.simplecloudstorage.commands.UploadFileCommand;
import ru.simplecloudstorage.controllers.MainWindow;
import ru.simplecloudstorage.utils.ClientUserUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSender {
    private final Channel clientChannel;
    private final ExecutorService threadPool;
    private static final int BUFFER_SIZE = 256 * 1024;
    private MainWindow mainWindow;
    private static final Logger logger = LogManager.getLogger(ClientSender.class);

    public ClientSender(Channel clientChannel) {
        this.clientChannel = clientChannel;
        threadPool = Executors.newCachedThreadPool();
    }

    public void newDirectoryCreateOnServer(String path) {
        NewDirectoryCommand newDirectoryCommand = new NewDirectoryCommand();
        newDirectoryCommand.setPath(path);
        clientChannel.writeAndFlush(newDirectoryCommand);
    }

    public void fileUploadToServer(String path, String destinationPath, String fileName) {
        threadPool.execute(() -> fileUploadProcess(path, destinationPath, fileName));
    }

    public void fileDownloadFromServer(String path, String destinationPath) {
        DownloadRequestCommand downloadRequestCommand = new DownloadRequestCommand();
        downloadRequestCommand.setServerPath(path);
        downloadRequestCommand.setDestinationPath(ClientUserUtils.checkDirectory(destinationPath));
        clientChannel.writeAndFlush(downloadRequestCommand);
    }

    private void fileUploadProcess(String path, String destinationPath, String fileName) {
        double percentage = 0.0;
        UploadFileCommand uploadFileCommand = new UploadFileCommand();
        try (RandomAccessFile uploadedFile = new RandomAccessFile(path, "r")) {
            long fileLength = uploadedFile.length();
            do {
                long position = uploadedFile.getFilePointer();
                long availableBytes = fileLength - position;
                byte[] bytes;
                boolean endOfFile = false;

                if (availableBytes >= BUFFER_SIZE) {
                    bytes = new byte[BUFFER_SIZE];
                } else {
                    bytes = new byte[(int) availableBytes];
                    endOfFile = true;
                }
                uploadedFile.read(bytes);
                uploadFileCommand.setTotalFileLength(fileLength);
                uploadFileCommand.setStartPosition(position);
                uploadFileCommand.setContent(bytes);
                uploadFileCommand.setPath(destinationPath);
                uploadFileCommand.setFileName(fileName);
                uploadFileCommand.setEndOfFile(endOfFile);
                clientChannel.writeAndFlush(uploadFileCommand).sync();
                percentage = (double) position / (double) fileLength;
                mainWindow.getProgressBar().setProgress(percentage);
                mainWindow.getDownloadButton().setDisable(true);
                mainWindow.getUploadButton().setDisable(true);
            } while (uploadedFile.getFilePointer() < uploadedFile.length());
            mainWindow.getDownloadButton().setDisable(false);
            mainWindow.getUploadButton().setDisable(false);
            mainWindow.getProgressBar().setProgress(0);
            logger.info(String.format("Command %s received. Total %d bytes sent to server",
                    uploadFileCommand.getClass().getSimpleName(), uploadFileCommand.getTotalFileLength()));
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void deleteOnServerProcess(String path) {
        DeleteCommand deleteCommand = new DeleteCommand();
        deleteCommand.setDestinationPath(path);
        clientChannel.writeAndFlush(deleteCommand);
        logger.info(String.format("File %s deleted on server", path));
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }
}
