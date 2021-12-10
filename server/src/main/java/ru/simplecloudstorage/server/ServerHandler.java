package ru.simplecloudstorage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.services.AuthorizeService;
import ru.simplecloudstorage.services.RegisterService;
import ru.simplecloudstorage.util.ServerUserUtils;

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

    private final String APP_ROOT_PATH = Paths.get("./").toAbsolutePath().normalize().toString() + File.separator;
    private final String APP_ROOT_URI = Paths.get("./").toUri().normalize().toString().substring(6);
    private final String DB_URL = "jdbc:sqlite:" + APP_ROOT_URI + "base.db";
    private static final int BUFFER_SIZE = 64 * 1024;
    private final Executor executor;
    private List<String> clientPathsList = new ArrayList<>();
    private Path clientPath;
    private String login;

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
    protected void channelRead0(ChannelHandlerContext ctx, BaseCommand command){
        checkCommands(command, ctx);
    }

    private void checkCommands(BaseCommand command, ChannelHandlerContext ctx) {
            checkDownloadRequestCommand(command, ctx);
            checkUploadFileCommand(command, ctx);
            checkAuthCommand(command, ctx);
            checkRegisterCommand(command, ctx);
            checkDeleteCommand(command, ctx);
            chekNewDirectoryCommand(command, ctx);
    }

    private void chekNewDirectoryCommand(BaseCommand command, ChannelHandlerContext ctx) {
        if (command.getType().equals(CommandType.NEW_DIRECTORY)) {
            NewDirectoryCommand newDirectoryCommand = (NewDirectoryCommand) command;
            Path newDirectory = Paths.get(APP_ROOT_PATH, newDirectoryCommand.getPath());
            if (ServerUserUtils.createNewDirectory(newDirectory)) {
                ctx.writeAndFlush(updateServerFileList(login));
            }
        }
    }

    private void checkDeleteCommand(BaseCommand command, ChannelHandlerContext ctx) {
        if (command.getType().equals(CommandType.DELETE)) {
            DeleteCommand deleteCommand = (DeleteCommand) command;
            Path deletePath = Paths.get(APP_ROOT_PATH, deleteCommand.getDestinationPath());
            if (ServerUserUtils.delete(deletePath)) {
                ctx.writeAndFlush(updateServerFileList(login));
            }

        }
    }

    private void checkAuthCommand(BaseCommand command, ChannelHandlerContext ctx) {
        if (command.getType().equals(CommandType.AUTH)) {
            AuthCommand authCommand = (AuthCommand) command;
            AuthorizeService authorizeService = new AuthorizeService();

            try {
                BaseCommand returnedCommand = authorizeService.tryAuthorize(authCommand.getLogin(),
                        authCommand.getPasswordHash(), DB_URL);
                ctx.writeAndFlush(returnedCommand);
                if (returnedCommand.getType().equals(CommandType.AUTH_OK)) {
                    login = authCommand.getLogin();
                    ctx.writeAndFlush(updateServerFileList(login));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private BaseCommand updateServerFileList(String login) {
        ServerFileListCommand serverFileListCommand = new ServerFileListCommand();
        clientPath = Paths.get(APP_ROOT_PATH + login);
        clientPathsList.clear();
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
                for (Path value : paths) {
                    source = value.toString();
                    index = source.indexOf(login) + login.length() + 1;
                    dir = login + File.separator + source.substring(index, source.length());
                    if (Files.isDirectory(value)) dir = "D:" + dir;
                    else dir = "F:" + dir;
                    clientPathsList.add(dir);
                    createClientPathsList(value, login);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void checkRegisterCommand(BaseCommand command, ChannelHandlerContext ctx) {
        if (command.getType().equals(CommandType.REGISTER)) {
            RegisterCommand registerCommand = (RegisterCommand) command;
            RegisterService registerService = new RegisterService();
            try {
                System.out.println("User: " + registerCommand.getLogin() + " trying register");
                ctx.writeAndFlush(registerService.tryRegister(registerCommand.getLogin(), registerCommand.getPasswordHash(),
                        registerCommand.getEmail(), DB_URL));

            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkUploadFileCommand(BaseCommand command, ChannelHandlerContext ctx) {
        if (command.getType().equals(CommandType.UPLOAD_FILE)) {
            UploadFileCommand uploadFileCommand = (UploadFileCommand) command;
            System.out.println("UploadFileCommand received" + System.lineSeparator() +
                    "Path to download: " + APP_ROOT_PATH + uploadFileCommand.getFilePath());
            String path = APP_ROOT_PATH + uploadFileCommand.getFilePath();
            try(RandomAccessFile uploadFile = new RandomAccessFile(path, "rw")) {
                uploadFile.seek(uploadFileCommand.getStartPosition());
                uploadFile.write(uploadFileCommand.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (uploadFileCommand.isEndOfFile()) {
                ctx.writeAndFlush(updateServerFileList(login));
            }
        }
    }

    private void checkDownloadRequestCommand(BaseCommand command, ChannelHandlerContext ctx) {
        if (command.getType().equals(CommandType.DOWNLOAD_REQUEST)) {
            DownloadRequestCommand downloadRequestCommand = (DownloadRequestCommand) command;
            executor.execute(() -> fileDownloadProcess(downloadRequestCommand, (ctx)));
        }
    }

    private void fileDownloadProcess(DownloadRequestCommand downloadRequestCommand, ChannelHandlerContext ctx) {
        String path = APP_ROOT_PATH + downloadRequestCommand.getPath();
        if(Files.isRegularFile(Path.of(path))) {
            try (RandomAccessFile requestedFile = new RandomAccessFile(path, "r")) {
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
                    downloadFileCommand.setDestinationPath(downloadRequestCommand.getDestinationPath() +
                            downloadRequestCommand.getPath().substring(downloadRequestCommand.getPath().lastIndexOf(File.separator),
                                    downloadRequestCommand.getPath().length()));
                    downloadFileCommand.setTotalFileLength(fileLength);
                    downloadFileCommand.setStartPosition(position);
                    downloadFileCommand.setContent(bytes);
                    downloadFileCommand.setEndOfFile(endOfFile);
                    ctx.writeAndFlush(downloadFileCommand).sync();
                } while (requestedFile.getFilePointer() < requestedFile.length());

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Exception: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
