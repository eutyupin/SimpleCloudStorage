package ru.simplecloudstorage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.services.AuthorizeService;
import ru.simplecloudstorage.services.RegisterService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ServerHandler extends SimpleChannelInboundHandler<BaseCommand> {

    private final String APP_ROOT_PATH = Paths.get("./").toUri().normalize().toString().substring(6);
    private final String DB_URL = "jdbc:sqlite:" + APP_ROOT_PATH + "base.db";
    private static final int BUFFER_SIZE = 64 * 1024;
    private final Executor executor;
    private List<String> clientPathsList = new ArrayList<>();
    private Path clientPath;

    public ServerHandler(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("Client trying to connect... Waiting authorization...");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        System.out.println("Client disconnected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseCommand command){
        checkCommands(command, channelHandlerContext);
    }

    private void checkCommands(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
            checkDownloadRequestCommand(command, channelHandlerContext);
            checkUploadFileCommand(command, channelHandlerContext);
            checkAuthCommand(command, channelHandlerContext);
            checkRegisterCommand(command, channelHandlerContext);
    }

    private void checkAuthCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.AUTH)) {
            AuthCommand authCommand = (AuthCommand) command;
            AuthorizeService authorizeService = new AuthorizeService();

            try {
                BaseCommand returnedCommand = authorizeService.tryAuthorize(authCommand.getLogin(),
                        authCommand.getPasswordHash(), DB_URL);
                channelHandlerContext.writeAndFlush(returnedCommand);
                if (returnedCommand.getType().equals(CommandType.AUTH_OK)) {
                    channelHandlerContext.writeAndFlush(updateServerFileList(authCommand.getLogin()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private BaseCommand updateServerFileList(String login) {
        ServerFileListCommand serverFileListCommand = new ServerFileListCommand();
        clientPath = Paths.get(APP_ROOT_PATH + login);
        createClientPathsList(clientPath, login);
        serverFileListCommand.setFileList(clientPathsList);
        serverFileListCommand.setRootDirectory(login);
        return serverFileListCommand;
    }

    private void createClientPathsList(Path path, String login) {
        if (path.toFile().isDirectory()) {
            try {
                List<Path> paths = Files.list(path)
                        .sorted(Comparator.comparing((Path p) -> !p.toFile().isDirectory()).thenComparing(Path::getFileName))
                        .collect(Collectors.toList());
                String source = "";
                int index = 0;
                String dir = "";
                for (int i = 0; i < paths.size(); i++) {
                    source = paths.get(i).toString();
                    index = source.indexOf(login) + login.length() + 1;
                    dir = source.substring(index, source.length());
                    if(Files.isDirectory(paths.get(i))) dir = "D:" + dir;
                    else dir = "F:" + dir;
                    clientPathsList.add(dir);
                    createClientPathsList(paths.get(i), login);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void checkRegisterCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.REGISTER)) {
            RegisterCommand registerCommand = (RegisterCommand) command;
            RegisterService registerService = new RegisterService();
            try {
                System.out.println("User: " + registerCommand.getLogin() + " trying register");
                channelHandlerContext.writeAndFlush(registerService.tryRegister(registerCommand.getLogin(), registerCommand.getPasswordHash(),
                        registerCommand.getEmail(), DB_URL));

            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkUploadFileCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.UPLOAD_FILE)) {
            UploadFileCommand uploadFileCommand = (UploadFileCommand) command;
            try(RandomAccessFile uploadFile = new RandomAccessFile(uploadFileCommand.getFilePath(), "rw")) {
                uploadFile.seek(uploadFileCommand.getStartPosition());
                uploadFile.write(uploadFileCommand.getContent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (uploadFileCommand.isEndOfFile()) channelHandlerContext.close();
        }
    }

    private void checkDownloadRequestCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.DOWNLOAD_REQUEST)) {
            DownloadRequestCommand downloadRequestCommand = (DownloadRequestCommand) command;
            executor.execute(() -> fileDownloadProcess(downloadRequestCommand, (channelHandlerContext)));
        }
    }

    private void fileDownloadProcess(DownloadRequestCommand downloadRequestCommand, ChannelHandlerContext channelHandlerContext) {
        try (RandomAccessFile requestedFile = new RandomAccessFile(downloadRequestCommand.getPath(), "r")) {
            long fileLength = requestedFile.length();
            do {
                long position = requestedFile.getFilePointer();
                long availableBytes = fileLength - position;
                byte[] bytes;
                boolean endOfFile = false;

                if (availableBytes >= BUFFER_SIZE) {
                    bytes = new byte[BUFFER_SIZE];
                } else {
                    bytes = new byte[(int) availableBytes];
                    endOfFile = true;
                }
                requestedFile.read(bytes);
                DownloadFileCommand downloadFileCommand = new DownloadFileCommand();
                downloadFileCommand.setTotalFileLength(fileLength);
                downloadFileCommand.setStartPosition(position);
                downloadFileCommand.setContent(bytes);
                downloadFileCommand.setEndOfFile(endOfFile);
                channelHandlerContext.writeAndFlush(downloadFileCommand).sync();
            } while (requestedFile.getFilePointer() < requestedFile.length());

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
//        System.out.println("Exception: " + cause.getMessage());
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
