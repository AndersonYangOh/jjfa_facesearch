package com.tt.face.common;


public enum CommonCode implements ResultCode{
    INVALID_PARAM(false,10003,"非法参数！"),
    SUCCESS(true,10000,"操作成功！"),
    FAIL(false,11111,"操作失败！"),
    UNAUTHENTICATED(false,10001,"此操作需要登陆系统！"),
    USER_NO_EXIST(false,10002,"该账号不存在！"),
    UNAUTHORISE(false,10002,"权限不足，无权操作！"),
    REGISTER_REPEAT(false,20001,"个人信息已经注册！"),
    REGISTER_FAIL(false,20002,"个人注册失败！"),
    FACE_UPLOAD_FAIL(false,20010,"人脸图片上传失败！"),
    FACE_UPLOAD_LIMIT_FAIL(false,20011,"人脸图片大小超过限制！"),
    FACE_REPOS_LIMIT_FAIL(false,20012,"人脸库数量超过最大限制！"),
    DETECT_FAIL(false,20020,"人脸检测失败！"),
    SERVER_ERROR(false,99999,"抱歉，系统繁忙，请稍后重试！");
//    private static ImmutableMap<Integer, CommonCode> codes ;
    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private CommonCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }
    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }


}
