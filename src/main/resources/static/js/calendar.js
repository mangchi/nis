'use strict'

const calInit = () => {
	// let calendar = document.querySelector(".calendar");
	const calendarModal = document.querySelector(".calendarModal");
    if(calendarModal){
		calendarModal.classList.add("hidden");
		let calendarHtml = '<div class="cal_nav">';
		calendarHtml += '<div class="go_prev"></div>';
	
		calendarHtml += '<div class="year_month">';
		calendarHtml += '<div class="year"></div>';
		calendarHtml += '<select id="month" class="form-select month">';
		for(let i=1;i<=12;i++){
			calendarHtml += '<option value="'+i+'">'+i+'월</option>';
		}
		calendarHtml += '</select>';
		calendarHtml += '</div>';
		calendarHtml += '<div class="go_next"></div>';
		calendarHtml += '</div>';
		calendarHtml += '<div class="cal_wrap">';
		calendarHtml += '<div class="days">';
		let dates = ['월','화','수','목','금','토','일'];
		const eng = calendarModal.classList;
		if(eng.contains("eng")){
			dates = ['MON','TUE','WED','THU','FRI','SAT','SUN'];	
		}
		for (let date of dates) {
			calendarHtml += '<div class="day">'+date+'</div>';
		}
		calendarHtml += '</div>';
	
		calendarHtml += '<div class="dates"></div></div>';
		calendarModal.innerHTML = calendarHtml;
		
		// 달력에서 표기하는 날짜 객체
		let date = new Date(); // 현재 날짜(로컬 기준) 가져오기
	    let utc = date.getTime() + (date.getTimezoneOffset() * 60 * 1000); // uct 표준시
	    let kstGap = 9 * 60 * 60 * 1000; // 한국 kst 기준시간 더하기
	    let pickDay; // 한국 시간으로 date 객체 만들기(오늘)
	
		let year,month;
		const calendar_inputs = document.querySelectorAll(".calendar_input");
		//console.log("calendar_inputs:",calendar_inputs);
		let target_obj = null;
		if(calendar_inputs){
			calendar_inputs.forEach((calendar_input) => {
				calendar_input.addEventListener('click', (e) =>{   
					if(e.target.value === undefined || e.target.value ===''){
						pickDay = new Date(utc + kstGap); // 현재 시간
						month = pickDay.getMonth()+1;
					}
					else{
						let vals = e.target.value.split('.');
						if(vals.length != 3){
							alert("오류");
							return;
						}
						pickDay = new Date(vals[0],parseInt(vals[1])-1,parseInt(vals[2])); // 현재 시간
						month = pickDay.getMonth()+1;
					}
					year = pickDay.getFullYear();
					target_obj = e.target;
					calendarInit();
					calendarModal.setAttribute('tabindex','0');
					calendarModal.focus();
					
				});
			})
		}
		
		const calendarInit = () => {
		    // 캘린더 렌더링
			calendarModal.classList.remove("hidden");
		    renderCalender();		    
		}
		
		document.querySelector(".go_prev").addEventListener('click', (e) =>{ 
			month = month - 1;
			if(month === 0){
				month = 12;
				year = year - 1;
			}
			document.querySelector('.month').value = month;
			renderCalender();
		});
		
		document.querySelector(".month").addEventListener('change', (e) =>{ 
			//console.log(e.target.value);
			month = parseInt(e.target.value);
			renderCalender();	
			calendarModal.setAttribute('tabindex','0');
			calendarModal.focus();
		});
		
		document.querySelector(".go_next").addEventListener('click', (e) =>{ 
			month = month + 1;
			if(month === 13){
				month = 1;
				year = year + 1;
			}
			//console.log(month);
			document.querySelector('.month').value = month;
			renderCalender();
		});
		
		calendarModal.addEventListener('blur', (e) => {
			//console.log("blur ",e);
			if(e.relatedTarget == null || e.relatedTarget.id != 'month'){
				calendarModal.classList.add("hidden");
			}
					
		});
		
		const renderCalender = () => {
	       // 이전 달의 마지막 날 날짜와 요일 구하기
	       let startDay = new Date(year, month-1, 0);
	       let prevDate = startDay.getDate();
	       let prevDay = startDay.getDay();
	
	       // 이번 달의 마지막날 날짜와 요일 구하기
	       let endDay = new Date(year, month , 0);
	       let nextDate = endDay.getDate();
	       let nextDay = endDay.getDay();
	
	       // 현재 월 표기
	       // document.querySelector('.year_month').textContent = year + '.' +
			// month;
	       document.querySelector('.year').textContent = year + '년';
	       document.querySelector('.month').value = month;
	
	       // 렌더링 html 요소 생성
	       let calendar = document.querySelector('.dates')
	       calendar.innerHTML = '';
	       
	       // 지난달
	       for (var i = prevDate - prevDay + 1; i <= prevDate; i++) {
	           calendar.innerHTML = calendar.innerHTML + '<div class="day prev disable">' + i + '</div>';
	       }
	       // 이번달
	       for (var i = 1; i <= nextDate; i++) {
	           calendar.innerHTML = calendar.innerHTML + '<div class="day current">' + i + '</div>';
	       }
	       // 다음달
	       for (var i = 1; i <= (7 - nextDay == 7 ? 0 : 7 - nextDay); i++) {
	           calendar.innerHTML = calendar.innerHTML + '<div class="day next disable">' + i + '</div>';
	       }
			
		   let days = document.querySelectorAll('.current');
		   if(days){
				days.forEach((day) => {
					day.addEventListener('click', function(e){
						let day = this.textContent<10?'0'+this.textContent:this.textContent;
						if(month < 10){
							target_obj.value = year+'.0'+month+'.'+day;
						}
						else{
							console.log("month:",month);
							target_obj.value = year+'.'+month+'.'+day;
						}
						
						calendarModal.classList.add("hidden");
						
					});
				})
		  }
			
	      // 선택 날짜 표기
		  let pickMonth = pickDay.getMonth();
		  pickMonth = pickMonth+1;
	      if(pickMonth == month) {	
	    	  let pickDate = pickDay.getDate();
	          let currentMonthDate = document.querySelectorAll('.dates .current');
	          currentMonthDate[pickDate -1].classList.add('pickDay');
	      }
	   }
    }

}