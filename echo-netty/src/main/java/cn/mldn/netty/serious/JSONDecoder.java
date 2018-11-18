package cn.mldn.netty.serious;

import cn.mldn.vo.Member;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class JSONDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list) throws Exception {
        int len = msg.readableBytes(); // 可以用的数据长度
        byte data[] = new byte[len];
        msg.getBytes(msg.readerIndex(), data, 0, len);
        list.add(JSON.parseObject(new String(data)).toJavaObject(Member.class)) ;
    }
}
