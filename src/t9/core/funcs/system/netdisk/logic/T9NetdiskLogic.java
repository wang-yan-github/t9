package t9.core.funcs.system.netdisk.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.filefolder.logic.T9FileSortLogic;
import t9.core.funcs.system.netdisk.data.T9Netdisk;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;

public class T9NetdiskLogic {
	private static Logger log = Logger.getLogger("t9.core.funcs.system.netdisk.logic");

	/**
	 * 保存共享目录信息
	 * 
	 * @param dbConn
	 * @param picture
	 */
	public void saveNetFolderInfo(Connection dbConn, T9Netdisk picture) {
		T9ORM orm = new T9ORM();
		try {
			orm.saveSingle(dbConn, picture);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取共享目录信息
	 * 
	 * @param dbConn
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<T9Netdisk> getNetdiskFolderInfo(Connection dbConn) throws Exception {
		T9ORM orm = new T9ORM();
		return orm.loadListSingle(dbConn, T9Netdisk.class, new HashMap());

	}

	public List<T9Netdisk> getNetdiskFolderList(Connection dbConn, String[] filters) throws Exception {
		T9ORM orm = new T9ORM();
		// return orm.loadListSingle(dbConn, T9Netdisk.class, new HashMap());
		return orm.loadListSingle(dbConn, T9Netdisk.class, filters);

	}

	/**
	 * 加载编辑时获取信息
	 * 
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 */
	public List<T9Netdisk> getNetdisksInfo(Connection dbConn, int seqId) {
		List<T9Netdisk> list = new ArrayList<T9Netdisk>();
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select SEQ_ID" + ",DISK_NAME" + ",DISK_PATH" + ",NEW_USER" + ",MANAGE_USER" + ",USER_ID" + ",DISK_NO" + ",SPACE_LIMIT"
				+ ",ORDER_BY" + ",ASC_DESC" + ",DOWN_USER " + "from NETDISK where SEQ_ID !=" + seqId;
		try {
			stmt = dbConn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				T9Netdisk netdisk = new T9Netdisk();
				netdisk.setSeqId(rs.getInt("SEQ_ID"));
				netdisk.setDiskName(rs.getString("DISK_NAME"));
				netdisk.setDiskPath(rs.getString("NEW_USER"));
				netdisk.setDiskPath(rs.getString("MANAGE_USER"));
				netdisk.setDiskPath(rs.getString("USER_ID"));
				netdisk.setDiskPath(rs.getString("DISK_NO"));
				netdisk.setDiskPath(rs.getString("SPACE_LIMIT"));
				netdisk.setDiskPath(rs.getString("ORDER_BY"));
				netdisk.setDiskPath(rs.getString("ASC_DESC"));
				netdisk.setDiskPath(rs.getString("DOWN_USER"));
				list.add(netdisk);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return list;
	}

	public T9Netdisk getNetdisk(Connection dbConn) throws Exception {
		T9ORM orm = new T9ORM();
		return (T9Netdisk) orm.loadObjSingle(dbConn, T9Netdisk.class, new HashMap());
	}

	public T9Netdisk getNetdiskInfoById(Connection dbConn, int seqId) throws Exception {
		T9ORM orm = new T9ORM();
		return (T9Netdisk) orm.loadObjSingle(dbConn, T9Netdisk.class, seqId);
	}

	public void updateNetdiskSort(Connection dbConn, Map map) {
		T9ORM orm = new T9ORM();
		PreparedStatement ps = null;
		String sql = "UPDATE NETDISK SET " + "DISK_NAME=?" + ",DISK_PATH=?" + ",DISK_NO=?" + ",SPACE_LIMIT=?" + ",ORDER_BY=? " + ",ASC_DESC=? "
				+ "where SEQ_ID=? ";
		try {
			ps = dbConn.prepareStatement(sql);
			ps.setString(1, (String) map.get("diskName"));
			ps.setString(2, (String) map.get("diskPath"));
			ps.setInt(3, (Integer) map.get("diskNo"));
			ps.setInt(4, (Integer) map.get("spaceLimit"));
			ps.setString(5, (String) map.get("orderBy"));
			ps.setString(6, (String) map.get("ascDesc"));
			ps.setInt(7, (Integer) map.get("seqId"));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			T9DBUtility.close(ps, null, log);
		}
	}

	/**
	 * 删除信息
	 * 
	 * @param dbConn
	 * @param netdisk
	 * @throws Exception
	 */
	public void delNetdiskFolderInfoById(Connection dbConn, T9Netdisk netdisk) throws Exception {
		T9ORM orm = new T9ORM();
		orm.deleteSingle(dbConn, netdisk);
	}

	/**
	 * 更新访问权限信息
	 * 
	 * @param dbConn
	 * @param map
	 * @throws Exception
	 * @throws Exception
	 */
	public void updatePrivateById(Connection dbConn, Map map, String action, String idStr) throws Exception {
		T9ORM orm = new T9ORM();
		PreparedStatement ps = null;
		T9Netdisk netdisk = (T9Netdisk) orm.loadObjSingle(dbConn, T9Netdisk.class, map);

		try {
			if ("USER_ID".equals(action)) {
				netdisk.setUserId(idStr);
				// String sql = "UPDATE NETDISK SET USER_ID=? where SEQ_ID=? ";
				// ps = dbConn.prepareStatement(sql);
				// ps.setString(1, (String) map.get("userId"));
			}
			if ("MANAGE_USER".equals(action)) {
				netdisk.setManageUser(idStr);
				// String sql = "UPDATE NETDISK SET MANAGE_USER=? where SEQ_ID=? ";
				// ps = dbConn.prepareStatement(sql);
				// ps.setString(1, (String) map.get("manageUser"));
			}
			if ("NEW_USER".equals(action)) {
				netdisk.setNewUser(idStr);
				// String sql = "UPDATE NETDISK SET NEW_USER=? where SEQ_ID=? ";
				// ps = dbConn.prepareStatement(sql);
				// ps.setString(1, (String) map.get("newUser"));
			}
			if ("DOWN_USER".equals(action)) {
				netdisk.setDownUser(idStr);
				// String sql = "UPDATE NETDISK SET DOWN_USER=? where SEQ_ID=? ";
				// ps = dbConn.prepareStatement(sql);
				// ps.setString(1, (String) map.get("downUser"));
			}
			// ps.setInt(2, (Integer) map.get("seqId"));
			// ps.executeUpdate();
		} finally {
			// T9DBUtility.close(ps, null, log);
		}
		orm.updateSingle(dbConn, netdisk);
	}

	/**
	 * 得到人员的id字符串
	 * 
	 * 
	 * @param dbConn
	 * @param map
	 * @param action
	 * @return
	 * @throws Exception
	 */
	public String selectManagerIds(Connection dbConn, Map map, String action) throws Exception {
		T9ORM orm = new T9ORM();
		String ids = "";
		String idString = "";
		T9Netdisk netdisk = (T9Netdisk) orm.loadObjSingle(dbConn, T9Netdisk.class, map);
		if (netdisk != null && !"".equals(netdisk)) {
			if ("USER_ID".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getUserId());
			} else if ("MANAGE_USER".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getManageUser());
			} else if ("NEW_USER".equals(action)) {
				idString = netdisk.getNewUser();
			} else if ("DOWN_USER".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getDownUser());
			}
			if (idString != null && !"".equals(idString.trim())) {
				String[] idsStrings = idString.split("\\|");
				// System.out.println(idsStrings);
				if (idsStrings.length != 0) {
					if (idsStrings.length == 2) {
						ids = "";
					} else if (idsStrings.length == 1) {
						ids = "";
					} else {
						ids = idsStrings[2];
					}

				}

			}

		}
		return ids;
	}

	/**
	 * 根据人员id字符串得到name字符串
	 * 
	 * 
	 * @param dbConn
	 * @param map
	 * @param action
	 * @return
	 * @throws Exception
	 */
	public String getNamesByIds(Connection dbConn, Map map, String action) throws Exception {
		String names = "";
		T9PersonLogic tpl = new T9PersonLogic();
		String ids = this.selectManagerIds(dbConn, map, action);
		// System.out.println(ids);
		names = tpl.getNameBySeqIdStr(ids, dbConn);
		return names;
	}

	/**
	 * 得到角色人员的id字符串
	 * 
	 * 
	 * @param dbConn
	 * @param map
	 * @param action
	 * @return
	 * @throws Exception
	 */
	public String getRoleIds(Connection dbConn, Map map, String action) throws Exception {
		T9ORM orm = new T9ORM();
		String ids = "";
		String idString = "";
		T9Netdisk netdisk = (T9Netdisk) orm.loadObjSingle(dbConn, T9Netdisk.class, map);
		if (netdisk != null && !"".equals(netdisk)) {
			if ("USER_ID".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getUserId());
			} else if ("MANAGE_USER".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getManageUser());
			} else if ("NEW_USER".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getNewUser());
			} else if ("DOWN_USER".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getDownUser());
			}

			if (idString != null && !"".equals(idString.trim())) {
				String[] idsStrings = idString.split("\\|");
				// System.out.println("角色idsStrings:" + idsStrings.length);
				if (idsStrings.length != 0) {
					if (idsStrings.length == 1) {
						ids = "";
					} else {
						ids = idsStrings[1];
					}

				}
			}
		}
		return ids;
	}

	/**
	 * 根据角色人员id字符串得到name字符串
	 * 
	 * 
	 * @param dbConn
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getRoleNamesByIds(Connection dbConn, Map map, String action) throws Exception {
		String names = "";
		T9FileSortLogic tpl = new T9FileSortLogic();
		String ids = this.getRoleIds(dbConn, map, action);
		// System.out.println(ids);
		names = tpl.getRoleNameBySeqIdStr(dbConn, ids);
		return names;
	}

	/**
	 * 得到部门人员的id字符串
	 * 
	 * 
	 * @param dbConn
	 * @param map
	 * @param action
	 * @return
	 * @throws Exception
	 */
	public String getDeptIds(Connection dbConn, Map map, String action) throws Exception {
		T9ORM orm = new T9ORM();
		String ids = "";
		String idString = "";
		T9Netdisk netdisk = (T9Netdisk) orm.loadObjSingle(dbConn, T9Netdisk.class, map);
		if (netdisk != null && !"".equals(netdisk)) {
			if ("USER_ID".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getUserId());
			} else if ("MANAGE_USER".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getManageUser());
			} else if ("NEW_USER".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getNewUser());
			} else if ("DOWN_USER".equals(action)) {
				idString = T9Utility.null2Empty(netdisk.getDownUser());
			}
			if (idString != null && !"".equals(idString.trim())) {
				String[] idsStrings = idString.split("\\|");
				// System.out.println(idsStrings);
				if (idsStrings.length != 0) {
					if (idsStrings[0].length() != 0) {
						ids = idsStrings[0];
					}

				}
			}
		}
		return ids;
	}

	/**
	 * 根据部门id字符串得到name字符串
	 * 
	 * 
	 * @param dbConn
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getDeptByIds(Connection dbConn, Map map, String action) throws Exception {
		String names = "";
		T9FileSortLogic tpl = new T9FileSortLogic();
		String ids = this.getDeptIds(dbConn, map, action);
		// System.out.println(ids);
		if (!"ALL_DEPT".equals(ids.trim())) {
			names = tpl.getDeptNameBySeqIdStr(dbConn, ids);
		}
		return names;
	}

	/**
	 * 获取访问权限：根据ids串返回与登录的seqId比较判断是否相等，返回boolean类型。
	 * 
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean getUserIdStr(int userSeqId, String ids, Connection dbConn) throws Exception, Exception {
		boolean flag = false;
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp)) {
					if (Integer.parseInt(tmp) == userSeqId) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 获取角色权限：根据userId中的角色Id串返回与登录的角色Id比较判断是否相等，返回boolean类型。
	 * 
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean getRoleIdStr(String loginUserRoleId, String ids, Connection dbConn) throws Exception, Exception {
		boolean flag = false;
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp)) {
					if (Integer.parseInt(tmp) == Integer.parseInt(loginUserRoleId)) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 根据登录用户的部门Id与权限中的部门Id对比返回boolean
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean getDeptIdStr(int loginUserDeptId, String ids, Connection dbConn) throws Exception, Exception {
		boolean flag = false;
		if (ids != null && !"".equals(ids)) {
			if ("0".equals(ids.trim()) || "ALL_DEPT".equals(ids.trim())) {
				return flag = true;
			}
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp.trim())) {
					if (loginUserDeptId == Integer.parseInt(tmp)) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 创建多级目录操作 方法名：CreateFolder 参 数： String FolderPath //要创建的目录 返回值：boolean类型 功
	 * 
	 * 能：根据用户指定的多级目录进行创建 备 注：
	 */
	public static boolean createFolder(String FolderPath) {
		File file1 = new File(FolderPath);
		// 检查参数
		if (FolderPath == null || FolderPath.length() == 0) {
			return false;
		}
		if (FolderPath.indexOf("/") == -1 && FolderPath.indexOf("\\") == -1) {
			return false;
		}
		if (file1.exists()) {
			return true;
		}
		// 首次处理传过来的字符串
		String str_temp = "";
		for (int i = 0; i < FolderPath.length(); i++) {
			if (i < FolderPath.length()) {
				if (FolderPath.substring(i, i + 1).equals("\\")) {
					str_temp += "/";
				} else {
					str_temp += FolderPath.substring(i, i + 1);
				}
			}
		}
		// 通过"/"，那字符串拆分
		String Str_P[] = str_temp.split("/");
		String Str_Create = "";
		for (int i = 0; i < Str_P.length; i++) {
			Str_Create += Str_P[i] + "/";
			File file = new File(Str_Create);
			if (!file.exists()) {
				if (!file.mkdir()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 得到该文件的文件夹名
	 * 
	 * @param key
	 * @return
	 */
	public String getFilePathFolder(String key) {
		String folder = "";
		if (key != null && !"".equals(key)) {
			String[] str = key.split("_");
			for (int i = 0; i < str.length; i++) {
				folder = str[0];
			}
		}
		return folder;
	}

	/**
	 * 得到附件的Id
	 * 
	 * @param keyId
	 * @return
	 */
	public String getAttaId(String keyId) {
		String attaId = "";
		if (keyId != null && !"".equals(keyId)) {
			String[] ids = keyId.split("_");
			if (ids.length > 0) {
				attaId = ids[1];
			}
		}
		return attaId;
	}

	/**
	 * 得到该文件的文件夹名 兼老数据
	 * 
	 * 
	 * @param key
	 * @return
	 */
	public String getAttFolderName(String key) {
		String folder = "";
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(date);

		if (key != null && !"".equals(key)) {

			if (key.indexOf('_') != -1) {
				String[] str = key.split("_");
				for (int i = 0; i < str.length; i++) {
					folder = str[0];
				}
			} else {
				folder = "all";
			}

		}
		return folder;
	}

	/**
	 * 得到附件的Id 兼老数据
	 * 
	 * 
	 * @param keyId
	 * @return
	 */
	public String getOldAttaId(String keyId) {
		String attaId = "";
		if (keyId != null && !"".equals(keyId)) {
			if (keyId.indexOf('_') != -1) {
				String[] ids = keyId.split("_");
				if (ids.length > 0) {
					attaId = ids[1];
				}

			} else {
				attaId = keyId;
			}
		}
		return attaId;
	}

	/**
	 * 转存文件到网络硬盘
	 * 
	 * 
	 * @param dbConn
	 * @param diskPath
	 * @param attachId
	 * @param attachName
	 * @param filePath
	 * @throws Exception
	 */
	public String transferNetdisk(Connection dbConn, String diskPath, String attachId, String attachName, String subject, String filePath)
			throws Exception {
		boolean flag = false;
		boolean exist = false;
		String dirName = "";
		String existName = "";

		String fileDir = filePath + File.separator + this.getAttFolderName(attachId); // D:\project\t9\attach\file_folder\\1007
		String fileName = this.getOldAttaId(attachId) + "_" + attachName.trim(); // a4af7859d19ce575b052dda815fa1d3f_新建文档sd^.ppt
		String oldFileName = this.getOldAttaId(attachId) + "." + attachName.trim(); // a4af7859d19ce575b052dda815fa1d3f.新建文档sd^.ppt

		File file2 = new File(diskPath); // d:\bjfaoitc
		if (!T9Utility.isNullorEmpty(dirName)) {
			dirName = file2.getName();
		}else {
			dirName = diskPath.replace('\\', '/');
		}

		File oldFromFile = new File(fileDir.trim() + "/" + oldFileName.trim());
		File fromFile = new File(fileDir.trim() + "/" + fileName.trim());

		if (fromFile.exists()) {
			File diskFile = new File(file2.getAbsoluteFile() + "/" + attachName.trim());
			String newNameStr = "";
			String newFileName = "";
			String type = attachName.substring(attachName.lastIndexOf(".")); // .doc
			if (!"".equals(subject.trim()) && subject != null) { // 新建文档sd^
				newNameStr = subject;
			} else {
				newNameStr = attachName.substring(0, attachName.lastIndexOf(".")); // 用文件的名字
			}
			newFileName = newNameStr + type;
			if (!diskFile.exists()) {
				T9FileUtility.copyFile(fromFile.getAbsolutePath(), file2.getAbsolutePath() + "/" + newFileName);
				flag = true;
			} else {
				existName = attachName.trim();
				exist = true;
			}

		} else if (oldFromFile.exists()) {
			File diskFile = new File(file2.getAbsoluteFile() + "/" + attachName.trim());
			String newNameStr = "";
			String newFileName = "";
			String type = attachName.substring(attachName.lastIndexOf(".")); // .doc
			if (!"".equals(subject.trim()) && subject != null) { // 新建文档sd^
				newNameStr = subject;
			} else {
				newNameStr = attachName.substring(0, attachName.lastIndexOf(".")); // 用文件的名字
			}
			newFileName = newNameStr + type;
			if (!diskFile.exists()) {
				T9FileUtility.copyFile(oldFromFile.getAbsolutePath(), file2.getAbsolutePath() + "/" + newFileName);
				flag = true;
			} else {
				existName = attachName.trim();
				exist = true;
			}
		}
		String data = "{flag:\"" + flag + "\",exist:\"" + exist + "\",dirName:\"" + T9Utility.encodeSpecial(dirName) + "\",existName:\"" + T9Utility.encodeSpecial(existName) + "\"}";
		return data;
	}
	
	public boolean checkDiskPathLogic(Connection dbConn,int seqId,String pathName) throws Exception{
		boolean flag = false;
		try {
			File file = new File(pathName.trim());
			List<T9Netdisk> netdisks = this.getNetdiskFolderInfo(dbConn);
			if (netdisks!=null && file !=null && netdisks.size()>0) {
				String filePaht = file.getPath().trim();
				if (filePaht.endsWith("\\")) {
					filePaht = filePaht.substring(0, filePaht.length()-1).trim();
				}
				for(T9Netdisk netdisk:netdisks){
					if (netdisk!=null) {
						String diskPath = T9Utility.null2Empty(netdisk.getDiskPath()).trim();
						File diskPathFile = new File(diskPath.trim());
						String diskPathStr = diskPathFile.getPath().trim();
						if (diskPathStr.endsWith("\\")) {
							diskPathStr = diskPathStr.substring(0, diskPathStr.length()-1).trim();
						}
						if (file!=null && filePaht.trim().equalsIgnoreCase(diskPathStr.trim())) {
							if (seqId!=netdisk.getSeqId()) {
								flag = true;
								break;
							}
							
						}
					}
				}
			}
			return flag;
		} catch (Exception e) {
			throw e;
		}
	}

}
