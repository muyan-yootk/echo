package cn.mldn.http.server.component;

import cn.mldn.http.server.component.impl.DefaultHttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现一个 session操作的并发管理程序类
 */
public class HttpSessionManager {
    // 在一个Http服务器的进程之中，只允许存在有一个Session保存集合
    // 使用ConcurrentHashMap是因为这个子类会将集合进行分片保存，每一段的数据多线程同步，而不同段进行异步操作
    private static final Map<String, HttpSession> SESSION_MAP = new ConcurrentHashMap<String, HttpSession>();

    /**
     * 每当有用户连接的时候就需要创建一个SessionId的数据内容
     * @return sessionId
     */
    public static String createSession() {
        HttpSession session = new DefaultHttpSession() ;    // 获取了一个SessionId
        String sessionId = session.getId() ; // HttpSession实现接口
        SESSION_MAP.put(sessionId,session) ;// 实现数据集合保存
        return sessionId ;
    }

    /**
     * 判断当前的SessionId是否存在于集合之中
     * @param sessionId
     * @return
     */
    public static boolean isExists(String sessionId) {
        if(SESSION_MAP.containsKey(sessionId)) {
            HttpSession session = SESSION_MAP.get(sessionId) ;
            if (session.getId() == null) {  // 该Session已经被销毁了
                SESSION_MAP.remove(sessionId) ;
                return false ;
            }
            return true ;
        } else {
            return false ;
        }
    }
    public static void invalidate(String sessionId) {
        SESSION_MAP.remove(sessionId) ;
    }
    public static HttpSession getSession(String sessionId) {
        return SESSION_MAP.get(sessionId) ;
    }
}
