package cn.mldn.msgpack;

import cn.mldn.vo.Member;
import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.util.ArrayList;
import java.util.List;

public class MessagePackDemoA {
    public static void main(String[] args) throws Exception {
        List<Member> allMembers = new ArrayList<Member>() ;
        for(int x = 0 ; x < 10 ; x ++) {
            Member member = new Member() ;
            member.setMid("MLDN - " + x);
            member.setName("Hello - " + x);
            member.setAge(10);
            member.setSalary(1.1);
            allMembers.add(member) ;
        }
        MessagePack msgPack = new MessagePack() ;
        byte data [] = msgPack.write(allMembers) ;
        System.out.println(data.length);
        {   // 将数据解析回来
            List<Member> all = msgPack.read(data, Templates.tList(msgPack.lookup(Member.class))) ;
            System.out.println(all);
        }
    }
}
