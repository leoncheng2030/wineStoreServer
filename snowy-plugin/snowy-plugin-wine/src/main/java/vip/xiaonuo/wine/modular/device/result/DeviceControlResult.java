package vip.xiaonuo.wine.modular.device.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备控制结果")
public class DeviceControlResult {

    /** 是否成功 */
    @Schema(description = "是否成功")
    private boolean success;

    /** 结果消息 */
    @Schema(description = "结果消息")
    private String message;

    /** 加密控制指令 */
    @Schema(description = "加密控制指令")
    private String cmd;

    /** 控制参数 */
    @Schema(description = "控制参数")
    private String params;

    /** 设备响应数据 */
    @Schema(description = "设备响应数据")
    private String responseData;

    /** 执行时间戳 */
    @Schema(description = "执行时间戳")
    private Long timestamp;

    /**
     * 创建成功结果
     */
    public static DeviceControlResult success(String cmd) {
        DeviceControlResult result = new DeviceControlResult();
        result.setSuccess(true);
        result.setMessage("指令生成成功");
        result.setCmd(cmd);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 创建成功结果（带消息）
     */
    public static DeviceControlResult success(String cmd, String message) {
        DeviceControlResult result = new DeviceControlResult();
        result.setSuccess(true);
        result.setMessage(message);
        result.setCmd(cmd);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 创建失败结果
     */
    public static DeviceControlResult failure(String message) {
        DeviceControlResult result = new DeviceControlResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
}
