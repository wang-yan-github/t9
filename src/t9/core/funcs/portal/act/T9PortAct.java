package t9.core.funcs.portal.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.notify.logic.T9NotifyManageLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.portal.data.T9Port;
import t9.core.funcs.portal.logic.T9PortalLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.mobile.util.T9MobileUtility;

public class T9PortAct {
    private String sp      = System.getProperty("file.separator");
    private String webPath = "core" + sp + "funcs" + sp + "portal" + sp
                                   + "modules" + sp;

    public String addPort(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;

        try {
            T9FileUploadForm fileForm = new T9FileUploadForm();
            fileForm.parseUploadRequest(request);
            T9Person person = (T9Person) request.getSession().getAttribute(
                    "LOGIN_USER");
            Map paramMap = fileForm.getParamMap();
            String seqId = fileForm.getParameter("seqId");
            String subject = fileForm.getParameter("subject");
            String nickname = fileForm.getParameter("nickname");
            String toId = fileForm.getParameter("toId");
            String userId = fileForm.getParameter("userId");
            String privId = fileForm.getParameter("privId");
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            T9PortalLogic logic = new T9PortalLogic();
            logic.savePorta(dbConn, seqId, subject, toId, userId, privId,nickname, person);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            request.setAttribute(T9ActionKeys.FORWARD_PATH,
                    "/core/inc/error.jsp");
            throw ex;
        }
        request.setAttribute(T9ActionKeys.RET_METHOD,
                T9ActionKeys.RET_METHOD_REDIRECT);
        return "/core/funcs/portal/portlet/list.jsp";
    }

    public void unzip(File zipPath, String filePath) throws IOException {
        try {
            ZipFile zipFile = new ZipFile(zipPath, "GBK");
            Enumeration e = zipFile.getEntries();
            ZipEntry ze = null;
            File folder = new File(filePath);
            folder.mkdir();
            while (e.hasMoreElements()) {
                ze = (ZipEntry) e.nextElement();
                if (ze.isDirectory()) {
                    new File(filePath + ze.getName()).mkdir();
                } else {
                    InputStream is = zipFile.getInputStream(ze);
                    String name = ze.getName();
                    // File f = new File(filePath + new
                    // String(ze.getName().getBytes("ISO8859_1"), "gbk"));
                    File f = new File(filePath + ze.getName());
                    FileOutputStream fout = new FileOutputStream(f);
                    byte[] b = new byte[1024];
                    int i = 0;
                    while ((i = is.read(b)) > 0) {
                        fout.write(b, 0, i);
                    }
                    is.close();
                    fout.flush();
                    fout.close();
                }
            }
            zipFile.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    public void zip(ZipOutputStream out, File file) throws Exception {
        try {
            byte[] buf = new byte[1024];
            if (file.isDirectory()) {
                zip(out, file.listFiles());
            } else {
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new org.apache.tools.zip.ZipEntry(file
                        .getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.flush();
        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            throw e;
        }
    }

    public void zip(ZipOutputStream out, List<String> filePath)
            throws Exception {
        for (String s : filePath) {
            File file = new File(s);
            zip(out, file);
        }
    }

    public void zip(ZipOutputStream out, File[] files) throws Exception {
        for (File file : files) {
            zip(out, file);
        }
    }

    public void zip(String zipFile, List<String> files) throws Exception {
        FileOutputStream fos = new FileOutputStream(zipFile);
        zip(fos, files);
    }

    public void zip(OutputStream fos, List<String> files) throws Exception {
        ZipOutputStream out = new ZipOutputStream(fos);
        out.setEncoding("GBK");
        zip(out, files);
        out.close();
    }

    /**
     * 导入模块
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String importPorts(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        int amount = 0;
        int status = 0;
        try {
            Connection dbConn = null;
            T9FileUploadForm fileForm = new T9FileUploadForm();
            fileForm.parseUploadRequest(request);

            InputStream is = fileForm.getInputStream((String) fileForm
                    .iterateFileFields().next());

            String type = fileForm.getParameter("type");
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String name = df.format(new Date());

            String zipPath = request.getSession().getServletContext()
                    .getRealPath(sp)
                    + webPath + "zip" + sp;

            File zipFile = new File(zipPath + name + ".zip");
            FileOutputStream fos = new FileOutputStream(zipFile);
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = is.read(b)) > 0) {
                fos.write(b, 0, i);
            }
            is.close();
            fos.flush();
            fos.close();
            unzip(zipFile, zipPath + name + sp);
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            File zipFolder = new File(zipPath + name);
            T9PortalLogic logic = new T9PortalLogic();

            String portPath = request.getSession().getServletContext()
                    .getRealPath(sp)
                    + webPath;
            for (File f : zipFolder.listFiles()) {
                if (logic.existPort(dbConn, f.getName())) {
                    f.delete();
                    continue;
                }
                T9Port port = new T9Port();
                port.setFileName(f.getName());
                port.setDeptId("0");
                logic.newPort(dbConn, port);
                FileOutputStream o = new FileOutputStream(portPath
                        + f.getName());
                FileInputStream in = new FileInputStream(zipPath + name + sp
                        + f.getName());

                int j = 0;
                while ((j = in.read(b)) > 0) {
                    o.write(b, 0, j);
                }
                in.close();
                o.flush();
                o.close();
                f.delete();
                amount++;
            }
            zipFolder.delete();
            zipFile.delete();
        } catch (Exception ex) {
            status = 1;
        }
        return "/core/funcs/portal/importsuccess.jsp?status=" + status
                + "&amount=" + amount;
    }

    /**
     * 导出模块
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String exportPorts(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        String ports = request.getParameter("portsStr");
        try {
            if (T9Utility.isNullorEmpty(ports)) {
                return "/core/inc/rtjson.jsp";
            }
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            T9PortalLogic logic = new T9PortalLogic();
            List<String> files = new ArrayList<String>();
            String portPath = request.getSession().getServletContext()
                    .getRealPath(sp)
                    + webPath;

            for (String port : ports.split(",")) {
                T9Port p = null;
                try {
                    p = logic.queryPort(dbConn, Integer.parseInt(port));
                } catch (NumberFormatException e) {
                    continue;
                }
                if (p == null) {
                    continue;
                }
                files.add(portPath + p.getFileName());
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
            response.setHeader("Content-disposition",
                    "attachment; filename=export-" + df.format(new Date())
                            + ".zip");
            response.setHeader("Cache-Control",
                    "must-revalidate, post-check=0, pre-check=0,private, max-age=0");
            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Type", "application/force-download");
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "maxage=3600");
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control",
                    "must-revalidate, post-check=0, pre-check=0");
            OutputStream out = response.getOutputStream();
            zip(out, files);
            out.flush();
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "";
    }

    /**
     * 列出用户拥有权限的所有模块
     * 
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String listAllPorts(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            T9Person user = (T9Person) request.getSession().getAttribute(
                    "LOGIN_USER");// 获得登陆用户

            String sql = "select SEQ_ID, FILE_NAME, VIEW_TYPE, MODULE_POS,TYPE, USER_ID, DEPT_ID, PRIV_ID from PORT order by SEQ_ID";

            T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM
                    .build(request.getParameterMap());
            T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,
                    queryParam, sql);

            PrintWriter pw = response.getWriter();
            pw.println(pageDataList.toJson());
            pw.flush();

            return null;
        } catch (Exception e) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
            throw e;
        }
    }

    /**
     * 模版详情
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getPort(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        T9Person person = (T9Person) request.getSession().getAttribute(
                "LOGIN_USER");
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String seqId = request.getParameter("seqId");
            int seq =0;
            if(!"".equals(seqId) &&seqId!=null){
                seq = Integer.parseInt(seqId);
            }
            T9PortalLogic logic = new T9PortalLogic();
            List data = logic.getModData(dbConn, seq);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, T9MobileUtility.list2Json(data));
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 删除模块
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String delete(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {

            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            String ports = request.getParameter("ports");

            T9PortalLogic logic = new T9PortalLogic();

            for (String port : ports.split(",")) {
                try {
                    logic.deletePort(dbConn, Integer.parseInt(port));
                } catch (NumberFormatException e) {
                    continue;
                }
            }

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }
}