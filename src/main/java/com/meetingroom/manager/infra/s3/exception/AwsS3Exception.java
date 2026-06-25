package com.meetingroom.manager.infra.s3.exception;

import com.meetingroom.manager.global.error.code.AwsS3ErrorCode;
import com.meetingroom.manager.global.error.exception.ExternalServiceManagerException;
import lombok.Getter;

@Getter
public class AwsS3Exception extends ExternalServiceManagerException {
    public AwsS3Exception(AwsS3ErrorCode errorCode) {
        super(errorCode);
    }
}
