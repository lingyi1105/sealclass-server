package cn.rongcloud.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import static cn.rongcloud.common.ErrorEnum.ERR_SUCCESS;

/**
 * Created by weiqinxiao on 2019/2/25.
 */
@ApiModel("基础响应参数")
public class BaseResponse<T> {
    @ApiModelProperty(value = "错误码", notes = "0为正常，255为未知错误")
    private @Getter int errCode;
    @ApiModelProperty("错误信息")
    private @Setter @Getter String errMsg;
    @ApiModelProperty("错误详情")
    private @Setter @Getter String errDetail;
    @ApiModelProperty("响应数据")
    private @Getter BaseResponseResult data;

    public BaseResponse() {
        this(ERR_SUCCESS);
    }

    public BaseResponse(T data) {
        this(ERR_SUCCESS);
        setData(data);
    }

    public void setData(T data) {
        this.data = new BaseResponseResult<T>(data);
    }

    public BaseResponse(ErrorEnum errorEnum) {
        setErr(errorEnum, "");
    }

    public BaseResponse(ErrorEnum err, String errDetail, T data) {
        setErr(err, errDetail);
        setData(data);
    }

    public void setErr(ErrorEnum error, String errDetail) {
        this.errCode = error.getErrCode();
        this.errMsg = error.getErrMsg();
        this.errDetail = errDetail;
    }

    @ApiModel("响应数据")
    static class BaseResponseResult<R> {
        @ApiModelProperty("响应数据")
        private @Setter @Getter R result;

        BaseResponseResult(R result) {
            this.result = result;
        }
    }
}
