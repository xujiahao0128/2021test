package com.wangzaiplus.test.listen;

import com.wangzaiplus.test.dto.LogDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年01月24日 15:09
 */
@Slf4j
@Component
public class SystemLogListen {

    /** 日志输出类 */
    public void logEvent(LogDTO logDTO){
        log.info("【本地日志】执行方法:{},方法描述:{},日志描述:{},业务id:{},业务类型:{},执行是否成功:{},执行耗时:{},失败原因:{},返回内容:{}"
                ,logDTO.getMethodName(),logDTO.getMethodDesc(),logDTO.getLogDesc()
                ,logDTO.getBusinessId(),logDTO.getBusinessType(),logDTO.getIsSuccessful(),logDTO.getExecutionTime(),logDTO.getErrorMessage()
                ,logDTO.getReturnMessage());
    }

}
