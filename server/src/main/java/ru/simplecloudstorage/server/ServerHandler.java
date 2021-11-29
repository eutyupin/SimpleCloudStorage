package ru.simplecloudstorage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.simplecloudstorage.commands.BaseCommand;
import ru.simplecloudstorage.commands.DownloadFileCommand;
import ru.simplecloudstorage.commands.DownloadRequestCommand;
import ru.simplecloudstorage.commands.UploadFileCommand;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ServerHandler extends SimpleChannelInboundHandler<BaseCommand> {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected");
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
        if (command instanceof DownloadRequestCommand) {
            DownloadRequestCommand downloadRequestCommand = (DownloadRequestCommand) command;
            try (RandomAccessFile requestedFile = new RandomAccessFile(downloadRequestCommand.getPath(), "r")) {
                final DownloadFileCommand downloadFileCommand = new DownloadFileCommand();
                byte[] content = new byte[(int) requestedFile.length()];
                requestedFile.read(content);
                downloadFileCommand.setContent(content);
                channelHandlerContext.writeAndFlush(downloadFileCommand);
                System.out.println("File " + content.length + " bytes was transferred to client");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

            if (command instanceof UploadFileCommand) {
                try(RandomAccessFile uploadedFile = new RandomAccessFile("./serverFiles/clientData.txt", "rw")) {
                    byte[] uploadFileData = ((UploadFileCommand) command).getContent();
                    uploadedFile.write(uploadFileData);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception: " + cause.getMessage());
        ctx.close();
    }
}
