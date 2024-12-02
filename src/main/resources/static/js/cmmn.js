'use strict'


let nis = { storageKey : "nisStorage"
	      , cryptKey   : "nisAuth"
	      , codeKey   : "codeKey"
	      , diffDay   : -100  
}

const gfn_formToJson = (form) => {
	try{
		/*
		if(typeof form != 'string'){
			console.log("form parameter is not valid");
			return;
		}
		let formData = null;
		if(form.startsWith( '#' )){
			formData = document.querySelector(form); 
		}
		else{
			formData = document.getElementById(form);
		}
		*/
		const serializedFormData = Object.fromEntries(new FormData(form));
		//const formEntries = new FormData(formData).entries();
		//const serializedFormData = Object.assign(...Array.from(formEntries, ([x,y]) => ({[x]:y})));
	    console.log("sendData:::",JSON.stringify(serializedFormData));
		return serializedFormData;
	}catch(e){
		console.log("formToJson:",e)
	}
	
}

const gfn_asyncDownCall = async (url,method,param) => {
	let _method = method===undefined?'POST':method;
	const data = await fetch(url, {method: _method
						        , headers: {'Content-Type': 'application/json'} // 'Content-Type':
						        , params : JSON.stringify(param)
	                             }
	                        ).then((result) =>{ return result.blob();})
							 .catch((error) => {
								 console.error("gfn_asyncDownCall errorr:",error);}
					        );
    return data;
}    

const gfn_asyncCallNoLogin = async (url,method,frm,requiredParams,comp,callBackFn) => {
	try{
		if(requiredParams != undefined && requiredParams != null){
			if(!gfn_valid(frm,requiredParams,null,comp)){
				return;
			}
		}
		let loadingLayer = document.querySelector('.loadingLayer');
		if(loadingLayer){
			loadingLayer.style.display = "block";
		}
		let sendData = {method: method
                , headers: {'Content-Type': 'application/json'} 
                }; 
		if(method.toUpperCase() === 'POST'){
			sendData.body = JSON.stringify(gfn_formToJson(frm));
		}
		else{
			sendData.params = {};
		}
		
		let _method = method===undefined?'POST':method;
		const data = await fetch(url,sendData).then((result) =>{
									 if(loadingLayer){
										 loadingLayer.style.display = "none";
									 }
									 return result.json();
								 })
								 .catch((error) => {
									 console.error("gfn_asyncCall errorr:",error);
								});
	
		return data;
    } catch (err) {
    	console.error("gfn_asyncCall errorr:", err);
    	gfn_goBackUrl();
    }
}

const gfn_asyncCall = async (url,method,frm,requiredParams,comp,callBackFn,msgYn) => {
	try{
		if(requiredParams != undefined && requiredParams != null){
			if(!gfn_valid(frm,requiredParams,null,comp)){
				return;
			}
		}
		let loadingLayer = document.querySelector('.loadingLayer');
		loadingLayer.style.display = "block";
		
		let _method = method===undefined?'POST':method;
		const data = await fetch(url, {method: _method
		                               , headers: {'Content-Type': 'application/json'} // 'Content-Type':
		                               , body: JSON.stringify(gfn_formToJson(frm))
								 }).then((result) =>{
									 loadingLayer.style.display = "none";
									 return result.json();
								 })
								 .catch((error) => {
									 console.error("gfn_asyncCall errorr:",error);
								});
		if(msgYn === undefined){
			if(data["success_msg"] != undefined){
				msgCallBack(data["success_msg"],true,callBackFn);
			}
			if(data["fail_msg"] != undefined){
				msgCallBack(data["fail_msg"],false);
			}
		}
		
		return data;
    } catch (err) {
    	console.error("gfn_asyncCall errorr:", err);
    	gfn_goBackUrl();
    }
}

const gfn_asyncJsonCall = async (url,method,jsonParam,callBackFn,requiredParams,msgYn) => {
	try{
		if(method.toUpperCase() === 'POST' && (jsonParam === undefined || jsonParam === null || Object.keys(jsonParam).length === 0)){
			console.error("param is null........");
			return false;
		}
		if(requiredParams != undefined && requiredParams != null){
			if(!gfn_validJson(jsonParam,requiredParams)){
				return;
			}
		}
		let loadingLayer = document.querySelector('.loadingLayer');
		loadingLayer.style.display = "block";
		let sendData = {method: method
                      , headers: {'Content-Type': 'application/json'} 
	                  }; 
		if(method.toUpperCase() === 'POST'){
			sendData.body = JSON.stringify(jsonParam);
		}
		else{
			sendData.params = JSON.stringify(jsonParam);
		}
		
		let _method = method===undefined?'POST':method;
		const data = await fetch(url, sendData).then((result) =>{
			                         loadingLayer.style.display = "none";
									 return result.json();
								 });
		if(msgYn === undefined){
			if(data["success_msg"] != undefined){
				msgCallBack(data["success_msg"],true,callBackFn);
			}
			if(data["fail_msg"] != undefined){
				console.log("gfn_asyncJsonCall fail_msg:",data["fail_msg"]);
				msgCallBack(data["fail_msg"],false);
			}
		}
		return data;
    } catch (err) {
    	console.error("gfn_asyncJsonCall errorr:", err);
    	gfn_goBackUrl();
    }
}

const gfn_asyncDownByForm = async (url,method,frm) => {
	try{
		
		let loadingLayer = document.querySelector('.loadingLayer');
		loadingLayer.style.display = "block";
		let frmData = gfn_formToJson(frm);
		let sendData = {method: method
                     , headers: {'Content-Type': 'application/json'} 
		              }; 
		sendData.body = JSON.stringify(frmData);
         console.log("frmData:",frmData);
		let _method = method===undefined?'POST':method;
		await fetch(url, sendData).then((result) =>{
			                         loadingLayer.style.display = "none";
			                         return result.blob()

								 }).then((data) => {
									  let anch = document.createElement("a");
									  const url = window.URL.createObjectURL(data);
									  anch.href = url;
									  if(frmData.searchType === 'CONN'){
										 anch.download = "접속이력_"+gfn_getDay('-');
									  }
									  else if(frmData.searchType === 'VIEW'){
										  anch.download = "자료열람이력_"+gfn_getDay('-');
									  }
									  else{
										  if(frmData.upOrDown === 'U'){
											  anch.download = "자료업로드이력_"+gfn_getDay('-');
										  }
										  else if(frmData.upOrDown === 'D'){
											  anch.download = "자료다운로드로드이력_"+gfn_getDay('-');
										  }
										  else{
											  
											  anch.download = "데이타베이스 자료_"+gfn_getDay('-');
										  }
									  }
									  
									  anch.click();
									  window.URL.revokeObjectURL(url);
								});
    } catch (err) {
    	console.error("gfn_asyncJsonCall errorr:", err);
    	gfn_goBackUrl();
    }
}

const gfn_asyncDown = async (url,method,jsonParam) => {
	try{
		if(method.toUpperCase() === 'POST' && (jsonParam === undefined || jsonParam === null || Object.keys(jsonParam).length === 0)){
			console.error("param is null........");
			return false;
		}
		let loadingLayer = document.querySelector('.loadingLayer');
		loadingLayer.style.display = "block";
		let sendData = {method: method
                      , headers: {'Content-Type': 'application/json'} 
	                  }; 
		if(method.toUpperCase() === 'POST'){
			sendData.body = JSON.stringify(jsonParam);
		}
		else{
			sendData.params = JSON.stringify(jsonParam);
		}
		
		let _method = method===undefined?'POST':method;
		await fetch(url, sendData).then((result) =>{
			                         loadingLayer.style.display = "none";
			                         return result.blob()

								 }).then((data) => {
									  let anch = document.createElement("a");
									  const url = window.URL.createObjectURL(data);
									  anch.href = url;
									  if(jsonParam.searchType === 'CONN'){
										  anch.download = "접속이력_"+gfn_getDay('-');
									  }
									  else if(jsonParam.searchType === 'VIEW'){
										  anch.download = "자료열람이력_"+gfn_getDay('-');
									  }
									  else{
										  if(jsonParam.upOrDown === 'U'){
											  anch.download = "자료업로드이력_"+gfn_getDay('-');
										  }
										  else if(jsonParam.upOrDown === 'U'){
											  anch.download = "자료다운로드로드이력_"+gfn_getDay('-');
										  }
										  else{
											  anch.download = "데이타베이스 자료_"+gfn_getDay('-');
										  }
										  
									  }
									  
									  anch.click();
									  window.URL.revokeObjectURL(url);
								});
    } catch (err) {
    	console.error("gfn_asyncJsonCall errorr:", err);
    	gfn_goBackUrl();
    }
}


const gfn_asyncJsonCallWithFile = async (url,method,frm,requiredParams,jsonParam,uploadTp) => {
	if(requiredParams != undefined && requiredParams != null){
		if(!gfn_validJson(jsonParam,requiredParams)){
			return;
		}
	}
	
	let sendData = {method: method
                  , headers: {'Content-Type': 'application/json'} 
                  }; 

	let formData = new FormData();
	for (var i = 0; i < filesArr.length; i++) {
        if (!filesArr[i].isDelete) {
            formData.append("file", filesArr[i]);
        } 
    }
	//let appendParam = {};
	//appendParam.fileParam = ;
	//appendParam.sourceCd = jsonParam.sourceCd;
	formData.append("fileParam", JSON.stringify(fileParam));
	formData.append("sourceCd", jsonParam.sourceCd);
	formData.append("collectionDt", jsonParam.collectionDt);
	formData.append("collectionPos", jsonParam.collectionPos);
	formData.append("collectionDeviceCd", jsonParam.collectionDeviceCd);
    //console.log("appendParam:",JSON.stringify(appendParam));
	
	//console.log("formData:",formData);
	let loadingLayer = document.querySelector('.loadingLayer');
	loadingLayer.style.display = "block";
	let _method = method===undefined?'POST':method;
	let uploadUrl = "/uploadFileDb";
	if(uploadTp != undefined && uploadTp === 'file'){
		uploadUrl = "/upload";
	}
	const data = await fetch(uploadUrl, {method: _method
                                       , body: formData
									    }).then((response) => response.json())
									      .then((result) => {
											  if(result["success_msg"] != undefined){
												  if(uploadTp != undefined && uploadTp === 'file'){
													  jsonParam["fileData"] = result["fileList"];
												  }else{
													  jsonParam["fileId"] = result["fileId"];
													  jsonParam["isChangeFile"] = result["isChangeFile"];
												  }
												  console.log("jsonParam:",JSON.stringify(jsonParam));
												  if(method.toUpperCase() === 'POST'){
													  sendData.body = JSON.stringify(jsonParam);
												  }
												  else{
													sendData.params = JSON.stringify(jsonParam);
												  }
												  return fetch(url,sendData).then((response) => { loadingLayer.style.display = "none"; return response.json(); });
										           
											  }
											  else if(result["fail_msg"] != undefined){
												  msgCallBack(result["fail_msg"],false);
											  }
									   }).catch((error) => {
										   console.error(error);
										   msgCallBack(msg.exceedFileSize,false);
									   });
	loadingLayer.style.display = "";
	return data;
	
}
const gfn_asyncCallWithFile = async (url,method,frm,requiredParams,uploadTp) => {
	try{
		if(requiredParams != undefined && requiredParams != null){
			if(!gfn_valid(frm,requiredParams,true)){
				return;
			}
		}
	
		let formData = new FormData();
		for (var i = 0; i < filesArr.length; i++) {
	        if (!filesArr[i].isDelete) {
	            formData.append("file", filesArr[i]);
	            
	        } 
	    }
		
		let loadingLayer = document.querySelector('.loadingLayer');
		loadingLayer.style.display = "block";
		let jsonObj = gfn_formToJson(frm);
		let _method = method===undefined?'POST':method;
		let uploadUrl = "/uploadFileDb";
		if(uploadTp != undefined && uploadTp === 'file'){
			uploadUrl = "/upload";
		}
		const data = await fetch(uploadUrl, {method: _method
		                                   , body: formData
											 }).then((response) => response.json())
											   .then((result) => {
												   if(result["success_msg"] != undefined){
													   if(uploadTp != undefined && uploadTp === 'file'){
														   jsonObj["fileData"] = result["fileList"];
													   }else{
														   jsonObj["fileId"] = result["fileId"];
													   }
													   console.log("jsonObj:",JSON.stringify(jsonObj));
													   return fetch(url,{method: _method
										    	                , headers: {'Content-Type': 'application/json'} 	
				                                                , body: JSON.stringify(jsonObj)
											           }).then((response) => { loadingLayer.style.display = "none"; return response.json(); });
											              /*
					                                     .then((result) => {
					                                    	 return result;
												             //loading_modal.classList.add("hidden");
					                                    	 /*
												             for(let key in result) {
												 				if(key.indexOf('success_msg') > -1){
												 					msgCall(result[key]+"<br> 이전 페이지로 이동합니다.",true,null,true);
												 					break;
												 				}
												 				if(key.indexOf('fail_msg') > -1){
												 					msgCall(result[key],false);
												 					break;
												 				}
												 				if(key.indexOf('valid') > -1){
												 					let valid = document.getElementById(key); 
												 					valid.innerHTML = result[key];
												 				}
												 			 }*/
											             /*
											             }).catch((error) => {
												              console.error(error);
											             });
		                                                 */
												   }
												   else if(result["fail_msg"] != undefined){
													   return false;
												   }
											   }).catch((error) => {
												   console.error(error);
											   });
		/*
		if(data["success_msg"] != undefined){
			msgCall(result[key]+"<br> 이전 페이지로 이동합니다.",true,null,true);
		}
		if(data["fail_msg"] != undefined){
			msgCall(result[key],false);
		}
		*/
		console.log("data:",data);
		return data;
    } catch (err) {
    	console.error("call errorr:", err);
    	gfn_goBackUrl();
    }
}


const gfn_asyncUrlCall = async (url,method,params,modalYn) => {
	try{
		let _modalYn = modalYn===undefined?false:modalYn;
		let _method = method===undefined?'POST':method;
		let sendData = {method: _method
                      , headers: {'Content-Type': 'application/json'} 
		              };
		
		if(_method === 'POST'){
			sendData.body = JSON.stringify(params);
		}
		else{
			sendData.params = params;
		}
		console.log("sendData:",sendData);

		if(!_modalYn){
			let sessionData = gfn_getStorage(nis.storageKey);
			if(sessionData != null || sessionData != undefined){
				let curUrl = gfn_getStorageItem("curUrl");
				if(curUrl != undefined && curUrl != '' && url != curUrl){
					gfn_setStorageItem("preUrl",curUrl);
				}
				gfn_setStorageItem("curUrl",url);
			}
		}
		const result = await fetch(url, sendData);
		const data = await result.text();
		return data;
    } catch (err) {
    	console.error("gfn_asyncUrlCall errorr:", err);
    }
}


const gfn_codes =  async () => {
	try{
		if(localStorage.hasOwnProperty(nis.codeKey)){
			return "";
		}
		const data = await fetch("/codes"
                               , {method: "GET"
		                        , headers: {'Content-Type': 'application/json'} // 'Content-Type':
						         }).then((result)=> { return result.json(); });
		gfn_setStorage(nis.codeKey,JSON.stringify(data));
    } catch (err) {
    	console.error("gfn_codes errorr:", err);
    }
}



const gfn_goBackUrl = () => {
	let params = {};
	let url = gfn_getStorageItem("preUrl");
	//console.log("gfn_goBackUrl preUrl:",url)
	let isMenu = false;
	const menuList = document.querySelector('.menu-list').querySelectorAll("a");
	if(menuList){
		menuList.forEach((menu) => {
		   const menuAnchHtml = menu.innerHTML;
		   if(menuAnchHtml.indexOf(url) > 0){
			   const menuAnch = menu.querySelector('a');
			   isMenu = true;
			   menuAnch.click();
			   
		   }
	    });
	}
	if(!isMenu){
		document.querySelector('.contentBody').innerHTML = '';
		gfn_asyncUrlCall(url,'GET',JSON.stringify(params)).then((data) => {
			dynimicContentCall(data);
		});
	}
}


/**
 * 
 * desc : valid msg innerhtml 제거
 */
const gfn_removeValidHtml = (frm,tag) => {
	try{
		let valids = frm.getElementsByTagName(tag);
	    for (let valid of valids) {
	        if(valid.id.indexOf('valid') > -1){
	        	valid.innerText = '';
	        }
	    }
	}catch(e){
		console.error("gfn_removeValidHtml error:",e);
	}

}

const gfn_initObj = (obj) => {
	try{
		let inputs = obj.getElementsByTagName('input');
		for (let input of inputs) {
			if(input.type != 'radio'){
				input.value = '';		
			}
			
		}
		
		let selects = obj.getElementsByTagName('select');
		if(selects){
			for(let select of selects){
				select.querySelectorAll('option')[0].selected = true;
			}
		}
		
	}catch(e){
		console.log("gfn_initObj:",e)
	}
	
}

/**
 * 
 * desc : valid msg innerhtml 제거
 */
const gfn_valid = (frm,requiredParams,fileYn,comp) => {
	let isValid = true;
	let _fileYn = (fileYn===undefined || fileYn === null)?false:fileYn;
	try{
	   let data = gfn_formToJson(frm);
	   for(let key in requiredParams){
		   if(!data.hasOwnProperty(key) || data[key] == null || data[key].trim() === ''){  
			   // console.log(key,"is required......");
			   if(comp != undefined){
				   console.log("comp:",comp);
				   comp.querySelector('#valid_'+key).innerText = requiredParams[key]+"는/은 필수 입력입니다.";
			   }
			   else{
				   document.querySelector('#valid_'+key).innerText = requiredParams[key]+"는/은 필수 입력입니다.";
			   }
			   
			   isValid = false;
		   }
	   }
	   if(_fileYn){
		   let existUploadFile = false;
		   for (var i = 0; i < filesArr.length; i++) {
		        if (!filesArr[i].isDelete) {
		        	existUploadFile = true;
		        	break;
		        } 
		   }
		   if(!existUploadFile){
			   document.querySelector('#valid_file').innerText = "파일을 추가하세요.";
			   isValid = false;
		   }
	   }
	  
	   return isValid;
	}catch(e){
		console.error("gfn_valid error:",e);
	}
}

const gfn_validJson = (jsonParam,requiredParams,fileYn,comp) => {
	console.log("gfn_validJson comp:",comp);
	let isValid = true;
	let _fileYn = (fileYn===undefined || fileYn === null)?false:fileYn;
	try{
	   for(let key in requiredParams){
		   if(!jsonParam.hasOwnProperty(key) || jsonParam[key] == null || jsonParam[key].trim() === ''){  
			   // console.log(key,"is required......");
			   if(comp != undefined){
				   console.log("key:",key);
				   comp.querySelector('#valid_'+key).innerText = requiredParams[key]+"는/은 필수 입력입니다.";
			   }else{
				   document.querySelector('#valid_'+key).innerText = requiredParams[key]+"는/은 필수 입력입니다.";
			   }
			   
			  
			   isValid = false;
		   }
	   }
	   if(_fileYn){
		   let existUploadFile = false;
		   for (var i = 0; i < filesArr.length; i++) {
		        if (!filesArr[i].isDelete) {
		        	existUploadFile = true;
		        	break;
		        } 
		   }
		   if(!existUploadFile){
			   document.querySelector('#valid_file').innerText = "파일을 추가하세요.";
			   isValid = false;
		   }
	   }
	  
	   return isValid;
	}catch(e){
		console.error("gfn_validJson error:",e);
	}

}


const gfn_isMobile = () => {
	let agentKind = "etc";
	let agent = navigator.userAgent;

	if (agent.indexOf("AppleWebKit") != -1 || agent.indexOf("Opera") != -1) {
		if (agent.indexOf("Android") != -1 || agent.indexOf("J2ME/MIDP") != -1) {
			agentKind = "android";
		} else if (agent.indexOf("iPhone") != -1) {
			agentKind = "iphone";
		} else if (agent.indexOf("iPad") != -1) {
			agentKind = "iphone";
		}
	} else {
		agentKind = "etc";
	}
	if (agentKind != "etc") {
		return true; // etc가 아닐경우, 모바일
	} else {
		return false;
	}
}


const gfn_getDate = () => {
	try{
		let date = new Date(); // 현재 날짜(로컬 기준) 가져오기
	    let utc = date.getTime() + (date.getTimezoneOffset() * 60 * 1000); // uct
																			// 표준시
																			// 도출
	    let kstGap = 9 * 60 * 60 * 1000; // 한국 kst 기준시간 더하기
	    let today = new Date(utc + kstGap); // 현재 시간
	    return today;
	}catch(e){ console.error("# gfn_clearStorage error msg:",e);} 
	return true;
};


const gfn_getDay = (frmt) => {
	try{
		let _frmt = frmt===undefined?'.':frmt;
		let today = gfn_getDate();
		return today.getFullYear()+_frmt+(parseInt(today.getMonth())+1)+_frmt+today.getDate();
        
	}catch(e){ console.error("# gfn_clearStorage error msg:",e);} 
	return true;
};

const gfn_getDiffDay = (day,frmt) => {
	try{
		let _frmt = frmt===undefined?'.':frmt;
		let today = gfn_getDate();
		let diffDay = new Date();
		diffDay.setDate(today.getDate()+day);
		return diffDay.getFullYear()+_frmt+(parseInt(diffDay.getMonth())+1)+_frmt+diffDay.getDate();
	}catch(e){ console.error("# gfn_clearStorage error msg:",e);} 
	return true;
};


const gfn_validDay = (sDay,eDay) => {
	let sDates = sDay.split(".");    
	let eDates = eDay.split(".");    
	let stDate = new Date(sDates[0], sDates[1], sDates[2]);
	let endDate = new Date(eDates[0], eDates[1], eDates[2]);
	  
	let btMs = endDate.getTime() - stDate.getTime() ;
	let btDay = btMs / (1000*60*60*24) ;
    if(btDay >= 0){
    	return false;
    }
    else{
    	return true;
    }
};



/**
 * Storage Item 조회
 * 
 * @return NIS Storage
 */
const gfn_getStorage = (key) => {
	let _key = key===undefined?nis.storageKey:key;
	try{
		if(localStorage.hasOwnProperty(key)){
			return localStorage.getItem(_key);
		}
		else{
			console.log("NIS Storage is not found");
			return null;
		}
		
	}catch(e){ console.error("# gfn_getStorageItem error msg: ",e);} 
	
};

/**
 * Storage Item 조회
 * 
 * @return rtnVal
 */
const gfn_getStorageItem = (key,isClear) => {
	let isClr = isClear === undefined?true:isClear;
	try{
		let nisStorage = JSON.parse(CryptoJS.AES.decrypt(gfn_getStorage(nis.storageKey),nis.cryptKey).toString(CryptoJS.enc.Utf8));
		if(nisStorage.hasOwnProperty(key)){
			return nisStorage[key];
		}
		else{
			//console.log(key," is not exist in the Nis Storage!");
		}
		if(isClr) {
			if(nisStorage.hasOwnProperty(key)){
				delete nisStorage.key;
			}
			gfn_setStorage(nis.storageKey,CryptoJS.AES.encrypt(JSON.stringify(nisStorage),nis.cryptKey));
		}
	}catch(e){ console.error("# gfn_getStorageItem error msg: ",e);} 
	return '';
};


/**
 * Storage 조회
 * 
 * @return rtnVal
 */
const gfn_setStorage = (key,val) => {
	try{
		localStorage.setItem(key,val);
	}catch(e){ console.error("# gfn_getStorageItem error msg: ",e);} 
};

const gfn_getCodeVal = (grpCd,cd) => {
	try{
		const codeCache = JSON.parse(gfn_getStorage(nis.codeKey))["codes"];
		for(let i=0;i<codeCache.length;i++){
			if(grpCd === codeCache[i]["grpCd"] && cd === codeCache[i]["cd"]){
				return codeCache[i]["cdNm"];
			}
		}
		console.error("코드그룹 및 코드에 일치하는 데이타가 없습니다.");
		return cd;
	}catch(e){ console.error("# gfn_getCodeVal error msg: ",e);} 
};

const gfn_getCodeGrp = (grpCd) => {
	let grpCode = new Array();
	try{
		const codeCache = JSON.parse(gfn_getStorage(nis.codeKey))["codes"];
		for(let i=0;i<codeCache.length;i++){
			if(grpCd === codeCache[i]["grpCd"]){
				grpCode.push(codeCache[i]);
				
			}
		}
		return grpCode;
	}catch(e){ console.error("# gfn_getCodeVal error msg: ",e);} 
};

const gfn_setCodeGrpObj = (grpCd,comp,select,filter) => {
	if(select){
		let option = document.createElement('option');
		option.append('선택하세요');
		option.setAttribute('value','');
		comp.appendChild(option);
	}
	try{
		const codeCache = JSON.parse(gfn_getStorage(nis.codeKey))["codes"];
		for(let i=0;i<codeCache.length;i++){
			if(grpCd === codeCache[i]["grpCd"]){
				if(filter && filter === codeCache[i]["cdFilter"]){
					let option = document.createElement('option');
					option.setAttribute('value',codeCache[i]["cd"]);
					option.append(codeCache[i]["cdNm"]);
					comp.appendChild(option);
				}
				if(!filter){
					let option = document.createElement('option');
					option.setAttribute('value',codeCache[i]["cd"]);
					option.append(codeCache[i]["cdNm"]);
					comp.appendChild(option);
				}
				
			}
		}
	}catch(e){ console.error("# gfn_getCodeVal error msg: ",e);} 
};



/**
 * Storage Item 조회
 * 
 * @return rtnVal
 */
const gfn_setStorageItem = (key,val) => {
	try{
		let nisStorage = JSON.parse(CryptoJS.AES.decrypt(gfn_getStorage(nis.storageKey),nis.cryptKey).toString(CryptoJS.enc.Utf8));
		if(nisStorage.hasOwnProperty(key)){
			console.warn(key," is already exist in the Nis Storage!");
		}
		nisStorage[key] = val;
		gfn_setStorage(nis.storageKey,CryptoJS.AES.encrypt(JSON.stringify(nisStorage),nis.cryptKey));

	}catch(e){ console.error("# gfn_getStorageItem error msg: ",e);} 
};


/**
 * Nis StorageItem 삭제
 * 
 * @param key
 * @return sessionStorage
 */
const gfn_removeStorageItem = (key) => {
	try{
	
		let nisStorage = JSON.parse(CryptoJS.AES.decrypt(gfn_getStorage(nis.storageKey),nis.cryptKey).toString(CryptoJS.enc.Utf8));
		if(nisStorage.hasOwnProperty(key)){
			delete nisStorage.key;
			gfn_setStorage(nis.storageKey,CryptoJS.AES.encrypt(JSON.stringify(nisStorage),nis.cryptKey));
		}
		else{
			console.error(key,"is not exist in the Nis Storage!");
		}
		
	}catch(e){ console.error("# gfn_removeStorageItem error msg: ",e);} 
	return sessionStorage;
};

/**
 * NIS Storage 삭제
 * 
 * @param key
 * @return NIS Storage
 */
const gfn_removeStorage = (key) => {
	try{
		if(localStorage.hasOwnProperty(key)){
			localStorage.removeItem(key);
		}
		else{
			console.error(key,"is not exist in localStorage!");
		}
	}catch(e){ console.error("# gfn_removeStorage error msg: ",e);} 
	return sessionStorage;
};
/**
 * NIS Storage 청소
 * 
 * @return boolean
 */
const gfn_clearStorage = () => {
	try{
		localStorage.removeItem(nis.storageKey);
		localStorage.removeItem(nis.codeKey);
	}catch(e){ console.error("# gfn_clearStorage error msg:",e);} 
	return true;
};


const gfn_dateFrmt = (timestamp,frmt) => {
	if(gfn_nullValue(timestamp) === ''){
		return '';
	}
	
	let date = new Date(timestamp);
	
	if(frmt.indexOf('yy') > -1){
		frmt = frmt.replace('yy',date.getFullYear());
	}
	
    if(frmt.indexOf('mm') > -1){
    	frmt = frmt.replace('mm',(date.getMonth()+1)<10?"0".concat(date.getMonth()+1):date.getMonth()+1);
	}
    
    if(frmt.indexOf('dd') > -1){
    	frmt = frmt.replace('dd',(date.getDate())<10?"0".concat(date.getDate()):date.getDate());
	}
    
    if(frmt.indexOf('hh') > -1){
    	frmt = frmt.replace('hh',(date.getHours())<10?"0".concat(date.getHours()):date.getHours());
	}
    
    if(frmt.indexOf('mi') > -1){
    	frmt = frmt.replace('mi',(date.getMinutes())<10?"0".concat(date.getMinutes()):date.getMinutes());
	}
    
    if(frmt.indexOf('ss') > -1){
    	frmt = frmt.replace('ss',(date.getSeconds())<10?"0".concat(date.getSeconds()):date.getSeconds());
	}
    return frmt;
	/*
    var d = new Date(timestamp * 1000), // Convert the passed timestamp to milliseconds
    yyyy = d.getFullYear(),
    mm = ('0' + (d.getMonth() + 1)).slice(-2),  // Months are zero based. Add leading 0.
    dd = ('0' + d.getDate()).slice(-2),         // Add leading 0.
    hh = d.getHours(),
    h = hh,
    min = ('0' + d.getMinutes()).slice(-2),     // Add leading 0.
    ampm = 'AM',
    time;
        
    if (hh > 12) {
        h = hh - 12;
        ampm = 'PM';
    } else if (hh === 12) {
        h = 12;
        ampm = 'PM';
    } else if (hh == 0) {
        h = 12;
    }
    
    // ie: 2013-02-18, 8:35 AM  
    time = yyyy + '년' + mm + '월' + dd + '일' + h + ':' + min + ' ' + ampm;
        
    return time;
    */
    /*
	const regExp = /[-]/g;
	let dateVal = timestamp.split("T")[0];
	if(frmt === undefined || frmt ===''){
		return timestamp.split("T")[0];
	}
	else if(frmt === '-'){
		return timestamp.split("T")[0];
	}else if(frmt === '.'){
		if (regExp.test(dateVal)) {
			dateVal = dateVal.replace(regExp, '.');
		}
		return dateVal;
		
	}else if(frmt === '/'){
		if (regExp.test(dateVal)) {
			dateVal = dateVal.replace(regExp, '/');
		}
		return dateVal;
	}
	*/
}

const gfn_dateTmFrmt = (timestamp,frmt) => {
	const regExp = /[-]/g;
	let dateVal = timestamp.split("T")[0];
	if(frmt === undefined || frmt ===''){
		return timestamp.split("T")[0];
	}
	else if(frmt === '-'){
		return timestamp.split("T")[0];
	}else if(frmt === '.'){
		if (regExp.test(dateVal)) {
			dateVal = dateVal.replace(regExp, '.');
		}
		return dateVal;
		
	}else if(frmt === '/'){
		if (regExp.test(dateVal)) {
			dateVal = dateVal.replace(regExp, '/');
		}
		return dateVal;
	}
}

const gfn_nullValue = (val) => {
	if(val === undefined || val === null || val === 'undefined'){
		return '';
	}
	return val;
}


const popUnit = () => {
	let list = null;
	gfn_asyncCall("/unitInfo",'GET',{}).then((data) => {
		for(key in data) {
            if(key.indexOf('list') > -1){
               	list = data[key];
			}
		}
		const treeBox = document.querySelector('.treeBox');
		treeBox.innerHTML = '';
		let treejs = createInitTree(list,false);
		if(list != null && list.length > 0){
			createTreeNoChk(list,treejs);
		}

	}).then(() => {
		document.querySelector('.treeBox').querySelectorAll('.treejs-label').forEach((label) => {
			let li = label.parentNode;
			if(li.getAttribute('data-id') === document.getElementById('unitId').value){
				label.classList.add('on');
				return false;
    		}
		});  	
		
	});
	let popLayer = document.querySelector('.unitPop');
	popLayer.style.display = "block";
}

const showCheckboxes = (selectBox) => {
	let checkboxes = selectBox.parentNode.querySelector(".checkboxes");
    if (checkboxes.style.display === 'none') {
    	checkboxes.style.display = "block";
    } else {
        checkboxes.style.display = "none";
    }
}

const schInputChk = (obj,val) => {
   
    let parentComp = obj.parentNode.parentNode.parentNode.parentNode.parentNode;
	let schItem = parentComp.querySelector('.'+obj.id);;
	if(obj.checked){
		schItem.style.display='block';
		if(obj.id === 'unitChk'){
			if(val === undefined || val === ''){
				parentComp.querySelector('#schUnitNm').value = '';
				parentComp.querySelector('#schUnitId').value = '';
			}else{
				parentComp.querySelector('#schUnitNm').value = val.schUnitNm;
				parentComp.querySelector('#schUnitId').value = val.schUnitId;
			}
		}
		if(obj.id === 'collectionChk'){
			if(val === undefined || val === ''){
				parentComp.querySelector('#schCollection').value = '';
			}else{
				parentComp.querySelector('#schCollection').value = val.schCollection;
			}
			
		}
		else if(obj.id === 'stillOrMovieChk'){  
			if(val === undefined || val === ''){
				parentComp.querySelector('#schStillOrMovie').value = '';
			}else{
				parentComp.querySelector('#schStillOrMovie').value = val.schStillOrMovie;
			}
		}
		else if(obj.id === 'dayOrNightChk'){  
			if(val === undefined || val === ''){
				parentComp.querySelector('#schDayOrNight').value = '';
			}else{
				parentComp.querySelector('#schDayOrNight').value = val.schDayOrNight;
			}
		}
		else if(obj.id === 'equipChk'){  
			if(val === undefined || val === ''){
				parentComp.querySelector('#blockYn').value = '';
			}else{
				parentComp.querySelector('#blockYn').value = val.blockYn;
			}
		}
	}else{
		schItem.style.display='none';
		if(obj.id === 'unitChk'){
			parentComp.querySelector('#schUnitNm').value = '';
			parentComp.querySelector('#schUnitId').value = '';
		}
		else if(obj.id === 'userChk'){  
			parentComp.querySelector('#schUserNm').value = '';
		}
		else if(obj.id === 'objChk'){  
			parentComp.querySelector('#schDataNm').value = '';
		}
		else if(obj.id === 'workerChk'){  
			parentComp.querySelector('#schWorkerNm').value = '';
		}
		
	}
}

const qryInit = () => {

	let selMulti = document.querySelector('.sel-multi');
	if(selMulti){
		let schUnitNm = selMulti.parentNode.querySelector('#schUnitNm');
		if(schUnitNm){
			schUnitNm.parentNode.style.display = 'none';
			schUnitNm.value = '';
			selMulti.parentNode.querySelector('#schUnitId').value = '';
			let unitChk = selMulti.parentNode.querySelector('#unitChk');
			if(unitChk.checked){
				unitChk.checked = false;
			}
		}
		let schCollection = selMulti.parentNode.querySelector('#schCollection');
		if(schCollection){
			schCollection.parentNode.style.display = 'none';
			schCollection.value = '';
			let collectionChk = selMulti.parentNode.querySelector('#collectionChk');
			if(collectionChk.checked){
				collectionChk.checked = false;
			}
		}
		let schStillOrMovie = selMulti.parentNode.querySelector('#schStillOrMovie');
		if(schStillOrMovie){
			schStillOrMovie.parentNode.style.display = 'none';
			schStillOrMovie.value = '';
			let stillOrMovieChk = selMulti.parentNode.querySelector('#stillOrMovieChk');
			if(stillOrMovieChk.checked){
				stillOrMovieChk.checked = false;
			}
		}
		let schDayOrNight = selMulti.parentNode.querySelector('#schDayOrNight');
		if(schDayOrNight){
			schDayOrNight.parentNode.style.display = 'none';
			schDayOrNight.value = '';
			let dayOrNightChk = selMulti.parentNode.querySelector('#dayOrNightChk');
			if(dayOrNightChk.checked){
				dayOrNightChk.checked = false;
			}
		}
		let blockYn = selMulti.parentNode.querySelector('#blockYn');
		if(blockYn){
			blockYn.parentNode.style.display = 'none';
			blockYn.value = '';
			let equipChk = selMulti.parentNode.querySelector('#equipChk');
			if(equipChk.checked){
				equipChk.checked = false;
			}
		}
		let schUserNm = selMulti.parentNode.querySelector('#schUserNm');
		if(schUserNm){
			schUserNm.parentNode.style.display = 'none';
			schUserNm.value = '';
			let userChk = selMulti.parentNode.querySelector('#userChk');
			if(userChk.checked){
				userChk.checked = false;
			}
		}
		let schDataCd = selMulti.parentNode.querySelector('#schDataCd');
		if(schDataCd){
			schDataCd.parentNode.style.display = 'none';
			schDataCd.value = '';
			let objChk = selMulti.parentNode.querySelector('#objChk');
			if(objChk.checked){
				objChk.checked = false;
			}
		}
		let schWorkerNm = selMulti.parentNode.querySelector('#schWorkerNm');
		if(schWorkerNm){
			schWorkerNm.parentNode.style.display = 'none';
			schWorkerNm.value = '';
			let workerChk = selMulti.parentNode.querySelector('#workerChk');
			if(workerChk.checked){
				workerChk.checked = false;
			}
		}
		let schEtc = selMulti.parentNode.querySelector('#schEtc');
		if(schEtc){
			schEtc.value = '';
		}
	}
}

const searchInit = (multiComp,schGrpType) => {
	
	let _schGrpType = schGrpType===undefined?'':schGrpType;
	multiComp.innerHTML = '';
	let multiHtml = '';
	multiHtml += '<div class="multiselect">';
	multiHtml += '<div class="selectBox" onclick="showCheckboxes(this)">';
	multiHtml += '<select class="basic">';
	multiHtml += '<option>전체</option>';
	multiHtml += '</select>';
	
		
	multiHtml += '<div class="overSelect"></div>';
	multiHtml += '</div>';
	multiHtml += '<div class="checkboxes" style="display:none;">';
	if(_schGrpType === '' || _schGrpType === '1' || _schGrpType === '5'){
		multiHtml += '<label for="unitChk"><input type="checkbox" id="unitChk" onclick="schInputChk(this);">부대명</label>';
		multiHtml += '<label for="collectionChk"><input type="checkbox" id="collectionChk" onclick="schInputChk(this)">탐지수단</label>';
		if( _schGrpType === '' ){
			multiHtml += '<label for="stillOrMovieChk"><input type="checkbox" id="stillOrMovieChk" onclick="schInputChk(this)">자료구분</label>';
		}
		multiHtml += '<label for="dayOrNightChk"><input type="checkbox" id="dayOrNightChk" onclick="schInputChk(this)">주/야 구분</label>';
		multiHtml += '<label for="objChk"><input type="checkbox" id="objChk" onclick="schInputChk(this)">함정/선박 종류</label>';
		multiHtml += '<label for="workerChk"><input type="checkbox" id="workerChk" onclick="schInputChk(this)">작업자</label>';
		if( _schGrpType === '5' ){
			//multiHtml += '<label for="etcChk"><input type="checkbox" id="etcChk" onclick="schInputChk(this)">특이사항</label>';
		}
	}
	else if(_schGrpType === '2' ){ //기기관리
		multiHtml += '<label for="equipChk"><input type="checkbox" id="equipChk" onclick="schInputChk(this)">기기상태</label>';
		multiHtml += '<label for="userChk"><input type="checkbox" id="userChk" onclick="schInputChk(this)">사용자</label>';
	}
	else if(_schGrpType === '3' ){ //사용자관리
		multiHtml += '<label for="unitChk"><input type="checkbox" id="unitChk" onclick="schInputChk(this)">부대명</label>';
		multiHtml += '<label for="userChk"><input type="checkbox" id="userChk" onclick="schInputChk(this)">사용자</label>';
	}
	else if(_schGrpType === '4' ){ //권한관리
		multiHtml += '<label for="unitChk"><input type="checkbox" id="unitChk" onclick="schInputChk(this)">부대명</label>';
	}
	multiHtml += '</div>';
	
	
	
	multiHtml += '</div>';
	multiComp.innerHTML = multiHtml;
	
	let searchItem ="";
	searchItem += '<div class="searchItem" style="width:155px;"></div>';
	if(_schGrpType === '' || _schGrpType === '1' || _schGrpType === '5'){
		searchItem += '<div class="searchItem unitChk" style="display:none;">';
		searchItem += '<input type="search" class="search" id="schUnitNm" name="schUnitNm" style="width:150px;" title="검색" placeholder="부대를 검색." readonly>';
		searchItem += '<input type="hidden" id="schUnitId" name="schUnitId" value="">';
		searchItem += '</div>';
		searchItem += '<div class="searchItem collectionChk" style="display:none;">';
		searchItem += '<select id="schCollection" name="schCollection" class="basic" >';
		let collectionCds = gfn_getCodeGrp('006');
		searchItem += "<option value=''>전체</option>";
		collectionCds.forEach((cd,index) => {
			if(cd.cd != null){
				searchItem += "<option value='"+cd.cd+"'>"+cd.cdNm+"</option>";
			}
		});
		
		searchItem += '</select>';
		searchItem += '</div>';
		if( _schGrpType === '' ){
			searchItem += '<div class="searchItem stillOrMovieChk" style="display:none;">';
			searchItem += '<select id="schStillOrMovie" name="schStillOrMovie" class="basic" >';
			let stillOrMovieCds = gfn_getCodeGrp('003');
			searchItem += "<option value=''>전체</option>";
			stillOrMovieCds.forEach((cd,index) => {
				if(cd.cd != null){
					searchItem += "<option value='"+cd.cd+"'>"+cd.cdNm+"</option>";
				}
			});
			searchItem += '</select>';
			searchItem += '</div>';
		}
		
		
		searchItem += '<div class="searchItem dayOrNightChk" style="display:none;">';
		searchItem += '<select id="schDayOrNight" name="schDayOrNight" class="basic" >';
		let dayOrNightCds = gfn_getCodeGrp('002');
		searchItem += "<option value=''>전체</option>";
		dayOrNightCds.forEach((cd,index) => {
			if(cd.cd != null){
				searchItem += "<option value='"+cd.cd+"'>"+cd.cdNm+"</option>";
			}
		});
		searchItem += '</select>';
		searchItem += '</div>';
		
		searchItem += '<div class="searchItem objChk" style="display:none;">';
		searchItem += '<select id="schDataCd" name="schDataCd" class="basic" >';
		let dataCds = gfn_getCodeGrp('010');
		searchItem += "<option value=''>전체</option>";
		dataCds.forEach((cd,index) => {
			if(cd.cd != null){
				searchItem += "<option value='"+cd.cd+"'>"+cd.cdNm+"</option>";
			}
		});
		searchItem += '</select>';
		searchItem += '</div>';
		/*
		searchItem += '<div class="searchItem objChk" style="display:none;">';
		searchItem += '<input type="search" class="search" id="schDataNm" name="schDataNm" style="width:150px;" title="검색" placeholder="함정/선박명을 입력.">';
		searchItem += '</div>';
		*/
		searchItem += '<div class="searchItem workerChk" style="display:none;">';
		searchItem += '<input type="search" class="search" id="schWorkerNm" name="schWorkerNm" style="width:150px;" title="검색" placeholder="작업자명을 입력.">';
		searchItem += '</div>';
		if(_schGrpType === '5' ){
			searchItem += '<div class="searchItem etcChk" style="display:block;">';
			searchItem += '<input type="search" class="search" id="schEtc" name="schEtc" style="width:180px;" title="검색" placeholder="특이사항 입력">';
			searchItem += '</div>';
	    }
	}
	else if(_schGrpType === '2' ){ //기기관리
		searchItem += '<div class="searchItem equipChk" style="display:none;">';
		searchItem += '<select id="blockYn" name="blockYn" class="basic" >';
		searchItem += "<option value=''>전체</option>";
		searchItem += "<option value='N'>정상</option>";
		searchItem += "<option value='Y'>차단</option>";
		searchItem += '</select>';
		searchItem += '</div>';
		searchItem += '<div class="searchItem userChk" style="display:none;">';
		searchItem += '<input type="search" class="search" id="schUserNm" name="schUserNm" style="width:150px;" title="검색" placeholder="사용자명을 입력.">';
		searchItem += '</div>';
	}
	else if(_schGrpType === '3' ){ //사용자관리
		searchItem += '<div class="searchItem unitChk" style="display:none;">';
		searchItem += '<input type="search" class="search" id="schUnitNm" name="schUnitNm" style="width:150px;" title="검색" placeholder="부대를 검색." readonly>';
		searchItem += '<input type="hidden" id="schUnitId" name="schUnitId" value="">';
		searchItem += '</div>';
		searchItem += '<div class="searchItem userChk" style="display:none;">';
		searchItem += '<input type="search" class="search" id="schUserNm" name="schUserNm" style="width:150px;" title="검색" placeholder="사용자명을 입력.">';
		searchItem += '</div>';
	}
	else if(_schGrpType === '4' ){ //권한관리
		searchItem += '<div class="searchItem unitChk" style="display:none;">';
		searchItem += '<input type="search" class="search" id="schUnitNm" name="schUnitNm" style="width:150px;" title="검색" placeholder="부대를 검색." readonly>';
		searchItem += '<input type="hidden" id="schUnitId" name="schUnitId" value="">';
		searchItem += '</div>';
	}
	let btnHtml = '';
	for(let ele of multiComp.parentNode.childNodes){
		if(ele.tagName === 'DIV' && ele.classList.contains('rightBtnGrp')){
			btnHtml = ele.outerHTML;
			ele.remove();
		}
	}
	multiComp.parentNode.innerHTML += searchItem+btnHtml;
	 
}

const searchSingleInit = (schGrpType) => {
	let _schGrpType = schGrpType===undefined?'':schGrpType;
	const schGrpCd = document.querySelector('#schGrpCd');
	if(schGrpCd){
		schGrpCd.innerHTML = "<option value='' selected>전체</option>";
		if(_schGrpType === '' || _schGrpType === '1' ){
			schGrpCd.innerHTML += "<option value='unitNm'>부대명</option>";
			schGrpCd.innerHTML += "<option value='006'>획득원</option>";
			if( _schGrpType === '' ){
			schGrpCd.innerHTML += "<option value='003'>자료구분</option>";
			}
			schGrpCd.innerHTML += "<option value='002'>주/야 구분</option>";
			schGrpCd.innerHTML += "<option value='dataType'>함정/선박 종류</option>";
			schGrpCd.innerHTML += "<option value='userNm'>작업자</option>";
		}
		else if(_schGrpType === '2' ){ //기기관리
			schGrpCd.innerHTML += "<option value='blockYn'>기기상태</option>";
			schGrpCd.innerHTML += "<option value='userNm'>사용자</option>";
		}
		else if(_schGrpType === '3' ){ //사용자관리
			schGrpCd.innerHTML += "<option value='unitNm'>부대명</option>";
			schGrpCd.innerHTML += "<option value='userNm'>사용자</option>";
		} 
		else if(_schGrpType === '4' ){ //권한관리
			schGrpCd.innerHTML += "<option value='unitNm'>부대명</option>";
		} 
	
	
		schGrpCd.addEventListener('click',(e)=>{
	    	if(e.target.value === ''){
	    		if(document.querySelector('.txtSch')){
	    			document.querySelector('#schTxt').value = '';
	    			document.querySelector('.txtSch').style.display='none';
	    		}
	    		if(document.querySelector('.cdSch')){
	    			document.querySelector('.cdSch').style.display='none';
	    		}
	    		if(document.querySelector('.unitChk')){
	    			document.querySelector('.unitChk').style.display='none';
	    			document.querySelector('#schUnitNm').value = '';
	    			document.querySelector('#schUnitId').value = '';
	    		}
	    	}
	    	else if(e.target.value === 'dataType' || e.target.value === 'userNm'){
	    		if(document.querySelector('.txtSch')){
	    			document.querySelector('.txtSch').style.display='block';
	    		}
	    		if(document.querySelector('.cdSch')){
	    			document.querySelector('.cdSch').style.display='none';
	    		}
	    		if(document.querySelector('.unitChk')){
	    			document.querySelector('.unitChk').style.display='none';
	    			document.querySelector('#schUnitNm').value = '';
	    			document.querySelector('#schUnitId').value = '';
	    		}
	    	}
	    	else if(e.target.value === 'unitNm'){
	    		if(document.querySelector('.txtSch')){
	    			document.querySelector('#schTxt').value = '';
	    			document.querySelector('.txtSch').style.display='none';
	    		}
	    		document.querySelector('.unitChk').style.display='block';
	    		if(document.querySelector('.cdSch')){
	    			document.querySelector('.cdSch').style.display='none';
	    		}	
	    	}
	    	else if(e.target.value === 'blockYn'){
	    		document.querySelector('#schTxt').value = '';
	    		document.querySelector('.txtSch').style.display='none';
	    		if(document.querySelector('.cdSch')){
	    			document.querySelector('.cdSch').style.display='block';
	    		}
	    		if(document.querySelector('.unitChk')){
	    			document.querySelector('.unitChk').style.display='none';
	    			document.querySelector('#schUnitNm').value = '';
	    			document.querySelector('#schUnitId').value = '';
	    		}
	    		document.getElementById('schCd').innerHTML = '';
	    		document.getElementById('schCd').innerHTML += "<option value='N'>정상</option>";
	    		document.getElementById('schCd').innerHTML += "<option value='Y'>차단</option>";
	    	}
	    	else{
	    		document.querySelector('.txtSch').style.display='none';
	    		document.querySelector('#schTxt').value = '';
	    		document.querySelector('.cdSch').style.display='block';
	    		if(document.querySelector('.unitChk')){
	    			document.querySelector('.unitChk').style.display='none';
	    			document.querySelector('#schUnitNm').value = '';
	    			document.querySelector('#schUnitId').value = '';
	    		}
	    		
	    		document.getElementById('schCd').innerHTML = '';
				let cds = gfn_getCodeGrp(e.target.value);
				cds.forEach((cd) => {
					if(cd.cd != null){
						document.getElementById('schCd').innerHTML += "<option value='"+cd.cd+"'>"+cd.cdNm+"</option>";
					}
				});
	    		
	    	}
	   
	    });
	}
	 
}


//////////////////////////////////////////////////// 사용하지 않음////////////////////////////////////////////////////////////////////////////

const gfn_serialize = (form) => {
	
    var field, s = [];
    if (typeof form == 'object' && form.nodeName == "FORM") {
        var len = form.elements.length;
        for (var i=0; i<len; i++) {
            field = form.elements[i];
            if (field.name && !field.disabled && field.type != 'file' && field.type != 'reset' && field.type != 'submit' && field.type != 'button') {
                if (field.type == 'select-multiple') {
                    for (j=form.elements[i].options.length-1; j>=0; j--) {
                        if(field.options[j].selected)
                            s[s.length] = encodeURIComponent(field.name) + "=" + encodeURIComponent(field.options[j].value);
                    }
                } else if ((field.type != 'checkbox' && field.type != 'radio') || field.checked) {
                    s[s.length] = encodeURIComponent(field.name) + "=" + encodeURIComponent(field.value);
                }
            }
        }
    }
    return s.join('&').replace(/%20/g, '+');
}





