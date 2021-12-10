package ru.simplecloudstorage.client;

import io.netty.channel.Channel;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.commands.DeleteCommand;
import ru.simplecloudstorage.commands.DownloadRequestCommand;
import ru.simplecloudstorage.commands.UploadFileCommand;
import ru.simplecloudstorage.controllers.MainWindow;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientDownloader {
    private final Channel clientChannel;
    private final ExecutorService threadPool;
    private static final int BUFFER_SIZE = 64 * 1024;
    private MainWindow mainWindow;

    public ClientDownloader(Channel clientChannel) {
        this.clientChannel = clientChannel;
        threadPool = Executors.newCachedThreadPool();
    }

    public void fileUploadToServer(String path, String destinationPath) {
        threadPool.execute(() -> fileUploadProcess(path, destinationPath));
    }

    public void fileDownloadFromServer(String path, String destinationPath) {
        DownloadRequestCommand downloadRequestCommand = new DownloadRequestCommand();
        downloadRequestCommand.setPath(path);
        downloadRequestCommand.setDestinationPath(destinationPath);
        clientChannel.writeAndFlush(downloadRequestCommand);
    }

    private void fileUploadProcess(String path, String destinationPath) {
        double percentage = 0.0;
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
                UploadFileCommand uploadFileCommand = new UploadFileCommand();
                uploadFileCommand.setTotalFileLength(fileLength);
                uploadFileCommand.setStartPosition(position);
                uploadFileCommand.setContent(bytes);
                uploadFileCommand.setFilePath(destinationPath);
                uploadFileCommand.setEndOfFile(endOfFile);
                clientChannel.writeAndFlush(uploadFileCommand).sync();
                percentage = (double) position / (double) fileLength;
                mainWindow.progressBar.setProgress(percentage);
                mainWindow.downloadButton.setDisable(true);
                mainWindow.uploadButton.setDisable(true);
            } while (uploadedFile.getFilePointer() < uploadedFile.length());
            mainWindow.downloadButton.setDisable(false);
            mainWindow.uploadButton.setDisable(false);
            mainWindow.progressBar.setProgress(0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteOnServerProcess(String path) {
        DeleteCommand deleteCommand = new DeleteCommand();
        deleteCommand.setDestinationPath(path);
        clientChannel.writeAndFlush(deleteCommand);
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }
}
