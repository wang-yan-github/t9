package t9.lucene.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import t9.core.autorun.T9AutoRun;


public class LuceneAutoIndex extends T9AutoRun{
  private static final Logger log = Logger.getLogger("t9.show.PersonService.logic.LuceneAutoIndex");


  @Override
 
  public void doTask() throws Exception {
    Connection conn = getRequestDbConn().getSysDbConn();
    List<Map<String,String>> dataList =new ArrayList();
    T9LuceneIndex lucene=new T9LuceneIndex();
    log.info("-----------站点开始构造索引-------------");
   
    lucene .setNewsContent(conn, dataList);
   
    
    log.info("---------站点结束构造索引------------");
    
   /* log.info("-----------ts开始构造索引-------------");
    Connection tsConn =lucene.getTsDbConn();
    lucene.setNewsIndexFromTs(tsConn, dataList);
    log.info("获取信息");

    lucene.setCandIndexFromTs(tsConn, dataList);
    log.info("获取人员");

    lucene.setDisconIndexFromTs(tsConn, dataList);
    log.info("获取介绍");

    log.info("---------ts结束构造索引------------");

    
    
    log.info("-----------行政开始构造索引-------------");
    tsConn =getRequestDbConn().getSysDbConn();
    lucene.setNewsIndexFromXZ(tsConn, dataList);
    log.info("获取信息");

    lucene.setCandIndexFromXZ(tsConn, dataList);
    log.info("获取人员");

    lucene.setDisconIndexFromXZ(tsConn, dataList);
    log.info("获取介绍");

    log.info("---------行政结束构造索引------------");
    
    
    */
    lucene.setIndex(dataList);
    log.info("---------结束构造索引------------");

  }

 
}
