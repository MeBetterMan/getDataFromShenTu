package cn.fri.getDatasFromShentu;

import cn.fri.Utils.HttpClientUtil;
import cn.fri.beans.BabyInfo;
import cn.fri.beans.ScanCodeRecord;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class FetchDataTasks {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //@Value("${BabyInfoStartTime}")
    private static String BabyInfoStartTime = "";
    //@Value("${ScanCodeRecordStartTime}")
    private static String ScanCodeRecordStartTime = "";
    @Autowired
    private Environment env;

    public void fetchBabyInfoTsk() {
        logger.info("babayinfo thread id:{}", Thread.currentThread().getId());
        /**
         * 1. 通过api从神兔获取数据
         * 2.将数据插入指定数据库
         */
        List<BabyInfo> babyInfos = getBabyInfoDataFromShentu();
        if (babyInfos == null && babyInfos.size() == 0) {
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            insertToBabyInfo(babyInfos);
        }
    }

    public void fetchScanCodeRecordsTsk() {
        logger.info("scancoderecord thread id:{}", Thread.currentThread().getId());
        /**
         * 1. 通过api从神兔获取数据
         * 2.将数据插入指定数据库
         */
        List<ScanCodeRecord> records = getScanCodeDataFromShentu();
        if (records.size() == 0) {
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            insertToScanCode(records);
        }
    }

    public void fetchScanCodeTsk() {
        logger.info("thread id:{}", Thread.currentThread().getId());
        /**
         * 1. 通过api从神兔获取数据
         * 2.将数据插入指定数据库
         */
        List<BabyInfo> babyInfos = getBabyInfoDataFromShentu();
        if (babyInfos.size() == 0) {
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            insertToBabyInfo(babyInfos);
        }
    }

    /**
     * 从数据库获取babyinfo接口的起始时间
     *
     * @return
     */
    private Map<String, Object> getStartTime() {
        String start_time = "";
        //从数据库读取起始时间
        List<Map<String, Object>> res = jdbcTemplate.queryForList("select baby_info_start_time,scan_code_record_start_time from flag");
        if (res == null || res.size() == 0) {
            return new HashMap<String, Object>();
        } else {
            return res.get(0);
        }
    }

    /**
     * 更新查询起止时间
     *
     */
    private void updateStartTime(String fieldName, String fieldValue) {
        String sql = String.format("update flag set %s ='%s'", fieldName, fieldValue);
        jdbcTemplate.update(sql);
    }

    /**
     * 通过api从神兔获取数据
     */
    private List<BabyInfo> getBabyInfoDataFromShentu() {
        Map<String, String> param = new HashMap<>();
        //从MPP获取其实时间
        Map<String,Object> time= getStartTime();
        if(time.get("baby_info_start_time") != null){
            BabyInfoStartTime = time.get("baby_info_start_time").toString();
        }
        param.put("time", BabyInfoStartTime);
        logger.info("BabyInfoStartTime {}", BabyInfoStartTime);
        String res = HttpClientUtil.doPostJson(env.getProperty("BabyInfoURL"), JSON.toJSONString(param));
        List<BabyInfo> babyInfos = null;
        JSONObject res_json = JSONObject.parseObject(res);
        if (!"200".equals(res_json.getString("code"))) {
            logger.info("api读取数据错误");
        } else {
            babyInfos = JSONArray.parseArray(res_json.getString("data"), BabyInfo.class);
        }
        return babyInfos;
    }

    /**
     * 将数据插入数据库
     */
    private void insertToBabyInfo(List<BabyInfo> babyInfos) {
        String sql = "insert into baby_info(name,gender,birthday,icon,height,weight,features,blood_type,drug_allergy,inborn_disease,other_info,insert_time) \n" +
                "values(?,?,?,?,?,?,?,?,?,?,?,?) ";
        List<Object[]> args = new ArrayList<>();
        for (BabyInfo info : babyInfos) {
            args.add(info.toObject());
        }
        System.out.println("向数据库插入数据");
        jdbcTemplate.batchUpdate(sql, args);

        //将最新时间更新到数据库
        BabyInfoStartTime = format.format(new Date(babyInfos.get(babyInfos.size() - 1).getInsertTime()));
        updateStartTime("baby_info_start_time",BabyInfoStartTime);
    }

    /**
     * 通过api从神兔获取数据
     */
    private List<ScanCodeRecord> getScanCodeDataFromShentu() {
        Map<String, String> param = new HashMap<>();
        //从MPP获取其实时间
        Map<String,Object> time= getStartTime();
        if(time.get("scan_code_record_start_time") != null){
            ScanCodeRecordStartTime = time.get("scan_code_record_start_time").toString();
        }
        param.put("time", ScanCodeRecordStartTime);
        logger.info("ScanCodeRecordStartTime {}", ScanCodeRecordStartTime);
        String res = HttpClientUtil.doPostJson(env.getProperty("ScanCodeRecordURL"), JSON.toJSONString(param));
        System.out.println("res--------------"+res);
        List<ScanCodeRecord> records = null;
        JSONObject res_json = JSONObject.parseObject(res);
        if (!"200".equals(res_json.getString("code"))) {
            logger.info("api读取数据错误");
        } else {
            records = JSONArray.parseArray(res_json.getString("data"), ScanCodeRecord.class);
        }
        return records;
    }

    /**
     * 将数据插入数据库
     */
    private void insertToScanCode(List<ScanCodeRecord> records) {
        String sql = "insert into scan_code_record(baby_id,name,address,insert_time) values(?,?,?,?) ";
        List<Object[]> args = new ArrayList<>();
        for (ScanCodeRecord info : records) {
            Object[] tempObj = new Object[4];
            tempObj[0] = info.getBabyId();
            tempObj[1] = info.getName();
            tempObj[2] = info.getAddress();
            tempObj[3] = info.getInsertTime();

            args.add(tempObj);
        }
        System.out.println("向数据库插入数据");
        jdbcTemplate.batchUpdate(sql, args);

        //将最新时间更新到数据库
        ScanCodeRecordStartTime = format.format(new Date(records.get(records.size() - 1).getInsertTime()));
        updateStartTime("scan_code_record_start_time",ScanCodeRecordStartTime);
    }

    public static void main(String[] args) {
        new FetchDataTasks().getBabyInfoDataFromShentu();
    }
}
