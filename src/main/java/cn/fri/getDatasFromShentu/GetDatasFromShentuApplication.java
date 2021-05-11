package cn.fri.getDatasFromShentu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@EnableScheduling // 开启定时任务功能
@SpringBootApplication
@ComponentScan(basePackages = "cn.fri")
public class GetDatasFromShentuApplication {
    @Autowired
    private FetchDataTasks fetchDataTasks;
    @Autowired
    private static FetchDataTasks fetchDataTasks_static;
    public static void main(String[] args) {
        SpringApplication.run(GetDatasFromShentuApplication.class, args);
        Thread babayInfoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    fetchDataTasks_static.fetchBabyInfoTsk();
                }
            }
        });
        Thread scanCodeRecordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    fetchDataTasks_static.fetchScanCodeRecordsTsk();
                }
            }
        });
        babayInfoThread.start();
        scanCodeRecordThread.start();
    }
    @PostConstruct
    private  void init(){
        fetchDataTasks_static = fetchDataTasks;
    }

}
