///////////////////////////////////////////////////MESSAGE///////////////////////////////////

let msg = {
		   expired : "세션이 만료되었습니다."
		 , fileLimit : "첨부파일은 최대 {}개 까지 <br>첨부 가능합니다."
	     , success : "정상 처리되었습니다."
	     , fail : "오류가 발생하였습니다."
	     , del  : "삭제하시겠습니까?"
	     , delChk  : "삭제할 항목을 선택하세요."
	     , blockChk  : "차단할 항목을 선택하세요."
	     , clearChk  : "해제할 항목을 선택하세요."
	     , downloadChk : "내려받기할 항목을 선택하세요."
	     , invalidDay : "시작일이 종료일보다 클 수 없습니다"
         , diffChk: "조회 시작일과 종료일을 100일 이내로 해주세요."
         , diffChkWarn: "조회 시작일과 종료일 차이가 100일이\x3cbr\x3e초과할 경우에 시간이 오래 걸릴 수 있습니다.<br>그래도 싫행하시겠습니까?"
         
}



/*
 * @exp     : 메시지 제공
 * @param   : msg-> msg문자열
 *          : msgArr-> 대체 문자열
 * @return  :
 * @example : getMsg(msg.fileLimit,["1"])
 * 
 */

const getMsg = (msg,msgArr) => {
	if(msg === undefined || msg === ''){
		return msg;
	}
	if(msgArr === undefined || msgArr.length === 0){
		return msg;
	}
	for(let msgIdx in msgArr){
		msg = msg.replace("{}",msgArr[msgIdx]);
	}
	msg = msg.replace(/{}/gi, "");
	return msg;
}