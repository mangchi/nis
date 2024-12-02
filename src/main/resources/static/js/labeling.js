'use strict'

let labeling = {};
let aisAry = [];
let clsWeaponAry = [];

const labelInit = () => {
	labeling = {
			mode:'select'	
		  , imageId: ''
		  , img : null             //이미지 객체
		  , choosedColor : 'rgba(255, 255, 0, 0.4)'
		  , otherColor : 'rgba(255,0,255, 0.4)'
		  , lineWidth : 0.05
		  , scaleX : 1
		  , scaleY : 1
		  , xOffset : 0
		  , yOffset : 0
		  , active : false
		  , rectIndex : -1
		  , currentX : 0
		  , currentY : 0
		  , objCnt : 0   // object count
		  , drawing : false // 그리고 있는 중인가
		  , sx : 0
		  , sy : 0
		  , ex : 0
		  , ey : 0
		  , initialX : 0
		  , initialY : 0
		  , arRectangle : new Array()
	      , cssTxt : document.querySelector('.labelingGraph').style.cssText
	      , fileNm : ""
	      , filePath : ""
	      , workFilePath : ""
	      , layerX : 0
	      , layerY : 0

	};
	aisAry = new Array();
	clsWeaponAry = new Array();
}

//var arRectangle = new Array();

 // 사각형 생성자
function Rectangle(sx, sy, ex, ey, color,objIdx,classId,classNm,pose,truncated,difficult,aisId,isMod) {
	try{
		let _classId = classId===undefined?'':classId;
		let _classNm = classNm===undefined?'':classNm;
		let _pose = pose===undefined?'':pose;
		let _truncated = truncated===undefined?'':truncated;
		let _difficult = difficult===undefined?'':difficult;
		let _aisId = aisId===undefined?'':aisId;
		let _isMod = isMod===undefined?'':isMod;
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;
		this.color = color;
		this.objIdx = objIdx;
		this.classInfoId = _classId;
		this.classNm = _classNm;
		this.difficult = _difficult;
		this.pose = _pose;
		this.truncated = _truncated;
		this.aisId = _aisId;
		this.isMod = _isMod;
	}catch(e){console.error(e);}
}

const canvasX = (clientX) => {
	let canvas = document.getElementById('drawLayer');
	let drawLayerOfffsetX = canvas.offsetLeft;
	//console.log("drawLayerOfffsetX:",drawLayerOfffsetX);
	if(labeling.layerX != 0){
		//console.log("layerX:",labeling.layerX);
	}
	var bound = canvas.getBoundingClientRect();
    var bw = 0;
    return (clientX - bound.left - bw) * (canvas.width / (bound.width - bw * 2));
}

const canvasY = (clientY) => {
	let canvas = document.getElementById('drawLayer');
	let	drawLayerOfffsetY = canvas.offsetTop;
	//console.log("drawLayerOfffsetY:",drawLayerOfffsetY);
	if(labeling.layerY != 0){
		//console.log("layerY:",labeling.layerY);
	}
    var bound = canvas.getBoundingClientRect();
    var bw = 0;
    return (clientY - bound.top - bw) * (canvas.height / (bound.height - bw * 2));
}

//x, y 위치의 사각형 찾음. 없으면 -1
const collisionDetection = () => {
	let x1 = Math.min(labeling.sx, labeling.ex);
	let y1 = Math.min(labeling.sy, labeling.ey);
	let x2 = Math.max(labeling.sx, labeling.ex);
	let y2 = Math.max(labeling.sy, labeling.ey);
	for (let i = 0;i < labeling.arRectangle.length;i++) {
		let r = labeling.arRectangle[i];
		if (labeling.ex > r.sx && labeling.ex < r.ex && labeling.ey > r.sy && labeling.ey < r.ey) {
			return true;
	    }
		if (labeling.ex > r.sx && labeling.ex < r.ex && labeling.sy > r.sy && labeling.sy < r.ey) {
			return true;
	    }
		if (r.sx > x1  && r.ex < x2 && r.sy > y1 && r.ey < y2) {
			return true;
	    }
		if (r.sx > x1  && r.sx < x2 && r.sy > y1 && r.sy < y2) {
			return true;
	    }		
		if (r.ex > x1  && r.ex < x2 && r.ey > y1 && r.ey < y2) {
			return true;
	    }
	}
	return false;
}    

// x, y 위치의 사각형 찾음. 없으면 -1
const getRectangle = (x, y) => {
	for (let i = 0;i < labeling.arRectangle.length;i++) {
		let r = labeling.arRectangle[i];
		if (x > r.sx && x < r.ex && y > r.sy && y < r.ey) {
			return r.objIdx;
	    }
	}
	return -1;
}    

// 화면 지우고 모든 도형을 순서대로 다 그림
const drawRects = (x,y,isRect) => {
	//console.log("drawRects");
	let canvas = document.getElementById('drawLayer');
	let ctx = canvas.getContext('2d');
    //ctx.clearRect(labeling.currentX-10,labeling.currentY-10, canvas.width, canvas.height);
	ctx.clearRect(0,0, labeling.img.width, labeling.img.height);
    for (var i = 0;i < labeling.arRectangle.length;i++) {
          let r = labeling.arRectangle[i];
          if(r.isMod === ''){ //수정중인 것 제외
        	  r.color = labeling.otherColor;
              if(isRect){
            	  if (x > r.sx && x < r.ex && y > r.sy && y < r.ey) {
                	  r.color = labeling.choosedColor;
                  }   
              }
              else{
            	  if (x-labeling.currentX > r.sx && x-labeling.currentX < r.ex && y-labeling.currentY > r.sy && y-labeling.currentY < r.ey) {
            		  r.color = labeling.choosedColor;
            	  } 
              }  
              ctx.fillStyle = r.color;
              ctx.fillRect(r.sx*labeling.scaleX+(labeling.layerX*labeling.scaleX), r.sy*labeling.scaleY+(labeling.layerY*labeling.scaleY), (r.ex-r.sx)*labeling.scaleX, (r.ey-r.sy)*labeling.scaleY);
              ctx.strokeRect(r.sx*labeling.scaleX+(labeling.layerX*labeling.scaleX), r.sy*labeling.scaleY+(labeling.layerY*labeling.scaleY), (r.ex-r.sx)*labeling.scaleX, (r.ey-r.sy)*labeling.scaleY);
              
              //ctx.fillRect(r.sx+labeling.layerX*labeling.scaleX, r.sy+labeling.layerY*labeling.scaleY, r.ex-r.sx, r.ey-r.sy);
              //ctx.strokeRect(r.sx+labeling.layerX*labeling.scaleX, r.sy+labeling.layerY*labeling.scaleY, r.ex-r.sx, r.ey-r.sy);
          }    
    }
}



//화면 지우고 모든 도형을 순서대로 다 그림
const drawRectsNormal= () => {
	//console.log("drawRectsNormal");
	let canvas = document.getElementById('drawLayer');
	let ctx = canvas.getContext('2d');
	//ctx.clearRect(0,0, labeling.img.width/labeling.scaleX, labeling.img.height/labeling.scaleY);
	ctx.clearRect(0,0, labeling.img.width, labeling.img.height);
    for (var i = 0;i < labeling.arRectangle.length;i++) {
          let r = labeling.arRectangle[i];
          if(r.isMod === ''){ //수정중인 것 제외
          //ctx.fillStyle = labeling.otherColor;
          ctx.fillStyle = r.color;
          ctx.fillRect(r.sx*labeling.scaleX+(labeling.layerX*labeling.scaleX), r.sy*labeling.scaleY+(labeling.layerY*labeling.scaleY), (r.ex-r.sx)*labeling.scaleX, (r.ey-r.sy)*labeling.scaleY);
          ctx.strokeRect(r.sx*labeling.scaleX+(labeling.layerX*labeling.scaleX), r.sy*labeling.scaleY+(labeling.layerY*labeling.scaleY), (r.ex-r.sx)*labeling.scaleX, (r.ey-r.sy)*labeling.scaleY);
          //ctx.fillRect(r.sx+labeling.layerX*labeling.scaleX, r.sy+labeling.layerY*labeling.scaleY, r.ex-r.sx, r.ey-r.sy);
          //ctx.strokeRect(r.sx+labeling.layerX*labeling.scaleX, r.sy+labeling.layerY*labeling.scaleY, r.ex-r.sx, r.ey-r.sy);
          }
    }
}

//화면 지우고 모든 도형을 순서대로 다 그림
const drawRectsByIdx= (idx) => {
	console.log("drawRectsByIdx idx:",idx);
	console.log("drawRectsByIdx labeling.arRectangle:",labeling.arRectangle);
	let canvas = document.getElementById('drawLayer');
	let ctx = canvas.getContext('2d');
	ctx.clearRect(0,0, labeling.img.width, labeling.img.height);
    for (var i = 0;i < labeling.arRectangle.length;i++) {
          let r = labeling.arRectangle[i];
          if(r.isMod === ''){ //수정중인 것 제외
          r.color = labeling.otherColor;
          if(r.objIdx === idx){
        	  r.color = labeling.choosedColor;
          }
          ctx.fillStyle = r.color;

          ctx.fillRect(r.sx*labeling.scaleX+(labeling.layerX*labeling.scaleX), r.sy*labeling.scaleY+(labeling.layerY*labeling.scaleY), (r.ex-r.sx)*labeling.scaleX, (r.ey-r.sy)*labeling.scaleY);
          ctx.strokeRect(r.sx*labeling.scaleX+(labeling.layerX*labeling.scaleX), r.sy*labeling.scaleY+(labeling.layerY*labeling.scaleY), (r.ex-r.sx)*labeling.scaleX, (r.ey-r.sy)*labeling.scaleY);
          //ctx.fillRect(r.sx+labeling.layerX*labeling.scaleX, r.sy+labeling.layerY*labeling.scaleY, r.ex-r.sx, r.ey-r.sy);
          //ctx.strokeRect(r.sx+labeling.layerX*labeling.scaleX, r.sy+labeling.layerY*labeling.scaleY, r.ex-r.sx, r.ey-r.sy);
          }
    }
}

const selectImg = () => {
	changeOn(document.querySelector('.selectBtn'));
	labeling.mode = 'select';
	let graph3 = document.querySelector('.labelingGraph');
	graph3.style.cssText = '';
	graph3.style.cssText = labeling.cssTxt + 'cursor:default;';
	if(labeling.scaleX !=  1 || labeling.currentX != 0){
		console.log("scaleX:",labeling.scaleX,",currentX:",labeling.currentX);
		labeling.scaleX = 1;
		labeling.scaleY = 1;
		labeling.currentX = 0;
		labeling.currentY = 0;
		labeling.layerX = 0;
		labeling.layerY = 0;
		let imgCvs = document.getElementById('imgLayer');
    	let imgCtx = imgCvs.getContext('2d');
    	imgCtx.clearRect(0,0, imgCvs.width, imgCvs.height);
    	imgCvs.width = labeling.img.width;
    	imgCvs.height = labeling.img.height;
	    imgCtx.scale(labeling.scaleX,labeling.scaleY);
	    imgCtx.drawImage(labeling.img,labeling.layerX,labeling.layerY,labeling.img.width,labeling.img.height);
	    /*
	    let drawCvs = document.getElementById('drawLayer');
	    let drawCtx = drawCvs.getContext('2d');
	    drawCvs.width = labeling.img.width;
	    drawCvs.height = labeling.img.height;
	    drawCtx.scale(labeling.scaleX,labeling.scaleY);
	    drawCtx.strokeStyle = "black";
	    drawCtx.lineWidth = labeling.lineWidth;
	    */
	    //let x = 0;
	    //let y = 0;
	    for (let i = 0;i < labeling.arRectangle.length;i++) {
    		let r = labeling.arRectangle[i];
    		if(r.color === labeling.choosedColor){
    			//x = r.sx+0.5;
    			//y = r.ey-0.5;
    			drawRectsByIdx(r.objIdx);
    			break;
    		}    		
	    }
    	//drawRects(x,y,true);
	}
}

const drawImg = (item,callbackFn) => {
	labeling.scaleX = 1;
	labeling.scaleY = 1;
	let graph3 = document.querySelector('.labelingGraph');
	let canvas = document.getElementById('imgLayer');
	let ctx = canvas.getContext('2d');
	let canvas1 = document.getElementById('drawLayer');
	let ctx1 = canvas1.getContext('2d');
	labeling.img = null;
	labeling.img = new Image();
	labeling.img.classList.add("graph3Img");
	labeling.fileNm = item.fileNm;
	labeling.filePath = item.workFilePath;
	console.log("item.fileId:",item.fileId);
	let loadingLayer = document.querySelector('.loadingLayer');
	loadingLayer.style.display = "block";
	labeling.img.addEventListener("load", () => {
		ctx.clearRect(0,0, graph3.width, graph3.height);
		ctx1.clearRect(0,0, graph3.width, graph3.height);
		//labeling.arRectangle.length = 0;
	    canvas.width = labeling.img.width;
	    canvas.height = labeling.img.height;
	    canvas1.width = labeling.img.width;
	    canvas1.height = labeling.img.height;
	    ctx.drawImage(labeling.img,labeling.currentX,labeling.currentY,labeling.img.width,labeling.img.height);
	    labeling.xOffset = document.getElementById("imgLayer").offsetLeft;
		labeling.yOffset = document.getElementById("imgLayer").offsetTop;
	    if(callbackFn != undefined){
			if(typeof callbackFn === 'function'){
				callbackFn();
			}
			else{
				new Function(callbackFn)();
			}
		}
	    loadingLayer.style.display = "none";
    });
	labeling.img.setAttribute("src", "fileId/"+item.fileId+"/"+item.seq+"/Y/Y"+gfn_getStorageItem("curUrl")+"/"+new Date().getTime());
	changeOn(document.querySelector('.selectBtn'));
	labeling.mode = 'select';
	graph3.style.cssText = '';
	graph3.style.cssText = labeling.cssTxt + 'cursor:default;';
}

const moveImg = () => {
	changeOn(document.querySelector('.moveBtn'));
	labeling.mode = 'move';
	let graph3 = document.querySelector('.labelingGraph');
	graph3.style.cssText = '';
	graph3.style.cssText = labeling.cssTxt + 'cursor:move;';
}

const zoominImg = () => {
	if(labeling.mode != 'zoomin'){
    	changeOn(document.querySelector('.zoominBtn'));
    	labeling.mode = 'zoomin';
    	let graph3 = document.querySelector('.labelingGraph');
    	graph3.style.cssText = '';
    	graph3.style.cssText = labeling.cssTxt + 'cursor:zoom-in;';
	}

}

const zoomoutImg = () => {
	if(labeling.mode != 'zoomout'){
    	changeOn(document.querySelector('.zoomoutBtn'));
    	labeling.mode = 'zoomout';
    	let graph3 = document.querySelector('.labelingGraph');
    	graph3.style.cssText = '';
    	graph3.style.cssText = labeling.cssTxt + 'cursor:zoom-out;';
	}
}

const callAi = () => {
	let objUl = document.querySelector('.list02');
	const deleteLabel = (isExec) => {
		 if(isExec){
			 labeling.arRectangle.length = 0; 
			 clsWeaponAry.length = 0;
			 aisAry.length = 0;
			 initAis();
			 initWeapon();
			 initObject();
			 document.getElementById('classInfoId').value = '';
			 document.getElementById('pose').value = '';
			 document.getElementById('truncated').value = '';
			 document.getElementById('difficult').value = '';
			
			 document.querySelector('.objCnt').innerText = 0;
     		 objUl.querySelectorAll('li').forEach((li) => {
     			objUl.removeChild(li);
     		 });     		 
     		drawAi();
		 }
		   
	}
	if(labeling.arRectangle.length > 0){
 	   confirmCall(msg.delLabel,deleteLabel);
	}
	else{
		drawAi();
	}
	
}

const drawAi = () => {
	let objUl = document.querySelector('.list02');
	changeOn(document.querySelector('.aiBtn'));
	labeling.mode = 'ai';
	let graph3 = document.querySelector('.labelingGraph');
	graph3.style.cssText = '';
	graph3.style.cssText = labeling.cssTxt + 'cursor:default;';
	let param = {filePath:labeling.filePath,fileNm:labeling.fileNm};
	
	let idx = labeling.arRectangle.length;
	console.log("idx:",idx);
	console.log("param:",param);
	let classId = '';
	
	gfn_asyncJsonCall("/callAi",'POST',param).then((data) => {
		let list = data['list'];
		let failMsg = data['fail_msg'];
		if(failMsg != null || failMsg != undefined){
			msgCall(failMsg,false);
			return;
		}
		
		for(let itm of list){
			if(itm.name === 'Object'){
				console.log("itm:",itm);
				let color = labeling.otherColor;
	        	if(idx === 0){
	        		color = labeling.choosedColor;
	        	}
				let rect = new Rectangle(parseFloat(itm.x), parseFloat(itm.y), parseFloat(itm.x+itm.width),parseFloat(itm.y+itm.height),color,idx
		                ,gfn_nullValue(itm.classId),gfn_nullValue(itm.classNm));
                labeling.arRectangle.push(rect);
                let objLi = document.createElement('li');
				objLi.classList.add('li_link');
				objLi.setAttribute("value",idx);
				objLi.append(' - '+(parseFloat(itm.x)).toFixed(0)+','+(parseFloat(itm.y)).toFixed(0)+','+(parseFloat(itm.x+itm.width)).toFixed(0)+','+(parseFloat(itm.y+itm.height)).toFixed(0));
				//objLi.append(' - '+(parseFloat(itm.x)).toFixed(0)+','+(parseFloat(itm.y+itm.height)).toFixed(0));
				let anch = document.createElement('a');
	    		anch.addEventListener('click',(e) => {
	    			e.preventDefault();
	    			removeObject(e.target);
	    		});
	    		anch.classList.add('btn-del-s')
	    		objLi.appendChild(anch);
				objLi.addEventListener('click',(e) => {
					e.preventDefault();
				    setInfo(rect);
				});
				
				if(gfn_nullValue(itm.classId) != '' && classId != gfn_nullValue(itm.classId) ){
					classId = gfn_nullValue(itm.classId);
				    let clsWeapon = {classId : classId,classNm : gfn_nullValue(itm.classNm)  ,on : 'N'};
				    if(idx === 0){clsWeapon.on = 'Y';}	
				    clsWeaponAry.push(clsWeapon);
				}
				if(idx === 0){
	        		objLi.classList.add('on');
	        		//labeling.rectIndex = idx;
	        		document.querySelector('.object').innerHTML = gfn_nullValue(itm.classNm);
	        		document.querySelector('.x1').innerHTML = gfn_nullValue(itm.x);
			    	document.querySelector('.y1').innerHTML = gfn_nullValue(itm.y);
			    	document.querySelector('.x2').innerHTML = gfn_nullValue(itm.x+itm.width);
			    	document.querySelector('.y2').innerHTML = gfn_nullValue(itm.y+itm.height);
			    	document.getElementById('classInfoId').value = gfn_nullValue(itm.classNm);
			    	document.getElementById('pose').value = gfn_nullValue(itm.pose);
			    	document.getElementById('truncated').value = gfn_nullValue(itm.truncated);
			    	document.getElementById('difficult').value = gfn_nullValue(itm.difficult);
			    	if(gfn_nullValue(itm.aisId) != ''){
			    		document.querySelector('.mmsi').innerHTML = gfn_nullValue(itm.mmsi);
						document.querySelector('.imoNo').innerHTML = gfn_nullValue(itm.imoNo);
						document.querySelector('.national').innerHTML = gfn_nullValue(itm.national);
						document.querySelector('.shipType').innerHTML = gfn_nullValue(itm.shipType);
						document.querySelector('.callSign').innerHTML = gfn_nullValue(itm.callSign);
			    	}
	        	}				
				objUl.appendChild(objLi);
				idx++;
			}	
		}
		document.querySelector('.objCnt').innerText = labeling.arRectangle.length;
		labeling.objCnt =  labeling.arRectangle.length;
		console.log("labeling.arRectangle:",labeling.arRectangle);
		drawRectsByIdx(0);
	});
}

const drawRect = () => {
	changeOn(document.querySelector('.drawBtn'));
	labeling.mode = 'draw';
	let graph3 = document.querySelector('.labelingGraph');
	graph3.style.cssText = '';
	graph3.style.cssText = labeling.cssTxt + 'cursor:crosshair;';
}

const editRect = () => {
	changeOn(document.querySelector('.editBtn'));
	labeling.mode = 'edit';
	let graph3 = document.querySelector('.labelingGraph');
	graph3.style.cssText = '';
	graph3.style.cssText = labeling.cssTxt + 'cursor:crosshair;';
}


const deleteRect = () => {
	changeOn(document.querySelector('.deleteBtn'));
	labeling.mode = 'delete';
	let graph3 = document.querySelector('.labelingGraph');
	graph3.style.cssText = '';
	graph3.style.cssText = labeling.cssTxt + 'cursor:not-allowed;';
}

const changeOn = (obj) => {
	const funcBtns = document.querySelectorAll('.btn-func01');
	funcBtns.forEach((funcBtn) => {
		funcBtn.classList.remove('on');
		if(obj === funcBtn){
			funcBtn.classList.add('on');
		}
	});

}

const setTranslate = (xPos, yPos, el) => {
    el.style.transform = "translate3d(" + xPos + "px, " + yPos + "px, 0)";
}

const drawDbRect = () => {
	//console.log("labeling.arRectangle:",labeling.arRectangle);
	let imgCvs = document.getElementById('imgLayer');
	let imgCtx = imgCvs.getContext('2d');	
	// 좌표 정규화해서 새로운 도형을 배열에 추가
	imgCvs.width = labeling.img.width;
	imgCvs.height = labeling.img.height;
	imgCtx.scale(labeling.scaleX,labeling.scaleY);
	imgCtx.clearRect(0,0, imgCvs.width, imgCvs.height);
	imgCtx.drawImage(labeling.img,labeling.currentX,labeling.currentY,labeling.img.width,labeling.img.height);
    
    let drawCvs = document.getElementById('drawLayer');
    let drawCtx = drawCvs.getContext('2d');
    drawCvs.width = labeling.img.width;
    drawCvs.height = labeling.img.height;
    drawCtx.strokeStyle = "black";
    drawCtx.lineWidth = labeling.lineWidth;
    drawCtx.scale(labeling.scaleX,labeling.scaleY);
    for (let i = 0;i < labeling.arRectangle.length;i++) {
		let r = labeling.arRectangle[i];
		if(r.color === labeling.choosedColor){
			drawRectsByIdx(r.objIdx);
			break;
		}	 
		
    }

}


const setInfo = (obj)  => {
	let objUl = document.querySelector('.list02');
	if(objUl.children.length > 0){
		objUl.querySelectorAll('li').forEach((li,index) =>{
			if(li.value === obj.objIdx){
				li.classList.add('on');
				labeling.arRectangle.forEach((rect,idx) => {
	    			if(index === idx){
	    				document.querySelector('.object').innerHTML = gfn_nullValue(rect.classNm);
	    				document.querySelector('.x1').innerHTML = rect.sx;
	    		    	document.querySelector('.y1').innerHTML = rect.sy;
	    		    	document.querySelector('.x2').innerHTML = rect.ex;
	    		    	document.querySelector('.y2').innerHTML = rect.ey;
	    		    	document.getElementById('classInfoId').value = gfn_nullValue(rect.classNm);
	    		    	document.getElementById('pose').value = gfn_nullValue(rect.pose);
	    		    	document.getElementById('truncated').value = gfn_nullValue(rect.truncated);
	    		    	document.getElementById('difficult').value = gfn_nullValue(rect.difficult);
	    		    	initAis();
						initWeapon();
	    		    	aisAry.forEach((aisItem,index) =>{
	    		    		if(rect.aisId === aisItem.id){
	    		    			document.querySelector('.mmsi').innerHTML = gfn_nullValue(aisItem.mmsi);
	    						document.querySelector('.imoNo').innerHTML = gfn_nullValue(aisItem.imoNo);
	    						document.querySelector('.national').innerHTML = gfn_nullValue(aisItem.national);
	    						document.querySelector('.shipType').innerHTML = gfn_nullValue(aisItem.shipType);
	    						document.querySelector('.callSign').innerHTML = gfn_nullValue(aisItem.callSign);
	    		    		}
	    		    	});
	    		    	
	    		    	console.log("clsWeaponAry:",clsWeaponAry);
	    		    	clsWeaponAry.forEach((clsWItem,index) =>{
	    		    		if(rect.classInfoId === clsWItem.classId){
	    		    			clsWItem.on = 'Y';
	    		    			let weaponGrps = clsWItem.weaponGrps;
	    		    			if(weaponGrps != undefined && weaponGrps.length > 0){
	    		    				weaponGrps.forEach((weaponGrp) =>{
	    		    					let weaponItems = "";
	        	    					weaponGrp.weapons.forEach((weapon,index) =>{
	        	    						weaponItems += weapon.nm+",";
	        	    					});
	        	    					document.querySelector('.w'+weaponGrp.weaponGrpId).innerHTML = weaponItems.substring(0,weaponItems.length-1);
	        		    			});
	    		    			}
	    		    			
	    		    		}
	    		    		else{
	    		    			clsWItem.on = 'N';
	    		    		}
	    		    	});
	    		    	drawRectsByIdx(obj.objIdx);
	    		    	//drawRects(rect.sx+0.5,rect.ey-0.5,true);
	    			}
	    		});
			}
			else{
				li.classList.remove('on');
			}
		});
	}
}

const removeObject = (obj) => {
	//console.log("removeObject labeling.rectIndex:",labeling.rectIndex);
	let li = obj.parentNode;
	let liVal = li.value;
	let objUl = document.querySelector('.list02');
	labeling.objCnt -= 1;
	document.querySelector('.objCnt').innerText = labeling.objCnt;
	console.log("labeling.rectIndex:",labeling.rectIndex);
	
	objUl.querySelectorAll('li').forEach((li) => {
		if(li.value === liVal){
			if(li.classList.contains('on')){
				initAis();
   				initWeapon();
   				initObject();
			}
			objUl.removeChild(li);
			
		}
	});
	for (let i = 0;i < labeling.arRectangle.length;i++) {
   		let rect = labeling.arRectangle[i];
   		if(rect.objIdx === liVal){
   			labeling.arRectangle.splice(i, 1);
   		}
	}
	drawRectsNormal();
	console.log("labeling.arRectangle:",labeling.arRectangle);
    if(labeling.arRectangle.length === 0){
    	clsWeaponAry = new Array();
    }
    else{
    	let aryClassId = new Array();
    	for (let i = 0;i < labeling.arRectangle.length;i++) {
    		let rect = labeling.arRectangle[i];labeling.arRectangle[i];
    		if(rect.classInfoId != '' && rect.classInfoId != 'blank'){
    			aryClassId.push(rect.classInfoId);
    		}
    	}
    	clsWeaponAry.forEach((clsWeapon,index) => {
    		if(!aryClassId.includes(clsWeapon.weaponGrpId)){
    			clsWeaponAry.splice(index, 1);
    		}
    		
    	});
    }
    console.log("removeObject clsWeaponAry:",clsWeaponAry);
}



const setLabel = () => {
	
	let objUl = document.querySelector('.list02');
	if(objUl.children.length > 0){
		objUl.querySelectorAll('li').forEach((li,index) =>{
			if(li.value === labeling.rectIndex){
				li.classList.add('on');
				let rect = null;
				for(let i=0;i<labeling.arRectangle.length;i++){
					rect =  labeling.arRectangle[i];
					if(rect.objIdx == labeling.rectIndex){
						break;
					}
				}
				document.querySelector('.object').innerHTML = gfn_nullValue(rect.classNm);
				document.querySelector('.x1').innerHTML = rect.sx;
		    	document.querySelector('.y1').innerHTML = rect.sy;
		    	document.querySelector('.x2').innerHTML = rect.ex;
		    	document.querySelector('.y2').innerHTML = rect.ey;
		    	document.getElementById('classInfoId').value = gfn_nullValue(rect.classNm);
		    	document.getElementById('pose').value = gfn_nullValue(rect.pose);
		    	document.getElementById('truncated').value = gfn_nullValue(rect.truncated);
		    	document.getElementById('difficult').value = gfn_nullValue(rect.difficult);
		    	initAis();
				initWeapon();
		    	aisAry.forEach((aisItem,index) =>{
		    		if(rect.aisId === aisItem.id){
		    			document.querySelector('.mmsi').innerHTML = gfn_nullValue(aisItem.mmsi);
						document.querySelector('.imoNo').innerHTML = gfn_nullValue(aisItem.imoNo);
						document.querySelector('.national').innerHTML = gfn_nullValue(aisItem.national);
						document.querySelector('.shipType').innerHTML = gfn_nullValue(aisItem.shipType);
						document.querySelector('.callSign').innerHTML = gfn_nullValue(aisItem.callSign);
		    		}
		    	});

		    	clsWeaponAry.forEach((clsWItem,index) =>{	
		    		if(rect.classInfoId == clsWItem.classId){
		    			clsWItem.on = 'Y';
		    			let weaponGrps = clsWItem.weaponGrps;
		    			if(weaponGrps != undefined && weaponGrps.length > 0){
		    				weaponGrps.forEach((weaponGrp) =>{
		    					let weaponItems = "";
    	    					weaponGrp.weapons.forEach((weapon,index) =>{
    	    						weaponItems += weapon.nm+",";
    	    					});
    	    					document.querySelector('.w'+weaponGrp.weaponGrpId).innerHTML = weaponItems.substring(0,weaponItems.length-1);
    		    			});
		    			}		
		    		}
		    		else{
		    			clsWItem.on = 'N';
		    		}
		    	});
		    	//let liTxt = ' - '+parseFloat(rect.sx).toFixed(0)+','+parseFloat(rect.ey).toFixed(0);
		    	let liTxt = ' - '+parseFloat(rect.sx).toFixed(0)+','+parseFloat(rect.sy).toFixed(0)+','+parseFloat(rect.ex).toFixed(0)+','+parseFloat(rect.ey).toFixed(0);
		    	if(li.children.length === 1){
		    		let liSpan = li.children[0];
		    		li.innerHTML = '';
		    		li.append(liSpan);
				}
		    	else{
		    		li.innerHTML = '';
		    	}
	    		li.append(liTxt);
	    		drawRectsByIdx(labeling.rectIndex);  //move
			}
			else{
				li.classList.remove('on');
			}
		});
	}

}

const editLabel = () => {
	let objUl = document.querySelector('.list02');
	if(objUl.children.length > 0){
		objUl.querySelectorAll('li').forEach((li,index) =>{
			if(li.value === labeling.rectIndex){
				li.classList.add('on');
				let rect = null;
				for(let i=0;i<labeling.arRectangle.length;i++){
					rect =  labeling.arRectangle[i];
					if(rect.objIdx == labeling.rectIndex){
						break;
					}
				}
				
				let x1 = Math.min(labeling.sx, labeling.ex);
	        	let y1 = Math.min(labeling.sy, labeling.ey);
	        	let x2 = Math.max(labeling.sx, labeling.ex);
	        	let y2 = Math.max(labeling.sy, labeling.ey);
	  
	            if(x2 - x1 > 10 && y2 - y1 > 10){
	            	rect.sx = x1;
    	        	rect.sy = y1;
    	        	rect.ex = x2;
    	        	rect.ey = y2;
    	        	rect.isMod = '';
    	        	document.querySelector('.object').innerHTML = gfn_nullValue(rect.classNm);
    				document.querySelector('.x1').innerHTML = rect.sx;
    		    	document.querySelector('.y1').innerHTML = rect.sy;
    		    	document.querySelector('.x2').innerHTML = rect.ex;
    		    	document.querySelector('.y2').innerHTML = rect.ey;
    		    	document.getElementById('classInfoId').value = gfn_nullValue(rect.classNm);
    		    	document.getElementById('pose').value = gfn_nullValue(rect.pose);
    		    	document.getElementById('truncated').value = gfn_nullValue(rect.truncated);
    		    	document.getElementById('difficult').value = gfn_nullValue(rect.difficult);
    		    	initAis();
					initWeapon();
    		    	aisAry.forEach((aisItem,index) =>{
    		    		if(rect.aisId === aisItem.id){
    		    			document.querySelector('.mmsi').innerHTML = gfn_nullValue(aisItem.mmsi);
    						document.querySelector('.imoNo').innerHTML = gfn_nullValue(aisItem.imoNo);
    						document.querySelector('.national').innerHTML = gfn_nullValue(aisItem.national);
    						document.querySelector('.shipType').innerHTML = gfn_nullValue(aisItem.shipType);
    						document.querySelector('.callSign').innerHTML = gfn_nullValue(aisItem.callSign);
    		    		}
    		    	});
    		    	
    		    	clsWeaponAry.forEach((clsWItem,index) =>{
    		    		console.log("clsWItem:::",clsWItem);
    		    		if(rect.classInfoId === clsWItem.classId){
    		    			clsWItem.on = 'Y';
    		    			let weaponGrps = clsWItem.weaponGrps;
    		    			if(weaponGrps != undefined && weaponGrps.length > 0){
    		    				weaponGrps.forEach((weaponGrp) =>{
    		    					console.log("weaponGrp:",weaponGrp);
    		    					let weaponItems = "";
        	    					weaponGrp.weapons.forEach((weapon,index) =>{
        	    						weaponItems += weapon.nm+",";
        	    					});
        	    					document.querySelector('.w'+weaponGrp.weaponGrpId).innerHTML = weaponItems.substring(0,weaponItems.length-1);
        		    			});
    		    			}
    		    		}
    		    		else{
    		    			clsWItem.on = 'N';
    		    		}
    		    	});
    		    	
    		    	let liTxt = ' - '+rect.sx.toFixed(0)+','+rect.sy.toFixed(0)+','+rect.ex.toFixed(0)+','+rect.ey.toFixed(0);
    		    	
    		    	if(li.children.length === 1){
    		    		let liSpan = li.children[0];
    		    		li.innerHTML = '';
    		    		li.append(liSpan);
    				}
    		    	else{
    		    		li.innerHTML = '';
    		    	}
    	    		li.append(liTxt);
            	}
            	else{
            		document.querySelector('.object').innerHTML = gfn_nullValue(rect.classNm);
            		document.querySelector('.x1').innerHTML = rect.sx;
    		    	document.querySelector('.y1').innerHTML = rect.sy;
    		    	document.querySelector('.x2').innerHTML = rect.ex;
    		    	document.querySelector('.y2').innerHTML = rect.ey;
    		    	
                    let liTxt = ' - '+rect.sx.toFixed(0)+','+rect.sy.toFixed(0)+','+rect.ex.toFixed(0)+','+rect.ey.toFixed(0);
    		    	
    		    	if(li.children.length === 1){
    		    		let liSpan = li.children[0];
    		    		li.innerHTML = '';
    		    		li.append(liSpan);
    				}
    		    	else{
    		    		li.innerHTML = '';
    		    	}
    	    		li.append(liTxt);
            	}
   
			}
			else{
				li.classList.remove('on');
			}
		});
	}
}

const initObject = () => {
	document.querySelector('.object').innerHTML = '';
	document.querySelector('.x1').innerHTML = '';
	document.querySelector('.y1').innerHTML = '';
	document.querySelector('.x2').innerHTML = '';
	document.querySelector('.y2').innerHTML = '';
}

const initAis = () => {
	document.querySelector('.mmsi').innerHTML = '';
	document.querySelector('.imoNo').innerHTML = '';
	document.querySelector('.national').innerHTML = '';
	document.querySelector('.shipType').innerHTML = '';
	document.querySelector('.callSign').innerHTML = '';
}

const initWeapon = () => {
	document.querySelector('.w001').innerHTML = '';
	document.querySelector('.w002').innerHTML = '';
	document.querySelector('.w003').innerHTML = '';
	document.querySelector('.w004').innerHTML = '';
	document.querySelector('.w005').innerHTML = '';
	document.querySelector('.w006').innerHTML = '';
	document.querySelector('.w007').innerHTML = '';
	document.querySelector('.w008').innerHTML = '';
	document.querySelector('.w009').innerHTML = '';
	document.querySelector('.w010').innerHTML = '';
}

