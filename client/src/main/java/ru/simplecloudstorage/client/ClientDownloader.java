package ru.simplecloudstorage.client;

import io.netty.channel.Channel;
import ru.simplecloudstorage.commands.DownloadRequestCommand;
import ru.simplecloudstorage.commands.UploadFileCommand;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientDownloader {
    private final Channel clientChannel;
    private final ExecutorService threadPool;
    private static final int BUFFER_SIZE = 64 * 1024;

    public ClientDownloader(Channel clientChannel) {
        this.clientChannel = clientChannel;
        threadPool = Executors.newCachedThreadPool();
    }

    private void fileUploadToServer(String path, String destinationPath) throws IOException {
        threadPool.execute(() -> fileUploadProcess(path, destinationPath));
    }

    private void fileDownloadFromServer(String path) {
        DownloadRequestCommand downloadRequestCommand = new DownloadRequestCommand();
        downloadRequestCommand.setPath(path);
        clientChannel.writeAndFlush(downloadRequestCommand);
    }

    private void fileUploadProcess(String path, String destinationPath) {
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
            } while (uploadedFile.getFilePointer() < uploadedFile.length());

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
