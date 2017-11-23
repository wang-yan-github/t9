package t9.pda2.login.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.global.T9BeanKeys;
import t9.core.util.db.T9ORM;
import t9.pda2.login.logic.T9PdaSystemLoginLogic;

public class T9PdaLoginAct {

  @SuppressWarnings("unchecked")
  public void doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String username = (String)request.getParameter("username");
      
      T9PdaSystemLoginLogic logic = new T9PdaSystemLoginLogic();

      //验证用户否存在      if(!logic.validateUser(dbConn , username)){
        request.setAttribute("errorMsg", "用户不存在");
        request.setAttribute("errorNo", "-1");
        request.setAttribute("username", username);
        request.getRequestDispatcher("/pda2/index.jsp").forward(request, response);
        return;
      }   
      
      T9Person person = null;
      try{
        T9ORM orm = new T9ORM();
        String[] filters = new String[]{"USER_ID = '" + username + "' or BYNAME = '" + username + "'"};
        List<T9Person> list = orm.loadListSingle(dbConn, T9Person.class, filters);
        if (list.size() > 0){
          person = list.get(0);
        }
      }catch(Exception ex) {
        throw ex;
      }
      
      //验证密码
      T9PasswordValidator passwordValidator = new T9PasswordValidator(request.getParameter("pwd"));
      if(!passwordValidator.isValid(request, person, dbConn)){
        request.setAttribute("errorMsg", "密码错误");
        request.setAttribute("errorNo", "-2");
        request.setAttribute("username", username);
        request.getRequestDispatcher("/pda2/index.jsp").forward(request, response);
        return;
      }
      
      this.loginSuccess(dbConn , person, request, response);
      request.getSession().setAttribute("P_VER", (String)request.getParameter("P_VER"));
      
    } catch (Exception ex) {
      request.setAttribute("errorMsg", "登录失败");
      throw ex;
    }
    request.getRequestDispatcher("/pda2/main.jsp").forward(request, response);
  }
  
  
  /**
   * 登录成功的处理

   * @param conn
   * @param person
   * @param request
   * @throws Exception
   */
  private void loginSuccess(Connection conn, T9Person person , HttpServletRequest request, HttpServletResponse response) throws Exception{
    
    //获取用户当前的session,如果不存在就生成一个新的session
    HttpSession session = request.getSession(true);
    //判断用户是否已经登录
    if (session.getAttribute("LOGIN_USER") == null){
      T9SystemAct logic = new T9SystemAct();
      logic.setUserInfoInSession(person, session, request.getRemoteAddr(), request);
      session.setAttribute("LOGIN_USER", person);
      session.setAttribute("ATTACH_LOCK_REF_SEC", 0l);
    }
    else {
      T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
      
      //如果是新用户登录时,销毁原有的session
      if (loginPerson.getSeqId() != person.getSeqId()) {
        
        //销毁session
        session.invalidate();
        
        //重新调用登录成功的处理
        loginSuccess(conn, person, request, response);
      }
    }
  }
}
