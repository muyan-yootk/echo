package cn.mldn.netty.serious;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public class MarshallingCodeFactory {
    public static MarshallingDecoder buildMarshallingDecoder() {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial") ;  // 获取原始的JDK序列化
        MarshallingConfiguration configuration = new MarshallingConfiguration() ;
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory,configuration) ;
        int maxSize = 1024 << 2 ; // 设置单个对象的最大长度
        MarshallingDecoder decoder = new MarshallingDecoder(provider,maxSize) ;
        return decoder ;
    }
    public static MarshallingEncoder buildMarshallingEncoder() {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial") ;  // 获取原始的JDK序列化
        MarshallingConfiguration configuration = new MarshallingConfiguration() ;
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory,configuration) ;
        MarshallingEncoder encoder = new MarshallingEncoder(provider) ;
        return encoder ;
    }
}
