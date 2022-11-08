package com.hdy.dbmovie.common.bean;

import lombok.Data;
import org.json.JSONObject;

/**
 * @ Program       :  com.hdy.dbmovie.common.bean.Result
 * @ Description   :  返回消息实体
 * @ Author        :  Honetooyoung
 * @ CreateDate    :  2022-10-28 17:38:30
 */
@Data
public class Result {
    private boolean success=true;
    private Integer code=null;
    private String msg=null;
    private Object data=new JSONObject();
    /**
     * 成功响应
     */
    public Result OK() {
        this.success = true;
        this.code = 200;
        if (this.msg==null) {
            this.msg = "success.";
        }
        return this;
    }

    /**
     * 请求成功，但业务逻辑处理不通过
     */
    public Result NO() {
        this.success = false;
        this.code = 400;
        return this;
    }

    public Result() {
        super();
    }

    public Result(int code) {
        super();
        this.success = false;
        this.code = code;
    }

    public Result(int code, String msg) {
        super();
        if (code==200)
        this.success = true;
        else
            this.success=false;
        this.code = code;
        this.msg = msg;
        data=null;
    }

    public Result(int code, String msg, Object data) {
        super();
        this.success = true;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public Result(boolean b) {
        super();
        if (b){
            this.success = true;
            this.code = 200;
            this.msg = "成功！";
        }
        else{
            this.success=false;
            this.code = 400;
            this.msg = "失败！";
        }
        data=null;
    }

}

