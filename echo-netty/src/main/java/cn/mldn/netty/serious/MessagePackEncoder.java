package cn.mldn.netty.serious;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class MessagePackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        MessagePack msgPack = new MessagePack() ;
        byte [] raw = msgPack.write(msg) ; // 进行对象的编码操作
        byteBuf.writeBytes(raw) ;
    }
}
