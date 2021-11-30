package ru.simplecloudstorage.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ru.simplecloudstorage.commands.BaseCommand;

public class CustomFileEncoder extends MessageToByteEncoder<BaseCommand> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseCommand command, ByteBuf outBuf) throws Exception {
        byte[] val = OBJECT_MAPPER.writeValueAsBytes(command);
        outBuf.writeBytes(val);
    }
}
