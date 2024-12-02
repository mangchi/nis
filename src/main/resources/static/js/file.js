'use strict'

window.addEventListener('DOMContentLoaded', function(){
	//fileInit();
});

let fileNo = 0;
let fileTotSize = 0;
let fileTp = 'img'; // img,mov,ais,xml
let filesArr = new Array();
let fileParam = new Array();

const fileInit = (boxType) => {
	fileNo = 0;
	fileTotSize = 0;
	let filesInfo = document.createElement('div');
	filesArr = new Array();
	let _boxType = boxType === undefined?'0':boxType;
	let fileBox = document.querySelector('.fileBox');
	let fileList = document.createElement('div');

	if(fileBox){
		let html = '';
		fileList.classList.add("fileList");
		let fileHead = document.createElement('div');
		fileHead.classList.add("fileHead");
		let fileName = document.createElement('div');
		fileName.classList.add("fileHeadItem");
		fileName.append('파일명');
		let fileLength = document.createElement('div');
		fileLength.classList.add("fileHeadItem");
		fileLength.append('파일크기');
		fileHead.appendChild(fileName);
		fileHead.appendChild(fileLength);
		
		let totDelBtn = document.createElement('a');
		let totDelIcon = document.createElement('i');
		totDelIcon.classList.add("fa","fa-trash-o");
		totDelBtn.classList.add("btn","hidden");
		totDelBtn.appendChild(totDelIcon);
		fileHead.appendChild(totDelBtn);
		
		fileList.appendChild(fileHead);
		let helpDiv = document.createElement('div');
		helpDiv.classList.add("helpDiv");
		let helptxt = document.createElement('p');
		let helpIcon = document.createElement('i');
		helpIcon.classList.add("fas","fa-file-upload","fa-3x");
		let strong1 = document.createElement('strong');
		strong1.append("더블클릭");
		let strong2 = document.createElement('strong');
		strong2.append("드레그");
		helptxt.append('이곳을 ');
		helptxt.appendChild(strong1);
		helptxt.append(' 또는 파일을 ');
		helptxt.appendChild(strong2);
		helptxt.append('하세요.');
		
		helpDiv.appendChild(helptxt);
		helpDiv.appendChild(helpIcon);
		fileList.appendChild(helpDiv);
		
		filesInfo.append("");
		filesInfo.append("파일개수: 0 ");
		filesInfo.append("파일크기: 0KB");
		filesInfo.classList.add("filesInfo");
		fileList.appendChild(filesInfo);
		
		let fileBtn = document.createElement('div');
		fileBtn.classList.add("fileBtn");
		let fileLabel = document.createElement('label');
		fileLabel.append('파일추가');
		fileLabel.setAttribute("for", "file");
		fileLabel.classList.add("btn-s02");
		let fileInput = document.createElement('input');
		fileInput.setAttribute("type", "file");
		fileInput.setAttribute("id", "file");
		fileInput.setAttribute("accept", "application/pdf,image/gif, image/jpeg,image/png, image/bmp, image/tif, text/xml,video/mp4,video/mpeg,video/x-msvideo,video/quicktime,video/x-ms-wmv");
		fileInput.multiple=true;
		
		let deleteAllBtn = document.createElement('button');
		deleteAllBtn.setAttribute("type", "button");
		deleteAllBtn.append('전체제거');
		deleteAllBtn.classList.add("btn-s02");
		let aisBtn = document.createElement('button');
		aisBtn.setAttribute("type", "button");
		aisBtn.append('AIS 정보추가');
		aisBtn.classList.add("btn-s02");
		let xmlBtn = document.createElement('button');
		xmlBtn.setAttribute("type", "button");
		xmlBtn.append('XML 파일추가');
		xmlBtn.classList.add("btn-s02");
		fileBtn.appendChild(fileLabel);
		fileBtn.appendChild(fileInput);
		fileBtn.appendChild(deleteAllBtn);
		if(_boxType === '1'){
			fileBtn.appendChild(aisBtn);
			fileBtn.appendChild(xmlBtn);
		}
		fileBox.appendChild(fileList);
		fileBox.appendChild(filesInfo);
		fileBox.appendChild(fileBtn);
		
		fileInput.addEventListener("change",(e) => {
			e.preventDefault();
			addFiles(e.target.files);
	    });
		
		deleteAllBtn.addEventListener("click", (e) => {
			e.preventDefault();
			deleteFiles(e.target);
			/*
			let fileRows = document.querySelectorAll('.fileRow');
			if(fileRows){
				fileRows.forEach((fileRow,index) => {
					fileRow.remove();
					filesArr[index].isDelete = true;
					fileNo--;
				    fileTotSize = fileTotSize - filesArr[index].size;
				});
				filesInfo.innerHTML = '';
				filesInfo.innerHTML += '파일개수 0:&nbsp;&nbsp;&nbsp;&nbsp;';
				filesInfo.innerHTML += '파일크기: 0KB';
				fileBox.querySelector('.helpDiv').style.display = 'none';
			}
			*/
	    });
		
		fileList.addEventListener('dblclick',  (e) => {
			try{
				e.preventDefault();
				document.getElementById("file").click();
			}catch(e){
				console.error(e);
			}
		});
		
		
		fileList.addEventListener('dragover', (e) => {
			e.stopPropagation();
		    e.preventDefault();
	    });
	
		fileList.addEventListener("dragenter", (e) => {
	        e.stopPropagation();
	        e.preventDefault();
	    });
		
		// 드래그한 파일이 dropZone 영역을 벗어났을 때
		fileList.addEventListener("dragleave", (e) => {
	        e.stopPropagation();
	        e.preventDefault();
	    });
	    
		fileList.addEventListener('drop', (e) => {
	    	e.stopPropagation();
			e.preventDefault();
			let files = e.dataTransfer && e.dataTransfer.files
			if (files != null) {
	            if (files.length < 1) {
	                return
	            } 
	            fileInput.files = files;
	            addFiles(fileInput.files);
	        } 
			
	    });
	}

	

}

/* 첨부파일 추가 */
const addFiles = (files) => {
	let loadingLayer = document.querySelector('.loadingLayer');
	loadingLayer.style.display = "block";
	let maxFileCnt = 100000;   // 첨부파일 최대 개수
	let attFileCnt = document.querySelectorAll('.fileRow').length;    // 기존 추가된 첨부파일 개수
	let remainFileCnt = maxFileCnt - attFileCnt;    // 추가로 첨부가능한 개수
	let curFileCnt = files.length;  // 현재 선택된 첨부파일 개수
	let filesInfo = document.querySelector('.filesInfo');
	let fileList =  document.querySelector('.fileList');
	filesInfo.innerHTML = '';
    // 첨부파일 개수 확인
    if (curFileCnt > remainFileCnt) {
        msgCall(getMsg(msg.fileLimit,[maxFileCnt]),false);
    } else {
    	for (const file of files) {
        	if (validation(file)) {
                // 파일 배열에 담기
                let reader = new FileReader();
                reader.onload =  () => {
                   
                    console.log("file:",file);
                };
                reader.readAsDataURL(file);
                
                // 목록 추가
                filesArr.push(file);
                fileNo = filesArr.length-1;
                let htmlData = '';
                htmlData += '<div id="file' + fileNo + '" class="fileRow ch-file">';//class 추가 : 20220925
                htmlData += '   <div class="name ch-name">' + file.name + '</div>';//class 추가 : 20220925
                htmlData += '   <div class="length ch-length">' + (file.size/1024).toFixed(2) + 'KB</div>';//class 추가 : 20220925
                htmlData += '   <button type="button" class="btn-func01" title="삭제" onclick="deleteFile(' + fileNo + ');"><i class="delete"></i></button>';
                //htmlData += '   <a class="btn btn-danger ch-btn-delete fa_del" onclick="deleteFile(' + fileNo + ');"><i></i></a>';//class 추가 : 20220925
                htmlData += '</div>';
                fileList.innerHTML += htmlData;
                //fileNo++;
                
                fileTotSize +=file.size;
                
            } else {
            	
                continue;
            }
        }
    	filesInfo.innerHTML = '';
    	filesInfo.innerHTML += '파일개수: '+ document.querySelectorAll('.fileRow').length + '&nbsp;&nbsp;&nbsp;&nbsp;';
        filesInfo.innerHTML += '파일크기: '+(fileTotSize/1024).toFixed(2) + 'KB';
        const helpClass = document.querySelector('.helpDiv').classList;
        if(document.querySelectorAll('.fileRow').length === 0){
        	document.querySelector('.fileBox').querySelector('.helpDiv').style.display = '';
        }
        else{
        	if(document.querySelector('.fileBox').querySelector('.helpDiv').style.display === ''){
        		document.querySelector('.fileBox').querySelector('.helpDiv').style.display = 'none';
	        }
        }
        
        loadingLayer.style.display = "none";
        
        
    }
    // 초기화
    document.querySelector("input[type=file]").value = "";
}


/* 첨부파일 검증 */
const validation = (obj) => {
	const imgFileTypes = ['application/pdf', 'image/gif', 'image/jpeg', 'image/png', 'image/bmp', 'image/tif'
                        , 'text/xml'];
    const movFileTypes = ['video/mp4','video/mpeg','video/x-msvideo','video/quicktime','video/x-ms-wmv'];
    console.log("filesArr:",filesArr);
    for(let i in filesArr){
    	console.log(filesArr[i]);
    	if(obj.name === filesArr[i].name && (filesArr[i].isDelete === null || filesArr[i].isDelete != true)){
    		msgCall(getMsg(msg.fileDup),false);
    		return false;
    	}
    }

    if (obj.name.length > 100) {
    	msgCall(getMsg(msg.fileName,["100"]),false);
        return false;
    }
    if (obj.size > (2048 * 1024 * 1024 * 1024)) {
    	msgCall(getMsg(msg.fileLength,["2048MB"]),false);
        return false;
    }
    if (obj.name.lastIndexOf('.') == -1) {
    	msgCall(getMsg(msg.fileExt),false);
        return false;
    } 
    if(fileTp === 'img'){
    	if (!imgFileTypes.includes(obj.type)) {
    		console.log(obj.type);
    		msgCall(getMsg(msg.fileType),false);
    		return false;
    	}
    }
    else{
    	if (!movFileTypes.includes(obj.type)) {
    		msgCall(getMsg(msg.fileType),false);
    		return false;
    	}
    }
  
    return true;
  
}


const deleteFiles = (target) => {
	///filesArr = new Array();
	//fileParam = new Array();

	let fileRows = document.querySelectorAll('.fileRow');
	let filesInfo = document.querySelector('.filesInfo');
	let fileBox = document.querySelector('.fileBox');
	if(fileRows){
		fileRows.forEach((fileRow,index) => {
			fileRow.remove();
			//filesArr[index].isDelete = true;
			fileNo--;
		    // fileTotSize = fileTotSize - filesArr[index].size;
		});
		filesInfo.innerHTML = '';
		filesInfo.innerHTML += '파일개수 : 0&nbsp;&nbsp;&nbsp;&nbsp;';
		filesInfo.innerHTML += '파일크기: 0KB';
		fileBox.querySelector('.helpDiv').style.display = 'none';
	}
	console.log("deleteFiles filesArr:{}",filesArr);
	fileTotSize = 0;
	for(let idx in filesArr){
		filesArr[idx].isDelete = true;
    }
	for(let idx in fileParam){
    	fileParam[idx].isDelete = true;
    }
	if(document.querySelectorAll('.fileRow').length === 0){
    	document.querySelector('.fileBox').querySelector('.helpDiv').style.display = '';
    }
    
}


const deleteFile = (num) => {
	console.log("num:",num);
	console.log("fileParam length:",fileParam.length);
	console.log("filesArr length:",filesArr.length);
	let filesInfo = document.querySelector('.filesInfo');
	filesInfo.innerHTML = '';
    document.querySelector("#file" + num).remove();
    filesArr[num].isDelete = true;
    if(num < fileParam.length ){
    	fileParam[num].isDelete = true;
    }
    
    //fileNo--;
    console.log("filesArr:",filesArr[num]);
    console.log("fileParam:",fileParam[num]);
    console.log("fileTotSize:",fileTotSize);
    if(num < fileParam.length ){
    	fileTotSize = parseInt(fileTotSize) - fileParam[num].size;
    }
    else{
    	fileTotSize = fileTotSize - filesArr[num].size;
    }
    
    
    //filesArr.splice(num,1);
    let fileRows = document.querySelectorAll('.fileRow');
    if(fileRows.length === 0){
    	document.querySelector('.fileBox').querySelector('.helpDiv').style.display = '';
    }

    filesInfo.innerHTML += '파일개수: '+fileRows.length + '&nbsp;&nbsp;&nbsp;&nbsp;';
    if(fileTotSize === 0){
    	filesInfo.innerHTML += '파일크기: 0KB';
    }else{
    	filesInfo.innerHTML += '파일크기: '+(fileTotSize/1024).toFixed(2) + 'KB';
    }
    
    
}


