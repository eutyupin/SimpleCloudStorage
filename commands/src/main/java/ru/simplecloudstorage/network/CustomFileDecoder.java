package ru.simplecloudstorage.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import ru.simplecloudstorage.commands.BaseCommand;

import java.util.List;

public class CustomFileDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> outList) throws Exception {
        BaseCommand command = OBJECT_MAPPER.readValue(ByteBufUtil.getBytes(buffer), BaseCommand.class);
        outList.add(command);
    }
}
