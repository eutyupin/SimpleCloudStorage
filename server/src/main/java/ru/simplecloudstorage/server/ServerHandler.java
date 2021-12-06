package ru.simplecloudstorage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.services.AuthorizeService;
import ru.simplecloudstorage.services.RegisterService;

import java.nio.file.Paths;
import java.sql.SQLException;

public class ServerHandler extends SimpleChannelInboundHandler<BaseCommand> {

    private final String DB_URL = "jdbc:sqlite:" + Paths.get("./").toUri().normalize().toString().substring(6) + "base.db";

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client trying to connect... Waiting authorization...");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
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
                channelHandlerContext.writeAndFlush(authorizeService.tryAuthorize(authCommand.getLogin(),
                        authCommand.getPasswordHash(), DB_URL));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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

            }catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void checkUploadFileCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
//        if (command.getType().equals(CommandType.UPLOAD_FILE)) {
//            try(RandomAccessFile uploadedFile = new RandomAccessFile("./serverFiles/clientData.txt", "rw")) {
//                byte[] uploadFileData = ((UploadFileCommand) command).getContent();
//                uploadedFile.write(uploadFileData);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void checkDownloadRequestCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
//        if (command.getType().equals(CommandType.DOWNLOAD_REQUEST)) {
//            DownloadRequestCommand downloadRequestCommand = (DownloadRequestCommand) command;
//            try (RandomAccessFile requestedFile = new RandomAccessFile(downloadRequestCommand.getPath(), "r")) {
//                final DownloadFileCommand downloadFileCommand = new DownloadFileCommand();
//                byte[] content = new byte[(int) requestedFile.length()];
//                requestedFile.read(content);
//                downloadFileCommand.setContent(content);
//                channelHandlerContext.writeAndFlush(downloadFileCommand);
//                System.out.println("File " + content.length + " bytes was transferred to client");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception: " + cause.getMessage());
        ctx.close();
    }
}
