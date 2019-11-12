package com.tt.face.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ybs
 * @since 2019-09-19
 */
@TableName("ttface_user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    private String jobId;

    /**
     * 姓名
     */
    private String name;

    private LocalDate birthday;

    /**
     * 年龄
     */
    private Integer age;

    private String phone;

    /**
     * 邮箱
     */
    private String email;

    private String facePicUrl;

    private String faceLandMark;

    private Integer seetaIndex;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    private Integer deleteflag;


    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public User setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public User setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFacePicUrl() {
        return facePicUrl;
    }

    public User setFacePicUrl(String facePicUrl) {
        this.facePicUrl = facePicUrl;
        return this;
    }

    public String getFaceLandMark() {
        return faceLandMark;
    }

    public User setFaceLandMark(String faceLandMark) {
        this.faceLandMark = faceLandMark;
        return this;
    }

    public Integer getSeetaIndex() {
        return seetaIndex;
    }

    public User setSeetaIndex(Integer seetaIndex) {
        this.seetaIndex = seetaIndex;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public User setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public User setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public User setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Integer getDeleteflag() {
        return deleteflag;
    }

    public User setDeleteflag(Integer deleteflag) {
        this.deleteflag = deleteflag;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "User{" +
        "id=" + id +
        ", jobId=" + jobId +
        ", name=" + name +
        ", birthday=" + birthday +
        ", age=" + age +
        ", phone=" + phone +
        ", email=" + email +
        ", facePicUrl=" + facePicUrl +
        ", faceLandMark=" + faceLandMark +
        ", seetaIndex=" + seetaIndex +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", status=" + status +
        ", deleteflag=" + deleteflag +
        "}";
    }
}
