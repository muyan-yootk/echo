package cn.mldn.http.server.component;

/**
 * 与JavaWEB开发接口标准完全同步
 */
public interface HttpSession {
    public static final String SESSIONID = "XiaoLilLaoShiSessionId" ;
    public Object getAttribute(String name) ;
    public void setAttribute(String name,Object value) ;
    public void removeAttribute(String name) ;
    public String getId() ;
    public void invalidate() ;
}
