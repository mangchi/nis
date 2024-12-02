package mil.gdl.nis.cmmn;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import mil.gdl.nis.user.vo.UserVo;

@Component
public class SessionData {
	
	public  UserVo getUserVo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 
		if(authentication == null) {
			return null;
		}
		Object principal = authentication.getPrincipal();
		UserVo sessionUser = null;
		if(principal instanceof String) {
			
		}
		else {
			sessionUser = (UserVo) authentication.getPrincipal();
		}
		
		return sessionUser;
	}
	
	public  void setUserVo(UserVo vo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 
		UserVo sessionUser = (UserVo) authentication.getPrincipal();
		sessionUser.setUserNm(vo.getUserNm());
		sessionUser.setDeptNm(vo.getDeptNm());
		sessionUser.setUnitNm(vo.getUnitNm());
		sessionUser.setSpecNm(vo.getSpecNm());
		sessionUser.setAuthority(vo.getAuthority());
	}


}
