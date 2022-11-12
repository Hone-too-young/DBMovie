package com.hdy.dbmovie.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 历史记录表(HistoryRecord)表实体类
 *
 * @author Honetooyoung
 * @since 2022-11-08 15:43:43
 */
@Data
@AllArgsConstructor
@TableName("db_history_record")
public class HistoryRecord {
    //评论主键id
    @TableId(type = IdType.AUTO)
    private Integer rid;
    //记录id
    private String filmId;
    //用户id
    private String userId;
    //查看时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;
public HistoryRecord(String filmId,String userId){
    this.filmId=filmId;
    this.userId=userId;
}
}

