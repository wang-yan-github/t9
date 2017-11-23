package t9.core.funcs.filefolder.logic;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.filefolder.data.T9FileContent;
import t9.core.funcs.filefolder.data.T9FileSort;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;

public class T9FileSortLogic {

	public void saveFileSortInfo(Connection dbConn, T9FileSort fileSort) {
		T9ORM orm = new T9ORM();
		try {
			orm.saveSingle(dbConn, fileSort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<T9FileSort> getFileSortsInfo(Connection dbConn) throws Exception {
		T9ORM orm = new T9ORM();
		return orm.loadListSingle(dbConn, T9FileSort.class, new HashMap());
	}

	public T9FileSort getFileSortInfoById(Connection dbConn, String seqIdStr) throws NumberFormatException, Exception {
		T9ORM orm = new T9ORM();
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		return (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, seqId);
	}

	public void updateFileSortInfoById(Connection dbConn, T9FileSort fileSort) throws Exception {
		T9ORM orm = new T9ORM();
		orm.updateSingle(dbConn, fileSort);
	}

	/**
	 * 递归删除文件夹及下的所有文件信息
	 * @param dbConn
	 * @param fileSort
	 * @throws Exception
	 */
	public void delFileSortInfoById(Connection dbConn, T9FileSort fileSort, int loginUserSeqId, String ipStr) throws Exception {
		T9FileContentLogic contentLogic = new T9FileContentLogic();
		String separator = File.separator;
		String filePath = T9SysProps.getAttachPath() + separator + "file_folder" + separator;
		String seqIdStrs = "";
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("SORT_PARENT", fileSort.getSeqId());
		List<T9FileSort> fileSortList = orm.loadListComplex(dbConn, T9FileSort.class, map);

		Map contentMap = new HashMap();
		contentMap.put("SORT_ID", fileSort.getSeqId());
		List<T9FileContent> fileContents = new ArrayList<T9FileContent>();
		fileContents = contentLogic.getFileContentsInfo(dbConn, contentMap);
		if (fileContents != null && fileContents.size() > 0) {
			for (int i = 0; i < fileContents.size(); i++) {
				T9FileContent content = fileContents.get(i);
				seqIdStrs += content.getSeqId() + ",";
			}
			if (seqIdStrs.endsWith(",")) {
				seqIdStrs = seqIdStrs.trim().substring(0, seqIdStrs.trim().length() - 1);
			}
			contentLogic.delFile(dbConn, seqIdStrs, filePath, loginUserSeqId, ipStr, "", "");
		}
		orm.deleteSingle(dbConn, fileSort);
		for (int i = 0; i < fileSortList.size(); i++) {
			delFileSortInfoById(dbConn, fileSortList.get(i), loginUserSeqId, ipStr);
		}
	}
}
