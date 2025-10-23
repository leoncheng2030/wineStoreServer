package vip.xiaonuo.mini.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.api.DevFileApi;

/**
 * 小程序文件上传控制器
 *
 * @author AI Assistant
 * @date 2025-01-20
 */
@Tag(name = "小程序文件上传控制器")
@RestController
@Validated
public class FileUploadController {

    @Resource
    private DevFileApi devFileApi;

    @Resource
    private DevConfigApi devConfigApi;

    /**
     * 小程序动态上传文件返回URL
     *
     * @param file 上传的文件
     * @return 文件URL
     */
    @Operation(summary = "小程序动态上传文件返回URL")
    @CommonLog("小程序动态上传文件返回URL")
    @PostMapping("/mini/file/uploadReturnUrl")
    public CommonResult<String> uploadReturnUrl(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(devFileApi.uploadDynamicReturnUrl(file));
    }

    /**
     * 小程序动态上传文件返回ID
     *
     * @param file 上传的文件
     * @return 文件ID
     */
    @Operation(summary = "小程序动态上传文件返回ID")
    @CommonLog("小程序动态上传文件返回ID")
    @PostMapping("/mini/file/uploadReturnId")
    public CommonResult<String> uploadReturnId(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(devFileApi.uploadDynamicReturnId(file));
    }

    /**
     * 小程序本地上传文件返回URL
     *
     * @param file 上传的文件
     * @return 文件URL
     */
    @Operation(summary = "小程序本地上传文件返回URL")
    @CommonLog("小程序本地上传文件返回URL")
    @PostMapping("/mini/file/uploadLocalReturnUrl")
    public CommonResult<String> uploadLocalReturnUrl(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(devFileApi.storageFileWithReturnUrlLocal(file));
    }

    /**
     * 小程序本地上传文件返回ID
     *
     * @param file 上传的文件
     * @return 文件ID
     */
    @Operation(summary = "小程序本地上传文件返回ID")
    @CommonLog("小程序本地上传文件返回ID")
    @PostMapping("/mini/file/uploadLocalReturnId")
    public CommonResult<String> uploadLocalReturnId(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(devFileApi.storageFileWithReturnIdLocal(file));
    }
}