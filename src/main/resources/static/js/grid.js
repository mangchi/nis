'use strict'

const grid = {
		
}

let _gridClass = "";

const gridInit = (comp,headColumns,gridHeight,gridClass) => {
	_gridClass = "";
	let _comp = comp === null?document.querySelector('.gridCont'):comp;
	//let _gridHeight = gridHeight===undefined?'480px':gridHeight;
	let gridColumnInfo = "";
	let divTable = document.createElement("DIV");
	let divHead = null;
	if(headColumns != undefined && headColumns != null){
		divTable.classList.add("divTable");
		divHead = document.createElement("DIV");
		divHead.classList.add("divHead");
		if(gridClass != undefined && gridClass != null){
			_gridClass = gridClass;
			divHead.classList.add("grid-tb"+_gridClass);
		}
		
		for(let i=0;i<headColumns.length;i++){
			let headDiv = document.createElement("DIV");
			headDiv.classList.add("hdCell");
			let sortable=false,resizable = false;
			headDiv.setAttribute("data-type","text-short");	
			for(let key in headColumns[i]){				
				if(key === 'id'){
					headDiv.setAttribute('data-id',headColumns[i][key]);	
					if(headColumns[i][key] === 'chk'){
						headDiv.setAttribute("data-type","numeric");	
						let chkbox = document.createElement("INPUT");
						chkbox.setAttribute("type","checkbox");
						chkbox.setAttribute("name","chkAll");
						headDiv.appendChild(chkbox);
						chkbox.addEventListener('click',(e) => {
							
							const divBody = _comp.querySelector('.divBody');
							if(divBody){
								for(let row of divBody.children){
									for(let i = 0;i<row.children.length;i++){
										if(i === 0 && row.children[i].children[0].children[0] && row.children[i].children[0].children[0].type === 'checkbox'){
											row.children[i].children[0].children[0].checked = e.target.checked;
										}										
									}
								}				
							}
						});
					}					
				}
				if(key === 'label'){
					headDiv.append(headColumns[i][key]);
				}
				if(key === 'sortable' && headColumns[i][key].toUpperCase() === 'Y'){
					headDiv.classList.add("sortable");
					sortable = true;
				}
				if(key === 'isNumber' && headColumns[i][key].toUpperCase() === 'Y'){
					headDiv.classList.add("isNumber");
					}
				if(key === 'info' && headColumns[i][key].toUpperCase() === 'Y'){
					headDiv.classList.add("info");
				}
				if(key === 'mediaCell' && headColumns[i][key].toUpperCase() === 'Y'){
					headDiv.classList.add("mediaCell");
				}
				if(key === 'detailSearch' && headColumns[i][key].toUpperCase() === 'Y'){
					headDiv.classList.add("detailSearch");
				}
				if(key === 'openType'){
					if(headColumns[i][key].toUpperCase() === 'SELF'){
						headDiv.classList.add("self");
					}
					else if(headColumns[i][key].toUpperCase() === 'POPUP'){
						headDiv.classList.add("popup");					
					}
					else if(headColumns[i][key].toUpperCase() === 'CALLBACK'){
						headDiv.classList.add("callBack");					
					}
					else{
						headDiv.classList.add("new");			
					}
					
				}
				if(key === 'button' && headColumns[i][key].toUpperCase() === 'Y'){
					headDiv.classList.add("cellBtn");
				}
				if(key === 'span_tag' && headColumns[i][key].toUpperCase() === 'STATUS'){
					headDiv.classList.add("status");
				}
				if(key === 'span_tag' && headColumns[i][key].toUpperCase() === 'APPROVE'){
					headDiv.classList.add("approve");
				}
				if(key === 'data_url'){
					headDiv.setAttribute('data-url',headColumns[i][key]);
				}
				if(key === 'data_btnNm'){
					headDiv.setAttribute('data-btnNm',headColumns[i][key]);
				}
				if(key === 'data_cellType'){
					headDiv.setAttribute('data-cellType',headColumns[i][key]);
				}
				if(key === 'data_dateFrmt'){
					headDiv.setAttribute('data-dateFrmt',headColumns[i][key]);
				}
				if(key === 'data_grpCd'){
					headDiv.setAttribute('data-grpCd',headColumns[i][key]);
				}
				
				if(key === 'resizable' && headColumns[i][key].toUpperCase() === 'Y'){
					resizable = true;
				}
				if(key === 'width'){
					if(headColumns[i]['id'] === 'chk'){
						gridColumnInfo += headColumns[i][key]+" ";
					}else if(headColumns[i]['label'] === '#'){
						gridColumnInfo += headColumns[i][key]+" ";
					}
					else if(headColumns[i][key] == '0px'){
						gridColumnInfo += headColumns[i][key]+" ";
						headDiv.classList.add("hidden");
					}
					else{
						gridColumnInfo += "minmax("+headColumns[i][key]+", auto) ";
					}					
				}
			}
			if(sortable){
				headDiv.classList.add("sortable");
				let icons_sort = document.createElement("DIV");
				icons_sort.classList.add("icons-sort");
				headDiv.appendChild(icons_sort);
			}
			if(resizable){
				let resize_handle = document.createElement("SPAN");
				resize_handle.classList.add("resize-handle");
				headDiv.appendChild(resize_handle);
			}
			divHead.appendChild(headDiv);			
		}
		
		divTable.appendChild(divHead);
		const divBody = document.createElement("DIV");
		divBody.classList.add("divBody");
		divTable.appendChild(divBody);
		_comp.appendChild(divTable);
	}
	else{
		divTable = _comp.querySelector(".divTable");
		divHead = _comp.querySelector(".divHead");
		let headItems = divHead.querySelectorAll('.hdCell');
		if(headItems){
	    	headItems.forEach((headItem,index) => {
			    if(headItem.children[0] && headItem.children[0].type === 'checkbox'){
			    	//gridColumnInfo += "45px ";
			    	gridColumnInfo += "35px ";
			    }
			    else{
			    	gridColumnInfo += "minmax(80px, auto) ";
			    }
	    	});
		}
		
	}
	if(gridHeight != undefined && gridHeight != null ){
    	//_comp.style.height = gridHeight;
		//comp.style.height = gridHeight;
    }
	divHead.style.cssText = 'display:grid; grid-template-columns:'+gridColumnInfo+';';
	divHead.style.cssText += '-ms-grid-columns:'+gridColumnInfo+';';
	divHead.style.cssText += 'grid-column-gap:5px;height:38px;background:var(--bg-tb-hd);font-size:12px;font-weight:600;color:var(--font-basic);border:0px;border-bottom:2px solid var(--hd-line);';

	const infos = _comp.querySelectorAll(".info");
    if(infos){
    	infos.forEach((info) => {
    		info.addEventListener('click', (e) => {
				   console.log("info click");
		    });
	    });
    }
	
	const totDelBtn = document.createElement('a');
	let totDelIcon = document.createElement('i');

	const min = 150;
	const columnTypeToRatioMap = {
			numeric: 1,
		    'text-short': 1.67,
		    'text-long': 3.33,
	};
	const columns = [];
	let headerBeingResized;
	
	
	const onMouseMove = (e) => requestAnimationFrame(() => {
		const horizontalScrollOffset = document.documentElement.scrollLeft;
	    const width = (horizontalScrollOffset + e.clientX) - headerBeingResized.offsetLeft;
	    const column = columns.find(({ header }) => header === headerBeingResized);
	    column.size = Math.max(min, width) + 'px'; 
	
	    columns.forEach((column) => {
	    	if(column.size.startsWith('minmax')){ 
	    		column.size = parseInt(column.header.clientWidth, 10) + 'px';
	        }
	    });
	  
	    divTable.style.gridTemplateColumns = columns.map(({ header, size }) => size).join(' ');
	});
	
	const onMouseUp = () => {
		window.removeEventListener('mousemove', onMouseMove);
	    window.removeEventListener('mouseup', onMouseUp);
	    headerBeingResized.classList.remove('header--being-resized');
	    headerBeingResized = null;
	};
	
	const initResize = ({ target }) => {  
		headerBeingResized = target.parentNode;
	    window.addEventListener('mousemove', onMouseMove);
	    window.addEventListener('mouseup', onMouseUp);
	    headerBeingResized.classList.add('header--being-resized');
	};
	
	_comp.querySelectorAll('.hdCell').forEach((header,index) => {
		const max = columnTypeToRatioMap[header.dataset.type] + 'fr';
		columns.push({ 
	        header, 
	        size: `minmax(${min}px, ${max})`,
	    });
		const hdResize = header.querySelector('.resize-handle');
		if(hdResize){
			hdResize.addEventListener('mousedown', initResize);
		}	
	    const icons_sort = header.querySelector('.icons-sort');
	    
	    if(icons_sort){
	    	icons_sort.addEventListener('click', (e) => {
				if(icons_sort.classList.contains('desc')){
					sortTable(icons_sort,index,'asc');
				}
				else if(icons_sort.classList.contains('asc')){
					sortTable(icons_sort,index,'desc');
				}
			})
	    }		
		
	});
	
	/**
	 * grid sort
	 */
	const sortTable = (ele,index,sortTp) => {
		//console.log("sortTabel ele:",ele," index:",index," sortTp:",sortTp);
		let headRow, body, rows, switching, i, x, y, shouldSwitch;
		headRow = divTable.querySelector(".divHead");
		body = divTable.querySelector(".divBody");
		rows = body.children;
		switching = true;
		let isNumber = headRow.children[index].classList.contains("isNumber");
		while (switching) {
			switching = false;
			for (i = 0; i < (rows.length - 1); i++) {
				shouldSwitch = false;
				//x = rows[i].getElementsByTagName("div")[index];
	   	        //y = rows[i + 1].getElementsByTagName("div")[index];
				x = rows[i].querySelector('.overChild').querySelectorAll(".sortCell")[index];
	   	        y = rows[i + 1].querySelector('.overChild').querySelectorAll(".sortCell")[index];
	   	        //console.log("x:",x,", y:",y)
	   	        if(sortTp == 'desc'){
	   	        	if(isNumber){
	   	        		if(parseInt(x.innerHTML) < parseInt(y.innerHTML)) {
		   	        		shouldSwitch = true;
		   	        		break;
		      	        }
	   	        	}
	   	        	else{
	   	        		if(x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
		   	        		shouldSwitch = true;
		   	        		break;
		      	        }
	   	        	}
	   	        	
	   	        }
	   	        else{
	   	        	if(isNumber){
	   	        		if(parseInt(x.innerHTML) > parseInt(y.innerHTML)) {
		   	        		shouldSwitch = true;
		   	        		break;
		      	        }
	   	        	}
	   	        	else{
	   	        		if(x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
		   	        		shouldSwitch = true;
		   	        		break;
		      	        }
	   	        	}
	   	        	
	   	        }
	   	     }
			  
		     if(shouldSwitch) {
		    	rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
   	            switching = true;
		     }
	    }
		let fa_sort_up = ele.querySelector('.fa-sort-up');
		let fa_sort_down = ele.querySelector('.fa-sort-down');
		if(sortTp == 'desc'){
			ele.classList.remove('asc');
			ele.classList.add('desc');
			fa_sort_up.classList.add('chgOpacity');
			fa_sort_down.classList.remove('chgOpacity');
			
		}else{
			ele.classList.remove('desc');
			ele.classList.add('asc');
			fa_sort_down.classList.add('chgOpacity');
			fa_sort_up.classList.remove('chgOpacity');
			
		} 
	}
}

const gridBind = (list,gridComp) => {
	let _gripComp = gridComp === undefined?document.querySelector(".gridCont"):gridComp;
	let divHead = _gripComp.querySelector(".divHead");
	let divBody = _gripComp.querySelector(".divBody");
	while(divBody.hasChildNodes())
	{
		divBody.removeChild( divBody.firstChild );       
	}
	const sortables = divHead.querySelectorAll(".icons-sort");
	if(sortables){
		sortables.forEach((sortable) =>{
			sortable.classList.add('desc');
			let sortHtml = "<i class='fa fa-sort-up chgOpacity'></i>";
			sortHtml += "<i class='fa fa-sort-down'></i>";
		    sortable.innerHTML += sortHtml;
		});
	} 
	for(let key in list){
		let item = list[key];
		/*if(item.hasOwnProperty('fileType') && item['fileType'].indexOf('text') > -1 ){
			continue; 
		}*/
		let divOver = document.createElement('div');
		divOver.classList.add("over");
		let gridRow = document.createElement('div');
		gridRow.classList.add("overChild");
		if(_gridClass != ''){
			gridRow.classList.add("grid-tb-sub"+_gridClass);
		}
		
		
		gridRow.style.cssText = 'display:grid; grid-template-columns:'+divHead.style.gridTemplateColumns+';';
		gridRow.style.cssText += '-ms-grid-columns:'+divHead.style.gridTemplateColumns+';';
		gridRow.style.cssText += '-ms-grid-rows:repeat(15, 38px); grid-column-gap:5px;';
		gridRow.style.cssText += 'font-size:12px; font-weight:300; color:var(--font-basic); border:0px; border-bottom:1px solid var(--td-line);';
		//gridRow.style.cssText += 'height: 38px; font-size:12px; font-weight:300; color:var(--font-basic); border:0px; border-bottom:1px solid var(--td-line);';
		gridRow.style.cssText += 'cursor:pointer; align-items:center;';
		
	    let headItems = divHead.querySelectorAll('.hdCell');

	    if(headItems){
	    	headItems.forEach((headItem,index) => {
	    		let column = document.createElement('div');
	    		column.style.cssText += 'display: inline-block;width:'+(headItem.offsetWidth-5)+"px;overflow:hidden;white-space:nowrap;text-overflow: ellipsis;";
	    		let cellType = "tdCell"; // tdCell:normal default, thCell:center.strong, tdLCell,tdCCell,tdRCell
	    		if(headItem.getAttribute("data-cellType") != undefined && headItem.getAttribute("data-cellType") != ''){
	    			cellType = headItem.getAttribute("data-cellType");
				}
	    		column.classList.add("sortCell");
	   			if(headItem.children[0] && headItem.children[0].type === 'checkbox'){
	   				headItem.children[0].checked = "";
	   				column.classList.add("tdCell");
    				let chkbox = document.createElement('input');
    				chkbox.setAttribute("type", "checkbox");
    				column.appendChild(chkbox);
      				chkbox.addEventListener('click', (e) =>{ 
       					if(e.target.checked){
							if(divBody){
								let checkCnt = 0;
								for(let row of divBody.children){
									for(let i = 0;i<row.children.length;i++){
										if(i === 0 && row.children[i].children[0].children[0] && row.children[i].children[0].children[0].type === 'checkbox'){
											if(row.children[i].children[0].children[0].checked){
												checkCnt += 1;
											}
										}
									}									
								}
								if(divBody.children.length === checkCnt && !headItem.children[0].checked){
									headItem.children[0].checked = e.target.checked;
								}
							}
    					}else{
    						if(headItem.children[0].checked){
    							headItem.children[0].checked = e.target.checked;
    						}
    					}
    				});
    		    }
    			else if(headItem.classList.contains('cellBtn')){
    				column.classList.add("tdCell");
    				if(headItem.getAttribute("data-url") === undefined || headItem.getAttribute("data-url") === ''){
    					console.error("button data-url is not defined........");
    				}
    				if(headItem.getAttribute("data-btnNm") === undefined || headItem.getAttribute("data-btnNm") === ''){
    					console.error("button data-btnNm is not defined........");
    				}
    				
    				let cellAnchor = document.createElement('a');
    				cellAnchor.setAttribute("href", "");
    				cellAnchor.classList.add("btn-edit");
    				cellAnchor.append(headItem.getAttribute("data-btnNm"));
    				cellAnchor.addEventListener('click',(e) => {
						e.preventDefault();
						if(headItem.classList.contains('self')){
							gfn_asyncJsonCall('/'+headItem.getAttribute("data-url"),'POST',item);
						}
						else if(headItem.classList.contains('popup')){
							dynimicCall(item,'POST','/'+headItem.getAttribute("data-url"));
						}
						else if(headItem.classList.contains('callBack')){
							let callBackFn = headItem.getAttribute("data-url");
							if(typeof callBackFn === 'function'){
								callBackFn();
							}
							else{
								callBackFn += '('+JSON.stringify(item)+')';
								new Function(callBackFn)();
							}
						}
						else if(headItem.classList.contains('new')){
							newUrl(headItem.getAttribute("data-url"),item);
						}
       				})
       				if(headItem.getAttribute("data-url") === 'pwdUnBlock'){
    					if(item.hasOwnProperty('pwFailCnt') && parseInt(item.pwFailCnt) >= 5){
    						column.appendChild(cellAnchor);
    					}
    				}
    				else{
    					column.appendChild(cellAnchor);
    				}
    				
    		    }
    			else{
    				column.setAttribute("title",item[headItem.getAttribute("data-id")]);
    				column.addEventListener('click',(e) => {
    	    			e.preventDefault();
    	    			if(document.querySelector('.layer').style.display === 'block'){
    	    				layerRowClick(item,gridComp,e.target.parentNode);	    
    	    	    	}else{
    	    	    		rowClick(item,gridComp,e.target.parentNode);	
    	    	    		
    	    	    	}		
    	    		});
    				
    				column.addEventListener('dblclick',(e) => {
    	    			e.preventDefault();
    	    			if(document.querySelector('.layer').style.display === 'block'){
    	    				layerRowDblClick(item,gridComp,e.target.parentNode);
    	    	    	}else{
    	    	    		rowDblClick(item,gridComp,e.target.parentNode);
    	    	    	}
    	    		});
    				
    				column.classList.add(cellType);
    				column.setAttribute("data-value",gfn_nullValue(item[headItem.getAttribute("data-id")]));
    				let detailAnchor = document.createElement('a');
					detailAnchor.setAttribute("href", "");
					detailAnchor.addEventListener('click',(e) => {
						e.preventDefault();
						detailSearch(item,gridComp,e.target);
					});
    				if(headItem.classList.contains('mediaCell')){
    					let midiaDiv = document.createElement('div');
    			        midiaDiv.setAttribute("style", "background:var(--bg); width:110px; height:65px; cursor:pointer;");
    			        if(item["stillOrMovie"] === '001'){
    			        	let img = document.createElement('IMG');
        					img.setAttribute("src","/fileId/"+item['id']+"/"+item["fileSeq"]+"/N/N"+gfn_getStorageItem("curUrl")+"/"+new Date().getTime());
        					img.setAttribute("width","110px");
    						img.setAttribute("height","65px");
    						/*
    						img.addEventListener('click',(e) => {
    	    	    			e.preventDefault();
    	    	    			if(document.querySelector('.layer').style.display === 'block'){
    	    	    				layerImgClick(item,gridComp,e.target);	    
    	    	    	    	}else{
    	    	    	    		imgClick(item,gridComp,e.target);	    
    	    	    	    	}		
    	    	    		});
    	    	    		*/
    						midiaDiv.appendChild(img);
    			        }
    			        else{
    			        	let video = document.createElement('video');
    			        	video.setAttribute('src','/fileId/'+item['id']+'/'+item['fileSeq']+'/N/N'+gfn_getStorageItem("curUrl")+"/"+new Date().getTime());
    			        	video.setAttribute('width','100%');
    						video.setAttribute('height','100%');
    						video.setAttribute('cotrols',true);
    						video.setAttribute('preload',"auto");
    						/*
    						video.addEventListener('click',(e) => {
    	    	    			e.preventDefault();
    	    	    			if(document.querySelector('.layer').style.display === 'block'){
    	    	    				layerImgClick(item,gridComp,e.target);	    
    	    	    	    	}else{
    	    	    	    		imgClick(item,gridComp,e.target);	    
    	    	    	    	}		
    	    	    		});
    	    	    		*/
    						midiaDiv.append(video);
    			        }
    					if(headItem.classList.contains('detailSearch')){
    						
    						detailAnchor.append(midiaDiv);
    						column.appendChild(detailAnchor);
    					}
    					else{
    						column.appendChild(midiaDiv);
    					}
    					

	    			}
    				else{
		    			if(item[headItem.getAttribute("data-id")] != undefined){
		    				if(headItem.getAttribute("data-dateFrmt") != undefined && headItem.getAttribute("data-dateFrmt") != ''){
		    					if(headItem.classList.contains('detailSearch')){
		    						detailAnchor.append(gfn_dateFrmt(gfn_nullValue(item[headItem.getAttribute("data-id")]),headItem.getAttribute("data-dateFrmt")));
		    						column.appendChild(detailAnchor);
		    					}
		    					else{
		    						column.append(gfn_dateFrmt(gfn_nullValue(item[headItem.getAttribute("data-id")]),headItem.getAttribute("data-dateFrmt")));
		    					}
		    				}
		    				else if(headItem.getAttribute("data-grpCd") != undefined && headItem.getAttribute("data-grpCd") != ''){
		    					if(headItem.classList.contains('status')){
		    						let statusSpan = document.createElement('span');
		    						statusSpan.classList.add("tag","write");
		    						statusSpan.append(gfn_getCodeVal(headItem.getAttribute("data-grpCd"),gfn_nullValue(item[headItem.getAttribute("data-id")])));
		    						if(headItem.classList.contains('detailSearch')){
			      						detailAnchor.append(statusSpan);
			    						column.appendChild(detailAnchor);
			    					}
			    					else{
			    						column.appendChild(statusSpan);		
			    					}
		    	    			}
		    					else{
		    						if(headItem.classList.contains('detailSearch')){
			    						detailAnchor.append(gfn_getCodeVal(headItem.getAttribute("data-grpCd"),gfn_nullValue(item[headItem.getAttribute("data-id")])));
			    						column.appendChild(detailAnchor);
			    					}
			    					else{
			    						column.append(gfn_getCodeVal(headItem.getAttribute("data-grpCd"),gfn_nullValue(item[headItem.getAttribute("data-id")])));
			    					}
		    						
		    					}
		    					
		    				}
		    				else{
		    	                if(headItem.classList.contains('approve')){
		    	    				let approveSpan = document.createElement('span');
		    	    				if(item[headItem.getAttribute("data-id")] === '승인'){
		    	    					approveSpan.classList.add("tag","conf");
		    	    				}else{
		    	    					approveSpan.classList.add("tag","noconf");
		    	    				}
		    	    					
		    	    				approveSpan.append(gfn_nullValue(item[headItem.getAttribute("data-id")]));
		    	    				if(headItem.classList.contains('detailSearch')){
			    						detailAnchor.append(approveSpan);
			    						column.appendChild(detailAnchor);
			    					}
			    					else{
			    						column.appendChild(approveSpan);	
			    					}
		    	    					
		    	    			}else{
		    	    				if(headItem.classList.contains('detailSearch')){
			    						detailAnchor.append(item[headItem.getAttribute("data-id")]);
			    						column.appendChild(detailAnchor);
			    					}
			    					else{
			    						column.append(gfn_nullValue(item[headItem.getAttribute("data-id")]));
			    					}
		    	    				
		    	    			}
		    					
		    				}	    				
		    			}	
		    			if(headItem.classList.contains('hidden')){
		    				column.classList.add("hidden");
		    			}
    				}
	    			
    			}
	   			   //column.setAttribute("title",item[headItem.getAttribute("data-id")]);
	   			gridRow.appendChild(column);
	    	});	    	
	    }
	    divOver.appendChild(gridRow);
		divBody.appendChild(divOver);
	}
}

const gridCheckedData = (gridComp) => {
	let jsonParm = {};
	let checkedData = new Array();
	let divHead = document.querySelector(".divHead");
	let divBody = document.querySelector(".divBody");
	if(gridComp != undefined && gridComp != null){
		divHead = gridComp.querySelector(".divHead");
		divBody = gridComp.querySelector('.divBody');
	}
	let divRows = divBody.querySelectorAll('.overChild');
	
	if(divRows){
		divRows.forEach((rowItem,index) => {
			if(rowItem.children[0] && rowItem.children[0].children[0].type === 'checkbox'){
				if(rowItem.children[0].children[0].checked){
					let rowData = {};
					for(let i=0;i<rowItem.children.length;i++){
						if(rowItem.children[i].children.length === 0){
							rowData[divHead.children[i].getAttribute("data-id")] = rowItem.children[i].getAttribute("data-value");
						}
					}
					console.log(rowData);
					checkedData.push(rowData);
					jsonParm["list"] = checkedData;
					return false;
				}
			}
		});
	}
	
	
	return jsonParm;
}

				