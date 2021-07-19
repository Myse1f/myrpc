/**
 * Created By Yufan Wu
 * 2021/7/15
 */
package com.myse1f.myrpc.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse {
    private String requestId; // 表示对该 requestId 的请求进行响应
    private Exception exception;
    private Object result;
}
