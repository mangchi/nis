package mil.gdl.nis.exception;

import mil.gdl.nis.cmmn.error.ErrorCode;

public class BizException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ErrorCode errorCode;

	public BizException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public BizException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
