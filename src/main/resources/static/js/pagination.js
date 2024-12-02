'use strict'

const pagination = {
		rowPerPage:10
	  , halfRowPerPage:5
	  , doubleRowPerPage:20
	
}


const pageInit = (comp,pageFunc,defaultPerPage,title,optionItems,totPageInfo,appendCss,schFrm) => {
    /*
	let pageNum = document.querySelector('.pageNum');
	let rowPerPage = document.querySelector('.rowPerPage');
	if(!pageNum){
		pageNum = document.createElement('INPUT'); 
		pageNum.setAttribute("type","hidden");
		pageNum.setAttribute("name","pageNum");
		pageNum.classList.add("pageNum");
		comp.parentNode.appendChild(pageNum);
	}
	if(!rowPerPage){
		rowPerPage = document.createElement('INPUT'); 
		rowPerPage.setAttribute("type","hidden");
		rowPerPage.setAttribute("name","rowPerPage");
		rowPerPage.classList.add("rowPerPage");
		comp.parentNode.appendChild(rowPerPage);
		
	}
	*/
	const divRowPerPage = document.createElement('div');
	divRowPerPage.classList.add("sel-group");
	const compPageNum = document.createElement('INPUT'); 
	compPageNum.setAttribute("type","hidden");
	compPageNum.setAttribute("name","compPageNum");
	compPageNum.classList.add("compPageNum");
	if(schFrm === undefined){
		compPageNum.setAttribute("value",1);
	}
	else{
		compPageNum.setAttribute("value",parseInt(schFrm.compPageNum));
	}
	
	divRowPerPage.appendChild(compPageNum);
	const pageLabel = document.createElement('label');
	let _title = (title===undefined || title===null)?"Row Per Pages :":title+" :";
	pageLabel.append(_title);
	
	const compRowPerPage = document.createElement('select');
	compRowPerPage.classList.add("compRowPerPage");
	compRowPerPage.setAttribute("name", "compRowPerPage");
	compRowPerPage.addEventListener('change',(e) => {
		e.preventDefault();
		compPageNum.setAttribute("value",1);
		if(typeof pageFunc === 'function'){
			pageFunc();
		}
		else{
			new Function(pageFunc)();
		}
	});
	if(optionItems === undefined || optionItems === null){
		let _defaultPerPage = defaultPerPage===undefined?5:defaultPerPage;
		if(schFrm != undefined){
			_defaultPerPage = parseInt(schFrm.compRowPerPage);
		}
		
		let size = _defaultPerPage-1;
		for(let i=0;i<size;i++){
			let option = document.createElement('option');
			option.append((i+1)*_defaultPerPage);
			compRowPerPage.appendChild(option);
		}
		
	}
	
	divRowPerPage.appendChild(pageLabel);
	divRowPerPage.appendChild(compRowPerPage);
	
	const divPageNum = document.createElement('div');
	divPageNum.classList.add("page-num");
	if(appendCss != undefined){
		divPageNum.style.cssText += 'margin-right:0px;';
	}
	
	const prev = document.createElement('button');
	prev.setAttribute("type","button");
	prev.classList.add("btn-page-l");
	prev.setAttribute("title", "이전");
	prev.addEventListener('click', (e) => {
		e.preventDefault();
		prevFunc(comp);
		if(typeof pageFunc === 'function'){
			pageFunc();
		}
		else{
			new Function(pageFunc)();
		}
	});
	const pageInfo = document.createElement('div');
	pageInfo.classList.add("num-txt");
	const next = document.createElement('button');
	next.setAttribute("type","button");
	next.classList.add("btn-page-r");
	next.setAttribute("title", "이후");
	next.addEventListener('click', (e) => {
		e.preventDefault();
		nextFunc(comp);
		if(typeof pageFunc === 'function'){
			pageFunc();
		}
		else{
			new Function(pageFunc)();
		}		
	});
	divPageNum.appendChild(prev);
	divPageNum.appendChild(pageInfo);
	divPageNum.appendChild(next);
	comp.appendChild(divRowPerPage);
	comp.appendChild(divPageNum);
	if(totPageInfo != undefined){
	}
	
}


const pageBind = (comp,pageInfo) => {
	const prev = comp.querySelector(".btn-page-l");
	const next = comp.querySelector(".btn-page-r");

	if(pageInfo.totalCount === 0){
		prev.disabled = true;
		next.disabled = true;
	}
	else{
		if(pageInfo.isFirstPage){
			prev.disabled = true;
		}
		else{
			prev.disabled = false;
		}
		if(pageInfo.isLastPage){
			next.disabled = true;
		}
		else{
			next.disabled = false;
		}
	}
	const pageDiv = comp.querySelector('.num-txt');
	pageDiv.innerHTML = '';
	pageDiv.innerHTML = pageInfo.pageNum+'/'+pageInfo.totalPage;	
}

const prevFunc = (comp) => {
	const pageNum = comp.querySelector('.compPageNum'); 
    pageNum.setAttribute("value",parseInt(pageNum.getAttribute("value"))-1);
}

const nextFunc = (comp) => {
	const pageNum = comp.querySelector('.compPageNum'); 
    pageNum.setAttribute("value",parseInt(pageNum.getAttribute("value"))+1);
}



const pageSearch = (formObj,url,gridComp,pageComp,invoker,listKey,callBackFn) => {
	

	if(document.getElementsByName('frDt')[0] && gfn_validDay(document.getElementsByName('frDt')[0].value,document.getElementsByName('toDt')[0].value)){
		msgCallBack(msg.invalidDay,false,() => {
			document.getElementsByName('frDt')[0].focus();
		});
		
		
	}else{
		const compPageNum = pageComp.querySelector('.compPageNum'); 
		const compRowPerPage = pageComp.querySelector('.compRowPerPage'); 
		if(invoker != undefined && invoker != null){
			if(invoker.getAttribute("type") === 'button'){ 
				compPageNum.setAttribute("value",1);
		    }
			if(invoker.tagName === 'A'){ 
				compPageNum.setAttribute("value",1);
		    }
	    }
		let pageNum = formObj.querySelector('.pageNum');
		let rowPerPage = formObj.querySelector('.rowPerPage');
		if(!pageNum){
			pageNum = document.createElement('INPUT'); 
			pageNum.setAttribute("type","hidden");
			pageNum.setAttribute("name","pageNum");
			pageNum.classList.add("pageNum");
			pageNum.setAttribute("value",compPageNum.getAttribute("value"));
			formObj.appendChild(pageNum);
			
		}
		else{
			pageNum.setAttribute("value",compPageNum.getAttribute("value"));
		}
		console.log("compRowPerPage:",compRowPerPage.value);
		if(!rowPerPage){
			rowPerPage = document.createElement('INPUT'); 
			rowPerPage.setAttribute("type","hidden");
			rowPerPage.setAttribute("name","rowPerPage");
			rowPerPage.classList.add("rowPerPage");
			rowPerPage.setAttribute("value",compRowPerPage.value);
			formObj.appendChild(rowPerPage);
			
		}
		else{
			rowPerPage.setAttribute("value",compRowPerPage.value);
		}
		gfn_asyncCall(url,'POST',formObj).then((data) => {
			pageGridSet(data,gridComp,pageComp,listKey,callBackFn);
		}); 
		
		if(parseInt(rowPerPage.getAttribute("value")) > 20){
			document.querySelector('.btn-top').click();
		}
	}
	

		
}

const pageGridSet = (data,gridComp,pageComp,listKey,callBackFn) => {
	let list = null;
	let appendData = {};
	let _listKey = (listKey===undefined ||listKey===null)?'list':listKey;
	let pageInfo = null;
	for(let key in data) {
		if(key.indexOf('success_msg') > -1){
			
		}
		if(key.indexOf('fail_msg') > -1){
			msgCall(data[key],false);
			break;
		}
		if(key.indexOf(_listKey) > -1){
         	list = data[key];
 		}
		if(key.indexOf("appendData") > -1){
			appendData = data[key];
 		}
        if(key.indexOf('pageInfo') > -1){
          	pageInfo = data[key];
  		}
      
	}
	
	gridBind(list,gridComp);
	pageBind(pageComp,pageInfo);
	if(callBackFn != undefined){
		callBackFn(appendData);
	}
}
