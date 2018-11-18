package cn.mldn.netty.serious;

import cn.mldn.vo.Member;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MessagePackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list) throws Exception {
        int len = msg.readableBytes(); // 获取读取的数据长度
        byte[] data = new byte[len]; // 准备读取数据的空间
        msg.getBytes(msg.readerIndex(), data, 0, len); // 读取数据
        MessagePack msgPack = new MessagePack() ;
        System.out.println(msgPack.read(data));
        // list.add(msgPack.read(data)) ;
        list.add(msgPack.read(data,msgPack.lookup(Member.class))) ;
    }
}
