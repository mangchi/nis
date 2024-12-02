package mil.gdl.nis.cmmn.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mil.gdl.nis.cmmn.EnumModel;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = Shape.OBJECT)
public enum ErrorCode implements EnumModel {

	// COMMON
	INVALID_CODE(001, "E001", "Invalid Code"), RESOURCE_NOT_FOUND(002, "E002", "Resource not found"),
	USER_NOT_FOUND(003, "E003", "User not found"), BAD_CRYPTO_FOUND(004, "E004", "Bad Credentials Exception"),
	TEMPORARY_SERVER_ERROR(005, "E005", "temporary server error happend"), EXPIRED_CODE(006, "E006", "Expired Code"),
	PWD_QUARTER_CHG(007, "E007", "Password change per quarter"),
	PWD_ERROR_FIFTH_TIMES(Long.parseLong("008"), "E008", "Password error with 5 times"),
	NOT_EXIST_MSG(Long.parseLong("009"), "E009", "Mandatory message is not exist"),
	USER_BlOCK(Long.parseLong("010"), "E010", "user is blocked"),
	NOT_EXIST_LEARNING(Long.parseLong("011"), "E011", "error.learningNoCount"),
	AUOT_LABELING(Long.parseLong("012"), "E012", "error.autoLabeling");

	private long status;
	private String code;
	private String message;
	private String detail;

	ErrorCode(long status, String code, String message) {
		this.status = status;
		this.message = message;
		this.code = code;

	}

	@Override
	public String getKey() {
		return this.code;
	}

	@Override
	public String getValue() {
		return this.message;
	}
}